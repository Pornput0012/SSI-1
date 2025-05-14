<script setup>
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
      class="m-10 overflow-auto rounded-lg shadow"
    >
    </div>
    
    <!-- show sale item list -->
    <div
      v-else-if="saleItems && saleItems.length > 0"
      class="m-10 overflow-auto rounded-lg shadow"
    >
      <table class="w-full">
        <thead class="bg-gray-100 border-b-2 border-gray-200">
          <tr class="text-sm font-semibold text-center">
            <th class="p-3 tracking-wide">Id</th>
            <th class="p-3 tracking-wide">Brand</th>
            <th class="p-3 tracking-wide">Model</th>
            <th class="p-3 tracking-wide">Ram</th>
            <th class="p-3 tracking-wide">Storage</th>
            <th class="p-3 tracking-wide">Color</th>
            <th class="p-3 tracking-wide">Screen Size</th>
            <th class="p-3 tracking-wide">Price</th>
            <th class="p-3 tracking-wide">Quantity</th>
            <th class="p-3 tracking-wide">Action</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="itemInList in saleItems"
            :key="itemInList.id"
            class="itbms-row bg-white text-center text-gray-700"
          >
            <td
              class="itbms-id p-3 font-bold text-blue-500 hover:underline cursor-pointer whitespace-nowrap"
            >
              {{ itemInList.id || '-' }}
            </td>
            <td class="itbms-brand p-3 text-sm whitespace-nowrap">
              {{ itemInList.brand || '-' }}
            </td>
            <td class="itbms-model p-3 text-sm text-left whitespace-nowrap">
              {{ itemInList.model || '-' }}
            </td>
            <td class="itbms-ramGb p-3 text-sm whitespace-nowrap">
              {{ itemInList.ramGb || '-' }}
            </td>
            <td class="itbms-storageGb p-3 text-sm whitespace-nowrap">
              {{ itemInList.storageGb || '-' }}
            </td>
            <td class="itbms-color p-3 text-sm whitespace-nowrap">
              {{ itemInList.color || '-' }}
            </td>
            <td class="itbms-screenSizeInch p-3 text-sm whitespace-nowrap">
              {{ itemInList.screenSizeInch || '-' }}
            </td>
            <td class="itbms-price p-3 text-sm whitespace-nowrap">
              {{ itemInList.price.toLocaleString() || '-' }}
            </td>
            <td class="itbms-quantity p-3 text-sm whitespace-nowrap">
              {{ itemInList.quantity || '-' }}
            </td>
            <td class="p-3 whitespace-nowrap">
              <div class="flex gap-2 justify-center">
                <button
                  class="itbms-edit-button btn btn-sm btn-outline btn-info"
                >
                  E
                </button>
                <button
                  class="itbms-delete-button btn btn-sm btn-outline btn-error"
                >
                  D
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- no sale item -->
    <div
      v-if="saleItems && saleItems.length === 0"
      class="w-full h-full p-12 flex justify-center items-center rounded-lg bg-purple-200 border-1 border-purple-400"
    >
      <p class="text-purple-700 font-medium">no sale item</p>
    </div>
  </div>
</template>

<style scoped></style>
