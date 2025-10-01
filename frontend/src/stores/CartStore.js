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

  function findProductById(id) {
    let sellerIndex = -1;
    let productIndex = -1;

    // วนลูปหา seller
    carts.value.forEach((cartBySeller, sIndex) => {
      // วนลูปหาสินค้าใน cart ของ seller
      cartBySeller.cart.forEach((item, pIndex) => {
        if (item.productId === id) {
          sellerIndex = sIndex;
          productIndex = pIndex;
        }
      });
    });

    return {
      sellerIndex,
      productIndex,
      found: sellerIndex !== -1 && productIndex !== -1
    };
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
    if (!carts.value) {
      carts.value = [];
    }

    console.log(product.productSellerNickname);
    

    const { found, sellerIndex,productIndex } = findProductById(product.productId);
    
    if (found) {
      carts.value[sellerIndex].cart[productIndex].quantity++
      return;
    }

    // หา cart ของ seller
    const existingSellerIndex = carts.value.findIndex(
      (cart) => cart.productSellerNickname === product.productSellerNickname
    );

    if (existingSellerIndex !== -1) {
      // ถ้ามี seller อยู่แล้ว เพิ่มสินค้าเข้าไปใน cart ของ seller นั้น
      carts.value[existingSellerIndex].cart.push(product);
    } else {
      carts.value.push({
        productSellerNickname: product.productSellerNickname,
        cart: [product],
        checkAll: false
      });
    }
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
    updateProductById,
  };
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useCartStore, import.meta.hot));
}
