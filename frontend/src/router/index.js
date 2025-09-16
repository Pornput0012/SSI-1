import SaleItemDetailView from "@/views/SaleItemDetailView.vue";
import BrandListView from "@/views/BrandListView.vue";
import CreateBrandView from "@/views/CreateBrandView.vue";
import CreateSaleItemView from "@/views/CreateSaleItemView.vue";
import EditBrandView from "@/views/EditBrandView.vue";
import EditSaleitemView from "@/views/EditSaleitemView.vue";
import homeView from "@/views/LandingView.vue";
import SaleItemListView from "@/views/SaleItemListView.vue";
import SaleItemView from "@/views/SaleItemView.vue";
import { createRouter, createWebHistory } from "vue-router";
import RegisterView from "@/views/RegisterView.vue";
import VerifyTokenView from "@/views/VerifyTokenView.vue";
import LoginView from "@/views/LoginView.vue";
import ProfileView from "@/views/ProfileView.vue";

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
      component: SaleItemDetailView,
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
      name: "brands",
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
    {
      path: "/register",
      component: RegisterView,
      name: "register",
    },
    {
      path: "/signin",
      component: LoginView,
      name: "signin",
    },
    {
      path: "/verify-email",
      component: VerifyTokenView,
      name: "verify-email",
    },
    {
      path: "/profile",
      component: ProfileView,
      name: "profile",
    },
  ],
});

export default router;
