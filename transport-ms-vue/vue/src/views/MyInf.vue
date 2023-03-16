<template>
    <el-form :model="form" label-width="70px" style="padding: 20px;margin: 20px 369px">
        <el-form-item prop="driverName" label="姓名:">
            <el-input :prefix-icon="Avatar" v-model="form.driverName" style="width: 320px" disabled/>
        </el-form-item>
        <el-form-item label="性别:" >
            <el-radio-group v-model="form.sex">
                <el-radio label="男" name="sex" style="margin-right: 20px" />
                <el-radio label="女" name="sex" style="margin-right: 20px"/>
            </el-radio-group>
        </el-form-item>
        <el-form-item label="状态:" style="padding-right: 10px">
            <el-input :prefix-icon="Key" v-model="form.status" style="width: 320px" disabled/>
        </el-form-item>
        <el-form-item label="有效时间:">
            <el-col :span="11">
                <el-date-picker
                        v-model="form.time"
                        type="date"
                        placeholder="输入时间"
                        style="width: 320px"
                />
            </el-col>
        </el-form-item>
        <el-form-item label="驾驶证件:">
            <el-image style="width: 200px; height: 200px;"
                      :src="form.url"
                      :preview-src-list="[form.url]"
                      preview-teleported="true">
            </el-image>
        </el-form-item>
        <el-form-item>
            <el-button type="primary" @click="back" style="margin-left: -2px;margin-bottom: 20px">取消修改</el-button>
            <el-button type="primary" @click="save" style="margin-left: 54px;margin-bottom: 20px">确认更新</el-button>
        </el-form-item>
    </el-form>
</template>

<script>
    import request from "../utils/request";
    import { Avatar,Key } from '@element-plus/icons-vue'

    export default {
        name: "MyInf",
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
                Key,
            }
        },
        created() {
            this.load()
        },
        methods:{
            //查询方法
            load(){
                request.get("/driver/findLicense",
                    {
                        params: {
                            driverName:this.form.driverName
                        }

                    }).then(res =>{
                    console.log(res)
                    this.form.sex = res.data.sex
                    this.form.status = res.data.status
                    this.form.time = res.data.time
                    this.form.url= res.data.url
                })
            },
            back(){
                this.load()
            },
            save(){
                request.get("/driver/updateInf",{
                    params:{
                        driverName:this.form.driverName,
                        sex:this.form.sex,
                        time:this.form.time,
                    }
                }).then(res =>{
                    console.log(res)
                    if(res.code ==  '0'){
                        this.$message({
                            type:"success",
                            message: "更新成功"
                        })
                        this.load();
                    }else {
                        this.$message({
                            type: "error",
                            message: "更新失败"
                        })
                    }
                })
            },
        }
    }
</script>

<style scoped>

</style>