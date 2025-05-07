import { useBrandStore } from "@/stores/BrandStore";

const BRAND_API_URL = `${import.meta.env.VITE_BACKEND_URL}/brands`;

// GET all sale-items
const getAllBrands = async () => {
  try {
    const response = await fetch(BRAND_API_URL);

    if (!response.ok) {
      throw new Error(`(${response.status}) Failed to fetch brand list.`);
    }
    const data = await response.json();

    const brandStore = useBrandStore();

    brandStore.setBrand(data);
    
    return data;
  } catch (error) {
    return { error };
  }
};

export { getAllBrands };
