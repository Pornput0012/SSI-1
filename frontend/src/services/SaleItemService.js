const SALE_ITEM_API_URL = `${import.meta.env.VITE_BACKEND_URL}/sale-items`

// GET all sale-items
const getAllSaleItems = async () => {
    try {
        const response = await fetch(SALE_ITEM_API_URL)
        if (!response.ok) {
            throw new Error("Failed to fetch sale item list.");
        }
        const data = await response.json()
        return data
    } catch (error) {
        return {
            error: `Error fetching sale items: ${error.message}`,
        }
    }
}

// GET sale-item by ID
const getSaleItemById = async (id) => {
    try {
        if (!id) {
            throw new Error("Sale Item ID is required.")
        }
        const response = await fetch(`${SALE_ITEM_API_URL}/${id}`)
        if (response.status === 404) {
            throw new Error("The requested sale item does not exist")
        }
        if (!response.ok) {
            throw new Error(`Failed to fetch sale item detail. Status: ${response.status}`)
        }
        const data = await response.json()
        return data
    } catch (error) {
        return {
            error: error.message,
        }
    }
}

export { getAllSaleItems, getSaleItemById }