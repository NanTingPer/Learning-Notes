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

<script lang="ts">
    export default{
        name : 'Person',
        setup(){
            //数据 setup中的this是undefined,vue3弱化了this
            //这样的name不是响应式的 页面是没有变化了
            //但是naem的值确实改了
            let name = "张三";   
            var age = 18;
            var tel = "12366666666";
            
            //方法
            function ShowTel(){
                alert(tel);
            }

            function AddAge(){
                age += 1;
            }

            //返回数据 左边是键 右边是值 键可自定义
            //可简写return {name,age}
            return {name:name,age:age,ShowTel,AddAge} 
        }
    }
</script>

<style scoped>
    .div1{
        font-size: 15px;
    }
</style>