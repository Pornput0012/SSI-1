import SaleItemDetail from "@/components/SaleItemDetail.vue";
import BrandListView from "@/views/BrandListView.vue";
import CreateBrandView from "@/views/CreateBrandView.vue";
import CreateSaleItemView from "@/views/CreateSaleItemView.vue";
import EditBrandView from "@/views/EditBrandView.vue";
import EditSaleitemView from "@/views/EditSaleitemView.vue";
import homeView from "@/views/LandingView.vue";
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
    {
      path: "/brands",
      component: BrandListView,
      name: "brands"
    },
    {
      path: "/brands/add",
      component: CreateBrandView,
      name: "create-brand",
    },

    {
      path: "/brands/:id/edit",
      component: EditBrandView,
      name: "edit-brand",
    },
  ],
});

export default router;
