<script setup>
import { useRoute } from "vue-router";
import NavBar from "./components/NavBar.vue";
import { computed } from "vue";
import GlobalToast from "./components/GlobalToast.vue";

const route = useRoute();



const authenFormPath = ['/register','/signin','/verify-email']

const hasNav = computed(()=>!authenFormPath.includes(route.path))
</script>

<template>
  <NavBar />
  <RouterView />
  <GlobalToast />
</template>

<style scoped></style>
