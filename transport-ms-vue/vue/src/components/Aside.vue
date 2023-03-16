<template>
    <el-menu
            style="width: 200px; min-height: calc(100vh - 55px)"
            router
            class="el-menu-vertical-demo"
    >
        <el-sub-menu index="driver">
            <template #title>
                <el-icon><User /></el-icon>
                <span>司机管理</span>
            </template>
            <el-menu-item index="check"><el-icon><UserFilled /></el-icon>查看司机</el-menu-item>
            <el-menu-item index="uncheck"><el-icon><TurnOff /></el-icon>审核司机</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="bus">
            <el-icon><Van /></el-icon>
            <span>车辆管理</span>
        </el-menu-item>
        <el-sub-menu index="station">
            <template #title>
                <el-icon><location /></el-icon>
                <span>站点管理</span>
            </template>
            <el-menu-item index="map"><el-icon><Promotion /></el-icon><span>站点地图</span></el-menu-item>
            <el-menu-item index="station"><el-icon><AddLocation /></el-icon>查看站点</el-menu-item>
            <el-menu-item index="through"><el-icon><DataBoard /></el-icon>某站点线路</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="routes">
            <template #title>
                <el-icon><Edit /></el-icon>
                <span>线路管理</span>
            </template>
            <el-menu-item index="mapRoute"><el-icon><Guide /></el-icon>线路地图</el-menu-item>
            <el-menu-item index="routes"><el-icon><AddLocation /></el-icon>所有线路</el-menu-item>
            <el-menu-item index="detail"><el-icon><Flag /></el-icon>线路详情</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="working">
            <el-icon><Setting /></el-icon>
            <span>工作分配</span>
        </el-menu-item>
    </el-menu>
</template>

<script>
    import { Location,User,Van,Edit,Setting,UserFilled,TurnOff,AddLocation,DataBoard,Flag,Promotion,Guide } from "@element-plus/icons-vue"
    export default {
        name: "Aside",
        components: {
            Location,
            User,
            Van,
            Edit,
            Setting,
            UserFilled,
            TurnOff,
            AddLocation,
            DataBoard,
            Flag,
            Promotion,
            Guide
        }
    }
</script>

<style scoped>

</style>