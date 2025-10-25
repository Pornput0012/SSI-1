<script setup>
import { ref, watchEffect } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/UserStore";
import { changePassword } from "@/services/userService";
import { useNotiStore } from "@/stores/NotiStore";

const router = useRouter();
const userStore = useUserStore();
const notiStore = useNotiStore();

const userId = userStore.getUserInfo().id;

const formData = ref({
  currentPassword: "",
  newPassword: "",
  confirmPassword: "",
});

const isSaving = ref(false);
const showError = ref(false);
const errorTitle = ref("");
const errorMessage = ref("");
const enableSaveBtn = ref(false);
const errorMessagePassword = ref("");
const errorMessageCurrent = ref("");
const errorMessageConfirm = ref("");

const checkPassword = (password) => {
  const rules = {
    lower: /[a-z]/, // ตัวพิมพ์เล็ก
    upper: /[A-Z]/, // ตัวพิมพ์ใหญ่
    number: /[0-9]/, // ตัวเลข
    special: /[^A-Za-z0-9]/, // อักขระพิเศษ
  };

  const minLength = 8;
  const failedRules = [];

  if (!password) {
    errorMessagePassword.value = "กรุณากรอกรหัสผ่าน";
    return false;
  }

  if (password.length < minLength) {
    errorMessagePassword.value = `รหัสผ่านต้องมีความยาวอย่างน้อย ${minLength} ตัวอักษร`;
    return false;
  }

  for (const [ruleName, regex] of Object.entries(rules)) {
    if (!regex.test(password)) {
      failedRules.push(ruleName);
    }
  }

  if (failedRules.length > 0) {
    const translate = {
      lower: "ตัวอักษรพิมพ์เล็ก",
      upper: "ตัวอักษรพิมพ์ใหญ่",
      number: "ตัวเลข",
      special: "อักขระพิเศษ",
    };
    const msg = `รหัสผ่านต้องประกอบด้วย ${failedRules
      .map((r) => translate[r])
      .join(" , ")}`;
    errorMessagePassword.value = msg;
    return false;
  }
  errorMessagePassword.value = "";
  return true;
};

const cancelEdit = () => {
  router.push({ name: "profile" });
};

const validateForm = () => {
  const { currentPassword, newPassword, confirmPassword } = formData.value;
  if (!currentPassword) {
    errorMessageCurrent.value = "กรุณากรอกรหัสผ่านปัจจุบัน";
    return false;
  }
  if (!checkPassword(newPassword)) {
    errorMessageCurrent.value = "";
    return false;
  }
  if (newPassword !== confirmPassword) {
    errorMessageConfirm.value = "รหัสผ่านไม่ตรงกัน";
    return false;
  }

  errorMessageConfirm.value = "";
  return true;
};

watchEffect(() => {
  enableSaveBtn.value = validateForm();
});

const handleSubmit = async () => {
  try {
    isSaving.value = true;
    if (!userId || !formData.value) {
      throw new Error("Invalid user or form data");
    }

    const response = await changePassword(
      userId,
      {
        newPassword: formData.value.newPassword,
        currentPassword: formData.value.currentPassword,
      },
    );

    if (!response.error) {
      isSaving.value = false;
      notiStore.updateSuccess = true;
      router.push({ name: "profile" });
    } else {
      console.log(response);

      throw new Error("Failed to change password");
    }
  } catch (error) {
    isSaving.value = false;
    showError.value = true;
    errorTitle.value = "ไม่สามารถเปลี่ยนรหัสผ่านได้";
    errorMessage.value = error.message;
  }
};
</script>

<template>
  <div class="min-h-screen bg-base-200 py-8">
    <div class="container mx-auto px-4 max-w-4xl">
      <!-- Header -->
      <div
        class="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-8 gap-4"
      >
        <div>
          <h1 class="text-3xl font-bold text-base-content">เปลี่ยนรหัสผ่าน</h1>
          <p class="text-base-content/70 mt-1">
            เพื่อความปลอดภัยของบัญชีผู้ใช้ของคุณ
          </p>
        </div>
      </div>

      <!-- Card -->
      <div class="card bg-base-100 shadow-xl">
        <div class="card-body p-6 md:p-8">
          <div class="divider divider-primary">
            <span class="text-lg font-semibold">ตั้งค่ารหัสผ่านใหม่</span>
          </div>

          <div class="grid grid-cols-1 gap-6">
            <!-- Current Password -->
            <div class="form-control">
              <label class="label">
                <span class="label-text font-semibold">
                  รหัสผ่านปัจจุบัน <span class="text-error">*</span>
                </span>
              </label>
              <input
                v-model.trim="formData.currentPassword"
                type="password"
                placeholder="กรอกรหัสผ่านปัจจุบัน"
                class="input input-bordered w-full itbms-current-password"
                required
              />
              <label class="label" v-if="errorMessageCurrent">
                <span class="label-text-alt text-red-400">
                  {{ errorMessageCurrent }}...
                </span>
              </label>
            </div>

            <!-- New Password -->
            <div class="form-control">
              <label class="label">
                <span class="label-text font-semibold">
                  รหัสผ่านใหม่ <span class="text-error">*</span>
                </span>
              </label>
              <input
                v-model.trim="formData.newPassword"
                type="password"
                placeholder="กรอกรหัสผ่านใหม่"
                class="input input-bordered w-full itbms-new-password"
                required
              />
              <label class="label" v-if="errorMessagePassword">
                <span class="label-text-alt text-red-400">
                  {{ errorMessagePassword }}...
                </span>
              </label>
            </div>

            <!-- Confirm Password -->
            <div class="form-control">
              <label class="label">
                <span class="label-text font-semibold">
                  ยืนยันรหัสผ่านใหม่ <span class="text-error">*</span>
                </span>
              </label>
              <input
                v-model.trim="formData.confirmPassword"
                type="password"
                placeholder="ยืนยันรหัสผ่านใหม่"
                class="input input-bordered w-full itbms-confirm-password"
                required
              />
              <label class="label" v-if="errorMessageConfirm">
                <span class="label-text-alt text-red-400">
                  {{ errorMessageConfirm }}...
                </span>
              </label>
            </div>
          </div>

          <!-- Action Buttons -->
          <div class="mt-8 pt-6 border-t border-base-300">
            <div class="flex gap-3 justify-end">
              <button
                @click="cancelEdit"
                :disabled="isSaving"
                class="btn w-30 itbms-cancel-button"
              >
                cancel
              </button>
              <button
                @click="handleSubmit"
                :disabled="!enableSaveBtn || isSaving"
                :class="[
                  'btn btn-primary w-30',
                  { 'cursor-not-allowed': isSaving },
                  'itbms-save-button',
                ]"
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

          <!-- Error Modal -->
          <dialog v-if="showError" class="modal modal-open">
            <div class="modal-box">
              <h3 class="font-bold text-lg text-error">{{ errorTitle }}</h3>
              <p class="py-4">{{ errorMessage }}</p>
              <div class="modal-action">
                <button class="btn" @click="showError = false">ปิด</button>
              </div>
            </div>
          </dialog>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.divider-primary::before,
.divider-primary::after {
  background-color: hsl(var(--p));
}

.form-control input {
  transition: all 0.2s ease;
}
.form-control input:focus {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
</style>
