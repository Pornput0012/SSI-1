<script setup>
import { useRouter, useRoute } from "vue-router";
import arrowleft from "../assets/icons/arrowleft.svg";
import dot from "../assets/icons/dot.svg";
import heart from "../assets/icons/heart.svg";
import { useSaleItemStore } from "@/stores/SaleItemStore";
import { onMounted, ref } from "vue";
import {
  getSaleItemById,
  deleteSaleItemById,
} from "@/services/SaleItemService";
import Breadcrumbs from "../components/Breadcrumbs.vue";
import { useNotiStore } from "@/stores/์NotiStore";

const route = useRoute();
const router = useRouter();

const saleItemStore = useSaleItemStore();

const saleItemId = route.params.id;

const selectSaleItem = ref(null);

const isNotFound = ref(false);
const errorMessage = ref("");

const showDeleteModal = ref(false);

const deleteSuccess = ref(false);

const fetchSaleItemById = async () => {
  try {
    if (!saleItemId) {
      throw new Error("Invalid ID");
    }

    const response = await getSaleItemById(saleItemId);
    if (response && !response.error) {
      selectSaleItem.value = response;
    } else {
      throw new Error("The requested sale item does not exist.");
    }
  } catch (error) {
    errorMessage.value = error.message;
    isNotFound.value = true;
  }
};

onMounted(fetchSaleItemById);

const handleDelete = async (id) => {
  try {
    console.log(selectSaleItem.value.id);
    if (selectSaleItem.value && selectSaleItem.value.id) {
      const response = await deleteSaleItemById(selectSaleItem.value.id);
      showDeleteModal.value = false;
      console.log(response);
      if (response.success) {
        saleItemStore.deleteSaleItemById(selectSaleItem.value.id);
        deleteSuccess.value = true;
        selectSaleItem.value = null;
        console.log(selectSaleItem.value);
      } else {
        throw new Error("The requested sale item does not exist.");
      }
    }
  } catch (e) {
    errorMessage.value = e.message;
    isNotFound.value = true;
  }
};

const successOk = () => {
  deleteSuccess.value = false;
  router.push({ name: "sale-items" });
};

const notiStore = useNotiStore();
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
    :crumbs="[{ label: selectSaleItem?.model, class: 'itbms-back-button' }]"
  />
  <div
    class="itbms-row w-full md:max-w-7xl h-full mx-auto px-6 pb-10 mt-4 md:mt-6"
  >
    <div v-if="selectSaleItem">
      <div class="flex flex-col md:flex-row gap-4">
        <div class="md:hidden">
          <div class="flex justify-between items-center">
            <button
              @click="$router.push({ name: 'sale-items' })"
              class="btn-circle w-[35px] h-[35px] cursor-pointer"
            >
              <img :src="arrowleft" class="m-auto w-[25px]" />
            </button>
            <button class="btn-circle w-[35px] h-[35px] cursor-pointer">
              <img :src="dot" class="m-auto w-[25px]" />
            </button>
          </div>
        </div>
        <div class="itbms-picture w-full md:w-[50%]">
          <img src="/pictures/iPhone15Pro.png" class="w-full object-cover" />
        </div>
        <div class="w-full md:w-[50%]">
          <div class="mb-[10px] hidden md:block">
            <div class="flex justify-between items-center">
              <button
                @click="$router.push({ name: 'sale-items' })"
                class="btn-circle w-[40px] h-[40px] cursor-pointer relative -left-3"
              >
                <img :src="arrowleft" class="m-auto w-[30px]" />
              </button>
              <button class="btn-circle w-[40px] h-[40px] cursor-pointer">
                <img :src="dot" class="m-auto w-[30px]" />
              </button>
            </div>
          </div>

          <p
            class="itbms-model text-[24px] md:text-[28px] lg:text-[32px] text-black mb-[4px]"
          >
            {{ selectSaleItem.model }}
          </p>
          <div class="flex justify-between mb-[16px]">
            <div
              class="itbms-price font-bold text-[18px] md:text-[20px] lg:text-[24px] text-purple-700"
            >
              {{ selectSaleItem.price.toLocaleString()
              }}<span class="itbms-price-unit">฿</span>
            </div>
            <button class="cursor-pointer">
              <img :src="heart" class="w-[25px] md:w-[25px] lg:w-[30px]" />
            </button>
          </div>
          <div
            class="text-[16px] md:text-[16px] lg:text-[18px] text-black leading-8 md:leading-8 lg:leading-9"
          >
            <div class="itbms-brand">
              <p>
                <span class="font-semibold">Brand:</span>
                {{ selectSaleItem.brandName }}
              </p>
            </div>
            <div class="itbms-description">
              <p class="font-semibold">Description:</p>
              <span :class="{ 'text-gray-400': !selectSaleItem.description }">{{
                selectSaleItem.description || "No description provided"
              }}</span>
            </div>
            <div class="itbms-ramGb-unit">
              <span class="font-semibold">Ram: </span>
              <span
                :class="{
                  'text-gray-400':
                    !selectSaleItem.ramGb && selectSaleItem.ramGb !== 0,
                }"
              >
                <span class="itbms-ramGb"
                  >{{ selectSaleItem.ramGb || "-" }}
                </span>
                <span class="itbms-ramGb-unit">GB</span>
              </span>
            </div>
            <div class="itbms-screenSizeInch-unit">
              <span class="font-semibold">Screen Size: </span>
              <span
                :class="{
                  'text-gray-400':
                    !selectSaleItem.screenSizeInch &&
                    selectSaleItem.screenSizeInch !== 0,
                }"
              >
                <span class="itbms-screenSizeInch">{{
                  selectSaleItem.screenSizeInch || "-"
                }}</span>
                <span class="itbms-screenSizeInch-unit">Inches</span>
              </span>
            </div>
            <div class="itbms-storageGb-unit">
              <span class="font-semibold">Storage: </span>
              <span
                :class="{
                  'text-gray-400':
                    !selectSaleItem.storageGb && selectSaleItem.storageGb !== 0,
                }"
              >
                <span class="itbms-storageGb">
                  {{ selectSaleItem.storageGb || "-" }}
                </span>
                <span class="itbms-storageGb-unit">GB</span>
              </span>
            </div>
            <div class="itbms-color">
              <span class="font-semibold">Color: </span>
              <span :class="{ 'text-gray-400': !selectSaleItem.color }">
                {{ selectSaleItem.color || "-" }}
              </span>
            </div>
            <div>
              <span class="font-semibold">Quantity: </span>
              <span class="itbms-quantity">
                {{ selectSaleItem.quantity }}
              </span>
            </div>
            <div class="flex gap-4 mt-4">
              <router-link
                :to="{ name: 'edit-sale-item', params: { id: saleItemId } }"
                class="itbms-edit-button btn btn-warning btn-outline"
                >Edit</router-link
              >
              <button
                @click="showDeleteModal = true"
                class="itbms-delete-button btn btn-error btn-outline"
              >
                Delete
              </button>
              <dialog
                class="modal"
                :open="showDeleteModal"
                v-if="showDeleteModal"
              >
                <div class="modal-box">
                  <h3 class="text-lg font-bold">Delete Confirmation</h3>

                  <p class="itbms-message py-4">
                    Do you want to delete this sale item?
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
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div v-if="isNotFound" class="modal modal-open">
    <div class="modal-box text-center">
      <h3
        class="font-bold text-[14px] md:text-[16px] lg:text-[18px] text-red-500"
      >
        Product Not Found
      </h3>
      <p class="itbms-message py-4 text-[12px] md:text-[14px] lg:text-[16px]">
        {{ errorMessage }}
      </p>
      <div class="itbms-button modal-action justify-center">
        <button
          @click="router.push({ name: 'sale-items' })"
          class="btn btn-primary bg-purple-700"
        >
          OK
        </button>
      </div>
    </div>
  </div>
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
  <div v-if="showUpdatedModal" class="modal modal-open">
    <div class="modal-box text-center">
      <h3
        class="font-bold text-[14px] md:text-[16px] lg:text-[18px] text-success"
      >
        Successfully!
      </h3>
      <p class="itbms-message py-4 text-[12px] md:text-[14px] lg:text-[16px]">
        The sale item has been updated.
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
  </div>
</template>

<style scoped></style>
