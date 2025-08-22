import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useNotiStore = defineStore('Noti', () => {
  const updateSuccess = ref(false);
  const createSuccess = ref(false);

  return { updateSuccess, createSuccess };
});
