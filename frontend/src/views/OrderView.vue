<script setup>
import { useOrderStore } from '@/stores/OrderStore'
import { useUserStore } from '@/stores/UserStore'
import { ref, onMounted, computed, watch } from 'vue'
import OrderGallery from '@/components/OrderGallery.vue'
import { getOrdersBuyer, getOrdersSeller } from '@/services/OrderService'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/CartStore'
import { useSaleOrderStore } from '@/stores/saleOrderStore'
import { getAllBrands } from '@/services/BrandService'
import { useOrderFilterStore } from '@/stores/OrderFilterStore'

const userStore = useUserStore()
const orderStore = useOrderStore()
const router = useRouter()
const cartStore = useCartStore()
const saleOrderStore = useSaleOrderStore()

const STORAGE_KEYS = {
  PAGE_INFO: 'orderPageInfo',
  SELECTED_BRANDS: 'orderSelectedBrands',
  SEARCH_KEYWORD: 'orderSearchKeyWord',
}

const orderFilterStore = useOrderFilterStore()

const {
    getFilterList,
    setSelectBrand,
    setSelectedBrand,
    removeBrand,
    setSearchKeyWord,
    clearSelectedBrands,
} = orderFilterStore

const activeTab = ref('COMPLETED')
const hasError = ref('')

const userId = computed(() => userStore.getUserId())

const ordersPage = computed(() => orderStore.getOrdersPage())

const selectedBrands = computed(() => orderFilterStore.selectedBrands)
const searchKeyWord = computed(() => orderFilterStore.searchKeyWord)
const filterBrands = computed(() => getFilterList().brands)

const searchInput = ref('')
const brandOptions = ref([])
const showFilterDropdown = ref(false)

// Filter orders ตาม active tab ก่อน
const allFilteredOrders = computed(() => {
  if (!ordersPage.value?.content) return null

  return ordersPage.value.content.filter((order) => {
    return order.orderStatus === activeTab.value
  })
})

// แบ่งหน้าที่ฝั่ง client
const orders = computed(() => {
  if (!allFilteredOrders.value) return null
  
  const pageSize = 10
  const start = currentPage.value * pageSize
  const end = start + pageSize
  
  return allFilteredOrders.value.slice(start, end)
})

const { setCurrentOrderPage } = orderStore
const currentPage = computed(() => orderStore.getCurrentOrderPage())

const saveToLocalStorage = () => {
  sessionStorage.setItem(
    STORAGE_KEYS.SELECTED_BRANDS,
    JSON.stringify(selectedBrands.value)
  )
  sessionStorage.setItem(
    STORAGE_KEYS.SEARCH_KEYWORD,
    JSON.stringify(searchKeyWord.value)
  )
  sessionStorage.setItem(
    STORAGE_KEYS.PAGE_INFO,
    JSON.stringify({
      page: currentPage.value,
    })
  )
}

const getFromLocalStorage = () => {
  const selectedBrands = JSON.parse(
    sessionStorage.getItem(STORAGE_KEYS.SELECTED_BRANDS)
  )
  const searchKeyWord = JSON.parse(
    sessionStorage.getItem(STORAGE_KEYS.SEARCH_KEYWORD)
  )
  const pageInfo = JSON.parse(sessionStorage.getItem(STORAGE_KEYS.PAGE_INFO))
  return {
    pageInfo,
    selectedBrands,
    searchKeyWord,
  }
}

const setDefaultStoreValues = (pageInfo) => {
  const { selectedBrands, searchKeyWord } = getFromLocalStorage()
  setSearchKeyWord(searchKeyWord ?? '')
  searchInput.value = searchKeyWord
  setSelectedBrand(Array.isArray(selectedBrands) ? selectedBrands : [])
  setCurrentOrderPage(pageInfo?.page ?? 0)
}

const fetchOrders = async (pageInfo) => {
  try {
    if (!userId.value) {
      throw new Error('User not logged in')
    }

    // ดึงข้อมูลทั้งหมดมาครั้งเดียว
    const response = await getOrdersBuyer(userId.value, {
      filterBrands: filterBrands.value,
      searchKeyWord: searchKeyWord.value,
      page: 0,
      size: 1000, // ดึงข้อมูลทั้งหมด
      sortDirection: 'DESC',
    })

    // Check for 401 error
    if (response.error && response.error.status === 401) {
      userStore.clearUserInfo()
      userStore.setAccessToken(null)
      cartStore.setCart(null)
      saleOrderStore.setSaleOrderQuanatity(0)
      sessionStorage.clear()
      localStorage.clear()
      router.push({ name: 'signin' })
      return
    }

    saveToLocalStorage()
    if (response.error) {
      throw new Error(response?.error)
    }
  } catch (error) {
    hasError.value = error.message
  }
}

const initializeBrands = async () => {
  try {
    const response = await getAllBrands()
    if (!response.error) {
      brandOptions.value = response.sort((a, b) => a.name.localeCompare(b.name))
    } else {
      throw new Error(response?.error)
    }
  } catch (error) {
    hasError.value = error.message
  }
}

const handleSearchKeyword = async () => {
  setSearchKeyWord(searchInput.value)
  handlePageChange(0)
  await fetchOrders()
}

const selectBrand = async (brand) => {
  setSelectBrand(brand)
  showFilterDropdown.value = false
  setCurrentOrderPage(0)
  await fetchOrders()
}

const removeBrandAndApply = async (brand) => {
  removeBrand(brand)
  setCurrentOrderPage(0)
  await fetchOrders()
}

const handlePageChange = async (pageIndex) => {
  setCurrentOrderPage(pageIndex)
  // ไม่ต้อง fetch ใหม่ เพราะมีข้อมูลทั้งหมดแล้ว
}

const clearSelectBrands = async () => {
  clearSelectedBrands()
  setCurrentOrderPage(0)
  await fetchOrders()
}

onMounted(async () => {
  try {
    const { pageInfo } = getFromLocalStorage()
    setDefaultStoreValues(pageInfo)
    await initializeBrands()
    await fetchOrders(pageInfo)
  } catch (error) {
    hasError.value = error.message
  }
})

watch(activeTab, () => {
  setCurrentOrderPage(0)
})

const pageInfo = computed(() => {
  if (!allFilteredOrders.value) return null

  const pageSize = 10
  const totalElements = allFilteredOrders.value.length
  const totalPages = Math.ceil(totalElements / pageSize)
  const currentPageNum = currentPage.value

  return {
    content: orders.value,
    page: currentPageNum,
    size: pageSize,
    totalElements: totalElements,
    totalPages: totalPages,
    first: currentPageNum === 0,
    last: currentPageNum >= totalPages - 1 || totalPages === 0,
  }
})

const clearSearchKeyword = async () => {
  searchInput.value = ''
  setSearchKeyWord('')
  sessionStorage.setItem(STORAGE_KEYS.SEARCH_KEYWORD, JSON.stringify(''))
  await fetchOrders()
}
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 p-8">
    <div class="max-w-7xl mx-auto">
      <!-- Header -->
      <div
        class="flex flex-col lg:flex-row justify-between items-start lg:items-center gap-4 mb-6"
      >
        <h1 class="text-4xl font-bold text-gray-800 mb-8">Your Orders</h1>

        <!-- ✅ Search Box -->
        <div class="flex gap-2 w-full lg:w-auto">
          <label
            class="input input-bordered input-sm md:input-md flex items-center gap-2 flex-1 lg:flex-initial lg:w-80"
          >
            <input
              @keypress.enter="handleSearchKeyword"
              type="search"
              v-model="searchInput"
              class="grow"
              placeholder="Search model or seller..."
            />
            <button
              @click="handleSearchKeyword"
              class="cursor-pointer hover:text-blue-600 transition"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="20"
                height="20"
                viewBox="0 0 24 24"
              >
                <path
                  fill="currentColor"
                  d="M10.5 2a8.5 8.5 0 1 0 5.262 15.176l3.652 3.652a1 1 0 0 0 1.414-1.414l-3.652-3.652A8.5 8.5 0 0 0 10.5 2M4 10.5a6.5 6.5 0 1 1 13 0a6.5 6.5 0 0 1-13 0"
                />
              </svg>
            </button>
          </label>
          <button
            v-if="searchKeyWord"
            class="btn btn-error btn-sm md:btn-md text-white"
            @click="clearSearchKeyword"
          >
            Clear
          </button>
        </div>
      </div>

      <!-- ✅ Filter Section -->
      <section class="mb-6">
        <div
          class="flex flex-col sm:flex-row gap-3 items-stretch sm:items-center"
        >
          <!-- Brand Dropdown -->
          <div class="relative flex-1 sm:max-w-xs">
            <input
              type="text"
              readonly
              class="input input-bordered input-sm md:input-md w-full cursor-pointer"
              placeholder="Filter by brand"
              @click="showFilterDropdown = !showFilterDropdown"
            />
            <ul
              v-if="showFilterDropdown"
              class="absolute bg-white border rounded-lg mt-1 w-full z-10 shadow-lg max-h-60 overflow-y-auto"
            >
              <li
                v-for="brand in brandOptions"
                :key="brand.id"
                @mousedown.prevent="selectBrand(brand)"
                class="px-4 py-2 hover:bg-blue-50 cursor-pointer transition"
                :class="{
                  'bg-blue-100 font-semibold': selectedBrands.find(
                    (b) => b.id === brand.id
                  ),
                }"
              >
                {{ brand.name }}
              </li>
            </ul>
          </div>

          <!-- Clear All Button -->
          <button
            v-if="selectedBrands.length > 0"
            class="btn btn-error btn-sm md:btn-md text-white"
            @click="clearSelectBrands"
          >
            Clear All Filters
          </button>
        </div>

        <!-- ✅ Selected Brands Tags -->
        <div class="flex flex-wrap gap-2 mt-3" v-if="selectedBrands.length > 0">
          <div
            v-for="brand in selectedBrands"
            :key="brand.id"
            class="badge badge-primary gap-2 px-3 py-3 text-sm"
          >
            <span>{{ brand.name }}</span>
            <button
              class="font-bold cursor-pointer hover:text-red-300 transition"
              @click="removeBrandAndApply(brand)"
              aria-label="Remove brand filter"
            >
              ✕
            </button>
          </div>
        </div>
      </section>

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
          class="itbms-completed-orders-button"
        >
          Completed
        </button>
        <button
          @click="activeTab = 'CANCELLED'"
          :class="[
            'px-6 py-3 font-medium transition-all cursor-pointer',
            activeTab === 'CANCELLED'
              ? 'text-blue-600 border-b-4 border-blue-600 -mb-0.5'
              : 'text-gray-500 hover:text-gray-700',
          ]"
          class="itbms-canceled-orders-button"
        >
          Cancelled
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
