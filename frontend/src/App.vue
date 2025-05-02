<script setup>
import NavBar from "./components/NavBar.vue";
</script>

<template>
  <NavBar />
  <!-- <div class="p-4">
    <h1 class="text-2xl font-bold mb-4">รายการสินค้า</h1>
    <div v-if="error" class="text-red-500">{{ error }}</div>
    <ul v-else>
      <li
        v-for="product in products"
        :key="product.productId"
        class="mb-2 p-3 border rounded shadow"
      >
        <h2 class="text-lg font-semibold">{{ product.name }}</h2>
        <p>{{ product.description }}</p>
        <p>สต็อก: {{ product.stock }} | ราคา: ฿{{ product.price }}</p>
        <p class="text-sm text-gray-500">ยี่ห้อ: {{ product.brand }}</p>
      </li>
    </ul>
  </div> -->
  <RouterView />
</template>

<style scoped></style>
