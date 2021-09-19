<template>
  <div class="container" style="padding-top: 1em">
    <ul class="nav nav-tabs mb-4">
      <li class="nav-item">
        <a
          :class="['nav-link', isActiveClass('masterpassword') ? 'active' : '']"
          href="masterpassword"
          >Change Master Password</a
        >
      </li>
    </ul>
    <router-view></router-view>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
// import SpinningButton from "../components/SpinningButton.vue";
export default {
  // components: { SpinningButton },
  data() {
    return {
      username: "",
      registerAt: "",
      newPassword: "",
      newPasswordConfirm: "",
      newMasterPassword: "",
      newMasterPasswordConfirm: "",
      clicked: false,
      loaded: false,
    };
  },
  computed: {
    ...mapGetters(["currentUser"]),
  },
  mounted() {
    this.username = this.currentUser.username;
    this.registerAt = this.currentUser.registerAt;
  },
  methods: {
    isActiveClass(link) {
      return "/settings/" + link === this.$route.path ? "active" : "";
    },
    async saveChanges() {
      console.log("haha");
      if (this.newMasterPassword !== this.newMasterPasswordConfirm) {
        alert("master passwords do not match");
        return;
      }

      this.clicked = true;

      let data = {
        username: this.username,
        newPassword: this.newPassword.length !== 0 ? this.newPassword : null,
        newMasterPassword:
          this.newMasterPassword.length !== 0 ? this.newMasterPassword : null,
      };

      let response = await fetch("http://localhost:9091/users", {
        method: "PUT",
        body: JSON.stringify(data),
        headers: {
          "content-type": "application/json",
        },
      });
    },
  },
};
</script>

<style scoped>
body {
  margin-top: 20px;
  color: #1a202c;
  text-align: left;
  background-color: #e2e8f0;
}
.main-body {
  padding: 15px;
}

.nav-link {
  color: #4a5568;
}
.card {
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06);
}

.card {
  position: relative;
  display: flex;
  flex-direction: column;
  min-width: 0;
  word-wrap: break-word;
  background-color: #fff;
  background-clip: border-box;
  border: 0 solid rgba(0, 0, 0, 0.125);
  border-radius: 0.25rem;
}

.card-body {
  flex: 1 1 auto;
  min-height: 1px;
  padding: 1rem;
}

.gutters-sm {
  margin-right: -8px;
  margin-left: -8px;
}

.gutters-sm > .col,
.gutters-sm > [class*="col-"] {
  padding-right: 8px;
  padding-left: 8px;
}
.mb-3,
.my-3 {
  margin-bottom: 1rem !important;
}

.bg-gray-300 {
  background-color: #e2e8f0;
}
.h-100 {
  height: 100% !important;
}
.shadow-none {
  box-shadow: none !important;
}
</style>