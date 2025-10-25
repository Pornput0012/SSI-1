<script setup>
import { useToastStore } from '@/stores/ToastStore'

const toastStore = useToastStore()
</script>

<template>
  <!-- ✅ Global Toast Container -->
  <div class="toast toast-bottom toast-end z-50 flex flex-col-reverse gap-2">
    <TransitionGroup name="toast">
      <div
        v-for="toast in toastStore.toasts"
        :key="toast.id"
        class="alert flex justify-between items-center gap-2 min-w-[100px] shadow-lg font-bold"
        :class="{
          'alert-success': toast.type === 'success',
          'alert-error': toast.type === 'error',
          'alert-info': toast.type === 'info',
        }"
      >
        <span>{{ toast.message }}</span>
        <button
          @click="toastStore.removeToast(toast.id)"
          class="btn btn-xs btn-circle btn-ghost"
        >
          ✕
        </button>
      </div>
    </TransitionGroup>
  </div>
</template>

<style scoped>
.toast-enter-active {
  animation: slideInRight 0.3s ease-out;
}

.toast-leave-active {
  animation: slideOutRight 0.3s ease-in;
}

.toast-move {
  transition: transform 0.3s ease;
}

@keyframes slideInRight {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

@keyframes slideOutRight {
  from {
    transform: translateX(0);
    opacity: 1;
  }
  to {
    transform: translateX(100%);
    opacity: 0;
  }
}
</style>