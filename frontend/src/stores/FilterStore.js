import { ref } from "vue";
import { defineStore, acceptHMRUpdate } from "pinia";

export const useFilterStore = defineStore("Filter", () => {
  const selectedBrands = ref([]);
  const selectedStorages = ref([])

  const sortField = ref("");
  const sortDirection = ref("ASC");
  const currentPage = ref(0);
  const pageSize = ref(10);

  const getSelectedBrands = () => selectedBrands.value.map((b) => b.name);
  const getSelectedStorages = () => selectedStorages.value;
  const getFilterList = () => ({
    brands: getSelectedBrands(),
    storages: getSelectedStorages(),
  });

  const resetCurrentPage = () => {
    currentPage.value = 0;
    const pageInfo = JSON.parse(sessionStorage.getItem("pageInfo"));
    if (pageInfo?.page) {

      pageInfo.page = 0;
    }
    sessionStorage.setItem("pageInfo", JSON.stringify(pageInfo));
  };

  const setSelectBrand = (brand) => {
    if (!selectedBrands.value.some((b) => b.name === brand.name)) {
      selectedBrands.value.push(brand);
    }
  };
  const setSelectedBrand = (brands) => {
    selectedBrands.value = brands;
  };

  const setCurrentPage = (page) => {
    currentPage.value = page;
  };

  const setSelectStorage = (storage) => {
    if (!selectedStorages.value.includes(storage)) {
      selectedStorages.value.push(storage);
    }
  };
  
  const removeStorage = (storage) => {
    selectedStorages.value = selectedStorages.value.filter(s => s !== storage);
  };

  const clearAllFilters = () => {
    clearSelectedBrands()
    clearSelectedStorages()
  }

  const clearSelectedBrands = () => {
    selectedBrands.value = [];
  };

  const clearSelectedStorages = () => {
    selectedStorages.value = []
  }

  const removeBrand = (brand) => {
    selectedBrands.value = selectedBrands.value.filter(
      (b) => b.name !== brand.name
    );
  };

  const setPageSize = (size) => {
    pageSize.value = Number(size);
  };

  const setSortBrand = (direction) => {
    sortField.value = direction ? "brand.name" : "";
    sortDirection.value = direction ? direction.toUpperCase() : "";
  };

  const setSortStorage = (direction) => {
  sortField.value = direction ? "storageGb" : "";
  sortDirection.value = direction ? direction.toUpperCase() : "";
};

  const setSortField = (field) => {
    sortField.value = field;
  };
  const setSortDirection = (direction) => {
    sortDirection.value = direction;
  };

  return {
    selectedBrands,
    sortField,
    sortDirection,
    currentPage,
    pageSize,
    clearAllFilters,
    removeBrand,
    resetCurrentPage,
    getFilterList,
    setCurrentPage,
    setPageSize,
    setSortBrand,
    setSelectedBrand,
    setSelectBrand,
    setSortField,
    setSortDirection,
    setSelectStorage,
    removeStorage,
    selectedStorages,
    setSortStorage
  };
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useFilterStore, import.meta.hot));
}
