<template>
    <div id="container" style="width: 1240px"> </div>
    <div style="width: 1240px;height: 56px; position: absolute; left: 200px;background:rgba(255,255,255,0.9)">
        <div style="padding: 15px">
            <el-form :inline="true" size='small' :model="searchFrom" :rules="rules" ref="searchFrom">
                <el-form-item label="站点名称：" style="padding-right: 10px" prop="stationName">
                    <el-input placeholder="请输入关键字" style="width: 130px" v-model="searchFrom.stationName" clearable/>
                </el-form-item>
                <el-form-item label="站点经度：" style="padding-right: 10px" prop="lang">
                    <el-input id="lang" style="width: 130px" v-model="searchFrom.lang" placeholder="请点击地图确定位置" clearable/>
                </el-form-item>
                <el-form-item label="站点维度：" style="padding-right: 10px" prop="la">
                    <el-input id="la" style="width: 130px" v-model="searchFrom.la" placeholder="请点击地图确定位置" clearable/>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" style="margin-left: 4px" @click="load">查询</el-button>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" style="margin-left: 4px" @click="add">新增</el-button>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>
<script>
    import request from "../utils/request";

    let lang='';
    let la='';
    var showInfoClick = function(e) {
        lang = e.lnglat.getLng(); //获取经度
        la = e.lnglat.getLat(); //获取纬度
        document.getElementById("lang").value=lang
        document.getElementById("la").value=la
    }

    export default {
        name:"Map",
        data(){
            return{
                map:null,
                searchFrom:{
                    stationName:'',
                    lang:'',
                    la:'',
                },
                rules: {
                    stationName: [
                        { required: true, message: '请输入站点名称', trigger: 'blur' },
                    ],
                    lang: [
                        { required: true, message: '请点击地图确定位置', trigger: 'blur' },
                    ],
                    la: [
                        { required: true, message: '请点击地图确定位置', trigger: 'blur' },
                    ],
                }
            }
        },
        mounted(){
            this.map = new AMap.Map("container", {
                resizeEnable: true,
                zoom: 14,
                zooms: [3, 20],
                center: [117.140121354797, 36.66732773544],
            })
            this.map.on("click", showInfoClick);
        },
        created() {
            this.load()
        },
        methods: {
            initial(){
                this.searchFrom.lang = lang
                this.searchFrom.la = la
            },
            //查询方法
            load(){
                request.get("/station/map",
                    {
                        params: {
                            stationName: this.searchFrom.stationName,
                        }

                    }).then(res =>{
                    console.log(this.searchFrom.stationName)
                    this.map.clearMap();
                    for(var i =0;i<res.data.length;i++)
                    {
                        // 创建一个 Marker 实例：
                        var marker = new AMap.Marker({
                            position: new AMap.LngLat(res.data[i].lang,res.data[i].la),   // 经纬度对象，也可以是经纬度构成的一维数组[116.39, 39.9]
                            title: res.data[i].stationName
                        });
                        // 将创建的点标记添加到已有的地图实例：
                        this.map.add(marker);
                    }
                })
            },

            //新增方法
            add(){
                this.initial()
                this.$refs['searchFrom'].validate((valid) => {
                    if (valid) {
                        request.post("/station",this.searchFrom).then(res =>{
                            console.log(res)
                            if(res.code ==  '0'){
                                this.$message({
                                    type:"success",
                                    message: "新增成功"
                                })
                                this.load()
                            }else {
                                this.$message({
                                    type: "error",
                                    message: res.msg
                                })
                            }
                        })
                    }
                })
            },
        }
    }
</script>