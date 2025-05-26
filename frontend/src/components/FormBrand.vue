<script setup>
import { ref, watchEffect } from "vue";

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
  isActive: props.brandDetail?.isActive ?? true,
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

const validate = ref({
  name: { msg: "", maxLength: 30, nullable: false },
  websiteUrl: { msg: "", maxLength: 40, nullable: true },
  countryOfOrigin: { msg: "", maxLength: 80, nullable: true },
});

const validateField = [...requireField, ...emptyCheck];
const pattern = /^https?:\/\/.+/;

function isValidUrl() {
  if (!pattern.test(formData.value.websiteUrl)) {
    console.log();
    
    validate.value.websiteUrl.msg = `Brand URL must be a valid URL or not specified`;
    return ;
  }
  else {
    validate.value.websiteUrl.msg = "";
  }

}

function maximumLength(field) {
  const labels = ["name", "country of origin"];
  const matchedLabel =
    labels.find((label) => {
      const normalizedLabel = label.toLowerCase().replace(/\s+/g, ""); //ลบช่องว่าง \s
      return field.toLowerCase().includes(normalizedLabel);
    }) || field;
  if (
    (formData.value[field] &&
    formData.value[field].length > validate.value[field].maxLength) || (field === "name" && !formData.value.name)
  ) {
    console.log(formData.value.length );
    
    validate.value[field].msg =
      `Brand ${matchedLabel} must be 1-${validate.value[field].maxLength} characters long` +
      (matchedLabel === "country of origin" ? " or not specified." : ".");
  } else {
    validate.value[field].msg = "";
  }
}


watchEffect(() => {

  let isInvalid = false;

  for (const field of validateField) {
    if (
      (formData.value[field] &&
      validate.value[field].maxLength &&
      formData.value[field].length > validate.value[field].maxLength) 
    ) {
      isInvalid = true;
      
    } 
    else if(field !== "websiteUrl" || (field === "websiteUrl" && !formData.value.websiteUrl)){
      validate.value[field].msg = ""
    }
  }

  if(formData.value.websiteUrl && !pattern.test(formData.value.websiteUrl)) {
      isInvalid = true
   }
   else {
    validate.value.websiteUrl.msg = ""
   }

  if (
    !formData.value?.name ||
    validate.value.name.msg ||
    validate.value.countryOfOrigin.msg ||
    validate.value.websiteUrl.msg ||
    isInvalid
  ) {
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

  if (
    validate.value.name.msg ||
    validate.value.countryOfOrigin.msg ||
    validate.value.websiteUrl.msg
  ) {
    isCanNotSave.value = true;
    return;
  }
  
  isCanNotSave.value = !hasChanges;
});


</script>

<template>
  <div class="mt-10 lg:mt-0 md:mx-16 mb-14">
    <div class="grid gap-y-4 gap-x-6 px-4 grid-cols-1 sm:grid-cols-[auto_1fr]">
      <label class="text-sm md:text-base" for="name"
        >Name<span class="text-red-500"> *</span></label
      >
      <div class="flex-col">
        <input
        type="text"
        id="name"
        class="itbms-name input w-full"
        :class="{
            'border-red-500 focus:border-red-500 focus:ring-red-500 bg-red-50':
              validate.name.msg !== '',
          }"
          v-model.trim="formData.name"
          @blur="maximumLength('name')"
        />
        <div
          class="text-red-500 mt-2 flex gap-2 text-sm"
          v-if="validate.name.msg !== ''"
        >
          <span class="font-extralight">{{ validate.name.msg }}</span>
        </div>
      </div>

      <label class="text-sm md:text-base" for="websiteUrl">Website Url</label>
      <div class="flex-col">
        <input
          type="text"
          id="websiteUrl"
          class="itbms-websiteUrl input w-full"
          :class="{
            'border-red-500 focus:border-red-500 focus:ring-red-500 bg-red-50':
              validate.websiteUrl.msg !== '',
          }"
          v-model.trim="formData.websiteUrl"
          @blur="isValidUrl"
        />
        <div
          class="text-red-500 mt-2 flex gap-2 text-sm"
          v-if="validate.websiteUrl.msg !== ''"
        >
          <span class="font-extralight">{{ validate.websiteUrl.msg }}</span>
        </div>
      </div>

      <div class="flex gap-2 sm:gap-24 col-span-1 sm:col-span-2 items-center">
        <label class="text-sm md:text-base" for="isActive">Active</label>
        <input
          type="checkbox"
          checked="checked"
          class="itbms-isActive toggle border-gray-600 bg-gray-500 checked:border-green-500 checked:bg-green-400 checked:text-green-800"
          id="isActive"
          v-model.trim="formData.isActive"
        />
      </div>

      <label class="text-sm md:text-base" for="countryOfOrigin"
        >Country of origin</label
      >

      <div class="flex-col">
        <input
          type="text"
          id="countryOfOrigin"
          class="itbms-countryOfOrigin input w-full"
          :class="{
            'border-red-500 focus:border-red-500 focus:ring-red-500 bg-red-50':
              validate.countryOfOrigin.msg !== '',
          }"
          v-model.trim="formData.countryOfOrigin"
          @blur="maximumLength('countryOfOrigin')"
        />
        <div
          class="text-red-500 mt-2 flex gap-2 text-sm"
          v-if="validate.countryOfOrigin.msg !== ''"
        >
          <span class="font-extralight">{{
            validate.countryOfOrigin.msg
          }}</span>
        </div>
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
