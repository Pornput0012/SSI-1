<script setup>
import Breadcrumbs from '@/components/Breadcrumbs.vue'
import SaleItemList from '@/components/SaleItemList.vue'
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
  <Breadcrumbs :crumbs="[{ label: 'Product List', to: '/sale-items/list' }]" />
  <div class="w-full h-full px-10 pb-10 md:px-20 md:pb-20">
    <div class="w-full mt-8 flex justify-between items-center">
      <router-link
        :to="{ name: 'create-sale-item' }"
        class="itbms-sale-item-add btn btn-primary"
      >
        Add Sale Item
      </router-link>
      <div class="flex gap-4 items-center">
        <router-link
          :to="{ name: 'create-sale-item' }"
          class="itbms-manage-brand btn btn-primary"
        >
          Manage Brand
        </router-link>
        <label class="w-fit h-8 input input-xs hidden sm:flex">
          <input type="search" class="grow" placeholder="Search..." />
          <button class="cursor-pointer">
            <svg
              class="h-[1em] opacity-50"
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
            >
              <g
                stroke-linejoin="round"
                stroke-linecap="round"
                stroke-width="2.5"
                fill="none"
                stroke="currentColor"
              >
                <circle cx="11" cy="11" r="8"></circle>
                <path d="m21 21-4.3-4.3"></path>
              </g>
            </svg>
          </button>
        </label>
      </div>
    </div>
    <SaleItemList :sale-items="saleItems" :has-error="hasError" />
  </div>
</template>

<style scoped></style>
