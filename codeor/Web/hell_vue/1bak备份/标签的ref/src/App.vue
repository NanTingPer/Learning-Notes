<template>
    <Person ref="ren"/>
    <button @click="ShowProper"></button>
</template>

<script lang="ts" setup name="App">
    //引入组件
    import { ref } from 'vue';
    import Person from './components/Person.vue';
    
    let ren = ref()

    function ShowProper(){
        console.log(ren)
    }

</script>

<style>
    .run{
        font-size: 20px;
    }
</style>