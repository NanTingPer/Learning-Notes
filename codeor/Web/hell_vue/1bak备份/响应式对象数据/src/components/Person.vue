<template>
    <div class="div1">
        <!-- 遍历数组 并打印内容 -->
        <h1 v-for="ca in CatArray" :key="ca.Name">{{ ca.Name }}  {{ ca.Age }}</h1>

        <!-- 显示对象 -->
        <h1> {{ Cat.Lift }} {{ Cat.Nmae }} </h1>
        <h1>
            <button @click="SetCatName"> 更改小猫的名字 </button>
            <button @click="SetArrayOne"> 更改数组第一个元素 </button>
        </h1>
    </div>
</template>

<script setup lang="ts" name="Person">
    import { reactive } from 'vue'
    //创建对象
    let Cat = reactive({Lift:64, Nmae:"小猫"})

    //创建数组
    let CatArray = reactive([
        {Name:"Steam",Age:24},
        {Name:"Epic",Age:31}
    ])

    function SetCatName(){
        Cat.Nmae = "小狗"
    }
    function SetArrayOne(){
        CatArray[0].Name = "腾讯"
    }

</script>

<style scoped>
    .div1{
        font-size: 15px;
    }
</style>