<script setup>
import Breadcrumbs from "@/components/Breadcrumbs.vue";
import FormSaleItem from "@/components/FormSaleItem.vue";
import router from "@/router";
import { createSaleItem, getAllSaleItems } from "@/services/SaleItemService";
import { useSaleItemStore } from "@/stores/SaleItemStore";
import { onMounted, ref } from "vue";

const isSaving = ref(false);
const showModal = ref(false);
const saleItemStore = useSaleItemStore();
const handleCreateSaleItem = async (formData) => {
  try {
    isSaving.value = true;
    console.log(formData);
    if (formData) {
      const response = await createSaleItem(formData);
      if (response && !response.error) {
        saleItemStore.createSaleItem(response);
        showModal.value = true;
      } else {
        throw new Error("Failed to add sale item");
      }
    }
  } catch (error) {
    isSaving.value = false;
    console.log(error.message);
  }
};

const createSuccess = () => {
  showModal.value = false;
  isSaving.value = false;
  router.push({ name: "sale-items" });
};

onMounted(async () => {
  if (saleItemStore.getSaleItems() === null) {
    try {
      const response = await getAllSaleItems();
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
  <Breadcrumbs :crumbs="[{ label: 'New Sale Item', to: '' }]" />

  <div>
    <FormSaleItem
      @on-submit="handleCreateSaleItem"
      :disableSaveBtn="isSaving"
    />
  </div>
  <div v-if="showModal" class="modal modal-open">
    <div class="modal-box text-center">
      <h3
        class="font-bold text-[14px] md:text-[16px] lg:text-[18px] text-success"
      >
        Successfully!
      </h3>
      <p class="itbms-message py-4 text-[12px] md:text-[14px] lg:text-[16px]">
        The sale item has been successfully added
      </p>
      <div class="itbms-button modal-action justify-center">
        <button @click="createSuccess" class="btn btn-primary bg-purple-700">
          OK
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
