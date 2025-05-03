<script setup>
import { useRouter, useRoute } from 'vue-router'
import arrowleft from '../assets/icons/arrowleft.svg'
import dot from '../assets/icons/dot.svg'
import heart from '../assets/icons/heart.svg'
import { useSaleItemStore } from '@/stores/SaleItemStore'
import { onMounted, ref } from 'vue'
import { getSaleItemById } from '@/services/SaleItemService'
import Breadcrumbs from './Breadcrumbs.vue'

const route = useRoute()
const router = useRouter()

const saleItemStore = useSaleItemStore()

const saleItemId = route.params.id

const selectSaleItem = ref(null)

const isNotFound = ref(false)

const fetchSaleItemById = async () => {
  try {
    if (!saleItemId) {
      throw new Error('Invalid ID')
    }
    const saleItemFromStore = saleItemStore.getSaleItemById(saleItemId)
    if (saleItemFromStore) {
      selectSaleItem.value = saleItemFromStore
      return
    }
    const response = await getSaleItemById(saleItemId)
    if (response && !response.error) {
      selectSaleItem.value = response
    } else {
      throw new Error(
        'The requested sale item does not exist or has been removed.'
      )
    }
  } catch (error) {
    console.error(error)
    isNotFound.value = true
  }
}

onMounted(fetchSaleItemById)
</script>

<template>
  <Breadcrumbs
    :crumbs="[
      { label: 'Product', to: '/sale-items' },
      { label: selectSaleItem?.model, to: '' },
    ]"
  />
  <div class="Itbms-row w-full md:max-w-7xl h-full mx-auto px-6 pb-10 mt-4 md:mt-6">
    <div v-if="selectSaleItem">
      <div class="flex flex-col md:flex-row gap-4">
        <div class="md:hidden">
          <div class="flex justify-between items-center">
            <button
              @click="$router.go(-1)"
              class="btn-circle w-[35px] h-[35px] cursor-pointer"
            >
              <img :src="arrowleft" class="m-auto w-[25px]" />
            </button>
            <button class="btn-circle w-[35px] h-[35px] cursor-pointer">
              <img :src="dot" class="m-auto w-[25px]" />
            </button>
          </div>
        </div>
        <div class="Itbms-picture w-full md:w-[50%]">
          <img src="/pictures/iPhone15Pro.png" class="w-full object-cover" />
        </div>
        <div class="w-full md:w-[50%]">
          <div class="mb-[10px] hidden md:block">
            <div class="flex justify-between items-center">
              <button
                @click="$router.go(-1)"
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
            class="Itbms-model text-[24px] md:text-[28px] lg:text-[32px] text-black mb-[4px]"
          >
            {{ selectSaleItem.model }}
          </p>
          <div class="flex justify-between mb-[16px]">
            <div
              class="Itbms-price-unit font-bold text-[18px] md:text-[20px] lg:text-[24px] text-purple-700"
            >
              {{ selectSaleItem.price.toLocaleString() }}<span>฿</span>
            </div>
            <button class="cursor-pointer">
              <img :src="heart" class="w-[25px] md:w-[25px] lg:w-[30px]" />
            </button>
          </div>
          <div
            class="text-[16px] md:text-[16px] lg:text-[18px] text-black leading-8 md:leading-8 lg:leading-9"
          >
            <div class="Itbms-brand">
              <p>
                <span class="font-semibold">Brand:</span>
                {{ selectSaleItem.brandName }}
              </p>
            </div>
            <div class="Itbms-description">
              <p class="font-semibold">Description:</p>
              <span :class="{ 'text-gray-400': !selectSaleItem.description }">{{
                selectSaleItem.description || 'No description provided'
              }}</span>
            </div>
            <div class="Itbms-ramGb-unit">
              <span class="font-semibold"> Ram: </span>
              <span
                :class="{
                  'text-gray-400':
                    !selectSaleItem.ramGb && selectSaleItem.ramGb !== 0,
                }"
              >
                {{ selectSaleItem.ramGb ?? '-'
                }}<span v-if="selectSaleItem.ramGb">GB</span>
              </span>
            </div>
            <div class="Itbms-screenSizeInch-unit">
              <span class="font-semibold"> Screen Size: </span>
              <span
                :class="{
                  'text-gray-400':
                    !selectSaleItem.screenSizeInch &&
                    selectSaleItem.screenSizeInch !== 0,
                }"
              >
                {{ selectSaleItem.screenSizeInch ? parseFloat(selectSaleItem.screenSizeInch).toFixed(1) : '-'
                }}<span v-if="selectSaleItem.screenSizeInch">Inches</span>
              </span>
            </div>
            <div class="Itbms-storageGb-unit">
              <span class="font-semibold"> Storage: </span>
              <span
                :class="{
                  'text-gray-400':
                    !selectSaleItem.storageGb && selectSaleItem.storageGb !== 0,
                }"
              >
                {{ selectSaleItem.storageGb ?? '-'
                }}<span v-if="selectSaleItem.storageGb">GB</span>
              </span>
            </div>
            <div class="Itbms-color">
              <span class="font-semibold"> Color: </span>
              <span :class="{ 'text-gray-400': !selectSaleItem.color }">
                {{ selectSaleItem.color || '-' }}
              </span>
            </div>
            <div class="Itbms-quantity-unit">
              <span class="font-semibold"> Quantity: </span>
              {{ selectSaleItem.quantity }}
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
      <p class="Itbms-message py-4 text-[12px] md:text-[14px] lg:text-[16px]">
        The requested sale item does not exist or has been removed.
      </p>
      <div class="Itbms-button modal-action justify-center">
        <button
          @click="router.push({ name: 'sale-items' })"
          class="btn btn-primary bg-purple-700"
        >
          OK
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
