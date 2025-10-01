<script setup>
import Breadcrumbs from "@/components/Breadcrumbs.vue";
import { getAllCart } from "@/services/CartService";
import { useCartStore } from "@/stores/CartStore";
import { useUserStore } from "@/stores/UserStore";
import noImage from "@/assets/No_image_available.png";
import { computed, onMounted, ref, watch, watchEffect } from "vue";
import { getImageByFileName } from "@/services/SaleItemService";

const userStore = useUserStore();
const cartStore = useCartStore();
const userId = computed(() => userStore.getUserId());
// Mockup ข้อมูลสินค้า
const carts = ref([]);

const hasError = ref("");

onMounted(async () => {
  try {
    if (cartStore.carts) {
      carts.value = cartStore.carts.map((ele) => {
        return {
          ...ele,
          checkAll: false,
        };
      });
    } else {
      console.log("Fetching cart for user:", userId.value);

      const response = await getAllCart(userId.value);

      if (response.error) {
        throw new Error(response.error);
      }

      carts.value = response?.map((ele) => {
        return {
          ...ele,
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
  carts.value.filter((cart, i) => {
    carts.value[i].checkAll = cart?.cart.every((ele2) => {
      return ele2.checked === true;
    });
    console.log(i + "index : " + carts.value[i].checkAll);
  });
});

const totalPrice = computed(() => {
  return itemSelected.value.reduce(
    (sum, item) => sum + item.productPrice * item.quantity,
    0
  );
});

// เพิ่ม/ลดจำนวน
const updateQuantity = (item, newQuantity) => {
  // ป้องกันไม่ให้ quantity น้อยกว่า 1
  item.quantity = Math.max(1, newQuantity);
};

const increaseQty = (item) => {
  updateQuantity(item, item.quantity + 1);
};

const decreaseQty = (item) => {
  updateQuantity(item, item.quantity - 1);
};
</script>

<template>
  <Breadcrumbs :crumbs="[{ label: 'Cart' }]" />
  <div class="w-full h-full px-10 pb-10 md:px-20 md:pb-20">
    <div
      v-if="!cartStore.carts && hasError !== '' && hasError"
      class="w-full h-full p-12 flex justify-center items-center rounded-lg bg-red-100 border-1 border-red-300"
    >
      <p class="text-red-500 font-extralight">{{ hasError }}</p>
    </div>
    <!-- Header Section -->
    <div class="w-full flex flex-col lg:flex-row justify-between items-center">
      <div class="w-full flex justify-between">
        <div class="w-full mx-8">
          <h1 class="font-bold text-2xl mb-4 sm:mb-6 md:mb-10 lg:mb-0">
            Shopping Cart ({{ carts.length }})
          </h1>

          <div
            class="flex gap-3 items-center text-md mt-4 bg-white w-full p-4 rounded-xl"
          >
            <input
              type="checkbox"
              class="checkbox checkbox-primary itbms-select-all"
              v-model="selectAll"
              :checked="selectSellerState"
            />
            Select All
          </div>

          <div>
            <div
              v-for="(cart, i) in carts"
              class="flex flex-col mt-4 bg-white w-full p-4 rounded-xl itbms-row"
            >
              <!-- ร้านค้า -->
              <div class="flex gap-3 items-center text-md">
                <input
                  type="checkbox"
                  :checked="cart.checkAll || selectAll"
                  @change="cart.checkAll = !cart.checkAll"
                  class="checkbox checkbox-primary itbms-select-nickname"
                />

                <p class="font-bold itbms-nickname">
                  {{ cart.productSellerNickname }}
                </p>
              </div>
              <hr class="my-3 text-base-300" />

              <!-- รายการสินค้าในร้าน -->
              <div
                v-for="item in cart.cart"
                :key="item.id"
                class="flex items-center justify-between mb-6 itbms-item-row"
              >
                <div class="flex items-center gap-6">
                  <input
                    type="checkbox"
                    v-model="item.checked"
                    :checked="selectAll || cart.checkAll"
                    class="checkbox checkbox-primary"
                  />
                  <img
                    v-if="item.image"
                    :src="item.image"
                    alt="Product image"
                    class="w-16 aspect-square"
                    :class="'object-cover'"
                  />
                  <img
                    v-else
                    :src="noImage"
                    alt="Product image"
                    class="w-16 aspect-square"
                    :class="'object-contain'"
                  />
                </div>

                <span class="itbms-item-description w-40">{{
                  item.productModel
                }}</span>

                <div class="quantity flex items-center gap-2">
                  <button
                    class="btn btn-sm itbms-dec-qty-button"
                    @click="decreaseQty(item)"
                  >
                    -
                  </button>

                  <span class="itbms-item-quantity">{{ item.quantity }}</span>
                  <button
                    class="btn btn-sm itbms-inc-qty-button"
                    @click="increaseQty(item)"
                  >
                    +
                  </button>
                </div>

                <span
                  class="price font-bold text-blue-600 itbms-item-total-price"
                >
                  {{ item.productPrice * item.quantity }} ฿
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- Summary -->
        <div class="flex flex-col h-fit bg-white w-80 p-4 rounded-xl">
          <div>
            <p class="font-bold text-2xl mb-4 sm:mb-6 md:mb-10 lg:mb-0">
              Cart Summary
            </p>
            <hr class="my-1 text-base-300" />
            <div class="mt-3">
              <div class="flex justify-between">
                <p class="text-black/30">Total items</p>
                <p>{{ itemSelected.length }}</p>
              </div>
              <div class="flex justify-between">
                <p class="text-black/30">Total price</p>
                <p>{{ totalPrice.toLocaleString() }} ฿</p>
              </div>
            </div>
          </div>
          <button
            class="mt-6 btn btn-primary w-full"
            :disabled="itemSelected.length === 0"
          >
            Place order
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.cart-item {
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}
</style>
