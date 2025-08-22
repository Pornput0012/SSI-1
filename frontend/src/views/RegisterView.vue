<script setup>
import AuthenForm from '@/components/AuthenForm.vue';
import { ref } from 'vue';

const isSaving = ref(false)
const createUser = (payload) => {
    isSaving.value =true
    console.log("hi");
    
    console.log(payload.userDetail);
    console.log(payload?.images);
    
    
}

</script>

<template>
    
    <AuthenForm @onSubmit="createUser" :disable-save-btn="isSaving" />
</template>

<style>

</style>