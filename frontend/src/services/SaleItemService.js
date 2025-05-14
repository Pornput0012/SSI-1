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


// DELETE sale-item by ID
const deleteSaleItemById = async (id) => {
  try {
    if (!id) throw new Error(`Sale Item ID is required for deletion.`);

    const response = await fetch(`${SALE_ITEM_API_URL}/${id}`, {
      method: "DELETE",
    });

    if (response.status !== 204) {
      throw new Error(
        `Failed to delete Sale Item with ID ${id}. Server responded with status ${response.status}`
      );
    }
    return { success: true };
  } catch (error) {
    return {
      error: `Error deleting Sale Item: ${error.message}`,
    };
  }
};

const createSaleItem = async (saleItem) => {
  try {
    //เช็ค required fieldในหน้า form แล้ว
    console.log(saleItem);
    
    const response = await fetch(`${SALE_ITEM_API_URL}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(saleItem),
    });

    

    if (response.status !== 201) {
      throw new Error(
        `Failed to create sale item. Server responded with status ${response.status}`
      );
    }

    const data = await response.json();
    console.log(data);
    
    return data;

  } catch (error) {
    console.log(error)
    return {
      error: `Error creating sale item: ${error.message}`,
    };
  }
};

const updateSaleItemById = async (id, saleItem) => {
  try {
    if (!id) throw new Error(`Sale Item is required for updating.`);

    //เช็ค required fieldในหน้า form แล้ว
    const response = await fetch(`${SALE_ITEM_API_URL}/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(saleItem),
    });

    if (!response.ok) {
      throw new Error(
        `Failed to create sale item. Server responded with status ${response.status}`
      );
    }

    const data = await response.json();
    return data;

  } catch (error) {
    return {
      error: `Error creating sale item: ${error.message}`,
    };
  }
};

export { getAllSaleItems, getSaleItemById, deleteSaleItemById ,updateSaleItemById,createSaleItem};
