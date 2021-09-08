<template>
  <form @submit.prevent="saveChanges">
    <h4 class="font-weight-bold">Change Password</h4>
    <p class="small text-muted mb-2">
      This is your account password that you use to login to your account! (not
      the files)
    </p>
    <div class="row mb-4">
      <div class="col-md-6">
        <div class="form-group">
          <label for="newPassword">New Password</label>
          <input
            type="password"
            class="form-control"
            v-model.trim="newPassword"
          />
        </div>
        <div class="form-group">
          <label for="newPasswordConfirm">Confirm Password</label>
          <input
            type="password"
            class="form-control"
            v-model.trim="newPasswordConfirm"
          />
          <p
            :style="!success ? 'color: #ff0000' : 'color: #3fb618'"
            v-if="apiMessage.length !== 0"
          >
            {{ apiMessage }}
          </p>
        </div>
        <SpinningButton
          :text="'Update'"
          :classNames="'btn btn-primary btn-lg btn-block'"
          :clicked="clicked"
          :textColor="'#fff'"
          @onClick="saveChanges"
        />
      </div>
    </div>
  </form>
</template>

<script>
import SpinningButton from "./SpinningButton.vue";
export default {
  components: { SpinningButton },
  data() {
    return {
      newPassword: "",
      newPasswordConfirm: "",
      clicked: false,
      loaded: false,
      apiMessage: "",
      success: false,
    };
  },
  methods: {
    async saveChanges() {
      if (this.newPassword.length === 0) {
        this.apiMessage = "new password cannot be empty";
        return;
      }

      if (this.newPassword !== this.newPasswordConfirm) {
        this.apiMessage = "passwords do not match";
        return;
      }

      this.clicked = true;

      let data = {
        newPassword: this.newPassword,
      };

      let response = await fetch(
        "http://localhost:9091/users/settings/password",
        {
          method: "PUT",
          body: JSON.stringify(data),
          headers: {
            "content-type": "application/json",
          },
        }
      );

      if (response.status === 200) {
        this.apiMessage = "Password updated successfully!";
        this.success = true;
        this.newPassword = "";
        this.newPasswordConfirm = "";
      } else {
        let body = await response.json();
        this.apiMessage = body.reason;
      }
      this.clicked = false;
    },
  },
};
</script>

<style>
</style>