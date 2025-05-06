import SaleItemDetail from "@/components/SaleItemDetail.vue";
import CreateSaleItemView from "@/views/CreateSaleItemView.vue";
import EditSaleitemView from "@/views/EditSaleitemView.vue";
import LandingView from "@/views/LandingView.vue";
import SaleItemView from "@/views/SaleItemView.vue";
import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      component: LandingView,
      name: "landing",
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
  ],
});

export default router;
