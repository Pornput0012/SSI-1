import SaleItemDetail from "@/components/SaleItemDetail.vue";
import CreateSaleItemView from "@/views/CreateSaleItemView.vue";
import EditSaleitemView from "@/views/EditSaleitemView.vue";
import homeView from "@/views/homeView.vue";
import SaleItemListView from "@/views/SaleItemListView.vue";
import SaleItemView from "@/views/SaleItemView.vue";
import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      component: homeView,
      name: "home",
    },
    {
      path: "/sale-items",
      component: SaleItemView,
      name: "sale-items",
    },
    {
      path: "/sale-items/:id",
      component: SaleItemDetail,
      name: "sale-item-detail",
    },
    {
      path: "/sale-items/add",
      component: CreateSaleItemView,
      name: "create-sale-item",
    },
    {
      path: "/sale-items/:id/edit",
      component: EditSaleitemView,
      name: "edit-sale-item",
    },
    {
      path: "/sale-items/list",
      component: SaleItemListView,
      name: "sale-items-list",
    },
    {
      path: "/ssi1",
      redirect: { name: "home" },
    },
  ],
});

export default router;
