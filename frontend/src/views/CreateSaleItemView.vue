<script setup>
import Breadcrumbs from "@/components/Breadcrumbs.vue";
import FormSaleItem from "@/components/FormSaleItem.vue";
import { ref } from "vue";

const isCreating = ref(false);
const showModal = ref(false);
const createSaleItem = (formData) => {
  try {
    isCreating.value = true;
    console.log(formData);
    showModal.value = true;
  } catch (error) {
    isCreating.value = false;
    showModal.value = false;
    console.log(error.message);
  }
};
</script>

<template>
  <Breadcrumbs :crumbs="[{ label: 'New Sale Item', to: '' }]" />

  <div>
    <FormSaleItem @on-submit="createSaleItem" :disableSaveBtn="isCreating" />
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
        <button
          @click="showModal = false"
          class="btn btn-primary bg-purple-700"
        >
          OK
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
