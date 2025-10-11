<script setup>
import OrderDetail from "@/components/OrderDetail.vue";
import { useRoute } from "vue-router";

const route = useRoute();
const orderId = route.params.orderId;
</script>

<template>
  <OrderDetail :id="orderId" :your-order="true" />
</template>

<style scoped></style>
