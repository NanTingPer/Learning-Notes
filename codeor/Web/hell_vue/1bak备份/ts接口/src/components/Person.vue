<template>
    <div class="div1">
    </div>
</template>

<script setup lang="ts" name="Person">
    import {type Person } from "@/types/Person"

    let ren:Person = {name:"ren",id:"1",age:12}
</script>

<style scoped>
    .div1{
        font-size: 15px;
    }
</style>