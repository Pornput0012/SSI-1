<script setup>
import { onMounted, ref, watch, watchEffect } from "vue";
import { useRoute, useRouter } from "vue-router";
import { resetPassword } from "@/services/userService";
import MessageModal from "@/components/MessageModal.vue";

const route = useRoute();
const router = useRouter();

const token = ref(route.query.token || "");
const password = ref("");
const confirmPassword = ref("");

const onSubmit = ref(false);
const disableSaveBtn = ref(true);

const errorTitle = ref("");
const errorMessage = ref("");
const showModal = ref(false);
const successTitle = ref("");
const successMessage = ref("");
const showSuccessModal = ref(false);

const validate = ref({
  password: { invalid: false, msg: "" },
  confirmPassword: { invalid: false, msg: "" },
});

const checkPassword = () => {
  // Use same constraints as AuthenForm.vue: minLength 8 and rules [lower, upper, number, special]
  const passwordConstraint = { minLength: 8, rules: ["lower", "upper", "number", "special"] };

  const rules = {
    lower: /[a-z]/,
    upper: /[A-Z]/,
    number: /[0-9]/,
    special: /[^A-Za-z0-9]/,
  };

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

  if (password.value.length < passwordConstraint.minLength) {
    validate.value.password.invalid = true;
    validate.value.password.msg = `Password must be at least ${passwordConstraint.minLength} characters`;
    return;
  }

  if (password.value.length > 50) {
    validate.value.password.invalid = true;
    validate.value.password.msg = `Password must be less than 50 characters`;
    return;
  }

  let failedRules = [];
  for (let rule of passwordConstraint.rules) {
    if (!rules[rule].test(password.value)) {
      failedRules.push(rule);
    }
  }

  if (failedRules.length > 0) {
    validate.value.password.invalid = true;
    validate.value.password.msg = `Password must contain at least one ${failedRules.join(", ")} character`;
    return;
  }

  validate.value.password.invalid = false;
  validate.value.password.msg = ``;
};

const checkConfirmPassword = () => {
  if (!confirmPassword.value) {
    validate.value.confirmPassword.invalid = true;
    validate.value.confirmPassword.msg = `Confirm password must be filled`;
    return;
  }

  if (confirmPassword.value !== password.value) {
    validate.value.confirmPassword.invalid = true;
    validate.value.confirmPassword.msg = `Passwords do not match`;
    return;
  }

  validate.value.confirmPassword.invalid = false;
  validate.value.confirmPassword.msg = ``;
};

watch(
  () => password.value,
  () => checkPassword()
);
watch(
  () => confirmPassword.value,
  () => checkConfirmPassword()
);

watchEffect(() => {
  disableSaveBtn.value = true;

  if (!token.value) return;
  if (!password.value || !confirmPassword.value) return;

  for (const [key, value] of Object.entries(validate.value)) {
    if (value.invalid) {
      disableSaveBtn.value = true;
      return;
    }
  }

  disableSaveBtn.value = false;
});

const handleSubmit = async () => {
  onSubmit.value = true;
  try {
    const body = {
      newPassword: password.value,
      confirmPassword: confirmPassword.value,
    };
    const res = await resetPassword(token.value, body);
    onSubmit.value = false;

    if (res.success) {
      successTitle.value = "Password Reset";
      successMessage.value =
        "Your password has been reset. You can now sign in.";
      showSuccessModal.value = true;
    } else {
      errorTitle.value = "Failed to reset password";
      errorMessage.value = res.error || "Unknown error";
      showModal.value = true;
    }
  } catch (err) {
    onSubmit.value = false;
    errorTitle.value = "Failed to reset password";
    errorMessage.value = err.message || "Unknown error";
    showModal.value = true;
  }
};

onMounted(() => {
  if (!token.value) {
    router.push({ name: "sale-items" });
  }
});

const closeAndGoToLogin = () => {
  showSuccessModal.value = false;
  router.push({ name: "signin" });
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
          <h1 class="text-4xl font-bold mb-4 leading-tight">Reset Password</h1>

          <p class="text-xl text-indigo-100 mb-8 leading-relaxed">
            Enter a new password for your account.
          </p>
        </div>
      </div>
    </div>

    <div
      class="w-full lg:w-1/2 min-h-screen flex flex-col justify-center items-center gap-4 py-16 px-8"
    >
      <h1 class="text-3xl mb-6 text-indigo-700 font-medium">Reset Password</h1>

      <form @submit.prevent="handleSubmit" class="w-full max-w-md space-y-6">
        <div class="space-y-2 itbms-password">
          <label for="password" class="block text-sm font-medium text-gray-700">
            New Password
          </label>
          <input
            id="password"
            v-model="password"
            maxlength="50"
            type="password"
            required
            class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-colors"
            placeholder="Enter new password"
          />
          <div
            class="text-red-500 mt-2 flex gap-2 text-sm"
            v-if="validate.password.invalid"
          >
            <span class="font-extralight itbms-message">{{
              validate.password.msg
            }}</span>
          </div>
        </div>

        <div class="space-y-2 itbms-password">
          <label
            for="confirmPassword"
            class="block text-sm font-medium text-gray-700"
          >
            Confirm New Password
          </label>
          <input
            id="confirmPassword"
            v-model="confirmPassword"
            maxlength="50"
            type="password"
            required
            class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-colors"
            placeholder="Confirm new password"
          />
          <div
            class="text-red-500 mt-2 flex gap-2 text-sm"
            v-if="validate.confirmPassword.invalid"
          >
            <span class="font-extralight itbms-message">{{
              validate.confirmPassword.msg
            }}</span>
          </div>
        </div>

        <div v-if="!token" class="text-sm text-red-600">
          Missing or invalid token.
        </div>

        <button
          type="submit"
          :disabled="disableSaveBtn || onSubmit || !token"
          class="itbms-signin-button"
          :class="[
            disableSaveBtn || onSubmit || !token
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
          Reset Password
        </button>

        <div class="text-center">
          <p class="text-sm text-gray-600">
            Remembered your password?
            <router-link
              :to="{ name: 'signin' }"
              class="text-indigo-600 hover:text-indigo-500 font-medium transition-colors"
            >
              Sign in
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

      <MessageModal
        v-if="showSuccessModal"
        type-modal="text-green-400"
        :handle-ok="closeAndGoToLogin"
        :title="successTitle"
        :message="successMessage"
      />
    </div>
  </div>
</template>

<style scoped></style>
