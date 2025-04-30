<script setup></script>

<template>
  <div class="flex-col">
      <div class="navbar bg-base-100 shadow-sm px-6 py-4 flex-col gap-4">
        <div class="w-full flex justify-end items-center">
          <label class="w-fit h-8 input input-xs">
            <input type="search" class="grow" placeholder="Search..." />
            <button class="cursor-pointer">
              <svg
                class="h-[1em] opacity-50"
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 24 24"
              >
                <g
                  stroke-linejoin="round"
                  stroke-linecap="round"
                  stroke-width="2.5"
                  fill="none"
                  stroke="currentColor"
                >
                  <circle cx="11" cy="11" r="8"></circle>
                  <path d="m21 21-4.3-4.3"></path>
                </g>
              </svg>
            </button>
          </label>
        </div>
        <div class="w-full flex justify-between">
          <p class="font-bold">ITB MShop</p>
          <div>
            <button class="btn btn-primary px-4 text-white bg-purple-700">Log in</button>
          </div>
        </div>
      </div>
      <div class="bg-base-100 border-t-2 border-base-300 shadow-sm px-6 py-1 ">
        <RouterLink :to="{name : 'sale-items'}" class="font-light hover:text-purple-700">Products</RouterLink>
      </div>
  </div>
</template>

<style scoped></style>
