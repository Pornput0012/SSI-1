<script setup>
import { ref, computed, watch, onMounted, watchEffect } from "vue";
import { useUserStore } from "@/stores/UserStore";
import {
  getImageByFileName,
  getUserById,
  updateUserById,
} from "@/services/userService";
import { useRouter } from "vue-router";
import { useNotiStore } from "@/stores/NotiStore";

const USER_TYPE = {
  SELLER: "SELLER",
  BUYER: "BUYER",
};

const errorTitle = ref("");
const errorMessage = ref("");
const showModal = ref(false);

const userStore = useUserStore();
const userId = computed(() => userStore.getUserInfo().id);
const userType = computed(() => userStore.getUserInfo().userType);

const router = useRouter();

const userInfo = ref(userStore.getUserInfo());
const originalFormData = ref(null);

const formatNumber = (data) => {
  if (!data) return "";

  const visibleCount = 3;

  const start = Math.max(0, data.length - (visibleCount + 1));
  const maskedPart = "x".repeat(start);
  const middlePart = data.slice(start, data.length - 1);

  const lastDigit = "x";

  return maskedPart + middlePart + lastDigit;
};

const formData = ref({
  nickName: userInfo.value?.nickName || "",
  fullName: userInfo.value?.fullName || "",
  email: userInfo.value?.email || "",
  password: userInfo.value?.password || "",
  phoneNumber: formatNumber(userInfo.value?.phoneNumber),
  bankAccount: formatNumber(userInfo.value?.bankAccount) || "",
  bankName: userInfo.value?.bankName || "",
  nationalIdNumber: formatNumber(userInfo.value?.nationalIdNumber) || "",
});

onMounted(() => {
  originalFormData.value = { ...formData.value };
});



const notiStore = useNotiStore();
const updateSuccess = () => {
  console.log("call function");

  notiStore.updateSuccess = true;
  console.log(notiStore.updateSuccess);
  router.push({ name: "profile" });
};

const handleSubmit = async () => {
  try {
    isSaving.value = true;
    if (!userId.value || !formData.value) {
      throw new Error("Invalid ID or form data");
    }

    const response = await updateUserById(userId.value, {
      nickName: formData.value.nickName,
      fullName: formData.value.fullName,
    });

    console.log(response);
    
    if (!response.error) {
      isSaving.value = false;
      userStore.setUserInfo(response);
      updateSuccess();
    } else {
      isSaving.value = false;
      throw new Error("The requested user id does not exist.");
    }
  } catch (error) {
    errorTitle.value = "Failed to update user";
    errorMessage.value = error.message;
    showModal.value = true;
  }
};



const isSaving = ref(false);
const enableSaveBtn = ref(false);

const cancelEdit = () => {
  router.push({ name: "profile" });
};

watchEffect(() => {
  let isValid = false;
  let hasChanges = false;
  if (originalFormData.value && formData.value) {
    hasChanges = false;
    const check = ["nickName", "fullName"];
    // Check each field for changes
    for (const key of check) {
      console.log(key);
      console.log(formData.value[key]);
      console.log(originalFormData.value[key]);

      if (formData.value[key]) {
        isValid = true;
      }

      if (formData.value[key] !== originalFormData.value[key]) {
        hasChanges = true;
        break;
      } else {
        isValid = true;
        hasChanges = false;
      }
    }
  }

  enableSaveBtn.value = isValid && hasChanges;

});
</script>

<template>
  <div class="min-h-screen bg-base-200 py-8">
    <div class="container mx-auto px-4 max-w-4xl">
      <!-- Header -->
      <div
        class="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-8 gap-4"
      >
        <div>
          <h1 class="text-3xl font-bold text-base-content">แก้ไขโปรไฟล์</h1>
          <p class="text-base-content/70 mt-1">จัดการข้อมูลส่วนตัวของคุณ</p>
        </div>
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
              <div class="badge badge-primary badge-lg mt-2 itbms-type">
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
                  >ชื่อผู้ใช้ (nickname) <span class="text-error">*</span></span
                >
              </label>
              <input
                v-model.trim="formData.nickName"
                type="text"
                placeholder="กรอกชื่อผู้ใช้"
                class="input input-bordered w-full itbms-nickname"
                required
              />
            </div>

            <!-- Email -->
            <div class="form-control">
              <label class="label">
                <span class="label-text font-semibold">อีเมล (email) </span>
              </label>
              <input
                v-model="formData.email"
                type="email"
                placeholder="กรอกอีเมล"
                class="input input-bordered w-full text-black/40 itbms-email"
                readonly
              />
            </div>

            <!-- Full Name -->
            <div class="form-control md:col-span-2">
              <label class="label">
                <span class="label-text font-semibold"
                  >ชื่อ-นามสกุล (fullname)
                  <span class="text-error">*</span></span
                >
              </label>
              <input
                v-model.trim="formData.fullName"
                type="text"
                placeholder="กรอกชื่อ-นามสกุล"
                class="input input-bordered w-full itbms-fullname"
                required
              />
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
                <input
                  v-model="formData.phoneNumber"
                  type="tel"
                  placeholder="กรอกหมายเลขโทรศัพท์"
                  class="input input-bordered w-full text-black/40 itbms-mobile"
                  readonly
                />
              </div>

              <!-- Bank Name -->
              <div class="form-control">
                <label class="label">
                  <span class="label-text font-semibold"
                    >ชื่อธนาคาร (bankName)
                  </span>
                </label>
                <input
                  v-model="formData.bankName"
                  type="text"
                  placeholder="กรอกหมายเลขโทรศัพท์"
                  class="input input-bordered w-full text-black/40 itbms-bankName"
                  readonly
                />
              </div>

              <!-- Bank Account -->
              <div class="form-control">
                <label class="label">
                  <span class="label-text font-semibold"
                    >หมายเลขบัญชีธนาคาร (bankAccount)
                  </span>
                </label>
                <input
                  v-model="formData.bankAccount"
                  type="text"
                  placeholder="กรอกหมายเลขบัญชีธนาคาร"
                  class="input input-bordered w-full font-mono text-black/40 itbms-bankAccount"
                  readonly
                />
              </div>

              <!-- ID Card Number -->
              <div class="form-control">
                <label class="label">
                  <span class="label-text font-semibold"
                    >หมายเลขบัตรประชาชน (nationalIdNumber)
                  </span>
                </label>
                <input
                  v-model="formData.nationalIdNumber"
                  type="text"
                  placeholder="กรอกหมายเลขบัตรประชาชน"
                  class="input input-bordered w-full font-mono text-black/40"
                  readonly
                />
              </div>
            </div>

            <!-- ID Card Images -->
          </div>

          <!-- Form Actions (Bottom) -->
          <div class="mt-8 pt-6 border-t border-base-300">
            <div class="flex gap-3 justify-end">
              <button @click="cancelEdit" :disabled="isSaving" class="btn w-30 itbms-cancel-button">
                cancel
              </button>
              <button
                @click="handleSubmit"
                :disabled="!enableSaveBtn || isSaving"
                :class="['btn btn-primary w-30',{isSaving : 'cursor-not-allowed'},'itbms-save-button']"
              >
                <svg
                  v-if="isSaving"
                  class="animate-spin h-5 w-5 text-white"
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                >
                  <circle
                    class="opacity-25"
                    cx="12"
                    cy="12"
                    r="10"
                    stroke="currentColor"
                    stroke-width="4"
                  ></circle>
                  <path
                    class="opacity-75"
                    fill="currentColor"
                    d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"
                  ></path>
                </svg>
                {{ isSaving ? "saving..." : "save" }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Additional custom styles */
.divider-primary::before,
.divider-primary::after {
  background-color: hsl(var(--p));
}

.divider-secondary::before,
.divider-secondary::after {
  background-color: hsl(var(--s));
}

/* Smooth transitions for form controls */
.form-control input,
.form-control select {
  transition: all 0.2s ease;
}

.form-control input:focus,
.form-control select:focus {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* Image upload hover effects */
.group:hover .group-hover\:opacity-100 {
  opacity: 1;
}
</style>
