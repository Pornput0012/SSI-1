import { useBrandStore } from "@/stores/BrandStore";
const BRAND_API_URL_V1 = "/ssi1/itb-mshop/v1/brands"
// GET all brand
const getAllBrands = async () => {
  try {
    const response = await fetch(BRAND_API_URL_V1);

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

// GET brand by id
const getBrandById = async (id) => {
  try {
    const response = await fetch(`${BRAND_API_URL_V1}/${id}`);
    const data = await response.json();

    if (!data.id || !response.ok) {
      throw new Error(`(${data.status || response.status})  ${data.message}`);
    }

    return data;
  } catch (error) {
    return { error };
  }
};

// create brand
const createBrand = async (brand) => {
  try {
    if (!brand.name) throw new Error("Brand name is required.");
    const response = await fetch(`${BRAND_API_URL_V1}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(brand),
    });

    if (response.status !== 201) {
      throw new Error(`The status could not be added.`);
    }

    const data = await response.json();

    return data;
  } catch (error) {
    return { error };
  }
};

// update brand
const updateBrand = async (id, brand) => {
  try {
    if (!id) throw new Error("Brand Id is required for updating.");
    else if (!brand.name)
      throw new Error("Brand Name is required for updating.");

    const response = await fetch(`${BRAND_API_URL_V1}/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(brand),
    });

    if (!response.ok) {
      throw new Error(`The brand does not exist.`);
    }

    const data = await response.json();

    return data;
  } catch (error) {
    return { error };
  }
};

const deleteBrandById = async (id) => {
  try {
    if (!id) throw new Error(`Brand ID is required for deletion.`);

    const response = await fetch(`${BRAND_API_URL_V1}/${id}`, {
      method: "DELETE",
    });

    if (!response.ok) {
      throw new Error("An error has occurred, the brand does not exist.");
    }
    return { success: true };
  } catch (error) {
    return { error };
  }
};
export { getAllBrands, getBrandById, createBrand, updateBrand, deleteBrandById };
