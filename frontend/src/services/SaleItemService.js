import { useFilterStore } from '@/stores/FilterStore'
import { useSaleItemStore } from '@/stores/SaleItemStore'

const SALE_ITEM_API_URL_V1 = `${
  import.meta.env.VITE_URL_API || '/ssi1'
}/itb-mshop/v1/sale-items`
const SALE_ITEM_API_URL_V2 = `${
  import.meta.env.VITE_URL_API || '/ssi1'
}/itb-mshop/v2/sale-items`

// GET all sale-items
const getAllSaleItems = async () => {
  try {
    const response = await fetch(SALE_ITEM_API_URL_V1)

    if (!response.ok) {
      throw new Error(`(${response.status}) Failed to fetch sale item list.`)
    }

    const data = await response.json()
    const saleItemStore = useSaleItemStore()

    saleItemStore.setSaleItems(data)
    return data
  } catch (error) {
    return { error }
  }
}

// Get all sale-item v2 (pagination)
const getAllSaleItems_v2 = async (
  pageInfo = {
    page: 0,
  }
) => {
  try {
    const getRequestParameter = () => {
      const requestParameter = []
      for (const key in pageInfo) {
        if (Object.prototype.hasOwnProperty.call(pageInfo, key)) {
          const element = pageInfo[key]
          // เงื่อนไขพิเศษสำหรับ filterStorages
          if (
            key === 'filterStorages' &&
            (!element || (Array.isArray(element) && element.length === 0))
          ) {
            continue // ข้ามถ้าไม่ได้เลือก storage
          }
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
      `${SALE_ITEM_API_URL_V2}?${getRequestParameter()}`
    )

    if (!response.ok) {
      throw new Error(`(${response.status}) Failed to fetch sale item list.`)
    }

    const data = await response.json()
    const saleItemStore = useSaleItemStore()

    saleItemStore.setSaleItemsPage(data)
    return data
  } catch (error) {
    return { error }
  }
}

// GET sale-item by ID
const getSaleItemById = async (id) => {
  try {
    if (!id) throw new Error('Sale Item ID is required.')
    const response = await fetch(`${SALE_ITEM_API_URL_V1}/${id}`)

    const data = await response.json()

    if (!data.id || !response.ok) {
      throw new Error(`(${data.status || response.status}) ${data.message}`)
    }

    return data
  } catch (error) {
    return { error }
  }
}

const getSaleItemById_v2 = async (id) => {
  try {
    if (!id) throw new Error('Sale Item ID is required.')
    const response = await fetch(`${SALE_ITEM_API_URL_V2}/${id}`)

    const data = await response.json()

    if (!data.id || !response.ok) {
      throw new Error(`(${data.status || response.status}) ${data.message}`)
    }

    return data
  } catch (error) {
    return { error }
  }
}

const getImageByFileName = async (fileName) => {
  try {
    if (!fileName) throw new Error('fileName is required.')
    const response = await fetch(
      `${
        import.meta.env.VITE_URL_API || '/ssi1'
      }/itb-mshop/sale-items/${fileName}`,
      {
        mode: 'cors',
      }
    )

    if (!response.ok) {
      throw new Error(
        `HTTP Error: ${response.status} - Failed to fetch image ${fileName}`
      )
    }

    const data = await response.blob()
    const url = URL.createObjectURL(data)

    return url
  } catch (error) {
    console.error(`Error fetching image ${fileName}:`, error)
    return { error: error.message }
  }
}

// DELETE sale-item by ID
const deleteSaleItemById = async (id) => {
  try {
    const { resetCurrentPage } = useFilterStore()

    if (!id) throw new Error(`Sale Item ID is required for deletion.`)

    const response = await fetch(`${SALE_ITEM_API_URL_V1}/${id}`, {
      method: 'DELETE',
    })

    if (response.status !== 204) {
      throw new Error(
        `Failed to delete Sale Item with ID ${id}. Server responded with status ${response.status}`
      )
    }
    resetCurrentPage()
    return { success: true }
  } catch (error) {
    return {
      error: `Error deleting Sale Item: ${error.message}`,
    }
  }
}

const createSaleItem = async (saleItem) => {
  try {
    const { resetCurrentPage } = useFilterStore()

    const response = await fetch(`${SALE_ITEM_API_URL_V1}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(saleItem),
    })

    if (response.status !== 201) {
      throw new Error(
        `Failed to create sale item. Server responded with status ${response.status}`
      )
    }

    const data = await response.json()
    resetCurrentPage()

    return data
  } catch (error) {
    return {
      error: `Error creating sale item: ${error.message}`,
    }
  }
}

const createSaleItem_v2 = async (saleItem, images) => {
  try {
    const formData = new FormData()

    for (const [key, value] of Object.entries(saleItem)) {
      if (value !== null && value !== undefined) {
        const isBrandKey = key === 'brand'
        formData.append(
          isBrandKey ? 'brandId' : key,
          isBrandKey ? value.id : value
        )
      }
    }
    console.log(images)

    if (images) {
      console.log(images)

      images.newImages.forEach((img, index) => {
        console.log(img)

        const splitFileName = img.fileName.split('.')

        const ext = splitFileName.pop()

        const joinFileName = splitFileName.join('.')

        const fileName = `${joinFileName}.${index + 1}.${ext}`

        formData.append('images', img.file, fileName)
      })
    }

    const response = await fetch(`${SALE_ITEM_API_URL_V2}`, {
      method: 'POST',
      body: formData,
    })

    if (!response.ok) {
      throw new Error(
        `Failed to create sale item. Server responded with status ${response.status}`
      )
    }

    return await response.json()
  } catch (error) {
    return {
      error: `Error creating sale item: ${error.message}`,
    }
  }
}

const updateSaleItemById = async (id, saleItem) => {
  try {
    const { resetCurrentPage } = useFilterStore()

    if (!id) throw new Error(`Sale Item ID is required for updating.`)

    //เช็ค required fieldในหน้า form แล้ว
    const response = await fetch(`${SALE_ITEM_API_URL_V1}/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(saleItem),
    })

    if (!response.ok) {
      throw new Error(
        `Failed to create sale item. Server responded with status ${response.status}`
      )
    }

    const data = await response.json()
    resetCurrentPage()

    return data
  } catch (error) {
    return {
      error: `Error creating sale item: ${error.message}`,
    }
  }
}

const updateSaleItemById_v2 = async (id, saleItem, images) => {
  try {
    const formData = new FormData()

    for (const [key, value] of Object.entries(saleItem)) {
      if (value !== null && value !== undefined) {
        const isBrandKey = key === 'brand'
        formData.append(
          isBrandKey ? 'brandId' : key,
          isBrandKey ? value.id : value
        )
      }
    }

    console.log(images)

    if (images) {
      images.newImages.forEach((img) => {
        const reordered = images.reorderedImages.find(
          (r) => r.fileName === img.fileName
        )
        const order = reordered ? reordered.newOrder : 1
        const splitFileName = img.fileName.split('.')
        const ext = splitFileName.pop()
        const joinFileName = splitFileName.join('.')
        const fileName = `${joinFileName}.${order}.${ext}`
        formData.append('newImages', img.file, fileName)
      })

      formData.append('removeFileNames', images.deletedImageFileNames.join(','))

      let orderImage = ''
      images.reorderedImages.forEach((ele, i) => {
        orderImage += `${i != 0 ? '|' : ''}${ele.fileName},${ele.newOrder}`
      })
      console.log(orderImage)

      formData.append('orderImages', orderImage)
    }

    const response = await fetch(`${SALE_ITEM_API_URL_V2}/${id}`, {
      method: 'PUT',
      body: formData,
    })

    if (!response.ok) {
      throw new Error(
        `Failed to create sale item. Server responded with status ${response.status}`
      )
    }

    return await response.json()
  } catch (error) {
    return {
      error: `Error creating sale item: ${error.message}`,
    }
  }
}

const updateSaleItemImageById = async (id, images) => {
  try {
    const formData = new FormData()

    if (images) {
      images.newImages.forEach((img) => {
        const reordered = images.reorderedImages.find(
          (r) => r.fileName === img.fileName
        )
        const order = reordered ? reordered.newOrder : 1
        const splitFileName = img.fileName.split('.')
        const ext = splitFileName.pop()
        const joinFileName = splitFileName.join('.')
        const fileName = `${joinFileName}.${order}.${ext}`
        formData.append('newImages', img.file, fileName)
      })

      formData.append('removeFileNames', images.deletedImageFileNames.join(','))

      let orderImage = ''
      images.reorderedImages.forEach((ele, i) => {
        orderImage += `${i != 0 ? '|' : ''}${ele.fileName},${ele.newOrder}`
      })
      formData.append('orderImages', orderImage)
    }

    const response = await fetch(`${SALE_ITEM_API_URL_V2}/${id}/images`, {
      method: 'PUT',
      body: formData,
    })

    if (!response.ok) {
      throw new Error(
        `Failed to create sale item. Server responded with status ${response.status}`
      )
    }

    return await response.json()
  } catch (error) {
    return {
      error: `Error creating sale item: ${error.message}`,
    }
  }
}

const getSaleItemsBySeller = async (
  sellerId,
  pageInfo = { page: 0, size: 10 }
) => {
  try {
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
      `${SALE_ITEM_API_URL_V2}/sellers/${sellerId}/sale-items?${getRequestParameter()}`,
      {
        credentials: 'include',
      }
    )

    if (!response.ok) {
      throw new Error(
        `(${response.status}) Failed to fetch seller's sale items.`
      )
    }

    const data = await response.json()
    const saleItemStore = useSaleItemStore()

    saleItemStore.setSaleItemsPage(data)
    return data
  } catch (error) {
    return { error }
  }
}

export {
  getAllSaleItems,
  getSaleItemById,
  deleteSaleItemById,
  updateSaleItemById,
  createSaleItem,
  getAllSaleItems_v2,
  createSaleItem_v2,
  getSaleItemById_v2,
  getImageByFileName,
  updateSaleItemById_v2,
  updateSaleItemImageById,
  getSaleItemsBySeller,
}
