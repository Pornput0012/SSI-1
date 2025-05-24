<script setup>
import { computed, onMounted, ref } from "vue";
import NavBar from "./components/NavBar.vue";
import { getAllBrands } from "./services/BrandService";
import { useBrandStore } from "./stores/BrandStore";
const { getBrands } = useBrandStore();

const brands = computed(() => getBrands());
const hasError = ref("");

onMounted(async () => {
  if (brands.value === null) {
    try {
      const response = await getAllBrands();
      if (response.error) {
        throw new Error(response.error);
      }
    } catch (e) {
      hasError.value = e.message;
    }
  }
});
</script>

<template>
  <NavBar />
  <RouterView />
</template>

<style scoped></style>
