<template>
    <div style="padding: 10px;">
        <div style="max-height: 310px">
            <div style="padding: 10px; ">
                <div>
                    <el-form :inline="true" size='small' :model="searchFrom">
                        <el-form-item label="站点名称：" style="padding-right: 0px" prop="stationDate">
                            <el-select style="width: 200px" v-model="searchFrom.stationName" placeholder="请选择站点" clearable>
                                <el-option
                                        v-for="item in stationDate"
                                        :key="item.id"
                                        :label="item.stationName"
                                        :value="item.id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" style="margin-left: 4px" @click="load">查询</el-button>
                        </el-form-item>
                    </el-form>
                </div>
            </div>
            <el-table :data="tableData" border stripe style="width: 100%;" ref="tableData">
                <el-table-column prop="routeName" label="线路名称" sortable/>
                <el-table-column prop="lastName" label="上一站" />
                <el-table-column prop="nextName" label="下一站" />
                <el-table-column prop="count" label="第几站"/>
            </el-table>

            <div style="margin: 10px 0">
                <el-pagination
                        v-model:currentPage="currentPage"
                        :page-size="pageSize"
                        :page-sizes="[10, 15, 20, 30]"
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
    </div>
</template>

<script>
    import request from "../utils/request";

    export default {
        name: "Through",
        data(){
            return{
               stationDate:[],
                tableData :[],
                searchFrom:{
                    stationName:'',
                },
                currentPage:1,
                pageSize:15,
                total:0,
            }
        },
        created() {
            this.initial()
        },
        methods:{
            load(){
                request.get("/detail",{
                    params: {
                        pageNum: this.currentPage,
                        pageSize: this.pageSize,
                        stationId:this.searchFrom.stationName
                    }
                }).then(res => {
                    console.log(res)
                    this.total = res.data.total
                    this.tableData = res.data.records
                    for(let i=0;i<res.data.total;i++){
                        //查询线路名称
                        let j;
                        request.get("/routes/find",{
                            params:{
                                routeId:res.data.records[i].routeId
                            }
                        }).then(res1 =>{
                            this.tableData[i].routeName = res1.data.routeId
                            j = res1.data.sum
                        })
                        //查询上一站
                        if(res.data.records[i].count == 1){
                            this.tableData[i].lastName = '始发站'
                        }else{
                            //查询上一站的routeid
                            request.get("/detail/last",{
                                params:{
                                    routeId:res.data.records[i].routeId,
                                    count:res.data.records[i].count
                                }
                            }).then(res2 =>{
                                if(res2.code ==  '0'){
                                    //查询上一站的名称
                                    if(res2.data != null){
                                        request.get("/station/name",{
                                            params:{
                                                sid:res2.data
                                            }
                                        }).then(res3 => {
                                            this.tableData[i].lastName = res3.data
                                        })
                                    }else{
                                        this.tableData[i].lastName = ''
                                    }

                                }
                            })
                        }
                        //查询下一站的routeid
                        request.get("/detail/next",{
                            params:{
                                routeId:res.data.records[i].routeId,
                                count:res.data.records[i].count
                            }
                        }).then(res2 =>{
                            if(res2.code ==  '0'){
                                //查询下一站的名称
                                if (res2.data !=null){
                                    request.get("/station/name",{
                                        params:{
                                            sid:res2.data
                                        }
                                    }).then(res3 => {
                                        this.tableData[i].nextName = res3.data
                                    })
                                }else{
                                    this.tableData[i].nextName = ''
                                }

                            }else if(res.data.records[i].count === j){
                                this.tableData[i].nextName = "终点站"
                            }else{
                                this.tableData[i].nextName =""
                            }

                        })
                    }
                })
            },
            initial(){
                request.get("/station/initial").then(res => {
                    console.log(res.data)
                    this.stationDate = res.data
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