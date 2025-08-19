import { ref } from 'vue'
import { defineStore, acceptHMRUpdate } from 'pinia'

export const useFilterStore = defineStore('Filter', () => {
  const selectedBrands = ref([])
  const selectedStorages = ref([])
  const selectedPrices = ref({ min: null, max: null })

  const sortField = ref('')
  const sortDirection = ref('ASC')
  const currentPage = ref(0)
  const pageSize = ref(10)

  const getSelectedBrands = () => selectedBrands.value.map((b) => b.name)
  const getSelectedStorages = () => selectedStorages.value
  const getSelectedPrices = () => selectedPrices.value
  const getFilterList = () => ({
    brands: getSelectedBrands(),
    storages: getSelectedStorages(),
    prices: getSelectedPrices(),
  })

  const resetCurrentPage = () => {
    currentPage.value = 0
    const pageInfo = JSON.parse(sessionStorage.getItem('pageInfo'))
    if (pageInfo?.page) {
      pageInfo.page = 0
    }
    sessionStorage.setItem('pageInfo', JSON.stringify(pageInfo))
  }

  // ----- BRAND -----
  const setSelectBrand = (brand) => {
    if (!selectedBrands.value.some((b) => b.name === brand.name)) {
      selectedBrands.value.push(brand)
    }
  }
  const setSelectedBrand = (brands) => {
    selectedBrands.value = brands
  }

  const removeBrand = (brand) => {
    selectedBrands.value = selectedBrands.value.filter(
      (b) => b.name !== brand.name
    )
  }

  // ----- STORAGE -----
  const setSelectStorage = (storage) => {
    if (!selectedStorages.value.includes(storage)) {
      selectedStorages.value.push(storage)

      selectedStorages.value.sort((a, b) => {
        const ax = a === 'Not specified' ? Number.MAX_SAFE_INTEGER : a
        const bx = b === 'Not specified' ? Number.MAX_SAFE_INTEGER : b
        return ax - bx
      })
    }
  }
  const setSelectedStorage = (storages) => {
    selectedStorages.value = storages
  }
  const removeStorage = (storage) => {
    selectedStorages.value = selectedStorages.value.filter((s) => s !== storage)
  }

  // ----- PRICE -----
  const setSelectPrice = (min, max) => {
    selectedPrices.value = {
      min: min !== undefined ? min : selectedPrices.value.min,
      max: max !== undefined ? max : selectedPrices.value.max,
    }
  }

  const setSelectedPrice = (prices) => {
    selectedPrices.value = prices ?? { min: null, max: null }
  }

  const removePrice = () => {
    selectedPrices.value = { min: null, max: null }
  }

  // ----- CLEAR -----
  const clearAllFilters = () => {
    clearSelectedBrands()
    clearSelectedStorages()
    clearSelectedPrices()
  }
  const clearSelectedBrands = () => {
    selectedBrands.value = []
  }
  const clearSelectedStorages = () => {
    selectedStorages.value = []
  }
  const clearSelectedPrices = () => {
    selectedPrices.value = { min: null, max: null }
  }

  // ----- PAGINATION -----
  const setCurrentPage = (page) => {
    currentPage.value = page
  }

  const setPageSize = (size) => {
    pageSize.value = Number(size)
  }

  // ----- SORT -----
  const setSortBrand = (direction) => {
    sortField.value = direction ? 'brand.name' : ''
    sortDirection.value = direction ? direction.toUpperCase() : ''
  }

  const setSortField = (field) => {
    sortField.value = field
  }
  const setSortDirection = (direction) => {
    sortDirection.value = direction
  }

  return {
    selectedBrands,
    selectedStorages,
    selectedPrices,
    sortField,
    sortDirection,
    currentPage,
    pageSize,
    getFilterList,
    resetCurrentPage,

    setSelectedBrand,
    setSelectBrand,
    removeBrand,

    setSelectedStorage,
    setSelectStorage,
    removeStorage,

    setSelectedPrice,
    setSelectPrice,
    removePrice,

    clearAllFilters,
    clearSelectedBrands,
    clearSelectedStorages,

    setCurrentPage,
    setPageSize,

    setSortBrand,
    setSortField,
    setSortDirection,
  }
})

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useFilterStore, import.meta.hot))
}
