<template>
    <div id="container" style="width: 1240px"> </div>
    <div style="width: 1240px;height: 56px; position: absolute; left: 200px;background:rgba(255,255,255,0.9)">
        <div style="padding: 15px">
            <el-form :inline="true" size='small' ref="form" :model="form">
                <el-form-item label="线路编号：" style="padding-right: 10px" prop="routeId">
                    <el-input style="width: 130px" v-model="form.routeId" readonly/>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script>
    import request from "../utils/request";

    export default {
        name: "MyRoute",
        data(){
            return{
                map:null,
                form:{
                    routeId:'',
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
        },
        created() {
            this.load()
        },
        methods:{
            load(){
                //根据司机姓名获取司机id
                request.get("/driver/findId",{
                    params:{
                        driverName:sessionStorage.getItem("driverName").replace('"','').replace('"',''),
                    }
                }).then( res => {
                    if(res.data.status == '未审核'){
                        this.$message({
                            type: "error",
                            message: '请等待管理员审核工作信息'
                        })
                        this.form.routeId = '请等待管理员审核工作信息'
                    }else{
                        request.get("/work/myWork",{
                            params:{
                                driverId:res.data.id
                            }
                        }).then( res1 => {
                            request.get("/routes/find",{
                                params:{
                                    routeId:res1.data.routeId
                                }
                            }).then(res2 =>{
                                this.form.routeId = res2.data.routeId
                            })
                            request.get("/detail/stations",{
                                params:{
                                    routeId: res1.data.routeId
                                }
                            }).then(res2 =>{
                                this.map.clearMap();
                                let origin
                                let destination
                                let waypoints=new Array()
                                for(let i=0;i<res2.data.length;i++) {
                                    if(res2.data[i].stationId !== null){
                                        request.get("/station/inf",{
                                            params:{
                                                sid:res2.data[i].stationId
                                            }
                                        }).then(res3 =>{
                                            if(i==0){
                                                origin=new AMap.LngLat(res3.data.lang,res3.data.la)
                                            }else if(i==(res2.data.length-1)){
                                                destination=new AMap.LngLat(res3.data.lang,res3.data.la)
                                            }else{
                                                waypoints.push(new AMap.LngLat(res3.data.lang,res3.data.la))
                                            }
                                            if(i==(res2.data.length-1)){
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
                        })
                    }

                })
            },
        },
    }
</script>

<style scoped>

</style>