<template>
    <div style="padding: 55px 380px">
        <el-form ref="form" :model="form" :rules="rules" label-width="100px" size="normal" >
            <el-form-item prop="driverName" label="用户名" >
                <el-input :prefix-icon="Avatar" v-model="form.driverName" style="width: 320px" disabled/>
            </el-form-item>
            <el-form-item prop="password" label="新密码">
                <el-input :prefix-icon="Lock" v-model="form.password" style="width: 320px" show-password/>
            </el-form-item>
            <el-form-item prop="confirm" label="新密码确认">
                <el-input :prefix-icon="Lock" v-model="form.confirm" style="width: 320px" show-password/>
            </el-form-item>
            <el-form-item >
                <el-button type="primary" @click="rePassword" style="margin-left: 54px;margin-bottom: 20px">确认修改</el-button>
            </el-form-item>
        </el-form>
    </div>

</template>

<script>
    import { Avatar,Lock} from '@element-plus/icons-vue'
    import request from "../utils/request";
    export default {
        name: "PersonD",
        data(){
            return{
                form:{
                    driverName: sessionStorage.getItem("driverName").replace('"','').replace('"',''),
                    password:'',
                    confirm:'',
                },
                rules: {
                    password: [
                        { required: true, message: '请输入密码', trigger: 'blur' },
                    ],
                    confirm: [
                        { required: true, message: '请再次输入密码', trigger: 'blur' },
                    ],
                }
            }
        },
        setup(){
            return {
                Avatar,
                Lock
            }
        },
        methods:{
            rePassword(){
                if(this.form.password !== this.form.confirm){
                    this.$message({
                        type:"error",
                        message: '两次密码输入不一致'
                    })
                    return
                }
                this.$refs['form'].validate((valid) => {
                    if(valid){
                        request.get("/driver/repass",{
                            params:{
                                driverName:this.form.driverName,
                                password:this.form.password,

                            }
                        }).then(res =>{
                            console.log(res)
                            if(res.code ==  '0'){
                                this.$message({
                                    type:"success",
                                    message: "修改成功"
                                })
                                this.form.password=''
                                this.form.confirm=''
                            }else {
                                this.$message({
                                    type: "error",
                                    message: "修改失败"
                                })
                            }
                        })
                    }
                })
            },
        },
    }
</script>

<style scoped>

</style>