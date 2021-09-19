<template>
  <b-modal id="bv-modal-example" hide-footer>
    <template #modal-title> Share File </template>
    <div class="d-block">
      <p style="color: #ff0000" v-if="apiMessage.length !== 0">
        {{ apiMessage }}
      </p>
      <div class="form-group">
        <label :for="recipientEmail"
          >Email of user you want to share file with</label
        >
        <input
          type="text"
          class="form-control"
          aria-describedby="hint"
          placeholder="Email Address"
          v-model.trim="recipientEmail"
        />
        <small id="hint" class="form-text text-muted"
          >Recipient will never know your master password. He will use his own
          password to unlock this file!</small
        >
      </div>
    </div>
    <div class="row">
      <div class="col">
        <button
          type="button"
          class="btn btn-light btn-block rounded"
          @click="hideModal"
        >
          Cancel
        </button>
      </div>
      <div class="col">
        <SpinningButton
          :text="'Share'"
          :textColor="'#fff'"
          :clicked="clickedShare"
          :enableClickHandler="true"
          :classNames="'btn btn-info btn-block rounded'"
          @onClick="onStartShare"
        />
      </div>
    </div>
  </b-modal>
</template>

<script>
import SpinningButton from "./SpinningButton.vue";
export default {
  components: { SpinningButton },
  props: {
    file: Object,
  },
  data() {
    return {
      clickedShare: false,
      recipientEmail: "",
      apiMessage: "",
    };
  },
  methods: {
    showModal() {
      this.$bvModal.show("bv-modal-example");
    },
    hideModal() {
      this.clickedShare = false;
      this.recipientEmail = "";
      this.apiMessage = "";
      this.$bvModal.hide("bv-modal-example");
    },
    onStartShare() {
      if (this.recipientEmail.length === 0) {
        this.apiMessage = "Please specify recipient's email!";
        return;
      }
      this.clickedShare = true;
      this.$emit("on-start-share-file", this.recipientEmail);
    },
    showErrorMessage(msg) {
      this.apiMessage = msg;
      this.clickedShare = false;
    },
  },
};
</script>

<style>
</style>