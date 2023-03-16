<template>
    <div style="padding: 10px;">
        <div style="padding: 10px; ">
            <div>
                <el-form :inline="true" size='small' :model="searchFrom">
                    <el-form-item label="车牌号：" style="padding-right: 10px">
                        <el-input placeholder="请输入车牌号" style="width: 150px" v-model="searchFrom.carNumber" clearable/>
                    </el-form-item>
                    <el-form-item label="车辆信息：" style="padding-right: 10px">
                        <el-input placeholder="请输入车辆信息" style="width: 150px" v-model="searchFrom.information" clearable/>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" style="margin-left: 4px" @click="load">查询</el-button>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" style="margin-left: 4px" @click="add">新增</el-button>
                    </el-form-item>

                    <el-form-item>
                        <el-popconfirm title="确定删除吗?" @confirm="deleteAll(sels)">
                            <template #reference >
                                <el-button type="primary" :disabled="this.sels.length === 0">一键删除</el-button>
                            </template>
                        </el-popconfirm>
                    </el-form-item>
                </el-form>
            </div>
        </div>
        <el-table :data="tableData" border stripe style="width: 100%;" ref="tableData" @selection-change="selsChange">
            <el-table-column type="selection" width="55" />
            <el-table-column prop="carNumber" label="车牌号" sortable/>
            <el-table-column prop="information" label="车辆信息" />
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

        <el-dialog
                v-model="dialogVisible"
                title="提示"
                width="30%">
            <el-form ref="addForm" :model="addForm" :rules="rules" label-width="80px">
                <el-form-item label="车牌号:" prop="carNumber">
                    <el-input v-model="addForm.carNumber" clearable v-bind:disabled="addForm.id"/>
                </el-form-item>
                <el-form-item label="车辆信息:">
                    <el-input v-model="addForm.information" clearable/>
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
        name: "Bus",
        data(){
            return{
                dialogVisible: false,
                sels:[],
                tableData :[
                ],
                searchFrom:{
                    carNumber:'',
                    information:'',
                },
                addForm: {
                    carNumber:'',
                    information:'',

                },
                currentPage:1,
                pageSize:10,
                total:10,
                fileList:[],
                rules: {
                    carNumber: [
                        { required: true, message: '请输入车牌号', trigger: 'blur' },
                    ],
                }
            }
        },
        created() {
            this.load()
        },
        methods:{
            //一键删除
            selsChange(sels){
                this.sels=sels
            },
            deleteAll(){
                var ids = this.sels.map(item =>item.id).join()
                console.log(ids)
                request.post("/bus/deleteAll",{ids:ids}).then(res=>{
                    if(res.code ==  '0'){
                        this.$message({
                            type:"success",
                            message: "删除成功"
                        })
                        this.load();
                    }else {
                        this.$message({
                            type: "error",
                            message: res.msg
                        })
                    }
                })
            },
            //查询方法
            load(){
                request.get("/bus",
                    {
                        params: {
                            pageNum: this.currentPage,
                            pageSize: this.pageSize,
                            carNumber: this.searchFrom.carNumber,
                            information:this.searchFrom.information
                        }

                    }).then(res =>{
                    console.log(res)
                    this.tableData = res.data.records
                    this.total = res.data.total
                })
            },
            //新增窗口
            add(){
                this.dialogVisible = true;
                this.addForm = {
                    carNumber:'',
                    information:'',
                }
            },
            //新增方法
            save(){
                this.$refs['addForm'].validate((valid) => {
                    if(valid) {
                        if(this.addForm.id){
                            request.put("/bus",this.addForm).then(res =>{
                                console.log(res)
                                if(res.code ==  '0'){
                                    this.$message({
                                        type:"success",
                                        message: "更新成功"
                                    })
                                }else {
                                    this.$message({
                                        type: "error",
                                        message: res.msg
                                    })
                                }
                                this.load();
                                this.dialogVisible = false;
                            })
                        }else{
                            request.post("/bus",this.addForm).then(res =>{
                                console.log(res)
                                if(res.code ==  '0'){
                                    this.$message({
                                        type:"success",
                                        message: "新增成功"
                                    })
                                    this.dialogVisible = false;
                                    this.load()
                                }else {
                                    this.$message({
                                        type: "error",
                                        message: res.msg
                                    })
                                }
                            })
                        }
                    }
                })
            },
            //删除方法
            handleDelete(id){
                request.delete("/bus/" + id).then(res => {
                    if(res.code ==  '0'){
                        this.$message({
                            type:"success",
                            message: "删除成功"
                        })
                        this.load()
                    }else {
                        this.$message({
                            type: "error",
                            message: "删除失败"
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