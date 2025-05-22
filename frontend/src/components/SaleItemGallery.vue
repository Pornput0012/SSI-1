<script setup>
import { computed, onMounted, ref, watchEffect } from "vue";
import ContainerList from "./ContainerList.vue";
import SaleItemGalleryModel from "./SaleItemGalleryModel.vue";

const props = defineProps({
  saleItems: {
    type: [Array, null],
    required: true,
  },

  pageInfo: {
    type: Object,
  },

  hasError: String,
});

const emit = defineEmits(["pageChanged"]);

// ตั้งค่าหน้าปัจจุบัน และจำนวนหน้าที่จะแสดงใน pagination
const currentPage = ref(1);
const maxVisiblePages = 6; // แสดงปุ่มไม่เกิน 6 หน้าใน pagination

function changPage(i) {
  currentPage.value = i
  emit("pageChanged",currentPage.value);
}

// คำนวณหน้าที่จะเริ่มต้นแสดงใน pagination
const startPage = computed(() => {
  const end = currentPage.value + maxVisiblePages - 1;

  // ถ้าเลขหน้าสุดท้ายเกิน totalPages ให้ขยับ start กลับ
  if (props.pageInfo && end > props.pageInfo.totalPages) {
    return Math.max(1, props.pageInfo.totalPages - maxVisiblePages + 1);
  }

  return currentPage.value;
});

// คำนวณหน้าสุดท้ายที่จะแสดงใน pagination
const endPage = computed(() => {
  if (!props.pageInfo) return 1;

  // เลือกค่าน้อยสุดระหว่าง end page ที่ควรจะแสดง กับ totalPages จริง
  return Math.min(
    startPage.value + maxVisiblePages - 1,
    props.pageInfo.totalPages
  );
});

// สร้าง array ของหมายเลขหน้า เช่น [1, 2, 3, 4, 5, 6]
const pageNumbers = computed(() => {
  const pages = [];
  for (let i = startPage.value; i <= endPage.value; i++) {
    pages.push(i);
  }
  return pages;
});

// เปลี่ยนไปหน้าถัดไป ถ้ายังไม่ถึงหน้าสุดท้าย
function nextPage() {
  if (currentPage.value < props.pageInfo.totalPages) {
    currentPage.value++;
    changPage(currentPage.value)
  }
}

// เปลี่ยนไปหน้าก่อนหน้า ถ้ายังไม่ใช่หน้าแรก
function prevPage() {
  if (currentPage.value > 1) {
    currentPage.value--;
    changPage(currentPage.value)
  }
}
</script>

<template>
  <div>
    <!-- error message -->
    <div
      v-if="!saleItems && hasError !== '' && hasError"
      class="w-full h-full p-12 flex justify-center items-center rounded-lg bg-red-100 border-1 border-red-300"
    >
      <p class="text-red-500 font-extralight">{{ hasError }}</p>
    </div>
    <!-- loader -->
    <div
      v-else-if="!saleItems"
      class="w-full h-full justify-center items-center grid grid-cols-1 gap-6 sm:grid-cols-2 md:grid-cols-3 md:gap-6 lg:grid-cols-4 lg:gap-8 xl:grid-cols-5 xl:gap-10"
    >
      <div v-for="i in 5" class="flex w-full flex-col gap-4">
        <div class="skeleton h-32 w-full"></div>
        <div class="skeleton h-4 w-28"></div>
        <div class="skeleton h-4 w-full"></div>
        <div class="skeleton h-4 w-full"></div>
      </div>
    </div>
    <!-- show sale item -->
    <ContainerList
      v-else-if="saleItems && saleItems.length > 0"
      :items="saleItems"
      class="w-full h-full justify-center items-center grid grid-cols-1 gap-4 sm:grid-cols-2 md:grid-cols-3 md:gap-6 lg:grid-cols-4 lg:gap-8 xl:grid-cols-5 xl:gap-10"
    >
      <template v-slot="{ itemInList }">
        <SaleItemGalleryModel
          :brand="itemInList.brandName"
          :model="itemInList.model"
          :price="itemInList.price"
          :ram-gb="itemInList.ramGb"
          :storage-gb="itemInList.storageGb"
          :color="itemInList.color"
          :id="itemInList.id"
        />
      </template>
    </ContainerList>

    <div
      v-if="pageInfo && pageInfo.totalPages !== 1"
      class="join mt-7 w-full px-auto"
    >
      <button
        class="join-item btn"
        @click="changPage(1)"
        :disabled="currentPage === 1"
      >
        First
      </button>
      <button
        class="join-item btn"
        @click="prevPage"
        :disabled="currentPage === 1"
      >
        Prev
      </button>

      <button
        v-for="i in pageNumbers"
        :key="i"
        class="join-item btn"
        :class="{ 'btn-active': currentPage === i }"
        @click="changPage(i)"
      >
        {{ i }}
      </button>

      <button
        class="join-item btn"
        @click="nextPage"
        :disabled="currentPage === pageInfo.totalPages"
      >
        Next
      </button>
      <button
        class="join-item btn"
        @click="changPage(pageInfo.totalPages)"
        :disabled="currentPage === pageInfo.totalPages"
      >
        Last
      </button>
    </div>
    <!-- no sale item -->
    <div
      v-if="saleItems && saleItems.length === 0"
      class="w-full h-full p-12 flex justify-center items-center rounded-lg bg-purple-200 border-1 border-purple-400"
    >
      <p class="text-purple-700 font-medium">no sale item</p>
    </div>
  </div>
</template>

<style scoped></style>
