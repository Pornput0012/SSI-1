<script setup>
import Breadcrumbs from "@/components/Breadcrumbs.vue";
import FormSaleItem from "@/components/FormSaleItem.vue";
import router from "@/router";
import { createSaleItem } from "@/services/SaleItemService";
import { useSaleItemStore } from "@/stores/SaleItemStore";
import { useNotiStore } from "@/stores/์NotiStore";
import { onMounted, ref } from "vue";

const isSaving = ref(false);
const saleItemStore = useSaleItemStore();
const handleCreateSaleItem = async (formData) => {
  try {
    isSaving.value = true;
    console.log(formData);
    if (formData) {
      const response = await createSaleItem(formData);
      if (response && !response.error) {
        saleItemStore.createSaleItem(response);
        createSuccess()
      } else {
        throw new Error("Failed to add sale item");
      }
    }
  } catch (error) {
    isSaving.value = false;
    console.log(error.message);
  }
};

const notiStore = useNotiStore()
const createSuccess = () => {
  notiStore.createSuccess = true
  router.push({ name: "sale-items" })
}
</script>

<template>
  <Breadcrumbs :crumbs="[{ label: 'New Sale Item', class: 'itbms-back-button' }]" />

  <div>
    <FormSaleItem
      @on-submit="handleCreateSaleItem"
      :disableSaveBtn="isSaving"
    />
  </div>
 
</template>

<style scoped></style>
