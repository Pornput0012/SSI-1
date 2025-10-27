<script setup>
import { getProfile, logout } from "@/services/userService";
import { useUserStore } from "@/stores/UserStore";
import { computed, onMounted, ref, watchEffect } from "vue";
import { RouterLink, useRouter } from "vue-router";
import CartIcon from "./CartIcon.vue";
import { useCartStore } from "@/stores/CartStore";
import { getAllCart } from "@/services/CartService";
import { Icon } from "@iconify/vue";
import { useSaleOrderStore } from "@/stores/saleOrderStore";
import { getOrdersSeller } from "@/services/OrderService";

const props = defineProps({
  qty: {
    type: Number,
  },
});

const openHamburger = ref(false);

function setOpenHamburger() {
  openHamburger.value = !openHamburger.value;
}
const router = useRouter();
const userStore = useUserStore();
const nickName = computed(() => userStore.getNickName());

const cartStore = useCartStore();
const saleOrderStore = useSaleOrderStore();
const menu = [
  {
    to: "home",
    text: "Home",
  },
  {
    to: "sale-items",
    text: "Sale Items",
  },
  {
    to: "brands",
    text: "Manage Brand",
    seller: true,
  },
  {
    to: "sale-items-list",
    text: "My Sale Items",
    seller: true,
  },
];

const handleLogout = async () => {
  const res = await logout();
  if (res.success) {
    userStore.clearUserInfo();
    userStore.setAccessToken(null);
    cartStore.setCart(null);
    saleOrderStore.setSaleOrderQuanatity(0);
    sessionStorage.clear();
    localStorage.clear();

    router.push({ name: "sale-items" });
  } else {
    alert("Logout Not Successful...");
  }
};

onMounted(async () => {
  const userInfo = await getProfile();
  if (userInfo.logined) {
    userStore.setUserInfo(userInfo?.data);
  }
});
watchEffect(async () => {
  if (userStore.getUserId() && !cartStore.carts) {
    try {
      const userId = userStore.getUserId();
      if (!userId) {
        console.log("No user ID available");
        return;
      }

      const response = await getAllCart(userId);

      if (!response) {
        console.log("No response from getAllCart");
        return;
      }

      if (response.error) {
        throw new Error(error);
      }

      cartStore.setCart(response);
    } catch (error) {
      console.error("Error loading cart:", error?.message || "Unknown error");
    }
  }
  const userType = userStore.getUserType();
  if (userType == "SELLER" && userStore.getUserId()) {
    try {
      const userId = userStore.getUserId();

      if (!userId) {
        console.log("No user ID available");
        return;
      }

      const response = await getOrdersSeller(userId, {
        page: 0,
        size: 10,
        sortDirection: "DESC",
      });

      if (!response) {
        console.log("No response from getOrdersSeller");
        return;
      }

      if (response.error) {
        throw new Error(error);
      }

      saleOrderStore.setSaleOrderQuanatity(response);
    } catch (error) {
      console.error("Error loading cart:", error?.message || "Unknown error");
    }
  }
});
const productLength = computed(() => {
  let countItem = 0;
  if (cartStore.carts) {
    cartStore.carts.forEach((sellerCart) => {
      sellerCart.cart.forEach((item) => {
        console.log(item);
        
        countItem+=item.quantity;
        console.log(countItem);
        
      });
    });
  }
  return countItem;
});
</script>

<template>
  <div class="w-full h-full flex-col hidden md:block">
    <div class="navbar bg-base-100 shadow-sm px-6 py-4 flex-col gap-4">
      <div class="w-full flex justify-between items-center text-lg">
        <RouterLink
          :to="{ name: 'home' }"
          class="font-bold transition-all duration-200 hover:tracking-wider hover:text-purple-800 flex items-center justify-center"
        >
          <img src="/logo.png" class="h-14 aspect-square" alt="logo" />
          ITB MShop
        </RouterLink>
        <div class="flex items-center gap-3">
          <div class="flex items-center gap-2" v-if="!nickName">
            <router-link :to="{ name: 'register' }">
              <button
                class="itbms-register-button btn bg-base-100 text-indigo-700 px-4 py-1 shadow-sm"
              >
                Register
              </button>
            </router-link>
            <router-link
              :to="{ name: 'signin' }"
              class="btn btn-primary px-4 py-1"
              >Login</router-link
            >
          </div>
          <RouterLink
            v-if="nickName"
            class="itbms-sale-order-button mr-2"
            :to="{ name: 'order' }"
            >Your orders</RouterLink
          >
          <RouterLink v-if="productLength" :to="{ name: 'cart' }">
            <CartIcon v-if="nickName && cartStore.carts" :qty="productLength" />
            <CartIcon v-else :qty="0" />
          </RouterLink>
          <button v-else class="opacity-60">
            <CartIcon v-if="nickName && cartStore.carts" :qty="productLength" />
            <CartIcon v-else :qty="0" />
          </button>
          <div class="flex items-center gap-2 mr-3" v-if="!nickName"></div>
          <div v-if="nickName" class="flex items-center justify-center gap-4">
            <p>
              Hello, <span class="itbms-nickname">{{ nickName }}</span>
            </p>
            <router-link
              v-if="userStore.getUserInfo().userType?.toUpperCase() == 'SELLER'"
              :to="{ name: 'sale-orders' }"
              class="cursor-pointer relative"
            >
              <Icon
                icon="flowbite:shopping-bag-outline"
                width="24"
                height="24"
              />
              <span
                v-if="saleOrderStore.getSaleOrderQuanatity() > 0"
                class="itbms-bag-quantity absolute -top-2 -right-2 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center"
              >
                {{ saleOrderStore.getSaleOrderQuanatity() }}
              </span>
            </router-link>
            <div class="dropdown dropdown-end">
              <!-- Avatar button -->
              <label tabindex="0" class="btn btn-ghost btn-circle avatar">
                <div class="w-10 rounded-full">
                  <img
                    src="https://img.daisyui.com/images/profile/demo/yellingcat@192.webp"
                    alt="User Avatar"
                  />
                </div>
              </label>

              <!-- Dropdown menu -->
              <ul
                tabindex="0"
                class="dropdown-content menu p-2 shadow bg-base-100 rounded-box w-48 mt-2"
              >
                <li>
                  <router-link class="itbms-profile" :to="{ name: 'profile' }"
                    >Profile</router-link
                  >
                </li>
                <li>
                  <button
                    @click="handleLogout"
                    class="w-full text-left itbms-logout"
                  >
                    Logout
                  </button>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div
      class="bg-base-100 border-t-1 border-base-300 shadow-sm px-6 py-2 flex gap-10"
    >
      <RouterLink
        v-if="userStore.getUserType() === 'SELLER'"
        active-class="border-b-1"
        v-for="(m, i) in menu"
        :to="{ name: m.to }"
        class="font-light hover:text-purple-700 transition-colors"
        >{{ m.text }}</RouterLink
      >
      <RouterLink
        v-else
        active-class="border-b-1"
        v-for="(m, i) in menu.filter((m) => m.seller !== true)"
        :to="{ name: m.to }"
        class="font-light hover:text-purple-700 transition-colors"
        >{{ m.text }}</RouterLink
      >
    </div>
  </div>
  <div
    class="w-full h-full navbar bg-base-100 shadow-sm md:hidden flex justify-between items-center"
  >
    <div>
      <div>
        <button
          @click="setOpenHamburger"
          tabindex="0"
          role="button"
          class="btn btn-ghost btn-circle"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-5 w-5"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M4 6h16M4 12h16M4 18h7"
            />
          </svg>
        </button>
      </div>
    </div>

    <div class="flex gap-2 items-center">
      <RouterLink v-if="productLength" :to="{ name: 'cart' }">
        <CartIcon v-if="nickName" :qty="productLength" />
        <CartIcon v-else :qty="0"
      /></RouterLink>

      <button v-else class="opacity-60">
        <CartIcon v-if="nickName" :qty="productLength" />
        <CartIcon v-else :qty="0" />
      </button>

      <div class="dropdown dropdown-bottom dropdown-end">
        <div tabindex="0" role="button" class="btn btn-ghost btn-circle">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-5 w-5"
            viewBox="0 0 16 16"
          >
            <path
              fill="currentColor"
              fill-rule="evenodd"
              d="M10 4.5a2 2 0 1 1-4 0a2 2 0 0 1 4 0m1.5 0a3.5 3.5 0 1 1-7 0a3.5 3.5 0 0 1 7 0m-9 8c0-.204.22-.809 1.32-1.459C4.838 10.44 6.32 10 8 10s3.162.44 4.18 1.041c1.1.65 1.32 1.255 1.32 1.459a1 1 0 0 1-1 1h-9a1 1 0 0 1-1-1m5.5-4c-3.85 0-7 2-7 4A2.5 2.5 0 0 0 3.5 15h9a2.5 2.5 0 0 0 2.5-2.5c0-2-3.15-4-7-4"
              clip-rule="evenodd"
            />
          </svg>
        </div>
        <ul
          tabindex="0"
          class="dropdown-content menu bg-base-100 rounded-box z-1 w-52 p-2 shadow-sm"
        >
          <RouterLink
            :to="{ name: userStore.getUserId() ? 'profile' : 'register' }"
            ><li>
              <a>{{ userStore.getUserId() ? "Profile" : "Register" }} </a>
            </li></RouterLink
          >
          <RouterLink v-if="!userStore.getUserId()" :to="{ name: 'signin' }"
            ><li><a>Login</a></li></RouterLink
          >
          <button v-else @click="handleLogout">
            <li><a>Logout</a></li>
          </button>
        </ul>
      </div>
    </div>
  </div>

  <div class="md:hidden">
    <div
      v-if="openHamburger"
      class="fixed top-0 left-0 w-[80%] h-full z-50 bg-base-100 shadow-lg flex flex-col p-4"
    >
      <button @click="setOpenHamburger" class="btn btn-ghost btn-sm ml-auto">
        X
      </button>
      <div class="flex flex-col">
        <RouterLink
          v-if="userStore.getUserType() === 'SELLER'"
          active-class="border-b-1"
          v-for="(m, i) in menu"
          :to="{ name: m.to }"
          class="mb-2 hover:text-purple-600 transition-colors w-fit"
          >{{ m.text }}</RouterLink
        >
        <RouterLink
          v-else
          active-class="border-b-1"
          v-for="(m, i) in menu.filter((m) => m.seller !== true)"
          :to="{ name: m.to }"
          class="mb-2 hover:text-purple-600 transition-colors w-fit"
          >{{ m.text }}</RouterLink
        >

        <RouterLink
          active-class="border-b-1"
          :to="{ name: 'order' }"
          class="mb-2 hover:text-purple-600 transition-colors w-fit"
          >Your orders</RouterLink
        >
        <RouterLink
          v-if="userStore.getUserType() === 'SELLER'"
          active-class="border-b-1"
          :to="{ name: 'sale-orders' }"
          class="mb-2 hover:text-purple-600 transition-colors w-fit"
          >Sale order</RouterLink
        >
      </div>
    </div>
  </div>
</template>

<style scoped></style>
