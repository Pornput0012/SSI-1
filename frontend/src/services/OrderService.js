import { useOrderStore } from "@/stores/OrderStore";
import { fetchWithTokenRetry } from "@/utils/fetchWithAuth";

const ORDER_API_URL_V2 = `${import.meta.env.VITE_URL_API || "/ssi1"}/itb-mshop/v2/orders`;

const getOrdersBuyer = async (buyerId, pageInfo = { page: 0, size: 10 }) => {
  try {
    if (!buyerId) {
      throw new Error("Buyer ID is required");
    }
    const getRequestParameter = () => {
      const requestParameter = [];
      for (const key in pageInfo) {
        if (Object.prototype.hasOwnProperty.call(pageInfo, key)) {
          const element = pageInfo[key];
          if (element || element === 0) {
            requestParameter.push(
              `${key}=${Array.isArray(element) ? element.join(",") : element}`
            );
          }
        }
      }
      return requestParameter.join("&");
    };
    const response = await fetchWithTokenRetry(
      `${
        import.meta.env.VITE_URL_API || "/ssi1"
      }/itb-mshop/v2/users/${buyerId}/orders?${getRequestParameter()}`,
      {
        method: "GET",
        headers: {},
      }
    );

    if (!response.ok) {
      const error = new Error(`(${response.status}) Failed to fetch user's orders.`);
      error.status = response.status;
      throw error;
    }

    const data = await response.json();
    const orderStore = useOrderStore();

    orderStore.setOrdersPage({ ...data });
    return data;
  } catch (error) {
    return { error: { message: error.message, status: error.status } };
  }
};

const getOrderById = async (orderId) => {
  try {
    if (!orderId) throw new Error("Order ID is required.");
    const response = await fetchWithTokenRetry(`${ORDER_API_URL_V2}/${orderId}`, {
      method: "GET",
      headers: {},
    });

    if (!response.ok) {
      const data = await response.json();
      const error = new Error(`(${data.status || response.status}) ${data.message}`);
      error.status = response.status;
      throw error;
    }

    const data = await response.json();

    if (!data.id) {
      throw new Error("Invalid order data received");
    }

    return data;
  } catch (error) {
    return { error: { message: error.message, status: error.status } };
  }
};

const sellerViewOrder = async (sellerId, id) => {
  try {
    if (!sellerId) throw new Error("Seller ID is required");
    
    const res = await fetchWithTokenRetry(
      `${
        import.meta.env.VITE_URL_API || "/ssi1"
      }/itb-mshop/v2/sellers/${sellerId}/orders/${id}`,
      {
        method: "PATCH",
        headers: {},
      }
    );

    if (!res.ok) {
      const error = new Error(`(${res.status}) Failed to update status view order.`);
      error.status = res.status;
      throw error;
    }

    const data = await res.json();
    return data;
  } catch (error) {
    return { error: { message: error.message, status: error.status } };
  }
};

const getOrdersSeller = async (sellerId, pageInfo = { page: 0, size: 10 }) => {
  try {
    if (!sellerId) throw new Error("Seller ID is required");
    const getRequestParameter = () => {
      const requestParameter = [];
      for (const key in pageInfo) {
        if (Object.prototype.hasOwnProperty.call(pageInfo, key)) {
          const element = pageInfo[key];
          if (element || element === 0) {
            requestParameter.push(
              `${key}=${Array.isArray(element) ? element.join(",") : element}`
            );
          }
        }
      }
      return requestParameter.join("&");
    };
    const response = await fetchWithTokenRetry(
      `${
        import.meta.env.VITE_URL_API || "/ssi1"
      }/itb-mshop/v2/sellers/${sellerId}/orders?${getRequestParameter()}`,
      {
        method: "GET",
        headers: {},
      }
    );

    if (!response.ok) {
      const error = new Error(`(${response.status}) Failed to fetch seller's orders.`);
      error.status = response.status;
      throw error;
    }

    const data = await response.json();
    const orderStore = useOrderStore();

    orderStore.setOrdersPage({ ...data, isSeller: true });
    return data;
  } catch (error) {
    return { error: { message: error.message, status: error.status } };
  }
};

const createOrder = async (orders) => {
  try {
    console.log(orders);

    const response = await fetchWithTokenRetry(`${ORDER_API_URL_V2}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(orders),
    });

    if (response.status !== 201) {
      throw new Error(
        `Failed to create order. Server responded with status ${response.status}`
      );
    }

    const data = await response.json();
    return data;
  } catch (error) {
    return {
      error: `Error creating order: ${error.message}`,
    };
  }
};

export {
  createOrder,
  getOrdersBuyer,
  getOrderById,
  getOrdersSeller,
  sellerViewOrder,
};
