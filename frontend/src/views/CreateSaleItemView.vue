<script setup>
import Breadcrumbs from "@/components/Breadcrumbs.vue";
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
  <Breadcrumbs :crumbs="[{ label: 'New Sale Item', to: '' }]" />

  <div>
    <FormSaleItem @on-submit="createSaleItem" />
  </div>
</template>

<style scoped></style>
