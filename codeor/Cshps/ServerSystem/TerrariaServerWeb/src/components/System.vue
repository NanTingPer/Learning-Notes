<template>
    <el-table :data="tableData" style="width: 100%">
    <el-table-column prop="id" label="唯一标识" style="width: auto;"/>
    <el-table-column prop="name" label="名称" style="width: auto;"/>
    <el-table-column label="服务器配置">
        <el-table-column prop="options.autoCreate" label="世界大小" :formatter="WorldSizeFormatter"></el-table-column>
        <el-table-column prop="options.port" label="服务器端口"></el-table-column>
        <el-table-column prop="options.maxPlayers" label="服务器最大人数"></el-table-column>
        <el-table-column prop="options.password" label="服务器密码"></el-table-column>
        <el-table-column prop="options.ip" label="服务器监听IP"></el-table-column>
        <el-table-column prop="options.seed" label="世界种子"></el-table-column>
        <el-table-column prop="options.evil" label="世界邪恶" :formatter="WroldEvilFormatter"></el-table-column>
        <el-table-column prop="options.worldName" label="世界名称"></el-table-column>
    </el-table-column>
    </el-table>
</template>

<script lang="ts" setup>
import axios from 'axios'
import { ref, onMounted } from 'vue'
import { type ViewServer } from '../types/ViewServer'
import type { TableColumnCtx } from 'element-plus';

const tableData = ref<ViewServer[]>()

onMounted(() => {
   axios.post("/server/list", {}).then(response => {
    if(response.status == 200){
        tableData.value = response.data as ViewServer[]
    }
  })
});

function WorldSizeFormatter(_row: any, _column: TableColumnCtx<any>, cellValue: any, _index: number){
    if(cellValue == 1)
        return "小"
    else if(cellValue == 2)
        return "中"
    else 
        return "大"
}

function WroldEvilFormatter(_row: any, _column: TableColumnCtx<any>, cellValue: any, _index: number){
    if(cellValue == 1)
        return "随机"
    else if(cellValue == 2)
        return "腐坏"
    else if(cellValue == 3)
        return "猩红"
}
</script>

<style>
.cell {
    display: flex;
    text-align: center;
    justify-content: center;
    align-items: center;
}
</style>