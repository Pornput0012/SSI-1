<script setup>
const props = defineProps({
  saleItems: {
    type: Array,
    required : true
  }
});

console.log(props.saleItems)
</script>

<template>
  <div
    v-if="saleItems && saleItems.length > 0">
    <div v-for="(saleItem, index) in saleItems" :key="index">
      <slot :itemInList="saleItem" :index="index"></slot>
    </div>
  </div>
</template>

<style scoped></style>
