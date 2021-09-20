<template>
  <form @submit.prevent="saveChanges">
    <h4 class="font-weight-bold">Change Master Password</h4>
    <p class="small text-muted mb-2">
      Master password is the key to unlock your files! Please memorize it
      because your files can get locked forever without correct key!
    </p>
    <p class="small text-muted mb-2">
      You need to provide your old master password so we can decrypt files! You
      also provide new master password that will be used to reencrypt
    </p>
    <p class="small text-muted mb-2">
      If old password is not correct you cannot update it with a new one!!!
    </p>
    <div class="row mb-4">
      <div class="col-md-6">
        <div class="form-group">
          <label for="oldMasterPassword">Old master password</label>
          <input
            type="password"
            class="form-control"
            v-model.trim="oldMasterPassword"
          />
        </div>
        <div class="form-group">
          <label for="newMasterPassword">New master password</label>
          <input
            type="password"
            class="form-control"
            v-model.trim="newMasterPassword"
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
        />
      </div>
    </div>
  </form>
</template>

<script>
import { mapActions } from "vuex";
import SpinningButton from "./SpinningButton.vue";
export default {
  components: { SpinningButton },
  data() {
    return {
      newMasterPassword: "",
      oldMasterPassword: "",
      clicked: false,
      loaded: false,
      apiMessage: "",
      success: "",
    };
  },
  methods: {
    ...mapActions(["getDrive"]),
    async saveChanges() {
      if (this.newMasterPassword.length === 0) {
        alert("new password cannot be empty");
        return;
      }

      this.clicked = true;

      let data = {
        oldMasterPassword: this.oldMasterPassword,
        newMasterPassword: this.newMasterPassword,
      };

      let response = await fetch(
        "http://localhost:8882/users/settings/master-password",
        {
          method: "PUT",
          body: JSON.stringify(data),
          headers: {
            "content-type": "application/json",
          },
        }
      );

      if (response.status === 200) {
        this.getDrive();
        this.apiMessage = "Master password updated successfully!";
        this.success = true;
        this.newMasterPassword = "";
        this.oldMasterPassword = "";
      } else {
        let body = await response.json();
        this.apiMessage = body.message;
      }
      this.clicked = false;
    },
  },
};
</script>

<style>
</style>