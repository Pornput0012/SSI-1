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

  return {
    brands,
    setBrand,
    getBrands,
    deleteBrandById,
  };
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useBrandStore, import.meta.hot));
}
