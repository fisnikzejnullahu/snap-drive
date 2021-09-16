import Vue from "vue";
import Vuex from "vuex";
import * as actions from "./actions";
import * as getters from "./getters";
import drive from "./modules/drive";
import createPersistedState from "vuex-persistedstate";

Vue.use(Vuex);

export default new Vuex.Store({
  actions,
  getters,
  modules: {
    drive,
  },
  plugins: [
    // createPersistedState({
    //   paths: ["drive.user"],
    // }),
    createPersistedState(),
  ],
});
