import { createRouter, createWebHistory } from 'vue-router'

import Layout from "../layout/Layout";
import LayoutD from "../layout/LayoutD";

const routes = [
  {
    path: '/',
    name: 'Layout',
    component: Layout,
    redirect: '/login',
    children:[
      {
        path: 'check',
        name: 'Check',
        component: () => import("../views/Check"),
      },
      {
        path: '/uncheck',
        name: 'Uncheck',
        component: () => import("../views/Uncheck")
      },
      {
        path: '/station',
        name: 'Station',
        component: () => import("../views/Station")
      },
      {
        path: '/through',
        name: 'Through',
        component: () => import("../views/Through")
      },
      {
        path: '/bus',
        name: 'Bus',
        component: () => import("../views/Bus")
      },
      {
        path: '/routes',
        name: 'Routes',
        component: () => import("../views/Routes")
      },
      {
        path: '/detail',
        name: 'Detail',
        component: () => import("../views/Detail")
      },
      {
        path: '/map',
        name: 'Map',
        component: () => import("../views/Map")
      },
      {
        path: '/mapRoute',
        name: 'MapRoute',
        component: () => import("../views/MapRoute")
      },
      {
        path: '/working',
        name: 'Working',
        component: () => import("../views/Working")
      },
      {
        path: '/person',
        name: 'Person',
        component: () => import("../views/Person")
      },
    ]
  },
  {
    path: '/layoutD',
    name: 'LayoutD',
    component: LayoutD,
    children:[
      {
        path: '/myInf',
        name: 'MyInf',
        component: () => import("../views/MyInf"),
      },
      {
        path: '/myWork',
        name: 'MyWork',
        component: () => import("../views/MyWork")
      },
      {
        path: '/myRoute',
        name: 'MyRoute',
        component: () => import("../views/MyRoute")
      },
      {
        path: '/personD',
        name: 'PersonD',
        component: () => import("../views/PersonD")
      },
      {
        path: '/license',
        name: 'License',
        component: () => import("../views/License")
      },
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import("../views/Login")
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import("../views/Register")
  },
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
