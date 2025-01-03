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
        //data是个函数 返回数据
        data() {
            return{
                name: '张三',
                age : 12,
                tel : '13489723333'
            }
        },
        //methods是方法 用来存放改组件的全部方法
        methods:{
            ShowTel(){
                alert(this.tel)
            },
            AddAge(){
                this.age += 1
            }
        }
    }
</script>

<style scoped>
    .div1{
        font-size: 15px;
    }
</style>