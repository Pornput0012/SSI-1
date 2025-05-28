<script setup>
import { getAllBrands } from "@/services/BrandService";
import { useBrandStore } from "@/stores/BrandStore";
import { computed, onMounted, ref, watch, watchEffect } from "vue";
import MessageModal from "./MessageModal.vue";

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
  brand: props.saleItemDetail?.brandName || "",
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
  brand: { msg: "Brand must be selected.", showError: false, notNull: true },
  model: {
    msg: "Model must be 1-60 characters long.",
    notNull: true,
    maxLength: 60,
    invalid: false,
    showError: false,
  },
  color: {
    msg: "Color must be 1-40 characters long or not specified.",
    invalid: false,
    maxLength: 40,
    showError: false,
  },
  price: {
    msg: "Price must be non-negative integer.",
    invalid: false,
    notNull: true,
    nonNeg: true,
    showError: false,
  },
  description: {
    msg: "Description must be 1-16,384 characters long.",
    invalid: false,
    notNull: true,
    maxLength: 16384,
    showError: false,
  },
  quantity: {
    msg: "Quantity must be non-negative integer.",
    invalid: false,
    notNull: true,
    nonNeg: true,
    showError: false,
  },
  ramGb: {
    msg: "RAM size must be positive integer or not specified.",
    invalid: false,
    positive: true,
    showError: false,
  },
  screenSizeInch: {
    msg: "Screen size must be positive number with at most 2 decimal points or not specified.",
    invalid: false,
    positive: true,
    showError: false,
  },
  storageGb: {
    msg: "Storage size must be positive integer or not specified.",
    invalid: false,
    positive: true,
    showError: false,
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

const validateField = [...fieldCheckOldData, "brand", "color"];
const handleSubmit = () => {
  for (const field of requireField) {
    if (
      (!formData.value[field] && formData.value[field] !== 0) ||
      formData.value[field] === "" || // เพิ่มการเช็ค empty string
      formData.value[field].id === 0
    ) {
      errorTitle.value = "Missing Required Field";
      errorMessage.value = `Please fill out the required field: ${field}`;
      showModal.value = true;
      return;
    }
  }

  emit("onSubmit", formData.value);
};

const allValid = computed(() => {
  return validateField.every(
    (field) =>
      !validate.value[field]?.showError && !validate.value[field]?.invalid
  );
});

//false xerror->blur
//true xerror->blur
function maximumLength(field, blur) {
  if (
    (formData.value[field] &&
      formData.value[field].length > validate.value[field].maxLength) ||
    (validate.value[field].notNull && formData.value[field].length < 1)
  ) {
    validate.value[field].invalid = true;

    if (blur) {
      // เช็คตอน blur แล้วผิด → ให้แสดง error
      validate.value[field].showError = true;
    }
  } else {
    validate.value[field].invalid = false;
    validate.value[field].showError = false;
    // ถ้าข้อมูลถูก → ลบ error ทิ้งเสมอ ไม่ว่าจะ blur หรือไม่
  }
}

function brandSelected(blur) {
  if (
    !formData.value.brand ||
    formData.value.brand === "" ||
    formData.value.brand.id === 0
  ) {
    validate.value.brand.invalid = true;
    if (blur) {
      validate.value.brand.showError = true;
    }
    return;
  } else {
    validate.value.brand.invalid = false;
    validate.value.brand.showError = false;
  }
}
function nonNegative(field, blur) {
  if (
    (!formData.value[field] && formData.value[field] !== 0) ||
    (formData.value[field] && formData.value[field] < 0)
  ) {
    validate.value[field].invalid = true;

    if (blur) {
      // เช็คตอน blur แล้วผิด → ให้แสดง error
      validate.value[field].showError = true;
    }

    return;
  } else {
    validate.value[field].invalid = false;
    validate.value[field].showError = false;
  }
}

function isValidPositiveField(field, blur) {
  if (!formData.value[field] && formData.value[field] !== 0) {
    validate.value[field].showError = false;
    validate.value[field].invalid = false;
    return;
  }

  const parsed = parseFloat(formData.value[field]);

  // กรณีค่าที่กรอกไม่ใช่ตัวเลข หรือ <= 0
  if (isNaN(parsed) || parsed <= 0) {
    validate.value[field].invalid = true;
    if (blur) {
      validate.value[field].showError = true;
    }
    return;
  }

  if (field === "screenSizeInch") {
    const decimalPart = formData.value[field].toString().split(".")[1];
    const hasTooManyDecimals = decimalPart && decimalPart.length > 2;

    if (hasTooManyDecimals) {
      validate.value[field].invalid = true;
      if (blur) {
        validate.value[field].showError = true;
      }
    } else {
      validate.value[field].showError = false;
      validate.value[field].invalid = false;
    }

    return;
  }

  // ถ้าผ่านทุกเงื่อนไขแล้ว → ถือว่าถูกต้อง ลบ error ทิ้ง
  validate.value[field].showError = false;
  validate.value[field].invalid = false;
}

watchEffect(() => {
  if (brands.value && brands.value.length > 0 && formData.value.brand) {
    const foundBrand = brands.value.find(
      (brand) => brand.name === formData.value.brand
    );
    if (foundBrand) {
      formData.value.brand = foundBrand;
    }
  }
});
const normalize = (val) =>
  val === null || val === undefined ? "" : val.trim?.() ?? val;

["quantity", "price"].forEach((field) => {
  watch(
    () => formData.value[field],
    () => nonNegative(field, false)
  );
});
["model", "description"].forEach((field) => {
  watch(
    () => formData.value[field],
    () => maximumLength(field, false)
  );
});
["ramGb", "screenSizeInch", "storageGb"].forEach((field) => {
  watch(
    () => formData.value[field],
    () => isValidPositiveField(field, false)
  );
});

watch(
  () => formData.value.brand,
  () => brandSelected(false)
);

watch(
  () => formData.value.color,
  () => {
    formData.value.color = normalize(formData.value.color);
    maximumLength("color", false);
  }
);

watchEffect(() => {
  if (!allValid.value) {
    isSave.value = true;
    return;
  }

  for (const field of requireField) {
    if (
      (!formData.value[field] && formData.value[field] !== 0) ||
      formData.value[field].id == 0
    ) {
      isSave.value = true; // ยังกรอกไม่ครบ ไม่ควรบันทึก
      return;
    }
  }

  // console.log(isSave.value);

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
  // console.log(isSave.value);
});
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
              @blur="brandSelected(true)"
            >
              <option value="">Select Brand</option>
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
              v-if="validate.brand.showError"
            >
              <span class="font-extralight itbms-message">{{
                validate.brand.msg
              }}</span>
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
                  validate.model.showError,
              }"
              @blur="maximumLength('model', true)"
            />
            <div
              class="text-red-500 mt-2 flex gap-2 text-sm"
              v-if="validate.model.showError"
            >
              <span class="font-extralight itbms-message">{{
                validate.model.msg
              }}</span>
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
                  validate.price.showError,
              }"
              @blur="nonNegative('price', true)"
            />
            <div
              class="text-red-500 mt-2 flex gap-2 text-sm"
              v-if="validate.price.showError"
            >
              <span class="font-extralight itbms-message">{{
                validate.price.msg
              }}</span>
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
                  validate.description.showError,
              }"
              @blur="maximumLength('description', true)"
            ></textarea>
            <div
              class="text-red-500 mt-2 flex gap-2 text-sm"
              v-if="validate.description.showError"
            >
              <span class="font-extralight itbms-message">{{
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
                  validate.ramGb.showError,
              }"
              min="1"
              v-model="formData.ramGb"
              @blur="isValidPositiveField('ramGb', true)"
            />
            <div
              class="text-red-500 mt-2 flex gap-2 text-sm"
              v-if="validate.ramGb.showError"
            >
              <span class="font-extralight itbms-message">{{
                validate.ramGb.msg
              }}</span>
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
                  validate.screenSizeInch.showError,
              }"
              step="0.01"
              min="1"
              v-model="formData.screenSizeInch"
              @blur="isValidPositiveField('screenSizeInch', true)"
            />
            <div
              class="text-red-500 mt-2 flex gap-2 text-sm"
              v-if="validate.screenSizeInch.showError"
            >
              <span class="font-extralight itbms-message">{{
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
              @blur="isValidPositiveField('storageGb', true)"
              :class="{
                'border-red-500 focus:border-red-500 focus:ring-red-500 bg-red-50':
                  validate.storageGb.showError,
              }"
            />
            <div
              class="text-red-500 mt-2 flex gap-2 text-sm"
              v-if="validate.storageGb.showError"
            >
              <span class="font-extralight itbms-message">{{
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
                  validate.color.showError,
              }"
              @blur="maximumLength('color', true)"
            />
            <div
              class="text-red-500 mt-2 flex gap-2 text-sm"
              v-if="validate.color.showError"
            >
              <span class="font-extralight itbms-message">{{
                validate.color.msg
              }}</span>
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
                  validate.quantity.showError,
              }"
              @blur="nonNegative('quantity', true)"
            />
            <div
              class="text-red-500 mt-2 flex gap-2 text-sm"
              v-if="validate.quantity.showError"
            >
              <span class="font-extralight itbms-message">{{
                validate.quantity.msg
              }}</span>
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
  <MessageModal
    v-if="showModal"
    :handle-ok="() => (showModal = false)"
    :title="errorTitle"
    :message="errorMessage"
  />
</template>

<style scoped></style>
