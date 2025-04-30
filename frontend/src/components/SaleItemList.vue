<script setup>
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
    <ContainerList v-if="saleItems && saleItems.length > 0" :sale-items="saleItems"  class="w-full h-full flex gap-12">
        <template v-slot="{itemInList}">
            <SaleItemModel
                :brand="itemInList.brandName"
                :model="itemInList.model"
                :price="itemInList.price"
                :ram-gb="itemInList.ramGb"
                :storage-gb="itemInList.storageGb"
                :color="itemInList.color"
            />

           
        </template>
    </ContainerList>
</div>
</template>
 
<style scoped>

</style>