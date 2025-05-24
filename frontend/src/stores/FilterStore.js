import { ref } from "vue";
import { defineStore, acceptHMRUpdate } from "pinia";

export const useFilterStore = defineStore("Filter", () => {
  const getSelectedBrands = JSON.parse(localStorage.getItem("selectedBrands"));
  const selectedBrands = ref(getSelectedBrands || []);

  const pageInfo = JSON.parse(localStorage.getItem("pageInfo"));

  const sortField = ref(pageInfo.sortField || "createdOn");
  const sortDirection = ref(pageInfo.sortDirection || "ASC");
  const currentPage = ref(pageInfo.page || 0);
  const pageSize = ref(pageInfo.size || 10);

  const getFilterList = () => {
    return selectedBrands.value.map((b) => b.name);
  };

  const setSelectBrand = (brand) => {
    if (!selectedBrands.value.some((b) => b.name === brand.name)) {
      selectedBrands.value.push(brand);
    }
  };

  const setCurrentPage = (page) => {
    currentPage.value = page;
  };

  const clearAllSelectedBrands = () => {
    selectedBrands.value = [];
  };

  const removeBrand = (brand) => {
    selectedBrands.value = selectedBrands.value.filter(
      (b) => b.name !== brand.name
    );
  };

  const setPageSize = (size) => {
    pageSize.value = Number(size);
  };

  const setSortBrand = (direction = "ASC") => {
    sortField.value = direction ? "brand.name" : "createdOn";
    sortDirection.value = direction.toUpperCase();
  };

  return {
    selectedBrands,
    setSelectBrand,
    sortField,
    sortDirection,
    currentPage,
    pageSize,
    getFilterList,
    setCurrentPage,
    clearAllSelectedBrands,
    removeBrand,
    setPageSize,
    setSortBrand,
  };
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useFilterStore, import.meta.hot));
}
