import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../views/ProfileView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/RegisterView.vue'),
    },
    {
      path: '/register-find',
      name: 'registerFind',
      component: () => import('../views/RegisterFindView.vue'),
    },
    {
      path: '/my-finds',
      name: 'myFinds',
      component: () => import('../views/MyFindsView.vue'),
    },
    {
      path: '/finds/:id',
      name: 'findDetail',
      component: () => import('../views/FindDetailView.vue'),
    },
    {
      path: '/validate-finds',
      name: 'validateFinds',
      component: () => import('../views/ValidateFindsView.vue'),
    },
    {
      path: '/authority',
      name: 'authority',
      component: () => import('../views/AuthorityDashboardView.vue'),
    }
  ],
})

export default router
