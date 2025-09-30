<script setup>
import { Icon } from '@iconify/vue';
const props = defineProps({
  qty: {
    type: Number,
    default: 0,
  },
});
</script>

<template>
  <div class="relative">
    <div class="rounded-full text-xs px-1 bg-red-500 text-white font-semibold text-center absolute left-3 bottom-4 itbms-cart-quantity" >{{ qty }}</div>
    <Icon
      icon="material-symbols:shopping-cart-rounded"
      height="24"
      width="24"
    />
  </div>
</template>

<style></style>
