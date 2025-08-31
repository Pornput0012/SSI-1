<script setup>
import { ref, watch, watchEffect } from "vue";
import { useRouter } from "vue-router";
import MessageModal from "./MessageModal.vue";


const emit = defineEmits(["onSubmit"]);
const props = defineProps({
  disableSaveBtn: Boolean,
  clearData:Boolean
});

const isCanNotSave = ref(true);

let roles = ["Buyer", "Seller"];
const userType = ref(roles[0]);

const router = useRouter();

const errorTitle = ref("");
const errorMessage = ref("");
const showModal = ref(false);

const formDataBuyer = ref({
  nickName: "",
  email: "",
  password: "",
  fullName: "",
});

const formDataSeller = ref({
  ...formDataBuyer.value,
  phoneNumber: "",
  bankAccountNumber: "",
  bankName: "",
  idCardNumber: "",
});

const buyerValidate = ref({
  nickName: { invalid: false, msg: "" },
  email: { invalid: false, msg: "" },
  password: { invalid: false, msg: "" },
  fullName: { invalid: false, msg: "" },
});

const sellerValidate = ref({
  nickName: { invalid: false, msg: "" },
  email: { invalid: false, msg: "" },
  password: { invalid: false, msg: "" },
  fullName: { invalid: false, msg: "" },
  phoneNumber: { invalid: false, msg: "" },
  bankAccountNumber: { invalid: false, msg: "" },
  bankName: { invalid: false, msg: "" },
  idCardNumber: { invalid: false, msg: "" },
});

const formConstraints = {
  Buyer: [
    {
      title: "Nickname",
      label: "nickname",
      model: "nickName",
      constraint: { type: "string", required: true },
    },
    {
      title: "Email",
      label: "email",
      model: "email",
      constraint: { type: "string", required: true, unique: true },
    },
    {
      title: "Password",
      label: "password",
      model: "password",
      constraint: {
        type: "string",
        required: true,
        minLength: 8,
        rules: ["lower", "upper", "number", "special"],
      },
    },
    {
      title: "Fullname",
      label: "fullname",
      model: "fullName",
      constraint: {
        type: "string",
        required: true,
        minLength: 4,
        maxLength: 40,
      },
    },
  ],

  Seller: [
    {
      title: "Mobile number",
      label: "mobile",
      model: "phoneNumber",
      constraint: { type: "string", required: true },
    },
    {
      title: "Bank account number",
      label: "bank-account-number",
      model: "bankAccountNumber",
      constraint: { type: "string", required: true },
    },
    {
      title: "Bank name",
      label: "bank-name",
      model: "bankName",
      constraint: { type: "string", required: true },
    },
    {
      title: "National ID number",
      label: "card-no",
      model: "idCardNumber",
      constraint: { type: "string", required: true },
    },
  ],
};

const validateField = () => {};

const checkPassword = (password, userType) => {
  const validateState = {
    Buyer: buyerValidate,
    Seller: sellerValidate,
  };

  const targetValidate = validateState[userType]; // เลือก state ที่ต้องใช้
  const passwordConstraint = formConstraints.Buyer[2].constraint;

  const rules = {
    lower: /[a-z]/,
    upper: /[A-Z]/,
    number: /[0-9]/,
    special: /[^A-Za-z0-9]/,
  };

  if (passwordConstraint.required && !password) {
    targetValidate.value.password.invalid = true;
    targetValidate.value.password.msg = `Password must be filled`;
    return;
  }

  if (password.length < passwordConstraint.minLength) {
    targetValidate.value.password.invalid = true;
    targetValidate.value.password.msg = `Password must be at least ${passwordConstraint.minLength} characters`;
    return;
  }

  let failedRules = [];
  for (let rule of passwordConstraint.rules) {
    if (!rules[rule].test(password)) {
      failedRules.push(rule);
    }
  }

  if (failedRules.length > 0) {
    targetValidate.value.password.invalid = true;
    targetValidate.value.password.msg = `Password must contain at least one ${failedRules.join(
      ", "
    )} character`;
  } else {
    targetValidate.value.password.invalid = false;
    targetValidate.value.password.msg = ``;
  }
};

const checkEmail = (email, userType) => {
  const emailConstraint = formConstraints.Buyer[1].constraint;

  const validate =
    userType === "Seller" ? sellerValidate.value : buyerValidate.value;

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  if (emailConstraint.required && !email) {
    validate.email.invalid = true;
    validate.email.msg = `Email must be filled`;
    return;
  }

  if (!emailRegex.test(email)) {
    validate.email.invalid = true;
    validate.email.msg = `Email format is invalid`;
    return;
  }

  validate.email.invalid = false;
  validate.email.msg = ``;
};

const frontImage = ref(null);
const backImage = ref(null);

const previewBinaryFile = (binaryFile) => URL.createObjectURL(binaryFile);

function uploadImage(event, side) {
  const file = event.target.files[0];
  if (!file) return;

  const fileName = file.name.toLowerCase();
  const MAX_SIZE_BYTES = 2 * 1024 * 1024;

  if (file && file.size > MAX_SIZE_BYTES) {
    errorTitle.value = "File Size Too Large";
    errorMessage.value = `File "${file.name}" exceeds the 2MB limit.`;
    showModal.value = true;
    return;
  }
  if (
    fileName.endsWith(".jpg") ||
    fileName.endsWith(".jpeg") ||
    fileName.endsWith(".png")
  ) {
    if (side === "front") {
      frontImage.value = {
        fileName: fileName,
        file: { src: previewBinaryFile(file), image: file },
      };
    } else {
      backImage.value = {
        fileName: fileName,
        file: { src: previewBinaryFile(file), image: file },
      };
    }
  } else {
    console.log("invalid type");
  }
}

function removeFrontImage() {
  frontImage.value = null;
}
function removeBackImage() {
  backImage.value = null;
}

const handleSubmit = () => {
  if (userType.value === "Seller") {
    emit("onSubmit", {
      userDetail: formDataSeller.value,
      images: {
        idCardImageFront: frontImage.value?.file.image,
        idCardImageBack: backImage.value?.file.image,
      },
      userType: "Seller",
    });
  } else {
    emit("onSubmit", { userDetail: formDataBuyer.value, userType: "Buyer" });
  }

  if (props.clearData) {
    clearData()
    
  }
};

function clearData(role = null) {
  const onToggle = role !== null;

  if (onToggle && role === "Seller") {
    formDataBuyer.value = {
      nickName: "",
      email: "",
      password: "",
      fullName: "",
    };
    removeBackImage()
    removeFrontImage();
    return;
  }

  if (onToggle && role === "Buyer") {
    formDataSeller.value = {
      ...formDataBuyer.value,
      phoneNumber: "",
      bankAccountNumber: "",
      bankName: "",
      idCardNumber: "",
    };
    return;
  }

  formDataBuyer.value = {
    nickName: "",
    email: "",
    password: "",
    fullName: "",
  };

  formDataSeller.value = {
    ...formDataBuyer.value,
    phoneNumber: "",
    bankAccountNumber: "",
    bankName: "",
    idCardNumber: "",
  };
  removeBackImage()
    removeFrontImage();
}

watch(
  [() => formDataSeller.value.password, () => formDataBuyer.value.password],
  ([newSellerPassword, newBuyerPassword]) => {
    checkPassword(newSellerPassword, "Seller");
    checkPassword(newBuyerPassword, "Buyer");
  }
);

watch(
  [() => formDataSeller.value.email, () => formDataBuyer.value.email],
  ([newSellerEmail, newBuyerEmail]) => {
    checkEmail(newSellerEmail, "Seller");
    checkEmail(newBuyerEmail, "Buyer");
  }
);

watchEffect(() => {
  let isValid = true;
  let validate = userType.value == "Seller" ? sellerValidate.value : buyerValidate.value
  let dataToCheck,
    imagesToSend = null;
  if (userType.value === "Seller") {
    dataToCheck = { ...formDataSeller.value, userType: "Seller" };
    imagesToSend = {
      front: frontImage.value?.file.src,
      back: backImage.value?.file.src,
    };
    for (const [key, value] of Object.entries(validate)) {
      if (value.invalid) {
        isValid = false;
      }
    }
  } else {
    dataToCheck = { ...formDataBuyer.value, userType: "Buyer" };
    for (const [key, value] of Object.entries(validate)) {
      if (value.invalid) {
        isValid = false;
      }
    }
  }

  const hasImage = imagesToSend?.front && imagesToSend?.back;
  const emptyFields = Object.entries(dataToCheck).filter(
    ([key, value]) => value === "" || value === null
  );

  if (emptyFields.length > 0) {
    isValid = false;
  }

  if (userType.value === "Seller" && !hasImage) {
    isValid = false;
  }

  isCanNotSave.value = !isValid;
});
</script>

<template>
  <div class="relative w-full h-full min-h-screen flex justify-center">
    <div
      class="absolute inset-0 w-1/2 min-h-screen h-full bg-gradient-to-br from-indigo-700 via-purple-700 to-blue-700 hidden lg:block"
    ></div>
    <!-- Left Banner Section -->
    <div
      class="w-1/2 h-screen bg-gradient-to-br from-indigo-700 via-purple-700 to-blue-700 hidden lg:flex lg:flex-col lg:justify-center lg:items-center text-white"
    >
      <!-- Main Content -->
      <div class="z-10 text-center px-12">
        <div class="mb-8">
          <!-- Icon -->
          <div
            class="mask mask-squircle bg-white w-20 h-20 flex items-center justify-center mb-4 mx-auto"
          >
            <img src="/logo.png" alt="" />
          </div>
          <!-- Main Title -->
          <h1 class="text-4xl font-bold mb-4 leading-tight">
            Join Our
            <span
              class="bg-gradient-to-r from-yellow-200 to-pink-200 bg-clip-text text-transparent"
            >
              Marketplace
            </span>
          </h1>

          <!-- Subtitle -->
          <p class="text-xl text-indigo-100 mb-8 leading-relaxed">
            Connect with thousands of buyers and sellers in our secure, trusted
            platform
          </p>

          <!-- Features List -->
          <div class="space-y-4 text-left max-w-sm mx-auto">
            <div class="flex items-center space-x-3">
              <div class="w-2 h-2 bg-green-400 rounded-full"></div>
              <span class="text-indigo-100">Secure transactions</span>
            </div>
            <div class="flex items-center space-x-3">
              <div class="w-2 h-2 bg-green-400 rounded-full"></div>
              <span class="text-indigo-100">24/7 customer support</span>
            </div>
            <div class="flex items-center space-x-3">
              <div class="w-2 h-2 bg-green-400 rounded-full"></div>
              <span class="text-indigo-100">Fast & easy setup</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div
      class="w-full lg:w-1/2 min-h-screen flex flex-col justify-center items-center gap-4 py-16"
    >
      <h1 class="text-3xl mb-6 text-indigo-700 font-medium">Create account</h1>
      <div
        class="itbms-account-type flex items-center space-x-2 bg-gray-200 rounded-lg p-1 w-fit"
      >
        <label
          v-for="role in roles"
          :key="role"
          class="px-4 py-1 rounded-lg cursor-pointer transition"
          :class="
            userType === role ? 'bg-indigo-600 text-white' : 'text-gray-700'
          "
        >
          <input
            type="radio"
            v-model="userType"
            @click="clearData(role)"
            :value="role"
            class="hidden"
          />
          {{ role }}
        </label>
      </div>

      <div class="max-w-full flex flex-col gap-2">
        <div
          v-if="userType == 'Buyer'"
          v-for="field in formConstraints.Buyer"
          class="flex flex-col gap-1"
        >
          <label class="text-sm md:text-base" :for="field.label"
            >{{ field.title
            }}<span
              class="text-red-500"
              v-show="field?.constraint?.required === true"
            >
              *</span
            ></label
          >

          <div class="flex-col">
            <input
              :type="
                field.label == 'password'
                  ? 'password'
                  : field.label == 'email'
                  ? 'email'
                  : 'text'
              "
              :id="field.label"
              class="input w-64 lg:w-80"
              :class="[
                'itbms-' + field.label,
                {
                  'border-red-500 focus:border-red-500 focus:ring-red-500 bg-red-50':
                    buyerValidate[field.model].invalid,
                },
              ]"
              v-model.trim="formDataBuyer[field.model]"
            />
            <div class="flex-col w-64 lg:w-80">
              <div
                class="text-red-500 mt-2 flex gap-2 text-sm"
                v-if="buyerValidate[field.model].invalid"
              >
                <span class="font-extralight itbms-message">{{
                  buyerValidate[field.model].msg
                }}</span>
              </div>
            </div>
          </div>
        </div>
        <div
          v-else
          v-for="field in [...formConstraints.Buyer, ...formConstraints.Seller]"
          class="flex flex-col gap-1"
        >
          <label class="text-sm md:text-base" :for="field.label"
            >{{ field.title
            }}<span
              class="text-red-500"
              v-show="field?.constraint?.required === true"
            >
              *</span
            ></label
          >

          <div class="flex-col">
            <input
              :type="
                field.label == 'password'
                  ? 'password'
                  : field.label == 'email'
                  ? 'email'
                  : 'text'
              "
              :id="field.label"
              class="itbms-model input w-64 lg:w-80"
              :class="[
                'itbms-' + field.label,
                {
                  'border-red-500 focus:border-red-500 focus:ring-red-500 bg-red-50':
                    sellerValidate[field.model].invalid,
                },
              ]"
              v-model.trim="formDataSeller[field.model]"
            />
            <div class="flex-col w-64 lg:w-80">
              <div
                class="text-red-500 mt-2 flex gap-2 text-sm"
                v-if="sellerValidate[field.model].invalid"
              >
                <span class="font-extralight itbms-message">{{
                  sellerValidate[field.model].msg
                }}</span>
              </div>
            </div>
          </div>
        </div>
        <div v-if="userType == 'Seller'">
          <label class="text-sm md:text-base" for="nationalIdPhoto">
            National ID photo<span class="text-red-500"> *</span>
          </label>

          <!-- Front Side -->
          <p class="my-2 text-sm text-purple-900/70">Front</p>
          <div class="flex-col mb-4">
            <div
              class="w-64 h-44 lg:w-80 lg:h-52 flex justify-center items-center overflow-hidden relative border-2 border-dashed border-gray-300 rounded-lg p-4 hover:border-purple-400 transition-colors"
            >
              <!-- Image Preview or Placeholder -->
              <div v-if="frontImage?.file.src" class="relative">
                <img
                  :src="frontImage?.file.src"
                  alt="National ID Front"
                  class="w-full h-48 object-cover rounded-lg"
                />
                <button
                  @click="removeFrontImage"
                  class="z-10 cursor-pointer absolute top-2 right-2 outline-1 outline-black bg-base-300/70 text-black rounded-full w-6 h-6 flex items-center justify-center hover:bg-base-content/70 hover:text-white transition-colors"
                >
                  ×
                </button>
              </div>

              <!-- Placeholder when no image -->
              <div
                v-else
                class="w-64 h-44 lg:w-80 lg:h-52 overflow-hidden flex flex-col items-center justify-center text-gray-500"
              >
                <svg
                  class="w-12 h-12 mb-2"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"
                  ></path>
                </svg>
                <p class="text-sm">Click to upload front side</p>
                <p class="text-xs text-gray-400 mt-1">PNG, JPG up to 2MB</p>
              </div>

              <!-- Hidden file input -->
              <input
                type="file"
                ref="frontUploadInput"
                accept="image/*"
                @change="uploadImage($event, 'front')"
                class="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
              />
            </div>
          </div>

          <!-- Back Side -->
          <p class="my-2 text-sm text-purple-900/70">Back</p>
          <div class="flex-col">
            <div
              class="w-64 h-44 lg:w-80 lg:h-52 flex justify-center items-center overflow-hidden relative border-2 border-dashed border-gray-300 rounded-lg p-4 hover:border-purple-400 transition-colors"
            >
              <!-- Image Preview or Placeholder -->
              <div v-if="backImage?.file.src" class="relative">
                <img
                  :src="backImage?.file.src"
                  alt="National ID Back"
                  class="w-full h-48 object-cover rounded-lg"
                />
                <button
                  @click="removeBackImage"
                  class="z-10 cursor-pointer absolute top-2 right-2 outline-1 outline-black bg-base-300/70 text-black rounded-full w-6 h-6 flex items-center justify-center hover:bg-base-content/70 hover:text-white transition-colors"
                >
                  ×
                </button>
              </div>

              <!-- Placeholder when no image -->
              <div
                v-else
                class="w-64 h-44 lg:w-80 lg:h-52 overflow-hidden flex flex-col items-center justify-center text-gray-500"
              >
                <svg
                  class="w-12 h-12 mb-2"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"
                  ></path>
                </svg>
                <p class="text-sm">Click to upload back side</p>
                <p class="text-xs text-gray-400 mt-1">PNG, JPG up to 2MB</p>
              </div>

              <!-- Hidden file input -->
              <input
                type="file"
                ref="backUploadInput"
                accept="image/*"
                @change="uploadImage($event, 'back')"
                class="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
              />
            </div>
          </div>
        </div>
      </div>
      <div class="flex gap-3 mt-6 w-64 lg:w-80 justify-end">
        <button
          class="itbms-submit-button btn btn-primary flex items-center justify-center gap-2"
          :class="disableSaveBtn ? 'cursor-not-allowed' : ''"
          @click="handleSubmit"
          :disabled="isCanNotSave || disableSaveBtn"
        >
          <svg v-if="disableSaveBtn" class="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"></path>
          </svg>
          <span>{{ disableSaveBtn ? 'Submitting...' : 'Submit' }}</span>
        </button>
        <button
          @click="$router.back()"
          class="itbms-cancel-button btn bg-base-200 shadow-sm text-indigo-700"
        >
          Cancel
        </button>
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

<style></style>
