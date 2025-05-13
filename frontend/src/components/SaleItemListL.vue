<script setup>
import ContainerList from './ContainerList.vue'
import SaleItemListModel from './SaleItemListModel.vue'

const props = defineProps({
  saleItems: {
    type: [Array, null],
    required: true,
  },
  hasError: String,
})
</script>

<template>
  <div>
    <!-- error message -->
    <div
      v-if="!saleItems && hasError !== '' && hasError"
      class="w-full h-full p-12 flex justify-center items-center rounded-lg bg-red-100 border-1 border-red-300"
    >
      <p class="text-red-500 font-extralight">{{ hasError }}</p>
    </div>

    <!-- loader -->
    <div
      v-else-if="!saleItems"
    >
      <div v-for="i in 5" class="flex w-full flex-col gap-4">
        <div class="skeleton h-32 w-full"></div>
        <div class="skeleton h-4 w-28"></div>
        <div class="skeleton h-4 w-full"></div>
        <div class="skeleton h-4 w-full"></div>
      </div>
    </div>

    <!-- show sale item list -->
    <div v-else-if="saleItems && saleItems.length > 0">
      <table class="table w-full border-2 border-black">
        <thead class="bg-gray-400 text-black text-xl text-center">
          <tr>
            <th>Id</th>
            <th>Brand</th>
            <th>Model</th>
            <th>Ram</th>
            <th>Storage</th>
            <th>Color</th>
            <th>Screen Size</th>
            <th>Price</th>
            <th>Quantity</th>
          </tr>
        </thead>
        <tbody>
          <ContainerList
            :items="saleItems"
          >
            <template v-slot="{ itemInList }">
              <SaleItemListModel
                :id="itemInList.id"
                :brand="itemInList.brandName"
                :model="itemInList.model"
                :ram-gb="itemInList.ramGb"
                :storage-gb="itemInList.storageGb"
                :color="itemInList.color"
                :screen-size-inch="itemInList.screenSizeInch"
                :price="itemInList.price"
                :quantity="itemInList.quantity"
              />
            </template>
          </ContainerList>
        </tbody>
      </table>
    </div>

    <!-- no sale item list -->
    <div
      v-if="saleItems && saleItems.length === 0"
      class="w-full h-full p-12 flex justify-center items-center rounded-lg bg-purple-200 border-1 border-purple-400"
    >
      <p class="text-purple-700 font-medium">no sale item</p>
    </div>
  </div>
</template>

<style scoped></style>
