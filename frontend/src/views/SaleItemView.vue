<script setup>
import Breadcrumbs from "@/components/Breadcrumbs.vue";
// import FormSaleItem from '@/components/FormSaleItem.vue'
import SaleItemGallery from "@/components/SaleItemGallery.vue";
// import SaleItemGalleryModel from '@/components/SaleItemGalleryModel.vue'
import { getAllBrands } from "@/services/BrandService";
import {
  getAllSaleItems,
  getAllSaleItems_v2,
} from "@/services/SaleItemService";
import { useBrandStore } from "@/stores/BrandStore";
import { useFilterStore } from "@/stores/FilterStore";
import { useSaleItemStore } from "@/stores/SaleItemStore";
import { useNotiStore } from "@/stores/์NotiStore";
import { computed, onMounted, ref, watch } from "vue";

const { getSaleItemsPage } = useSaleItemStore();
const {
  getFilterList,
  setSelectBrand,
  setCurrentPage,
  clearAllSelectedBrands,
  removeBrand,
  setPageSize,
  setSortBrand,
} = useFilterStore();
const filterStore = useFilterStore();

const sortField = computed(() => filterStore.sortField);
const sortDirection = computed(() => filterStore.sortDirection);
const currentPage = computed(() => filterStore.currentPage);
const pageSize = computed(() => filterStore.pageSize);
const selectedBrands = computed(() => filterStore.selectedBrands);

const filterBrands = computed(() => getFilterList());
const saleItems = computed(() => getSaleItemsPage());

const hasError = ref("");

const showFilterDropdown = ref(false);
const brandStore = useBrandStore();
const brandOptions = ref([]);

const applyFilter = async () => {
  try {
    const response = await getAllSaleItems_v2({
      filterBrands: filterBrands.value,
      page: currentPage.value,
      size: pageSize.value,
      sortField: sortField.value,
      sortDirection: sortDirection.value,
    });
    localStorage.setItem(
      "selectedBrands",
      JSON.stringify(selectedBrands.value)
    );
    localStorage.setItem(
      "pageInfo",
      JSON.stringify({
        page: currentPage.value,
        size: pageSize.value,
        sortField: sortField.value,
        sortDirection: sortDirection.value,
      })
    );
    if (response.error && !response) {
      throw new Error(response?.error);
    }
  } catch (error) {
    hasError.value = error.message;
  }
};

const selectBrand = (brand) => {
  setSelectBrand(brand);
  showFilterDropdown.value = false;
};

const clearAllBrands = () => {
  clearAllSelectedBrands();
};

// เมื่อ selectedBrands เปลี่ยน ให้ reset หน้า และเรียก applyFilter
watch(
  selectedBrands,
  () => {
    setCurrentPage(0);
    applyFilter();
  },
  { deep: true }
);

// เมื่อ sortField หรือ sortDirection เปลี่ยน
watch([sortField, sortDirection, pageSize], () => {
  setCurrentPage(0);
  applyFilter();
});

onMounted(async () => {
  try {
    const response = await getAllSaleItems_v2({
      page: currentPage.value,
      size: pageSize.value,
    });
    if (response.error) {
      throw new Error(response.error);
    }
  } catch (e) {
    hasError.value = e.message;
  }
});
onMounted(async () => {
  try {
    const response = await getAllBrands();
    if (response && !response.error) {
      brandOptions.value = response.sort((a, b) =>
        a.name.localeCompare(b.name)
      );
      brandStore.setBrand(response);
    }
  } catch (error) {
    hasError.value = error.message;
  }
});

const notiStore = useNotiStore();
const showCreatedModal = ref(false);
onMounted(() => {
  if (notiStore.createSuccess) {
    showCreatedModal.value = true;
    notiStore.createSuccess = false;
  }
});

function handlePageChange(pageIndex) {
  setCurrentPage(pageIndex);
  applyFilter();
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
    <div class="mt-6 w-full">
      <div class="flex justify-between items-center w-full">
        <div class="flex gap-3 items-center">
          <!-- Filter input -->
          <div class="relative flex-1">
            <input
              type="text"
              class="itbms-brand-filter input input-sm w-full border border-gray-300 rounded px-3 py-2"
              placeholder="Filter by brand(s)"
              readonly
            />

            <!-- Filter dropdown -->
            <ul
              v-if="showFilterDropdown"
              class="absolute bg-white border border-gray-300 rounded mt-1 w-full z-10 shadow max-h-40 overflow-y-auto"
            >
              <li
                v-for="(brand, idx) in brandOptions"
                :key="idx"
                @mousedown.prevent="selectBrand(brand)"
                class="px-3 py-2 hover:bg-gray-100 cursor-pointer"
              >
                {{ brand.name }}
              </li>
            </ul>
          </div>

          <!-- Filter button -->
          <button
            class="itbms-brand-filter-button btn btn-sm bg-green-500 hover:bg-green-600 px-3 py-2"
            @click="showFilterDropdown = !showFilterDropdown"
          >
            <img
              src="../assets/icons/filter.png"
              alt="filter"
              class="w-5 h-5"
            />
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
        <!-- number of element and sort  -->
        <div class="flex gap-3 items-center">
          <label for="numberElement">Show</label>
          <select
            id="numberElement"
            class="itbms-page-size select"
            @input="setPageSize($event.target.value)"
          >
            <option
              v-for="ele in [5, 10, 20]"
              :value="ele"
              :selected="ele === pageSize"
            >
              {{ ele }}
            </option>
          </select>

          <button
            @click="setSortBrand()"
            class="itbms-brand-none btn cursor-pointer"
            :class="!sortDirection && !sortField && 'btn-active'"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="16"
              height="16"
              fill="currentColor"
              class="bi bi-filter"
              viewBox="0 0 16 16"
            >
              <path
                d="M6 10.5a.5.5 0 0 1 .5-.5h3a.5.5 0 0 1 0 1h-3a.5.5 0 0 1-.5-.5m-2-3a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5m-2-3a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-11a.5.5 0 0 1-.5-.5"
              />
            </svg>
          </button>

          <button
            @click="setSortBrand('ASC')"
            class="itbms-brand-asc btn cursor-pointer"
            :class="sortDirection === 'ASC' && sortField && 'btn-active'"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="16"
              height="16"
              fill="currentColor"
              class="bi bi-sort-up"
              viewBox="0 0 16 16"
            >
              <path
                d="M3.5 12.5a.5.5 0 0 1-1 0V3.707L1.354 4.854a.5.5 0 1 1-.708-.708l2-1.999.007-.007a.5.5 0 0 1 .7.006l2 2a.5.5 0 1 1-.707.708L3.5 3.707zm3.5-9a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5M7.5 6a.5.5 0 0 0 0 1h5a.5.5 0 0 0 0-1zm0 3a.5.5 0 0 0 0 1h3a.5.5 0 0 0 0-1zm0 3a.5.5 0 0 0 0 1h1a.5.5 0 0 0 0-1z"
              />
            </svg>
          </button>
          <button
            @click="setSortBrand('DESC')"
            class="itbms-brand-desc btn cursor-pointer"
            :class="sortDirection === 'DESC' && sortField && 'btn-active'"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="16"
              height="16"
              fill="currentColor"
              class="bi bi-sort-down"
              viewBox="0 0 16 16"
            >
              <path
                d="M3.5 2.5a.5.5 0 0 0-1 0v8.793l-1.146-1.147a.5.5 0 0 0-.708.708l2 1.999.007.007a.497.497 0 0 0 .7-.006l2-2a.5.5 0 0 0-.707-.708L3.5 11.293zm3.5 1a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5M7.5 6a.5.5 0 0 0 0 1h5a.5.5 0 0 0 0-1zm0 3a.5.5 0 0 0 0 1h3a.5.5 0 0 0 0-1zm0 3a.5.5 0 0 0 0 1h1a.5.5 0 0 0 0-1z"
              />
            </svg>
          </button>
        </div>
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
      v-if="saleItems"
      class="mt-10"
      :sale-items="saleItems?.content"
      :page-info="{ ...saleItems, page: currentPage }"
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
