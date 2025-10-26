<script setup>
import noImage from "@/assets/No_image_available.png";
import router from "@/router";
import { getImageByFileName } from "@/services/SaleItemService";
import { useCartStore } from "@/stores/CartStore";
import { useUserStore } from "@/stores/UserStore";
import { useSaleOrderStore } from "@/stores/saleOrderStore";
import { computed, onMounted, ref } from "vue";
import { addItemToCart } from "@/services/CartService";
import { useNotiStore } from "@/stores/NotiStore";
import { useToastStore } from "@/stores/ToastStore";
const notiStore = useNotiStore();
const cartStore = useCartStore();
const userStore = useUserStore();
const saleOrderStore = useSaleOrderStore();
const props = defineProps({
  id: {
    type: Number,
    required: true,
  },
  brand: {
    type: String,
    required: true,
  },
  model: {
    type: String,
    required: true,
  },
  ramGb: Number,
  storageGb: Number,
  price: {
    type: Number,
    required: true,
  },
  color: String,
  fileName: {
    type: Object,
    required: false,
  },

  sellerId: {
    type: Number,
    required: true,
  },
  quantity: {
    type: Number,
    required: true,
  },
});

const hasError = ref("");
const userNickname = computed(() => userStore.getNickName());
const userId = computed(() => userStore.getUserId());

const imageUrl = ref(null);

const quantityInCart = computed(() => {
  return cartStore.getQuantityByProductId(props.id) || 0;
});

const availableQuantity = computed(() => {
  return props.quantity - quantityInCart.value;
});

const isAvailableBuy = computed(() => {
  return availableQuantity.value <= 0;
});

const isSoldOut = computed(() => {
  return props.quantity <= 0;
});

const loadImages = async () => {
  try {
    if (props.fileName?.fileName) {
      const image_url = await getImageByFileName(props.fileName.fileName);
      imageUrl.value = image_url;
    } else {
      imageUrl.value = noImage;
    }
  } catch (error) {
    console.error(`Failed to load image:`, error);
    imageUrl.value = noImage;
  }
};

onMounted(async () => {
  await loadImages();
});

const toastStore = useToastStore();

const addToCart = async () => {
  if (!userNickname.value) {
    router.push({ name: "signin" });
    return;
  }

  if (userId.value === props.sellerId) {
    notiStore.addToCartFail = true;
    return;
  }

  try {
    const data = {
      userId: userId.value,
      productId: props.id,
      imageFileName: props.fileName?.fileName || "",
      quantity: 1,
    };

    const response = await addItemToCart(data);

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
      notiStore.addToCartFail = true;
      throw new Error(response.error);
    }

    cartStore.addToCart(response);
    toastStore.showToast("✓ Added to cart!", "success");
  } catch (error) {
    console.error("Error adding to cart:", error);
    toastStore.showToast("✗ Failed to add to cart", "error");
    hasError.value = error.message || "Failed to add item to cart";
  }
};
</script>

<template>
  <RouterLink
    class="itbms-row"
    :to="{ name: 'sale-item-detail', params: { id } }"
  >
    <div
      class="card w-full h-72 bg-base-100 shadow-md rounded-xl flex flex-col md:h-80 lg:h-[350px] xl:h-[360px] hover:shadow-xl hover:scale-[1.02] transition duration-300 ease-in-out"
      :class="{ 'opacity-75': isSoldOut }"
    >
      <div class="relative">
        <div
          class="w-full overflow-hidden rounded-t-xl flex justify-center items-center shrink-0 h-36 md:h-40 lg:h-48 xl:h-52"
        >
          <img
            v-if="imageUrl"
            :src="imageUrl"
            alt="Product image"
            class="w-full h-full object-center"
            :class="'object-cover'"
          />
          <img
            v-else
            :src="noImage"
            alt="Product image"
            class="w-full h-full object-center"
            :class="'object-contain'"
          />
        </div>

        <!-- ✅ Sold Out Overlay -->
        <div
          v-if="isSoldOut"
          class="absolute inset-0 flex items-center justify-center rounded-t-xl"
        >
          <div
            class="bg-red-500 text-white font-bold px-6 py-2 rounded-lg shadow-lg text-lg md:text-xl"
          >
            SOLD OUT
          </div>
        </div>
      </div>

      <div class="h-px bg-gray-200 shadow-sm"></div>

      <div
        class="flex-[1] flex flex-col justify-between w-full h-full p-3 text-[10px] sm:text-xs sm:p-3 lg:p-4"
      >
        <div class="pt-2 pb-0">
          <p class="itbms-brand text-gray-500 font-medium">{{ brand }}</p>
          <p class="itbms-model font-semibold">{{ model }}</p>

          <div class="flex items-center font-light">
            <p v-if="ramGb" class="itbms-ramGb">{{ ramGb }}</p>
            <span v-else class="itbms-ramGb">-</span>
            <p>/</p>
            <div v-if="storageGb" class="flex gap-1">
              <p class="itbms-storageGb">{{ storageGb }}</p>
            </div>
            <p v-else class="itbms-storageGb mx-1">-</p>
            <p class="itbms-storageGb-unit">GB</p>
          </div>

          <p v-if="color" class="itbms-color text-gray-600">
            color : {{ color }}
          </p>

          <!-- ✅ แสดง Available Quantity แบบมีสี -->
          <p class="itbms-quantity-status text-xs mt-1">
            <span v-if="!isSoldOut" class="text-gray-500">
              Available:
              <span
                class="font-semibold"
                :class="{
                  'text-green-600': availableQuantity > 5,
                  'text-orange-500':
                    availableQuantity > 0 && availableQuantity <= 5,
                }"
              >
                {{ Math.max(0, availableQuantity) }}
              </span>
            </span>
            <span v-else class="text-red-500 font-semibold"> Sold Out </span>
          </p>
        </div>
        <div class="pt-1 flex justify-between items-center">
          <p
            class="itbms-price text-[11px] font-semibold text-indigo-700 sm:text-sm"
          >
            <span class="itbms-price-unit">Bath </span
            >{{ price.toLocaleString() }}
          </p>
          <button
            @click.prevent="addToCart"
            :disabled="isAvailableBuy"
            class="itbms-add-to-cart-button ml-1 btn btn-sm btn-primary text-xs hover:shadow-xl hover:scale-[1.02] transition duration-300 ease-in-out"
            :class="{
              'opacity-50 cursor-not-allowed': isAvailableBuy,
            }"
          >
            <svg
              class=""
              xmlns="http://www.w3.org/2000/svg"
              width="20"
              height="20"
              viewBox="0 0 24 24"
            >
              <path
                fill="currentColor"
                d="M19 20c0 1.11-.89 2-2 2a2 2 0 0 1-2-2c0-1.11.89-2 2-2a2 2 0 0 1 2 2M7 18c-1.11 0-2 .89-2 2a2 2 0 0 0 2 2c1.11 0 2-.89 2-2s-.89-2-2-2m.2-3.37l-.03.12c0 .14.11.25.25.25H19v2H7a2 2 0 0 1-2-2c0-.35.09-.68.24-.96l1.36-2.45L3 4H1V2h3.27l.94 2H20c.55 0 1 .45 1 1c0 .17-.05.34-.12.5l-3.58 6.47c-.34.61-1 1.03-1.75 1.03H8.1zM8.5 11H10V9H7.56zM11 9v2h3V9zm3-1V6h-3v2zm3.11 1H15v2h1zm1.67-3H15v2h2.67zM6.14 6l.94 2H10V6z"
              />
            </svg>
            Add to Cart
          </button>
        </div>
      </div>
    </div>
  </RouterLink>
</template>

<style scoped></style>
