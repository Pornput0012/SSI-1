<script setup>
import { onMounted, ref } from "vue";
import NavBar from "./components/NavBar.vue";
import { getAllBrands } from "./services/BrandService";
const hasError = ref("");

// onMounted(async () => {
//   try {
//     const response = await getAllBrands();
//     if (response.error) {
//       throw new Error(response.error);
//     }
//   } catch (e) {
//     hasError.value = e.message;
//   }
// });
</script>

<template>
  <NavBar />
  <RouterView />
</template>

<style scoped></style>
