<script setup>
import Breadcrumbs from "@/components/Breadcrumbs.vue";
import MessageModal from "@/components/MessageModal.vue";
import FormBrand from "@/components/FormBrand.vue";
import { getBrandById, updateBrand } from "@/services/BrandService";
import { useBrandStore } from "@/stores/BrandStore";
import { useNotiStore } from "@/stores/์NotiStore";
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

const errorTitle = ref('')
const showModal = ref(false)

const isSaving = ref(false);
const brandStore = useBrandStore();

const route = useRoute();
const router = useRouter();

const selectBrand = ref(null);
const brandId = route.params.id;

const isNotFound = ref(false);
const errorMessage = ref("");

const fetchBrandById = async () => {
  try {
    if (!brandId) {
      throw new Error("Invalid ID");
    }

    const response = await getBrandById(brandId);

    if (!response.error) {
      selectBrand.value = response;
      
    } else {
      throw new Error("The brand does not exist.");
    }
  } catch (error) {
    errorMessage.value = error.message;
    isNotFound.value = true;
  }
};

onMounted(fetchBrandById);

const normalizeOptional = (val) =>
  typeof val === "string" && val !== "" ? val : null;

const editBrand = async (formData) => {
  try {
    isSaving.value = true;
    const sentData = {
      name: formData?.name,
      websiteUrl: normalizeOptional(formData?.websiteUrl),
      isActive: formData?.isActive,
      countryOfOrigin: normalizeOptional(formData?.countryOfOrigin),
    };

    if (formData) {
      const response = await updateBrand(selectBrand.value.id, sentData);
      if (!response.error) {
        brandStore.updateBrandById(selectBrand.value.id, response);
        updateSuccess();
      } else {
        throw new Error(response.error);
      }
    }
  } catch (error) {
    errorTitle.value = 'Failed to update brand'

    errorMessage.value = error.message.substr(7,error.message.length)
    showModal.value = true
    isSaving.value = false;
  }
};

const notiStore = useNotiStore();
const updateSuccess = () => {
  notiStore.updateSuccess = true;
  router.push({ name: "brands" });
};
</script>

<template>
  <Breadcrumbs
    :crumbs="[
      { label: 'Brand List', name: 'brands', class: 'itbms-manage-brand' },
      { label: selectBrand?.name },
    ]"
    :home="{
      label: 'Sale Item List',
      name: 'sale-items-list',
      class: 'itbms-item-list',
    }"
  />
  <FormBrand
    v-if="selectBrand"
    @on-submit="editBrand"
    :disableSaveBtn="isSaving"
    :brand-detail="selectBrand"
  ></FormBrand>

  <MessageModal
    v-if="isNotFound"
    :handle-ok="() => router.push({ name: 'brands' })"
    :title="'Brand Not Found'"
    :message="errorMessage"
  />
   <MessageModal
    v-if="showModal"
    :handle-ok="() => (showModal = false)"
    :title="errorTitle"
    :message="errorMessage"
  />
</template>

<style scoped></style>
