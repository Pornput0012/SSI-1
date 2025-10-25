<script setup>
import { ref, watch, watchEffect } from "vue";
import { forgotPassword } from "@/services/userService";
import MessageModal from "@/components/MessageModal.vue";
import router from "@/router";

const email = ref("");
const onSubmit = ref(false);
const disableSaveBtn = ref(true);

const errorTitle = ref("");
const errorMessage = ref("");
const showModal = ref(false);
const successTitle = ref("");
const successMessage = ref("");
const showSuccessModal = ref(false);

const validate = ref({
	email: { invalid: false, msg: "" },
});

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

watch(
	() => email.value,
	() => {
		checkEmail();
	}
);

watchEffect(() => {
	disableSaveBtn.value = true;
	if (!email.value) return;
	for (const [_, value] of Object.entries(validate.value)) {
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
		const res = await forgotPassword(email.value);
		onSubmit.value = false;

		if (res.success) {
			successTitle.value = "Reset Email Sent";
			successMessage.value =
				"If the email exists in our system, a password reset link has been sent.";
			showSuccessModal.value = true;
		} else {
			errorTitle.value = "Failed to send reset email";
			errorMessage.value = res.error || "Unknown error";
			showModal.value = true;
		}
	} catch (err) {
		onSubmit.value = false;
		errorTitle.value = "Failed to send reset email";
		errorMessage.value = err.message || "Unknown error";
		showModal.value = true;
	}
};

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
					<h1 class="text-4xl font-bold mb-4 leading-tight">
						Forgot your password?
					</h1>

					<p class="text-xl text-indigo-100 mb-8 leading-relaxed">
						Enter your email and we'll send a link to reset your password.
					</p>
				</div>
			</div>
		</div>

		<div
			class="w-full lg:w-1/2 min-h-screen flex flex-col justify-center items-center gap-4 py-16 px-8"
		>
			<h1 class="text-3xl mb-6 text-indigo-700 font-medium">Reset Password</h1>

			<form @submit.prevent="handleSubmit" class="w-full max-w-md space-y-6">
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
						<span class="font-extralight itbms-message">{{ validate.email.msg }}</span>
					</div>
				</div>

				<button
					type="submit"
					:disabled="disableSaveBtn || onSubmit"
					class="itbms-signin-button"
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
					Send Reset Link
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
				:handle-ok="closeAndGoToLogin"
				:title="successTitle"
                type-modal="text-green-400"
				:message="successMessage"
			/>
		</div>
	</div>
</template>
 
<style scoped></style>