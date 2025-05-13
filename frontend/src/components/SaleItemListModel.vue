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
  <tr class="itbms-row text-center text-sm border-b hover:bg-gray-100">
    <td class="itbms-id px-2 py-1">{{ id || '-' }}</td>
    <td class="itbms-brand px-2 py-1">{{ brand || '-' }}</td>
    <td class="itbms-model px-2 py-1">{{ model || '-' }}</td>
    <td class="itbms-ramGb px-2 py-1">{{ ramGb || '-' }}</td>
    <td class="itbms-storageGb px-2 py-1">{{ storageGb || '-' }}</td>
    <td class="itbms-color px-2 py-1">{{ color || '-' }}</td>
    <td class="itbms-screenSizeInch px-2 py-1">{{ screenSizeInch || '-' }}</td>
    <td class="itbms-price px-2 py-1">{{ price.toLocaleString() || '-' }}</td>
    <td class="itbms-quantity px-2 py-1">{{ quantity || '-' }}</td>
    <td class="p-3">
      <div class="flex gap-2 justify-center">
        <button class="itbms-edit-button btn btn-sm btn-outline btn-info">E</button>
        <button class="itbms-delete-button btn btn-sm btn-outline btn-error">D</button>
      </div>
    </td>
  </tr>
</template>

<style scoped></style>