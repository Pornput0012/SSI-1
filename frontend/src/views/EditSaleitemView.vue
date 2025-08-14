<script setup>
import { useRouter, useRoute } from "vue-router";
import { useSaleItemStore } from "@/stores/SaleItemStore";
import { onMounted, ref } from "vue";
import {
  getSaleItemById,
  getSaleItemById_v2,
  updateSaleItemById,
} from "@/services/SaleItemService";
import Breadcrumbs from "@/components/Breadcrumbs.vue";
import FormSaleItem from "@/components/FormSaleItem.vue";
import { useNotiStore } from "@/stores/์NotiStore";
import MessageModal from "@/components/MessageModal.vue";

const isSaving = ref(false);
const route = useRoute();
const router = useRouter();

const errorTitle = ref('')
const showModal = ref(false)

const saleItemStore = useSaleItemStore();

const selectSaleItem = ref(null);
const saleItemId = route.params.id;

const isNotFound = ref(false);
const errorMessage = ref("");

const fetchSaleItemById = async () => {
  try {
    if (!saleItemId) {
      throw new Error("Invalid ID");
    }

    const response = await getSaleItemById_v2(saleItemId);

    if (!response.error) {
      selectSaleItem.value = response;
    } else {
      throw new Error("The requested sale item does not exist.");
    }
  } catch (error) {
    errorMessage.value = error.message;
    isNotFound.value = true;
  }
};

onMounted(fetchSaleItemById);

const editSaleItem = async (formData) => {
  try {
    isSaving.value = true;
    if (formData) {
      const response = await updateSaleItemById(
        selectSaleItem.value.id,
        formData
      );
      if (!response.error) {
        // saleItemStore.updateSaleItemById(selectSaleItem.value.id, response);
        updateSuccess();
      } else {
        throw new Error(response.error);
      }
    }
  } catch (error) {
    errorTitle.value = 'Failed to update sale item'
    errorMessage.value = error.message
    showModal.value = true
    isSaving.value = false;
  }
};

const notiStore = useNotiStore();
const updateSuccess = () => {
  console.log("call function");
  
  notiStore.updateSuccess = true;
  console.log(notiStore.updateSuccess);
  router.push({
    name: "sale-item-detail",
    params: { id: selectSaleItem.value.id },
  });
};
</script>

<template>
  <Breadcrumbs
    :crumbs="[
      { label: selectSaleItem?.model, name: 'sale-item-detail', params: saleItemId, class: 'itbms-back-button' },
    ]"
  />
  <div>
    <FormSaleItem
      v-if="selectSaleItem"
      :saleItemDetail="selectSaleItem"
      @on-submit="editSaleItem"
      :disable-save-btn="isSaving"
    />
  </div>

  <MessageModal
    v-if="isNotFound"
    :handle-ok="() => router.push({ name: 'sale-items' })"
    :title="'Product Not Found'"
    :message="errorMessage"
  />
  <MessageModal
    v-if="showModal"
    :handle-ok="() => (showModal = false)"
    :title="errorTitle"
    :message="errorMessage"
  />

</template>

<style scoped></style>
