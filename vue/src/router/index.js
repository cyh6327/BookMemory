import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/book',
    name: 'dashboard',
    component: () => import('../views/DashBoard.vue')
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/LoginPage.vue'),
  },
  {
    path: '/about',
    name: 'about',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/AboutView.vue')
  },
  {
    path: '/book/create',
    name: 'createBook',
    component: () => import('../views/CreateBook.vue')
  },
  {
    path: '/book/detail/:bookId',
    name: 'bookDetail',
    component: () => import('../views/BookDetail.vue'),
  },
  {
    path: '/email',
    name: 'sendEmail',
    component: () => import('../views/SendEmail.vue'),
  },
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router