<template>
    <div class="bg">
        <div class="register">
            <div class="text">欢迎注册</div>
            <el-form ref="form" :model="form" :rules="rules" label-width="40px" size="normal" >
                <el-form-item prop="driverName">
                    <el-input :prefix-icon="Avatar" v-model="form.driverName" placeholder="请输入账号" style="width: 320px" clearable/>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input :prefix-icon="Lock" v-model="form.password" style="width: 320px" placeholder="请输入密码" show-password/>
                </el-form-item>
                <el-form-item prop="confirm">
                    <el-input :prefix-icon="Lock" v-model="form.confirm" style="width: 320px" placeholder="请再次输入密码" show-password/>
                </el-form-item>
                <el-form-item>
                    <div style="display: flex">
                        <el-input :prefix-icon="Key" v-model="form.validCode" style="width: 50%;" placeholder="请输入验证码"></el-input>
                        <ValidCode @input="createValidCode" />
                    </div>
                </el-form-item>
                <el-form-item style="margin-bottom: 0px; margin-left: 16px" prop="sex">
                    <el-radio-group v-model="form.sex">
                        <el-radio label="男" name="sex" style="margin-right: 20px" />
                        <el-radio label="女" name="sex" style="margin-right: 20px"/>
                    </el-radio-group>
                </el-form-item>
                <el-form-item style="margin-left: 76px; margin-bottom: 0px">
                    <el-upload
                            ref="upload"
                            v-model="form.url"
                            action="http://localhost:9090/files/upload"
                            :on-success="filesUploadSuccess"
                            :before-upload="beforeUpload"
                            :on-change="handleChange"
                            :file-list="fileList"
                    >
                        <el-button type="primary">上传驾照</el-button>
                        <template #tip>
                            <div class="el-upload__tip">
                                上传单个jpg/png文件需小于1M
                            </div>
                        </template>

                    </el-upload>
                </el-form-item>
                <el-form-item >
                    <el-button type="primary" @click="back" style="margin-left: 47px;margin-bottom: 20px">返回登录</el-button>
                    <el-button type="primary" @click="register" style="margin-left: 50px;margin-bottom: 20px">司机注册</el-button>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script>
    import { Avatar,Lock,Key } from '@element-plus/icons-vue'
    import request from "../utils/request";
    import ValidCode from "../components/ValidCode";
    export default {
        name: "Register",
        components: {
            ValidCode
        },
        data(){
            return{
                form:{
                    driverName:'',
                    password:'',
                    confirm:'',
                    sex:'男',
                },
                rules: {
                    driverName: [
                        { required: true, message: '请输入用户名', trigger: 'blur' },
                    ],
                    password: [
                        { required: true, message: '请输入密码', trigger: 'blur' },
                    ],
                    confirm: [
                        { required: true, message: '请再次输入密码', trigger: 'blur' },
                    ],
                },
                ValidCode:''
            }
        },
        setup(){
            return {
                Avatar,
                Lock,
                Key
            }
        },
        methods:{
            // 接收验证码组件提交的 4位验证码
            createValidCode(data) {
                this.validCode = data
            },
            register(){
                if(this.form.password !== this.form.confirm){
                    this.$message({
                        type:"error",
                        message: '两次密码输入不一致'
                    })
                    return
                }
                this.$refs['form'].validate((valid) => {
                    if(valid) {
                        if (!this.form.validCode) {
                            this.$message.error("请填写验证码")
                            return
                        }
                        if(this.form.validCode.toLowerCase() !== this.validCode.toLowerCase()) {
                            this.$message.error("验证码错误")
                            return
                        }
                        request.post("/driver/register", this.form).then(res => {
                            if (res.code == '0') {
                                this.$message({
                                    type: "success",
                                    message: "注册成功"
                                })
                                this.$router.push("/login")
                            } else {
                                this.$message({
                                    type: "error",
                                    message: res.msg
                                })
                            }
                        })
                     }
                })
            },
            filesUploadSuccess (res){
                console.log(res)
                this.form.url = res.data
            },
            back(){
                this.$router.push("/login")
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
        }
    }
</script>

<style scoped>
    .bg{
        width: 100%;
        height: 100vh;
        background: url(../assets/bg.png) no-repeat;
        background-size:100% 100%;
        background-attachment:fixed;
        overflow:hidden;
    }

    .register{
        width: 400px;
        margin: 160px auto;
        background-color: rgba(255,255,255,0.5);
        border-radius: 10px;
    }

    .text{
        color: #777;
        font-size: 30px;
        text-align: center;
        padding: 30px 0;
    }
</style>