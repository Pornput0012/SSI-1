<script setup>
const props = defineProps({
  message: {
    type: String,
    required: true,
  },
  title: {
    type: String,
    required: true,
  },
  handleOk: {
    type: Function,
    required: true,
  },
  typeModal: {
    type: String,
    default: "text-red-500",
  },
});
</script>

<template>
  <div class="modal modal-open">
    <div class="modal-box text-center">
      <h3
        class="font-bold text-[14px] md:text-[16px] lg:text-[18px]"
        :class="typeModal"
      >
        {{ title }}
      </h3>
      <p class="itbms-message py-4 text-[12px] md:text-[14px] lg:text-[16px]">
        {{ message }}
      </p>
      <div class="itbms-button modal-action justify-center">
        <button @click="handleOk" class="btn btn-primary bg-purple-700">
          OK
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
