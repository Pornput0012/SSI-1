<script setup>
import { getImageByFileName } from '@/services/SaleItemService'
import { useNotiStore } from '@/stores/NotiStore'
import { ref, onMounted, computed } from 'vue'
import noImage from '@/assets/No_image_available.png'
import router from '@/router'

const imageUrl = ref(null)

const props = defineProps({
  order: { type: Object, required: true },
})

const loadImages = async () => {
  try {
    if (props.fileName?.fileName) {
      const image_url = await getImageByFileName(props.fileName.fileName)
      imageUrl.value = image_url
    } else {
      imageUrl.value = noImage
    }
  } catch (error) {
    console.error(`Failed to load image:`, error)
    imageUrl.value = noImage
  }
}

const formatDate = (dateString) => {
  if (!dateString) return '-'
  
  const date = new Date(dateString)
  
  // ตัวเลือกการแสดงผล
  const options = { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric' 
  }
  
  return date.toLocaleDateString('en-US', options)
}

onMounted(async () => {
  await loadImages();
});

const calculateTotal = computed(() => {
  return props.order.orderItems.reduce((sum, item) => {
    return sum + item.price * item.quantity
  }, 0)
})
</script>

<template>
  <div
    class="itbms-row bg-white rounded-2xl shadow-lg overflow-hidden hover:shadow-xl transition-shadow"
  >
    <!-- Order Header -->
    <div
      class="bg-gradient-to-r from-blue-50 to-indigo-50 px-6 py-4 border-b-2 border-blue-100"
    >
      <div class="flex items-center justify-between flex-wrap gap-4">
        <div class="flex items-center gap-3">
          <div
            class="bg-white rounded-full p-2 shadow-sm w-12 h-12 flex items-center justify-center text-2xl"
          >
            👤
          </div>
          <span class="itbms-nickname font-semibold text-xl text-gray-800">
            {{ order.seller?.userName || order.buyer?.userName }}
          </span>
        </div>

        <div class="grid grid-cols-2 lg:grid-cols-6 lg:gap-8 flex-1 ml-6 lg:ml-12">
          <div>
            <p class="text-xs text-gray-500 mb-1">Order No:</p>
            <p class="itbms-order-id font-semibold text-gray-800">
              {{ order.id }}
            </p>
          </div>
          <div>
            <p class="text-xs text-gray-500 mb-1">Order Date:</p>
            <p class="itbms-order-date font-semibold  text-gray-800">
              {{ formatDate(order.orderDate) }}
            </p>
          </div>
          <div>
            <p class="text-xs text-gray-500 mb-1">Payment Date:</p>
            <p class="itbms-payment-date font-semibold text-gray-800">
              {{ formatDate(order.paymentDate) }}
            </p>
          </div>
          <div>
            <p class="text-xs text-gray-500 mb-1">Payment Date:</p>
            <p class="itbms-payment-date font-semibold text-gray-800">
              {{ calculateTotal.toLocaleString() }}
            </p>
          </div>
          <div class="text-right">
            <p class="text-xs text-gray-500 mb-1">Status:</p>
            <span
              class="itbms-order-orderStatus inline-block px-4 py-1 rounded-full text-sm font-semibold"
              :class="{
                'bg-green-100 text-green-700':
                  order.orderStatus === 'COMPLETED',
                'bg-red-100 text-red-700': order.orderStatus === 'CANCELED',
                'bg-yellow-100 text-yellow-700':
                  order.orderStatus === 'PENDING',
              }"
            >
              {{ order.orderStatus }}
            </span>
          </div>
        </div>
      </div>

      <!-- Shipping Address -->
      <div
        class="mt-4 flex items-start gap-2 bg-white bg-opacity-60 rounded-lg p-3"
      >
        <span class="text-sm font-medium text-gray-600">Shipped To:</span>
        <span class="text-sm text-gray-700 itbms-shipping-address">{{
          order.shippingAddress
        }}</span>
      </div>

      <div
        class="mt-4 flex items-start gap-2 bg-white bg-opacity-60 rounded-lg p-3"
      >
        <span class="text-sm font-medium text-gray-600">Note:</span>
        <span class="text-sm text-gray-700 itbms-order-note">{{
          order.orderNote || '-'
        }}</span>
      </div>
    </div>

    <!-- Order Items -->
    <div class="p-6 space-y-4">
      <div
        v-for="(item, index) in order.orderItems"
        :key="index"
        class="itbms-item-row flex items-center gap-6 p-4 bg-gray-50 rounded-xl hover:bg-gray-100 transition-colors"
      >
        <div
          class="w-20 h-20 bg-white rounded-lg flex items-center justify-center shadow-sm text-4xl"
        >
          <img
            v-if="imageUrl"
            :src="imageUrl"
            alt="Product image"
            class="w-full h-full object-center"
            :class="'object-cover'"
          />
          <img
            v-else
            :src="noImage"
            alt="Product image"
            class="w-full h-full object-center"
            :class="'object-contain'"
          />
        </div>
        <div class="flex-1">
          <h4 class="font-semibold text-gray-800 mb-1 itbms-item-description">
            {{ item.description }}
          </h4>
          <p class="text-sm text-gray-500">
            Qty <span class="itbms-item-quantity">{{ item.quantity }}</span>
          </p>
        </div>
        <div class="text-right">
          <p class="text-lg font-bold text-blue-600">
            ฿<span class="itbms-item-total-price">{{
              (item.price * item.quantity).toLocaleString()
            }}</span>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
