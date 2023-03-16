<template>
    <div>
        <!--    头部-->
        <HeaderD></HeaderD>
        <!--    主体-->
        <div style="display: flex">
            <!--      侧边栏-->
            <AsideD></AsideD>
            <!--      内容区域-->
            <router-view style="flex: 1"/>
        </div>
    </div>
</template>

<script>
    import HeaderD from "../components/HeaderD";
    import AsideD from "../components/AsideD";
    export default {
        name: "LayoutD",
        components:{
            HeaderD,
            AsideD
        }
    }
</script>

<style scoped>

</style>