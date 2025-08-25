<script setup>
import Breadcrumbs from "@/components/Breadcrumbs.vue";
import SaleItemGallery from "@/components/SaleItemGallery.vue";
import { getAllBrands } from "@/services/BrandService";
import { getAllSaleItems_v2 } from "@/services/SaleItemService";
import { useFilterStore } from "@/stores/FilterStore";
import { useSaleItemStore } from "@/stores/SaleItemStore";
import { useNotiStore } from "@/stores/NotiStore";
import { computed, onMounted, ref } from "vue";

const PAGE_SIZE_OPTIONS = [5, 10, 20];
const STORAGE_KEYS = {
  SELECTED_BRANDS: "selectedBrands",
  SELECTED_STORAGES: "selectedStorages",
  SELECTED_PRICES: "selectedPrices",
  PAGE_INFO: "pageInfo",
  SEARCH_KEYWORD: "searchKeyword",
};

const filterStore = useFilterStore();
const saleItemStore = useSaleItemStore();
const notiStore = useNotiStore();

const {
  getFilterList,
  clearAllFilters,

  setSelectBrand,
  removeBrand,
  setSelectedBrand,

  setCurrentPage,
  setPageSize,
  resetCurrentPage,

  setSortBrand,
  setSortField,
  setSortDirection,

  setSelectStorage,
  setSelectedStorage,
  removeStorage,

  setSelectPrice,
  setSelectedPrice,
  removePrice,
  setSearchKeyword,
} = filterStore;

const { getSaleItemsPage } = saleItemStore;

const sortField = computed(() => filterStore.sortField);
const sortDirection = computed(() => filterStore.sortDirection);
const currentPage = computed(() => filterStore.currentPage);
const pageSize = computed(() => filterStore.pageSize);
const selectedBrands = computed(() => filterStore.selectedBrands);
const selectedStorages = computed(() => filterStore.selectedStorages);
const selectedPrices = computed(() => filterStore.selectedPrices);
const searchKeyword = computed(() => filterStore.searchKeyword);

const filterBrands = computed(() => getFilterList().brands);
const filterStorages = computed(() => getFilterList().storages);
const filterPrices = computed(() => getFilterList().prices);

const saleItems = computed(() => getSaleItemsPage());

const searchInput = ref("");

const hasError = ref("");
const showFilterDropdown = ref(false);
const showCreatedModal = ref(false);
const brandOptions = ref([]);
const storageOptions = ref([32, 64, 128, 256, 512, 1024, -1]);
const priceRangeOptions = ref([
  { label: "0 – 5,000", min: 0, max: 5000 },
  { label: "5,001 – 10,000", min: 5001, max: 10000 },
  { label: "10,001 – 20,000", min: 10001, max: 20000 },
  { label: "20,001 – 30,000", min: 20001, max: 30000 },
  { label: "30,001 – 40,000", min: 30001, max: 40000 },
  { label: "40,001 – 50,000", min: 40001, max: 50000 },
]);

const saveToLocalStorage = () => {
  sessionStorage.setItem(
    STORAGE_KEYS.SELECTED_BRANDS,
    JSON.stringify(selectedBrands.value)
  );
  sessionStorage.setItem(
    STORAGE_KEYS.SEARCH_KEYWORD,
    JSON.stringify(searchKeyword.value)
  );
  sessionStorage.setItem(
    STORAGE_KEYS.SELECTED_STORAGES,
    JSON.stringify(selectedStorages.value)
  );
  sessionStorage.setItem(
    STORAGE_KEYS.SELECTED_PRICES,
    JSON.stringify(selectedPrices.value)
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
  const selectedStorages = JSON.parse(
    sessionStorage.getItem(STORAGE_KEYS.SELECTED_STORAGES)
  );
  const selectedPrices = JSON.parse(
    sessionStorage.getItem(STORAGE_KEYS.SELECTED_PRICES)
  );
  const searchKeyword = JSON.parse(
    sessionStorage.getItem(STORAGE_KEYS.SEARCH_KEYWORD)
  );
  const pageInfo = JSON.parse(sessionStorage.getItem(STORAGE_KEYS.PAGE_INFO));

  return {
    selectedBrands,
    selectedStorages,
    selectedPrices,
    pageInfo,
    searchKeyword,
  };
};

const setDefaultStoreValues = (pageInfo) => {
  const { selectedBrands, selectedStorages, selectedPrices, searchKeyword } =
    getFromLocalStorage();
  setSearchKeyword(searchKeyword ?? "");
  searchInput.value = searchKeyword;
  setSelectedBrand(Array.isArray(selectedBrands) ? selectedBrands : []);
  setSelectedStorage(Array.isArray(selectedStorages) ? selectedStorages : []);
  setSelectedPrice(selectedPrices ?? { min: null, max: null });
  setCurrentPage(pageInfo?.page ?? 0);
  setPageSize(pageInfo?.size ?? 10);
  setSortField(pageInfo?.sortField ?? null);
  setSortDirection(pageInfo?.sortDirection ?? null);
};

const applyFilter = async () => {
  try {
    const response = await getAllSaleItems_v2({
      filterBrands: filterBrands.value,
      filterStorages: filterStorages.value,
      filterPriceLower: filterPrices.value.min,
      filterPriceUpper: filterPrices.value.max,
      page: currentPage.value,
      size: pageSize.value,
      sortField: sortField.value,
      sortDirection: sortDirection.value,
      searchKeyWord: searchKeyword.value,
    });

    saveToLocalStorage();

    if (response.error) {
      throw new Error(response?.error);
    }
  } catch (error) {
    hasError.value = error.message;
  }
};

const handleSearchKeyword = async () => {
  setSearchKeyword(searchInput.value);
  handlePageChange(0);
  await applyFilter();
};

const selectBrand = async (brand) => {
  setSelectBrand(brand);
  showFilterDropdown.value = false;
  resetCurrentPage();
  await applyFilter(true);
};

const removeBrandAndApply = async (brand) => {
  removeBrand(brand);
  resetCurrentPage();
  await applyFilter();
};

const selectStorage = async (storage) => {
  setSelectStorage(storage);
  showFilterDropdown.value = false;
  resetCurrentPage();
  await applyFilter(true);
};

const removeStorageAndApply = async (storage) => {
  removeStorage(storage);
  resetCurrentPage();
  await applyFilter();
};

const selectPriceRange = async (range) => {
  setSelectPrice(range.min, range.max);
  showFilterDropdown.value = false;
  resetCurrentPage();
  await applyFilter(true);
};

const customMin = ref(null);
const customMax = ref(null);

const applyCustomPrice = async () => {
  // validate input
  if (customMin.value === null && customMax.value === null) return;

  setSelectPrice(
    customMin.value !== null ? customMin.value : null,
    customMax.value !== null ? customMax.value : null
  );
  showFilterDropdown.value = false;
  resetCurrentPage();
  await applyFilter(true);

  // เคลียร์ input หลัง apply
  customMin.value = null;
  customMax.value = null;
};

const removePriceAndApply = async () => {
  removePrice();
  resetCurrentPage();
  await applyFilter();
};

const clearAll = async () => {
  clearAllFilters();
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
      filterBrands: getFilterList().brands,
      filterStorages: getFilterList().storages,
      filterPriceLower: getFilterList().prices.min,
      filterPriceUpper: getFilterList().prices.max,
      page: pageInfo?.page ?? 0,
      size: pageInfo?.size ?? 10,
      sortField: pageInfo?.sortField ?? null,
      sortDirection: pageInfo?.sortDirection ?? null,
      searchKeyWord: searchKeyword.value,
    });

    if (response.error) {
      throw new Error(response?.error);
    }
  } catch (error) {
    hasError.value = error.message;
  }
};

const showSuccessModal = () => {
  if (notiStore.createSuccess || notiStore.createUserSuccess) {
    showCreatedModal.value = true;
  }
};

function closeModal() {
  showCreatedModal.value = false;
  notiStore.createSuccess = false;
  notiStore.createUserSuccess = false;
}

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

const clearSearchKeyword = async () => {
  searchInput.value = "";
  setSearchKeyword("");
  sessionStorage.setItem(STORAGE_KEYS.SEARCH_KEYWORD, JSON.stringify(""));
  await applyFilter()
};

const windowWidth = ref(window.innerWidth);

const updateWidth = () => {
  windowWidth.value = window.innerWidth;
};
</script>

<template>
  <Breadcrumbs :crumbs="[{ label: 'Sale Items' }]" />

  <div class="w-full h-full px-10 pb-10 md:px-20 md:pb-20">
    <!-- Header Section -->
    <header
      class="w-full flex flex-col lg:flex-row justify-between items-center"
    >
      <h1 class="font-bold text-2xl mb-4 sm:mb-6 md:mb-10 lg:mb-0">Products</h1>
      <div class="flex flex-col md:flex-row gap-4 items-center">
        <router-link
          :to="{ name: 'create-sale-item' }"
          class="itbms-sale-item-add btn btn-primary text-xs md:text-base"
        >
          Add Sale Item
        </router-link>
        <div class="flex gap-2">
          <label class="w-fit h-8 input input-xs flex">
            <input
              @keypress.enter="handleSearchKeyword"
              type="search"
              v-model="searchInput"
              class="grow text-lg"
              placeholder="Search..."
            />
            <button
              @click="handleSearchKeyword"
              class="itbms-search-text cursor-pointer"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="24"
                height="24"
                viewBox="0 0 24 24"
              >
                <g fill="none" fill-rule="evenodd">
                  <path
                    d="m12.593 23.258l-.011.002l-.071.035l-.02.004l-.014-.004l-.071-.035q-.016-.005-.024.005l-.004.01l-.017.428l.005.02l.01.013l.104.074l.015.004l.012-.004l.104-.074l.012-.016l.004-.017l-.017-.427q-.004-.016-.017-.018m.265-.113l-.013.002l-.185.093l-.01.01l-.003.011l.018.43l.005.012l.008.007l.201.093q.019.005.029-.008l.004-.014l-.034-.614q-.005-.018-.02-.022m-.715.002a.02.02 0 0 0-.027.006l-.006.014l-.034.614q.001.018.017.024l.015-.002l.201-.093l.01-.008l.004-.011l.017-.43l-.003-.012l-.01-.01z"
                  />
                  <path
                    fill="currentColor"
                    d="M10.5 2a8.5 8.5 0 1 0 5.262 15.176l3.652 3.652a1 1 0 0 0 1.414-1.414l-3.652-3.652A8.5 8.5 0 0 0 10.5 2M4 10.5a6.5 6.5 0 1 1 13 0a6.5 6.5 0 0 1-13 0"
                  />
                </g>
              </svg>
            </button>
          </label>
          <button
            class="itbms-brand-filter-button btn btn-error btn-sm text-white"
            @click="clearSearchKeyword"
          >
            clear
          </button>
        </div>
      </div>
    </header>

    <!-- Filter Section -->
    <section class="mt-6 w-full">
      <!-- Brand Filter Controls -->
      <div
        class="flex flex-col lg:flex-row justify-between items-center flex-wrap md:flex-nowrap gap-4"
      >
        <div class="flex flex-col md:flex-row gap-3 items-center">
          <!-- Brand Dropdown -->
          <div
            class="itbms-brand-filter relative flex-1 md:max-w-28 xl:max-w-none"
          >
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
                class="itbms-brand-item px-3 py-2 hover:bg-gray-100 cursor-pointer"
              >
                {{ brand.name }}
              </li>
            </ul>
          </div>
          <!-- Price Dropdown -->
          <div
            class="itbms-price-filter relative flex-1 xl:flex-2 md:max-w-28 xl:max-w-none"
          >
            <input
              type="number"
              readonly
              class="input input-sm w-full border px-3 py-2"
              placeholder="Price range"
            />
            <ul
              v-if="showFilterDropdown"
              class="absolute bg-white border rounded mt-1 w-full z-10 shadow max-h-40 overflow-y-auto"
            >
              <li class="font-semibold px-2 py-1">Preset ranges</li>
              <li
                v-for="(price, idx) in priceRangeOptions"
                :key="idx"
                @mousedown.prevent="selectPriceRange(price)"
                class="itbms-price-item px-3 py-2 hover:bg-gray-100 cursor-pointer"
              >
                {{ price.label }} Baht
              </li>
              <li class="font-semibold px-2 py-1">Custom</li>
              <div class="flex gap-2 px-2 pb-2">
                <input
                  class="itbms-price-item-min input input-sm w-20 border px-2 py-1"
                  type="number"
                  min="0"
                  placeholder="Min Price"
                  v-model.number="customMin"
                />
                <input
                  class="itbms-price-item-max input input-sm w-20 border px-2 py-1"
                  type="number"
                  min="0"
                  placeholder="Max Price"
                  v-model.number="customMax"
                />
                <button
                  class="btn btn-sm btn-primary"
                  @mousedown.prevent="applyCustomPrice"
                >
                  Apply
                </button>
                <button
                  class="btn btn-sm"
                  @mousedown.prevent="removePriceAndApply"
                >
                  Clear
                </button>
              </div>
            </ul>
          </div>
          <!-- Storage Size Dropdown -->
          <div
            class="itbms-storage-size-filter relative flex-1 md:max-w-28 xl:max-w-none"
          >
            <input
              type="number"
              readonly
              class="input input-sm w-full border px-3 py-2"
              placeholder="Storage size(s)"
            />
            <ul
              v-if="showFilterDropdown"
              class="absolute bg-white border rounded mt-1 w-full z-10 shadow max-h-40 overflow-y-auto"
            >
              <li
                v-for="(storage, idx) in storageOptions"
                :key="idx"
                @mousedown.prevent="selectStorage(storage)"
                class="itbms-storage-size-item px-3 py-2 hover:bg-gray-100 cursor-pointer"
              >
                {{
                  typeof storage === "number" && storage != -1
                    ? storage + " GB"
                    : "Not specified"
                }}
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
            v-if="
              selectedBrands?.length ||
              selectedStorages?.length ||
              selectedPrices?.min !== null ||
              selectedPrices?.max !== null
            "
            class="itbms-brand-filter-clear btn btn-error btn-sm text-white"
            @click="clearAll"
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
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="24"
              height="24"
              viewBox="0 0 24 24"
            >
              <path
                fill="none"
                stroke="currentColor"
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-miterlimit="10"
                stroke-width="1.5"
                d="M4 12h12M4 8h16M4 16h8"
              />
            </svg>
          </button>
          <button
            @click="handleSortChange('ASC')"
            class="itbms-brand-asc btn"
            :class="{ 'btn-active': sortDirection === 'ASC' }"
            :disabled="sortDirection === 'ASC'"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="16"
              height="16"
              viewBox="0 0 16 16"
            >
              <path
                fill="currentColor"
                d="M3 1.5a.5.5 0 0 0-1 0v11.8L.85 12.15a.5.5 0 0 0-.707.707l2 2a.5.5 0 0 0 .707 0l2-2a.5.5 0 0 0-.707-.707l-1.15 1.15V1.5zM7.5 12a.5.5 0 0 0 0 1h3a.5.5 0 0 0 0-1zm0-3a.5.5 0 0 0 0 1h4a.5.5 0 0 0 0-1zm0-3a.5.5 0 0 0 0 1h6a.5.5 0 0 0 0-1zm8-2a.5.5 0 0 0 0-1h-8a.5.5 0 0 0 0 1z"
              />
            </svg>
          </button>
          <button
            @click="handleSortChange('DESC')"
            class="itbms-brand-desc btn"
            :class="{ 'btn-active': sortDirection === 'DESC' }"
            :disabled="sortDirection === 'DESC'"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="24"
              height="24"
              viewBox="0 0 24 24"
            >
              <path
                fill="currentColor"
                d="M4.5 22a.5.5 0 0 0 .5-.5V3.7l3.15 3.15a.5.5 0 0 0 .707-.707l-4-4a.5.5 0 0 0-.707 0l-4 4a.5.5 0 0 0 .707.707l3.15-3.15v17.8a.5.5 0 0 0 .5.5zM11 6.5a.5.5 0 0 1 .5-.5h12a.5.5 0 0 1 0 1h-12a.5.5 0 0 1-.5-.5m.5 3.5a.5.5 0 0 0 0 1h9a.5.5 0 0 0 0-1zm0 4a.5.5 0 0 0 0 1h6a.5.5 0 0 0 0-1zm-.5 4.5a.5.5 0 0 1 .5-.5h4a.5.5 0 0 1 0 1h-4a.5.5 0 0 1-.5-.5"
              />
            </svg>
          </button>
        </div>
      </div>

      <!-- Selected Brands Tags -->
      <div class="flex flex-wrap gap-2 mt-3">
        <div
          v-for="(brand, index) in selectedBrands"
          :key="index"
          class="itbms-brand-item badge badge-soft badge-primary px-3 py-1 flex items-center gap-1"
        >
          <span class="itbms-brand-item">{{ brand.name }}</span>
          <button
            class="itbms-brand-item-clear font-bold cursor-pointer"
            @click="removeBrandAndApply(brand)"
            aria-label="Remove brand filter"
          >
            ×
          </button>
        </div>
      </div>

      <!-- Selected Prices Tags -->
      <div
        class="flex flex-wrap gap-2 mt-3"
        v-if="selectedPrices.min !== null || selectedPrices.max !== null"
      >
        <div
          class="itbms-price-item badge badge-soft badge-primary px-3 py-1 flex items-center gap-1"
        >
          <span>
            {{ selectedPrices.min ?? 0 }} – {{ selectedPrices.max ?? "∞" }} Baht
          </span>
          <button
            class="itbms-price-clear font-bold cursor-pointer"
            @click="removePriceAndApply"
            aria-label="Remove price filter"
          >
            ×
          </button>
        </div>
      </div>

      <!-- Selected Storages Tags -->
      <div class="flex flex-wrap gap-2 mt-3">
        <div
          v-for="(storage, index) in selectedStorages"
          :key="index"
          class="itbms-storage-size-item badge badge-soft badge-primary px-3 py-1 flex items-center gap-1"
        >
          <span class="itbms-storage-size-filter">{{
            storage === -1 ? "Not specified" : storage + " GB"
          }}</span>
          <button
            class="itbms-storage-size-item-clear font-bold cursor-pointer"
            @click="removeStorageAndApply(storage)"
            aria-label="Remove storage size filter"
          >
            ×
          </button>
        </div>
      </div>

      <!-- Sort and Page Size Controls -->
      <div class="block" v-if="windowWidth < 768">
        <div class="flex gap-3 items-center mt-4">
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
        {{
          notiStore.createUserSuccess
            ? "The user account has been successfully registered."
            : "The sale item has been successfully added."
        }}
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
