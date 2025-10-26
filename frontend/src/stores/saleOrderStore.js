import { ref } from "vue";
import { defineStore, acceptHMRUpdate } from "pinia";

export const useSaleOrderStore = defineStore("saleOrder", () => {
  const saleOrderQuanatity = ref(0);

  function setSaleOrderQuanatity(saleOrderOrQuantity) {
    if (typeof saleOrderOrQuantity === "number") {
      saleOrderQuanatity.value = saleOrderOrQuantity;
      return;
    }

    saleOrderQuanatity.value = saleOrderOrQuantity.content.filter((order) => {
      return (
        order.orderStatus?.toUpperCase() === "COMPLETED" && !order.viewOrder
      );
    }).length;
  }

  function getSaleOrderQuanatity() {
    return saleOrderQuanatity.value;
  }

  return {
    setSaleOrderQuanatity,
    getSaleOrderQuanatity,
  };
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useSaleOrderStore, import.meta.hot));
}
