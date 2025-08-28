<script setup>
import { ref } from "vue";

const openHamburger = ref(false);

function setOpenHamburger() {
  openHamburger.value = !openHamburger.value;
}

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
  },
];
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
        <div class="flex gap-2">
          <RouterLink :to="{ name: 'register' }">
            <button
              class="itbms-register-button btn bg-base-100 text-indigo-700 px-4 py-1 shadow-sm"
            >
              Register
            </button>
          </RouterLink>
          <div class="border border-l-1 border-base-300"></div>
          <router-link :to="{ name: 'login' }" class="btn btn-primary px-4 py-1"
            >Login</router-link
          >
        </div>
      </div>
    </div>
    <div
      class="bg-base-100 border-t-1 border-base-300 shadow-sm px-6 py-2 flex gap-10"
    >
      <RouterLink
        active-class="border-b-1"
        v-for="(m, i) in menu"
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
    <!-- <div>
      <RouterLink :to="{ name: 'home' }" class="btn btn-ghost text-xl">
        ITB MShop</RouterLink
      >
    </div> -->
    <div class="">
      <button class="btn btn-ghost btn-circle">
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
            d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
          />
        </svg>
      </button>
      <button class="btn btn-ghost btn-circle">
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
      </button>
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
          active-class="border-b-1"
          :to="{ name: 'home' }"
          class="mb-2 hover:text-purple-600 transition-colors w-fit"
          >Home</RouterLink
        >
        <RouterLink
          active-class="border-b-1"
          :to="{ name: 'sale-items' }"
          class="mb-2 hover:text-purple-600 transition-colors w-fit"
          >Products</RouterLink
        >
        <RouterLink
          active-class="border-b-1"
          to="l"
          class="mb-2 hover:text-purple-600 transition-colors w-fit"
          >Log in</RouterLink
        >
      </div>
    </div>
  </div>
</template>

<style scoped></style>
