<template>
    <div style="padding: 10px;">
        <div>
            <el-form :inline="true" size='small' :model="searchFrom">
                <el-form-item label="线路名称：" style="padding-right: 0px" prop="routesDate">
                    <el-select style="width: 200px" v-model="searchFrom.routeId" placeholder="请选择线路" clearable>
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
                    <el-button type="primary" style="margin-left: 4px" @click="add">增加站点</el-button>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" style="margin-left: 4px" @click="minus">减少站点</el-button>
                </el-form-item>
            </el-form>
        </div>
        <div>
            <el-table :data="tableData" border stripe style="width: 100%;" ref="tableData">
                <el-table-column prop="count" label="第几站"/>
                <el-table-column label="站点名称：" style="padding-right: 0px" prop="stationName"/>
                <el-table-column fixed="right" label="操作" width="300px">
                    <template  #default="scope">
                        <el-button link type="primary" @click="handleEdit(scope.row)">编辑</el-button>
                    </template>
                </el-table-column>
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

        <el-dialog
                v-model="dialogVisible"
                title="提示"
                width="30%">
            <el-form :model="editForm" label-width="100px" style="padding-left: 16px">
                <el-form-item label="站点数:">
                    <el-input v-model="editForm.count" style="width: 200px" disabled/>
                </el-form-item>
                <el-form-item label="站点名称:">
                    <el-select style="width: 200px" v-model="editForm.stationName" placeholder="请选择站点" clearable>
                        <el-option
                                v-for="item in stationDate"
                                :key="item.id"
                                :label="item.stationName"
                                :value="item.id">
                        </el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="save">确定</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script>
    import request from "../utils/request";

    export default {
        id:'',
        name: "Detail",
        data(){
            return{
                dialogVisible: false,
                stationDate:[],
                editForm:[],
                routesDate:[],
                searchFrom:{
                    routeId:'',
                },
                tableData :[],
                currentPage:1,
                pageSize:30,
                total:0,
            }
        },
        created() {
            this.initial()
        },
        methods:{
            //初始化下拉框
            initial(){
                request.get("/routes/initial").then(res => {
                    console.log(res.data)
                    this.routesDate = res.data
                })
            },
            //查询
            load(){
                if(this.searchFrom.routeId === ''){
                    this.$message({
                        type:"error",
                        message: "请选择线路"
                    })
                }else{
                    request.get("/detail/all",{
                            params:{
                                pageNum: this.currentPage,
                                pageSize: this.pageSize,
                                routeId: this.searchFrom.routeId
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
                }

            },
            add () {
                if(this.searchFrom.routeId === ''){
                    this.$message({
                        type:"error",
                        message: "请选择线路"
                    })
                }else{
                    request.get("/routes/add",{
                        params:{
                            routeId: this.searchFrom.routeId
                        }
                    }).then(res => {
                        //设置detail表格数据
                        request.get("/detail/add",{
                            params:{
                                routeId:this.searchFrom.routeId,
                                count:res.data
                            }
                        })
                        let name = this.tableData[this.tableData.length-1].stationName
                        this.tableData[this.tableData.length-1].stationName = ''
                        let j = this.tableData.length+1
                        this.tableData.push({count:"第"+j+"站",stationName:name})
                        this.total++
                    })
                }
            },
            minus(){
                if(this.searchFrom.routeId === ''){
                    this.$message({
                        type:"error",
                        message: "请选择线路"
                    })
                }else{
                    request.get("/routes/minus",{
                        params:{
                            routeId: this.searchFrom.routeId
                        }
                    }).then(res =>{
                        if(res.code == '0'){
                            request.get("/detail/minus",{
                                params:{
                                    routeId:this.searchFrom.routeId,
                                    count:res.data
                                }
                            })
                            this.tableData[this.tableData.length-2].stationName = this.tableData[this.tableData.length-1].stationName
                            this.tableData.pop();
                        }else{
                            this.$message({
                                type:"error",
                                message: res.msg
                            })
                        }
                        this.total--
                    })
                }
            },
            //编辑保存
            save(){
                let myCount = this.editForm.count.replace("第","").replace("站","")
                let stationId
                if( this.editForm.stationName === ''){
                    stationId = 0
                }else{
                    stationId = this.editForm.stationName
                }

                //可能会更新首末站的情况
                request.get("/routes/detail",{
                    params:{
                        routeId:this.editForm.routeId,
                        count:myCount,
                        stationId:stationId
                    }
                }).then(res => {
                    if(res.code == '0'){
                        request.get("/detail/update",{
                            params:{
                                routeId:this.editForm.routeId,
                                count:myCount,
                                stationId:stationId
                            }
                        }).then( res => {
                            if(res.code == '0'){
                                this.$message({
                                    type:"success",
                                    message: "更新成功"
                                })
                                request.get("/station/name",{
                                    params:{
                                        sid:stationId
                                    }
                                }).then(res1 => {
                                    this.tableData[id-1].stationName = res1.data
                                })
                            }else{
                                this.$message({
                                    type: "error",
                                    message: res.msg
                                })
                            }
                        })
                        this.dialogVisible = false
                    }else{
                        this.$message({
                            type: "error",
                            message: res.msg
                        })
                    }
                })
            },
            //编辑
            handleEdit(row){
                window.id = row.count.replace('第','').replace('站','')
                this.editForm = JSON.parse(JSON.stringify(row))
                this.dialogVisible = true
                let stationId1 = this.editForm.stationId
                let stationName1 = this.editForm.stationName
                let routeId1 = this.editForm.routeId
                //已经分配过线路的站点不会再被分配，只有已经有地址的站点才能被分配
                request.get("/station/initial").then(res => {
                    this.stationDate = new Array()
                    if(stationId1 != null){
                        this.stationDate.push({id:stationId1,stationName:stationName1})
                    }
                    for(let i=0;i<res.data.length;i++)
                    {
                        request.get("/detail/station",{
                            params:{
                                routeId:routeId1,
                                stationId:res.data[i].id,
                            }
                        }).then( res1 =>{
                            if(res1.code== '0' ){
                                //根据站点id查询站点名称
                                request.get("/station/name",{
                                    params:{
                                        sid:res.data[i].id
                                    }
                                }).then(res2 => {
                                    this.stationDate.push({id:res.data[i].id,stationName:res2.data})
                                })
                            }
                        })
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