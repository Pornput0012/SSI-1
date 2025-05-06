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

  return {
    brands,
    setBrand,
    getBrands,
  };
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useBrandStore, import.meta.hot));
}
