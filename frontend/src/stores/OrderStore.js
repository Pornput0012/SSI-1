import { ref } from 'vue'
import { defineStore, acceptHMRUpdate } from 'pinia'

export const useOrderStore = defineStore('order', () => {
  const orders = ref([])
  const buyerOrdersPage = ref(null) // pagination ของ buyer
  const sellerOrdersPage = ref(null) // pagination ของ seller

  function setOrders(orderArray) {
    orders.value = orderArray
  }

  function setBuyerOrdersPage(orderArray) {
    buyerOrdersPage.value = orderArray
  }
  function setSellerOrdersPage(orderArray) {
    sellerOrdersPage.value = orderArray
  }

  function getOrders() {
    return orders.value
  }

  function getBuyerOrdersPage() {
    return buyerOrdersPage.value
  }
  function getSellerOrdersPage() {
    return sellerOrdersPage.value
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

  return {
    orders,
    setOrders,
    getOrders,
    addOrder,
    ordersPage,
    setBuyerOrdersPage,
    getBuyerOrdersPage,
    setSellerOrdersPage,
    getSellerOrdersPage,
    getOrderById,
  }
})

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useOrderStore, import.meta.hot))
}
