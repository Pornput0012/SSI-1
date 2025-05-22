<script setup>
import Breadcrumbs from "@/components/Breadcrumbs.vue";
import FormSaleItem from "@/components/FormSaleItem.vue";
import SaleItemGallery from "@/components/SaleItemGallery.vue";
import SaleItemGalleryModel from "@/components/SaleItemGalleryModel.vue";
import { getAllSaleItems } from "@/services/SaleItemService";
import { useSaleItemStore } from "@/stores/SaleItemStore";
import { useNotiStore } from "@/stores/์NotiStore";
import { computed, onMounted, ref } from "vue";

const { getSaleItems } = useSaleItemStore();

const saleItems = computed(() => getSaleItems());
const hasError = ref("");

onMounted(async () => {
  try {
    const response = await getAllSaleItems();
    if (response.error) {
      throw new Error(response.error);
    }
  } catch (e) {
    hasError.value = e.message;
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
