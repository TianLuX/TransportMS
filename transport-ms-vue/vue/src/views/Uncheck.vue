<template>
    <div style="padding: 10px;">
        <div style="padding: 10px; ">
            <div>
                <el-form :inline="true" size='small' :model="searchFrom">
                    <el-form-item label="姓名：" style="padding-right: 10px">
                        <el-input placeholder="请输入关键字" style="width: 150px" v-model="searchFrom.driverName" clearable/>
                    </el-form-item>
                    <el-form-item label="性别：" style="padding-right: 10px">
                        <el-radio-group v-model="searchFrom.sex">
                            <el-radio @click.native.prevent="clickItem('男')" label="男" name="sex" style="margin-right: 20px"/>
                            <el-radio @click.native.prevent="clickItem('女')" label="女" name="sex" style="margin-right: 20px"/>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" style="margin-left: 4px" @click="load">查询</el-button>
                    </el-form-item>

                </el-form>
            </div>
        </div>
        <el-table :data="tableData" border stripe style="width: 100%;">
            <el-table-column v-model="driverName" prop="driverName" label="姓名" sortable/>
            <el-table-column prop="sex" label="性别" />
            <el-table-column prop="status" label="审核状态">
                <template #default="scope">
                    <el-switch
                        v-model="scope.row.status"
                        :active-value="'已审核'"
                        :inactive-value="'未审核'"
                        active-text="已审核"
                        inactive-text="未审核"
                        @change="switchChange($event, scope.row)"
                    >
                    </el-switch>
                </template>
            </el-table-column>
            <el-table-column prop="time" label="驾照有效时间" :formatter="formatTime" sortable/>
            <el-table-column label="驾照">
                <template #default="scope">
                    <el-image style="width: 100px; height: 100px;"
                              :src="scope.row.url"
                              :preview-src-list="[scope.row.url]"
                              preview-teleported="true">
                    </el-image>
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
</template>

<script>
    import request from "../utils/request";

    export default {
        name: "Uncheck",
        data(){
            return{
                tableData :[
                ],
                searchFrom:{
                    driverName:'',
                },
                currentPage:1,
                pageSize:5,
                total:10,
                fileList:[],
            }
        },
        created() {
            this.load()
        },
        methods:{
            switchChange(e, row) {
                if(e=="已审核"){
                    request.get("/driver/past",
                        {
                        params: {
                            driverName:row.driverName,
                            status:"已审核"
                        }
                    }).then(res => {
                        if(res.code ==  '0'){
                            this.$message({
                                type:"success",
                                message: "审核成功"
                            })
                        }else {
                            this.$message({
                                type: "error",
                                message: "审核失败"
                            })
                        }
                    })
                }else{
                    request.get("/driver/past",
                        {
                            params: {
                                driverName:row.driverName,
                                status:"未审核"
                            }
                        }).then(res => {
                        if(res.code ==  '0'){
                        }else {
                            this.$message({
                                type: "error",
                                message: "审核失败"
                            })
                        }
                    })
                }
            },
            // 时间格式化
            formatTime(row, column){
                let data = row[column.property]
                let dtime = new Date(data)
                const year = dtime.getFullYear()
                let month = dtime.getMonth() + 1
                if (month < 10) {
                    month = '0' + month
                }
                let day = dtime.getDate()
                if (day < 10) {
                    day = '0' + day
                }
                return year+ '-' + month+ '-' + day
            },
            //再点击取消sex
            clickItem(e){
                e==this.searchFrom.sex ? this.searchFrom.sex='':this.searchFrom.sex=e
            },
            //查询方法
            load(){
                request.get("/driver/fast",
                    {
                        params: {
                            pageNum: this.currentPage,
                            pageSize: this.pageSize,
                            driverName: this.searchFrom.driverName,
                            sex:this.searchFrom.sex,
                        }

                    }).then(res =>{
                    console.log(res)
                    this.tableData = res.data.records
                    this.total = res.data.total
                })
            },
            //分页函数
            handleEdit(row){
                this.addForm = JSON.parse(JSON.stringify(row))
                this.$nextTick(()=>{//处理未来元素
                    this.$refs['upload'].clearFiles()//清除文件列表
                })

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