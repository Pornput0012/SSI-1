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

onMounted(async () => {
  try {
    const res = await getBrandById(999);
    console.log(res);
  } catch (error) {
    console.log(error.message);
  }
});
</script>

<template>
  <NavBar />
  <RouterView />
</template>

<style scoped></style>
