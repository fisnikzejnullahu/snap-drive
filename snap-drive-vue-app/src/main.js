import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
// import "./assets/css/vendor/bootstrap.min.css";
import "./assets/css/landing.css";
import "./assets/bootstrap.css";
import "bootstrap-vue/dist/bootstrap-vue.css";
import "./assets/styles.css";
import { BootstrapVue } from "bootstrap-vue";
import GAuth from "vue-google-oauth2";

// Make BootstrapVue available throughout your project
Vue.use(BootstrapVue);
const gauthOption = {
  clientId:
    "911060518707-bp0ccucthlfhkfne74it4rko1cm5e2e0.apps.googleusercontent.com",
  scope: "profile email https://www.googleapis.com/auth/drive",
  prompt: "consent",
};
Vue.use(GAuth, gauthOption);

Vue.config.productionTip = false;

new Vue({
  router,
  store,
  render: (h) => h(App),
}).$mount("#app");
