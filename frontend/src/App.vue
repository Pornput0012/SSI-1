<script setup>
import { useRoute } from "vue-router";
import NavBar from "./components/NavBar.vue";
import { computed } from "vue";

const route = useRoute();

const authenFormPath = ['/register','/login']

const hasNav = computed(()=>!authenFormPath.includes(route.path))

console.log(route.path);

</script>

<template>
  <NavBar v-if="hasNav" />
  <RouterView />
</template>

<style scoped></style>
