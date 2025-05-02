<script setup>
import { onMounted } from 'vue';
import ContainerList from './ContainerList.vue';
import SaleItemModel from './SaleItemModel.vue';



const props = defineProps({
    saleItems : {
        type : Array,
        required : true
    }
})

console.log(props.saleItems)
</script>
 
<template>
<div>
    <ContainerList v-if="saleItems && saleItems.length > 0" :sale-items="saleItems"  class="w-full h-full grid grid-cols-1 gap-4  sm:grid-cols-2 md:grid-cols-3 md:gap-6 lg:grid-cols-4 lg:gap-8 xl:grid-cols-5 xl:gap-10">
        <template v-slot="{itemInList}">
            <SaleItemModel
                :brand="itemInList.brandName"
                :model="itemInList.model"
                :price="itemInList.price"
                :ram-gb="itemInList.ramGb"
                :storage-gb="itemInList.storageGb"
                :color="itemInList.color"
                :id="itemInList.id"
            />
        </template>
    </ContainerList>
    <div v-if="!saleItems || saleItems.length === 0" class="w-full h-full p-12 flex justify-center items-center bg-purple-200 border-1 border-purple-400">

        <p class="text-purple-700 font-medium">
            no sale item
        </p>
    </div>
</div>
</template>
 
<style scoped>
</style>