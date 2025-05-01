<script setup>
import { useRouter, useRoute } from 'vue-router'
import arrowleft from '../assets/icons/arrowleft.svg'
import dot from '../assets/icons/dot.svg'
import heart from '../assets/icons/heart.svg'
import { useSaleItemStore } from '@/stores/SaleItemStore'
import { onMounted, ref } from 'vue'
import { getSaleItemById } from '@/services/SaleItemService'

const route = useRoute()
const router = useRouter()

const saleItemStore = useSaleItemStore()

const saleItemId = route.params.id

const selectSaleItem = ref(null)

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
      throw new Error(response.error)
    }
  } catch (error) {
    console.error(error)
    router.push({ name: 'sale-items' })
  }
}

// const formatDate = (dateString) => {
//   const d = new Date(dateString)
//   return isNaN(d.getTime()) ? '-' : d.toLocaleString()
// }

onMounted(fetchSaleItemById)
</script>

<template>
  <div class="Itbms-row bg-pink-200 w-full md:max-w-7xl h-full mx-auto p-6">
    <div v-if="selectSaleItem">
      <div class="flex flex-col md:flex-row gap-4">
        <div class="md:hidden">
          <div class="flex justify-between items-center">
            <button
              @click="$router.go(-1)"
              class="bg-white btn-circle md:w-[26px] md:h-[26px] cursor-pointer"
            >
              <img :src="arrowleft" class="m-auto md:w-[18px]" />
            </button>
            <button
              class="bg-white btn-circle md:w-[26px] md:h-[26px] cursor-pointer"
            >
              <img :src="dot" class="m-auto md:w-[18px]" />
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
                class="bg-white btn-circle md:w-[40px] md:h-[40px] cursor-pointer"
              >
                <img :src="arrowleft" class="m-auto md:w-[30px]" />
              </button>
              <button
                class="bg-white btn-circle md:w-[40px] md:h-[40px] cursor-pointer"
              >
                <img :src="dot" class="m-auto md:w-[30px]" />
              </button>
            </div>
          </div>

          <p
            class="Itbms-model text-[18px] md:text-[24px] lg:text-[32px] text-black mb-[4px]"
          >
            {{ selectSaleItem.model }}
          </p>
          <!-- <div
            class="Itbms-createdOn-updatedOn flex gap-1 text-[10px] md:text-[14px] lg:text-[18px] text-black mb-[8px]"
          >
            <p>{{ formatDate(selectSaleItem.createdOn) }}</p>
            <p>|</p>
            <p>{{ formatDate(selectSaleItem.updatedOn) }}</p>
          </div> -->
          <div
            class="Itbms-createdOn-updatedOn flex gap-1 text-[10px] md:text-[14px] lg:text-[18px] text-black mb-[8px]"
          >
            <p>วันที่สร้าง</p>
            <p>|</p>
            <p>วันที่แก้ไขล่าสุด</p>
          </div>
          <div class="flex justify-between mb-[16px]">
            <div
              class="Itbms-price-unit text-[16px] md:text-[20px] lg:text-[24px] text-blue-500"
            >
              {{ selectSaleItem.price }}฿
            </div>
            <button class="cursor-pointer">
              <img :src="heart" class="w-[15px] md:w-[20px] lg:w-[30px]" />
            </button>
          </div>
          <div
            class="text-[10px] md:text-[14px] lg:text-[18px] text-black leading-5 md:leading-7 lg:leading-9"
          >
            <div class="Itbms-brand">
              <p>Brand: {{ selectSaleItem.brandName }}</p>
            </div>
            <div class="Itbms-description">
              <p>Description:</p>
              <span :class="{ 'text-gray-400': !selectSaleItem.description }">{{
                selectSaleItem.description || 'No description provided'
              }}</span>
            </div>
            <div
              class="Itbms-ramGb-unit"
              v-if="
                selectSaleItem.ramGb !== null &&
                selectSaleItem.ramGb !== undefined
              "
            >
              Ram: {{ selectSaleItem.ramGb }}GB
            </div>
            <div
              class="Itbms-screenSizeInch-unit"
              v-if="
                selectSaleItem.screenSizeInch !== null &&
                selectSaleItem.screenSizeInch !== undefined
              "
            >
              Screen Size: {{ selectSaleItem.screenSizeInch }}Inch
            </div>
            <div
              class="Itbms-storageGb-unit"
              v-if="
                selectSaleItem.storageGb !== null &&
                selectSaleItem.storageGb !== undefined
              "
            >
              Storage: {{ selectSaleItem.storageGb }}GB
            </div>
            <div
              class="Itbms-color"
              v-if="
                selectSaleItem.color !== null &&
                selectSaleItem.color !== undefined
              "
            >
              Color: {{ selectSaleItem.color }}
            </div>
            <div class="Itbms-quantity-unit">
              Available quantity: {{ selectSaleItem.quantity }} units
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
