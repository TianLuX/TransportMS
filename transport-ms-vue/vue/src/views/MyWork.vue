<template>
    <div style="padding: 55px 380px">
        <el-form ref="form" :model="form" :rules="rules" label-width="100px" size="normal" >
            <el-form-item prop="driverName" label="司机名称:" >
                <el-input :prefix-icon="Avatar" v-model="form.driverName" style="width: 320px" readonly/>
            </el-form-item>
            <el-form-item prop="status" label="审核状态:">
                <el-input :prefix-icon="Key" v-model="form.status" style="width: 320px" readonly/>
            </el-form-item>
            <el-form-item prop="bus" label="工作车辆:">
                <el-input :prefix-icon="Van" v-model="form.bus" style="width: 320px" readonly/>
            </el-form-item>
            <el-form-item prop="route" label="工作线路:">
                <el-input :prefix-icon="AddLocation" v-model="form.route" style="width: 320px" readonly/>
            </el-form-item>
        </el-form>
        <span>线路详情</span>
        <div style="padding-top: 10px">
            <el-table :data="tableData" border stripe style="width: 100%;" ref="tableData">
                <el-table-column prop="count" label="第几站"/>
                <el-table-column label="站点名称：" style="padding-right: 0px" prop="stationName"/>
            </el-table>
        </div>
        <div style="margin: 10px 0">
            <el-pagination
                    v-model:currentPage="currentPage"
                    :page-size="pageSize"
                    :page-sizes="[20, 30, 40, 50]"
                    :small="small"
                    :disabled="disabled"
                    :background="background"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="total"
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
            />
        </div>

    </div>
</template>

<script>
    import { Avatar,Van,AddLocation,Key } from '@element-plus/icons-vue'
    import request from "../utils/request";
    export default {
        name: "MyWork",
        data() {
            return {
                form:{
                    driverName:sessionStorage.getItem("driverName").replace('"','').replace('"',''),
                    bus:'',
                    route:'',
                    status:'',
                },
                tableData:[],
                currentPage:1,
                pageSize:30,
                total:0,
            }
        },
        setup(){
            return {
                Avatar,
                Van,
                AddLocation,
                Key
            }
        },
        created() {
            this.load()
        },
        methods:{
            load(){
                //根据司机姓名获取司机id
                request.get("/driver/findId",{
                    params:{
                        driverName:this.form.driverName
                    }
                }).then( res => {
                    if(res.data.status == '未审核'){
                        this.form.status='请等待管理员审核'
                    }else{
                        var nowdate = new Date();
                        var year = new Date().getFullYear();
                        var month = new Date().getMonth()+1;
                        var today=new Date().getDate();
                        if (month<10){month="0"+month;}
                        if (today<10){ today="0"+today; }
                        var nowTime = year + "-" + month + "-" + today
                        console.log(nowTime)
                        console.log(res.data.time)
                        if(nowTime>res.data.time){
                            this.form.status='请更新驾照时间'
                        }else{
                            this.form.status='已审核'
                            request.get("/work/myWork",{
                                params:{
                                    driverId:res.data.id
                                }
                            }).then( res1 => {

                                request.get("/bus/find",{
                                    params:{
                                        busId:res1.data.busId
                                    }
                                }).then(res2 =>{
                                    this.form.bus = res2.data.carNumber
                                })

                                request.get("/routes/find",{
                                    params:{
                                        routeId:res1.data.routeId
                                    }
                                }).then(res2 =>{
                                    this.form.route = res2.data.routeId
                                })

                                request.get("/detail/all",{
                                    params:{
                                        pageNum: this.currentPage,
                                        pageSize: this.pageSize,
                                        routeId: res1.data.routeId
                                    }
                                }).then( res =>{
                                    this.total = res.data.total
                                    this.tableData = new Array()
                                    this.tableData = res.data.records
                                    for(let i=0; i<res.data.total;i++)
                                    {
                                        let j=i+1;
                                        this.tableData[i].count = "第"+j+"站";
                                        if(res.data.records[i].stationId != null){
                                            request.get("/station/name",{
                                                params:{
                                                    sid:res.data.records[i].stationId
                                                }
                                            }).then(res1 => {
                                                this.tableData[i].stationName = res1.data
                                            })
                                        }else{
                                            this.tableData[i].stationName = ""
                                        }
                                    }
                                })
                            })
                        }
                    }

                })
            },


            //分页函数
            handleSizeChange(pageSize){//改变每个页的个数触发
                this.pageSize = pageSize
                this.load()
            },
            handleCurrentChange(pageNum){//改变当前页码触发
                this.currentPage= pageNum
                this.load()
            },
        }
    }
</script>

<style scoped>

</style>