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
            ><span class="mr-2"
              ><svg
                xmlns="http://www.w3.org/2000/svg"
                width="24"
                height="24"
                :fill="
                  isActiveClass('/drive') ? '#fff' : 'rgba(255, 255, 255, 0.6)'
                "
                class="bi bi-file-text"
                viewBox="0 0 16 16"
              >
                <path
                  d="M5 4a.5.5 0 0 0 0 1h6a.5.5 0 0 0 0-1H5zm-.5 2.5A.5.5 0 0 1 5 6h6a.5.5 0 0 1 0 1H5a.5.5 0 0 1-.5-.5zM5 8a.5.5 0 0 0 0 1h6a.5.5 0 0 0 0-1H5zm0 2a.5.5 0 0 0 0 1h3a.5.5 0 0 0 0-1H5z"
                />
                <path
                  d="M2 2a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V2zm10-1H4a1 1 0 0 0-1 1v12a1 1 0 0 0 1 1h8a1 1 0 0 0 1-1V2a1 1 0 0 0-1-1z"
                /></svg></span
            >My Drive</router-link
          >
        </li>
        <li :class="isActiveClass('/shared')">
          <router-link to="/shared"
            ><span class="mr-2"
              ><svg
                xmlns="http://www.w3.org/2000/svg"
                width="24"
                height="24"
                :fill="
                  isActiveClass('/shared') ? '#fff' : 'rgba(255, 255, 255, 0.6)'
                "
                class="bi bi-people"
                viewBox="0 0 16 16"
              >
                <path
                  d="M15 14s1 0 1-1-1-4-5-4-5 3-5 4 1 1 1 1h8zm-7.978-1A.261.261 0 0 1 7 12.996c.001-.264.167-1.03.76-1.72C8.312 10.629 9.282 10 11 10c1.717 0 2.687.63 3.24 1.276.593.69.758 1.457.76 1.72l-.008.002a.274.274 0 0 1-.014.002H7.022zM11 7a2 2 0 1 0 0-4 2 2 0 0 0 0 4zm3-2a3 3 0 1 1-6 0 3 3 0 0 1 6 0zM6.936 9.28a5.88 5.88 0 0 0-1.23-.247A7.35 7.35 0 0 0 5 9c-4 0-5 3-5 4 0 .667.333 1 1 1h4.216A2.238 2.238 0 0 1 5 13c0-1.01.377-2.042 1.09-2.904.243-.294.526-.569.846-.816zM4.92 10A5.493 5.493 0 0 0 4 13H1c0-.26.164-1.03.76-1.724.545-.636 1.492-1.256 3.16-1.275zM1.5 5.5a3 3 0 1 1 6 0 3 3 0 0 1-6 0zm3-2a2 2 0 1 0 0 4 2 2 0 0 0 0-4z"
                /></svg></span
            >Shared with me</router-link
          >
        </li>
        <li :class="isActiveClass('/settings')">
          <router-link to="/settings/masterpassword">
            <span class="mr-2"
              ><svg
                :fill="
                  isActiveClass('/settings')
                    ? '#fff'
                    : 'rgba(255, 255, 255, 0.6)'
                "
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 32 32"
                width="24px"
                height="24px"
              >
                <path
                  fill="none"
                  :stroke="
                    isActiveClass('/settings')
                      ? '#fff'
                      : 'rgba(255, 255, 255, 0.6)'
                  "
                  stroke-miterlimit="10"
                  stroke-width="2"
                  d="M16 12A4 4 0 1 0 16 20A4 4 0 1 0 16 12Z"
                />
                <path
                  fill="none"
                  :stroke="
                    isActiveClass('/settings')
                      ? '#fff'
                      : 'rgba(255, 255, 255, 0.6)'
                  "
                  stroke-miterlimit="10"
                  stroke-width="2"
                  d="M25,16c0-0.677-0.081-1.335-0.223-1.97l2.615-2.298l-2-3.464l-3.304,1.118c-0.966-0.89-2.122-1.578-3.407-1.979L18,4h-4l-0.681,3.406c-1.286,0.401-2.441,1.089-3.407,1.979L6.608,8.268l-2,3.464l2.615,2.298C7.081,14.665,7,15.323,7,16s0.081,1.335,0.223,1.97l-2.615,2.298l2,3.464l3.304-1.118c0.966,0.89,2.122,1.578,3.407,1.979L14,28h4l0.681-3.406c1.286-0.401,2.441-1.089,3.407-1.979l3.304,1.118l2-3.464l-2.615-2.298C24.919,17.335,25,16.677,25,16z"
                /></svg></span
            >Settings</router-link
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

      <span
        style="color: #fff; white-space: initial; text-align: left"
        class="badge badge-secondary"
      >
        Total storage in your google cloud:
      </span>
      <p>{{ driveSize.storageInGCM }} / {{ driveSize.storageLimit }}</p>

      <span
        style="color: #fff; white-space: initial; text-align: left"
        class="badge badge-secondary"
      >
        Total storage that Snap Drive took in your google cloud:
      </span>
      <p>
        {{ driveSize.storageSizeSnapDrive }} / {{ driveSize.storageLimit }} ({{
          (
            Math.round(driveSize.storageSizeSnapDrivePercentage * 100) / 100
          ).toFixed(2)
        }}%)
      </p>

      <div class="progress rounded">
        <div
          class="progress-bar bg-success rounded"
          role="progressbar"
          :style="'width: ' + driveSize.storageSizeSnapDrivePercentage + '%'"
          :aria-valuenow="driveSize.storageSizeSnapDrivePercentage"
          aria-valuemin="0"
          aria-valuemax="100"
        ></div>
      </div>

      <!-- <div class="mt-4">
        <span>Cloud Provider:</span>
        <img src="@/assets/googlecloud.png" style="width: 100%" alt="" />
      </div> -->

      <button
        @click="onLogoutClick"
        type="button"
        class="btn btn-light mb-4 rounded shadow-lg"
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
      let googleLogoutResponse = await this.$gAuth.signOut();
      console.log(googleLogoutResponse.status);
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
