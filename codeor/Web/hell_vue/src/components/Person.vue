﻿<template>
    <div class="div1">
        <h1>{{ sum }}</h1>
        <button @click="AddSum"> 点我+1 </button>
    </div>
</template>

<script setup lang="ts" name="Person">
    import getDog from './getDog';
    const {sum,AddSum} = getDog();

</script>

<style scoped>
    .div1{
        font-size: 15px;
    }
</style>