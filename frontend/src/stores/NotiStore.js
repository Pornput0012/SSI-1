import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useNotiStore = defineStore('Noti', () => {
  const updateSuccess = ref(false);
  const createSuccess = ref(false);
  const createUserSuccess = ref(false)
  const loginUserSuccess = ref(false)
  const orderCreateSuccess = ref(false)

  const addToCartFail = ref(false)
  return { updateSuccess, createSuccess, createUserSuccess, loginUserSuccess, orderCreateSuccess, addToCartFail };
});
