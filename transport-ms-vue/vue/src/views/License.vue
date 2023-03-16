<template>
    <div style="padding: 55px 380px">
        <el-form ref="form" :model="form" :rules="rules" label-width="100px" size="normal" >
            <el-form-item prop="driverName" label="用户名" >
                <el-input :prefix-icon="Avatar" v-model="form.driverName" style="width: 320px" disabled/>
            </el-form-item>
            <el-form-item label="驾驶证件:">
                <el-image style="width: 200px; height: 200px;"
                          :src="form.url"
                          :preview-src-list="[form.url]"
                          preview-teleported="true">
                </el-image>
            </el-form-item>
            <el-form-item label="更新驾驶证件:">
                <el-upload
                        ref="upload"
                        v-model="form.url"
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
            <el-form-item>
                <el-button type="primary" @click="back" style="margin-left: -2px;margin-bottom: 20px">取消更新</el-button>
                <el-button type="primary" @click="save" style="margin-left: 54px;margin-bottom: 20px">确认更新</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
    import request from "../utils/request";
    import { Avatar } from '@element-plus/icons-vue'

    export default {
        name: "License",
        data(){
            return{
                form:{
                    driverName: sessionStorage.getItem("driverName").replace('"','').replace('"',''),
                },
                rules: {
                }
            }
        },
        setup(){
            return {
                Avatar,
            }
        },
        created() {
            this.load()
        },
        methods:{
            back(){
                this.load()
                this.$refs.upload.clearFiles()
            },
            //查询方法
            load(){
                request.get("/driver/findLicense",
                    {
                        params: {
                            driverName:this.form.driverName
                        }

                    }).then(res =>{
                    console.log(res)
                    this.form.url= res.data.url
                })
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
                this.form.url = res.data
            },
            //更新驾照
            save(){
                request.get("/driver/updateLicense",{
                    params:{
                        driverName:this.form.driverName,
                        url:this.form.url,

                    }
                }).then(res =>{
                    console.log(res)
                    if(res.code ==  '0'){
                        this.$message({
                            type:"success",
                            message: "上传成功"
                        })
                        this.$refs.upload.clearFiles()
                    }else {
                        this.$message({
                            type: "error",
                            message: "上传失败"
                        })
                    }
                })
            },
        },
    }
</script>

<style scoped>

</style>