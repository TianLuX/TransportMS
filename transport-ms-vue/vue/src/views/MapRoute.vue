<template>
    <div id="container" style="width: 1240px"> </div>
    <div style="width: 1240px;height: 120px; position: absolute; left: 200px;background:rgba(255,255,255,0.9)">
        <div  style="padding-top: 18px;border-bottom: #cccccc solid 1px">
            <el-form :inline="true" size='small' :model="searchFrom" :rules="rules" ref="searchFrom">
                <el-form-item label="线路编号：" style="padding-right: 0px" prop="routesDate">
                    <el-select style="width: 100px" v-model="searchFrom.routeId" placeholder="请选择线路" clearable>
                        <el-option
                                v-for="item in routesDate"
                                :key="item.id"
                                :label="item.routeId"
                                :value="item.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" style="margin-left: 4px" @click="load">查询</el-button>
                </el-form-item>
                <el-form-item>
                    <el-popconfirm title="确定清除吗?" @confirm="clean">
                        <template #reference >
                            <el-button type="primary">清除线路</el-button>
                        </template>
                    </el-popconfirm>
                </el-form-item>
            </el-form>
        </div>
        <div style="padding-top: 18px">
            <el-form :inline="true" size='small' :model="addForm" :rules="rules1" ref="addForm">
                <el-form-item label="线路编号:" prop="routeId">
                    <el-input style="width: 100px" v-model="addForm.routeId" placeholder="请填写线路" clearable/>
                </el-form-item>
                <el-form-item label="经过站点：" style="padding-right: 0px" prop="stationDate">
                    <el-select style="width: 800px" v-model="addForm.stationName" placeholder="请选择站点" :multiple="true" clearable>
                        <el-option
                                v-for="item in stationDate"
                                :key="item.id"
                                :label="item.stationName"
                                :value="item.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-popconfirm title="确定新增吗?" @confirm="add">
                        <template #reference >
                            <el-button type="primary">新增</el-button>
                        </template>
                    </el-popconfirm>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script>
    import request from "../utils/request";

    export default {
        name: "MapRoute",
        data(){
            return{
                map:null,
                routesDate:[],
                stationDate:[],
                searchFrom:{
                    routeId:'',
                },
                addForm:{
                    routeId:'',
                    stationName:[],
                },
                rules:{
                    routesDate: [
                        { required: true, message: '请选择线路', trigger: 'blur' },
                    ],
                },
                rules1: {
                    routeId: [
                        { required: true, message: '请填写线路', trigger: 'blur' },
                    ],
                },
            }
        },
        mounted(){
            this.map = new AMap.Map("container", {
                resizeEnable: true,
                zoom: 14,
                zooms: [3, 20],
                center: [117.140121354797, 36.66732773544],
            })
        },
        created() {
            this.initialRoutes()
            this.initialStation()
            this.loadStation()
        },
        methods:{
            clean(){
                this.map=null
                this.map = new AMap.Map("container", {
                    resizeEnable: true,
                    zoom: 14,
                    zooms: [3, 20],
                    center: [117.140121354797, 36.66732773544],
                })
                this.loadStation()
            },
            //初始化下拉框
            initialRoutes(){
                request.get("/routes/initial").then(res => {
                    this.routesDate = res.data
                })
            },
            initialStation(){
                request.get("/station/initial").then(res => {
                    this.stationDate = res.data
                })
            },
            loadStation(){
                request.get("/station/map",
                    {
                        params: {
                            stationName: '',
                        }

                    }).then(res =>{
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
            //查询线路
            load(){
                this.map.clearMap();
                if(this.searchFrom.routeId === ''){
                    this.$message({
                        type:"error",
                        message: "请选择线路"
                    })
                }else{
                    request.get("/detail/stations",{
                        params:{
                            routeId: this.searchFrom.routeId
                        }
                    }).then(res =>{
                        this.map.clearMap();
                        let origin
                        let destination
                        let waypoints=new Array()
                        for(let i=0;i<res.data.length;i++) {
                            if(res.data[i].stationId !== null){
                                request.get("/station/inf",{
                                    params:{
                                        sid:res.data[i].stationId
                                    }
                                }).then(res1 =>{
                                    if(i==0){
                                        origin=new AMap.LngLat(res1.data.lang,res1.data.la)
                                    }else if(i==(res.data.length-1)){
                                        destination=new AMap.LngLat(res1.data.lang,res1.data.la)
                                    }else{
                                        waypoints.push(new AMap.LngLat(res1.data.lang,res1.data.la))
                                    }
                                    if(i==(res.data.length-1)){
                                        console.log(origin)
                                        console.log(destination)
                                        console.log(waypoints)
                                        const driving = new AMap.Driving({
                                            map: this.map,
                                        })
                                        // 根据起终点经纬度规划驾车导航路线
                                        driving.search(origin, destination,{
                                            waypoints:waypoints
                                        }, function(status, result) {
                                            // result 即是对应的驾车导航信息，相关数据结构文档请参考
                                            if (status === 'complete') {
                                                console.log("绘制线路成功")
                                            } else{
                                                console.log(result)
                                            }
                                        });
                                    }
                                })
                            }
                        }
                    })
                }

            },
            //新增线路
            add(){
                this.$refs['addForm'].validate((valid) => {
                    if(valid){
                        if(this.addForm.stationName.length<2){
                            this.$message({
                                type:"error",
                                message: "线路最少有两站"
                            })
                        }else{
                            let length = this.addForm.stationName.length
                            request.get("/routes/map",{
                                params:{
                                    routeId:this.addForm.routeId,
                                    startStation:this.addForm.stationName[0],
                                    endStation:this.addForm.stationName[length-1],
                                    sum:this.addForm.stationName.length,
                                }
                            }).then( res =>{
                                if(res.code=='0'){
                                    for(let i=1;i<=length;i++)
                                    {
                                        request.get("/detail/one",{
                                            params:{
                                                routeId:res.data,
                                                count:i,
                                                stationId:this.addForm.stationName[i-1],
                                            }
                                        })
                                    }
                                    this.$message({
                                        type:"success",
                                        message: "新增成功"
                                    })
                                    this.initialRoutes()
                                    request.get("/detail/stations",{
                                        params:{
                                            routeId: res.data
                                        }
                                    }).then(res =>{
                                        this.clean()
                                        let origin
                                        let destination
                                        let waypoints=new Array()
                                        for(let i=0;i<res.data.length;i++) {
                                            if(res.data[i].stationId !== null){
                                                request.get("/station/inf",{
                                                    params:{
                                                        sid:res.data[i].stationId
                                                    }
                                                }).then(res1 =>{
                                                    if(i==0){
                                                        origin=new AMap.LngLat(res1.data.lang,res1.data.la)
                                                    }else if(i==(res.data.length-1)){
                                                        destination=new AMap.LngLat(res1.data.lang,res1.data.la)
                                                    }else{
                                                        waypoints.push(new AMap.LngLat(res1.data.lang,res1.data.la))
                                                    }
                                                    if(i==(res.data.length-1)){
                                                        console.log(origin)
                                                        console.log(destination)
                                                        console.log(waypoints)
                                                        const driving = new AMap.Driving({
                                                            map: this.map,
                                                        })
                                                        // 根据起终点经纬度规划驾车导航路线
                                                        driving.search(origin, destination,{
                                                            waypoints:waypoints
                                                        }, function(status, result) {
                                                            // result 即是对应的驾车导航信息，相关数据结构文档请参考
                                                            if (status === 'complete') {
                                                                console.log("绘制线路成功")
                                                            } else{
                                                                console.log(result)
                                                            }
                                                        });
                                                    }
                                                })
                                            }
                                        }
                                    })
                                }else{
                                    this.$message({
                                        type:"error",
                                        message: res.msg
                                    })
                                }
                            })
                        }
                    }
                })
            },
        }
    }
</script>

<style scoped>

</style>