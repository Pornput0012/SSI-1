<script setup>
import Breadcrumbs from "@/components/Breadcrumbs.vue";
import SaleItemGallery from "@/components/SaleItemGallery.vue";
import { getAllBrands } from "@/services/BrandService";
import { getAllSaleItems_v2 } from "@/services/SaleItemService";
import { useBrandStore } from "@/stores/BrandStore";
import { useFilterStore } from "@/stores/FilterStore";
import { useSaleItemStore } from "@/stores/SaleItemStore";
import { useNotiStore } from "@/stores/์NotiStore";
import { computed, onMounted, ref } from "vue";

const PAGE_SIZE_OPTIONS = [5, 10, 20];
const STORAGE_KEYS = {
  SELECTED_BRANDS: "selectedBrands",
  PAGE_INFO: "pageInfo",
};

const filterStore = useFilterStore();
const saleItemStore = useSaleItemStore();
const notiStore = useNotiStore();
const brandStore = useBrandStore();

const {
  getFilterList,
  setSelectBrand,
  setCurrentPage,
  clearAllSelectedBrands,
  removeBrand,
  setPageSize,
  setSortBrand,
  setSelectedBrand,
  setSortField,
  setSortDirection,
  resetCurrentPage,
} = filterStore;

const { getSaleItemsPage } = saleItemStore;

const sortField = computed(() => filterStore.sortField);
const sortDirection = computed(() => filterStore.sortDirection);
const currentPage = computed(() => filterStore.currentPage);
const pageSize = computed(() => filterStore.pageSize);
const selectedBrands = computed(() => filterStore.selectedBrands);
const filterBrands = computed(() => getFilterList());
const saleItems = computed(() => getSaleItemsPage());

const hasError = ref("");
const showFilterDropdown = ref(false);
const showCreatedModal = ref(false);
const brandOptions = ref([]);

const saveToLocalStorage = () => {
  sessionStorage.setItem(
    STORAGE_KEYS.SELECTED_BRANDS,
    JSON.stringify(selectedBrands.value)
  );
  sessionStorage.setItem(
    STORAGE_KEYS.PAGE_INFO,
    JSON.stringify({
      page: currentPage.value,
      size: pageSize.value,
      sortField: sortField.value,
      sortDirection: sortDirection.value,
    })
  );
};

const getFromLocalStorage = () => {
  const selectedBrands = JSON.parse(
    sessionStorage.getItem(STORAGE_KEYS.SELECTED_BRANDS)
  );
  const pageInfo = JSON.parse(sessionStorage.getItem(STORAGE_KEYS.PAGE_INFO));

  return { selectedBrands, pageInfo };
};

const setDefaultStoreValues = (pageInfo) => {
  const { selectedBrands } = getFromLocalStorage();

  setSelectedBrand(Array.isArray(selectedBrands) ? selectedBrands : []);
  setCurrentPage(pageInfo?.page ?? 0);
  setPageSize(pageInfo?.size ?? 10);
  setSortField(pageInfo?.sortField ?? null);
  setSortDirection(pageInfo?.sortDirection ?? null);
};

const applyFilter = async (closeModal = false) => {
  try {

    showFilterDropdown.value = closeModal;


    const response = await getAllSaleItems_v2({
      filterBrands: filterBrands.value,
      page: currentPage.value,
      size: pageSize.value,
      sortField: sortField.value,
      sortDirection: sortDirection.value,
    });

    saveToLocalStorage();

    if (response.error) {
      throw new Error(response?.error);
    }
  } catch (error) {
    hasError.value = error.message;
  }
};

const selectBrand = async (brand) => {
  setSelectBrand(brand);
  resetCurrentPage();
  await applyFilter(true);
};

const clearAllBrands = async () => {
  clearAllSelectedBrands();
  resetCurrentPage();
  await applyFilter();
};

const removeBrandAndApply = async (brand) => {
  removeBrand(brand);
  resetCurrentPage();
  await applyFilter();
};

const handlePageChange = async (pageIndex) => {
  setCurrentPage(pageIndex);
  await applyFilter();
};

const handlePageSizeChange = async (event) => {
  setPageSize(event.target.value);
  resetCurrentPage();
  await applyFilter();
};

const handleSortChange = async (sortType) => {
  if (sortType) {
    setSortBrand(sortType);
  } else {
    setSortBrand("");
  }

  resetCurrentPage();
  await applyFilter();
};

const initializeBrands = async () => {
  try {
    const response = await getAllBrands();
    if (!response.error) {
      brandOptions.value = response.sort((a, b) =>
        a.name.localeCompare(b.name)
      );
    } else {
      throw new Error(response?.error);
    }
  } catch (error) {
    hasError.value = error.message;
  }
};

const initializeSaleItems = async (pageInfo) => {
  try {
    const response = await getAllSaleItems_v2({
      filterBrands: getFilterList(),
      page: pageInfo?.page ?? 0,
      size: pageInfo?.size ?? 10,
      sortField: pageInfo?.sortField ?? null,
      sortDirection: pageInfo?.sortDirection ?? null,
    });

    if (response.error) {
      throw new Error(response?.error);
    }
  } catch (error) {
    hasError.value = error.message;
  }
};

const showSuccessModal = () => {
  if (notiStore.createSuccess) {
    showCreatedModal.value = true;
    notiStore.createSuccess = false;
  }
};

onMounted(async () => {
  window.addEventListener("resize", updateWidth);

  try {
    const { pageInfo } = getFromLocalStorage();

    setDefaultStoreValues(pageInfo);
    await initializeBrands();
    await initializeSaleItems(pageInfo);
    showSuccessModal();
  } catch (error) {
    hasError.value = error.message;
  }
});

const windowWidth = ref(window.innerWidth);

const updateWidth = () => {
  windowWidth.value = window.innerWidth;
};
</script>

<template>
  <Breadcrumbs :crumbs="[{ label: 'Sale Items' }]" />

  <div class="w-full h-full px-10 pb-10 md:px-20 md:pb-20">
    <!-- Header Section -->
    <header class="w-full flex justify-between items-center">
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
        </label>
      </div>
    </header>

    <!-- Filter Section -->
    <section class="mt-6 w-full">
      <!-- Brand Filter Controls -->
      <div class="flex justify-between items-center">
        <div class="flex gap-3 items-center">
          <!-- Brand Dropdown -->
          <div class="itbms-brand-filter relative flex-1">
            <input
              type="text"
              readonly
              class="input input-sm w-full border px-3 py-2"
              placeholder="Filter by brand(s)"
            />
            <ul
              v-if="showFilterDropdown"
              class="absolute bg-white border rounded mt-1 w-full z-10 shadow max-h-40 overflow-y-auto"
            >
              <li
                v-for="(brand, idx) in brandOptions"
                :key="idx"
                @mousedown.prevent="selectBrand(brand)"
                class="itbms-filter-item px-3 py-2 hover:bg-gray-100 cursor-pointer"
              >
                {{ brand.name }}
              </li>
            </ul>
          </div>

          <!-- Filter Actions -->
          <button
            class="itbms-brand-filter-button btn btn-success btn-sm text-white"
            @click="showFilterDropdown = !showFilterDropdown"
          >
            Filter
          </button>
          <button
            v-if="selectedBrands?.length"
            class="itbms-brand-filter-clear btn btn-error btn-sm text-white"
            @click="clearAllBrands"
          >
            Clear
          </button>
        </div>

        <!-- Sort and Page Size Controls -->
        <div class="flex gap-3 items-center">
          <div class="hidden md:block">
            <div class="flex gap-3 items-center">
              <label>Show</label>
              <select
                class="itbms-page-size select"
                @input="handlePageSizeChange"
              >
                <option
                  v-for="size in PAGE_SIZE_OPTIONS"
                  :key="size"
                  :value="size"
                  :selected="size === pageSize"
                >
                  {{ size }}
                </option>
              </select>
            </div>
          </div>

          <!-- Sort Buttons -->
          <button
            @click="handleSortChange(null)"
            class="itbms-brand-none btn"
            :class="{ 'btn-active': !['ASC', 'DESC'].includes(sortDirection) }"
            :disabled="!['ASC', 'DESC'].includes(sortDirection)"
          >
            None
          </button>
          <button
            @click="handleSortChange('ASC')"
            class="itbms-brand-asc btn"
            :class="{ 'btn-active': sortDirection === 'ASC' }"
            :disabled="sortDirection === 'ASC'"
          >
            Asc
          </button>
          <button
            @click="handleSortChange('DESC')"
            class="itbms-brand-desc btn"
            :class="{ 'btn-active': sortDirection === 'DESC' }"
            :disabled="sortDirection === 'DESC'"
          >
            Desc
          </button>
        </div>
      </div>

      <!-- Selected Brands Tags -->
      <div class="flex flex-wrap gap-2 mt-3">
        <div
          v-for="(brand, index) in selectedBrands"
          :key="index"
          class="itbms-filter-item badge badge-soft badge-primary px-3 py-1 flex items-center gap-1"
        >
          <span class="itbms-brand-filter">{{ brand.name }}</span>
          <button
            class="itbms-filter-item-clear font-bold cursor-pointer"
            @click="removeBrandAndApply(brand)"
            aria-label="Remove brand filter"
          >
            ×
          </button>
        </div>
      </div>

      <!-- Sort and Page Size Controls -->
      <div class="block" v-if="windowWidth < 768">
        <div class="flex gap-3 items-center">
          <label>Show</label>
          <select class="itbms-page-size select" @input="handlePageSizeChange">
            <option
              v-for="size in PAGE_SIZE_OPTIONS"
              :key="size"
              :value="size"
              :selected="size === pageSize"
            >
              {{ size }}
            </option>
          </select>
        </div>
      </div>
    </section>

    <!-- Sale Item Gallery -->
    <SaleItemGallery
      v-if="saleItems || hasError"
      class="mt-10"
      :sale-items="saleItems?.content || null"
      :page-info="{ ...saleItems, page: currentPage }"
      :has-error="hasError"
      @page-changed="handlePageChange"
    />
  </div>

  <!-- Success Modal -->
  <div v-if="showCreatedModal" class="modal modal-open">
    <div class="modal-box text-center">
      <h3 class="font-bold text-success">Successfully!</h3>
      <p class="py-4 itbms-message">
        The sale item has been successfully added.
      </p>
      <div class="itbms-button modal-action justify-center">
        <button @click="showCreatedModal = false" class="btn btn-primary">
          OK
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
