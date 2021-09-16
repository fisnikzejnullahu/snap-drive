import Vue from "vue";
import VueRouter from "vue-router";
import Home from "../views/Home.vue";
import Settings from "../views/Settings.vue";
import Drive from "../views/Drive.vue";
import SharedDrive from "../views/SharedDrive.vue";
import DriveUnlock from "../views/DriveUnlock.vue";
import UserProfile from "../components/UserProfile.vue";
import UserPassword from "../components/UserPassword.vue";
import UserMasterPassword from "../components/UserMasterPassword.vue";

import store from "../store";

Vue.use(VueRouter);

const routes = [
  {
    path: "/",
    name: "Home",
    component: Home,
  },
  {
    path: "/drive",
    name: "Drive",
    component: Drive,
  },
  {
    path: "/shared",
    name: "SharedDrive",
    component: SharedDrive,
  },
  {
    path: "/settings",
    name: "Settings",
    component: Settings,
    children: [
      {
        path: "profile",
        component: UserProfile,
      },
      {
        path: "password",
        component: UserPassword,
      },
      {
        path: "masterpassword",
        component: UserMasterPassword,
      },
    ],
  },
  {
    path: "/unlock",
    name: "DriveUnlock",
    component: DriveUnlock,
    props: true,
  },
];

const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes,
});

router.beforeEach((to, from, next) => {
  // redirect to login page if not logged in and trying to access a restricted page
  const allowedPages = ["/", "/unlock"];

  const loggedIn = store.getters.currentUser != null;
  const unlocked = loggedIn && store.getters.driveSize != null;
  console.log(loggedIn);
  console.log(unlocked);

  console.log(to.path);
  if (!loggedIn && to.path !== "/") {
    return next("/");
  }

  console.log(to.path);
  if (loggedIn && !unlocked && !allowedPages.includes(to.path)) {
    return next("/unlock");
  }

  next();
});

export default router;
