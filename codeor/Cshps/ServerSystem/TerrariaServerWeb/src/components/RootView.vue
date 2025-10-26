<script lang="ts">
    import TerrariaServerInfoDto from '../Models/TerrariaServerInfoDto';
    export default() { 
        methods: {
            getServerList() {
                return 1;
            };
        },
    }
</script>

<template>
<div>
    <h1> {{ getServerList() }} </h1>
</div>
</template>

<style>

</style>