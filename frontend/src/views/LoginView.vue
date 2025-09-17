<script setup>
import { ref, watch, watchEffect } from "vue";
import { login } from "@/services/userService";
import { useNotiStore } from "@/stores/NotiStore";
import MessageModal from "@/components/MessageModal.vue";
import router from "@/router";


const email = ref("");
const password = ref("");
const showPassword = ref(false);

const errorTitle = ref("");
const errorMessage = ref("");
const showModal = ref(false);
const notiStore = useNotiStore();
const disableSaveBtn = ref(true);
const onSubmit = ref(false);
const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value;
};



const handleLogin = async () => {
  onSubmit.value = true;
  try {
    const response = await login(email.value, password.value);
    onSubmit.value =false
    disableSaveBtn.value = true
    
    if (response.success) {
      loginSuccess();
      console.log("Login success!");
    } else {
      throw new Error(response.error);
    }
  } catch (error) {
    errorTitle.value = "Failed to log in";
    errorMessage.value = error.message;
    showModal.value = true;
  }
};

const validate = ref({
  email: { invalid: false, msg: "" },
  password: { invalid: false, msg: "" },
});

const checkPassword = () => {
  if (!password.value) {
    validate.value.password.invalid = true;
    validate.value.password.msg = `Password must be filled`;
    return;
  }

  if (typeof password.value !== "string") {
    validate.value.password.invalid = true;
    validate.value.password.msg = `Password must be text`;
    return;
  }

  if (password.length > 14) {
    validate.value.password.invalid = true;
    validate.value.password.msg = `Password must be at least ${passwordConstraint.minLength} characters`;
    return;
  }

  validate.value.password.invalid = false;
  validate.value.password.msg = ``;
};

const checkEmail = () => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  if (!email.value) {
    validate.value.email.invalid = true;
    validate.value.email.msg = `Email must be filled.`;
    return;
  }

  if (email.value.length > 50) {
    validate.value.email.invalid = true;
    validate.value.email.msg = `Email must be less than 50 characters.`;
    return;
  }

  if (!emailRegex.test(email.value)) {
    validate.value.email.invalid = true;
    validate.value.email.msg = `Email format is invalid.`;
    return;
  }

  validate.value.email.invalid = false;
  validate.value.email.msg = ``;
};

const closeModal =()=> {
  showModal.value = false
  router.push()
}

watch(
  () => password.value,
  () => {
    checkPassword();
  }
);

watch(
  () => email.value,
  () => {
    checkEmail();
  }
);
watchEffect(() => {
  disableSaveBtn.value = true;

  if (!email.value || !password.value) {
    return;
  }
  for (const [key, value] of Object.entries(validate.value)) {
    if (value.invalid) {
      disableSaveBtn.value = true;
      return;
    } 
  }

  disableSaveBtn.value = false
});

const loginSuccess = () => {
  notiStore.loginUserSuccess = true;
  router.push({ name: "sale-items" });
};
</script>

<template>
  <div class="relative w-full h-full min-h-screen flex justify-center">
    <div
      class="absolute inset-0 w-1/2 min-h-screen h-full bg-gradient-to-br from-indigo-700 via-purple-700 to-blue-700 hidden lg:block"
    ></div>
    <div
      class="w-1/2 h-screen bg-gradient-to-br from-indigo-700 via-purple-700 to-blue-700 hidden lg:flex lg:flex-col lg:justify-center lg:items-center text-white"
    >
      <div class="z-10 text-center px-12">
        <div class="mb-8">
          <div
            class="mask mask-squircle bg-white w-20 h-20 flex items-center justify-center mb-4 mx-auto"
          >
            <img src="/logo.png" alt="" />
          </div>
          <!-- Main Title -->
          <h1 class="text-4xl font-bold mb-4 leading-tight">
            Welcome Back to
            <span
              class="bg-gradient-to-r from-yellow-200 to-pink-200 bg-clip-text text-transparent"
            >
              Marketplace
            </span>
          </h1>

          <p class="text-xl text-indigo-100 mb-8 leading-relaxed">
            Sign in to access your account and continue trading on our secure
            platform
          </p>

          <div class="space-y-4 text-left max-w-sm mx-auto">
            <div class="flex items-center space-x-3">
              <div class="w-2 h-2 bg-green-400 rounded-full"></div>
              <span class="text-indigo-100">Secure login system</span>
            </div>
            <div class="flex items-center space-x-3">
              <div class="w-2 h-2 bg-green-400 rounded-full"></div>
              <span class="text-indigo-100">Protected transactions</span>
            </div>
            <div class="flex items-center space-x-3">
              <div class="w-2 h-2 bg-green-400 rounded-full"></div>
              <span class="text-indigo-100">Instant access</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div
      class="w-full lg:w-1/2 min-h-screen flex flex-col justify-center items-center gap-4 py-16 px-8"
    >
      <h1 class="text-3xl mb-6 text-indigo-700 font-medium">Sign In</h1>

      <form @submit.prevent="handleLogin" class="w-full max-w-md space-y-6">
        <div class="space-y-2 itbms-email">
          <label for="email" class="block text-sm font-medium text-gray-700">
            Email Address
          </label>
          <input
            id="email"
            v-model="email"
            maxlength="50"
            type="email"
            required
            class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-colors"
            placeholder="Enter your email"
          />
          <div
            class="text-red-500 mt-2 flex gap-2 text-sm"
            v-if="validate.email.invalid"
          >
            <span class="font-extralight itbms-message">{{
              validate.email.msg
            }}</span>
          </div>
        </div>

        <div class="space-y-2 itbms-password">
          <label for="password" class="block text-sm font-medium text-gray-700">
            Password
          </label>
          <div class="relative">
            <input
              id="password"
              v-model="password"
              maxlength="14"
              :type="showPassword ? 'text' : 'password'"
              required
              class="w-full px-4 py-3 pr-12 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-colors"
              placeholder="Enter your password"
            />
            <div
              class="text-red-500 mt-2 flex gap-2 text-sm"
              v-if="validate.password.invalid"
            >
              <span class="font-extralight itbms-message">{{
                validate.password.msg
              }}</span>
            </div>
            <button
              type="button"
              @click="togglePasswordVisibility"
              class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-500 hover:text-gray-700 transition-colors"
            >
              <svg
                v-if="!showPassword"
                class="w-5 h-5"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
                />
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"
                />
              </svg>
              <svg
                v-else
                class="w-5 h-5"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.878 9.878L3 3m6.878 6.878L12 12m3 3l6.878 6.878"
                />
              </svg>
            </button>
          </div>
        </div>

        <div class="flex items-center justify-between">
          <label class="flex items-center">
            <input
              type="checkbox"
              class="rounded border-gray-300 text-indigo-600 focus:ring-indigo-500"
            />
            <span class="ml-2 text-sm text-gray-600">Remember me</span>
          </label>
          <button
            type="button"
            class="text-sm text-indigo-600 hover:text-indigo-500 transition-colors"
          >
            Forgot password?
          </button>
        </div>

        <button
          type="submit"
          :disabled="disableSaveBtn || onSubmit"
          :class="[
            disableSaveBtn || onSubmit
              ? 'flex items-center justify-center gap-2 w-full text-white py-3 px-4 rounded-lg font-medium  cursor-not-allowed bg-gray-300 '
              : 'w-full bg-gradient-to-r from-indigo-600 to-purple-600 text-white py-3 px-4 rounded-lg font-medium hover:from-indigo-700 hover:to-purple-700 focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 transition-all transform hover:scale-105',
          ]"
        >
          <svg
            v-if="onSubmit"
            class="animate-spin h-5 w-5 text-white"
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
          >
            <circle
              class="opacity-25"
              cx="12"
              cy="12"
              r="10"
              stroke="currentColor"
              stroke-width="4"
            ></circle>
            <path
              class="opacity-75"
              fill="currentColor"
              d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"
            ></path>
          </svg>
          Sign In
        </button>

        <div class="text-center">
          <p class="text-sm text-gray-600">
            Don't have an account?
            <router-link
              :to="{ name: 'register' }"
              class="text-indigo-600 hover:text-indigo-500 font-medium transition-colors"
            >
              Create account
            </router-link>
          </p>
        </div>
      </form>
      <MessageModal
        v-if="showModal"
        :handle-ok="() => (showModal = false)"
        :title="errorTitle"
        :message="errorMessage"
      />
    </div>
  </div>
</template>

<style scoped></style>
