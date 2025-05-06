<script setup>
import { getAllBrands } from "@/services/BrandService";
import { useBrandStore } from "@/stores/BrandStore";
import { computed, onMounted } from "vue";
import Breadcrumbs from "./Breadcrumbs.vue";

const { getBrands } = useBrandStore();

const brands = computed(() => getBrands());

onMounted(async () => {
  if (brands.value === null) {
    try {
      const response = await getAllBrands();
      if (response.error) {
        throw new Error(response.error);
      }
    } catch (e) {
      console.log(e);
    }
  }
});
</script>

<template>
  <Breadcrumbs :crumbs="[{ label: 'New Sale Item', to: '/sale-items/add' }]" />
  <div class="mx-16 mb-14">
    <div class="flex flex-col lg:flex-row">
      <div class="w-2/4 h-screen ">
        <div class="border-2 rounded-2xl w-4/5 mx-auto">
          <img src="/pictures/iPhone15Pro.png" alt="" />
        </div>
        <div class="flex gap-4 mt-10 justify-center">
          <img
            v-for="i in 4"
            class="h-full w-24 border rounded-2xl"
            :key="i"
            src="/pictures/iPhone15Pro.png"
            alt=""
          />
        </div>
      </div>
      <div class="w-2/4 flex flex-col gap-4 p-14 ">
        <!-- brand -->
        <div class="flex items-center gap-10">
          <label for="brand">Brand</label>
          <select id="brand" class="itbms-brand select">
            <option disabled selected>Select Brand</option>
            <option v-for="(brand, i) in brands" :key="i">
              {{ brand.name }}
            </option>
          </select>
        </div>
        <!-- model -->
        <div class="flex items-center gap-10">
          <label for="model">Model</label>
          <input type="text" id="model" class="itbms-model input" />
        </div>
        <!-- price -->
        <div class="flex items-center gap-10">
          <label for="price">Price(Bath)</label>
          <input type="number" id="price" class="itbms-price input" />
        </div>
        <!-- Description -->
        <div class="flex flex-col gap-2">
          <label for="description" class="">Description</label>
          <textarea class="itbms-description textarea w-full"></textarea>
        </div>
        <!-- Ram -->
        <div class="flex items-center gap-10">
          <label for="ramGb">Ram (GB)</label>
          <input type="text" id="ramGb" class="itbms-ramGb input" />
        </div>
        <!-- screenSizeInch -->
        <div class="flex items-center gap-10">
          <label for="screenSizeInch">Screen Size (Inch)</label>
          <input
            type="text"
            id="screenSizeInch"
            class="itbms-screenSizeInch input"
          />
        </div>
        <!-- storageGb -->
        <div class="flex items-center gap-10">
          <label for="storageGb">Storage (GB)</label>
          <input type="text" id="ramGB" class="itbms-storageGb input" />
        </div>
        <!-- color -->
        <div class="flex items-center gap-10">
          <label for="color">Color</label>
          <input type="text" id="color" class="itbms-color input" />
        </div>
        <!-- quantity -->
        <div class="flex items-center gap-10">
          <label for="quantity">Quantity</label>
          <input type="number" id="quantity" class="itbms-quantity input" />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
