const ORDER_API_URL_V2 = `${
  import.meta.env.VITE_URL_API || '/ssi1'
}/itb-mshop/v2/orders`
const getAccessToken = () => {
  return localStorage.getItem("access_token");
};
const createOrder = async (orders) => {
  try {
    const response = await fetch(`${ORDER_API_URL_V2}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: getAccessToken() ? `Bearer ${getAccessToken()}` : '',
      },
      body: JSON.stringify(orders),
    })

    if (response.status !== 201) {
      throw new Error(
        `Failed to create order. Server responded with status ${response.status}`
      )
    }

    const data = await response.json()
    return data
  } catch (error) {
    return {
      error: `Error creating order: ${error.message}`,
    }
  }
}

export { createOrder }
