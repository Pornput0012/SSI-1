// stores/ToastStore.js
import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'

export const useToastStore = defineStore('toast', () => {
  const toasts = ref([])
  let toastIdCounter = 0
  const MAX_TOASTS = 5

  const showToast = (message, type = 'success') => {
    // ลบ toast เก่าถ้าเกิน MAX
    if (toasts.value.length >= MAX_TOASTS) {
      toasts.value.shift()
    }

    const id = toastIdCounter++
    const toast = {
      id,
      message,
      type,
    }

    toasts.value.push(toast)

    // Auto remove หลัง duration
    const duration = type === 'error' ? 4500 : 1500

    setTimeout(() => {
      removeToast(id)
    }, duration)
  }

  const removeToast = (id) => {
    const index = toasts.value.findIndex((t) => t.id === id)
    if (index !== -1) {
      toasts.value.splice(index, 1)
    }
  }

  return {
    toasts,
    showToast,
    removeToast,
  }
})

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useToastStore, import.meta.hot));
}