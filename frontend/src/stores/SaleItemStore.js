import { ref } from "vue";
import { defineStore, acceptHMRUpdate } from "pinia";

export const useSaleItemStore = defineStore("SaleItem", () => {
    const saleItems = ref([
        {
            id: 1,
            model: "iPhone 15 Pro",
            brandName: "Apple",
            description: "สมาร์ทโฟนรุ่นท็อปจาก Apple",
            price: 42900,
            quantity: 10,
            ramGb: 8,
            storageGb: 512,
            screenSizeInch: 6.1,
            color: "Black Titanium",
            createdOn: "2024-04-01",
            updatedOn: "2024-04-01"
        },
        {
            id: 2,
            model: "Galaxy S24 Ultra",
            brandName: "Samsung",
            description: null, // ทดสอบไม่มี description
            price: 23900,
            quantity: 20,
            ramGb: 8,
            storageGb: 256,
            screenSizeInch: null, // ทดสอบ null unit
            color: "",
            createdOn: "2024-04-03",
            updatedOn: "2024-04-03"
        },
        {
            id: 3,
            model: "Xiaomi 14 Ultra",
            brandName: "Xiaomi",
            description: "",
            price: null, // ทดสอบ null price
            quantity: 0,
            ramGb: null,
            storageGb: null,
            screenSizeInch: 6.7,
            color: "White",
            createdOn: "2024-04-04",
            updatedOn: "2024-04-05"
        }
    ])

    // setter
    function setSaleItems(items) {
        saleItems.value = items
    }

    // getter
    function getSaleItems() {
        return saleItems.value
    }
    
    function getSaleItemById(id) {
        const index = saleItems.value.findIndex((item) => item.id === id)
        if (index === -1) {
            return null
        }
        return saleItems.value[index]
    }

    return {
        saleItems,
        setSaleItems,
        getSaleItems,
        getSaleItemById
    }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useSaleItemStore, import.meta.hot))
}