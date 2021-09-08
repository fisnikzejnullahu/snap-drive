<template>
  <nav id="sidebar" class="">
    <div class="custom-menu">
      <button type="button" id="sidebarCollapse" class="btn btn-primary">
        <i class="fa fa-bars"></i>
        <span class="sr-only">Toggle Menu</span>
      </button>
    </div>
    <div class="p-4">
      <h1>
        <p class="logo">Snap Drive <span>Secured Cloud</span></p>
      </h1>
      <ul class="list-unstyled components mb-3">
        <li :class="isActiveClass('/drive')">
          <router-link to="/drive"
            ><span class="fa fa-home mr-3"></span>My Drive</router-link
          >
        </li>
        <li :class="isActiveClass('/settings')">
          <router-link to="/settings/profile"
            ><span class="fa fa-home mr-3"></span>Settings</router-link
          >
        </li>
        <li :class="isActiveClass('/shared')">
          <router-link to="/shared"
            ><span class="fa fa-home mr-3"></span>Shared with me</router-link
          >
        </li>
      </ul>
      <h1 style="color: #fff">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="32"
          height="32"
          fill="currentColor"
          class="bi bi-cloud mr-2"
          viewBox="0 0 16 16"
        >
          <path
            d="M4.406 3.342A5.53 5.53 0 0 1 8 2c2.69 0 4.923 2 5.166 4.579C14.758 6.804 16 8.137 16 9.773 16 11.569 14.502 13 12.687 13H3.781C1.708 13 0 11.366 0 9.318c0-1.763 1.266-3.223 2.942-3.593.143-.863.698-1.723 1.464-2.383zm.653.757c-.757.653-1.153 1.44-1.153 2.056v.448l-.445.049C2.064 6.805 1 7.952 1 9.318 1 10.785 2.23 12 3.781 12h8.906C13.98 12 15 10.988 15 9.773c0-1.216-1.02-2.228-2.313-2.228h-.5v-.5C12.188 4.825 10.328 3 8 3a4.53 4.53 0 0 0-2.941 1.1z"
          />
        </svg>

        Storage
      </h1>

      <p style="color: #fff">
        {{ driveSize.totalUsed }} / 15 GB (
        {{ (Math.round(driveSize.percentage * 100) / 100).toFixed(2) }}% Full )
      </p>
      <div class="progress">
        <div
          class="progress-bar bg-success"
          role="progressbar"
          :style="'width: ' + driveSize.percentage + '%'"
          :aria-valuenow="driveSize.percentage"
          aria-valuemin="0"
          aria-valuemax="100"
        ></div>
      </div>

      <div class="mt-4">
        <span>Cloud Provider:</span>
        <img src="@/assets/googlecloud.png" style="width: 100%" alt="" />
      </div>

      <button
        @click="onLogoutClick"
        type="button"
        class="btn btn-light mb-4"
        style="position: absolute; bottom: 0; right: 10%; left: 10%; width: 80%"
      >
        SIGN OUT
      </button>
    </div>
  </nav>
</template>

<script>
import { mapActions, mapGetters } from "vuex";
export default {
  name: "Sidebar",
  computed: {
    ...mapGetters(["driveSize"]),
  },
  methods: {
    ...mapActions(["logout"]),
    async onLogoutClick() {
      let response = await this.logout();
      if (response.status === 204) {
        this.$router.push("/login");
      }
    },
    isActiveClass(link) {
      if (link === "/settings" && this.$route.path.startsWith("/settings")) {
        return "active";
      }
      return link === this.$route.path ? "active" : "";
    },
  },
};
</script>

<style>
</style>
