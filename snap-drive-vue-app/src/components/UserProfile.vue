<template>
  <form @submit.prevent="saveChanges">
    <h4 class="font-weight-bold">Personal info</h4>
    <div class="row mb-4">
      <div class="col-md-6">
        <div class="form-group">
          <label for="username">Username</label>
          <input
            type="text"
            v-model.trim="username"
            class="form-control"
            required
          />
        </div>
        <div class="form-group">
          <label for="registerDate">Registered At</label>
          <input
            type="text"
            v-model="registerAt"
            class="form-control"
            required
            disabled
            readonly
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
import { mapActions, mapGetters } from "vuex";
import SpinningButton from "./SpinningButton.vue";
export default {
  components: { SpinningButton },
  data() {
    return {
      username: "",
      registerAt: "",
      clicked: false,
      loaded: false,
      apiMessage: "",
      success: "",
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
    ...mapActions(["updateUserUsername"]),
    async saveChanges() {
      if (this.username.length === 0) {
        alert("new username cannot be empty");
        return;
      }

      this.clicked = true;

      let data = {
        newUsername: this.username,
      };

      let response = await this.updateUserUsername(data);

      if (response.status === 200) {
        this.apiMessage = "Profile updated successfully!";
        this.success = true;
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