<template>
  <div class="login-dark">
    <form method="post" @submit.prevent="onSubmit">
      <div class="illustration text-primary">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="128"
          height="128"
          fill="currentColor"
          class="bi bi-person-circle"
          viewBox="0 0 16 16"
        >
          <path d="M11 6a3 3 0 1 1-6 0 3 3 0 0 1 6 0z" />
          <path
            fill-rule="evenodd"
            d="M0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8zm8-7a7 7 0 0 0-5.468 11.37C3.242 11.226 4.805 10 8 10s4.757 1.225 5.468 2.37A7 7 0 0 0 8 1z"
          />
        </svg>
        <h4 class="mt-3">Log In</h4>
        <p class="forgot">
          New User?
          <span class="font-weight-bold"
            ><router-link to="/signup"
              >Create Snap Drive account</router-link
            ></span
          >
        </p>
      </div>
      <div class="form-group">
        <input
          class="form-control"
          type="text"
          v-model.trim="username"
          placeholder="Username"
        />
      </div>
      <div class="form-group">
        <input
          class="form-control"
          type="password"
          v-model.trim="password"
          placeholder="Password"
        />
        <p style="color: #ff0000" v-if="errorText.length !== 0">
          {{ errorText }}
        </p>
      </div>
      <div class="form-group">
        <SpinningButton
          :text="'Submit'"
          :textColor="'#fff'"
          :classNames="'btn btn-primary btn-block'"
          :clicked="clicked"
        />
      </div>
    </form>
  </div>
</template>

<script>
import { mapActions, mapGetters } from "vuex";
import SpinningButton from "../components/SpinningButton.vue";

export default {
  components: { SpinningButton },
  data() {
    return {
      username: "",
      password: "",
      clicked: false,
      errorText: "",
    };
  },
  computed: {
    ...mapGetters(["currentUser"]),
  },
  methods: {
    ...mapActions(["login", "logout"]),
    async onSubmit(e) {
      if (this.username.length === 0 || this.password.length === 0) {
        this.errorText = "Username and password cannot be empty";
        return;
      }

      this.clicked = true;
      let userInfo = JSON.stringify({
        username: this.username,
        password: this.password,
      });
      console.log(userInfo);
      let response = await this.login(userInfo);
      this.clicked = false;

      if (response.status === 200) {
        this.$router.push("unlock");
      } else {
        let body = await response.json();
        this.errorText = body.message;
      }
    },
  },
};
</script>

<style scoped>
.login-dark {
  height: 100%;
  background: #fafbfe;
  background-size: cover;
  position: relative;
}

.login-dark form {
  height: 500px;
  width: 500px;
  background-color: #fff;
  padding: 30px 60px;
  border-radius: 10px;
  transform: translate(-50%, -50%);
  position: absolute;
  top: 50%;
  left: 50%;
  border: 1px solid #dadce0;
}

.login-dark .illustration {
  text-align: center;
  padding: 15px 0 20px;
  font-size: 100px;
}

.login-dark form .form-control {
  background: none;
  border: none;
  border-bottom: 1px solid #dadce0;
  border-radius: 0;
  box-shadow: none;
  outline: none;
  color: inherit;
}

.login-dark form .forgot {
  display: block;
  text-align: center;
  font-size: 12px;
  color: #6f7a85;
  opacity: 0.9;
  text-decoration: none;
}

::-webkit-input-placeholder {
  color: #999 !important;
}
:-moz-placeholder {
  color: #999 !important;
}
::-moz-placeholder {
  color: #999 !important;
}
:-ms-input-placeholder {
  color: #999 !important;
}
</style>