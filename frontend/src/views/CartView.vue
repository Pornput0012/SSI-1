<script setup>
import Breadcrumbs from "@/components/Breadcrumbs.vue";
import { computed, ref } from "vue";

// Mockup ข้อมูลสินค้า
const carts = ref([
  { id: 1, name: "iPhone 15", price: 32900, quantity: 1, checked: false },
  { id: 2, name: "AirPods Pro 2", price: 8990, quantity: 2, checked: true },
  { id: 3, name: "iPad Air", price: 19900, quantity: 1, checked: false },
]);

const selectAll = ref(false);

// กด selectAll ให้เลือกทุกอัน
const toggleSelectAll = () => {
  carts.value.forEach((item) => (item.checked = selectAll.value));
};

// จำนวนที่เลือก
const itemSelected = computed(() => {
  return carts.value.filter((item) => item.checked);
});

// ราคารวมของที่เลือก
const totalPrice = computed(() => {
  return itemSelected.value.reduce(
    (sum, item) => sum + item.price * item.quantity,
    0
  );
});

// เพิ่ม/ลดจำนวน
const increaseQty = (index) => {
  carts.value[index].quantity++;
};
const decreaseQty = (index) => {
  if (carts.value[index].quantity > 1) {
    carts.value[index].quantity--;
  }
};
</script>

<template>
  <Breadcrumbs :crumbs="[{ label: 'Cart' }]" />
  <div class="w-full h-full px-10 pb-10 md:px-20 md:pb-20">
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
              class="checkbox checkbox-primary"
              v-model="selectAll"
              @change="toggleSelectAll"
            />
            Select All
          </div>

          <div>
            <div class="flex flex-col mt-4 bg-white w-full p-4 rounded-xl">
              <!-- ร้านค้า -->
              <div class="flex gap-3 items-center text-md">
                <input
                  type="checkbox"
                  v-model="selectAll"
                  class="checkbox checkbox-primary"
                  @change="toggleSelectAll"
                />
                <p class="font-bold">{{ "Apple Official Store" }}</p>
              </div>
              <hr class="my-3 text-base-300" />

              <!-- รายการสินค้าในร้าน -->
              <div
                v-for="(item, index) in carts"
                :key="item.id"
                class="flex items-center justify-between mb-6"
              >
                <div class="flex items-center gap-6">
                  <input
                    type="checkbox"
                    v-model="item.checked"
                    class="checkbox checkbox-primary"
                  />
                  <img
                    src="../assets/bg.jpg"
                    class="w-16 aspect-square"
                    alt=""
                  />
                </div>

                <span class="product-name w-40">{{ item.name }}</span>

                <div class="quantity flex items-center gap-2">
                  <button class="btn btn-sm" @click="decreaseQty(index)">
                    -
                  </button>
                  <span>{{ item.quantity }}</span>
                  <button class="btn btn-sm" @click="increaseQty(index)">
                    +
                  </button>
                </div>

                <span class="price font-bold text-blue-600">
                  {{ item.price * item.quantity }} ฿
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
            class=" mt-6 btn btn-primary w-full"
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
