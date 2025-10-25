<script setup>
import { useRouter, useRoute } from 'vue-router'
import arrowleft from '../assets/icons/arrowleft.svg'
import dot from '../assets/icons/dot.svg'
import heart from '../assets/icons/heart.svg'
import { useSaleItemStore } from '@/stores/SaleItemStore'
import { onMounted, ref, onBeforeUnmount, computed } from 'vue'
import {
  getSaleItemById,
  deleteSaleItemById,
  getSaleItemById_v2,
  getImageByFileName,
} from '@/services/SaleItemService'
import Breadcrumbs from '../components/Breadcrumbs.vue'
import { useNotiStore } from '@/stores/NotiStore'
import MessageModal from '@/components/MessageModal.vue'
import noImage from '@/assets/No_image_available.png'
import { useUserStore } from '@/stores/UserStore'
import { addItemToCart } from '@/services/CartService'
import { useCartStore } from '@/stores/CartStore'
import { useToastStore } from '@/stores/ToastStore'

const userStore = useUserStore()
const cartStore = useCartStore()
const route = useRoute()
const router = useRouter()
const saleItemStore = useSaleItemStore()
const saleItemId = route.params.id
const selectSaleItem = ref(null)
const isNotFound = ref(false)
const errorMessage = ref('')
const quantitySelectedItemInCart = ref(0)
const userNickname = computed(() => userStore.getNickName())
const userId = computed(() => userStore.getUserId())

const notiStore = useNotiStore()

// เพิ่ม state สำหรับจัดการรูปภาพ
const currentImageIndex = ref(0)
const imageUrls = ref([])

// ฟังก์ชันสำหรับโหลดรูปภาพ
const loadImages = async (imageFileNames) => {
  if (!imageFileNames || imageFileNames.length === 0) {
    imageUrls.value = [noImage, noImage, noImage, noImage]
    return
  }

  const urls = []
  for (const fileName of imageFileNames) {
    try {
      const imageUrl = await getImageByFileName(fileName.fileName)
      urls.push(imageUrl)
    } catch (error) {
      console.error(`Failed to load image ${fileName.fileName}:`, error)
      urls.push(noImage)
    }
  }

  // เติม noImage ให้ครบ 4 รูปถ้าไม่ครบ
  while (urls.length < 4) {
    urls.push(noImage)
  }

  imageUrls.value = urls.slice(0, 4) // จำกัดไม่เกิน 4 รูป
}

// ฟังก์ชันสำหรับเปลี่ยนรูปหลัก
const changeMainImage = (index) => {
  currentImageIndex.value = index
}

const fetchSaleItemById = async () => {
  try {
    if (!saleItemId) {
      throw new Error('Invalid ID')
    }
    const response = await getSaleItemById_v2(saleItemId)
    if (!response.error) {
      selectSaleItem.value = response
      quantitySelectedItemInCart.value =
        cartStore.getQuantityByProductId(saleItemId)
      await loadImages(response.saleItemImages)
    } else {
      throw new Error('The requested sale item does not exist.')
    }
  } catch (error) {
    errorMessage.value = error.message
    isNotFound.value = true
  }
}

const toastStore = useToastStore()

const addToCart = async () => {
  if (!userNickname.value) {
    router.push({ name: 'signin' })
    return
  }

  if (selectSaleItem.value.seller.id === userId.value) {
    notiStore.addToCartFail = true
    return
  }

  console.log('before set data')
  console.log('uid' + userId.value)
  console.log('pid' + selectSaleItem.value.id)
  console.log('fileName' + selectSaleItem.value?.saleItemImages[0]?.fileName)
  console.log('qty' + cartQuantity.value)

  try {
    const data = {
      userId: userId.value,
      productId: selectSaleItem.value.id,
      imageFileName: selectSaleItem.value?.saleItemImages[0]?.fileName || '',
      quantity: cartQuantity.value,
    }

    console.log('after set data')
    console.log('Adding to cart:', data)
    const response = await addItemToCart(data)
    console.log('Add to cart response:', response)

    if (response.error) {
      notiStore.addToCartFail = true
      throw new Error(response.error)
    }

    cartStore.addToCart(response, cartQuantity.value)
    toastStore.showToast('✓ Added to cart!', 'success')
    quantitySelectedItemInCart.value =
      cartStore.getQuantityByProductId(saleItemId)
    cartQuantity.value = 1
    console.log('Successfully added to cart store')
  } catch (error) {
    console.error('Error adding to cart:', error)
    toastStore.showToast('✗ Failed to add to cart', 'error')
  }
}

onMounted(fetchSaleItemById)

onBeforeUnmount(() => {
  imageUrls.value.forEach((url) => {
    if (url !== noImage && url.startsWith('blob:')) {
      URL.revokeObjectURL(url)
    }
  })
})

const cartQuantity = ref(1)

const decreaseQuantity = () => {
  if (cartQuantity.value > 1) {
    cartQuantity.value--
  }
}

const increaseQuantity = () => {
  if (
    selectSaleItem.value &&
    cartQuantity.value <
      selectSaleItem.value.quantity - quantitySelectedItemInCart.value
  ) {
    cartQuantity.value++
  }
}
</script>

<template>
  <Breadcrumbs
    :crumbs="[{ label: selectSaleItem?.model, class: 'itbms-back-button' }]"
  />
  <div
    class="itbms-row w-full md:max-w-7xl h-full mx-auto px-6 pb-10 mt-4 md:mt-6"
  >
    <div v-if="selectSaleItem">
      <div class="flex flex-col md:flex-row gap-4">
        <div class="md:hidden">
          <div class="flex justify-between items-center">
            <button
              @click="$router.push({ name: 'sale-items' })"
              class="btn-circle w-[35px] h-[35px] cursor-pointer"
            >
              <img :src="arrowleft" class="m-auto w-[25px]" />
            </button>
            <button class="btn-circle w-[35px] h-[35px] cursor-pointer">
              <img :src="dot" class="m-auto w-[25px]" />
            </button>
          </div>
        </div>

        <div class="itbms-picture w-full md:w-[50%]">
          <!-- รูปหลัก -->
          <div class="w-full mb-4 aspect-square max-w-md mx-auto">
            <img
              :src="imageUrls[currentImageIndex] || noImage"
              class="w-full h-full object-cover rounded-lg border border-gray-200"
              :alt="selectSaleItem.model"
            />
          </div>

          <!-- รูปย่อ (แสดงทั้งหมด 4 รูป) -->
          <div class="flex gap-2 justify-center max-w-md mx-auto">
            <div
              v-for="(imageUrl, index) in imageUrls"
              :key="index"
              class="flex-1 aspect-square cursor-pointer rounded-lg overflow-hidden border-2 transition-all max-w-[80px]"
              :class="
                currentImageIndex === index
                  ? 'border-purple-700'
                  : 'border-gray-300 hover:border-gray-400'
              "
              @click="changeMainImage(index)"
            >
              <img
                :src="imageUrl"
                class="w-full h-full object-cover"
                :alt="`${selectSaleItem.model} image ${index + 1}`"
              />
            </div>
          </div>
        </div>

        <div class="w-full md:w-[50%]">
          <div class="mb-[10px] hidden md:block">
            <div class="flex justify-between items-center">
              <button
                @click="$router.push({ name: 'sale-items' })"
                class="btn-circle w-[40px] h-[40px] cursor-pointer relative -left-3"
              >
                <img :src="arrowleft" class="m-auto w-[30px]" />
              </button>
              <button class="btn-circle w-[40px] h-[40px] cursor-pointer">
                <img :src="dot" class="m-auto w-[30px]" />
              </button>
            </div>
          </div>
          <p
            class="itbms-model text-[24px] md:text-[28px] lg:text-[32px] text-black mb-[4px]"
          >
            {{ selectSaleItem.model }}
          </p>
          <div class="flex justify-between mb-[16px]">
            <div
              class="itbms-price font-bold text-[18px] md:text-[20px] lg:text-[24px] text-purple-700"
            >
              {{ selectSaleItem.price.toLocaleString()
              }}<span class="itbms-price-unit">฿</span>
            </div>
            <button class="cursor-pointer">
              <img :src="heart" class="w-[25px] md:w-[25px] lg:w-[30px]" />
            </button>
          </div>
          <div
            class="text-[16px] md:text-[16px] lg:text-[18px] text-black leading-8 md:leading-8 lg:leading-9"
          >
            <div class="itbms-brand">
              <p>
                <span class="font-semibold">Brand:</span>
                {{ selectSaleItem.brandName }}
              </p>
            </div>
            <div class="itbms-description">
              <p class="font-semibold">Description:</p>
              <span :class="{ 'text-gray-400': !selectSaleItem.description }">{{
                selectSaleItem.description || 'No description provided'
              }}</span>
            </div>
            <div class="itbms-ramGb-unit">
              <span class="font-semibold">Ram: </span>
              <span
                :class="{
                  'text-gray-400':
                    !selectSaleItem.ramGb && selectSaleItem.ramGb !== 0,
                }"
              >
                <span class="itbms-ramGb"
                  >{{ selectSaleItem.ramGb || '-' }}
                </span>
                <span class="itbms-ramGb-unit">GB</span>
              </span>
            </div>
            <div class="itbms-screenSizeInch-unit">
              <span class="font-semibold">Screen Size: </span>
              <span
                :class="{
                  'text-gray-400':
                    !selectSaleItem.screenSizeInch &&
                    selectSaleItem.screenSizeInch !== 0,
                }"
              >
                <span class="itbms-screenSizeInch">{{
                  selectSaleItem.screenSizeInch || '-'
                }}</span>
                <span class="itbms-screenSizeInch-unit">Inches</span>
              </span>
            </div>
            <div class="itbms-storageGb-unit">
              <span class="font-semibold">Storage: </span>
              <span
                :class="{
                  'text-gray-400':
                    !selectSaleItem.storageGb && selectSaleItem.storageGb !== 0,
                }"
              >
                <span class="itbms-storageGb">
                  {{ selectSaleItem.storageGb || '-' }}
                </span>
                <span class="itbms-storageGb-unit">GB</span>
              </span>
            </div>
            <div class="itbms-color">
              <span class="font-semibold">Color: </span>
              <span :class="{ 'text-gray-400': !selectSaleItem.color }">
                {{ selectSaleItem.color || '-' }}
              </span>
            </div>
            <div>
              <span class="font-semibold">Quantity: </span>
              <span class="itbms-quantity">
                {{ selectSaleItem.quantity }}
              </span>
            </div>
            <div class="flex gap-4 mt-4">
              <!-- Quantity Selector and Add to Cart Button -->
              <div class="flex gap-3 items-center">
                <div class="flex items-center bg-gray-200 rounded-lg px-2 py-1">
                  <!-- ปุ่ม - -->
                  <button
                    @click="decreaseQuantity"
                    class="itbms-dec-qty-button text-md font-bold text-gray-600 hover:text-gray-800 px-2 cursor-pointer"
                    :disabled="cartQuantity <= 1"
                    :class="{
                      'opacity-50 cursor-not-allowed': cartQuantity <= 1,
                    }"
                  >
                    -
                  </button>

                  <!-- แสดงจำนวน -->
                  <span
                    class="itbms-add-to-cart-quantity text-[16px] font-semibold min-w-[40px] text-center text-gray-800"
                  >
                    {{ cartQuantity }}
                  </span>

                  <!-- ปุ่ม + -->
                  <button
                    @click="increaseQuantity"
                    class="itbms-inc-qty-button text-md text-gray-600 font-bold hover:text-gray-800 px-2 cursor-pointer"
                    :disabled="
                      cartQuantity >=
                      selectSaleItem.quantity - quantitySelectedItemInCart
                    "
                    :class="{
                      'opacity-50 cursor-not-allowed':
                        cartQuantity >= selectSaleItem.quantity,
                    }"
                  >
                    +
                  </button>
                </div>
                <button
                  @click="addToCart"
                  :disabled="
                    cartQuantity >
                    selectSaleItem.quantity - quantitySelectedItemInCart
                  "
                  class="itbms-add-to-cart-button btn btn-primary bg-blue-700 hover:bg-blue-800 flex-1 text-white"
                >
                  Add to Cart
                </button>
              </div>

              <!-- <router-link
                :to="{ name: 'edit-sale-item', params: { id: saleItemId } }"
                class="itbms-edit-button btn btn-warning btn-outline"
                >Edit</router-link
              >
              <button
                @click="showDeleteModal = true"
                class="itbms-delete-button btn btn-error btn-outline"
              >
                Delete
              </button> -->
              <!-- <dialog
                class="modal"
                :open="showDeleteModal"
                v-if="showDeleteModal"
              >
                <div class="modal-box">
                  <h3 class="text-lg font-bold">Delete Confirmation</h3>

                  <p class="itbms-message py-4">
                    Do you want to delete this sale item?
                  </p>
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
              </dialog> -->
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <MessageModal
    v-if="isNotFound"
    :handle-ok="() => router.push({ name: 'sale-items' })"
    :title="'Product Not Found'"
    :message="errorMessage"
  />

  <MessageModal
    v-if="notiStore.addToCartFail"
    :handle-ok="() => (notiStore.addToCartFail = false)"
    :title="'Add to cart fail'"
    :message="'Can not add your own sale item'"
  />
</template>

<style scoped>
</style>
