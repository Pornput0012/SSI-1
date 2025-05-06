<script setup>
import { getAllBrands } from "@/services/BrandService";
import { useBrandStore } from "@/stores/BrandStore";
import { computed, onMounted, ref, watchEffect } from "vue";
import Breadcrumbs from "./Breadcrumbs.vue";

const emit = defineEmits(["onSubmit"]);

const { getBrands } = useBrandStore();

const brands = computed(() => getBrands());

const formData = ref({
  brand: {
    id: 0,
    name: "",
  },
  model: "",
  price: null,
  description: "",
  ramGb: null,
  screenSizeInch: null,
  storageGb: null,
  color: "",
  quantity: null,
});
const errorTitle = ref("");
const errorMessage = ref("");
const showModal = ref(false);

onMounted(async () => {
  if (brands.value === null) {
    try {
      const response = await getAllBrands();
      if (response.error) {
        throw new Error(response.error);
      }
    } catch (e) {
      errorTitle.value = "Something Wrong!";
      errorMessage.value = e.message;
      showModal.value = true;
    }
  }
});

const handleSubmit = () => {
  const requireField = ["model", "price", "description", "quantity"];
  
  for (const field of requireField) {
    if (!formData.value[field]) {
      errorTitle.value = "Missing Required Field";
      errorMessage.value = `Please fill out the required field: ${field}`;
      showModal.value = true;
      return;
    }
  }
  emit("onSubmit", formData.value);
};
</script>

<template>
  <Breadcrumbs :crumbs="[{ label: 'New Sale Item', to: '/sale-items/add' }]" />
  <div class="mt-10 lg:mt-0 lg:mx-16 mb-14">
    <div class="flex flex-col lg:flex-row">
      <div class="w-full lg:w-2/4 mx-auto overflow-auto mb-10 lg:mb-10">
        <div class="border-2 rounded-2xl w-4/5 md:w-2/3 mx-auto">
          <img src="/pictures/iPhone15Pro.png" alt="" />
        </div>
        <div
          class="flex gap-4 mt-10 w-full justify-center items-center overflow-auto"
        >
          <img
            v-for="i in 4"
            class="h-full w-14 lg:w-18 border rounded-2xl"
            :key="i"
            src="/pictures/iPhone15Pro.png"
            alt=""
          />
        </div>
      </div>
      <div
        class="flex flex-col items-center lg:items-start justify-center lg:justify-start xl:w-2/4"
      >
        <div
          class="grid grid-cols-[auto_1fr] md:grid-cols-[auto_260px] xl:grid-cols-[auto_320px] items-center gap-x- md:gap-x-6 gap-y-4 px-4"
        >
          <label class="text-sm md:text-base" for="brand">Brand</label>
          <select
            id="brand"
            class="itbms-brand select"
            v-model="formData.brand"
          >
            <option disabled selected>Select Brand</option>
            <option v-for="(brand, i) in brands" :value="brand" :key="i">
              {{ brand.name }}
            </option>
          </select>

          <label class="text-sm md:text-base" for="model">Model</label>
          <input
            type="text"
            id="model"
            class="itbms-model input"
            v-model.trim="formData.model"
          />

          <label class="text-sm md:text-base" for="price">Price(Bath)</label>
          <input
            type="number"
            id="price"
            class="itbms-price input"
            v-model="formData.price"
          />

          <label class="text-sm md:text-base" for="description"
            >Description</label
          >
          <textarea
            id="description"
            class="itbms-description textarea resize-y"
            v-model.trim="formData.description"
          ></textarea>

          <label class="text-sm md:text-base" for="ramGb">Ram (GB)</label>
          <input
            type="number"
            id="ramGb"
            class="itbms-ramGb input"
            min="1"
            v-model="formData.ramGb"
          />

          <label class="text-sm md:text-base max-w-[110px]" for="screenSizeInch"
            >Screen Size (Inch)</label
          >
          <input
            type="number"
            id="screenSizeInch"
            class="itbms-screenSizeInch input"
            step="0.01"
            min="1"
            v-model="formData.screenSizeInch"
          />

          <label class="text-sm md:text-base" for="storageGb"
            >Storage (GB)</label
          >
          <input
            type="number"
            id="storageGb"
            class="itbms-storageGb input"
            min="1"
            v-model="formData.storageGb"
          />

          <label class="text-sm md:text-base" for="color">Color</label>
          <input
            type="text"
            id="color"
            class="itbms-color input"
            v-model.trim="formData.color"
          />

          <label class="text-sm md:text-base" for="quantity">Quantity</label>
          <input
            type="number"
            id="quantity"
            class="itbms-quantity input"
            min="1"
            v-model="formData.quantity"
          />
          <div
            class="flex items- jus w-full gap-4 col-span-2 justify-center mt-4 lg:justify-end"
          >
            <button @click="handleSubmit" class="btn btn-primary">Save</button>
            <button class="btn">Cancle</button>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div v-if="showModal" class="modal modal-open">
    <div class="modal-box text-center">
      <h3
        class="font-bold text-[14px] md:text-[16px] lg:text-[18px] text-red-500"
      >
        {{ errorTitle }}
      </h3>
      <p class="itbms-message py-4 text-[12px] md:text-[14px] lg:text-[16px]">
        {{ errorMessage }}
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
