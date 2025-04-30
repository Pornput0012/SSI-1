import { ref } from "vue";
import { defineStore, acceptHMRUpdate } from "pinia";

export const useBrandStore = defineStore("Brand", () => {
    const brands = ref([
        {
            id: 1,
            brandName: "Apple",
            countryOfOrigin: "United States",
            websiteUrl: "https://www.apple.com/",
            isActive: true,
            createdOn: "2024-04-01",
            updatedOn: "2024-04-01" 
        },
        {
            id: 2,
            brandName: "Samsung",
            countryOfOrigin: "South Korea",
            websiteUrl: "https://www.samsung.com/th/",
            isActive: true,
            createdOn: "2024-04-03",
            updatedOn: "2024-04-03" 
        },
        {
            id: 3,
            brandName: "Xiaomi",
            countryOfOrigin: "China",
            websiteUrl: "https://www.mi.com/global/",
            isActive: true,
            createdOn: "2024-04-04",
            updatedOn: "2024-04-05" 
        }
    ])

    // setter
    function setBrands(brandArray) {
        brands.value = brandArray
    }

    // getter
    function getBrands() {
        return brands.value
    }

    function getBrandById(id) {
        const index = brands.value.findIndex((brand) => brand.id === id)
        if (index === -1) {
            return null
        }
        return brands.value[index]
    }

    return {
        brands,
        setBrands,
        getBrands,
        getBrandById
    }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useBrandStore, import.meta.hot))
}