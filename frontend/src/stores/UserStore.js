import { ref } from "vue";
import { defineStore, acceptHMRUpdate } from "pinia";

export const useUserStore = defineStore("user", () => {
  const userInfo = ref({});

  const getNickName = () => {
    return userInfo.value?.nickName;
  };
  const getUserInfo = () => {
    return userInfo.value;
  };
  const getUserType = () => {
    return userInfo.value?.userType;
  };
  const hasInfo = () => {
    return Object.keys(userInfo.value).length != 0;
  };
  const setUserInfo = (user) => {
    userInfo.value = user;
  };
  const getUserId = () => userInfo.value.id;
  return {
    getNickName,
    getUserInfo,
    hasInfo,
    setUserInfo,
    getUserType,
    getUserId,
  };
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useUserStore, import.meta.hot));
}
