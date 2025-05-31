<script setup>
import Breadcrumbs from "@/components/Breadcrumbs.vue";
import FormSaleItem from "@/components/FormSaleItem.vue";
import MessageModal from "@/components/MessageModal.vue";
import router from "@/router";
import { createSaleItem } from "@/services/SaleItemService";
import { useSaleItemStore } from "@/stores/SaleItemStore";
import { useNotiStore } from "@/stores/์NotiStore";
import { ref } from "vue";

const errorTitle = ref('')
const errorMessage = ref('')
const showModal = ref(false)
const isSaving = ref(false);
const handleCreateSaleItem = async (formData) => {
  try {
    isSaving.value = true;
    if (formData) {
      const response = await createSaleItem(formData);
      if (!response.error) {
        createSuccess();
      } else {
        
        throw new Error(response.error);
      }
    }
  } catch (error) {
    errorTitle.value = 'Failed to add sale item'
    errorMessage.value = error.message
    showModal.value = true
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
  <MessageModal
    v-if="showModal"
    :handle-ok="() => (showModal = false)"
    :title="errorTitle"
    :message="errorMessage"
  />
</template>

<style scoped></style>
