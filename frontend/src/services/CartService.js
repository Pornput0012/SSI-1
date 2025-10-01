import { useCartStore } from "@/stores/CartStore";


const CART_API_URL_V1 = `${import.meta.env.VITE_URL_API || '/ssi1'}/itb-mshop/v1/carts`
// GET all cart
const getAllCart = async (userId) => {

if (userId) {
    
    try {
      const response = await fetch(CART_API_URL_V1 + "/" + userId);
  
      if (!response.ok) {
        throw new Error(`(${response.status}) Failed to fetch cart list.`);
      }
      const data = await response.json();
  
      const cartStore = useCartStore();
  
      cartStore.setCart(data);
  
      return data;
    } catch (error) {
      return { error };
    }
}
};

// GET cart by id
const getCartById = async (id) => {
  try {
    const response = await fetch(`${CART_API_URL_V1}/${id}`);
    const data = await response.json();

    if (!data.id || !response.ok) {
      throw new Error(`(${data.status || response.status})  ${data.message}`);
    }

    return data;
  } catch (error) {
    return { error };
  }
};

// create cart
const addItemToCart = async (cart) => {
  try {
    
    const response = await fetch(`${CART_API_URL_V1}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(cart),
    });

    if (response.status !== 200) {
      throw new Error(`The cart could not be added.`);
    }

    const data = await response.json();

    return data;
  } catch (error) {
    return { error };
  }
};

// update cart
const updateCart = async (id, cart) => {
  try {
    if (!id) throw new Error("Cart Id is required for updating.");
    else if (!cart.name)
      throw new Error("Cart Name is required for updating.");

    const response = await fetch(`${CART_API_URL_V1}/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(cart),
    });

    if (!response.ok) {
      throw new Error(response.status === 404 ? `The cart does not exist.` : 'An error has occured, Duplicate cart name');
    }

    const data = await response.json();

    return data;
  } catch (error) {
    return { error };
  }
};

const deleteCartById = async (id) => {
  try {
    if (!id) throw new Error(`Cart ID is required for deletion.`);

    const response = await fetch(`${CART_API_URL_V1}/${id}`, {
      method: "DELETE",
    });

    if (!response.ok) {
      throw new Error("An error has occurred, the cart does not exist.");
    }
    return { success: true };
  } catch (error) {
    return { error };
  }
};
export { getAllCart, getCartById, addItemToCart, updateCart, deleteCartById };
