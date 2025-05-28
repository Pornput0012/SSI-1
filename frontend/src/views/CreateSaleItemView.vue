<script setup>
import Breadcrumbs from "@/components/Breadcrumbs.vue";
import FormSaleItem from "@/components/FormSaleItem.vue";
import router from "@/router";
import { createSaleItem } from "@/services/SaleItemService";
import { useSaleItemStore } from "@/stores/SaleItemStore";
import { useNotiStore } from "@/stores/์NotiStore";
import { ref } from "vue";

const isSaving = ref(false);
const handleCreateSaleItem = async (formData) => {
  try {
    isSaving.value = true;
    if (formData) {
      const response = await createSaleItem(formData);
      if (response && !response.error) {
        createSuccess();
      } else {
        
        throw new Error("Failed to add sale item");
      }
    }
  } catch (error) {
    isSaving.value = false;
  }
};

const notiStore = useNotiStore();
const createSuccess = () => {
  notiStore.createSuccess = true;
  router.back();
};
</script>

<template>
  <Breadcrumbs
    :crumbs="[{ label: 'New Sale Item', class: 'itbms-back-button' }]"
  />

  <div>
    <FormSaleItem
      @on-submit="handleCreateSaleItem"
      :disableSaveBtn="isSaving"
    />
  </div>
</template>

<style scoped></style>
