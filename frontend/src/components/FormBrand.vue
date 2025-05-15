<script setup>
import { getAllBrands } from "@/services/BrandService";
import { useBrandStore } from "@/stores/BrandStore";
import { computed, onMounted, ref, watchEffect } from "vue";
import router from "@/router";

const emit = defineEmits(["onSubmit"]);

const isCanNotSave = ref(true);

const props = defineProps({
  brandDetail: {
    type: Object,
  },
  disableSaveBtn: Boolean,
});

const formData = ref({
  name: props.brandDetail?.name ?? "",
  websiteUrl: props.brandDetail?.websiteUrl || null, //ใช้ null เพราะเป็นทั้ง optionalและ not empty
  isActive: props.brandDetail?.isActive || false,
  countryOfOrigin: props.brandDetail?.countryOfOrigin || null,
});

const errorTitle = ref("");
const errorMessage = ref("");
const showModal = ref(false);

const requireField = ["name"];
const fieldCheckOldData = ["name", "isActive"];
const emptyCheck = ["websiteUrl", "countryOfOrigin"];
const handleSubmit = () => {
  if (!formData.value?.name) {
    errorTitle.value = "Missing Required Field";
    errorMessage.value = `Please fill out the required field : name`;
    showModal.value = true;
    return;
  }

  emit("onSubmit", formData.value);
};

watchEffect(() => {
  if (!formData.value?.name) {
    isCanNotSave.value = true;
    return;
  }

  let hasChanges = false;

  const normalizeOptional = (val) =>
    typeof val === "string"
      ? val.trim() === ""
        ? null
        : val.trim()
      : val ?? null;

  if (!hasChanges) {
    for (const field of fieldCheckOldData) {
      if (formData.value[field] !== props.brandDetail?.[field]) {
        hasChanges = true;
        break;
      }
    }

    for (const field of emptyCheck) {
      if (
        normalizeOptional(formData.value[field]) !==
        normalizeOptional(props.brandDetail?.[field])
      ) {
        hasChanges = true;
        break;
      }
    }
  }

  isCanNotSave.value = !hasChanges;
});
</script>

<template>
  <div class="mt-10 lg:mt-0 md:mx-16 mb-14">
    <div class="grid gap-y-4 gap-x-6 px-4 grid-cols-1 sm:grid-cols-[auto_1fr]">
      <label class="text-sm md:text-base" for="name">Name</label>
      <input
        type="text"
        id="name"
        class="itbms-name input w-full"
        v-model.trim="formData.name"
      />

      <label class="text-sm md:text-base" for="websiteUrl">Website Url</label>
      <input
        type="text"
        id="websiteUrl"
        class="itbms-websiteUrl input w-full"
        v-model.trim="formData.websiteUrl"
      />

      <label class="text-sm md:text-base" for="countryOfOrigin"
        >Country of origin</label
      >
      <input
        type="text"
        id="countryOfOrigin"
        class="itbms-countryOfOrigin input w-full"
        v-model.trim="formData.countryOfOrigin"
      />

      <div class="flex gap-2 col-span-1 sm:col-span-2 items-center">
        <input
          type="checkbox"
          id="isActive"
          class="itbms-isActive"
          v-model.trim="formData.isActive"
        />
        <label class="text-sm md:text-base" for="isActive">Active</label>
      </div>

      <div
        class="flex gap-4 col-span-1 sm:col-span-2 justify-center sm:justify-end mt-4"
      >
        <button
          @click="handleSubmit"
          class="itbms-save-button btn"
          :class="isCanNotSave || disableSaveBtn ? '' : 'btn-primary'"
          :disabled="isCanNotSave || disableSaveBtn"
        >
          Save
        </button>
        <button @click="$router.back()" class="itbms-cancel-button btn">
          Cancel
        </button>
      </div>
    </div>
  </div>

  <div v-if="showModal" class="modal modal-open">
    <div class="modal-box text-center">
      <h3
        class="font-bold text-[14px] md:text-[16px] lg:text-[18px] text-red-500"
      >
        {{ errorTitle }}
      </h3>
      <p class="itbms-message py-4 text-[12px] md:text-[14px] lg:text-[16px]">
        {{ errorMessage }}
      </p>
      <div class="itbms-button modal-action justify-center">
        <button
          @click="showModal = false"
          class="btn btn-primary bg-purple-700"
        >
          OK
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
