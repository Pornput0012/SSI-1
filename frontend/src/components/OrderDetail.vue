<script setup>
import { getOrderById, getOrdersBuyer } from "@/services/OrderService";
import { getImageByFileName } from "@/services/SaleItemService";
import { useUserStore } from "@/stores/UserStore";
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import MessageModal from "@/components/MessageModal.vue";
import noImage from "@/assets/No_image_available.png";
import Breadcrumbs from "@/components/Breadcrumbs.vue";

const props = defineProps({
  yourOrder: {
    type: Boolean,
  },
  id: {
    type: String,
    required: true,
  },
});

const selectedOrder = ref(null);
const userStore = useUserStore();
const route = useRoute();
const router = useRouter();
const orderId = Number(props.id);
const isNotFound = ref(false);
const errorMessage = ref("");
const userId = computed(() => userStore.getUserId());

const fetchOrderById = async () => {
  try {
    if (!orderId) {
      throw new Error("Invalid ID");
    }
    const response = await getOrderById(orderId);
    if (!response.error) {
      selectedOrder.value = response;
      console.log(response);

      const isBuyer = selectedOrder.value.buyerId === userId.value;
      const isSeller = selectedOrder.value.seller?.id === userId.value;

      if (!isBuyer && !isSeller) {
        throw new Error("You do not have permission to view this order");
      }
    } else {
      throw new Error("The requested order does not exist.");
    }
  } catch (error) {
    errorMessage.value = error.message;
    isNotFound.value = true;
  }
};

const calculateTotal = computed(() => {
  if (!selectedOrder.value?.orderItems) return 0;
  return selectedOrder.value.orderItems.reduce((sum, item) => {
    return sum + item.price * item.quantity;
  }, 0);
});

const formatDate = (dateString) => {
  if (!dateString) return "-";

  const date = new Date(dateString);

  // ตัวเลือกการแสดงผล
  const options = {
    year: "numeric",
    month: "long",
    day: "numeric",
  };

  return date.toLocaleDateString("en-US", options);
};

onMounted(fetchOrderById);
</script>

<template>
  <Breadcrumbs
    :crumbs="[
      {
        label: 'Order Details',
        class: 'itbms-your-orders',
        name: 'order-detail',
      },
    ]"
    :home="
      yourOrder
        ? {
            label: 'Your Order',
            name: 'order',
          }
        : {
            label: 'Sale Orders',
            name: 'sale-orders',
          }
    "
  />
  <div class="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 p-8">
    <div v-if="selectedOrder" class="max-w-4xl mx-auto">
      <div class="bg-white rounded-2xl shadow-lg overflow-hidden itbms-row">
        <!-- Header -->
        <div
          class="bg-gradient-to-r from-blue-50 to-indigo-50 px-6 py-6 border-b-2 border-blue-100"
        >
          <div class="grid grid-cols-2 gap-6">
            <div class="itbms-order-id">
              <p class="text-sm text-gray-500 mb-1">Order No:</p>
              <p class="font-semibold text-lg itbms-order-id">
                {{ selectedOrder.id }}
              </p>
            </div>
            <div class="itbms-nickname">
              <p class="text-sm text-gray-500 mb-1">
                {{ yourOrder ? "Seller" : "Buyer" }}:
              </p>
              <p class="font-semibold text-lg itbms-nickname">
                {{
                  yourOrder
                    ? selectedOrder.seller?.userName
                    : selectedOrder.buyer?.userName
                }}
              </p>
            </div>
            <div class="itbms-order-date">
              <p class="text-sm text-gray-500 mb-1">Order Date:</p>
              <p class="font-semibold itbms-order-date">
                {{ formatDate(selectedOrder.orderDate) }}
              </p>
            </div>
            <div class="itbms-payment-date">
              <p class="text-sm text-gray-500 mb-1">Payment Date:</p>
              <p class="font-semibold itbms-payment-date">
                {{ formatDate(selectedOrder.paymentDate) }}
              </p>
            </div>
            <div class="itbms-total-order-price">
              <p class="text-sm text-gray-500 mb-1">Total:</p>
              <p class="font-semibold text-lg itbms-total-order-price">
                {{ calculateTotal.toLocaleString() }}
              </p>
            </div>
            <div class="itbms-order-status">
              <p class="text-sm text-gray-500 mb-1">Status:</p>
              <span
                class="inline-block px-4 py-1 rounded-full text-sm font-semibold itbms-order-status"
                :class="{
                  'bg-green-100 text-green-700':
                    selectedOrder.orderStatus === 'COMPLETED',
                  'bg-red-100 text-red-700':
                    selectedOrder.orderStatus === 'CANCELED',
                  'bg-yellow-100 text-yellow-700':
                    selectedOrder.orderStatus === 'PENDING',
                }"
              >
                {{ selectedOrder.orderStatus }}
              </span>
            </div>
          </div>

          <!-- Shipping Address -->
          <div
            class="mt-6 bg-white bg-opacity-60 rounded-lg p-4 itbms-shipping-address"
          >
            <p class="text-sm font-medium text-gray-600 mb-1">Shipped To:</p>
            <p class="text-gray-700 itbms-shipping-address">
              {{ selectedOrder.shippingAddress }}
            </p>
          </div>

          <!-- Note -->
          <div class="mt-3 rounded-lg p-4 itbms-order-note">
            <p class="text-sm font-medium text-gray-600 mb-1">Note:</p>
            <p class="text-gray-700 itbms-order-note">
              {{ selectedOrder.orderNote || "-" }}
            </p>
          </div>
        </div>

        <!-- Order Items -->
        <div class="p-6">
          <div
            v-for="(item, index) in selectedOrder.orderItems"
            :key="index"
            class="itbms-row flex items-center gap-6 p-4 bg-gray-50 rounded-xl mb-4 hover:bg-gray-100 transition-colors itbms-item-row"
          >
            <img
              :src="noImage"
              alt="Product"
              class="w-24 h-24 object-cover rounded-lg"
            />
            <div class="flex-1">
              <h4
                class="font-semibold text-gray-800 mb-1 itbms-item-description"
              >
                {{ item.description }}
              </h4>
              <p class="text-sm text-gray-500 itbms-item-quantity">
                Qty:
                <span class="itbms-item-quantity">{{ item.quantity }}</span>
              </p>
              <p class="text-sm text-gray-500 itbms-item-price">
                Unit Price:
                <span class="itbms-item-price">{{
                  item.price.toLocaleString()
                }}</span>
              </p>
            </div>
            <div class="text-right itbms-item-total-price">
              <p class="text-lg font-bold text-blue-600">
                ฿<span class="itbms-item-total-price">{{
                  (item.price * item.quantity).toLocaleString()
                }}</span>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <MessageModal
    v-if="isNotFound"
    :handle-ok="
      () => router.push({ name: yourOrder ? 'order' : 'sale-orders' })
    "
    :title="'Order Not Found'"
    :message="errorMessage"
  />
</template>

<style scoped></style>
