<script setup>
import { useOrderStore } from "@/stores/OrderStore";
import { useUserStore } from "@/stores/UserStore";
import { ref, onMounted, computed, watch } from "vue";
import OrderGallery from "@/components/OrderGallery.vue";
import { getOrdersSeller } from "@/services/OrderService";
import MessageModal from "@/components/MessageModal.vue";

const userStore = useUserStore();
const orderStore = useOrderStore();

const STORAGE_KEYS = {
  PAGE_INFO: "saleOrdersPageInfo",
};

const activeTab = ref("newOrder");
const hasError = ref("");

const userId = computed(() => userStore.getUserId());
const userType = computed(() => userStore.getUserType());
const isSeller = computed(() => userType.value?.toUpperCase() === "SELLER");

const ordersPage = computed(() => orderStore.getOrdersPage());

const orders = computed(() => {
  if (!ordersPage.value?.content) return null;

  switch (activeTab.value) {
    case "allOrder":
      return ordersPage.value.content.filter((order) => {
        return (
          order.orderStatus?.toUpperCase() === "COMPLETED" && order.viewOrder
        );
      });
    case "cancelOrder":
      return ordersPage.value.content.filter((order) => {
        return order.orderStatus?.toUpperCase() === "CANCELLED";
      });
      break;
    case "newOrder":
      return ordersPage.value.content.filter((order) => {
        return (
          order.orderStatus?.toUpperCase() === "COMPLETED" && !order.viewOrder
        );
      });
  }

  return ordersPage.value.content.filter((order) => {
    return order.orderStatus === activeTab.value;
  });
});

const { setCurrentOrderPage } = orderStore;
const currentPage = computed(() => orderStore.getCurrentOrderPage());

const saveToLocalStorage = () => {
  sessionStorage.setItem(
    STORAGE_KEYS.PAGE_INFO,
    JSON.stringify({
      page: currentPage.value,
    })
  );
};

const getFromLocalStorage = () => {
  const pageInfo = JSON.parse(sessionStorage.getItem(STORAGE_KEYS.PAGE_INFO));
  return {
    pageInfo,
  };
};

const setDefaultStoreValues = (pageInfo) => {
  getFromLocalStorage();
  setCurrentOrderPage(pageInfo?.page ?? 0);
};

const fetchOrders = async (pageInfo) => {
  try {
    if (!userId.value) {
      throw new Error("User not logged in");
    }

    const response = await getOrdersSeller(userId.value, {
      page: pageInfo?.page ?? currentPage.value,
      size: 10,
      sortDirection: "DESC",
    });
    saveToLocalStorage();
    if (response.error) {
      throw new Error(response?.error);
    }
  } catch (error) {
    hasError.value = error.message;
  }
};

const handlePageChange = async (pageIndex) => {
  setCurrentOrderPage(pageIndex);
  await fetchOrders();
};

onMounted(async () => {
  try {
    const { pageInfo } = getFromLocalStorage();
    setDefaultStoreValues(pageInfo);
    await fetchOrders(pageInfo);
  } catch (error) {
    hasError.value = error.message;
  }
});

watch(activeTab, () => {
  setCurrentOrderPage(0);
});

const pageInfo = computed(() => {
  if (!ordersPage.value) return null;

  return {
    ...ordersPage.value,
    page: ordersPage.value.page ?? 0,
  };
});
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 p-8">
    <div class="max-w-7xl mx-auto">
      <h1 class="text-4xl font-bold text-gray-800 mb-8">Your Sale Orders</h1>

      <div class="flex gap-1 mb-6 border-b-2 border-gray-200">
        <button
          @click="activeTab = 'allOrder'"
          :class="[
            'px-6 py-3 font-medium transition-all cursor-pointer',
            activeTab === 'allOrder'
              ? 'text-blue-600 border-b-4 border-blue-600 -mb-0.5'
              : 'text-gray-500 hover:text-gray-700',
          ]"
        >
          All Orders
        </button>
        <button
          @click="activeTab = 'newOrder'"
          :class="[
            'px-6 py-3 font-medium transition-all',
            activeTab === 'newOrder'
              ? 'text-blue-600 border-b-4 border-blue-600 -mb-0.5'
              : 'text-gray-500 hover:text-gray-700',
          ]"
        >
          New Orders
        </button>
        <button
          @click="activeTab = 'cancelOrder'"
          :class="[
            'px-6 py-3 font-medium transition-all',
            activeTab === 'cancelOrder'
              ? 'text-blue-600 border-b-4 border-blue-600 -mb-0.5'
              : 'text-gray-500 hover:text-gray-700',
          ]"
        >
          Cancelled Orders
        </button>
      </div>
      <!-- Orders Gallery -->
      <OrderGallery
        :sale-orders="true"
        :new-order="activeTab === 'newOrder'"
        :orders="orders"
        :page-info="pageInfo"
        :has-error="hasError"
        @page-changed="handlePageChange"
      />
    </div>
  </div>
</template>

<style scoped></style>
