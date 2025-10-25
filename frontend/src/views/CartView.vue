<script setup>
import Breadcrumbs from "@/components/Breadcrumbs.vue";
import { deleteCartById, getAllCart, updateCart } from "@/services/CartService";
import { useCartStore } from "@/stores/CartStore";
import { useUserStore } from "@/stores/UserStore";
import noImage from "@/assets/No_image_available.png";
import { computed, onMounted, ref, watch, watchEffect } from "vue";
import { getImageByFileName } from "@/services/SaleItemService";
import { createOrder } from "@/services/OrderService";
import { useOrderStore } from "@/stores/OrderStore";
import router from "@/router";
import { useFilterStore } from "@/stores/FilterStore";
import { useNotiStore } from "@/stores/NotiStore";
import { useSaleOrderStore } from "@/stores/saleOrderStore";

const userStore = useUserStore();
const cartStore = useCartStore();
const saleOrderStore = useSaleOrderStore();
const userId = computed(() => userStore.getUserId());
// Mockup ข้อมูลสินค้า
const carts = ref([]);
const shipTo = ref({
  address: "",
  note: "",
});
const hasError = ref("");

onMounted(async () => {
  loadSavedData();
  try {
    if (cartStore.carts) {
      carts.value = cartStore.carts.map((ele) => {
        return {
          ...ele,
          checkAll: false,
        };
      });
      // initialize shipTo from store if available
      if (cartStore.carts && cartStore.carts.length > 0) {
        const first = cartStore.carts[0];
        if (first) {
          shipTo.value.address = first.address || shipTo.value.address;
          shipTo.value.note = first.note || shipTo.value.note;
          // keep persistent vars in sync
          shippingAddress.value = shipTo.value.address;
          orderNote.value = shipTo.value.note;
        }
      }
    } else {
      console.log("Fetching cart for user:", userId.value);

      const response = await getAllCart(userId.value);

      // Check for 401 error
      if (response.error && response.error.status === 401) {
        userStore.clearUserInfo();
        userStore.setAccessToken(null);
        cartStore.setCart(null);
        saleOrderStore.setSaleOrderQuanatity(0);
        sessionStorage.clear();
        localStorage.clear();
        router.push({ name: "signin" });
        return;
      }

      if (response.error) {
        throw new Error(response.error);
      }
      // response is the fetched cart list; set shipTo from the first seller entry if present
      const first = response && response.length ? response[0] : null;
      if (first) {
        shipTo.value.address = first.address || shipTo.value.address;
        shipTo.value.note = first.note || shipTo.value.note;
        // keep persistent vars in sync
        shippingAddress.value = shipTo.value.address;
        orderNote.value = shipTo.value.note;
      }

      carts.value = response?.map((ele) => {
        return {
          ...ele,
          cart: ele?.cart.map((ele) => {
            return { ...ele, checked: false };
          }),
          checkAll: false,
        };
      });
    }
    console.log("Cart data loaded:", carts.value);
    // โหลดรูปภาพหลังจากได้ข้อมูล cart
    await loadImages();
  } catch (error) {
    hasError.value = error.message;
    console.error("Error loading cart:", error);
  }

  console.log(cartStore.carts);
});

const selectAll = ref(false);

const itemSelected = computed(() => {
  return carts.value.flatMap((cart) =>
    cart.cart.filter((item) => item.checked)
  );
});

const selectSellerState = computed(() => {
  return carts.value.every((item) => {
    return item.checkAll === true;
  });
});

const loadImages = async () => {
  console.log("hi");

  try {
    for (const ele of carts.value) {
      console.log("hi2");
      console.log(ele.cart);

      for (const item of ele.cart) {
        if (item.imageName) {
          console.log("Loading image for:", item.imageName);
          const image_url = await getImageByFileName(item.imageName);
          item.image = image_url;
          console.log("Loaded image:", item.image);
        } else {
          console.log("No image name, using default");
          item.image = noImage;
        }
      }
    }
  } catch (error) {
    console.error(`Failed to load images:`, error);
  }
};

onMounted(async () => {
  await loadImages();
});

watchEffect(() => {
  carts.value.forEach((cart) => {
    // ถ้าสินค้าทุกตัวในร้านถูกเลือก
    cart.checkAll =
      cart.cart.length > 0 && cart.cart.every((item) => item.checked);
  });

  // sync selectAll ด้วย (ยังใช้ได้เหมือนเดิม)
  selectAll.value =
    carts.value.length > 0 && carts.value.every((cart) => cart.checkAll);
});

const totalPrice = computed(() => {
  return itemSelected.value.reduce(
    (sum, item) => sum + item.productPrice * item.quantity,
    0
  );
});

const queque = ref([]);
// เพิ่ม/ลดจำนวน
const updateQuantity = (item, newQuantity) => {
  // ป้องกันไม่ให้ quantity น้อยกว่า 1
  item.quantity = Math.max(0, newQuantity);
};

const callUpdateCart = async (item) => {
  try {
    const data = {
      userId: userId.value,
      productId: item.id,
      imageFileName: item.fileName,
      quantity: item.quantity,
    };

    const response = await updateCart(item.id, data);
    
    // Check for 401 error
    if (response.error && response.error.status === 401) {
      userStore.clearUserInfo();
      userStore.setAccessToken(null);
      cartStore.setCart(null);
      saleOrderStore.setSaleOrderQuanatity(0);
      sessionStorage.clear();
      localStorage.clear();
      router.push({ name: "signin" });
      return;
    }
    
    if (response.error) {
      throw new Error(response.error);
    }

    cartStore.updateProductCartById(item);
  } catch (error) {
    hasError.value = error.message;
    console.error("Error update cart:", error);
  }
};

const itemExist = (item, arr) => {
  let changeQuantity;
  return {
    index: arr.findIndex((ele) => {
      if (ele.id === item.id) {
        changeQuantity = ele;
      }
      return ele.id === item.id;
    }),
    changeQuantity,
  };
};
const deleteTargetId = ref(null);
const deleteTargetPid = ref(null);

const increaseQty = (item) => {
  if (item.quantity <= item.productQuantity) {
    updateQuantity(item, item.quantity + 1);
    callUpdateCart(item);
  }
  // const itemWait = itemExist(item, queque.value);
  // if (itemWait.index == -1) {
  //   queque.value.push(item);
  // } else {
  //   console.log("else");
  //   itemWait.changeQuantity.quantity++;
  // }
};

const showWarningModal = ref(false);
const showDeleteModal = ref(false);

const confirmDelete = () => {
  console.log("confirm");

  showDeleteModal.value = true;
};

const decreaseQty = (item) => {
  // const itemWait = itemExist(item, queque.value);
  // if (itemWait.index == -1) {
  console.log("0");
  if (item.quantity === 1) {
    console.log("before set");

    console.log("id" + item.id);
    console.log("pid" + item.productId);
    deleteTargetId.value = item.id;
    console.log(deleteTargetId.value);

    deleteTargetPid.value = item.productId;
    console.log("after set");
    confirmDelete();
    return;
  }

  updateQuantity(item, item.quantity - 1);
  if (item.quantity !== 0) {
    callUpdateCart(item);
  }
  //   queque.value.push(item);
  // } else {
  //   console.log("else");
  //   itemWait.changeQuantity.quantity--;
  // }
};

const checkAllItem = (i) => {
  const isAllTrue = carts.value[i].cart.every((item) => {
    return item.checked;
  });
  const isAllFalse = carts.value[i].cart.every((item) => {
    return !item.checked;
  });
  if (isAllTrue || isAllFalse) {
    carts.value[i].cart.forEach((item) => {
      item.checked = !item.checked;
      return;
    });
  } else {
    carts.value[i].cart.forEach((item) => {
      if (item.checked === true) {
        item.checked = false;
      }
      item.checked = !item.checked;
      return;
    });
  }
};

const productLength = computed(() => {
  let countItem = 0;
  if (carts.value) {
    carts.value.forEach((sellerCart) => {
      sellerCart.cart.forEach((item) => {
        countItem++;
      });
    });
  }
  return countItem;
});

const removeCart = async (id, pid) => {
  try {
    showDeleteModal.value = false;
    console.log(id);

    if (id) {
      const response = await deleteCartById(id);
      if (response.success) {
        const hasSaleItem = cartStore.findProductById(pid);
        if (hasSaleItem.found) {
          cartStore.deleteProductById(pid);
          deleteTargetId.value = null;
          deleteTargetPid.value = null;
          return;
        } else {
          showWarningModal.value = true;
          deleteTargetId.value = null;
          deleteTargetPid.value = null;
          return;
        }
      } else {
        throw new Error("An error has occurred, the cart does not exist.");
      }
    }
  } catch (error) {
    console.error(error);

    // errorMessage.value = error.message;
    // isNotFound.value = true;
  }
};
// const hasUpdateQueue = ref(false);
// watchEffect(() => {
//   if (!hasUpdateQueue.value) {
//     hasUpdateQueue.value = true;
//     setTimeout(() => {
//       console.log("callApi");

//       queque.value.forEach((ele) => {
//         callUpdateCart(ele)
//       });
//     }, 5000);
//   }
// });
const STORAGE_KEYS = {
  SHIPPING_ADDRESS: "shippingAddress",
  ORDER_NOTE: "orderNote",
};
const shippingAddress = ref("");
const orderNote = ref("");
const orderStore = useOrderStore();

const loadSavedData = () => {
  const savedAddress = sessionStorage.getItem(STORAGE_KEYS.SHIPPING_ADDRESS);
  const savedNote = sessionStorage.getItem(STORAGE_KEYS.ORDER_NOTE);

  if (savedAddress) {
    // restore into both the visible form (shipTo) and the backing var (shippingAddress)
    shippingAddress.value = savedAddress;
    shipTo.value.address = savedAddress;
  }
  if (savedNote) {
    orderNote.value = savedNote;
    shipTo.value.note = savedNote;
  }
};

// บันทึก address และ note ลง sessionStorage
const saveFormData = () => {
  // keep the visible form (`shipTo`) in sync with the backing refs
  // and save the backing refs to sessionStorage. Do NOT overwrite
  // the backing refs from `shipTo` here — that caused typed input
  // to be reverted.
  shipTo.value.address = shippingAddress.value
    ? shippingAddress.value.trim()
    : "";
  shipTo.value.note = orderNote.value ? orderNote.value.trim() : "";

  sessionStorage.setItem(
    STORAGE_KEYS.SHIPPING_ADDRESS,
    shippingAddress.value ? shippingAddress.value.trim() : ""
  );
  sessionStorage.setItem(
    STORAGE_KEYS.ORDER_NOTE,
    orderNote.value ? orderNote.value.trim() : ""
  );
};

const handlePlaceOrder = async () => {
  try {
    const orderRequestList = [];

    carts.value.forEach((sellerCart) => {
      console.log(sellerCart);

      const selectedItems = sellerCart.cart.filter((item) => item.checked);
      if (selectedItems.length === 0) {
        return;
      }
      const orderItems = selectedItems.map((item) => ({
        saleItemId: item.productId,
        price: item.productPrice,
        quantity: item.quantity,
        description:
          item.productBrandName +
          " " +
          item.productModel +
          " (" +
          item.productStorageGb +
          "GB," +
          item.productColor +
          ")",
      }));

      console.log(sellerCart.productSellerId);

      const isSellerSelect = sellerCart.cart.some((item) => item.checked);
      console.log(isSellerSelect);

      if (isSellerSelect) {
        orderRequestList.push({
          buyerId: userId.value,
          sellerId: sellerCart.productSellerId,
          shippingAddress: shippingAddress.value,
          orderNote: orderNote.value,
          orderItems,
          orderStatus: "COMPLETED",
        });
      }
    });

    if (orderRequestList.length === 0) {
      return;
    }

    console.log("📦 Order Payload:", JSON.stringify(orderRequestList, null, 2));

    const response = await createOrder(orderRequestList);

    // Check for 401 error
    if (response.error && response.error.status === 401) {
      userStore.clearUserInfo();
      userStore.setAccessToken(null);
      cartStore.setCart(null);
      saleOrderStore.setSaleOrderQuanatity(0);
      sessionStorage.clear();
      localStorage.clear();
      router.push({ name: "signin" });
      return;
    }

    if (response.error) {
      throw new Error(response.error);
    }

    saveFormData();

    orderStore.addOrder(response);

    carts.value.forEach((sellerCart, i) => {
      sellerCart.cart = sellerCart.cart.filter((item) => !item.checked);
    });

    carts.value = carts.value.filter(
      (sellerCart) => sellerCart.cart.length > 0
    );

    cartStore.setCart(carts.value);

    // shippingAddress.value = ''
    // orderNote.value = ''

    sessionStorage.removeItem("pageInfo");

    orderCreateSuccess();
  } catch (error) {
    hasError.value = error.message;
    console.error("Error placing order:", error);
  }
};

watch([shippingAddress, orderNote], () => {
  if (shippingAddress.value || orderNote.value) {
    saveFormData();
  }
});

const notiStore = useNotiStore();
const orderCreateSuccess = () => {
  notiStore.orderCreateSuccess = true;
  router.push({ name: "order" });
};

const isPlaceOrderDisabled = computed(() => {
  const hasNoItems = itemSelected.value.length === 0;
  const hasNoAddress =
    !shippingAddress.value || shippingAddress.value.trim() === "";
  return hasNoItems || hasNoAddress;
});
</script>

<template>
  <Breadcrumbs :crumbs="[{ label: 'Cart' }]" />
  <div class="w-full h-full px-4 pb-10 sm:px-6 md:px-10 lg:px-20 md:pb-20">
    <div
      v-if="!cartStore.carts && hasError !== '' && hasError"
      class="w-full h-full p-6 sm:p-8 md:p-12 flex justify-center items-center rounded-lg bg-red-100 border-1 border-red-300"
    >
      <p class="text-red-500 font-extralight text-sm sm:text-base">{{ hasError }}</p>
    </div>
    
    <!-- Header Section -->
    <div class="w-full flex flex-col lg:flex-row justify-between items-start gap-6 lg:gap-10">
      <div class="w-full flex flex-col gap-6 lg:flex-row justify-between">
        <!-- Cart Items Section -->
        <div class="w-full lg:flex-1">
          <h1 class="font-bold text-xl sm:text-2xl mb-4">
            Shopping Cart ({{ productLength }})
          </h1>

          <!-- Select All -->
          <div class="flex gap-2 sm:gap-3 items-center text-sm sm:text-base bg-white w-full p-3 sm:p-4 rounded-xl shadow-sm">
            <input
              type="checkbox"
              v-model="selectAll"
              @change="
                carts.forEach((cart) => {
                  cart.checkAll = selectAll;
                  cart.cart.forEach((item) => (item.checked = selectAll));
                })
              "
              class="checkbox checkbox-primary checkbox-sm sm:checkbox-md itbms-select-all"
            />
            <span class="font-medium">Select All</span>
          </div>

          <!-- Seller Carts -->
          <div v-for="(cart, i) in carts" :key="i">
            <div
              v-if="cart.cart.length > 0"
              class="flex flex-col mt-4 bg-white w-full p-3 sm:p-4 rounded-xl shadow-sm itbms-row"
            >
              <!-- Seller Header -->
              <div class="flex gap-2 sm:gap-3 items-center text-sm sm:text-base">
                <input
                  type="checkbox"
                  v-model="cart.checkAll"
                  @change="checkAllItem(i)"
                  class="checkbox checkbox-primary checkbox-sm sm:checkbox-md itbms-select-nickname"
                />
                <p class="font-bold text-sm sm:text-base itbms-nickname truncate">
                  {{ cart.productSellerNickname }}
                </p>
              </div>
              <hr class="my-3 text-base-300" />

              <!-- Cart Items -->
              <div
                v-for="item in cart.cart"
                :key="item.id"
                class="flex flex-col sm:flex-row gap-3 sm:gap-4 items-start sm:items-center justify-between mb-4 last:mb-0 itbms-item-row"
              >
                <!-- Left Section: Checkbox + Image + Description -->
                <div class="flex items-start sm:items-center gap-2 sm:gap-3 w-full sm:w-auto sm:flex-1">
                  <input
                    type="checkbox"
                    v-model="item.checked"
                    class="checkbox checkbox-primary checkbox-sm sm:checkbox-md mt-1 sm:mt-0 flex-shrink-0"
                  />
                  
                  <div class="flex gap-2 sm:gap-3 items-start sm:items-center flex-1 min-w-0">
                    <img
                      v-if="item.image"
                      :src="item.image"
                      alt="Product image"
                      class="w-12 h-12 sm:w-16 sm:h-16 rounded-lg sm:rounded-2xl object-cover flex-shrink-0"
                    />
                    <img
                      v-else
                      :src="noImage"
                      alt="Product image"
                      class="w-12 h-12 sm:w-16 sm:h-16 rounded-lg sm:rounded-2xl object-contain flex-shrink-0"
                    />
                    
                    <span class="itbms-item-description text-xs sm:text-sm lg:text-base leading-tight break-words">
                      {{ item.productBrandName + " " + item.productModel + " (" + item.productStorageGb + "GB," + item.productColor + ")" }}
                    </span>
                  </div>
                </div>

                <!-- Right Section: Quantity + Price + Delete -->
                <div class="flex gap-3 sm:gap-4 items-center justify-between sm:justify-end w-full sm:w-auto ml-auto">
                  <!-- Quantity Controls -->
                  <div class="flex items-center gap-1 sm:gap-2 flex-shrink-0">
                    <button
                      class="btn btn-xs sm:btn-sm itbms-dec-qty-button"
                      :disabled="item.quantity === 0"
                      @click="decreaseQty(item)"
                    >
                      -
                    </button>
                    <span class="itbms-item-quantity text-xs sm:text-sm font-medium min-w-[1.5rem] sm:min-w-[2rem] text-center">
                      {{ item.quantity }}
                    </span>
                    <button
                      class="btn btn-xs sm:btn-sm itbms-inc-qty-button"
                      :disabled="item.quantity === item.productQuantity"
                      @click="increaseQty(item)"
                    >
                      +
                    </button>
                  </div>

                  <!-- Price -->
                  <span class="price font-bold text-blue-600 text-xs sm:text-sm lg:text-base itbms-item-total-price whitespace-nowrap">
                    {{ (item.productPrice * item.quantity).toLocaleString() }} ฿
                  </span>
                  
                  <!-- Delete Button -->
                  <button
                    @click="removeCart(item.id, item.productId)"
                    class="text-black/20 hover:text-red-500 cursor-pointer text-lg sm:text-xl flex-shrink-0"
                  >
                    ×
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Summary Section -->
        <div class="flex flex-col h-fit bg-white w-full lg:w-80 xl:w-96 p-4 sm:p-5 rounded-xl shadow-sm sticky top-4">
          <div>
            <p class="font-bold text-lg sm:text-xl md:text-2xl mb-3 sm:mb-4">
              Cart Summary
            </p>
            <hr class="my-3 text-base-300" />

            <!-- Ship To Section -->
            <div class="mt-3 sm:mt-4 mb-3 sm:mb-4">
              <p class="font-bold text-base sm:text-lg mb-2">Ship To</p>
              <label class="text-xs sm:text-sm mb-1 block">
                <span class="font-bold">Address </span>
                <span class="text-gray-400">
                  (Address No, Street, Subdistrict, District, Province, Postal Code)
                </span>
              </label>
              <textarea
                v-model.trim="shippingAddress"
                class="itbms-shipping-address textarea textarea-bordered w-full h-20 sm:h-24 text-xs sm:text-sm resize-none"
                placeholder="123/45 Moo 6, T. Bangna, A. Bangna, Bangkok 10260"
              ></textarea>
            </div>

            <!-- Note Section -->
            <div class="mb-3 sm:mb-4">
              <p class="font-bold text-sm sm:text-base mb-2">Note</p>
              <textarea
                v-model.trim="orderNote"
                class="itbms-order-note textarea textarea-bordered w-full h-16 sm:h-20 text-xs sm:text-sm resize-none"
                placeholder="Additional instructions or requests"
              ></textarea>
            </div>

            <hr class="my-3 text-base-300" />

            <!-- Price Summary -->
            <div class="mt-3 space-y-2">
              <div class="flex justify-between itbms-total-orders-items text-sm sm:text-base">
                <p class="text-black/30">Total items</p>
                <p class="font-medium">{{ itemSelected.length }}</p>
              </div>
              <div class="flex justify-between itbms-total-order-price text-sm sm:text-base">
                <p class="text-black/30">Total price</p>
                <p class="font-bold text-blue-600">{{ totalPrice.toLocaleString() }} ฿</p>
              </div>
            </div>
          </div>
          
          <!-- Place Order Button -->
          <button
            class="mt-4 sm:mt-6 btn btn-primary w-full itbms-place-order-button text-sm sm:text-base"
            :disabled="isPlaceOrderDisabled"
            @click="handlePlaceOrder"
          >
            Place order
          </button>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <dialog class="modal" :open="showDeleteModal" v-if="showDeleteModal">
      <div class="modal-box max-w-sm sm:max-w-md">
        <h3 class="text-base sm:text-lg font-bold">Delete Confirmation</h3>
        <p class="itbms-message py-3 sm:py-4 text-sm sm:text-base">
          Do you want to remove the sale item from the cart?
        </p>
        <div class="modal-action gap-2">
          <form method="dialog">
            <button
              @click="showDeleteModal = false"
              class="btn btn-error btn-sm sm:btn-md itbms-cancel-button"
            >
              Cancel
            </button>
          </form>
          <button
            @click="removeCart(deleteTargetId, deleteTargetPid)"
            class="btn btn-success btn-sm sm:btn-md itbms-confirm-button"
          >
            Confirm
          </button>
        </div>
      </div>
    </dialog>

    <!-- Warning Modal -->
    <dialog class="modal" :open="showWarningModal" v-if="showWarningModal">
      <div class="modal-box max-w-sm sm:max-w-md">
        <h3 class="text-base sm:text-lg font-bold">Cannot Delete</h3>
        <p class="itbms-message py-3 text-sm sm:text-base">This cart is not found</p>
        <div class="modal-action">
          <form method="dialog">
            <button
              @click="showWarningModal = false"
              class="btn btn-error btn-sm sm:btn-md itbms-cancel-button"
            >
              Cancel
            </button>
          </form>
        </div>
      </div>
    </dialog>
  </div>
</template>

<style scoped>
.cart-item {
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}
</style>
