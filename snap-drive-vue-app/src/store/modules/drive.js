const state = {
  user: null,
  files: null,
  size: null,
};

const mutations = {
  UNLOCKED(state, { user, files, size }) {
    console.log("UNLOCKED");
    state.user = user;
    state.files = files;
    state.size = size;
  },
  DELETE_FILE(state, fileId) {
    let fileIndex = state.files.findIndex((file) => file.id === fileId);
    state.files.splice(fileIndex, 1);
  },
  ADD_FILE(state, newFile) {
    state.files = [newFile].concat(state.files);
  },
  UPDATE_SIZE(state, newSize) {
    state.size = newSize;
  },
  LOGGED_IN(state, user) {
    console.log("MUTATION LGGEDIN ");
    console.log(user);
    state.user = user;
  },
  LOGGED_OUT(state) {
    state.user = null;
    state.files = null;
    state.size = null;
  },
  NEW_FILES(state, newFiles) {
    console.log("NEW+FILE");
    console.log(newFiles);
    console.log(state.files);
    state.files = newFiles;
    console.log(state.files);
  },
  UPDATE_USERNAME(state, newUsername) {
    state.user.username = newUsername;
  },
};

export default {
  state,
  mutations,
};
