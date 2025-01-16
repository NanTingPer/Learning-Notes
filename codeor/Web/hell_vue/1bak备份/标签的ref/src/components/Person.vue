<template>
    <div class="div1">
        <h1 ref="h1One">内容</h1>
        <button @click="ViewH1One"> 输出h1内容 </button>
    </div>
</template>

<script setup lang="ts" name="Person">
    import {ref,defineExpose} from 'vue'
    let h1One = ref()

    let a = ref(0)
    let b = ref(1)
    let c = ref(2)

    function ViewH1One(){
        console.log(h1One)
    }

    defineExpose({a,b,c})

</script>

<style scoped>
    .div1{
        font-size: 15px;
    }
</style>