import { ref } from "vue";
import { defineStore, acceptHMRUpdate } from "pinia";

export const useBrandStore = defineStore("Brand", () => {
  const brands = ref(null);

  function setBrand(brand) {
    if (Array.isArray(brand)) {
      brands.value = brand;
    }
  }

  function getBrands() {
    return brands.value;
  }

  function deleteBrandById(id) {
    if (!Array.isArray(brands.value)) {
      return;
    }

    const brandIndex = brands.value.findIndex((ele) => ele.id == id);

    if (id && brandIndex !== -1) {
      brands.value.splice(brandIndex, 1);
    }
  }

  function createBrand(brand) {
    brands.value.push(brand);
  }

  function updateBrandById(brandId, brand) {
    const brandIndex = brands.value.findIndex((ele) => ele.id === brandId);

    if (brandIndex !== -1 && brand) {
      brands.value.splice(brandIndex, 1, brand);
    }
  }

  return {
    brands,
    setBrand,
    getBrands,
    deleteBrandById,
    createBrand,
    updateBrandById,
  };
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useBrandStore, import.meta.hot));
}
