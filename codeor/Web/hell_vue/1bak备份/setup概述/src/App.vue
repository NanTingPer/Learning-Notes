<template>
    <Person/>
</template>

<script lang="ts">
    //引入组件
    import Person from './components/Person.vue';

    export default{
        name: "App",
        //注册组件
        components: {Person}
    }
</script>

<style>
    .run{
        font-size: 20px;
    }
</style>