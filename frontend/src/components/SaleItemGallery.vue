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

const currentPage = ref(props.pageInfo.page);
const pageNumbers = ref([1, 2, 3, 4, 5, 6, 7, 8, 9, 10]);
const startPageIndex = ref(0);

const calcPageNumber = (oldPage) => {
  const totalPages = props.pageInfo.totalPages;
  const MAX_SHOW_PAGE = Math.min(totalPages, 10);

  if (
    currentPage.value >= 10 &&
    totalPages > 10 &&
    !isNaN(pageNumbers.value[9])
  ) {
    console.log("a");
    startPageIndex.value += 1;
  } else if (oldPage && currentPage.value < oldPage) {
    console.log("b");
    startPageIndex.value -= 1;
  }

  const newPageNumber = [];
  for (
    let i = startPageIndex.value;
    i < MAX_SHOW_PAGE + startPageIndex.value;
    i++
  ) {
    newPageNumber.push(i + 1);
  }
  pageNumbers.value = newPageNumber;
};

onMounted(() => {
  if (currentPage.value > 10) {
    console.log(currentPage.value);

    startPageIndex.value =
      Math.floor(currentPage.value / 10) > 1
        ? (currentPage.value / 10) * 10 - 10
        : currentPage.value % 10;

    console.log(startPageIndex.value);
  }
  calcPageNumber();
});

function changePage(pageIndex) {
  currentPage.value = pageIndex;
  if (pageIndex == 0) {
    startPageIndex.value = 0;
    calcPageNumber();
  } else if (pageIndex == props.pageInfo.totalPages - 1) {
    startPageIndex.value =
      Math.floor(currentPage.value / 10) > 1
        ? (currentPage.value / 10) * 10 - 10
        : currentPage.value % 10;

    calcPageNumber();
    console.log(startPageIndex.value);
  }
  emit("pageChanged", currentPage.value);
}
// เปลี่ยนไปหน้าถัดไป ถ้ายังไม่ถึงหน้าสุดท้าย
function nextPage() {
  if (currentPage.value < props.pageInfo.totalPages - 1) {
    currentPage.value++;
    if (currentPage.value > 9) {
      calcPageNumber();
    }
    changePage(currentPage.value);
  }
}
// เปลี่ยนไปหน้าก่อนหน้า ถ้ายังไม่ใช่หน้าแรก
function prevPage() {
  if (currentPage.value > 0) {
    const oldPageNumber = currentPage.value;
    currentPage.value--;
    console.log(currentPage.value);
    console.log(pageNumbers.value[0]);

    if (currentPage.value < pageNumbers.value[0] - 1) {
      calcPageNumber(oldPageNumber);
    }
    changePage(currentPage.value);
  }
}
</script>

<template>
  <div>
    <div
      v-if="pageInfo && pageInfo.totalPages !== 1"
      class="join mt-7 w-full px-auto"
    >
      <button
        class="itbms-page-first join-item btn"
        @click="changePage(0)"
        :disabled="pageInfo.first"
      >
        First
      </button>
      <button
        class="itbms-page-prev join-item btn"
        @click="prevPage"
        :disabled="pageInfo.first"
      >
        Prev
      </button>

      <button
        v-for="(number, index) in pageNumbers"
        :class="'itbms-page-' + index"
        class="btn"
        :disabled="currentPage == number - 1"
        @click="changePage(number - 1)"
      >
        {{ number }}
      </button>

      <button
        class="itbms-page-next join-item btn"
        @click="nextPage"
        :disabled="pageInfo.last"
      >
        Next
      </button>
      <button
        class="itbms-page-last join-item btn"
        @click="changePage(pageInfo.totalPages - 1)"
        :disabled="pageInfo.last"
      >
        Last
      </button>
    </div>
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
