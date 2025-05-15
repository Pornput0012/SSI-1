<script setup>
import Breadcrumbs from "@/components/Breadcrumbs.vue";
import FormBrand from "@/components/FormBrand.vue";
import { getBrandById, updateBrand } from "@/services/BrandService";
import { useBrandStore } from "@/stores/BrandStore";
import { useNotiStore } from "@/stores/์NotiStore";
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

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

    if (response && !response.error) {
      selectBrand.value = response;
      console.log(selectBrand.value);
    } else {
      throw new Error("The requested brand does not exist.");
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
      console.log(response);
      console.log(response.error);
      if (response && !response.error) {
        brandStore.updateBrandById(selectBrand.value.id, response);
        updateSuccess();
      } else {
        throw new Error("Failed to update brand");
      }
    }
  } catch (error) {
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
    :crumbs="[{ label: selectBrand?.name, to: `/brands/${brandId}` }]"
  />
  <FormBrand
    v-if="selectBrand"
    @on-submit="editBrand"
    :disableSaveBtn="isSaving"
    :brand-detail="selectBrand"
  ></FormBrand>

  <div v-if="isNotFound" class="modal modal-open">
    <div class="modal-box text-center">
      <h3
        class="font-bold text-[14px] md:text-[16px] lg:text-[18px] text-red-500"
      >
        Brand Not Found
      </h3>
      <p class="itbms-message py-4 text-[12px] md:text-[14px] lg:text-[16px]">
        {{ errorMessage }}
      </p>
      <div class="itbms-button modal-action justify-center">
        <button
          @click="router.push({ name: 'sale-items' })"
          class="btn btn-primary bg-purple-700"
        >
          OK
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
