<script setup>
import {
  deleteSaleItemById,
} from '@/services/SaleItemService'
import { useSaleItemStore } from '@/stores/SaleItemStore'
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  saleItems: {
    type: [Array, null],
    required: true,
  },
  pageInfo: Object,
  hasError: String,
})

// === Pagination ===
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


// ==== Delete Item ====
const router = useRouter()
const showDeleteModal = ref(false)
const deleteSuccess = ref(false)
const deleteTargetId = ref(null)
const errorMessage = ref('')
const isNotFound = ref(false)
const saleItemStore = useSaleItemStore()

const confirmDelete = (id) => {
  deleteTargetId.value = id
  showDeleteModal.value = true
}

const handleDelete = async () => {
  try {
    if (deleteTargetId.value) {
      const response = await deleteSaleItemById(deleteTargetId.value)
      showDeleteModal.value = false
      if (response.success) {
        deleteSuccess.value = true
      } else {
        throw new Error('The requested sale item does not exist.')
      }
    }
  } catch (error) {
    errorMessage.value = error.message
    isNotFound.value = true
  }
}

const successOk = () => {
  deleteSuccess.value = false
  router.push({ name: 'sale-items-list' })
}
</script>

<template>
  <div class="mt-5">
    <!-- error message -->
    <div
      v-if="!saleItems && hasError !== '' && hasError"
      class="w-full h-full p-12 flex justify-center items-center rounded-lg bg-red-100 border-1 border-red-300"
    >
      <p class="text-red-500 font-extralight">{{ hasError }}</p>
    </div>

    <!-- Loader -->
    <div v-else-if="!saleItems" class="m-10 overflow-auto rounded-lg shadow">
      <table class="w-full animate-pulse">
        <thead class="bg-gray-100 border-b-2 border-gray-200">
          <tr class="text-sm font-semibold text-center">
            <th v-for="i in 8" :key="i" class="p-3 tracking-wide">
              <div class="h-4 bg-gray-300 rounded"></div>
            </th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="n in 5" :key="n" class="text-center">
            <td v-for="i in 8" :key="i" class="p-3">
              <div class="h-4 bg-gray-200 rounded w-full"></div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- show sale item list -->
    <div
      v-else-if="saleItems && saleItems.length > 0"
      class="rounded-lg shadow"
    >
      <div class="overflow-x-auto border border-gray-300 rounded-md">
        <table class="w-full">
          <thead class="bg-gray-100 border-b-2 border-gray-200">
            <tr class="text-sm font-semibold text-center">
              <th class="p-3 tracking-wide">Id</th>
              <th class="p-3 tracking-wide">Brand</th>
              <th class="p-3 tracking-wide">Model</th>
              <th class="p-3 tracking-wide">Ram</th>
              <th class="p-3 tracking-wide">Storage</th>
              <th class="p-3 tracking-wide">Color</th>
              <th class="p-3 tracking-wide">Price</th>
              <th class="p-3 tracking-wide">Action</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="itemInList in saleItems"
              :key="itemInList.id"
              class="itbms-row bg-white text-center text-gray-700"
            >
              <RouterLink
                :to="{
                  name: 'sale-item-detail',
                  params: { id: itemInList.id },
                }"
              >
                <td
                  class="itbms-id p-3 font-bold text-blue-500 hover:underline cursor-pointer whitespace-nowrap"
                >
                  {{ itemInList.id || '-' }}
                </td>
              </RouterLink>
              <td class="itbms-brand p-3 text-sm whitespace-nowrap">
                {{ itemInList.brandName || '-' }}
              </td>
              <td class="itbms-model p-3 text-sm text-left whitespace-nowrap">
                {{ itemInList.model || '-' }}
              </td>
              <td class="itbms-ramGb p-3 text-sm whitespace-nowrap">
                {{ itemInList.ramGb || '-' }}
              </td>
              <td class="itbms-storageGb p-3 text-sm whitespace-nowrap">
                {{ itemInList.storageGb || '-' }}
              </td>
              <td class="itbms-color p-3 text-sm whitespace-nowrap">
                {{ itemInList.color || '-' }}
              </td>
              <td class="itbms-price p-3 text-sm whitespace-nowrap">
                {{ itemInList.price.toLocaleString() || '-' }}
              </td>
              <td class="p-4 whitespace-nowrap">
                <div class="flex gap-2 justify-center">
                  <router-link
                    :to="{
                      name: 'edit-sale-item',
                      params: { id: itemInList.id },
                    }"
                    class="itbms-edit-button btn btn-sm btn-outline btn-info"
                  >
                    Edit
                  </router-link>
                  <button
                    class="itbms-delete-button btn btn-sm btn-outline btn-error"
                    @click="confirmDelete(itemInList.id)"
                  >
                    Delete
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- no sale item -->
    <div
      v-if="saleItems && saleItems.length === 0"
      class="w-full h-full p-12 flex justify-center items-center rounded-lg bg-purple-200 border-1 border-purple-400"
    >
      <p class="text-purple-700 font-medium">no sale item</p>
    </div>

    <!-- Pagination -->
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

  <dialog class="modal" :open="showDeleteModal" v-if="showDeleteModal">
    <div class="modal-box">
      <h3 class="text-lg font-bold">Delete Confirmation</h3>

      <p class="itbms-message py-4">Do you want to delete this sale item?</p>
      <div class="modal-action">
        <form method="dialog">
          <button
            @click="showDeleteModal = false"
            class="btn btn-error itbms-cancel-button"
          >
            Cancel
          </button>
        </form>
        <button
          @click="handleDelete"
          class="btn btn-success itbms-confirm-button"
        >
          Confirm
        </button>
      </div>
    </div>
  </dialog>

  <div v-if="deleteSuccess" class="modal modal-open">
    <div class="modal-box text-center">
      <p class="itbms-message py-4 text-[12px] md:text-[14px] lg:text-[16px]">
        The sale item has been deleted.
      </p>
      <div class="itbms-button modal-action justify-center">
        <button @click="successOk" class="btn btn-primary bg-purple-700">
          OK
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped></style>