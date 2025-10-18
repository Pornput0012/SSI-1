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
import EditProfileView from "@/views/EditProfileView.vue";
import { useUserStore } from "@/stores/UserStore";
import CartView from "@/views/CartView.vue";
import OrderView from "@/views/OrderView.vue";
import OrderDetailView from "@/views/OrderDetailView.vue";
import SaleOrders from "@/views/SaleOrdersView.vue";
import SaleOrdersDetail from "@/views/SaleOrdersDetail.vue";
import ChangePassword from "@/views/ChangePasswordView.vue";

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
      meta: { requiresAuth: true, role: "SELLER" },
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
    {
      path: "/profile/edit",
      component: EditProfileView,
      name: "EditProfile",
    },
    {
      path: "/profile/password",
      component: ChangePassword,
      name: "changePassword",
    },
    {
      path: "/cart",
      component: CartView,
      name: "cart",
      meta: { requiresAuth: true },
    },
    {
      path: "/your-orders",
      component: OrderView,
      name: "order",
      meta: { requiresAuth: true },
    },
    {
      path: "/your-orders/:orderId",
      component: OrderDetailView,
      name: "order-detail",
      meta: { requiresAuth: true },
    },
    {
      path: "/sale-orders",
      component: SaleOrders,
      name: "sale-orders",
      meta: { requiresAuth: true },
    },
    {
      path: "/sale-orders/:orderId",
      component: SaleOrdersDetail,
      name: "sale-orders-detail",
      meta: { requiresAuth: true },
    },
  ],
});

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore();
  try {
    await userStore.waitForUser();
  } catch (error) {
    console.log(error);
    
  } finally {
    if (to.meta.requiresAuth) {
      const isLoggedIn = userStore.hasInfo();
      console.log(isLoggedIn);

      const userType = userStore.getUserType()?.toUpperCase();

      if (!isLoggedIn) {
        next({ name: "signin" });
      } else if (
        to.meta.role?.toUpperCase() === "SELLER" &&
        userType !== "SELLER"
      ) {
        next({ name: "sale-items" });
      }
    }

    next();
  }
});

export default router;
