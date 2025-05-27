<script setup>
import Breadcrumbs from "@/components/Breadcrumbs.vue";
import MessageModal from "@/components/MessageModal.vue";
import { deleteBrandById, getAllBrands } from "@/services/BrandService";
import { useBrandStore } from "@/stores/BrandStore";
import { useSaleItemStore } from "@/stores/SaleItemStore";
import { useNotiStore } from "@/stores/์NotiStore";
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";

const brandStore = useBrandStore();
const saleItemStore = useSaleItemStore();
const hasError = ref("");
const router = useRouter();
const showDeleteModal = ref(false);
const showWarningModal = ref(false);
const deleteSuccess = ref(false);
const deleteTargetId = ref(null);
const deleteTargetName = ref(null);
const errorMessage = ref("");
const isNotFound = ref(false);

onMounted(async () => {
  try {
    const response = await getAllBrands();
    if (response.error) {
      throw new Error(response.error);
    }
  } catch (e) {
    hasError.value = e.message;
  }
});

const confirmDelete = (brand) => {
  deleteTargetId.value = brand.id;
  deleteTargetName.value = brand.name;

  const hasSaleItem = saleItemStore
    .getSaleItems()
    ?.some((item) => item.brandName === brand.name);

  if (hasSaleItem) {
    showWarningModal.value = true;
  } else {
    showDeleteModal.value = true;
  }
};

const handleDelete = async () => {
  try {
    if (deleteTargetId.value) {
      console.log(deleteTargetId.value);

      const response = await deleteBrandById(deleteTargetId.value);
      showDeleteModal.value = false;
      if (response.success) {
        console.log(deleteTargetId.value);

        brandStore.deleteBrandById(deleteTargetId.value);

        console.log(brandStore.brands);

        deleteSuccess.value = true;
      } else {
        throw new Error("An error has occurred, the brand does not exist.");
      }
    }
  } catch (error) {
    errorMessage.value = error.message;
    isNotFound.value = true;
  }
};

const successOk = () => {
  deleteSuccess.value = false;
  router.push({ name: "brands" });
};

const warningCancel = () => {
  showWarningModal.value = false;
};

const notiStore = useNotiStore();
const showCreatedModal = ref(false);
onMounted(() => {
  if (notiStore.createSuccess) {
    showCreatedModal.value = true;
    notiStore.createSuccess = false;
  }
});

const showUpdatedModal = ref(false);
onMounted(() => {
  if (notiStore.updateSuccess) {
    showUpdatedModal.value = true;
    notiStore.updateSuccess = false;
  }
});
</script>

<template>
  <Breadcrumbs
    :crumbs="[
      { label: 'Add Brand', class: 'itbms-add-button', name: 'create-brand' },
    ]"
    :home="{
      label: 'Sale Item List',
      name: 'sale-items-list',
      class: 'itbms-item-list',
    }"
  />
  <div class="w-full h-full px-10 pb-10 md:px-20 md:pb-20">
    <div class="w-full flex justify-between items-center">
      <h1 class="font-bold text-2xl">Brands</h1>
      <div class="flex gap-4 items-center">
        <label class="w-fit h-8 input input-xs hidden sm:flex">
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
    </div>
    <div class="mt-10">
      <!-- error message -->
      <div
        v-if="!brandStore.brands && hasError !== '' && hasError"
        class="w-full h-full p-12 flex justify-center items-center rounded-lg bg-red-100 border-1 border-red-300"
      >
        <p class="text-red-500 font-extralight">{{ hasError }}</p>
      </div>

      <!-- loader -->
      <div
        v-else-if="!brandStore.brands"
        class="m-10 overflow-auto rounded-lg shadow"
      >
        <table class="w-full animate-pulse">
          <thead class="bg-gray-100 border-b-2 border-gray-200">
            <tr class="text-sm font-semibold text-center">
              <th v-for="i in 2" :key="i" class="p-3 tracking-wide">
                <div class="h-4 bg-gray-300 rounded"></div>
              </th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="n in 5" :key="n" class="text-center">
              <td v-for="i in 2" :key="i" class="p-3">
                <div class="h-4 bg-gray-200 rounded w-full"></div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- show brand list -->
      <div
        v-else-if="brandStore.brands && brandStore.brands.length > 0"
        class="rounded-lg shadow"
      >
        <div class="overflow-x-auto border border-gray-300 rounded-md">
          <table class="w-full">
            <thead class="bg-gray-100 border-b-2 border-gray-200">
              <tr class="text-sm font-semibold text-center">
                <th class="p-3 tracking-wide">Id</th>
                <th class="p-3 tracking-wide text-left">Name</th>
                <th class="p-3 tracking-wide">Action</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="brand in brandStore.brands"
                :key="brand.id"
                class="itbms-row bg-white text-center text-gray-700"
              >
                <td
                  class="itbms-id p-4 text-sm whitespace-nowrap font-bold text-blue-500"
                >
                  {{ brand.id || "-" }}
                </td>
                <td class="itbms-name p-4 text-sm whitespace-nowrap text-left">
                  {{ brand.name || "-" }}
                </td>

                <td class="p-4 whitespace-nowrap">
                  <div class="flex gap-2 justify-center">
                    <router-link
                      :to="{
                        name: 'edit-brand',
                        params: { id: brand.id },
                      }"
                    >
                      <button
                        class="itbms-edit-button btn btn-sm btn-outline btn-info"
                      >
                        Edit
                      </button>
                    </router-link>
                    <button
                      class="itbms-delete-button btn btn-sm btn-outline btn-error"
                      @click="confirmDelete(brand)"
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
        v-if="brandStore.brands && brandStore.brands.length === 0"
        class="w-full h-full p-12 flex justify-center items-center rounded-lg bg-purple-200 border-1 border-purple-400"
      >
        <p class="text-purple-700 font-medium">no brand</p>
      </div>
    </div>

    <dialog class="modal" :open="showDeleteModal" v-if="showDeleteModal">
      <div class="modal-box">
        <h3 class="text-lg font-bold">Delete Confirmation</h3>

        <p class="itbms-message py-4">
          Do you want to delete <b>{{ deleteTargetName }}</b> brand?
        </p>
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

    <dialog class="modal" :open="showWarningModal" v-if="showWarningModal">
      <div class="modal-box">
        <h3 class="text-lg font-bold">Cannot Delete</h3>

        <p class="itbms-message py-3">
          Delete <b>{{ deleteTargetName }}</b> is not allowed. There are sale
          items with <b>{{ deleteTargetName }}</b> brand.
        </p>
        <div class="modal-action">
          <form method="dialog">
            <button
              @click="warningCancel"
              class="btn btn-error itbms-cancel-button"
            >
              Cancel
            </button>
          </form>
        </div>
      </div>
    </dialog>

    <div v-if="deleteSuccess" class="modal modal-open">
      <div class="modal-box text-center">
        <p class="itbms-message py-4 text-[12px] md:text-[14px] lg:text-[16px]">
          The brand has been deleted.
        </p>
        <div class="itbms-button modal-action justify-center">
          <button @click="successOk" class="btn btn-primary bg-purple-700">
            OK
          </button>
        </div>
      </div>
    </div>
  </div>

  <MessageModal
    v-if="showCreatedModal"
    :handle-ok="() => (showCreatedModal = false)"
    :title="'Successfully!'"
    :message="'The brand has been added.'"
    :type-modal="'text-success'"
  />

  <!-- <div v-if="showCreatedModal" class="modal modal-open">
    <div class="modal-box text-center">
      <h3
        class="font-bold text-[14px] md:text-[16px] lg:text-[18px] text-success"
      >
        Successfully!
      </h3>
      <p class="itbms-message py-4 text-[12px] md:text-[14px] lg:text-[16px]">
        The brand has been added.
      </p>
      <div class="itbms-button modal-action justify-center">
        <button
          @click="showCreatedModal = false"
          class="btn btn-primary bg-purple-700"
        >
          OK
        </button>
      </div>
    </div>
  </div> -->

  <MessageModal
    v-if="showUpdatedModal"
    :handle-ok="() => (showUpdatedModal = false)"
    :title="'Successfully!'"
    :message="'The brand has been updated.'"
    :type-modal="'text-success'"
  />

  <!-- <div v-if="showUpdatedModal" class="modal modal-open">
    <div class="modal-box text-center">
      <h3
        class="font-bold text-[14px] md:text-[16px] lg:text-[18px] text-success"
      >
        Successfully!
      </h3>
      <p class="itbms-message py-4 text-[12px] md:text-[14px] lg:text-[16px]">
        The brand has been updated.
      </p>
      <div class="itbms-button modal-action justify-center">
        <button
          @click="showUpdatedModal = false"
          class="btn btn-primary bg-purple-700"
        >
          OK
        </button>
      </div>
    </div>
  </div> -->

  <MessageModal
    v-if="errorMessage"
    :handle-ok="() => (errorMessage = '')"
    :title="'Brand Not Found!'"
    :message="errorMessage"
  />
</template>

<style scoped></style>
