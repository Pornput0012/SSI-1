import { ref } from "vue";
import { defineStore, acceptHMRUpdate } from "pinia";

export const useUserStore = defineStore("user", () => {
  const userInfo = ref({});

  const getNickName = () => {
    console.log(userInfo.value);
    
    return userInfo.value?.nickName
  };
  const getUserInfo = () => {
    return userInfo.value
  }
  const hasInfo = () => {
    return Object.keys(userInfo.value).length != 0;
  }
  const setUserInfo = (user) =>{
    userInfo.value = user
  }
  return { getNickName, getUserInfo, hasInfo, setUserInfo };
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useUserStore, import.meta.hot));
}
