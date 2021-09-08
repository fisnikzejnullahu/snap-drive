<template>
  <div class="wrapper d-flex align-items-stretch">
    <Sidebar v-if="!currentPageIsLogin" />
    <div
      id="content"
      :class="!currentPageIsLogin ? 'p-4 p-md-5 pt-5' : ''"
      :style="currentPageIsLogin ? 'width:100% !important; margin-left: 0' : ''"
    >
      <router-view />
    </div>
  </div>
</template>

<script>
import Sidebar from "./components/Sidebar.vue";

export default {
  components: { Sidebar },
  data() {
    return {
      currentPageIsLogin:
        this.$router.currentRoute.path === "/login" ||
        this.$router.currentRoute.path === "/unlock" ||
        this.$router.currentRoute.path === "/signup",
    };
  },
  watch: {
    $route(to, from) {
      console.log(to);
      if (
        to.path === "/login" ||
        to.path === "/unlock" ||
        to.path === "/signup"
      ) {
        this.currentPageIsLogin = true;
      } else {
        this.currentPageIsLogin = false;
      }
    },
  },
};
</script>

<style>
@import url(https://fonts.googleapis.com/css?family=Poppins:300,400,500,600,700);
@import url(https://fonts.googleapis.com/css?family=Josefin+Sans:400,700);
@import url(https://fonts.googleapis.com/css?family=Great+Vibes);
.slide-enter-active {
  transition: opacity 0.7s, transform 0.7s;
}

.slide-enter,
.slide-leave-to {
  opacity: 0;
  transform: translateX(30%);
}
</style>
