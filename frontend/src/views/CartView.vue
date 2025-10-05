<script setup>
import Breadcrumbs from '@/components/Breadcrumbs.vue'
import { deleteCartById, getAllCart, updateCart } from '@/services/CartService'
import { useCartStore } from '@/stores/CartStore'
import { useUserStore } from '@/stores/UserStore'
import noImage from '@/assets/No_image_available.png'
import { computed, onMounted, ref, watch, watchEffect } from 'vue'
import { getImageByFileName } from '@/services/SaleItemService'
import { createOrder } from '@/services/OrderService'
import { useOrderStore } from '@/stores/OrderStore'
import router from '@/router'
import { useFilterStore } from '@/stores/FilterStore'

const userStore = useUserStore()
const cartStore = useCartStore()
const userId = computed(() => userStore.getUserId())
// Mockup ข้อมูลสินค้า
const carts = ref([])

const hasError = ref('')

onMounted(async () => {
  try {
    if (cartStore.carts) {
      carts.value = cartStore.carts.map((ele) => {
        return {
          ...ele,
          checkAll: false,
        }
      })
    } else {
      console.log('Fetching cart for user:', userId.value)

      const response = await getAllCart(userId.value)

      if (response.error) {
        throw new Error(response.error)
      }

      carts.value = response?.map((ele) => {
        return {
          ...ele,
          cart: ele?.cart.map((ele) => {
            return { ...ele, checked: false }
          }),
          checkAll: false,
        }
      })
    }
    console.log('Cart data loaded:', carts.value)
    // โหลดรูปภาพหลังจากได้ข้อมูล cart
    await loadImages()
  } catch (error) {
    hasError.value = error.message
    console.error('Error loading cart:', error)
  }

  console.log(cartStore.carts)
})

const selectAll = ref(false)

const itemSelected = computed(() => {
  return carts.value.flatMap((cart) => cart.cart.filter((item) => item.checked))
})

const selectSellerState = computed(() => {
  return carts.value.every((item) => {
    return item.checkAll === true
  })
})

const loadImages = async () => {
  console.log('hi')

  try {
    for (const ele of carts.value) {
      console.log('hi2')
      console.log(ele.cart)

      for (const item of ele.cart) {
        if (item.imageName) {
          console.log('Loading image for:', item.imageName)
          const image_url = await getImageByFileName(item.imageName)
          item.image = image_url
          console.log('Loaded image:', item.image)
        } else {
          console.log('No image name, using default')
          item.image = noImage
        }
      }
    }
  } catch (error) {
    console.error(`Failed to load images:`, error)
  }
}

onMounted(async () => {
  await loadImages()
})

watchEffect(() => {
  carts.value.forEach((cart) => {
    // ถ้าสินค้าทุกตัวในร้านถูกเลือก
    cart.checkAll =
      cart.cart.length > 0 && cart.cart.every((item) => item.checked)
  })

  // sync selectAll ด้วย (ยังใช้ได้เหมือนเดิม)
  selectAll.value =
    carts.value.length > 0 && carts.value.every((cart) => cart.checkAll)
})

const totalPrice = computed(() => {
  return itemSelected.value.reduce(
    (sum, item) => sum + item.productPrice * item.quantity,
    0
  )
})

const queque = ref([])
// เพิ่ม/ลดจำนวน
const updateQuantity = (item, newQuantity) => {
  // ป้องกันไม่ให้ quantity น้อยกว่า 1
  item.quantity = Math.max(0, newQuantity)
}

const callUpdateCart = async (item) => {
  try {
    const data = {
      userId: userId.value,
      productId: item.id,
      imageFileName: item.fileName,
      quantity: item.quantity,
    }

    const response = await updateCart(item.id, data)
    if (response.error) {
      throw new Error(response.error)
    }

    cartStore.updateProductCartById(item)
  } catch (error) {
    hasError.value = error.message
    console.error('Error update cart:', error)
  }
}

const itemExist = (item, arr) => {
  let changeQuantity
  return {
    index: arr.findIndex((ele) => {
      if (ele.id === item.id) {
        changeQuantity = ele
      }
      return ele.id === item.id
    }),
    changeQuantity,
  }
}
const deleteTargetId = ref(null)
const deleteTargetPid = ref(null)

const increaseQty = (item) => {
  if (item.quantity <= item.productQuantity) {
    updateQuantity(item, item.quantity + 1)
    callUpdateCart(item)
  }
  // const itemWait = itemExist(item, queque.value);
  // if (itemWait.index == -1) {
  //   queque.value.push(item);
  // } else {
  //   console.log("else");
  //   itemWait.changeQuantity.quantity++;
  // }
}

const showWarningModal = ref(false)
const showDeleteModal = ref(false)

const confirmDelete = () => {
  console.log('confirm')

  showDeleteModal.value = true
}

const decreaseQty = (item) => {
  // const itemWait = itemExist(item, queque.value);
  // if (itemWait.index == -1) {
  console.log('0')
  if (item.quantity === 1) {
    console.log('before set')

    console.log('id' + item.id)
    console.log('pid' + item.productId)
    deleteTargetId.value = item.id
    console.log(deleteTargetId.value)

    deleteTargetPid.value = item.productId
    console.log('after set')
    confirmDelete()
    return
  }

  updateQuantity(item, item.quantity - 1)
  if (item.quantity !== 0) {
    callUpdateCart(item)
  }
  //   queque.value.push(item);
  // } else {
  //   console.log("else");
  //   itemWait.changeQuantity.quantity--;
  // }
}

const checkAllItem = (i) => {
  const isAllTrue = carts.value[i].cart.every((item) => {
    return item.checked
  })
  const isAllFalse = carts.value[i].cart.every((item) => {
    return !item.checked
  })
  if (isAllTrue || isAllFalse) {
    carts.value[i].cart.forEach((item) => {
      item.checked = !item.checked
      return
    })
  } else {
    carts.value[i].cart.forEach((item) => {
      if (item.checked === true) {
        item.checked = false
      }
      item.checked = !item.checked
      return
    })
  }
}

const productLength = computed(() => {
  let countItem = 0
  if (carts.value) {
    carts.value.forEach((sellerCart) => {
      sellerCart.cart.forEach((item) => {
        countItem++
      })
    })
  }
  return countItem
})

const removeCart = async (id, pid) => {
  try {
    showDeleteModal.value = false
    console.log(id)

    if (id) {
      const response = await deleteCartById(id)
      if (response.success) {
        const hasSaleItem = cartStore.findProductById(pid)
        if (hasSaleItem.found) {
          cartStore.deleteProductById(pid)
          deleteTargetId.value = null
          deleteTargetPid.value = null
          return
        } else {
          showWarningModal.value = true
          deleteTargetId.value = null
          deleteTargetPid.value = null
          return
        }
      } else {
        throw new Error('An error has occurred, the cart does not exist.')
      }
    }
  } catch (error) {
    console.error(error)

    // errorMessage.value = error.message;
    // isNotFound.value = true;
  }
}
// const hasUpdateQueue = ref(false);
// watchEffect(() => {
//   if (!hasUpdateQueue.value) {
//     hasUpdateQueue.value = true;
//     setTimeout(() => {
//       console.log("callApi");

//       queque.value.forEach((ele) => {
//         callUpdateCart(ele)
//       });
//     }, 5000);
//   }
// });
const shippingAddress = ref('')
const orderNote = ref('')
const orderStore = useOrderStore()
const handlePlaceOrder = async () => {
  try {
    if (!shippingAddress.value || shippingAddress.value === '') {
      console.log('Please enter a shipping address.')
      return
      
    }

    const orderRequestList = []

    carts.value.forEach((sellerCart) => {
      const selectedItems = sellerCart.cart.filter((item) => item.checked)
      if (selectedItems.length === 0) {
        return
      }
      const orderItems = selectedItems.map((item) => ({
        saleItemId: item.productId,
        price: item.productPrice,
        quantity: item.quantity,
        description:
          item.productBrandName +
          ' ' +
          item.productModel +
          ' (' +
          item.productStorageGb +
          'GB,' +
          item.productColor +
          ')',
      }))

      const sellerId = sellerCart.productSellerId

      orderRequestList.push({
        buyerId: userId.value,
        sellerId,
        shippingAddress: shippingAddress.value,
        orderNote: orderNote.value,
        orderItems,
        orderStatus: 'COMPLETED',
      })
    })

    if (orderRequestList.length === 0) {
      return
    }

    console.log('📦 Order Payload:', JSON.stringify(orderRequestList, null, 2))

    const response = await createOrder(orderRequestList)

    if (response.error) {
      throw new Error(response.error)
    }

    orderStore.addOrder(response)

    carts.value.forEach((sellerCart, i) => {
      sellerCart.cart = sellerCart.cart.filter((item) => !item.checked)
    })

    carts.value = carts.value.filter((sellerCart) => sellerCart.cart.length > 0)

    cartStore.setCart(carts.value)

    shippingAddress.value = ''
    orderNote.value = ''

    sessionStorage.removeItem('pageInfo')

    router.push({ name: 'order' })
  } catch (error) {
    console.error(error)
  }
}
</script>

<template>
  <Breadcrumbs :crumbs="[{ label: 'Cart' }]" />
  <div class="w-full h-full px-10 pb-10 md:px-20 md:pb-20">
    <div
      v-if="!cartStore.carts && hasError !== '' && hasError"
      class="w-full h-full p-12 flex justify-center items-center rounded-lg bg-red-100 border-1 border-red-300"
    >
      <p class="text-red-500 font-extralight">{{ hasError }}</p>
    </div>
    <!-- Header Section -->
    <div class="w-full flex flex-col lg:flex-row justify-between items-center">
      <div class="w-full flex flex-col gap-10 md:flex-row justify-between">
        <div class="w-full mx-auto">
          <h1 class="font-bold text-2xl mb-4 sm:mb-6 md:mb-10 lg:mb-0">
            Shopping Cart ({{ productLength }})
          </h1>

          <div
            class="flex gap-3 items-center text-md mt-4 bg-white w-full p-4 rounded-xl"
          >
            <input
              type="checkbox"
              v-model="selectAll"
              @change="
                carts.forEach((cart) => {
                  cart.checkAll = selectAll
                  cart.cart.forEach((item) => (item.checked = selectAll))
                })
              "
              class="checkbox checkbox-primary itbms-select-all"
            />
            Select All
          </div>

          <div v-for="(cart, i) in carts">
            <div
              v-if="cart.cart.length > 0"
              class="flex flex-col mt-4 bg-white w-full p-4 rounded-xl itbms-row"
            >
              <!-- ร้านค้า -->
              <div class="flex gap-3 items-center text-md">
                <input
                  type="checkbox"
                  v-model="cart.checkAll"
                  @change="checkAllItem(i)"
                  class="checkbox checkbox-primary itbms-select-nickname"
                />

                <p class="font-bold itbms-nickname">
                  {{ cart.productSellerNickname }}
                </p>
              </div>
              <hr class="my-3 text-base-300" />

              <!-- รายการสินค้าในร้าน -->
              <div
                v-for="item in cart.cart"
                :key="item.id"
                class="flex flex-col gap-6 md:flex-row items-center justify-between mb-6 itbms-item-row"
              >
                <div class="flex flex-col gap-14 md:flex-row">
                  <div class="flex items-center gap-5">
                    <input
                      type="checkbox"
                      v-model="item.checked"
                      class="checkbox checkbox-primary"
                    />

                    <img
                      v-if="item.image"
                      :src="item.image"
                      alt="Product image"
                      class="w-16 aspect-square rounded-2xl"
                      :class="'object-cover'"
                    />
                    <img
                      v-else
                      :src="noImage"
                      alt="Product image"
                      class="w-16 aspect-square rounded-2xl"
                      :class="'object-contain'"
                    />
                  </div>
                  <span class="itbms-item-description text-sm lg:text-lg">{{
                    item.productBrandName +
                    ' ' +
                    item.productModel +
                    ' (' +
                    item.productStorageGb +
                    'GB,' +
                    item.productColor +
                    ')'
                  }}</span>
                </div>
                <div class="flex gap-6 items-center">
                  <div class="quantity flex items-center gap-2">
                    <button
                      class="btn btn-sm itbms-dec-qty-button"
                      :disabled="item.quantity === 0"
                      @click="decreaseQty(item)"
                    >
                      -
                    </button>

                    <span class="itbms-item-quantity">{{ item.quantity }}</span>
                    <button
                      class="btn btn-sm itbms-inc-qty-button"
                      :disabled="item.quantity === item.productQuantity"
                      @click="increaseQty(item)"
                    >
                      +
                    </button>
                  </div>

                  <span
                    class="price font-bold text-blue-600 itbms-item-total-price"
                  >
                    {{ item.productPrice * item.quantity }} ฿
                  </span>
                  <div
                    @click="removeCart(item.id, item.productId)"
                    class="text-black/20 cursor-pointer"
                  >
                    X
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Summary -->
        <div class="flex flex-col h-fit bg-white w-60 md:w-80 p-4 rounded-xl">
          <div>
            <p class="font-bold text-2xl mb-4 sm:mb-6 md:mb-10 lg:mb-0">
              Cart Summary
            </p>
             <hr class="my-3 text-base-300" />

            <!-- Ship To Section -->
            <div class="mt-4 mb-4">
              <p class="font-bold text-lg mb-2">Ship To</p>
              <label class="text-sm mb-1 block">
                <span class="font-bold">Address </span>
                <span class="text-gray-400"
                  >(Address No, Street, Subdistrict, District, Province, Postal
                  Code)</span
                >
              </label>
              <textarea
                v-model.trim="shippingAddress"
                class="textarea textarea-bordered w-full h-24 text-sm"
                placeholder="123/45 Moo 6, T. Bangna, A. Bangna, Bangkok 10260"
              ></textarea>
            </div>

            <!-- Note Section -->
            <div class="mb-4">
              <p class="font-bold text-sm mb-2">Note</p>
              <textarea
                v-model.trim="orderNote"
                class="textarea textarea-bordered w-full h-20 text-sm"
                placeholder="Additional instructions or requests"
              ></textarea>
            </div>

             <hr class="my-3 text-base-300" />

            <div class="mt-3">
              <div class="flex justify-between">
                <p class="text-black/30">Total items</p>
                <p>{{ itemSelected.length }}</p>
              </div>
              <div class="flex justify-between">
                <p class="text-black/30">Total price</p>
                <p>{{ totalPrice.toLocaleString() }} ฿</p>
              </div>
            </div>
          </div>
          <button
            class="mt-6 btn btn-primary w-full"
            :disabled="itemSelected.length === 0"
            @click="handlePlaceOrder"
          >
            Place order
          </button>
        </div>
      </div>
    </div>
    <dialog class="modal" :open="showDeleteModal" v-if="showDeleteModal">
      <div class="modal-box">
        <h3 class="text-lg font-bold">Delete Confirmation</h3>

        <p class="itbms-message py-4">
          Do you want to remove the sale item from the cart?
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
            @click="removeCart(deleteTargetId, deleteTargetPid)"
            class="btn btn-success itbms-confirm-button"
          >
            Confirm
          </button>
        </div>
      </div>
    </dialog>

    <dialog class="modal" :open="showWarningModal" v-if="showWarningModal">
      <div class="modal-box">
        <h3 class="text-lg font-bold">Cannot Delete</h3>

        <p class="itbms-message py-3">This cart is not found</p>
        <div class="modal-action">
          <form method="dialog">
            <button
              @click="showWarningModal = false"
              class="btn btn-error itbms-cancel-button"
            >
              Cancel
            </button>
          </form>
        </div>
      </div>
    </dialog>
  </div>
</template>

<style scoped>
.cart-item {
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}
</style>
