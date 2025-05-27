<script setup>
import { useRouter, useRoute } from "vue-router";
import { useSaleItemStore } from "@/stores/SaleItemStore";
import { onMounted, ref } from "vue";
import {
  getSaleItemById,
  updateSaleItemById,
} from "@/services/SaleItemService";
import Breadcrumbs from "@/components/Breadcrumbs.vue";
import FormSaleItem from "@/components/FormSaleItem.vue";
import { useNotiStore } from "@/stores/์NotiStore";
import MessageModal from "@/components/MessageModal.vue";

const isSaving = ref(false);
const route = useRoute();
const router = useRouter();

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

    const response = await getSaleItemById(saleItemId);

    if (response && !response.error) {
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
    console.log(formData);
    if (formData) {
      const response = await updateSaleItemById(
        selectSaleItem.value.id,
        formData
      );
      console.log(response);
      console.log(response.error);
      if (response && !response.error) {
        saleItemStore.updateSaleItemById(selectSaleItem.value.id, response);
        updateSuccess();
      } else {
        throw new Error("Failed to update sale item");
      }
    }
  } catch (error) {
    isSaving.value = false;
    console.log(error.message);
  }
};

const notiStore = useNotiStore();
const updateSuccess = () => {
  notiStore.updateSuccess = true;
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

</template>

<style scoped></style>
