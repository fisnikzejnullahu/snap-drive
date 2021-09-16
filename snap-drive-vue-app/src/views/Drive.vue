<template>
  <div class="container-fluid">
    <div class="row">
      <div class="col-3">
        <h3>
          {{ !sharedOnlyDrive ? "My Drive:" : "Files shared with me:" }}
          <button
            type="button"
            class="btn bg-transparent"
            data-toggle="tooltip"
            data-placement="right"
            title="In this page you can see your decrypted files! Original files stored in our servers are encrypted (even file names) and only you can decrypt them!"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="24"
              height="24"
              fill="currentColor"
              class="bi bi-question-circle"
              viewBox="0 0 16 16"
            >
              <path
                d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z"
              />
              <path
                d="M5.255 5.786a.237.237 0 0 0 .241.247h.825c.138 0 .248-.113.266-.25.09-.656.54-1.134 1.342-1.134.686 0 1.314.343 1.314 1.168 0 .635-.374.927-.965 1.371-.673.489-1.206 1.06-1.168 1.987l.003.217a.25.25 0 0 0 .25.246h.811a.25.25 0 0 0 .25-.25v-.105c0-.718.273-.927 1.01-1.486.609-.463 1.244-.977 1.244-2.056 0-1.511-1.276-2.241-2.673-2.241-1.267 0-2.655.59-2.75 2.286zm1.557 5.763c0 .533.425.927 1.01.927.609 0 1.028-.394 1.028-.927 0-.552-.42-.94-1.029-.94-.584 0-1.009.388-1.009.94z"
              />
            </svg>
          </button>
        </h3>
      </div>
      <div class="col-4 mt-1">
        <input
          type="text"
          class="form-control popo"
          v-model.trim="searchQuery"
          placeholder="Search..."
        />
      </div>
      <div class="col mt-1" v-if="!sharedOnlyDrive">
        <form ref="form" style="display: none">
          <input
            type="file"
            multiple
            name="uploadedFile"
            style="opacity: 0; height: 1px"
            @change="onChange"
            ref="file"
          />
        </form>
        <button
          type="button"
          class="btn btn-info btn-block rounded"
          id="file-browser"
          :disabled="uploading"
          @click="$refs.file.click()"
        >
          {{ !uploading ? "+ Upload File" : "Uploading..." }}
        </button>
      </div>
    </div>

    <hr />

    <div v-if="sharedOnlyDrive && !loaded">
      <LoadingScreen />
    </div>

    <div v-else>
      <div v-if="sharedOnlyDrive" class="mt-4 row">
        <p v-if="sharedFiles.length === 0">
          You don't have any files shared with you!
        </p>
        <DriveFile
          :sharedFile="true"
          v-for="sharedFile in sharedFiles"
          :key="sharedFile.googleDriveId"
          :file="sharedFile"
          :sharedBy="sharedFile.sharedBy"
          @open-modal="openFileInfoModal(sharedFile)"
        />
        <DriveFileInformationOld
          :sharedFile="true"
          :file="selectedFile"
          ref="fileInformationModal"
          @delete-file="onDeleteFile(selectedFile.googleDriveId)"
        />
      </div>
      <div v-else class="mt-4 row">
        <p v-if="!uploading && filteredList.length === 0">
          You don't have any files in your drive!
        </p>
        <DriveFile v-if="uploading" :file="uploadingFile" :uploading="true" />
        <DriveFile
          v-for="file in filteredList"
          :key="file.link"
          :file="file"
          @open-modal="openFileInfoModal(file)"
        />
        <DriveFileInformationOld
          :sharedFile="false"
          :file="selectedFile"
          ref="fileInformationModal"
          @delete-file="onDeleteFile(selectedFile.googleDriveId)"
          @on-share-file="onShareFileOpen(selectedFile.googleDriveId)"
        />
        <ShareFileModal
          ref="shareFileModal"
          @on-start-share-file="
            onStartShareFileClick(selectedFile.googleDriveId, ...arguments)
          "
        />
      </div>
    </div>
  </div>
</template>

<script>
import { mapActions, mapGetters } from "vuex";
import DriveFile from "../components/DriveFile.vue";
import DriveFileInformationOld from "../components/DriveFileInformationOld.vue";
import LoadingScreen from "../components/LoadingScreen.vue";
import ShareFileModal from "../components/ShareFileModal.vue";

export default {
  name: "Drive",
  components: {
    DriveFile,
    DriveFileInformationOld,
    LoadingScreen,
    ShareFileModal,
  },
  props: {
    sharedOnlyDrive: Boolean,
  },
  data() {
    return {
      selectedFile: null,
      loaded: false,
      uploading: false,
      uploadingFile: {},
      searchQuery: "",
      apiUrl: "http://localhost:9091/drive",
      sharedFiles: [],
    };
  },
  mounted() {
    if (this.sharedOnlyDrive) {
      console.log("yep");
      this.fetchSharedWithMe();
    }
  },
  methods: {
    ...mapActions(["deleteFile", "uploadFile", "getDriveSize"]),
    onChange() {
      this.filelist = [...this.$refs.file.files];
      this.upload();
    },
    async upload() {
      let formData = new FormData();
      formData.append("data", this.filelist[0]);
      formData.append("name", this.filelist[0].name);

      this.uploadingFile = { fileName: this.filelist[0].name };
      this.uploading = true;
      console.log(this.filelist[0]);

      let response = await this.uploadFile(formData);

      if (response === null) {
        this.uploading = false;
        alert("Error. File not uploaded!");
        return;
      }

      if (response.status === 201) {
        this.getDriveSize();
        this.makeToast("Success", "File uploaded", "success");
      } else {
        this.makeToast("Error", "File was not uploaded!", "danger");
      }
      this.uploading = false;
      this.uploadingFile = {};
    },
    async onDeleteFile(fileId) {
      let response = await this.deleteFile(fileId);
      if (response.status === 204) {
        this.$refs.fileInformationModal.hide();
        this.getDriveSize();
        this.makeToast("Success", "File deleted", "success");
      }
    },
    async onShareFileOpen(fileId) {
      console.log(fileId);
      this.$refs.shareFileModal.showModal();
    },
    async onStartShareFileClick(fileId, recipientEmail) {
      console.log("SHARING...");
      console.log(recipientEmail);
      let response = await fetch(
        `${this.apiUrl}/${fileId}/file-shares?recipientEmail=${recipientEmail}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      console.log(response.status);
      if (response.status === 200) {
        let body = await response.json();
        this.selectedFile.permissions.push({
          emailAddress: body.emailAddress,
          role: body.role,
        });

        console.log(this.selectedFile.shares);
        this.$refs.shareFileModal.hideModal();
        this.makeToast("Success", "File shared successfully", "success");
      } else {
        let body = await response.json();
        console.log(body);
        this.$refs.shareFileModal.showErrorMessage(body.message);
      }
    },
    async fetchSharedWithMe() {
      let response = await fetch(`${this.apiUrl}?sharedWithMeOnly`);
      let body = await response.json();
      this.sharedFiles = body.files;
      console.log(this.sharedFiles);
      this.loaded = true;
    },
    openFileInfoModal(file) {
      console.log("OPENNING..................");
      this.selectedFile = file;
      this.$refs.fileInformationModal.show();
    },
    makeToast(title, message, variant) {
      this.$bvToast.toast(message, {
        title: title,
        variant: variant,
        solid: true,
        appendToast: true,
      });
    },
  },
  computed: {
    ...mapGetters(["currentUser", "driveSize", "driveFiles"]),
    filteredList() {
      if (this.driveFiles.length !== 0 && this.searchQuery.length !== 0) {
        return this.driveFiles.filter((file) => {
          return file.fileName
            .toLowerCase()
            .includes(this.searchQuery.toLowerCase());
        });
      } else {
        return this.driveFiles;
      }
    },
  },
};
</script>

<style scoped>
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

.popo {
  border: 1px solid #ced4da !important;
}
</style>
