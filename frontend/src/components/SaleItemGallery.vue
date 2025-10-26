<script setup>
import { ref, onMounted, watch } from "vue";
import ContainerList from "./ContainerList.vue";
import SaleItemGalleryModel from "./SaleItemGalleryModel.vue";
const props = defineProps({
  saleItems: {
    type: [Array, null],
    required: true,
  },
  pageInfo: Object,
  hasError: String,
});

const emit = defineEmits(["pageChanged"]);

const currentPage = ref(props.pageInfo?.page ?? 0);
const pageNumbers = ref([]);
const startPageIndex = ref(0);
const isActionPage = ref(true);

const calcPageNumber = (oldPage) => {
  const totalPages = props.pageInfo.totalPages;
  const MAX_SHOW_PAGE = Math.min(totalPages, 10);

  if (
    currentPage.value >= 10 &&
    totalPages > 10 &&
    !isNaN(pageNumbers.value[9])
  ) {
    isActionPage.value = true;
    startPageIndex.value += 1;
  } else if (oldPage && currentPage.value < oldPage) {
    isActionPage.value = true;
    startPageIndex.value -= 1;
  }

  if (isActionPage.value) {
    const newPageNumber = [];
    for (
      let i = startPageIndex.value;
      i < MAX_SHOW_PAGE + startPageIndex.value;
      i++
    ) {
      newPageNumber.push(i + 1);
    }

    pageNumbers.value = newPageNumber;
  }
  isActionPage.value = false;
};

onMounted(() => {
  if (currentPage.value >= 10) {
    startPageIndex.value =
      Math.floor((currentPage.value + 1) / 10) > 1
        ? ((currentPage.value + 1) / 10) * 10 - 10
        : (currentPage.value + 1) % 10;
  }
  calcPageNumber();
});

function changePage(pageIndex) {
  currentPage.value = pageIndex;
  if (pageIndex === 0) {
    startPageIndex.value = 0;
    isActionPage.value = true;
    calcPageNumber();
  } else if (pageIndex === props.pageInfo.totalPages - 1) {
    startPageIndex.value =
      props.pageInfo.totalPages > 10
        ? Math.floor(currentPage.value / 10) > 1
          ? (currentPage.value / 10) * 10 - 10
          : currentPage.value % 10
        : 0;

    isActionPage.value = true;
    calcPageNumber();
  }
  emit("pageChanged", currentPage.value);
}

function nextPage() {
  if (currentPage.value < props.pageInfo.totalPages - 1) {
    currentPage.value++;
    if (currentPage.value > 9) {
      calcPageNumber();
    }
    changePage(currentPage.value);
  }
}

function prevPage() {
  if (currentPage.value > 0) {
    const oldPageNumber = currentPage.value;
    currentPage.value--;
    if (currentPage.value < pageNumbers.value[0] - 1) {
      calcPageNumber(oldPageNumber);
    }

    changePage(currentPage.value);
  }
}

watch(
  () => props.pageInfo,
  (newVal, oldVal) => {
    if (
      newVal &&
      (newVal.size != oldVal.size ||
        newVal.totalPages != oldVal.totalPages ||
        newVal.sort != oldVal.sort ||
        newVal.totalElements != oldVal.totalElements)
    ) {
      startPageIndex.value = 0;
      currentPage.value = newVal.page;
      isActionPage.value = true;
      calcPageNumber();
    } else if (newVal && newVal.page != oldVal.page) {
      currentPage.value = newVal.page;
    }
  },
  { deep: true }
);
</script>

<template>
  <div>
    <div
      v-if="!saleItems && hasError !== '' && hasError"
      class="w-full h-full p-12 flex justify-center items-center rounded-lg bg-red-100 border-1 border-red-300"
    >
      <p class="text-red-500 font-extralight">{{ hasError }}</p>
    </div>

    <div
      v-else-if="!saleItems"
      class="w-full h-full justify-center items-center grid grid-cols-1 gap-6 sm:grid-cols-2 md:grid-cols-3 md:gap-6 lg:grid-cols-4 lg:gap-8 xl:grid-cols-5 xl:gap-10"
    >
      <div v-for="i in 5" :key="i" class="flex w-full flex-col gap-4">
        <div class="skeleton h-32 w-full"></div>
        <div class="skeleton h-4 w-28"></div>
        <div class="skeleton h-4 w-full"></div>
        <div class="skeleton h-4 w-full"></div>
      </div>
    </div>

    <ContainerList
      v-else-if="saleItems && saleItems.length > 0"
      :items="saleItems"
      class="w-full h-full justify-center items-center grid grid-cols-1 gap-4 sm:grid-cols-2 md:grid-cols-3 md:gap-6 lg:grid-cols-4 lg:gap-8 xl:grid-cols-5 xl:gap-10"
    >
      <template v-slot="{ itemInList }">
        <SaleItemGalleryModel
          :fileName="itemInList.saleItemImages[0]"
          :brand="itemInList.brandName"
          :model="itemInList.model"
          :price="itemInList.price"
          :ram-gb="itemInList.ramGb"
          :storage-gb="itemInList.storageGb"
          :color="itemInList.color"
          :id="itemInList.id"
          :sellerId="itemInList.seller.id"
        />
      </template>
    </ContainerList>

    <div
      v-if="saleItems && saleItems.length === 0"
      class="w-full h-full p-12 flex justify-center items-center rounded-lg bg-purple-200 border-1 border-purple-400"
    >
      <p class="text-purple-700 font-medium">no sale item</p>
    </div>

    <div
      v-if="pageInfo && !hasError && saleItems && saleItems.length !== 0"
      class="join mt-7 w-full justify-center flex-wrap md:flex-nowrap"
    >
      <button
        class="itbms-page-first join-item btn"
        :class="pageInfo.totalPages === 1 ? 'hidden' : ''"
        @click="changePage(0)"
        :disabled="pageInfo.first"
      >
        First
      </button>
      <button
        class="itbms-page-prev join-item btn"
        :class="pageInfo.totalPages === 1 ? 'hidden' : ''"
        @click="prevPage"
        :disabled="pageInfo.first"
      >
        Prev
      </button>
      <button
        v-for="(number, index) in pageNumbers"
        :key="index"
        :class="[
          'itbms-page-' + index,
          pageInfo.totalPages === 1 ? 'hidden' : '',
        ]"
        class="btn"
        :disabled="currentPage == number - 1"
        @click="changePage(number - 1)"
      >
        {{ number }}
      </button>
      <button
        class="itbms-page-next join-item btn"
        @click="nextPage"
        :class="pageInfo.totalPages === 1 ? 'hidden' : ''"
        :disabled="pageInfo.last"
      >
        Next
      </button>
      <button
        class="itbms-page-last join-item btn"
        @click="changePage(pageInfo.totalPages - 1)"
        :class="pageInfo.totalPages === 1 ? 'hidden' : ''"
        :disabled="pageInfo.last"
      >
        Last
      </button>
    </div>
  </div>
</template>

<style scoped></style>
