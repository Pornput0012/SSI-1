<script setup>
const props = defineProps({
  items: {
    type: Array,
    required : true
  }
});
</script>

<template>
  <div
    v-if="items && items.length > 0">
    <div v-for="(item, index) in items" :key="index">
      <slot :itemInList="item" :index="index"></slot>
    </div>
  </div>
</template>

<style scoped></style>
