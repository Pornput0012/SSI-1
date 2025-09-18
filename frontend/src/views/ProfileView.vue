<script setup>
import { ref, computed, watch, onMounted, watchEffect } from "vue";
import { useUserStore } from "@/stores/UserStore";
import { getImageByFileName, getUserById } from "@/services/userService";
import { useNotiStore } from "@/stores/NotiStore";

const USER_TYPE = {
  SELLER: "SELLER",
  BUYER: "BUYER",
};

const userStore = useUserStore();

const frontImageUrl = ref(null);
const backImageUrl = ref(null);

const userId = computed(() => userStore.getUserInfo().id);
const userType = computed(() => userStore.getUserInfo().userType);
const userInfo = ref(null);
watchEffect(async () => {
  if (userId.value) {
    const user = await getUserById(userId.value);
    userInfo.value = user;
    if (user.nationalIdPhotoFront) {
      frontImageUrl.value = await getImageByFileName(user.nationalIdPhotoFront);
    }
    if (user.nationalIdPhotoBack) {
      backImageUrl.value = await getImageByFileName(user.nationalIdPhotoBack);
    }
  }
});

const formatNumber = (data) => {
  if (!data) return "";

  const visibleCount = 3;

  const start = Math.max(0, data.length - (visibleCount + 1));
  const maskedPart = "x".repeat(start);
  const middlePart = data.slice(start, data.length - 1);

  const lastDigit = "x";

  return maskedPart + middlePart + lastDigit;
};

const notiStore = useNotiStore();
const showUpdatedModal = computed(() => {
  return notiStore.updateSuccess;
});

const closeModal = () => {
  notiStore.updateSuccess = false;
}
</script>

<template>
  <div class="min-h-screen bg-base-200 py-8">
    <div class="container mx-auto px-4 max-w-4xl">
      <!-- Header -->
      <div
        class="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-8 gap-4"
      >
        <div>
          <h1 class="text-3xl font-bold text-base-content">โปรไฟล์</h1>
          <p class="text-base-content/70 mt-1">จัดการข้อมูลส่วนตัวของคุณ</p>
        </div>
        <router-link :to="{name:'EditProfile'}" class="btn btn-primary gap-2 min-w-fit">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-4 w-4"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"
            />
          </svg>
          แก้ไขโปรไฟล์
        </router-link>
      </div>

      <!-- Loading State -->
      <div v-if="!userInfo" class="flex justify-center items-center py-20">
        <span class="loading loading-spinner loading-lg text-primary"></span>
      </div>

      <!-- Profile Card -->
      <div v-if="userInfo" class="card bg-base-100 shadow-xl">
        <div class="card-body p-6 md:p-8">
          <!-- Profile Header with Avatar -->
          <div
            class="flex flex-col sm:flex-row items-center sm:items-start gap-6 mb-8"
          >
            <div class="avatar">
              <div
                class="w-24 h-24 rounded-full ring ring-primary ring-offset-base-100 ring-offset-2"
              >
                <img
                  src="https://img.daisyui.com/images/profile/demo/yellingcat@192.webp"
                  :alt="userInfo.fullName"
                />
              </div>
            </div>
            <div class="text-center sm:text-left flex-1">
              <h2 class="text-2xl font-bold text-base-content">
                {{ userInfo.fullName }}
              </h2>
              <p class="text-base-content/70 text-lg">
                @{{ userInfo.nickName }}
              </p>
              <div class="badge badge-primary badge-lg mt-2">
                {{ userType === USER_TYPE.SELLER ? "Seller" : "Buyer" }}
              </div>
            </div>
          </div>

          <!-- Basic Information -->
          <div class="divider divider-primary">
            <span class="text-lg font-semibold">ข้อมูลพื้นฐาน</span>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <!-- Nickname -->
            <div class="form-control">
              <label class="label">
                <span class="label-text font-semibold"
                  >ชื่อผู้ใช้ (nickname)</span
                >
              </label>
              <div class="p-3 bg-base-200 rounded-lg">
                {{ userInfo.nickName }}
              </div>
            </div>

            <!-- Email -->
            <div class="form-control">
              <label class="label">
                <span class="label-text font-semibold">อีเมล (email)</span>
              </label>
              <div class="p-3 bg-base-200 rounded-lg">
                {{ userInfo.email }}
              </div>
            </div>

            <!-- Full Name -->
            <div class="form-control md:col-span-2">
              <label class="label">
                <span class="label-text font-semibold"
                  >ชื่อ-นามสกุล (fullname)</span
                >
              </label>
              <div class="p-3 bg-base-200 rounded-lg">
                {{ userInfo.fullName }}
              </div>
            </div>
          </div>

          <!-- Seller Additional Information -->
          <div v-if="userType === USER_TYPE.SELLER" class="mt-8">
            <div class="divider divider-secondary">
              <span class="text-lg font-semibold">ข้อมูลผู้ขาย</span>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <!-- Phone -->
              <div class="form-control">
                <label class="label">
                  <span class="label-text font-semibold"
                    >หมายเลขโทรศัพท์มือถือ (phone)</span
                  >
                </label>
                <div class="p-3 bg-base-200 rounded-lg">
                  {{ formatNumber(userInfo.phoneNumber) }}
                </div>
              </div>

              <!-- Bank Name -->
              <div class="form-control">
                <label class="label">
                  <span class="label-text font-semibold"
                    >ชื่อธนาคาร (bankName)</span
                  >
                </label>
                <div class="p-3 bg-base-200 rounded-lg">
                  {{ userInfo.bankName }}
                </div>
              </div>

              <!-- Bank Account -->
              <div class="form-control">
                <label class="label">
                  <span class="label-text font-semibold"
                    >หมายเลขบัญชีธนาคาร (bankAccount)</span
                  >
                </label>
                <div class="p-3 bg-base-200 rounded-lg font-mono">
                  {{ formatNumber(userInfo.bankAccount) }}
                </div>
              </div>

              <!-- ID Card Number -->
              <div class="form-control">
                <label class="label">
                  <span class="label-text font-semibold"
                    >หมายเลขบัตรประชาชน (nationalIdNumber)</span
                  >
                </label>
                <div class="p-3 bg-base-200 rounded-lg font-mono">
                  {{formatNumber( userInfo.nationalIdNumber )}}
                </div>
              </div>
            </div>

            <!-- ID Card Images -->
            <div class="mt-6">
              <label class="label">
                <span class="label-text font-semibold"
                  >รูปถ่ายบัตรประชาชน (idCardFront, idCardBack)</span
                >
              </label>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <!-- Front ID Card -->
                <div class="card bg-base-200 shadow-sm">
                  <div class="card-body p-4">
                    <h4 class="font-semibold text-sm mb-3">ด้านหน้า</h4>
                    <div
                      class="aspect-[1.6/1] bg-base-300 rounded-lg overflow-hidden"
                    >
                      <img
                        v-if="frontImageUrl"
                        :src="frontImageUrl"
                        alt="บัตรประชาชนด้านหน้า"
                        class="w-full h-full object-cover"
                      />
                      <div
                        v-else
                        class="w-full h-full flex items-center justify-center text-base-content/60"
                      >
                        <span class="text-sm">ไม่มีรูปภาพ</span>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Back ID Card -->
                <div class="card bg-base-200 shadow-sm">
                  <div class="card-body p-4">
                    <h4 class="font-semibold text-sm mb-3">ด้านหลัง</h4>
                    <div
                      class="aspect-[1.6/1] bg-base-300 rounded-lg overflow-hidden"
                    >
                      <img
                        v-if="backImageUrl"
                        :src="backImageUrl"
                        alt="บัตรประชาชนด้านหลัง"
                        class="w-full h-full object-cover"
                      />
                      <div
                        v-else
                        class="w-full h-full flex items-center justify-center text-base-content/60"
                      >
                        <span class="text-sm">ไม่มีรูปภาพ</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  
  <div v-if="showUpdatedModal" class="modal modal-open">
    <div class="modal-box text-center">
      <h3 class="font-bold text-success">Successfully!</h3>
      <p class="py-4 itbms-message">
        "The user account has been successfully updated." 
      </p>
      <div class="itbms-button modal-action justify-center">
        <button @click="closeModal" class="btn btn-primary">OK</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Additional custom styles if needed */
.divider-primary::before,
.divider-primary::after {
  background-color: hsl(var(--p));
}

.divider-secondary::before,
.divider-secondary::after {
  background-color: hsl(var(--s));
}
</style>
