<template>
    <div style="padding: 10px;">
        <div style="max-height: 310px">
            <div style="padding: 10px; ">
                <div>
                    <el-form :inline="true" size='small' :model="searchFrom" :rules="rules1">
                        <el-form-item label="线路编号：" style="padding-right:0px">
                            <el-input placeholder="请输入线路编号" style="width: 130px" v-model="searchFrom.routeId" clearable/>
                        </el-form-item>
                        <el-form-item label="起始站：" style="padding-right: 0px" prop="startDate">
                            <el-select style="width: 130px" v-model="searchFrom.startStation" placeholder="请输入起始站" clearable>
                                <el-option
                                        v-for="item in startDate"
                                        :key="item.id"
                                        :label="item.stationName"
                                        :value="item.id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item label="终点站：" style="padding-right: 0px">
                            <el-select style="width: 130px" v-model="searchFrom.endStation" placeholder="请输入终点站" clearable>
                                <el-option
                                        v-for="item in endDate"
                                        :key="item.id"
                                        :label="item.stationName"
                                        :value="item.id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item label="总站数：" style="padding-right: 0px" prop="sum">
                            <el-input placeholder="请输入总站数" style="width: 130px" v-model.number="searchFrom.sum" clearable/>
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
            <el-table :data="tableData" border stripe style="width: 100%;" ref="tableData" @selection-change="selsChange">
                <el-table-column prop="routeId" label="站点名称" sortable/>
                <el-table-column prop="startName" label="起始站" />
                <el-table-column prop="endName" label="终点站" />
                <el-table-column prop="sum" label="站点总数" sortable/>
                <el-table-column fixed="right" label="操作" width="120">
                    <template  #default="scope">
                        <el-button link type="primary" @click="handleEdit(scope.row)">编辑</el-button>
                        <el-popconfirm title="确定删除吗?" @confirm="handleDelete(scope.row.id)">
                            <template #reference >
                                <el-button type="text">删除</el-button>
                            </template>
                        </el-popconfirm>
                    </template>
                </el-table-column>
            </el-table>

            <div style="margin: 10px 0">
                <el-pagination
                        v-model:currentPage="currentPage"
                        :page-size="pageSize"
                        :page-sizes="[5, 10, 15, 20]"
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

        <el-dialog
                v-model="dialogVisible"
                title="提示"
                width="30%">
            <el-form ref="addForm" :model="addForm" :rules="rules" label-width="80px">
                <el-form-item label="线路编号:" prop="routeId">
                    <el-input v-model="addForm.routeId" clearable v-bind:disabled="addForm.id"/>
                </el-form-item>
                <el-form-item label="起始站:" prop="startStation">
                    <el-select style="width: 130px" v-model="addForm.startStation" placeholder="请输入起始站" clearable>
                        <el-option
                                v-for="item in startDate"
                                :key="item.id"
                                :label="item.stationName"
                                :value="item.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="终点站:" prop="endStation">
                    <el-select style="width: 130px" v-model="addForm.endStation" placeholder="请输入终点站" clearable>
                        <el-option
                                v-for="item in endDate"
                                :key="item.id"
                                :label="item.stationName"
                                :value="item.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="总站数:" prop="sum">
                    <el-input v-model.number="addForm.sum" clearable v-bind:disabled="addForm.id"/><span v-bind:hidden="!addForm.id">请在线路详情页修改站数</span>
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
        name: "Routes",
        data(){
            return{
                dialogVisible: false,
                startDate:[],
                endDate:[],
                tableData :[],
                searchFrom:{
                    routeId:'',
                    startStation:'',
                    endStation:'',
                    sum:'',
                },
                addForm: {
                    routeId:'',
                    startStation:'',
                    endStation:'',
                    sum:'',
                },
                currentPage:1,
                pageSize:10,
                total:10,
                rules: {
                    routeId: [
                        { required: true, message: '请输入站点名称', trigger: 'blur' },
                    ],
                    startStation:[
                        {required: true, message: '请选择起始站点', trigger: 'blur'}
                    ],
                    endStation:[
                        {required: true, message: '请选择终点站', trigger: 'blur'}
                    ],
                    sum:[
                        { required: false, message: '请输入数字',type: 'number', trigger: 'change' },
                    ],
                },
                rules1: {
                    sum:[
                        { required: false, message: '请输入数字', type: 'number', trigger: 'change' }
                    ],
                },
            }
        },
        created() {
            this.inStartDate()
            this.endStartDate()
            this.load()
        },
        methods:{
            //初始化startDate下拉框
            inStartDate(){
                request.get("/station/initial").then(res => {
                    console.log(res.data)
                    this.startDate = res.data
                })
            },
            //初始化endDate下拉框
            endStartDate(){
                request.get("/station/initial").then(res => {
                    console.log(res.data)
                    this.endDate = res.data
                })
            },
            //查询方法
            load(){
                request.get("/routes",
                    {
                        params: {
                            pageNum: this.currentPage,
                            pageSize: this.pageSize,
                            routeId:this.searchFrom.routeId,
                            startStation: this.searchFrom.startStation,
                            endStation: this.searchFrom.endStation,
                            sum:this.searchFrom.sum
                        }

                    }).then(res =>{
                    console.log(res)
                    this.total = res.data.total
                    this.tableData = res.data.records
                    for(let i=0;i<res.data.total;i++){
                        request.get("/station/name",{
                            params:{
                                sid:res.data.records[i].startStation
                            }
                        }).then(res1 => {
                            this.tableData[i].startName = res1.data
                        })
                        request.get("/station/name",{
                            params:{
                                sid:res.data.records[i].endStation
                            }
                        }).then(res1 => {
                            this.tableData[i].endName = res1.data
                        })
                    }
                })
            },
            //新增窗口
            add(){
                this.dialogVisible = true;
                this.addForm = {
                    routeId:'',
                    startStation:'',
                    endStation:'',
                    sum:'',
                }
            },
            //新增方法
            save(){
                this.$refs['addForm'].validate((valid) => {
                    if(valid) {
                        if(this.addForm.startStation === this.addForm.endStation){
                            this.$message({
                                type:"error",
                                message: "起始站和终点站不能相同"
                            })
                        }else{
                            if(this.addForm.id){
                                request.put("/routes",this.addForm).then(res =>{
                                    if(res.code ==  '0'){
                                        request.get("/detail/updateroute",{
                                            params:{
                                                routeId:res.data.id,
                                                stationId1:res.data.startStation,
                                                stationId2:res.data.endStation,
                                                sum:res.data.sum
                                            }
                                        }).then( res1 => {
                                            if(res1.code ==  '0'){
                                                this.$message({
                                                    type:"success",
                                                    message: "更新成功"
                                                })
                                                this.dialogVisible = false;
                                                this.load();
                                            }
                                        })
                                    }else {
                                        this.$message({
                                            type: "error",
                                            message: res.msg
                                        })
                                    }
                                })
                            }else{
                                if(this.addForm.sum<2){
                                    this.$message({
                                        type: "error",
                                        message: "最少有两站"
                                    })
                                }else{
                                    request.post("/routes",this.addForm).then(res =>{
                                        if(res.code ==  '0'){
                                            this.$message({
                                                type:"success",
                                                message: "新增成功"
                                            })
                                            this.dialogVisible = false;
                                            this.load()
                                            request.get("/detail/addroute",{
                                                params:{
                                                    routeId1:res.data,
                                                    startStation1:this.addForm.startStation,
                                                    endStation1:this.addForm.endStation,
                                                    sum1:this.addForm.sum
                                                }
                                            })
                                        }else {
                                            this.$message({
                                                type: "error",
                                                message: res.msg
                                            })
                                        }
                                    })
                                }

                            }

                        }
                    }
                })
            },
            //删除方法
            handleDelete(id){
                request.get("/detail/delete",{
                    params:{
                        routeId:id
                    }
                })
                request.delete("/routes/" + id).then(res => {
                    if(res.code ==  '0'){
                        this.$message({
                            type:"success",
                            message: "删除成功"
                        })
                        this.load()
                    }else {
                        this.$message({
                            type: "error",
                            message: res.msg
                        })
                    }
                })
            },
            //分页函数
            handleEdit(row){
                this.addForm = JSON.parse(JSON.stringify(row))
                this.dialogVisible = true

            },
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