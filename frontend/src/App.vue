<script setup>
import { computed, onMounted, ref } from "vue";
import NavBar from "./components/NavBar.vue";
import { useSaleItemStore } from "./stores/SaleItemStore";
import { getAllSaleItems } from "./services/SaleItemService";
import { getAllBrands, getBrandById } from "./services/BrandService";
import FormBrand from "./components/FormBrand.vue";
import { useBrandStore } from "./stores/BrandStore";

const { getSaleItems } = useSaleItemStore();
const {getBrands} = useBrandStore()

const saleItems = computed(() => getSaleItems());
const brands = computed(()=> getBrands())
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
