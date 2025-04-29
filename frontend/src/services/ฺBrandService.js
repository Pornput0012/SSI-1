const BRAND_API_URL = `${import.meta.env.VITE_BACKEND_URL}/brands`

// GET all brands
const getAllBrands = async () => {
    try {
        const response = await fetch(BRAND_API_URL)
        if (!response.ok) {
            throw new Error("Failed to fetch brand list.")
        }
        const data = await response.json()
        return data
    } catch (error) {
        return {
            error: `Error fetching brands: ${error.message}`,
        }
    }
}

// GET brand by ID
const getBrandById = async (id) => {
    try {
        if (!id) {
            throw new Error("Brand ID is required.");
        }
        const response = await fetch(`${BRAND_API_URL}/${id}`)
        if (response.status === 404) {
            throw new Error("Brand not found.");
        }
        if (!response.ok) {
            throw new Error(`Failed to fetch brand. Status: ${response.status}`);
        }
        const data = await response.json()
        return data
    } catch (error) {
        return {
            error: error.message,
        }
    }
}

export { getAllBrands, getBrandById }