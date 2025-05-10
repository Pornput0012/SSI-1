<script setup>
import { useRouter, useRoute } from "vue-router";
import { useSaleItemStore } from "@/stores/SaleItemStore";
import { onMounted, ref } from "vue";
import { getSaleItemById, updateSaleItemById } from "@/services/SaleItemService";
import Breadcrumbs from "@/components/Breadcrumbs.vue";
import FormSaleItem from "@/components/FormSaleItem.vue";
import { useNotiStore } from "@/stores/์NotiStore";

const isSaving = ref(false);
// const showModal = ref(false);
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
      const response = await updateSaleItemById(selectSaleItem.value.id,formData);
      console.log(response)
      console.log(response.error)
      if (response && !response.error) {
        saleItemStore.updateSaleItemById(selectSaleItem.value.id,response)
        updateSuccess()
      } else {
        throw new Error("Failed to update sale item");
      }
    }
  } catch (error) {
    isSaving.value = false;
    console.log(error.message);
  }
};

// const updateSuccess = () => {
//     showModal.value = false
//     isSaving.value = false
//     router.push({ name: 'sale-items' })
// }

const notiStore = useNotiStore()

const updateSuccess = () => {
  notiStore.updateSuccess = true
  router.push({
    name: "sale-item-detail",
    params: { id: selectSaleItem.value.id }
  })
}
</script>

<template>
  <Breadcrumbs
    :crumbs="[
      { label: selectSaleItem?.model, to: `/sale-items/${saleItemId}` },
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
  <div v-if="isNotFound" class="modal modal-open">
    <div class="modal-box text-center">
      <h3
        class="font-bold text-[14px] md:text-[16px] lg:text-[18px] text-red-500"
      >
        Product Not Found
      </h3>
      <p class="itbms-message py-4 text-[12px] md:text-[14px] lg:text-[16px]">
        {{ errorMessage }}
      </p>
      <div class="itbms-button modal-action justify-center">
        <button
          @click="router.push({ name: 'sale-items' })"
          class="btn btn-primary bg-purple-700"
        >
          OK
        </button>
      </div>
    </div>
  </div>
  <!-- <div v-if="showModal" class="modal modal-open">
    <div class="modal-box text-center">
      <h3
        class="font-bold text-[14px] md:text-[16px] lg:text-[18px] text-success"
      >
        Successfully!
      </h3>
      <p class="itbms-message py-4 text-[12px] md:text-[14px] lg:text-[16px]">
        The sale item has been updated.
      </p>
      <div class="itbms-button modal-action justify-center">
        <button
          @click="updateSuccess"
          class="btn btn-primary bg-purple-700"
        >
          OK
        </button>
      </div>
    </div>
  </div> -->
</template>

<style scoped></style>
