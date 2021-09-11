<template>
  <!-- TODO: <DriveFile :isShared=true, :sharedInfo={jsonObj} edhe kur te maron carden, if shared, qite *shared file, by who: username... -->
  <div
    v-if="uploading"
    class="card text-center shadow-sm p-3 mb-5 bg-white rounded"
    style="width: 15rem; background-color: #c9c9c9; cursor: not-allowed"
  >
    <LoadingScreen style="position: absolute; top: 40%; left: 40%" />
    <img
      :src="fileIcon(file.fileName)"
      class="card-img-top mx-auto d-block thumbnail"
      style="opacity: 0.4"
      :alt="file.fileName"
    />
    <div class="card-body" style="opacity: 0.4">
      <p class="card-text">{{ truncate(file.fileName) }}</p>
    </div>
    <span class="font-weight-bold text-primary"
      >Encrypting and Uploading...</span
    >
  </div>
  <div
    v-else
    @click="showFileInformationModal"
    class="card text-center shadow-sm p-3 mb-5 bg-white rounded"
    style="width: 15rem"
  >
    <img
      :src="fileIcon(file.fileName)"
      class="card-img-top mx-auto d-block thumbnail"
      :alt="file.fileName"
    />
    <div class="card-body">
      <p class="card-text">{{ truncate(file.fileName) }}</p>
    </div>
    <a
      class="btn btn-light"
      :href="
        !sharedFile
          ? `http://localhost:9091/drive/download?fileLink=${file.link}`
          : `http://localhost:9091/drive/download?fileLink=${file.link}&shared=true`
      "
      :download="file.fileName"
      ><svg
        xmlns="http://www.w3.org/2000/svg"
        width="16"
        height="16"
        fill="currentColor"
        class="bi bi-cloud-download"
        viewBox="0 0 16 16"
      >
        <path
          d="M4.406 1.342A5.53 5.53 0 0 1 8 0c2.69 0 4.923 2 5.166 4.579C14.758 4.804 16 6.137 16 7.773 16 9.569 14.502 11 12.687 11H10a.5.5 0 0 1 0-1h2.688C13.979 10 15 8.988 15 7.773c0-1.216-1.02-2.228-2.313-2.228h-.5v-.5C12.188 2.825 10.328 1 8 1a4.53 4.53 0 0 0-2.941 1.1c-.757.652-1.153 1.438-1.153 2.055v.448l-.445.049C2.064 4.805 1 5.952 1 7.318 1 8.785 2.23 10 3.781 10H6a.5.5 0 0 1 0 1H3.781C1.708 11 0 9.366 0 7.318c0-1.763 1.266-3.223 2.942-3.593.143-.863.698-1.723 1.464-2.383z"
        />
        <path
          d="M7.646 15.854a.5.5 0 0 0 .708 0l3-3a.5.5 0 0 0-.708-.708L8.5 14.293V5.5a.5.5 0 0 0-1 0v8.793l-2.146-2.147a.5.5 0 0 0-.708.708l3 3z"
        />
      </svg>
      Download</a
    >
  </div>
</template>

<script>
import LoadingScreen from "./LoadingScreen.vue";
export default {
  components: { LoadingScreen },
  name: "DriveFile",
  props: {
    uploading: Boolean,
    file: Object,
    sharedFile: Boolean,
    sharedAt: String,
    sharedBy: String,
  },
  mounted() {
    console.log(this.sharedFile);
    console.log(this.file);
  },
  data() {
    return {
      pdfIcon:
        "https://templates.iqonic.design/cloudbox/html/assets/images/layouts/page-1/pdf.png",
      docIcon:
        "https://templates.iqonic.design/cloudbox/html/assets/images/layouts/page-1/doc.png",
      xlsIcon:
        "https://templates.iqonic.design/cloudbox/html/assets/images/layouts/page-1/xlsx.png",
      pptIcon:
        "https://templates.iqonic.design/cloudbox/html/assets/images/layouts/page-1/ppt.png",
      txtIcon:
        "https://icon-library.com/images/text-file-icon/text-file-icon-5.jpg",
      zipIcon: "https://www.iconpacks.net/icons/2/free-zip-icon-1519-thumb.png",
      imageIcon: "https://icon-library.com/images/image-icon/image-icon-16.jpg",
      defaultFileIcon:
        "https://findicons.com/files/icons/2813/flat_jewels/512/file.png",
    };
  },
  methods: {
    showFileInformationModal() {
      this.$emit("open-modal", this.sharedAt, this.sharedBy);
    },
    fileIcon(fileName) {
      switch (fileName.substring(fileName.lastIndexOf(".") + 1)) {
        case "pdf":
          return this.pdfIcon;
        case "doc":
          return this.docIcon;
        case "docx":
          return this.docIcon;
        case "xls":
          return this.xlsIcon;
        case "xlsx":
          return this.xlsIcon;
        case "ppt":
          return this.pptIcon;
        case "txt":
          return this.txtIcon;
        case "zip":
          return this.zipIcon;
        case "rar":
          return this.zipIcon;
        case "png":
          return this.imageIcon;
        case "jpg":
          return this.imageIcon;
        case "jpeg":
          return this.imageIcon;
        default:
          return this.defaultFileIcon;
      }
    },
    truncate(source) {
      return source.length > 40 ? source.slice(0, 40 - 1) + "…" : source;
    },
  },
};
</script>

<style scoped>
.thumbnail {
  max-width: 100px;
  max-height: 100px;
  height: auto;
}

.card {
  margin-right: 15px !important;
  margin-left: 15px !important;
}

.card:hover {
  cursor: pointer;
  /* background-color: #fdfdfd !important; */
  transform: scale(1.01);
}
</style>