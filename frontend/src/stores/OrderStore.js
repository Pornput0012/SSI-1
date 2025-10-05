import { ref } from 'vue'
import { defineStore, acceptHMRUpdate } from 'pinia'

export const useOrderStore = defineStore('order', () => {
  const orders = ref([])
  const ordersPage = ref(null)
  const currentOrderPage = ref(0)

  function setOrders(orderArray) {
    orders.value = orderArray
  }

  function setOrdersPage(orderArray) {
    ordersPage.value = orderArray
  }

  function getOrders() {
    return orders.value
  }

  function getOrdersPage() {
    return ordersPage.value
  }

  function getOrderById(orderId) {
    if (!Array.isArray(orders.value)) {
      return null
    }

    const index = orders.value.findIndex((item) => item.id == orderId)

    if (index === -1) {
      return null
    }
    return orders.value[index]
  }

  function addOrder(order) {
    console.log('🟢 Before add:', orders.value)

    if (Array.isArray(order)) {
      orders.value.push(...order)
    } else {
      orders.value.push(order)
    }

    console.log('🟢 After add:', orders.value)
  }

  // ✅ เพิ่มฟังก์ชันจัดการ currentOrderPage
  function setCurrentOrderPage(page) {
    currentOrderPage.value = page
  }

  function getCurrentOrderPage() {
    return currentOrderPage.value
  }

  function resetOrderPage() {
    currentOrderPage.value = 0
  }

  return {
    orders,
    setOrders,
    getOrders,
    addOrder,
    ordersPage,
    setOrdersPage,
    getOrdersPage,
    getOrderById,
    currentOrderPage,
    setCurrentOrderPage,
    getCurrentOrderPage,
    resetOrderPage,
  }
})

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useOrderStore, import.meta.hot))
}
