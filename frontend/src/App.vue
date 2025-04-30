<script setup>
import { useRouter, useRoute } from 'vue-router';
import arrowleft from './assets/icons/arrowleft.svg'
import dot from './assets/icons/dot.svg'
import heart from './assets/icons/heart.svg'

const route = useRoute();
const router = useRouter();
</script>

<template>
  <div class="Itbms-row bg-pink-200 w-full md:max-w-7xl h-full mx-auto p-6">
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
          iPhone 15 Pro
        </p>
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
            42,900 THB
          </div>
          <div class="flex gap-2">
            <div
              class="Itbms-quantity-unit text-[10px] md:text-[14px] lg:text-[18px] text-black pt-1"
            >
              Available quantity: 10 units
            </div>
            <button class="cursor-pointer">
              <img :src="heart" class="w-[15px] md:w-[20px] lg:w-[30px]" />
            </button>
          </div>
        </div>
        <div
          class="text-[10px] md:text-[14px] lg:text-[18px] text-black leading-5 md:leading-7 lg:leading-9"
        >
          <div class="Itbms-brand">
            <p>Brand: Apple</p>
          </div>
          <div class="Itbms-description">
            <p>Description:</p>
            <span> สมาร์ทโฟนรุ่นท็อปจาก Apple </span>
          </div>
          <div class="Itbms-ramGb-unit">Ram: 8GB</div>
          <div class="Itbms-screenSizeInch-unit">Screen Size: 6.1Inch</div>
          <div class="Itbms-storageGb-unit">Storage: 512GB</div>
          <div class="Itbms-color">Color: Black Titanium</div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
