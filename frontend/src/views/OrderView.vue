<script setup>
import { ref } from 'vue';

const activeTab = ref('completed');

const orders = [
  {
    id: '123456',
    buyerId : '6262',
    Seller:{id:'123',username: 'Somsuan'},
    orderDate: 'October 1, 2025',
    paymentDate: 'October 1, 2025',
    total: '62,700',
    orderStatus: 'Completed',
    shippingAddress: 'Somchai Jaidee, 123/45 Moo 6, T. Bangna, A. Bangna, Bangkok 10260',
    note: 'hi',
    orderItems: [
      {
        no: '123',
        saleItemId : '123',

        description: 'Apple iPhone 14 Pro Max (512GB, Space Black)',
        quantity: 1,
        price: '42,900',
        image: '📱'
      },
      {
        no: '123',
        saleItemId : '123',
        description: 'Apple iPhone 13 mini (128GB, Green)',
        quantity: 1,
        price: '19,800',
        image: '📱'
      }
    ]
  }
];
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 p-8">
    <div class="max-w-7xl mx-auto">
      <!-- Header -->
      <h1 class="text-4xl font-bold text-gray-800 mb-8">Your Orders</h1>

      <!-- Tabs -->
      <div class="flex gap-1 mb-6 border-b-2 border-gray-200">
        <button
          @click="activeTab = 'completed'"
          :class="[
            'px-6 py-3 font-medium transition-all',
            activeTab === 'completed'
              ? 'text-blue-600 border-b-4 border-blue-600 -mb-0.5'
              : 'text-gray-500 hover:text-gray-700'
          ]"
        >
          Completed
        </button>
        <button
          @click="activeTab = 'canceled'"
          :class="[
            'px-6 py-3 font-medium transition-all',
            activeTab === 'canceled'
              ? 'text-blue-600 border-b-4 border-blue-600 -mb-0.5'
              : 'text-gray-500 hover:text-gray-700'
          ]"
        >
          Canceled
        </button>
      </div>

      <!-- Orders List -->
      <div class="space-y-6">
        <div
          v-for="order in orders"
          :key="order.id"
          class="itbms-row bg-white rounded-2xl shadow-lg overflow-hidden hover:shadow-xl transition-shadow"
        >
          <!-- Order Header -->
          <div class="bg-gradient-to-r from-blue-50 to-indigo-50 px-6 py-4 border-b-2 border-blue-100">
            <div class="flex items-center justify-between flex-wrap gap-4">
              <div class="flex items-center gap-3">
                <div class="bg-white rounded-full p-2 shadow-sm w-12 h-12 flex items-center justify-center text-2xl">
                  👤
                </div>
                <span class="itbms-nickname font-semibold text-xl text-gray-800">{{ order.Seller.username }}</span>
              </div>
              
              <div class="grid grid-cols-2 lg:grid-cols-4 gap-8 flex-1 ml-0 lg:ml-12">
                <div>
                  <p class="text-xs text-gray-500 mb-1">Order No:</p>
                  <p class="itbms-order-id font-semibold text-gray-800">{{ order.id }}</p>
                </div>
                <div>
                  <p class="text-xs text-gray-500 mb-1">Order Date:</p>
                  <p class="itbms-order-date font-semibold text-gray-800">{{ order.orderDate }}</p>
                </div>
                <div>
                  <p class="text-xs text-gray-500 mb-1">Payment Date:</p>
                  <p class="itbms-payment-date font-semibold text-gray-800">{{ order.paymentDate }}</p>
                </div>
                <div class="text-right">
                  <p class="text-xs text-gray-500 mb-1">Status:</p>
                  <span class="itbms-order-orderStatus inline-block px-4 py-1 bg-green-100 text-green-700 rounded-full text-sm font-semibold">
                    {{ order.orderStatus }}
                  </span>
                </div>
              </div>
            </div>

            <!-- Shipping Address -->
            <div class="mt-4 flex items-start gap-2 bg-white bg-opacity-60 rounded-lg p-3">
              <span class="text-sm font-medium text-gray-600">Shipped To:</span>
              <span class="text-sm text-gray-700 itbms-shipping-address">{{ order.shippingAddress }}</span>
            </div>

            <div v-if="order.note" class="mt-2 flex items-start gap-2 rounded-lg p-3">
              <span class="text-sm font-medium text-gray-600">Note:</span>
              <span class="text-sm text-gray-700 itbms-order-note">{{ order.note }}</span>
            </div>
          </div>

          <!-- Order Items -->
          <div class="p-6 space-y-4">
            <div
              v-for="(item, index) in order.orderItems"
              :key="index"
              class="itbms-item-row flex items-center gap-6 p-4 bg-gray-50 rounded-xl hover:bg-gray-100 transition-colors"
            >
              <div class="w-20 h-20 bg-white rounded-lg flex items-center justify-center shadow-sm text-4xl">
                {{ item.image }}
              </div>
              <div class="flex-1">
                <h4 class="font-semibold text-gray-800 mb-1 itbms-item-description">{{ item.description }}</h4>
                <p class="text-sm text-gray-500">Qty <span class="itbms-item-quantity">{{ item.quantity }}</span></p>
              </div>
              <div class="text-right">
                <p class="text-lg font-bold text-blue-600">฿<span class="itbms-item-total-price"></span>{{ item.price * item.quantity}}</p>
              </div>
            </div>

            <!-- Total -->
            <div class="flex justify-end items-center gap-4 pt-4 border-t-2 border-gray-200">
              <span class="text-lg font-semibold text-gray-700">Total:</span>
              <span class="text-xl font-bold text-blue-600">฿<span class="itbms-total-order-price">{{ order.total }}</span></span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Additional custom styles if needed */
</style>