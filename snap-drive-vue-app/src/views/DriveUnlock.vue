<template>
  <transition name="slide" v-if="show">
    <div class="login-dark">
      <form method="post" @submit.prevent="onSubmit">
        <h2 class="sr-only">Login Form</h2>
        <div class="illustration text-primary">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="128"
            height="128"
            fill="currentColor"
            class="bi bi-shield-lock"
            viewBox="0 0 16 16"
          >
            <path
              d="M5.338 1.59a61.44 61.44 0 0 0-2.837.856.481.481 0 0 0-.328.39c-.554 4.157.726 7.19 2.253 9.188a10.725 10.725 0 0 0 2.287 2.233c.346.244.652.42.893.533.12.057.218.095.293.118a.55.55 0 0 0 .101.025.615.615 0 0 0 .1-.025c.076-.023.174-.061.294-.118.24-.113.547-.29.893-.533a10.726 10.726 0 0 0 2.287-2.233c1.527-1.997 2.807-5.031 2.253-9.188a.48.48 0 0 0-.328-.39c-.651-.213-1.75-.56-2.837-.855C9.552 1.29 8.531 1.067 8 1.067c-.53 0-1.552.223-2.662.524zM5.072.56C6.157.265 7.31 0 8 0s1.843.265 2.928.56c1.11.3 2.229.655 2.887.87a1.54 1.54 0 0 1 1.044 1.262c.596 4.477-.787 7.795-2.465 9.99a11.775 11.775 0 0 1-2.517 2.453 7.159 7.159 0 0 1-1.048.625c-.28.132-.581.24-.829.24s-.548-.108-.829-.24a7.158 7.158 0 0 1-1.048-.625 11.777 11.777 0 0 1-2.517-2.453C1.928 10.487.545 7.169 1.141 2.692A1.54 1.54 0 0 1 2.185 1.43 62.456 62.456 0 0 1 5.072.56z"
            />
            <path
              d="M9.5 6.5a1.5 1.5 0 0 1-1 1.415l.385 1.99a.5.5 0 0 1-.491.595h-.788a.5.5 0 0 1-.49-.595l.384-1.99a1.5 1.5 0 1 1 2-1.415z"
            />
          </svg>
          <h4 class="mt-3">
            {{
              createMasterPassword
                ? "Create master password"
                : "What's the password?"
            }}
          </h4>
          <p v-if="createMasterPassword" class="forgot">
            Master password is the key to unlock your files! Please memorize it
            because your files can get locked forever without correct key!
            <span class="text-success">({{ currentUser.user.email }})</span>
          </p>
          <p v-else class="forgot">
            Enter your master password to unlock your files
            <span class="text-success">({{ currentUser.user.email }})</span>
          </p>
        </div>
        <div class="form-group">
          <input
            class="form-control"
            type="password"
            v-model.trim="masterPassword"
            placeholder="Password"
          />
          <p
            :style="!success ? 'color: #ff0000' : 'color: #3fb618'"
            v-if="apiMessage.length !== 0"
          >
            {{ apiMessage }}
          </p>
        </div>
        <div class="form-group">
          <SpinningButton
            :text="createMasterPassword ? 'Submit' : 'Unlock'"
            :textColor="'#fff'"
            :classNames="'btn btn-light btn-block rounded'"
            :clicked="clicked"
            :style="'background: linear-gradient(90deg, #34d399 0%, #4154f1 100%); color: #fff'"
          />
        </div>
        <div class="form-group">
          <p
            class="text-center forgot"
            style="cursor: pointer"
            @click="backToHome"
          >
            ← Back to home
          </p>
        </div>
      </form>
    </div>
  </transition>
</template>

<script>
import { mapActions, mapGetters } from "vuex";
import SpinningButton from "../components/SpinningButton.vue";

export default {
  components: { SpinningButton },
  props: {
    createMasterPassword: Boolean,
  },
  data() {
    return {
      masterPassword: "",
      clicked: false,
      show: false,
      apiMessage: "",
      success: true,
      apiUrl: "http://localhost:8882/users",
    };
  },
  mounted() {
    console.log(this.createMasterPassword);
    console.log(this.currentUser);
    if (this.currentUser.user === null) {
      this.$router.push("login");
    }
    setTimeout(() => {
      this.show = true;
    }, 100);
  },
  computed: {
    ...mapGetters(["currentUser"]),
  },
  methods: {
    ...mapActions(["unlock", "logout"]),
    backToHome() {
      this.logout();
      this.$router.push("/");
    },
    async onSubmit(e) {
      this.clicked = true;
      this.apiMessage = "";
      this.success = true;

      if (this.createMasterPassword) {
        let resp = await fetch(`${this.apiUrl}/master-password`, {
          method: "POST",
          headers: {
            "content-type": "application/json",
          },
          body: JSON.stringify({ masterPassword: this.masterPassword }),
        });

        if (resp.status === 200) {
          this.apiMessage = "Password created. We're signing you in...";
        } else {
          this.apiMessage = "Something went wrong!";
          this.success = false;
          this.clicked = false;
          return;
        }
      }
      let response = await this.unlock(
        JSON.stringify({ masterPassword: this.masterPassword })
      );

      if (response.status === 200) {
        this.$router.push("drive");
      } else if (response.status === 403) {
        this.apiMessage = "Invalid password!";
        this.success = false;
      }
      this.masterPassword = "";
      this.clicked = false;
    },
  },
};
</script>

<style scoped>
.login-dark {
  height: 100%;
  background: url(../assets/img/hero-bg.png) top center no-repeat;
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