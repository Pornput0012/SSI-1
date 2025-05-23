<script setup>
import { getAllBrands } from "@/services/BrandService";
import { useBrandStore } from "@/stores/BrandStore";
import { computed, onMounted, ref, watch, watchEffect } from "vue";
import { useRouter } from "vue-router";
const router = useRouter();
const emit = defineEmits(["onSubmit"]);
const { getBrands } = useBrandStore();
const brands = computed(() => getBrands());
const isSave = ref(true);

const props = defineProps({
  saleItemDetail: {
    type: Object,
  },
  disableSaveBtn: Boolean,
});
const formData = ref({
  brand: {
    id: 0,
    name: props.saleItemDetail?.brandName || "",
  },
  model: props.saleItemDetail?.model || "",
  price: props.saleItemDetail?.price || null,
  description: props.saleItemDetail?.description || "",
  ramGb: props.saleItemDetail?.ramGb || null,
  screenSizeInch: props.saleItemDetail?.screenSizeInch || null,
  storageGb: props.saleItemDetail?.storageGb || null,
  color: props.saleItemDetail?.color || "",
  quantity: props.saleItemDetail?.quantity || null,
});

const validate = ref({
  brand: { msg: "Brand must be selected", invalid: false, notNull: true },
  model: {
    msg: "Model must be 1-60 characters long.",
    notNull: true,
    maxLength: 60,
    invalid: false,
  },
  color: {
    msg: "Color must be 1-40 characters long or not specified.",
    maxLength: 40,
    invalid: false,
  },
  price: {
    msg: "Price must be non-negative integer",
    notNull: true,
    invalid: false,
  },
  description: {
    msg: "Description must be 1-65,535 characters long.",
    notNull: true,
    maxLength: 65535,
    invalid: false,
  },
  quantity: {
    msg: "Quantity must be non-negative integer",
    notNull: true,
    invalid: false,
  },
  ramGb: {
    msg: "RAM size must be positive integer or not specified",
    invalid: false,
  },
  screenSizeInch: {
    msg: "Screen size must be positive number with at most 2 decimal points or not specified",
    invalid: false,
  },
  storageGb: {
    msg: "Storage size must be positive integer or not specified",
    invalid: false,
  },
});

const errorTitle = ref("");
const errorMessage = ref("");
const showModal = ref(false);

onMounted(async () => {
  if (brands.value === null) {
    try {
      const response = await getAllBrands();
      if (response.error) {
        throw new Error(response.error);
      }
    } catch (e) {
      errorTitle.value = "Something Wrong!";
      errorMessage.value = e.message;
      showModal.value = true;
    }
  }
});

const requireField = ["brand", "model", "price", "description"];
const fieldCheckOldData = [
  "model",
  "price",
  "description",
  "ramGb",
  "screenSizeInch",
  "storageGb",
  "quantity",
];

const handleSubmit = () => {
  const validateField = [...requireField, "quantity"];
  for (const field of validateField) {
    if (validate.value[field].msg) {
      const invalidFields = [];
      if (validate.value[field].msg) {
        invalidFields.push(field);
      }
      errorTitle.value = "Invalid Input";
      errorMessage.value = `Please check your input field : ${invalidFields.join(
        ", "
      )}`;
      showModal.value = true;
      return;
    }
  }

  for (const field of requireField) {
    if (!formData.value[field] || formData.value[field].id === 0) {
      console.log(field + "2");
      errorTitle.value = "Missing Required Field";
      errorMessage.value = `Please fill out the required field: ${field}`;
      showModal.value = true;
      return;
    }
  }

  console.log(formData.value);

  emit("onSubmit", formData.value);
};

function maximumLength(field) {
  console.log(formData.value[field].length);

  if (
    (formData.value[field] &&
      formData.value[field].length > validate.value[field].maxLength) ||
    (validate.value[field].notNull && formData.value[field].length < 1)
  ) {
    validate.value[field].invalid = true;
  } else {
    validate.value[field].invalid = false;
  }
}
watchEffect(() => {
  if (brands.value && brands.value.length > 0 && formData.value.brand.name) {
    const foundBrand = brands.value.find(
      (brand) => brand.name === formData.value.brand.name
    );
    if (foundBrand) {
      formData.value.brand = foundBrand;
    }
  }
});
const normalize = (val) =>
  val === null || val === undefined ? "" : val.trim?.() ?? val;

watchEffect(() => {
  for (const field of requireField) {
    if (!formData.value[field] || formData.value[field].id == 0) {
      isSave.value = true; // ยังกรอกไม่ครบ ไม่ควรบันทึก
      return;
    }
  }
  let hasChanges = false;

  if (formData.value.brand.name !== props.saleItemDetail?.brandName) {
    hasChanges = true;
  }

  if (!hasChanges) {
    for (const field of fieldCheckOldData) {
      if (formData.value[field] !== props.saleItemDetail[field]) {
        hasChanges = true;
        break;
      }
    }

    if (formData.value.color !== normalize(props.saleItemDetail.color)) {
      hasChanges = true;
    }
  }

  isSave.value = !hasChanges;
});

function brandSelected() {
  if (
    (!formData.value.brand || formData.value.brand.id === 0) &&
    !validate.value.brand.notNull
  ) {
    validate.value.brand.invalid = true;
    return;
  } else {
    validate.value.brand.invalid = false;
  }
}
function nonNegative(field) {
  if (
    (!formData.value[field]&& formData.value[field] !== 0) ||
    (formData.value[field] && formData.value[field] < 0)
  ) {
    validate.value[field].invalid = true;
    return;
  } else {
    validate.value[field].invalid = false;
  }
}

function isValidPositiveField(field) {
  if (!formData.value[field] && formData.value[field] !== 0) {
    validate.value[field].invalid = false;
    return;
  }
  const parsed = parseFloat(formData.value[field]);
  if (isNaN(parsed) || parsed <= 0) {
    validate.value[field].invalid = true;
    return;
  } else if (field === "screenSizeInch") {
    const decimalPart = formData.value[field].toString().split(".")[1];
    validate.value[field].invalid = decimalPart && decimalPart.length > 2;
    return;
  } else {
    validate.value[field].invalid = false;
  }
}

// const notNull = [...requireField, "quantity"];

// watch(
//   () => formData.value.brand,
//   () => {
//     if (
//       (!formData.value.brand || formData.value.brand.id === 0) &&
//       !validate.value.brand.notNull
//     ) {
//       validate.value.brand.msg = "Brand is required";
//     } else {
//       validate.value.brand.msg = "";
//     }
//   }
// );

// watch(
//   () => formData.value.model,
//   () => {
//     if (!formData.value.model && !validate.value.model.notNull) {
//       validate.value.model.msg = "Model is required";
//     } else if (
//       formData.value.model &&
//       formData.value.model.length > validate.value.model.maxLength
//     ) {
//       validate.value.model.msg = `Model must be less than ${validate.value.model.maxLength} characters.`;
//     } else {
//       validate.value.model.msg = "";
//     }
//   }
// );

// watch(
//   () => formData.value.description,
//   () => {
//     if (!formData.value.description && !validate.value.description.notNull) {
//       validate.value.description.msg = "Description is required";
//     } else {
//       validate.value.description.msg = "";
//     }
//   }
// );

// watch(
//   () => formData.value.price,
//   () => {
//     if (!formData.value.price && !validate.value.price.notNull) {
//       validate.value.price.msg = "Price is required";
//     } else {
//       validate.value.price.msg = "";
//     }
//   }
// );

// watch(
//   () => formData.value.quantity,
//   () => {
//     if (!formData.value.quantity && !validate.value.quantity.notNull) {
//       validate.value.quantity.msg = "Quantity is required";
//     } else {
//       validate.value.quantity.msg = "";
//     }
//   }
// );
</script>

<template>
  <div class="mt-10 lg:mt-0 lg:mx-16 mb-14">
    <div class="flex flex-col lg:flex-row">
      <div class="w-full lg:w-2/4 mx-auto overflow-auto mb-10 lg:mb-10">
        <div class="border-2 rounded-2xl w-4/5 md:w-2/3 mx-auto">
          <img src="/pictures/iPhone15Pro.png" alt="" />
        </div>
        <div
          class="flex gap-4 mt-10 w-full justify-center items-center overflow-auto"
        >
          <img
            v-for="i in 4"
            class="h-full w-14 lg:w-18 border rounded-2xl"
            :key="i"
            src="/pictures/iPhone15Pro.png"
            alt=""
          />
        </div>
      </div>
      <div
        class="flex flex-col items-center lg:items-start justify-center lg:justify-start xl:w-2/4"
      >
        <div
          class="grid grid-cols-[auto_1fr] md:grid-cols-[auto_260px] xl:grid-cols-[auto_320px] items-center gap-x- md:gap-x-6 gap-y-4 px-4"
        >
          <label class="text-sm md:text-base" for="brand"
            >Brand<span class="text-red-500"> *</span></label
          >
          <div class="flex-col">
            <select
              id="brand"
              class="itbms-brand select"
              v-model="formData.brand"
              @blur="brandSelected"
            >
              <option :value="{ id: 0, name: '' }" disabled>
                Select Brand
              </option>
              <option
                v-for="(brand, i) in brands
                  ?.sort((a, b) => a.name.localeCompare(b.name))
                  ?.filter((b) => b.isActive === true)"
                :value="brand"
                :key="i"
              >
                {{ brand.name }}
              </option>
            </select>
            <div
              class="text-red-500 mt-2 flex gap-2 text-sm"
              v-if="validate.brand.invalid"
            >
              <span class="font-extralight">{{ validate.brand.msg }}</span>
            </div>
          </div>

          <label class="text-sm md:text-base" for="model"
            >Model<span class="text-red-500"> *</span></label
          >

          <div class="flex-col">
            <input
              type="text"
              id="model"
              class="itbms-model input"
              v-model.trim="formData.model"
              :class="{
                'border-red-500 focus:border-red-500 focus:ring-red-500 bg-red-50':
                  validate.model.invalid,
              }"
              @blur="maximumLength('model')"
            />
            <div
              class="text-red-500 mt-2 flex gap-2 text-sm"
              v-if="validate.model.invalid"
            >
              <span class="font-extralight">{{ validate.model.msg }}</span>
            </div>
          </div>

          <label class="text-sm md:text-base" for="price"
            >Price(Bath)<span class="text-red-500"> *</span></label
          >
          <div class="flex-col">
            <input
              type="number"
              id="price"
              class="itbms-price input"
              v-model="formData.price"
              :class="{
                'border-red-500 focus:border-red-500 focus:ring-red-500 bg-red-50':
                  validate.price.invalid,
              }"
              @blur="nonNegative('price')"
            />
            <div
              class="text-red-500 mt-2 flex gap-2 text-sm"
              v-if="validate.price.invalid"
            >
              <span class="font-extralight">{{ validate.price.msg }}</span>
            </div>
          </div>

          <label class="text-sm md:text-base" for="description"
            >Description<span class="text-red-500"> *</span></label
          >
          <div class="flex-col">
            <textarea
              id="description"
              class="itbms-description textarea resize-y"
              v-model.trim="formData.description"
              :class="{
                'border-red-500 focus:border-red-500 focus:ring-red-500 bg-red-50':
                  validate.description.invalid,
              }"
              @blur="maximumLength('description')"
            ></textarea>
            <div
              class="text-red-500 mt-2 flex gap-2 text-sm"
              v-if="validate.description.invalid"
            >
              <span class="font-extralight">{{
                validate.description.msg
              }}</span>
            </div>
          </div>

          <label class="text-sm md:text-base" for="ramGb">Ram (GB)</label>

          <div class="flex-col">
            <input
              type="number"
              id="ramGb"
              class="itbms-ramGb input"
              :class="{
                'border-red-500 focus:border-red-500 focus:ring-red-500 bg-red-50':
                  validate.ramGb.invalid,
              }"
              min="1"
              v-model="formData.ramGb"
              @blur="isValidPositiveField('ramGb')"
            />
            <div
              class="text-red-500 mt-2 flex gap-2 text-sm"
              v-if="validate.ramGb.invalid"
            >
              <span class="font-extralight">{{ validate.ramGb.msg }}</span>
            </div>
          </div>

          <label class="text-sm md:text-base max-w-[110px]" for="screenSizeInch"
            >Screen Size (Inch)</label
          >
          <div class="flex-col">
            <input
              type="number"
              id="screenSizeInch"
              class="itbms-screenSizeInch input"
              :class="{
                'border-red-500 focus:border-red-500 focus:ring-red-500 bg-red-50':
                  validate.screenSizeInch.invalid,
              }"
              step="0.01"
              min="1"
              v-model="formData.screenSizeInch"
              @blur="isValidPositiveField('screenSizeInch')"
            />
            <div
              class="text-red-500 mt-2 flex gap-2 text-sm"
              v-if="validate.screenSizeInch.invalid"
            >
              <span class="font-extralight">{{
                validate.screenSizeInch.msg
              }}</span>
            </div>
          </div>

          <label class="text-sm md:text-base" for="storageGb"
            >Storage (GB)</label
          >
          <div class="flex-col">
            <input
              type="number"
              id="storageGb"
              class="itbms-storageGb input"
              min="1"
              v-model="formData.storageGb"
              @blur="isValidPositiveField('storageGb')"
              :class="{
                'border-red-500 focus:border-red-500 focus:ring-red-500 bg-red-50':
                  validate.storageGb.invalid,
              }"
            />
            <div
              class="text-red-500 mt-2 flex gap-2 text-sm"
              v-if="validate.storageGb.invalid"
            >
              <span class="font-extralight">{{
                validate.storageGb.msg
              }}</span>
            </div>
          </div>

          <label class="text-sm md:text-base" for="color">Color</label>
          <div class="flex-col">
            <input
              type="text"
              id="color"
              class="itbms-color input"
              v-model.trim="formData.color"
              :class="{
                'border-red-500 focus:border-red-500 focus:ring-red-500 bg-red-50':
                  validate.color.invalid,
              }"
              @blur="maximumLength('color')"
            />
            <div
              class="text-red-500 mt-2 flex gap-2 text-sm"
              v-if="validate.color.invalid"
            >
              <span class="font-extralight">{{ validate.color.msg }}</span>
            </div>
          </div>

          <label class="text-sm md:text-base" for="quantity"
            >Quantity<span class="text-red-500"> *</span></label
          >
          <div class="flex-col">
            <input
              type="number"
              id="quantity"
              class="itbms-quantity input"
              min="1"
              value="1"
              v-model="formData.quantity"
              :class="{
                'border-red-500 focus:border-red-500 focus:ring-red-500 bg-red-50':
                  validate.quantity.invalid,
              }"
              @blur="nonNegative('quantity')"
            />
            <div
              class="text-red-500 mt-2 flex gap-2 text-sm"
              v-if="validate.quantity.invalid"
            >
              <span class="font-extralight">{{ validate.quantity.msg }}</span>
            </div>
          </div>

          <div
            class="flex items- jus w-full gap-4 col-span-2 justify-center mt-4 lg:justify-end"
          >
            <button
              @click="handleSubmit"
              class="itbms-save-button btn"
              :class="isSave || disableSaveBtn ? '' : 'btn-primary'"
              :disabled="isSave || disableSaveBtn"
            >
              Save
            </button>

            <button @click="$router.back()" class="itbms-cancel-button btn">
              Cancle
            </button>
          </div>
        </div>
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
