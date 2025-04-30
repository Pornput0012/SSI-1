<script setup>
import { useSaleItemStore } from "@/stores/SaleItemStore";
import { useBrandStore } from "@/stores/BrandStore";

const props = defineProps({
  brand: {
    type: String,
    require: true,
  },
  model: {
    type: String,
    require: true,
  },
  ramGb: Number,
  storageGb: Number,
  price: {
    type: Number,
    require: true,
  },
  color: String,
});

console.log(props.brand);
</script>

<template>
  <div class="card w-64 h-96 bg-base-100 shadow-md rounded-xl flex flex-col">
    <div class="w-full h-52 p-3 mx-auto">
      <img
        src="../assets/logo.svg"
        alt="Product image"
        class="w-full h-full object-cover object-center rounded"
      />
    </div>
    <div class="h-px bg-gray-200 shadow-sm my-2"></div>

    <div class="flex flex-col w-full h-full">
      <div class="px-4 pt-2 pัb-0">
        <p class="Itbms-brand text-gray-500 text-sm font-medium">{{ brand }}</p>
        <p class="Itbms-model text-lg font-semibold">{{ model }}</p>
  
        <div class="flex items-center text-lg font-light">
          <p v-if="ramGb" class="Itbms-ramGb">{{ ramGb }}</p>
          <p v-if="storageGb" class="Itbms-storageGb">/{{ storageGb }}GB</p>
        </div>
  
        <p v-if="color" class="Itbms-color text-sm text-gray-600">
          color : {{ color }}
        </p>
      </div>
      <div class="p-4 pt-2 mt-auto" v-if="price">
        <p class="Itbms-price text-lg font-semibold text-purple-700">
          {{ price }}฿
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
