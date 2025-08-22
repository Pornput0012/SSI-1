<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";

const props = defineProps({
  userDetail: {
    type: Object,
  },
  disableSaveBtn: Boolean,
});
const userType = ref("Buyer");

let roles = ["Buyer", "Seller"];

const { router } = useRouter();

const formDataBuyer = ref({
  nickname: props.userDetail?.nickname ?? "",
  email: props.userDetail?.email || "",
  password: props.userDetail?.password ?? "",
  fullname: props.userDetail?.fullname || "",
  userType: "",
});

const formDataSeller = ref({
  ...formDataBuyer.value,
  mobileNumber: props.userDetail?.mobileNumber ?? "",
  bankAccountNumber: props.userDetail?.bankAccountNumber || "",
  bankName: props.userDetail?.bankName ?? "",
  nationalIdNumber: props.userDetail?.nationalIdNumber || "",
});

const formConstraints = {
  Buyer: [
    {
      title: "Nickname",
      label: "nickname",
      constraint: { type: "string", required: true },
    },
    {
      title: "Email",
      label: "email",
      constraint: { type: "string", required: true, unique: true },
    },
    {
      title: "Password",
      label: "password",
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
      label: "mobileNumber",
      constraint: { type: "string", required: true },
    },
    {
      title: "Bank account number",
      label: "bankAccountNumber",
      constraint: { type: "string", required: true },
    },
    {
      title: "Bank name",
      label: "bankName",
      constraint: { type: "string", required: true },
    },
    {
      title: "National ID number",
      label: "nationalIdNumber",
      constraint: { type: "string", required: true },
    },
   
  ],
};
</script>

<template>
  <div class="relative w-full h-full min-h-screen flex">
    <div
      class="absolute inset-0 w-1/2 min-h-screen h-full bg-gradient-to-br from-indigo-700 via-purple-700 to-blue-700"
    ></div>
    <!-- Left Banner Section -->
    <div
      class="w-1/2 h-screen bg-gradient-to-br from-indigo-700 via-purple-700 to-blue-700 flex flex-col justify-center items-center text-white"
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
      class="w-1/2 min-h-screen flex flex-col justify-center items-center gap-4 py-16"
    >
      <h1 class="text-3xl mb-6 text-indigo-700 font-medium">Create account</h1>
      <div class="flex items-center space-x-2 bg-gray-200 rounded-lg p-1 w-fit">
        <label
          v-for="role in roles"
          :key="role"
          class="px-4 py-1 rounded-lg cursor-pointer transition"
          :class="
            userType === role ? 'bg-indigo-600 text-white' : 'text-gray-700'
          "
        >
          <input type="radio" v-model="userType" :value="role" class="hidden" />
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
              type="text"
              :id="field.label"
              class="itbms-model input w-48 sm:w-64 md:w-80"
              :class="'itbms-' + field.label"
            />
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
              type="text"
              :id="field.label"
              class="itbms-model input w-48 sm:w-64 md:w-80"
              :class="'itbms-' + field.label"
            />
          </div>
        </div>
        <div v-if="userType == 'Seller'">
          <label class="text-sm md:text-base" for="nationalIdPhoto"
            >National ID photo<span class="text-red-500"> *</span></label
          >
          <p class="my-2 text-sm text-purple-900/70">front</p>
          <div class="flex-col">
            <input
              type="file"
              ref="uploadInput"
              accept="image/*"
              @change="uploadImage"
              class="itbms-nationalIdPhoto file-input file-input-ghost"
            />
          </div>
          <p class="my-2 text-sm text-purple-900/70 ">Back</p>
          <div class="flex-col">
            <input
              type="file"
              ref="uploadInput"
              accept="image/*"
              @change="uploadImage"
              class="itbms-nationalIdPhoto file-input file-input-ghost"
            />
          </div>
        </div>
      </div>
      <div class="flex gap-3 mt-6 w-48 sm:w-64 md:w-80 justify-end">
        <button
          @click="$router.back()"
          class="btn bg-base-200 shadow-sm text-indigo-700"
        >
          Cancel
        </button>
        <button class="btn btn-primary">Submit</button>
      </div>
    </div>
  </div>
</template>

<style></style>
