<script setup>
import SaleItemList from '@/components/SaleItemList.vue';
import SaleItemModel from '@/components/SaleItemModel.vue';
import { useBrandStore } from '@/stores/BrandStore';
import { useSaleItemStore } from '@/stores/SaleItemStore';


const useItem = useSaleItemStore();

const saleItems = useItem.getSaleItems();


console.log(saleItems);

</script>
 
<template>
<div class="w-full h-full p-20">
    <h1 class="font-bold text-2xl">Products</h1>
    <SaleItemList class="mt-10" :sale-items="saleItems"/>
    
</div>
</template>
 
<style scoped>

</style>