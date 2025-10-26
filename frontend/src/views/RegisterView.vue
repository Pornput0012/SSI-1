<script setup>
import AuthenForm from "@/components/AuthenForm.vue";
import MessageModal from "@/components/MessageModal.vue";
import { createUser } from "@/services/userService";

import router from "@/router";

import { ref } from "vue";
import { useNotiStore } from "@/stores/NotiStore";

const errorTitle = ref("");
const errorMessage = ref("");
const showModal = ref(false);
const isSaving = ref(false);

const clearData = ref("false")
const handleSubmit = async (payload) => {
  try {
    isSaving.value = true;
    if (payload) {
      const response =
        payload.userType === "Seller"
          ? await createUser(
              payload.userDetail,
              payload.userType,
              payload.images
            )
          : await createUser(payload.userDetail, payload.userType);
      if (!response.error) {
        createSuccess();
        clearData.value = true
      } else {
        const errorInfo = getErrorByField(response);
        errorTitle.value = errorInfo.title;
        errorMessage.value = errorInfo.message;
        showModal.value = true;
        isSaving.value = false;
      }
    }
  } catch (error) {
    isSaving.value = false;
    console.error("❌ Unexpected error:", error);
    errorTitle.value = "Failed to Create Account";
    errorMessage.value = "An unexpected error occurred. Please try again.";
    showModal.value = true;
  }
};

// ✅ แปลง error ตาม field
const getErrorByField = (response) => {
  // ถ้ามี errors array
  if (response.errors && response.errors.length > 0) {
    const field = response.errors[0].field?.toLowerCase() || "";
    
    // ✅ แมพ field กับ error message
    const errorMap = {
      "email": {
        title: "Email Already Exists",
        message: "This email is already registered. Please use a different email or sign in."
      },
      "nationalId": {
        title: "Invalid National ID",
        message: "National ID number is not valid. Please enter a valid 13-digit ID number."
      },
    };

    // ถ้าเจอ field ใน map ใช้ message ที่กำหนดไว้
    if (errorMap[field]) {
      return errorMap[field];
    }

    // ถ้าไม่เจอ ใช้ message จาก backend
    return {
      title: "Validation Error",
      message: response.errors[0].message || "Please check your information."
    };
  }

  // ถ้าไม่มี errors array ใช้ message เดียว
  return {
    title: "Failed to Create Account",
    message: response.message || "An error occurred. Please try again."
  };
};

const notiStore = useNotiStore();
const createSuccess = () => {
  notiStore.createUserSuccess = true;
  router.push({ name: "sale-items" });
};
</script>

<template>
  <AuthenForm @onSubmit="handleSubmit" :disable-save-btn="isSaving" />
  <MessageModal
    v-if="showModal"
    :handle-ok="() => (showModal = false)"
    :title="errorTitle"
    :message="errorMessage"
    :clearData="clearData"
  />
</template>

<style></style>
