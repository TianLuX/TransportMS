<template>
    <el-menu
            style="width: 200px; min-height: calc(100vh - 55px)"
            default-active="check"
            router
            class="el-menu-vertical-demo"
    >
        <el-menu-item index="myInf">
            <el-icon><User /></el-icon>
            <span>个人信息</span>
        </el-menu-item>
        <el-menu-item index="myWork">
            <el-icon><Setting /></el-icon>
            <span>工作信息</span>
        </el-menu-item>
        <el-menu-item index="myRoute">
            <el-icon><Guide /></el-icon>
            <span>线路地图</span>
        </el-menu-item>
    </el-menu>
</template>

<script>
    import { User,Setting,Guide } from "@element-plus/icons-vue"
    export default {
        name: "AsideD",
        components: {
            Location,
            User,
            Setting,
            Guide
        }
    }
</script>

<style scoped>

</style>