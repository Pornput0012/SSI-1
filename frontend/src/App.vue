<script setup>
import { computed, onMounted, ref } from "vue";
import NavBar from "./components/NavBar.vue";
import { useSaleItemStore } from "./stores/SaleItemStore";
import { getAllSaleItems } from "./services/SaleItemService";
import { getBrandById } from "./services/BrandService";

const { getSaleItems } = useSaleItemStore();

const saleItems = computed(() => getSaleItems());
const hasError = ref("");

onMounted(async () => {
  if (saleItems.value === null) {
    try {
      const response = await getAllSaleItems();
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
