import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import "./assets/bootstrap.css";
import "bootstrap-vue/dist/bootstrap-vue.css";
import "./assets/styles.css";
import { BootstrapVue } from "bootstrap-vue";

// Make BootstrapVue available throughout your project
Vue.use(BootstrapVue);

Vue.config.productionTip = false;

new Vue({
  router,
  store,
  render: (h) => h(App),
}).$mount("#app");
