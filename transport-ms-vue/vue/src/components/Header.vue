<template>
    <div class="header">
        <div class="name">城市公交管理系统</div>
        <div style="flex: 1"></div>
        <div class="user">
            <el-dropdown>
                <span class="el-dropdown-link">
                    {{managerName}}
                    <el-icon class="el-icon--right">
                        <arrow-down />
                    </el-icon>
                </span>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item @click="$router.push('/person')">个人信息</el-dropdown-item>
                        <el-dropdown-item @click="$router.push('/login')" divided>退出系统</el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </div>
    </div>
</template>

<script>
    import { ArrowDown } from "@element-plus/icons-vue";
    export default {
        name: "Header",
        components: {
            ArrowDown
        },
        data() {
            return {
                managerName: sessionStorage.getItem("managerName").replace('"','').replace('"',''),
            }
        }
    }
</script>

<style scoped>

    .el-dropdown-link {
        cursor: pointer;
        color: var(--el-color-primary);
        padding-top: 18px;
        font-size: 15px;
    }

    .header {
        height: 55px;
        line-height: 55px;
        border-bottom: 1px solid #ccc;
        display: flex;
    }

    .name {
        width: 200px;
        color: cornflowerblue;
        font-size: 18px;
    }

    .user {
        width: 150px;
    }

</style>