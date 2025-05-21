import { ref } from "vue";
import { defineStore, acceptHMRUpdate } from "pinia";

export const useSaleItemStore = defineStore("SaleItem", () => {
  const saleItems = ref(null);
  const saleItemsPage = ref(null);

  function setSaleItems(itemArray) {
    saleItems.value = itemArray;
  }

  function setSaleItemsPage(itemArray) {
    saleItemsPage.value = itemArray;
  }

  function getSaleItems() {
    return saleItems.value;
  }

  function getSaleItemsPage() {
    return saleItemsPage.value;
  }

  function getSaleItemById(id) {
    if (!Array.isArray(saleItems.value)) {
      return null;
    }
    console.log(saleItems.value);

    const index = saleItems.value.findIndex((item) => item.id == id);
    console.log(index);

    if (index === -1) {
      return null;
    }
    return saleItems.value[index];
  }

  function getSaleItemByIdPage(id) {
    if (!Array.isArray(saleItemsPage.value)) {
      return null;
    }

    const index = saleItemsPage.value.content.findIndex(
      (item) => item.id == id
    );
    console.log(index);

    if (index === -1) {
      return null;
    }
    return saleItemsPage.content.value[index];
  }

  function deleteSaleItemById(saleItemId) {
    if (!Array.isArray(saleItems.value)) {
      return;
    }

    const saleItemIndex = saleItems.value.findIndex(
      (ele) => ele.id == saleItemId
    );

    if (saleItemId && saleItemIndex !== -1) {
      saleItems.value.splice(saleItemIndex, 1);
    }
  }

  function createSaleItem(saleItem) {
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
    getSaleItemByIdPage,
    getSaleItemsPage,
    setSaleItemsPage,
  };
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useSaleItemStore, import.meta.hot));
}
