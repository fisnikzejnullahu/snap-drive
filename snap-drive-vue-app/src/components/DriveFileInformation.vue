<template>
  <b-modal
    id="file-info-modal"
    title="File Information"
    style="right: 0 !important; padding: 0 !important"
    :cancel-disabled="true"
    :hide-footer="true"
  >
    <div class="modal-body">
      <div class="mb-3">
        <h6><span class="badge badge-light">Name</span></h6>
        <h6 class="info-value">{{ file.fileName }}</h6>
      </div>
      <div class="mb-3">
        <h6><span class="badge badge-light">Size</span></h6>
        <h6 class="info-value">{{ file.readableSize }}</h6>
      </div>
      <div class="mb-3">
        <h6><span class="badge badge-light">Type</span></h6>
        <h6 class="info-value">
          {{
            file.fileName.substring(
              file.fileName.lastIndexOf(".") + 1,
              file.fileName.length
            ) || file.fileName
          }}
        </h6>
      </div>
      <div class="mb-3">
        <h6><span class="badge badge-light">Created</span></h6>
        <h6 class="info-value">{{ file.createdAt }}</h6>
      </div>
      <div class="mb-3">
        <h6>
          <span class="badge badge-light">Allowed users:</span>
        </h6>
        <span
          class="info-value"
          style="font-size: 13px; display: inline-block"
          v-for="permission in file.permissions"
          :key="permission.recipientUsername"
        >
          <span>{{ permission.emailAddress }}</span>
          <span class="text-primary font-weight-bold"
            >({{ permission.role }})</span
          >
        </span>
      </div>
      <!-- <div v-if="sharedFile" class="mb-3">
        <h6><span class="badge badge-light">Shared By</span></h6>
        <h6 class="info-value">{{ file.sharedBy }}</h6>
      </div>
      <div v-if="sharedFile" class="mb-3">
        <h6><span class="badge badge-light">Shared At</span></h6>
        <h6 class="info-value">{{ file.sharedAt }}</h6>
      </div> -->
    </div>
    <div class="modal-footer" v-if="!sharedFile">
      <button
        class="btn btn-info btn-block"
        style="color: #fff"
        @click="shareFile"
      >
        Share
      </button>

      <SpinningButton
        :text="'Delete'"
        :textColor="'#fff'"
        :clicked="clickedDelete"
        :enableClickHandler="true"
        :classNames="'btn btn-danger btn-block'"
        @onClick="deleteFile"
      />
    </div>
  </b-modal>
  <!-- <div id="modal" v-if="showModal">
    <div
      class="modal fade show"
      id="exampleModal4"
      tabindex="-1"
      role="dialog"
      aria-labelledby="exampleModalLabel4"
      style="display: block"
      aria-hidden="true"
    >
      <div class="modal-dialog modal-dialog-slideout modal-sm" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="exampleModalLabel">File Information</h5>
            <button
              type="button"
              class="close"
              data-dismiss="modal"
              aria-label="Close"
              @click="hide"
            >
              <span aria-hidden="true">X</span>
            </button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <h6><span class="badge badge-light">Name</span></h6>
              <h6 class="info-value">{{ file.fileName }}</h6>
            </div>
            <div class="mb-3">
              <h6><span class="badge badge-light">Size</span></h6>
              <h6 class="info-value">{{ file.readableSize }}</h6>
            </div>
            <div class="mb-3">
              <h6><span class="badge badge-light">Type</span></h6>
              <h6 class="info-value">
                {{
                  file.fileName.substring(
                    file.fileName.lastIndexOf(".") + 1,
                    file.fileName.length
                  ) || file.fileName
                }}
              </h6>
            </div>
            <div class="mb-3">
              <h6><span class="badge badge-light">Created</span></h6>
              <h6 class="info-value">{{ file.createdAt }}</h6>
            </div>
            <div
              v-if="!sharedFile && file.shares && file.shares.length !== 0"
              class="mb-3"
            >
              <h6>
                <span class="badge badge-light"
                  >You have shared this file with:</span
                >
              </h6>
              <span
                class="info-value"
                style="font-size: 13px; display: inline-block"
                v-for="share in file.shares"
                :key="share.recipientUsername"
              >
                <span class="text-primary">{{ share.recipientUsername }}</span>
                on
                <span class="text-primary">{{ share.sharedAt }}</span>
              </span>
            </div>
            <div v-if="sharedFile" class="mb-3">
              <h6><span class="badge badge-light">Shared By</span></h6>
              <h6 class="info-value">{{ file.sharedBy }}</h6>
            </div>
            <div v-if="sharedFile" class="mb-3">
              <h6><span class="badge badge-light">Shared At</span></h6>
              <h6 class="info-value">{{ file.sharedAt }}</h6>
            </div>
          </div>
          <div class="modal-footer" v-if="!sharedFile">
            <button
              class="btn btn-info btn-block"
              style="color: #fff"
              @click="shareFile"
            >
              Share
            </button>

            <SpinningButton
              :text="'Delete'"
              :textColor="'#fff'"
              :clicked="clickedDelete"
              :enableClickHandler="true"
              :classNames="'btn btn-danger btn-block'"
              @onClick="deleteFile"
            />
          </div>
        </div>
      </div>
    </div>
  </div> -->
</template>

<script>
// import SpinningButton from "./SpinningButton.vue";
export default {
  // components: { SpinningButton },
  props: {
    file: Object,
    sharedFile: Boolean,
  },
  mounted() {
    console.log("+++++++++++++++++++++++");
    console.log(this.file);
  },
  data() {
    return {
      clickedDelete: false,
      showModal: false,
    };
  },
  methods: {
    show() {
      console.log(this.file);
      this.$bvModal.show("file-info-modal");
      this.showModal = true;
    },
    hide() {
      this.clickedDelete = false;
      this.showModal = false;
    },
    deleteFile() {
      this.clickedDelete = true;

      if (confirm("Are you sure you want to delete?")) {
        // Save it!
        console.log("delete " + this.file.id);
        this.$emit("delete-file");
      } else {
        this.clickedDelete = false;
      }
    },
    shareFile() {
      console.log("SHARE CIK");
      this.$emit("on-share-file");
    },
  },
  computed: {
    fileSharesToString() {
      var usernamesString = "";
      this.file.shares.forEach((share) => {
        usernamesString += share.recipientUsername + ",";
      });
      return usernamesString;
    },
  },
};
</script>
<style>
.modal-dialog {
  min-height: 100%;
  width: 100% !important;
  margin: 0 !important;
  padding: 0 !important;
  margin: 0;
  background: #fff;
}
.modal.fade .modal-dialog.modal-dialog {
  -webkit-transform: translate(100%, 0) scale(1);
  transform: translate(100%, 0) scale(1);
}
.modal.fade.show .modal-dialog.modal-dialog {
  -webkit-transform: translate(0, 0);
  transform: translate(0, 0);
  display: flex;
  align-items: stretch;
  -webkit-box-align: stretch;
  height: 100%;
}
.modal.fade.show .modal-dialog.modal-dialog .modal-body {
  overflow-y: auto;
  overflow-x: hidden;
}
.modal-dialog .modal-content {
  border: 0;
}
.modal-dialog .modal-header,
.modal-dialog .modal-footer {
  display: block;
}
.modal-dialog .modal-header h5 {
  float: left;
}

.modal {
  top: 0 !important;
  right: 0 !important;
  width: 15% !important;
  overflow: hidden !important;
}

.modal-content {
  top: 0 !important;
  right: 0 !important;
}
.info-value {
  font-weight: normal;
  margin-left: 5px;
}
</style>