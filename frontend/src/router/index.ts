import { createRouter, createWebHashHistory, RouteRecordRaw, RouterView } from 'vue-router'
import LoginPage from '../pages/LoginPage.vue'
import ChangePassword from '../pages/ChangePassword.vue'
import register from '../pages/register3.vue'
import merchant from '../pages/merchant.vue'
import room from '../pages/room/room.vue'
import roomDetail from '../pages/room/roomDetail.vue'
import roomImg from '../pages/room/roomImg.vue'
import roomOrder from '../pages/room/orders.vue'

import Front from '../pages/front/Front.vue'
import HotelPage from '../pages/front/hotel/HotelPage.vue'
import UserPage from '../pages/front/user/UserPage.vue'
import HotelIntroduction from '../pages/front/hotel/HotelIntroduction.vue'
import HotelBookRoom from '../pages/front/hotel/HotelBookRoom.vue'
import HotelFloorPlan from '../pages/front/hotel/HotelFloorPlan.vue'
import HotelComment from '../pages/front/hotel/HotelComment.vue'
import HotelChat from '../pages/front/hotel/HotelChat.vue'
import UserSetting from '../pages/front/user/UserSetting.vue'
import UserOrder from '../pages/front/user/UserOrder.vue'
import UserCollection from '../pages/front/user/UserCollection.vue'
import { ElNotification } from 'element-plus'
import { h } from 'vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'login',
    component: LoginPage,
    meta: { requiresAuth: false }
  },
  {
    path: '/merchant',
    name: 'merchant',
    component: merchant,
    meta: { requiresAuth: true }
  },
  {
    path: '/merchant/room',
    name: 'merchant_room',
    component: room,
    meta: { requiresAuth: true },
    children: [
      { path: 'detail', name: 'detail', component: roomDetail },
      { path: 'picture', name: 'img', component: roomImg },
      { path: 'order', name: 'orders', component: roomOrder }
    ]
  },
  {
    path: '/signup',
    name: 'signup',
    component: register,
    meta: { requiresAuth: false }
  },
  {
    path: '/changepassword',
    name: 'changepassword',
    component: ChangePassword,
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'front',
    component: Front,
    meta: { requiresAuth: false }
  },
  {
    path: '/hotel/:hotelId(\\d+)',
    name: 'hotel',
    component: HotelPage,
    meta: { requiresAuth: false },
    children: [
      { path: 'introduction', name: 'introduction', component: HotelIntroduction, meta: { requiresAuth: false } },
      { path: 'bookroom', name: 'bookroom', component: HotelBookRoom, meta: { requiresAuth: false } },
      { path: 'floorplan', name: 'floorplan', component: HotelFloorPlan, meta: { requiresAuth: false } },
      { path: 'comment', name: 'comment', component: HotelComment, meta: { requiresAuth: false } },
      { path: 'chat', name: 'chat', component: HotelChat, meta: { requiresAuth: true } }
    ]
  },
  {
    path: '/user',
    name: 'user',
    component: UserPage,
    meta: { requiresAuth: true },
    children: [
      { path: 'setting', name: 'setting', component: UserSetting, meta: { requiresAuth: true } },
      { path: 'collection', name: 'collection', component: UserCollection, meta: { requiresAuth: true } },
      { path: 'order', name: 'order', component: UserOrder, meta: { requiresAuth: true } }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes: routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.token ? JSON.parse(localStorage.token) : ''
  if (to.meta.requiresAuth && !token) {
    ElNotification({
      title: "Information",
      message: h("i", { style: "color: teal" }, "请先登录！"),
    })
    next('/login')
  } else {
    next()
  }
})

export default router
