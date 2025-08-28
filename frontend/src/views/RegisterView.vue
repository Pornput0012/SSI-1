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
      } else {
        throw new Error(response.error);
      }
    }
  } catch (error) {
    errorTitle.value = "Failed to create user";
    errorMessage.value = error.message;
    showModal.value = true;
    isSaving.value = false;
  }
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
  />
</template>

<style></style>
