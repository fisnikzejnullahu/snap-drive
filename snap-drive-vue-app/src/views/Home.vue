<template>
  <div>
    <header id="header" class="header fixed-top mt-2">
      <div
        class="
          container-fluid container-xl
          d-flex
          align-items-center
          justify-content-between
        "
      >
        <a href="index.html" class="logo d-flex align-items-center"
          ><img src="@/assets/img/logo.png" alt="" /><span>SnapDrive</span></a
        >
      </div>
    </header>
    <section id="hero" class="hero d-flex align-items-center">
      <div class="container">
        <div class="row">
          <div class="col-lg-6 d-flex flex-column justify-content-center">
            <h1 data-aos="fade-up">
              SnapDrive | Your Data. Your Cloud. Your Privacy.
            </h1>
            <h2 data-aos="fade-up" data-aos-delay="400">
              Encrypt your files and upload them yo Cloud, without worrying
              about who can access it!
            </h2>
            <div data-aos="fade-up" data-aos-delay="600">
              <div class="text-start text-lg-start">
                <a
                  href="#signin"
                  class="
                    btn-get-started
                    scrollto
                    d-inline-flex
                    align-items-center
                    justify-content-center
                    align-self-center
                  "
                  ><span>Get Started</span><i class="bi bi-arrow-right"></i
                ></a>
              </div>
            </div>
          </div>
          <div
            data-aos="zoom-out"
            data-aos-delay="200"
            class="col-lg-6 hero-img"
          >
            <img src="@/assets/img/hero-img.png" alt="" class="img-fluid" />
          </div>
        </div>
      </div>
    </section>
    <main id="main">
      <section id="about" class="about">
        <div data-aos="fade-up" class="container">
          <div class="row gx-0">
            <div
              data-aos="fade-up"
              data-aos-delay="200"
              class="col-lg-6 d-flex flex-column justify-content-center"
            >
              <div class="content">
                <h3>Why SnapDrive?</h3>
                <h2>Secure any file on your cloud</h2>
                <p>
                  Encryption protects your privacy. It gives you full control on
                  your files. With SnapDrive, encryption and decryption of your
                  data will be done on your local machine without no one else
                  knowing what's going on (neither do we).
                </p>
              </div>
            </div>
            <div
              data-aos="zoom-out"
              data-aos-delay="200"
              class="col-lg-6 d-flex align-items-center"
            >
              <img src="@/assets/img/about.jpg" alt="" class="img-fluid" />
            </div>
          </div>
        </div>
      </section>
    </main>
    <footer id="signin" class="footer">
      <div class="footer-newsletter">
        <div class="container">
          <div class="row justify-content-center">
            <div class="col-lg-12 text-center">
              <h4>Start using SnapDrive</h4>
              <p>
                Sign in with your Google Account. Files will be safely uploaded
                on your Google Drive.
              </p>
            </div>

            <button
              @click="signIn"
              type="button"
              :disabled="disabled"
              class="login-with-google-btn rounded-pill border-0 shadow-sm"
            >
              {{ googleBtnText }}
            </button>
          </div>
        </div>
      </div>
      <div class="container">
        <div class="credits">
          Designed by <a href="https://bootstrapmade.com/">BootstrapMade</a>
        </div>
      </div>
    </footer>
    <a
      href="#"
      class="back-to-top d-flex align-items-center justify-content-center"
      ><i class="bi bi-arrow-up-short"></i
    ></a>
  </div>
</template>

<script>
import { mapActions } from "vuex";
export default {
  data() {
    return {
      clicked: false,
      apiUrl: "http://localhost:8882",
      disabled: false,
      googleBtnText: "Sign in with Google",
    };
  },
  methods: {
    ...mapActions(["onUserLogin"]),
    async signIn() {
      this.disabled = true;
      try {
        const authCode = await this.$gAuth.getAuthCode();
        console.log(authCode);
        let response = await fetch(
          `${this.apiUrl}/users/signin?authCode=${authCode}`,
          {
            method: "POST",
          }
        );

        if (response.ok) {
          this.googleBtnText = "Signing in...";
          let user = await response.json();
          this.onUserLogin(user);

          /*
          if user just created, then we forward to driveunlock view, with param creteMasterPassword = True
          In DriveUnlock if creteMasterPassword = true, that will make a http request to api that creates new master password
          Else if creteMasterPassword = false, DriveUnlock will make a http request to api that tries to unlock drive
        */
          if (response.status === 201) {
            this.$router.push({
              name: "DriveUnlock",
              params: { createMasterPassword: true },
            });
          } else if (response.status === 200) {
            this.$router.push({
              name: "DriveUnlock",
              params: { createMasterPassword: false },
            });
          }
        }
      } catch (e) {
        this.disabled = false;
      }
    },
  },
};
</script>

<style scoped>
.login-with-google-btn {
  padding: 12px 16px 12px 42px;
  color: #757575;
  font-size: 14px;
  font-weight: 500;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen,
    Ubuntu, Cantarell, "Fira Sans", "Droid Sans", "Helvetica Neue", sans-serif;

  background-image: url(data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTgiIGhlaWdodD0iMTgiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGcgZmlsbD0ibm9uZSIgZmlsbC1ydWxlPSJldmVub2RkIj48cGF0aCBkPSJNMTcuNiA5LjJsLS4xLTEuOEg5djMuNGg0LjhDMTMuNiAxMiAxMyAxMyAxMiAxMy42djIuMmgzYTguOCA4LjggMCAwIDAgMi42LTYuNnoiIGZpbGw9IiM0Mjg1RjQiIGZpbGwtcnVsZT0ibm9uemVybyIvPjxwYXRoIGQ9Ik05IDE4YzIuNCAwIDQuNS0uOCA2LTIuMmwtMy0yLjJhNS40IDUuNCAwIDAgMS04LTIuOUgxVjEzYTkgOSAwIDAgMCA4IDV6IiBmaWxsPSIjMzRBODUzIiBmaWxsLXJ1bGU9Im5vbnplcm8iLz48cGF0aCBkPSJNNCAxMC43YTUuNCA1LjQgMCAwIDEgMC0zLjRWNUgxYTkgOSAwIDAgMCAwIDhsMy0yLjN6IiBmaWxsPSIjRkJCQzA1IiBmaWxsLXJ1bGU9Im5vbnplcm8iLz48cGF0aCBkPSJNOSAzLjZjMS4zIDAgMi41LjQgMy40IDEuM0wxNSAyLjNBOSA5IDAgMCAwIDEgNWwzIDIuNGE1LjQgNS40IDAgMCAxIDUtMy43eiIgZmlsbD0iI0VBNDMzNSIgZmlsbC1ydWxlPSJub256ZXJvIi8+PHBhdGggZD0iTTAgMGgxOHYxOEgweiIvPjwvZz48L3N2Zz4=);
  background-color: white;
  background-repeat: no-repeat;
  background-position: 12px 11px;
}

.login-with-google-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 -1px 0 rgba(0, 0, 0, 0.04), 0 2px 4px rgba(0, 0, 0, 0.25);
}

.login-with-google-btn:active {
  background-color: #eeeeee;
}

.login-with-google-btn:focus {
  outline: none;
  box-shadow: 0 -1px 0 rgba(0, 0, 0, 0.04), 0 2px 4px rgba(0, 0, 0, 0.25),
    0 0 0 3px #c8dafc;
}

.login-with-google-btn:disabled {
  filter: grayscale(100%);
  background-color: #ebebeb;
  box-shadow: 0 -1px 0 rgba(0, 0, 0, 0.04), 0 1px 1px rgba(0, 0, 0, 0.25);
  cursor: not-allowed;
}
</style>