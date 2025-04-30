<script setup>
import { ref, onMounted } from 'vue'
import NavBar from './components/NavBar.vue'
const products = ref([])
const error = ref(null)

const fetchProducts = async () => {
  try {
    const response = await fetch(`${import.meta.env.VITE_BACKEND_URL}/api/products`)
    if (!response.ok) {
      throw new Error('Network response was not ok')
    }
    const data = await response.json()
    products.value = data
  } catch (err) {
    error.value = 'เกิดข้อผิดพลาด: ' + err.message
  }
}

onMounted(() => {
  fetchProducts()
})
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
