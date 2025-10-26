import { ref } from "vue";
import { defineStore, acceptHMRUpdate } from "pinia";

export const useOrderStore = defineStore("order", () => {
  const orders = ref([]);
  const ordersPage = ref(null);
  const currentOrderPage = ref(0);

  function setOrders(orderArray) {
    orders.value = orderArray;
  }

  function setOrdersPage(orderArray) {
    ordersPage.value = orderArray;
  }

  function getOrders() {
    return orders.value;
  }

  function getOrdersPage() {
    return ordersPage.value;
  }

  function addOrder(order) {
    if (Array.isArray(order)) {
      orders.value.push(...order);
    } else {
      orders.value.push(order);
    }
  }

  // ✅ เพิ่มฟังก์ชันจัดการ currentOrderPage
  function setCurrentOrderPage(page) {
    currentOrderPage.value = page;
  }

  function getCurrentOrderPage() {
    return currentOrderPage.value;
  }

  function resetOrderPage() {
    currentOrderPage.value = 0;
  }

  return {
    orders,
    setOrders,
    getOrders,
    addOrder,
    ordersPage,
    setOrdersPage,
    getOrdersPage,
    currentOrderPage,
    setCurrentOrderPage,
    getCurrentOrderPage,
    resetOrderPage,
  };
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useOrderStore, import.meta.hot));
}
