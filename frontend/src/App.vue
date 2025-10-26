<script setup>
import NavBar from "./components/NavBar.vue";
import GlobalToast from "./components/GlobalToast.vue";
</script>

<template>
  <NavBar />
  <RouterView />
  <GlobalToast />
</template>

<style scoped></style>
