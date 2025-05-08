import { ref } from "vue";
import { defineStore, acceptHMRUpdate } from "pinia";

export const useSaleItemStore = defineStore("SaleItem", () => {
  const saleItems = ref(null);

  // setter
  function setSaleItems(itemArray) {
    saleItems.value = itemArray;
  }

  // getter
  function getSaleItems() {
    return saleItems.value;
  }

  function getSaleItemById(id) {
    if (!Array.isArray(saleItems.value)) {
      return null;
    }
    const index = saleItems.value.findIndex((item) => item.id === id);
    if (index === -1) {
      return null;
    }
    return saleItems.value[index];
  }

  function deleteSaleItemById(saleItemId) {
    const saleItemIndex = saleItems.value.findIndex(
      (ele) => ele.id === saleItemId
    );
    saleItems.value.splice(saleItemIndex, 1);
  }

  function createSaleItem(saleItem) {
    console.log(saleItem);
    console.log(saleItems.value);
    const createSaleItem = {
      id: saleItem.id,
      brandName: saleItem.brandName,
      model: saleItem.model,
      price: saleItem.price,
      ramGb: saleItem.ramGb,
      storageGb: saleItem.storageGb,
      color: saleItem.color,
    };
    saleItems.value.push(createSaleItem);
  }

  function updateSaleItemById(saleItemId, saleItem) {
    const saleItemIndex = saleItems.value.findIndex(
      (ele) => ele.id === saleItemId
    );

    const updateSaleItem = {
      id: saleItem.id,
      brandName: saleItem.brandName,
      model: saleItem.model,
      price: saleItem.price,
      ramGb: saleItem.ramGb,
      storageGb: saleItem.storageGb,
      color: saleItem.color,
    };
    if (saleItemIndex !== -1 && saleItem) {
      saleItems.value.splice(saleItemIndex, 1, updateSaleItem);
    }
  }

  return {
    saleItems,
    setSaleItems,
    getSaleItems,
    getSaleItemById,
    deleteSaleItemById,
    createSaleItem,
    updateSaleItemById,
  };
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useSaleItemStore, import.meta.hot));
}
