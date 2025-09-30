import { ref } from "vue";
import { defineStore, acceptHMRUpdate } from "pinia";

export const useCartStore = defineStore("Cart", () => {
  const carts = ref(null);

  function setCart(cart) {
    if (Array.isArray(cart)) {
      carts.value = cart;
    }
  }

  function getCarts() {
    return carts.value;
  }

  function findProductById(id){
    const productIndex = carts.value.findIndex((ele) => ele.id === id);
    return productIndex 
  }


  function deleteProductById(id) {
    if (!Array.isArray(carts.value)) {
      return;
    }

    if (id && findProductById(id) !== -1) {
      carts.value.splice(findProductById(id), 1);
    }
  }

  function addToCart(product) {
    carts.value.push(product);
  }

  function updateProductById(productId, product) {
   

    if (findProductById(productId) !== -1 && product) {
      carts.value.splice(productId, 1, product);
    }
  }

  return {
    carts,
    setCart,
    getCarts,
    addToCart,
    findProductById,
    deleteProductById,
    updateProductById
  };
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useCartStore, import.meta.hot));
}
