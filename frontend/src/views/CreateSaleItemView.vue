<script setup>
import FormSaleItem from "@/components/FormSaleItem.vue";

const createSaleItem = (formData) => {
  try {
    console.log(formData);
  } catch (error) {
    console.log(error.message);
  }
};
</script>

<template>
  <div>
    <FormSaleItem @on-submit="createSaleItem" />
  </div>
</template>

<style scoped></style>
