<script setup>
import { useRoute } from "vue-router";
import NavBar from "./components/NavBar.vue";
import { computed } from "vue";

const route = useRoute();

const authenFormPath = ['/register','/login','/verify-email']

const hasNav = computed(()=>!authenFormPath.includes(route.path))
</script>

<template>
  <NavBar />
  <RouterView />
</template>

<style scoped></style>
