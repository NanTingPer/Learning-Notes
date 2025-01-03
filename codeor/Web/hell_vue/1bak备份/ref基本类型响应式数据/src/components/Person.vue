<template>
    <div class="div1">
        <h1>
            姓名: {{ name }} <br>
            年龄: {{ age }} <br>
            <button @click="ShowTel"> 显示Tel </button>
            <button @click="AddAge"> 年龄变大 </button>
        </h1>
    </div>
</template>

<!-- <script lang="ts">
    export default{
        name: "Person",
    }
</script> -->

<script setup lang="ts" name="Person">
    import { ref } from 'vue'

    let name = ref("张三")
    var age = ref(18)
    var tel = "12366666666"
    
    //方法
    function ShowTel(){
        alert(tel)
    }

    function AddAge(){
        age.value += 1
    }
</script>

<style scoped>
    .div1{
        font-size: 15px;
    }
</style>