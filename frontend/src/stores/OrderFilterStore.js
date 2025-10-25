import { ref } from "vue";
import { defineStore, acceptHMRUpdate } from "pinia";

export const useOrderFilterStore = defineStore('orderFilter', () => {
  const selectedBrands = ref([])
  const searchKeyWord = ref('')

  const getSelectedBrands = () => selectedBrands.value.map((b) => b.name)
  
  const getFilterList = () => ({
    brands: getSelectedBrands(),
  })
  const getSearchKeyWord = () => searchKeyWord.value;

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

  const setSearchKeyWord = (text) => {
    searchKeyWord.value = text
  }

  const clearSelectedBrands = () => {
    selectedBrands.value = []
  }

  return {
    selectedBrands,
    searchKeyWord,
    getFilterList,
    setSelectBrand,
    setSelectedBrand,
    removeBrand,
    setSearchKeyWord,
    clearSelectedBrands,
    getSearchKeyWord,
  }
})

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useOrderFilterStore, import.meta.hot));
}