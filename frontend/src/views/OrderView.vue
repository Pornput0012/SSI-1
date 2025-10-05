<script setup>
import { useFilterStore } from '@/stores/FilterStore'
import { useOrderStore } from '@/stores/OrderStore'
import { useUserStore } from '@/stores/UserStore'
import { ref, onMounted, computed, watch } from 'vue'
import Breadcrumbs from '@/components/Breadcrumbs.vue'
import OrderGallery from '@/components/OrderGallery.vue'
import { getOrdersBuyer, getOrdersSeller } from '@/services/OrderService'
import MessageModal from '@/components/MessageModal.vue'

const userStore = useUserStore()
const filterStore = useFilterStore()
const orderStore = useOrderStore()

const STORAGE_KEYS = {
  PAGE_INFO: 'orderPageInfo',
}

const activeTab = ref('COMPLETED')
const hasError = ref('')

const userId = computed(() => userStore.getUserId())
const userType = computed(() => userStore.getUserType())
const isSeller = computed(() => userType.value?.toUpperCase() === 'SELLER')

const ordersPage = computed(() => orderStore.getOrdersPage())

const orders = computed(() => {
  if (!ordersPage.value?.content) return null

  return ordersPage.value.content.filter((order) => {
    return order.orderStatus === activeTab.value
  })
})

const { setCurrentOrderPage } = orderStore
const currentPage = computed(() => orderStore.getCurrentOrderPage())

const saveToLocalStorage = () => {
  sessionStorage.setItem(
    STORAGE_KEYS.PAGE_INFO,
    JSON.stringify({
      page: currentPage.value,
    })
  )
}

const getFromLocalStorage = () => {
  const pageInfo = JSON.parse(sessionStorage.getItem(STORAGE_KEYS.PAGE_INFO))
  return {
    pageInfo,
  }
}

const setDefaultStoreValues = (pageInfo) => {
  getFromLocalStorage()
  setCurrentOrderPage(pageInfo?.page ?? 0)
}

const fetchOrders = async (pageInfo) => {
  try {
    if (!userId.value) {
      throw new Error('User not logged in')
    }

    const response = isSeller.value
      ? await getOrdersSeller(userId.value, {
          page: pageInfo?.page ?? currentPage.value,
          size: 10,
          sortDirection: 'DESC',
        })
      : await getOrdersBuyer(userId.value, {
          page: pageInfo?.page ?? currentPage.value,
          size: 10,
          sortDirection: 'DESC',
        })
    saveToLocalStorage()
    if (response.error) {
      throw new Error(response?.error)
    }
  } catch (error) {
    hasError.value = error.message
  }
}

const handlePageChange = async (pageIndex) => {
  setCurrentOrderPage(pageIndex)
  await fetchOrders()
}

onMounted(async () => {
  try {
    const { pageInfo } = getFromLocalStorage()
    setDefaultStoreValues(pageInfo)
    await fetchOrders(pageInfo)
  } catch (error) {
    hasError.value = error.message
  }
})

watch(activeTab, () => {
  setCurrentOrderPage(0)
})

const pageInfo = computed(() => {
  if (!ordersPage.value) return null
  
  return {
    ...ordersPage.value,
    page: ordersPage.value.page ?? 0,
  }
})
// const orders = [
//   {
//     id: '123456',
//     buyerId : '6262',
//     Seller:{id:'123',username: 'Somsuan'},
//     orderDate: 'October 1, 2025',
//     paymentDate: 'October 1, 2025',
//     total: '62,700',
//     orderStatus: 'Completed',
//     shippingAddress: 'Somchai Jaidee, 123/45 Moo 6, T. Bangna, A. Bangna, Bangkok 10260',
//     note: 'hi',
//     orderItems: [
//       {
//         no: '123',
//         saleItemId : '123',

//         description: 'Apple iPhone 14 Pro Max (512GB, Space Black)',
//         quantity: 1,
//         price: '42,900',
//         image: '📱'
//       },
//       {
//         no: '123',
//         saleItemId : '123',
//         description: 'Apple iPhone 13 mini (128GB, Green)',
//         quantity: 1,
//         price: '19,800',
//         image: '📱'
//       }
//     ]
//   }
// ];
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 p-8">
    <div class="max-w-7xl mx-auto">
      <!-- Header -->
      <h1 class="text-4xl font-bold text-gray-800 mb-8">Your Orders</h1>

      <!-- Tabs -->
      <div class="flex gap-1 mb-6 border-b-2 border-gray-200">
        <button
          @click="activeTab = 'COMPLETED'"
          :class="[
            'px-6 py-3 font-medium transition-all',
            activeTab === 'COMPLETED'
              ? 'text-blue-600 border-b-4 border-blue-600 -mb-0.5'
              : 'text-gray-500 hover:text-gray-700',
          ]"
        >
          Completed
        </button>
        <button
          @click="activeTab = 'CANCELED'"
          :class="[
            'px-6 py-3 font-medium transition-all',
            activeTab === 'CANCELED'
              ? 'text-blue-600 border-b-4 border-blue-600 -mb-0.5'
              : 'text-gray-500 hover:text-gray-700',
          ]"
        >
          Canceled
        </button>
      </div>

      <!-- Orders Gallery -->
      <OrderGallery
        :orders="orders"
        :page-info="pageInfo"
        :has-error="hasError"
        @page-changed="handlePageChange"
      />
    </div>
  </div>
</template>

<style scoped>
/* Additional custom styles if needed */
</style>
