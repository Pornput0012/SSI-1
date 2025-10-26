import { ref } from "vue";
import { defineStore, acceptHMRUpdate } from "pinia";

export const useUserStore = defineStore("user", () => {
  const userInfo = ref({});
  const accessToken = ref(null);
  const getAccessToken = () => accessToken.value;
  const setAccessToken = (token) => (accessToken.value = token);
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
    return userInfo.value && Object.keys(userInfo.value).length != 0;
  };
  const setUserInfo = (user) => {
    userInfo.value = user;
  };
  const clearUserInfo = () => {
    userInfo.value = {};
    sessionStorage.removeItem("userInfo");
    sessionStorage.removeItem("pageInfo");
    sessionStorage.removeItem("orderPageInfo");
    sessionStorage.removeItem("sellerSaleItemPageInfo");
  };
  const getUserId = () => userInfo.value.id;

  const waitForUser = (timeout = 500) => {
    return new Promise((resolve, reject) => {
      const start = Date.now();

      const check = () => {
        if (Object.keys(userInfo.value) != 0) {
          resolve(userInfo);
        } else if (Date.now() - start >= timeout) {
          reject(new Error("Timeout waiting for user id"));
        } else {
          setTimeout(check, 100);
        }
      };
      check();
    });
  };
  return {
    getNickName,
    getUserInfo,
    hasInfo,
    setUserInfo,
    getUserType,
    getUserId,
    waitForUser,
    clearUserInfo,
    getAccessToken,
    setAccessToken
  };
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useUserStore, import.meta.hot));
}
