<script setup>
import SaleItemList from "@/components/SaleItemList.vue";
import SaleItemModel from "@/components/SaleItemModel.vue";
import { getAllSaleItems } from "@/services/SaleItemService";
import { useSaleItemStore } from "@/stores/SaleItemStore";
import { computed, onMounted, ref } from "vue";

const { getSaleItems } = useSaleItemStore();

const saleItems = computed(() => getSaleItems());

onMounted(async () => {
  if (saleItems.value.length === 0) {
    await getAllSaleItems();
  }
});
</script>

<template>
  <div class="w-full h-full p-10  md:p-20">
    <h1 class="font-bold text-2xl">Products</h1>
    <SaleItemList class="mt-10" :sale-items="saleItems" />
  </div>
</template>

<style scoped></style>
