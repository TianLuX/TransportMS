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
                    <el-form-item label="审核状态：" style="padding-right: 10px">
                        <el-radio-group v-model="searchFrom.status">
                            <el-radio @click.native.prevent="clickItem1('已审核')" label="已审核" name="status" style="margin-right: 20px"/>
                            <el-radio @click.native.prevent="clickItem1('未审核')" label="未审核" name="status" style="margin-right: 20px"/>
                        </el-radio-group>
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
        <el-table :data="tableData" border stripe style="width: 100%;">
            <el-table-column prop="driverName" label="姓名" sortable/>
            <el-table-column prop="sex" label="性别" />
            <el-table-column prop="status" label="审核状态"/>
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
                <el-form-item label="姓名:">
                    <el-input v-model="addForm.driverName" clearable v-bind:disabled="addForm.id"/>
                </el-form-item>
                <el-form-item label="性别:" >
                    <el-radio-group v-model="addForm.sex">
                        <el-radio label="未知" name="sex" style="margin-right: 20px" />
                        <el-radio label="男" name="sex" style="margin-right: 20px" />
                        <el-radio label="女" name="sex" style="margin-right: 20px"/>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="状态:" style="padding-right: 10px">
                    <el-radio-group v-model="addForm.status">
                        <el-radio label="未审核" name="status" style="margin-right: 20px" />
                        <el-radio label="已审核" name="status" style="margin-right: 20px"/>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="有效时间:">
                    <el-col :span="11">
                        <el-date-picker
                                v-model="addForm.time"
                                type="date"
                                placeholder="输入时间"
                                style="width: 100%"
                        />
                    </el-col>
                </el-form-item>
                <el-form-item label="驾照:">
                    <el-upload
                            ref="upload"
                            v-model="addForm.url"
                            action="http://localhost:9090/files/upload"
                            :on-success="filesUploadSuccess"
                            :before-upload="beforeUpload"
                            :on-change="handleChange"
                            :file-list="fileList"
                    >
                            <el-button type="primary" style="margin-right: 80px">上传驾照</el-button>
                            <template #tip>
                                <div class="el-upload__tip">
                                    上传单个jpg/png文件需小于1M
                                </div>
                            </template>

                    </el-upload>
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
        name: "Check",
        data(){
            return{
                dialogVisible: false,
                tableData :[
                ],
                searchFrom:{
                    driverName:'',
                },
                addForm: {
                    sex:'未知',
                    status:'未审核',
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
            //删除方法
            handleDelete(id){
                request.delete("/driver/" + id).then(res => {
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
            //再点击取消status
            clickItem1(e){
                e==this.searchFrom.status ? this.searchFrom.status='':this.searchFrom.status=e
            },
            //限制传一个文件
            handleChange(file, fileList) {
                if(fileList.length > 1) {
                    this.fileList = [fileList[fileList.length - 1]]
                    this.$nextTick(()=>{
                        // 主动去调用提交接口
                        this.$refs.upload.submit();
                    })
                    // 若文件只有一个，直接上传
                }else{
                    this.$refs.upload.submit();
                }
            },
            //限制文件大小和类型
            beforeUpload(file){
                const imgType = file.type === 'image/jpeg' || file.type === 'image/png'
                const isLt500k = file.size / 1024 / 1024 < 0.99;
                if (!imgType) {
                    this.$message.error("上传图片只能是 JPG和png 格式!");
                    return false;
                }
                if (!isLt500k) {
                    this.$message.error("上传图片大小应小于1M");
                    return false;
                }
            },
            filesUploadSuccess (res){
                console.log(res)
                this.addForm.url = res.data
            },
            //查询方法
            load(){
                request.get("/driver",
                    {
                        params: {
                            pageNum: this.currentPage,
                            pageSize: this.pageSize,
                            driverName: this.searchFrom.driverName,
                            sex:this.searchFrom.sex,
                            status:this.searchFrom.status
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
                    sex:'未知',
                    status:'未审核',
                };
                this.$nextTick(()=>{//处理未来元素
                    this.$refs['upload'].clearFiles()//清除文件列表
                })
            },
            //新增方法
            save(){
                if(this.addForm.id){
                    request.put("/driver",this.addForm).then(res =>{
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
                    request.post("/driver",this.addForm).then(res =>{
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
            },
            //编辑
            handleEdit(row){
                this.addForm = JSON.parse(JSON.stringify(row))
                this.dialogVisible = true
                this.$nextTick(()=>{//处理未来元素
                    this.$refs['upload'].clearFiles()//清除文件列表
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