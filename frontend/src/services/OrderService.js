import { useOrderStore } from '@/stores/OrderStore'

const ORDER_API_URL_V2 = `${
  import.meta.env.VITE_URL_API || '/ssi1'
}/itb-mshop/v2/orders`
const getAccessToken = () => {
  return localStorage.getItem('access_token')
}

const getOrdersBuyer = async (buyerId, pageInfo = { page: 0, size: 10 }) => {
  try {
    if (!buyerId) {
      throw new Error('Buyer ID is required')
    }
    const getRequestParameter = () => {
      const requestParameter = []
      for (const key in pageInfo) {
        if (Object.prototype.hasOwnProperty.call(pageInfo, key)) {
          const element = pageInfo[key]
          if (element || element === 0) {
            requestParameter.push(
              `${key}=${Array.isArray(element) ? element.join(',') : element}`
            )
          }
        }
      }
      return requestParameter.join('&')
    }
    const response = await fetch(
      `${
        import.meta.env.VITE_URL_API || '/ssi1'
      }/itb-mshop/v2/users/${buyerId}/orders?${getRequestParameter()}`,
      {
        headers: {
          Authorization: getAccessToken() ? `Bearer ${getAccessToken()}` : '',
        },
      }
    )

    if (!response.ok) {
      throw new Error(`(${response.status}) Failed to fetch user's orders.`)
    }

    const data = await response.json()
    const orderStore = useOrderStore()

    orderStore.setOrdersPage({ ...data })
    return data
  } catch (error) {
    return { error }
  }
}

const getOrderById = async (orderId) => {
  try {
    if (!orderId) throw new Error('Order ID is required.')
    const response = await fetch(`${ORDER_API_URL_V2}/${orderId}`, {
      headers: {
        Authorization: getAccessToken() ? `Bearer ${getAccessToken()}` : '',
      },
    })

    const data = await response.json()

    if (!data.id || !response.ok) {
      throw new Error(`(${data.status || response.status}) ${data.message}`)
    }

    return data
  } catch (error) {
    return { error }
  }
}

const getOrdersSeller = async (sellerId, pageInfo = { page: 0, size: 10 }) => {
  try {
    if (!sellerId) throw new Error('Seller ID is required')
    const getRequestParameter = () => {
      const requestParameter = []
      for (const key in pageInfo) {
        if (Object.prototype.hasOwnProperty.call(pageInfo, key)) {
          const element = pageInfo[key]
          if (element || element === 0) {
            requestParameter.push(
              `${key}=${Array.isArray(element) ? element.join(',') : element}`
            )
          }
        }
      }
      return requestParameter.join('&')
    }
    const response = await fetch(
      `${
        import.meta.env.VITE_URL_API || '/ssi1'
      }/itb-mshop/v2/sellers/${sellerId}/orders?${getRequestParameter()}`,
      {
        headers: {
          Authorization: getAccessToken() ? `Bearer ${getAccessToken()}` : '',
        },
      }
    )

    if (!response.ok) {
      throw new Error(
        `(${response.status}) Failed to fetch seller's orders.`
      )
    }

    const data = await response.json()
    const orderStore = useOrderStore()

    orderStore.setOrdersPage({ ...data, isSeller: true })
    return data
  } catch (error) {
    return { error }
  }
}

const createOrder = async (orders) => {
  try {
    console.log(orders);
    
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

export { createOrder, getOrdersBuyer, getOrderById, getOrdersSeller }
