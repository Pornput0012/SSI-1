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
  ],
  linkActiveClass: 'border-b-1 '
});

export default router;
