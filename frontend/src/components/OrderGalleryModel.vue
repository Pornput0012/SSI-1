<script setup>
import { computed } from 'vue'
import noImage from '@/assets/No_image_available.png'
import router from '@/router'
import { sellerViewOrder } from '@/services/OrderService'
import { useUserStore } from '@/stores/UserStore'
import { useSaleOrderStore } from '@/stores/saleOrderStore'
import { useCartStore } from '@/stores/CartStore'
const userStore = useUserStore()
const userId = computed(() => userStore.getUserId())
const saleOrderQuantity = useSaleOrderStore()
const cartStore = useCartStore()
const props = defineProps({
  order: { type: Object, required: true },
  newOrder: Boolean,
  saleOrders: Boolean,
})

const formatDate = (dateString) => {
  if (!dateString) return '-'

  const date = new Date(dateString)

  // ตัวเลือกการแสดงผล
  const options = {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  }

  return date.toLocaleDateString('en-US', options)
}

const calculateTotal = computed(() => {
  return props.order.orderItems.reduce((sum, item) => {
    return sum + item.price * item.quantity
  }, 0)
})

const handleNewOrder = async () => {
  try {
    if (props.newOrder) {
      console.log('active handle NewOrder')
      const res = await sellerViewOrder(userId.value, props.order.id)

      // Check for 401 error
      if (res.error && res.error.status === 401) {
        userStore.clearUserInfo()
        userStore.setAccessToken(null)
        cartStore.setCart(null)
        saleOrderQuantity.setSaleOrderQuanatity(0)
        sessionStorage.clear()
        localStorage.clear()
        router.push({ name: 'signin' })
        return
      }

      if (res.error) {
        throw new Error(res.error)
      }
      saleOrderQuantity.setSaleOrderQuanatity(
        saleOrderQuantity.getSaleOrderQuanatity() - 1
      )

      router.push({
        name: 'sale-orders-detail',
        params: { orderId: props.order.id },
      })
    }
  } catch (error) {
    console.log(error)
  }
}
</script>

<template>
  <div class="itbms-row">
    <div
      class="itbms-row bg-white rounded-2xl shadow-lg overflow-hidden hover:shadow-xl transition-shadow"
    >
      <!-- Order Header -->
      <div
        class="bg-gradient-to-r px-4 md:px-6 py-4 border-b-2 border-blue-100"
        :class="
          order.orderStatus === 'COMPLETED'
            ? 'from-green-300 to-blue-200'
            : 'from-red-400 to-orange-200'
        "
      >
        <!-- ✅ Seller/Buyer Info + Status (Mobile) -->
        <div class="flex items-center justify-between mb-4 md:mb-0">
          <div class="flex items-center gap-3 itbms-nickname">
            <div
              class="bg-white rounded-full p-2 shadow-sm w-10 h-10 md:w-12 md:h-12 flex items-center justify-center text-xl md:text-2xl"
            >
              👤
            </div>
            <span
              class="itbms-nickname font-semibold text-lg md:text-xl text-gray-800"
            >
              {{ order.seller?.userName || order.buyer?.userName }}
            </span>
          </div>

          <!-- ✅ Status Badge (Mobile) -->
          <div class="md:hidden itbms-order-status">
            <span
              class="itbms-order-status inline-block px-3 py-1 rounded-full text-xs font-semibold"
              :class="{
                'bg-green-100 text-green-700':
                  order.orderStatus === 'COMPLETED',
                'bg-red-100 text-red-700': order.orderStatus === 'CANCELLED',
                'bg-yellow-100 text-yellow-700':
                  order.orderStatus === 'PENDING',
              }"
            >
              {{ order.orderStatus }}
            </span>
          </div>
        </div>

        <!-- ✅ Order Info Grid (Responsive) -->
        <div
          class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-3 md:gap-4 lg:gap-6 mt-4 md:ml-0"
        >
          <!-- Order No -->
          <div class="itbms-order-id">
            <p class="text-xs text-gray-600 mb-1">Order No:</p>
            <p
              class="itbms-order-id font-semibold text-sm md:text-base text-gray-800"
            >
              #{{ order.id }}
            </p>
          </div>

          <!-- Order Date -->
          <div class="itbms-order-date">
            <p class="text-xs text-gray-600 mb-1">Order Date:</p>
            <p
              class="itbms-order-date font-semibold text-sm md:text-base text-gray-800"
            >
              {{ formatDate(order.orderDate) }}
            </p>
          </div>

          <!-- Payment Date -->
          <div class="itbms-payment-date">
            <p class="text-xs text-gray-600 mb-1">Payment Date:</p>
            <p
              class="itbms-payment-date font-semibold text-sm md:text-base text-gray-800"
            >
              {{ formatDate(order.paymentDate) }}
            </p>
          </div>

          <!-- Total -->
          <div class="itbms-total-order-price">
            <p class="text-xs text-gray-600 mb-1">Total:</p>
            <p
              class="itbms-total-order-price font-semibold text-sm md:text-base text-gray-800"
            >
              ฿{{ calculateTotal.toLocaleString() }}
            </p>
          </div>

          <!-- Status (Desktop) -->
          <div class="hidden md:block itbms-order-status">
            <p class="text-xs text-gray-600 mb-1">Status:</p>
            <span
              class="itbms-order-status inline-block px-3 md:px-4 py-1 rounded-full text-xs md:text-sm font-semibold"
              :class="{
                'bg-green-100 text-green-700':
                  order.orderStatus === 'COMPLETED',
                'bg-red-100 text-red-700': order.orderStatus === 'CANCELLED',
                'bg-yellow-100 text-yellow-700':
                  order.orderStatus === 'PENDING',
              }"
            >
              {{ order.orderStatus }}
            </span>
          </div>

          <!-- View Button -->
          <div class="col-span-2 md:col-span-1 itbms-view-button">
            <RouterLink
              :to="{
                name: saleOrders ? 'sale-orders-detail' : 'order-detail',
                params: { orderId: props.order.id },
              }"
              class="block w-full"
            >
              <button
                class="w-full bg-blue-500 hover:bg-blue-600 text-white px-4 md:px-6 py-2 rounded-lg font-medium transition-colors shadow-sm cursor-pointer text-sm md:text-base"
                @click="handleNewOrder"
              >
                View Details
              </button>
            </RouterLink>
          </div>
        </div>

        <!-- ✅ Shipping Address (Responsive) -->
        <div
          class="mt-4 flex flex-col md:flex-row md:items-start gap-2 bg-white bg-opacity-60 rounded-lg p-3 itbms-shipping-address"
        >
          <span class="text-xs md:text-sm font-medium text-gray-600 shrink-0">
            Shipped To:
          </span>
          <span
            class="text-xs md:text-sm text-gray-700 itbms-shipping-address break-words"
          >
            {{ order.shippingAddress }}
          </span>
        </div>

        <!-- ✅ Order Note (Responsive) -->
        <div
          v-if="order.orderNote"
          class="mt-3 flex flex-col md:flex-row md:items-start gap-2 bg-white bg-opacity-40 rounded-lg p-3 itbms-order-note"
        >
          <span class="text-xs md:text-sm font-medium text-gray-600 shrink-0">
            Note:
          </span>
          <span
            class="text-xs md:text-sm text-gray-700 itbms-order-note break-words"
          >
            {{ order.orderNote }}
          </span>
        </div>
      </div>

      <!-- ✅ Order Items (Responsive) -->
      <div class="p-4 md:p-6 space-y-3 md:space-y-4">
        <div
          v-for="(item, index) in order.orderItems"
          :key="index"
          class="itbms-item-row flex flex-col sm:flex-row sm:items-center gap-4 p-3 md:p-4 bg-gray-50 rounded-xl hover:bg-gray-100 transition-colors"
        >
          <!-- Product Image -->
          <div
            class="w-16 h-16 sm:w-20 sm:h-20 bg-white rounded-lg flex items-center justify-center shadow-sm shrink-0 mx-auto sm:mx-0"
          >
            <img
              :src="noImage"
              alt="Product image"
              class="w-full h-full object-contain"
            />
          </div>

          <!-- Product Info -->
          <div class="flex-1 text-center sm:text-left itbms-item-description">
            <h4
              class="font-semibold text-sm md:text-base text-gray-800 mb-1 itbms-item-description"
            >
              {{ item.description }}
            </h4>
            <p class="text-xs md:text-sm text-gray-500 itbms-item-quantity">
              Qty:
              <span class="itbms-item-quantity font-semibold">{{
                item.quantity
              }}</span>
            </p>
          </div>

          <!-- Price -->
          <div class="text-center sm:text-right itbms shrink-0">
            <p class="text-base md:text-lg font-bold text-blue-600">
              ฿<span class="itbms-item-total-price">{{
                (item.price * item.quantity).toLocaleString()
              }}</span>
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
