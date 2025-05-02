<script setup>
import { onMounted } from "vue";
import ContainerList from "./ContainerList.vue";
import SaleItemModel from "./SaleItemModel.vue";

const props = defineProps({
  saleItems: {
    type: [Array,null],
    required: true,
  },
  hasError: String,
});

console.log(props.saleItems);
console.log(props.hasError);

</script>

<template>
  <div>
    <ContainerList
      v-if="saleItems && saleItems.length > 0 && hasError === ''"
      :items="saleItems"
      class="w-full h-full justify-center items-center grid grid-cols-1 gap-4 sm:grid-cols-2 md:grid-cols-3 md:gap-6 lg:grid-cols-4 lg:gap-8 xl:grid-cols-5 xl:gap-10"
    >
      <template v-slot="{ itemInList }">
        <SaleItemModel
          :brand="itemInList.brandName"
          :model="itemInList.model"
          :price="itemInList.price"
          :ram-gb="itemInList.ramGb"
          :storage-gb="itemInList.storageGb"
          :color="itemInList.color"
          :id="itemInList.id"
        />
      </template>
    </ContainerList>
    <div
      v-if="saleItems && saleItems.length === 0 && hasError === ''"
      class="w-full h-full p-12 flex justify-center items-center rounded-lg bg-purple-200 border-1 border-purple-400"
    >
      <p class="text-purple-700 font-medium">no sale item</p>
    </div>
    <div v-else-if="!saleItems && hasError === ''" class="w-full h-full justify-center items-center grid grid-cols-1 gap-6 sm:grid-cols-2 md:grid-cols-3 md:gap-6 lg:grid-cols-4 lg:gap-8 xl:grid-cols-5 xl:gap-10">
      <div v-for="i in 5" class="flex w-full flex-col gap-4">
        <div class="skeleton h-32 w-full"></div>
        <div class="skeleton h-4 w-28"></div>
        <div class="skeleton h-4 w-full"></div>
        <div class="skeleton h-4 w-full"></div>
      </div>
    </div>
    <div v-else-if="!saleItems && hasError !== '' && hasError" class="w-full h-full p-12 flex justify-center items-center rounded-lg bg-red-100 border-1 border-red-300">
        <p class="text-red-500 font-extralight">{{ hasError }}</p>
    </div>
  </div>
</template>

<style scoped></style>
