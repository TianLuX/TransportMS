<template>
    <div class="bg">
        <div class="login">
            <div class="text">欢迎登录</div>
            <el-form ref="form" :model="form" :rules="rules" label-width="40px" size="normal" >
                <el-form-item prop="username">
                    <el-input :prefix-icon="Avatar" v-model="form.username" placeholder="请输入账号" style="width: 320px" clearable/>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input :prefix-icon="Lock" v-model="form.password" style="width: 320px" placeholder="请输入密码" show-password/>
                </el-form-item>
                <el-form-item>
                    <div style="display: flex">
                        <el-input :prefix-icon="Key" v-model="form.validCode" style="width: 50%;" placeholder="请输入验证码"></el-input>
                        <ValidCode @input="createValidCode" />
                    </div>
                </el-form-item>
                <el-form-item style="padding-left: 52px;" prop="identity">
                    <el-radio-group v-model="form.identity">
                        <el-radio label="管理员" name="identity" style="margin-right: 20px" />
                        <el-radio label="司机" name="identity" style="margin-right: 20px;padding-left: 73px;"/>
                    </el-radio-group>
                </el-form-item>
                <el-form-item style="padding-bottom: 30px;padding-left: 20px;">
                    <el-button style="width: 40%" type="primary" @click="$router.push('/register')">注册</el-button>
                    <el-button style="width: 40%" type="primary" @click="login">登录</el-button>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script>
    import { Avatar,Lock,Key } from '@element-plus/icons-vue'
    import ValidCode from "../components/ValidCode";
    import request from "../utils/request";
    export default {
        name: "Login",
        components: {
          ValidCode
        },
        data(){
            return{
                form:{
                    username:'',
                    password:'',
                    identity:'管理员',
                },
                rules: {
                    username: [
                        { required: true, message: '请输入用户名', trigger: 'blur' },
                    ],
                    password: [
                        { required: true, message: '请输入密码', trigger: 'blur' },
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
        methods: {
            // 接收验证码组件提交的 4位验证码
            createValidCode(data) {
                this.validCode = data
            },
            login(){
                this.$refs['form'].validate((valid) => {
                    if(valid){
                        if (!this.form.validCode) {
                            this.$message.error("请填写验证码")
                            return
                        }
                        if(this.form.validCode.toLowerCase() !== this.validCode.toLowerCase()) {
                            this.$message.error("验证码错误")
                            return
                        }
                        if(this.form.identity == '管理员'){
                            request.get("/manager/login",
                                {
                                    params: {
                                        managerName: this.form.username,
                                        password:this.form.password
                                    }

                                }).then(res => {
                                if(res.code == '0' ){
                                    this.$message({
                                        type:"success",
                                        message: "登录成功"
                                    })
                                    sessionStorage.setItem("managerName",JSON.stringify(this.form.username));
                                    this.$router.push("/person")
                                }else{
                                    this.$message({
                                        type:"error",
                                        message:"用户名或密码错误"
                                    })
                                }
                            })
                        }else{
                            request.get("/driver/login",
                                {
                                    params: {
                                        driverName: this.form.username,
                                        password:this.form.password
                                    }

                                }).then(res => {
                                if(res.code == '0' ){
                                    this.$message({
                                        type:"success",
                                        message: "登录成功"
                                    })
                                    sessionStorage.setItem("driverName",JSON.stringify(this.form.username));
                                    this.$router.push("/personD")
                                }else{
                                    this.$message({
                                        type:"error",
                                        message:"用户名或密码错误"
                                    })
                                }
                            })
                        }
                    }
                })


            }
        }
    }
</script>


<style>
    .bg{
        width: 100%;
        height: 100vh;
        background: url(../assets/bg.png) no-repeat;
        background-size:100% 100%;
        background-attachment:fixed;
        overflow:hidden;
    }

    .login{
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