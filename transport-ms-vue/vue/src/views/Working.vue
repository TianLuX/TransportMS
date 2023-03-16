<template>
    <div style="padding: 10px;">
        <div style="padding: 10px; ">
            <div>
                <el-form :inline="true" size='small' :model="searchFrom">
                    <el-form-item label="司机姓名：" style="padding-right: 10px">
                        <el-select style="width:180px" v-model="searchFrom.driverName" placeholder="请选择司机" clearable>
                            <el-option
                                    v-for="item in searchFrom.driverDate"
                                    :key="item.id"
                                    :label="item.driverName"
                                    :value="item.id">
                            </el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="车牌号：" style="padding-right: 10px">
                        <el-select style="width: 180px" v-model="searchFrom.carNumber" placeholder="请选择站点" clearable>
                            <el-option
                                    v-for="item in searchFrom.carDate"
                                    :key="item.id"
                                    :label="item.carNumber"
                                    :value="item.id">
                            </el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="线路号：" style="padding-right: 10px">
                        <el-select style="width: 180px" v-model="searchFrom.routeId" placeholder="请选择线路" clearable>
                            <el-option
                                    v-for="item in searchFrom.routeDate"
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
                        <el-button type="primary" style="margin-left: 4px" @click="add">分配工作</el-button>
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
            <el-table-column prop="driverName" label="司机姓名" sortable/>
            <el-table-column prop="carNumber" label="车牌号" sortable/>
            <el-table-column prop="routeId" label="线路号" sortable/>
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
            <el-form :model="addForm" label-width="70px">
                <el-form-item label="司机姓名:" style="margin-left: 55px">
                    <el-select style="width: 200px" v-model="addForm.driverName" placeholder="请选择司机" clearable>
                        <el-option
                                v-for="item in addForm.driverDate"
                                :key="item.id"
                                :label="item.driverName"
                                :value="item.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="车牌号:" style="margin-left: 55px">
                    <el-select style="width: 200px" v-model="addForm.carNumber" placeholder="请选择站点" clearable>
                        <el-option
                                v-for="item in addForm.carDate"
                                :key="item.id"
                                :label="item.carNumber"
                                :value="item.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="线路号:" style="margin-left: 55px">
                    <el-select style="width: 200px" v-model="addForm.routeId" placeholder="请选择线路" clearable>
                        <el-option
                                v-for="item in addForm.routeDate"
                                :key="item.id"
                                :label="item.routeId"
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
        name: "Working",
        data(){
            return{
                driverDate:[],
                tableData:[],
                carDate:[],
                routeDate:[],
                sels:[],
                searchFrom:{
                    driverName:'',
                    carNumber:'',
                    routeId:'',
                },
                addForm:{
                    driverName:'',
                    carNumber:'',
                    routeId:'',
                },
                dialogVisible: false,
                currentPage:1,
                pageSize:5,
                total:0,
            }
        },
        created() {
            this.initialDriver()
            this.initialBus()
            this.initialRoute()
            this.load()
        },
        methods:{
            //初始化搜索中司机下拉框
            initialDriver(){
                request.get("/driver/initial").then(res => {
                    this.searchFrom.driverDate = res.data
                })
            },
            //初始化搜索中车辆下拉框
            initialBus(){
                request.get("/bus/initial").then(res => {
                    this.searchFrom.carDate = res.data
                })
            },
            //初始化搜索中线路下拉框
            initialRoute(){
                request.get("/routes/initial").then(res => {
                    this.searchFrom.routeDate = res.data
                })
            },
            //查询
            load(){
                request.get("/work",
                    {
                        params: {
                            pageNum: this.currentPage,
                            pageSize: this.pageSize,
                            driverId: this.searchFrom.driverName,
                            busId:this.searchFrom.carNumber,
                            routeId:this.searchFrom.routeId
                        }

                    }).then(res =>{
                    console.log(res)
                    this.tableData = res.data.records
                    this.total = res.data.total
                    for(let i=0;i<res.data.total;i++){

                        request.get("/driver/find",{
                            params:{
                                driverId:res.data.records[i].driverId
                            }
                        }).then(res1 =>{
                            console.log(res1)
                            this.tableData[i].driverName = res1.data.driverName
                        })

                        request.get("/bus/find",{
                            params:{
                                busId:res.data.records[i].busId
                            }
                        }).then(res1 =>{
                            console.log(res1)
                            this.tableData[i].carNumber = res1.data.carNumber
                        })

                        request.get("/routes/find",{
                            params:{
                                routeId:res.data.records[i].routeId
                            }
                        }).then(res1 =>{
                            console.log(res1)
                            this.tableData[i].routeId = res1.data.routeId
                        })
                    }
                })
            },
            //分配工作
            add(){
                this.dialogVisible = true;
                this.addForm = {
                    driverName:'',
                    carNumber:'',
                    routeId:'',
                };
                //已经分配过工作的司机不会再被分配工作
                request.get("/driver/initial").then(res => {
                    this.addForm.driverDate = new Array()
                    for(let i=0;i<res.data.length;i++){
                        request.get("/work/driver",
                            {
                                params:{
                                    driverId:res.data[i].id
                                }
                            }).then(res1 =>{
                            if(res1.code ==  '0'){
                                this.addForm.driverDate.push(res.data[i])
                            }
                        })
                    }
                })
                //已经分配过线路的车辆不会在被分配线路
                request.get("/bus/initial").then(res => {
                    this.addForm.carDate = new Array()
                    for(let i=0;i<res.data.length;i++){
                        request.get("/work/bus",
                            {
                                params:{
                                    busId:res.data[i].id
                                }
                            }).then(res1 =>{
                            if(res1.code ==  '0'){
                                this.addForm.carDate.push(res.data[i])
                            }
                        })
                    }
                })
                //线路可以被重复分配
                request.get("/routes/initial").then(res => {
                    this.addForm.routeDate = res.data
                })

            },
            save(){
                if(this.addForm.id){
                    //编辑
                    request.put("/work",this.addForm).then(res =>{
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
                    //新增
                    request.get("/work/save",{
                        params:{
                            driverId:this.addForm.driverName,
                            busId:this.addForm.carNumber,
                            routeId:this.addForm.routeId
                        }
                    }).then(res =>{
                        if(res.code ==  '0'){
                            this.$message({
                                type:"success",
                                message: "分配工作成功"
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
            },
            //删除方法
            handleDelete(id){
                request.delete("/work/" + id).then(res => {
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
            //一键删除
            selsChange(sels){
                this.sels=sels
            },
            deleteAll(){
                var ids = this.sels.map(item =>item.id).join()
                request.post("/work/deleteAll",{ids:ids}).then(res=>{
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
            //编辑
            handleEdit(row){
                this.addForm = JSON.parse(JSON.stringify(row))
                this.dialogVisible = true
                let driverName1 = this.addForm.driverName;
                let driverId1 = this.addForm.driverId
                //已经分配过工作的司机不会再被分配工作
                request.get("/driver/initial").then(res => {
                    this.addForm.driverDate = new Array()
                    this.addForm.driverDate.push({id:driverId1,driverName:driverName1})
                    for(let i=0;i<res.data.length;i++){
                        request.get("/work/driver",
                            {
                                params:{
                                    driverId:res.data[i].id
                                }
                            }).then(res1 =>{
                            if(res1.code ==  '0'){
                                this.addForm.driverDate.push(res.data[i])
                            }
                        })
                    }
                })
                let busId1 = this.addForm.busId
                let carNumber1 = this.addForm.carNumber
                //已经分配过线路的车辆不会在被分配线路
                request.get("/bus/initial").then(res => {
                    this.addForm.carDate = new Array()
                    this.addForm.carDate.push({id:busId1,carNumber:carNumber1})
                    for(let i=0;i<res.data.length;i++){
                        request.get("/work/bus",
                            {
                                params:{
                                    busId:res.data[i].id
                                }
                            }).then(res1 =>{
                            if(res1.code ==  '0'){
                                this.addForm.carDate.push(res.data[i])
                            }
                        })
                    }
                })
                //线路可以被重复分配
                request.get("/routes/initial").then(res => {
                    this.addForm.routeDate = res.data
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