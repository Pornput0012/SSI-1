import { useSaleItemStore } from "@/stores/SaleItemStore";

const SALE_ITEM_API_URL = `${import.meta.env.VITE_BACKEND_URL}/sale-items`;

// GET all sale-items
const getAllSaleItems = async () => {
  try {
    const response = await fetch(SALE_ITEM_API_URL);

    if (!response.ok) {
      throw new Error(`(${response.status}) Failed to fetch sale item list.`);
    }

    const data = await response.json();
    const saleItemStore = useSaleItemStore();

    saleItemStore.setSaleItems(data);
    return data;
  } catch (error) {
    return { error };
  }
};

// GET sale-item by ID
const getSaleItemById = async (id) => {
  try {
    if (!id) throw new Error("Sale Item ID is required.");
    const response = await fetch(`${SALE_ITEM_API_URL}/${id}`);

    const data = await response.json();

    if (!data.id || !response.ok) {
      throw new Error(`(${data.status || response.status}) ${data.message}`);
    }

    return data;
  } catch (error) {
    return { error };
  }
};

export { getAllSaleItems, getSaleItemById };
