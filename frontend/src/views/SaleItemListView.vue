<script setup>
import SaleItemListL from '@/components/SaleItemListL.vue'
import { getAllSaleItems } from '@/services/SaleItemService'
import { useSaleItemStore } from '@/stores/SaleItemStore'
import { computed, onMounted, ref } from 'vue'

const { getSaleItems } = useSaleItemStore()

const saleItems = computed(() => getSaleItems())
const hasError = ref('')

onMounted(async () => {
  try {
    const response = await getAllSaleItems()
    if (response.error) {
      throw new Error(response.error)
    }
  } catch (e) {
    hasError.value = e.message
  }
})
</script>

<template>
  <div class="w-full h-full">
    <SaleItemListL :sale-items="saleItems" :has-error="hasError" />
  </div>
</template>

<style scoped></style>
