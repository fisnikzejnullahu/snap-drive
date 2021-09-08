<template>
  <transition name="slide">
    <div id="modal" v-if="showModal">
      <div
        class="modal fade show"
        id="exampleModal4"
        tabindex="-1"
        role="dialog"
        aria-labelledby="exampleModalLabel4"
        style="display: block"
        aria-hidden="true"
      >
        <div
          class="modal-dialog modal-dialog-slideout modal-sm"
          role="document"
        >
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title" id="exampleModalLabel">
                File Information
              </h5>
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
                <h6 class="info-value">{{ file.readableCreatedAt }}</h6>
              </div>
              <div v-if="sharedFile" class="mb-3">
                <h6><span class="badge badge-light">Shared By</span></h6>
                <h6 class="info-value">{{ file.sharedBy }}</h6>
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
    </div>
  </transition>
</template>

<script>
import SpinningButton from "./SpinningButton.vue";
export default {
  components: { SpinningButton },
  props: {
    file: Object,
    sharedFile: Boolean,
  },
  data() {
    return {
      clickedDelete: false,
      showModal: false,
    };
  },
  methods: {
    show() {
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
};
</script>
<style scoped>
.modal-dialog-slideout {
  min-height: 100%;
  margin: 0 0 0 auto;
  background: #fff;
}
.modal.fade .modal-dialog.modal-dialog-slideout {
  -webkit-transform: translate(100%, 0) scale(1);
  transform: translate(100%, 0) scale(1);
}
.modal.fade.show .modal-dialog.modal-dialog-slideout {
  -webkit-transform: translate(0, 0);
  transform: translate(0, 0);
  display: flex;
  align-items: stretch;
  -webkit-box-align: stretch;
  height: 100%;
}
.modal.fade.show .modal-dialog.modal-dialog-slideout .modal-body {
  overflow-y: auto;
  overflow-x: hidden;
}
.modal-dialog-slideout .modal-content {
  border: 0;
}
.modal-dialog-slideout .modal-header,
.modal-dialog-slideout .modal-footer {
  display: block;
}
.modal-dialog-slideout .modal-header h5 {
  float: left;
}

.modal {
  right: 0 !important;
  left: auto;
  width: 15% !important;
}

.modal-dialog {
  width: 100% !important;
}

.info-value {
  font-weight: normal;
  margin-left: 5px;
}
</style>
