<script setup>
import Breadcrumbs from '@/components/Breadcrumbs.vue'
import FormSaleItem from '@/components/FormSaleItem.vue'
import SaleItemGallery from '@/components/SaleItemGallery.vue'
import SaleItemGalleryModel from '@/components/SaleItemGalleryModel.vue'
import { getAllBrands } from '@/services/BrandService'
import { getAllSaleItems, getAllSaleItems_v2 } from '@/services/SaleItemService'
import { useBrandStore } from '@/stores/BrandStore'
import { useSaleItemStore } from '@/stores/SaleItemStore'
import { useNotiStore } from '@/stores/์NotiStore'
import { computed, onMounted, ref } from 'vue'

const { getSaleItems } = useSaleItemStore()

const saleItems = computed(() => getSaleItems())
const hasError = ref('')
const saleItemStore = useSaleItemStore()

const brandStore = useBrandStore()
const brandOptions = ref([])
const brandSearchText = ref('')
const selectedBrands = ref([])
const showHints = ref(false)

const applyFilter = async () => {
  try {
    const filterNames = selectedBrands.value.map((b) => b.name)
    const response = await getAllSaleItems_v2(filterNames)
     if (response && !response.error) {
      saleItemStore.setSaleItemsPage(response)
    } else {
      throw new Error(response?.error)
    }
  } catch (error) {
    hasError.value = error.message
  }
}

// filter hint จากคำที่พิมพ์
const hints = computed(() => {
  if (!brandSearchText.value) return []
  const lower = brandSearchText.value.toLowerCase()
  return brandOptions.value
    .filter(
      (brand) =>
        brand.name.toLowerCase().includes(lower) &&
        !selectedBrands.value.some((b) => b.name === brand.name)
    )
})

const onSearchInput = () => {
  showHints.value = !!brandSearchText.value
}

const selectHint = (brand) => {
  selectedBrands.value.push(brand)
  brandSearchText.value = ''
  showHints.value = false
}

const removeBrand = (brand) => {
  selectedBrands.value = selectedBrands.value.filter(
    (b) => b.name !== brand.name
  )
  applyFilter()
}

const clearAllBrands = () => {
  selectedBrands.value = []
  applyFilter()
}

onMounted(async () => {
  
  try {
    const response = await getAllSaleItems()
    if (response.error) {
      throw new Error(response.error)
    }
  } catch (e) {
    hasError.value = e.message
  }

  try {
    const response = await getAllBrands()
    if (response && !response.error) {
      brandOptions.value = response
      brandStore.setBrand(response)
    }
  } catch (error) {
    hasError.value = error.message
  }
})

const notiStore = useNotiStore()
const showCreatedModal = ref(false)
onMounted(() => {
  if (notiStore.createSuccess) {
    showCreatedModal.value = true
    notiStore.createSuccess = false
  }
});

function handlePageChange(page) {
  
  console.log(page);
  
}
</script>

<template>
  <Breadcrumbs :crumbs="[{ label: 'Sale Items' }]" />
  <div class="w-full h-full px-10 pb-10 md:px-20 md:pb-20">
    <div class="w-full flex justify-between items-center">
      <h1 class="font-bold text-2xl">Products</h1>
      <div class="flex gap-4 items-center">
        <router-link
          :to="{ name: 'create-sale-item' }"
          class="itbms-sale-item-add btn btn-primary"
        >
          Add Sale Item
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

    <!-- Filter Section -->
    <div class="mt-6 w-full max-w-md">
      <div class="flex gap-3 items-center">
        <!-- Input + Hint -->
        <div class="relative flex-1">
          <input
            type="text"
            class="itbms-brand-filter input input-sm w-full border border-gray-300 rounded px-3 py-2"
            placeholder="Filter by brand(s)"
            v-model="brandSearchText"
            @input="onSearchInput"
          />

          <!-- Hint dropdown -->
          <ul
            v-if="showHints && hints.length"
            class="absolute bg-white border border-gray-300 rounded mt-1 w-full z-10 shadow max-h-40 overflow-y-auto"
          >
            <li
              v-for="(brand, idx) in hints"
              :key="idx"
              @mousedown.prevent="selectHint(brand)"
              class="px-3 py-2 hover:bg-gray-100 cursor-pointer"
            >
              {{ brand.name }}
            </li>
          </ul>
        </div>

        <!-- Filter button -->
        <button
          class="itbms-brand-filter-button btn btn-sm bg-green-500 hover:bg-green-600 px-3 py-2"
          @click="applyFilter"
        >
          <img src="../assets/icons/filter.png" alt="filter" class="w-5 h-5">
        </button>

        <!-- Clear button -->
        <button
          v-if="selectedBrands.length"
          class="itbms-brand-filter-clear btn btn-sm bg-red-500 hover:bg-red-600 text-white font-bold px-3 py-2"
          @click="clearAllBrands"
        >
          Clear
        </button>
      </div>

      <!-- Selected brands -->
      <div class="flex flex-wrap gap-2 mt-3">
        <div
          v-for="(brand, index) in selectedBrands"
          :key="index"
          class="itbms-filter-item bg-gray-200 px-3 py-1 rounded-full flex items-center gap-1"
        >
          <span class="itbms-filter-item">{{ brand.name }}</span>
          <button
            class="itbms-filter-item-clear text-red-500 font-bold cursor-pointer"
            @click="removeBrand(brand)"
          >
            ×
          </button>
        </div>
      </div>
    </div>

    <SaleItemGallery
      class="mt-10"
      :sale-items="saleItems"
      :page-info="{
        last: false,
        first: true,
        totalPages: 13,
        totalElements: 63,
        size: 5,
        sort: 'color: ASC',
        page: 1,
      }"
      :has-error="hasError"
      @page-changed="handlePageChange"
    />
  </div>
  <div v-if="showCreatedModal" class="modal modal-open">
    <div class="modal-box text-center">
      <h3
        class="font-bold text-[14px] md:text-[16px] lg:text-[18px] text-success"
      >
        Successfully!
      </h3>
      <p class="itbms-message py-4 text-[12px] md:text-[14px] lg:text-[16px]">
        The sale item has been successfully added.
      </p>
      <div class="itbms-button modal-action justify-center">
        <button
          @click="showCreatedModal = false"
          class="btn btn-primary bg-purple-700"
        >
          OK
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
