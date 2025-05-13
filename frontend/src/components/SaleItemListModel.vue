<script setup>
const props = defineProps({
  id: {
    type: Number,
    required: true,
  },
  brand: {
    type: String,
    required: true,
  },
  model: {
    type: String,
    required: true,
  },
  ramGb: Number,
  storageGb: Number,
  color: String,
  screenSizeInch: Number,
  price: {
    type: Number,
    required: true,
  },
  quantity: Number
});
</script>

<template>
  <tr class="itbms-row border-2 text-center text-gray-600 text-sm md:text-base">
    <td class="itbms-id">{{ id || '-' }}</td>
    <td class="itbms-brand">{{ brand || '-' }}</td>
    <td class="itbms-model">{{ model || '-' }}</td>
    <td class="itbms-ramGb">{{ ramGb || '-' }}</td>
    <td class="itbms-storageGb">{{ storageGb || '-' }}</td>
    <td class="itbms-color">{{ color || '-' }}</td>
    <td class="itbms-screenSizeInch">{{ screenSizeInch || '-' }}</td>
    <td class="itbms-price">{{ price.toLocaleString() || '-' }}</td>
    <td class="itbms-quantity">{{ quantity || '-' }}</td>
    <td class="p-3">
      <div class="flex gap-2 justify-center">
        <button class="itbms-edit-button btn btn-sm btn-outline btn-info">E</button>
        <button class="itbms-delete-button btn btn-sm btn-outline btn-error">D</button>
      </div>
    </td>
  </tr>
</template>

<style scoped></style>