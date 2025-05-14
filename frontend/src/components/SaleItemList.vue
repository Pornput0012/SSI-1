<script setup>
import { deleteSaleItemById } from '@/services/SaleItemService'
import { useSaleItemStore } from '@/stores/SaleItemStore'
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const props = defineProps({
  saleItems: {
    type: [Array, null],
    required: true,
  },
  hasError: String,
})

const localSaleItems = ref([])
watch(
  () => props.saleItems,
  (val) => {
    if (Array.isArray(val)) {
      localSaleItems.value = [...val]
    }
  },
  { immediate: true }
)

const router = useRouter()
const showDeleteModal = ref(false)
const deleteSuccess = ref(false)
const deleteTargetId = ref(null)
const errorMessage = ref('')
const isNotFound = ref(false)

const confirmDelete = (id) => {
  deleteTargetId.value = id
  showDeleteModal.value = true
}

const handleDelete = async () => {
  try {
    if (deleteTargetId.value) {
      const response = await deleteSaleItemById(deleteTargetId.value)
      showDeleteModal.value = false
      if (response.success) {
        const idx = localSaleItems.value.findIndex(
          (i) => i.id === deleteTargetId.value
        )
        if (idx !== -1) localSaleItems.value.splice(idx, 1)
        deleteSuccess.value = true
      } else {
        throw new Error('The requested sale item does not exist.')
      }
    }
  } catch (error) {
    errorMessage.value = error.message
    isNotFound.value = true
  }
}

const successOk = () => {
  deleteSuccess.value = false
  router.push({ name: 'sale-items-list' })
}
</script>

<template>
  <div class="mt-5">
    <!-- error message -->
    <div
      v-if="!saleItems && hasError !== '' && hasError"
      class="w-full h-full p-12 flex justify-center items-center rounded-lg bg-red-100 border-1 border-red-300"
    >
      <p class="text-red-500 font-extralight">{{ hasError }}</p>
    </div>

    <!-- loader -->
    <div v-else-if="!saleItems" class="m-10 overflow-auto rounded-lg shadow">
      <table class="w-full animate-pulse">
        <thead class="bg-gray-100 border-b-2 border-gray-200">
          <tr class="text-sm font-semibold text-center">
            <th v-for="i in 10" :key="i" class="p-3 tracking-wide">
              <div class="h-4 bg-gray-300 rounded"></div>
            </th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="n in 5" :key="n" class="text-center">
            <td v-for="i in 10" :key="i" class="p-3">
              <div class="h-4 bg-gray-200 rounded w-full"></div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- show sale item list -->
    <div
      v-else-if="saleItems && saleItems.length > 0"
      class="rounded-lg shadow"
    >
      <div class="overflow-x-auto border border-gray-300 rounded-md">
        <table class="w-full">
          <thead class="bg-gray-100 border-b-2 border-gray-200">
            <tr class="text-sm font-semibold text-center">
              <th class="p-3 tracking-wide">Id</th>
              <th class="p-3 tracking-wide">Brand</th>
              <th class="p-3 tracking-wide">Model</th>
              <th class="p-3 tracking-wide">Ram</th>
              <th class="p-3 tracking-wide">Storage</th>
              <th class="p-3 tracking-wide">Color</th>
              <th class="p-3 tracking-wide">Screen Size</th>
              <th class="p-3 tracking-wide">Price</th>
              <th class="p-3 tracking-wide">Quantity</th>
              <th class="p-3 tracking-wide">Action</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="itemInList in saleItems"
              :key="itemInList.id"
              class="itbms-row bg-white text-center text-gray-700"
            >
              <RouterLink
                :to="{
                  name: 'sale-item-detail',
                  params: { id: itemInList.id },
                }"
              >
                <td
                  class="itbms-id p-3 font-bold text-blue-500 hover:underline cursor-pointer whitespace-nowrap"
                >
                  {{ itemInList.id || '-' }}
                </td>
              </RouterLink>
              <td class="itbms-brand p-3 text-sm whitespace-nowrap">
                {{ itemInList.brand || '-' }}
              </td>
              <td class="itbms-model p-3 text-sm text-left whitespace-nowrap">
                {{ itemInList.model || '-' }}
              </td>
              <td class="itbms-ramGb p-3 text-sm whitespace-nowrap">
                {{ itemInList.ramGb || '-' }}
              </td>
              <td class="itbms-storageGb p-3 text-sm whitespace-nowrap">
                {{ itemInList.storageGb || '-' }}
              </td>
              <td class="itbms-color p-3 text-sm whitespace-nowrap">
                {{ itemInList.color || '-' }}
              </td>
              <td class="itbms-screenSizeInch p-3 text-sm whitespace-nowrap">
                {{ itemInList.screenSizeInch || '-' }}
              </td>
              <td class="itbms-price p-3 text-sm whitespace-nowrap">
                {{ itemInList.price.toLocaleString() || '-' }}
              </td>
              <td class="itbms-quantity p-3 text-sm whitespace-nowrap">
                {{ itemInList.quantity || '-' }}
              </td>
              <td class="p-3 whitespace-nowrap">
                <div class="flex gap-2 justify-center">
                  <router-link
                    :to="{
                      name: 'edit-sale-item',
                      params: { id: itemInList.id },
                    }"
                    class="itbms-edit-button btn btn-sm btn-outline btn-info"
                  >
                    E
                  </router-link>
                  <button
                    class="itbms-delete-button btn btn-sm btn-outline btn-error"
                    @click="confirmDelete(itemInList.id)"
                  >
                    D
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- no sale item -->
    <div
      v-if="saleItems && saleItems.length === 0"
      class="w-full h-full p-12 flex justify-center items-center rounded-lg bg-purple-200 border-1 border-purple-400"
    >
      <p class="text-purple-700 font-medium">no sale item</p>
    </div>
  </div>

  <dialog class="modal" :open="showDeleteModal" v-if="showDeleteModal">
    <div class="modal-box">
      <h3 class="text-lg font-bold">Delete Confirmation</h3>

      <p class="itbms-message py-4">Do you want to delete this sale item?</p>
      <div class="modal-action">
        <form method="dialog">
          <button
            @click="showDeleteModal = false"
            class="btn btn-error itbms-cancel-button"
          >
            Cancel
          </button>
        </form>
        <button
          @click="handleDelete"
          class="btn btn-success itbms-confirm-button"
        >
          Confirm
        </button>
      </div>
    </div>
  </dialog>

  <div v-if="deleteSuccess" class="modal modal-open">
    <div class="modal-box text-center">
      <p class="itbms-message py-4 text-[12px] md:text-[14px] lg:text-[16px]">
        The sale item has been deleted.
      </p>
      <div class="itbms-button modal-action justify-center">
        <button @click="successOk" class="btn btn-primary bg-purple-700">
          OK
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
