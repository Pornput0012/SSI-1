<script setup>
import Breadcrumbs from '@/components/Breadcrumbs.vue'
import FormBrand from '@/components/FormBrand.vue'
import MessageModal from '@/components/MessageModal.vue'
import { createBrand } from '@/services/BrandService'
import { useBrandStore } from '@/stores/BrandStore'
import { useNotiStore } from '@/stores/์NotiStore'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const errorTitle = ref('')
const errorMessage = ref('')
const showModal = ref(false)
const isSaving = ref(false)
const brandStore = useBrandStore()
const normalizeOptional = (str) =>
  typeof str === 'string' && str !== '' ? str : null
const handleCreateBrand = async (formData) => {
  try {
    isSaving.value = true

    const sentData = {
      name: formData?.name,
      websiteUrl: normalizeOptional(formData?.websiteUrl),
      isActive: formData?.isActive,
      countryOfOrigin: normalizeOptional(formData?.countryOfOrigin),
    }

    if (formData) {
      const response = await createBrand(sentData)
      if (!response.error) {
        brandStore.createBrand(response)
        createSuccess()
      } else {
        throw new Error(response.error)
      }
    }
  } catch (error) {
    isSaving.value = false

    errorTitle.value = 'Failed to add brand'
    errorMessage.value =  error.message.substr(7,error.message.length)
    showModal.value = true
  }
}

const notiStore = useNotiStore()
const router = useRouter()
const createSuccess = () => {
  notiStore.createSuccess = true
  router.push({ name: 'brands' })
}
</script>

<template>
  <Breadcrumbs
    :crumbs="[
      { label: 'Brand List', name: 'brands', class: 'itbms-manage-brand' },
      { label: 'New Brand' },
    ]"
    :home="{
      label: 'Sale Item List',
      name: 'sale-items-list',
      class: 'itbms-item-list',
    }"
  />
  <FormBrand
    @on-submit="handleCreateBrand"
    :disableSaveBtn="isSaving"
  ></FormBrand>

  <MessageModal
    v-if="showModal"
    :handle-ok="() => (showModal = false)"
    :title="errorTitle"
    :message="errorMessage"
  />
</template>

<style scoped></style>
