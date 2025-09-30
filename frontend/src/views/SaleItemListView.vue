<script setup>
import MessageModal from "@/components/MessageModal.vue";
import {
  getAllSaleItems,
  getSaleItemsBySeller,
} from "@/services/SaleItemService";
import { useSaleItemStore } from "@/stores/SaleItemStore";
import { useNotiStore } from "@/stores/NotiStore";
import { computed, onMounted, ref } from "vue";
import { useFilterStore } from "@/stores/FilterStore";
import { useRoute } from "vue-router";
import SaleItemList from "@/components/SaleItemList.vue";
import { useUserStore } from "@/stores/UserStore";

const route = useRoute();
const userStore = useUserStore();
const hasError = ref("");
const STORAGE_KEYS = {
  PAGE_INFO: "pageInfo",
};

const filterStore = useFilterStore();

const { setCurrentPage } = filterStore;

const saleItemStore = useSaleItemStore();

const { getSaleItemsPage } = saleItemStore;
const currentPage = computed(() => filterStore.currentPage);

const saleItems = computed(() => getSaleItemsPage());

const saveToLocalStorage = () => {
  sessionStorage.setItem(
    STORAGE_KEYS.PAGE_INFO,
    JSON.stringify({
      page: currentPage.value,
    })
  );
};

const getFromLocalStorage = () => {
  const pageInfo = JSON.parse(sessionStorage.getItem(STORAGE_KEYS.PAGE_INFO));
  return {
    pageInfo,
  };
};

const setDefaultStoreValues = (pageInfo) => {
  getFromLocalStorage();
  setCurrentPage(pageInfo?.page ?? 0);
};

const handlePageChange = async (pageIndex) => {
  setCurrentPage(pageIndex);
  await saleItemsBySeller();
};

const saleItemsBySeller = async (pageInfo) => {
  try {
    const userId = userStore.getUserInfo();
    const response = await getSaleItemsBySeller(userId.id, {
      page: pageInfo?.page ?? currentPage.value,
      size: 10,
    });
    saveToLocalStorage();
    if (response.error) {
      throw new Error(response?.error);
    }
  } catch (error) {
    hasError.value = error.message;
  }
};

onMounted(async () => {
  try {
    const { pageInfo } = getFromLocalStorage();
    setDefaultStoreValues(pageInfo);
    await saleItemsBySeller(pageInfo);
  } catch (error) {
    hasError.value = error.message;
  }
});

const notiStore = useNotiStore();
const showCreatedModal = ref(false);
onMounted(() => {
  // if (notiStore.loginUserSuccess) { 
  //   showCreatedModal.value = true;
  //   notiStore.loginUserSuccess = false;
  // }
});
</script>

<template>
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
          :to="{ name: 'brands' }"
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
    <SaleItemList
      v-if="saleItems || hasError"
      :sale-items="saleItems?.content || null"
      :has-error="hasError"
      :page-info="{ ...saleItems, page: currentPage }"
      @page-changed="handlePageChange"
    />

    <MessageModal
      v-if="showCreatedModal"
      :handle-ok="() => (showCreatedModal = false)"
      :title="'Successfully!'"
      :message="'The sale item has been successfully added.'"
      :type-modal="'text-success'"
    />
  </div>
</template>

<style scoped></style>
