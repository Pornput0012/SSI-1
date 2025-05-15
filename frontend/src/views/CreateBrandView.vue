<script setup>
import Breadcrumbs from "@/components/Breadcrumbs.vue";
import FormBrand from "@/components/FormBrand.vue";
import { createBrand } from "@/services/BrandService";
import { useBrandStore } from "@/stores/BrandStore";
import { useNotiStore } from "@/stores/์NotiStore";
import { ref } from "vue";
import { useRouter } from "vue-router";

const isSaving = ref(false);
const brandStore = useBrandStore();
const normalizeOptional = (str) =>
  typeof str === "string" && str !== "" ? str : null;
const handleCreateBrand = async (formData) => {
  try {
    isSaving.value = true;
    
    const sentData = {
      name: formData?.name,
      websiteUrl: normalizeOptional(formData?.websiteUrl),
      isActive: formData?.isActive,
      countryOfOrigin: normalizeOptional(formData?.countryOfOrigin),
    };

    if (formData) {
      const response = await createBrand(sentData);
      if (response && !response.error) {
        brandStore.createBrand(response);
        createSuccess();
      } else {
        throw new Error("Failed to add brand");
      }
    }
  } catch (error) {
    isSaving.value = false;
  }
};

const notiStore = useNotiStore();
const router = useRouter()
const createSuccess = () => {
    console.log("Noti Store push");
    
  notiStore.createSuccess = true;
  router.push({ name: "brands" });
};
</script>

<template>
  <Breadcrumbs :crumbs="[{ label: 'New Brand', to: '' }]" />
  <FormBrand 
    @on-submit="handleCreateBrand"
    :disableSaveBtn="isSaving"
  ></FormBrand>
</template>

<style scoped></style>
