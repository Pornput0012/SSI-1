import { ref } from "vue";
import { defineStore, acceptHMRUpdate } from "pinia";

export const useCartStore = defineStore("Cart", () => {
  const carts = ref(null);

  function setCart(cart) {
    if (Array.isArray(cart)) {
      carts.value = cart;
    }else{
      carts.value = null
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
    const { found, sellerIndex,productIndex } = findProductById(id);
    
    if (found) {
      carts.value[sellerIndex].cart.splice(productIndex, 1)
      console.log(carts.value[sellerIndex]);
      console.log(carts.value[sellerIndex].cart.length === 0);
      
      if (carts.value[sellerIndex].cart.length === 0 ) {
         carts.value.splice(sellerIndex, 1)
         
         console.log(carts.value);
         
      }
      return;
    }
  }

  function updateProductCartById(product) {
    
    if (!carts.value) {
      carts.value = [];
    }

    console.log(product.productSellerNickname);
    

    const { found, sellerIndex,productIndex } = findProductById(product.productId);
    
    if (found) {
      carts.value[sellerIndex].cart.splice(productIndex, 1, product)
      
      return;
    }
    
  }

   function addToCart(product,quantity=1) {
  
    
    if (!carts.value) {
      carts.value = [];
    }

    console.log(product.productSellerNickname);
    

    const { found, sellerIndex,productIndex } = findProductById(product.productId);
    
    console.log(product.quantity);
    if (found) {
      
      carts.value[sellerIndex].cart[productIndex].quantity+=quantity
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
        productSellerId : product.productSellerId,
        productSellerNickname: product.productSellerNickname,
        cart: [product],
        checkAll: false
      });
    }
  }
  // function updateProductById(productId, product) {
  //   if (findProductById(productId) !== -1 && product) {
  //     carts.value.splice(productId, 1, product);
  //   }
  // }

  return {
    carts,
    setCart,
    getCarts,
    addToCart,
    findProductById,
    deleteProductById,
    updateProductCartById,
  };
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useCartStore, import.meta.hot));
}
