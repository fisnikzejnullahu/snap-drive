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
        <h4 class="mt-3">Create SnapDrive Account</h4>
        <p class="forgot">
          Already have an account
          <span class="font-weight-bold"
            ><router-link to="/login">Log In</router-link></span
          >
        </p>
      </div>
      <div class="form-group">
        <div class="row">
          <div class="col-10">
            <input
              class="form-control"
              type="text"
              v-model.trim="username"
              placeholder="Username"
            />
          </div>
          <div class="col-2">
            <button
              disabled
              style="cursor: pointer"
              type="button"
              class="btn bg-transparent"
              data-toggle="tooltip"
              data-placement="right"
              title="Your username that you will use to login!"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="22"
                height="22"
                fill="#9954bb"
                class="bi bi-question-circle-fill"
                viewBox="0 0 16 16"
              >
                <path
                  d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0zM5.496 6.033h.825c.138 0 .248-.113.266-.25.09-.656.54-1.134 1.342-1.134.686 0 1.314.343 1.314 1.168 0 .635-.374.927-.965 1.371-.673.489-1.206 1.06-1.168 1.987l.003.217a.25.25 0 0 0 .25.246h.811a.25.25 0 0 0 .25-.25v-.105c0-.718.273-.927 1.01-1.486.609-.463 1.244-.977 1.244-2.056 0-1.511-1.276-2.241-2.673-2.241-1.267 0-2.655.59-2.75 2.286a.237.237 0 0 0 .241.247zm2.325 6.443c.61 0 1.029-.394 1.029-.927 0-.552-.42-.94-1.029-.94-.584 0-1.009.388-1.009.94 0 .533.425.927 1.01.927z"
                />
              </svg>
            </button>
          </div>
        </div>
      </div>
      <div class="form-group">
        <div class="row">
          <div class="col-10">
            <input
              class="form-control"
              type="password"
              v-model.trim="password"
              placeholder="Password"
            />
          </div>
          <div class="col-2">
            <button
              type="button"
              disabled
              style="cursor: pointer"
              class="btn bg-transparent"
              data-toggle="tooltip"
              data-placement="right"
              title="Your account password that you will use to login!"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="22"
                height="22"
                fill="#9954bb"
                class="bi bi-question-circle-fill"
                viewBox="0 0 16 16"
              >
                <path
                  d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0zM5.496 6.033h.825c.138 0 .248-.113.266-.25.09-.656.54-1.134 1.342-1.134.686 0 1.314.343 1.314 1.168 0 .635-.374.927-.965 1.371-.673.489-1.206 1.06-1.168 1.987l.003.217a.25.25 0 0 0 .25.246h.811a.25.25 0 0 0 .25-.25v-.105c0-.718.273-.927 1.01-1.486.609-.463 1.244-.977 1.244-2.056 0-1.511-1.276-2.241-2.673-2.241-1.267 0-2.655.59-2.75 2.286a.237.237 0 0 0 .241.247zm2.325 6.443c.61 0 1.029-.394 1.029-.927 0-.552-.42-.94-1.029-.94-.584 0-1.009.388-1.009.94 0 .533.425.927 1.01.927z"
                />
              </svg>
            </button>
          </div>
        </div>
      </div>
      <div class="form-group">
        <div class="row">
          <div class="col-10">
            <input
              class="form-control"
              type="password"
              v-model.trim="masterPassword"
              placeholder="Master Password"
            />
          </div>
          <div class="col-2">
            <button
              type="button"
              disabled
              style="cursor: pointer"
              class="btn bg-transparent"
              data-toggle="tooltip"
              data-placement="right"
              title="Master password is the key that you use to lock and unlock your files! Please memorize it
              because your files can get locked forever without correct key!"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="22"
                height="22"
                fill="#9954bb"
                class="bi bi-question-circle-fill"
                viewBox="0 0 16 16"
              >
                <path
                  d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0zM5.496 6.033h.825c.138 0 .248-.113.266-.25.09-.656.54-1.134 1.342-1.134.686 0 1.314.343 1.314 1.168 0 .635-.374.927-.965 1.371-.673.489-1.206 1.06-1.168 1.987l.003.217a.25.25 0 0 0 .25.246h.811a.25.25 0 0 0 .25-.25v-.105c0-.718.273-.927 1.01-1.486.609-.463 1.244-.977 1.244-2.056 0-1.511-1.276-2.241-2.673-2.241-1.267 0-2.655.59-2.75 2.286a.237.237 0 0 0 .241.247zm2.325 6.443c.61 0 1.029-.394 1.029-.927 0-.552-.42-.94-1.029-.94-.584 0-1.009.388-1.009.94 0 .533.425.927 1.01.927z"
                />
              </svg>
            </button>
          </div>
        </div>

        <p
          :style="!success ? 'color: #ff0000' : 'color: #3fb618'"
          v-if="apiMessage.length !== 0"
        >
          {{ apiMessage }}
        </p>
      </div>
      <div class="form-group">
        <SpinningButton
          :text="'Sign Up'"
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
      masterPassword: "",
      clicked: false,
      apiMessage: "",
      success: false,
    };
  },
  computed: {
    ...mapGetters(["currentUser"]),
  },
  methods: {
    ...mapActions(["login", "logout"]),
    async onSubmit(e) {
      if (
        this.username.length === 0 ||
        this.password.length === 0 ||
        this.masterPassword.length === 0
      ) {
        this.apiMessage = "Username and passwords cannot be empty";
        return;
      }

      this.clicked = true;
      let createUserRequest = JSON.stringify({
        username: this.username,
        password: this.password,
        masterPassword: this.masterPassword,
      });
      let response = await fetch("http://localhost:9091/users/", {
        method: "POST",
        headers: {
          "content-type": "application/json",
        },
        body: createUserRequest,
      });

      if (response.status === 201) {
        this.apiMessage =
          "Account created successfully. You will now be redirected to login page!";
        this.success = true;
        setTimeout(() => {
          this.$router.push("unlock");
        }, 1000);
      } else {
        let body = await response.json();
        this.success = false;
        this.apiMessage = body.message;
        this.clicked = false;
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
  height: 580px;
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