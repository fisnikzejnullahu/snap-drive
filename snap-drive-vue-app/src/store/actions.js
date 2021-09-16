const apiUrl = "http://localhost:9091";

export const onUserLogin = ({ commit }, user) => {
  commit("LOGGED_IN", {
    user: user,
  });
};

export const unlock = async ({ commit }, masterPassword) => {
  console.log("ACTIONS: LOGIN");
  let response = await fetch(`${apiUrl}/drive/unlock`, {
    method: "POST",
    headers: {
      "content-type": "application/json",
    },
    body: masterPassword,
  });

  if (response.status === 200) {
    let body = await response.json();
    console.log(body);
    commit("UNLOCKED", {
      files: body.files,
      size: {
        storageSizeSnapDrive: body.storageSizeSnapDrive,
        storageSizeSnapDrivePercentage: body.storageSizeSnapDrivePercentage,
        storageLimit: body.storageLimit,
        storageInDrive: body.storageInDrive,
        storageInGCM: body.storageInGCM,
      },
    });
  }
  return response;
};

export const deleteFile = async ({ commit }, fileId) => {
  console.log("ACTIONS: DELETEfile");

  let response = await fetch(`${apiUrl}/drive/${fileId}`, {
    method: "DELETE",
  });

  if (response.status === 204) {
    commit("DELETE_FILE", fileId);
  }
  return response;
};

export const uploadFile = async ({ commit }, formData) => {
  try {
    let response = await fetch(`${apiUrl}/drive/upload`, {
      headers: {
        accept: "*/*",
      },
      body: formData,
      method: "POST",
    });

    if (response.status === 201) {
      let locationHeader = response.headers.get("Location");
      let getFileResponse = await fetch(locationHeader);
      let addedFile = await getFileResponse.json();
      commit("ADD_FILE", addedFile);
    }

    return response;
  } catch (ex) {
    return null;
  }
};

export const getDriveSize = async ({ commit }) => {
  let response = await fetch(`${apiUrl}/drive/size`);

  if (response.status === 200) {
    let body = await response.json();
    commit("UPDATE_SIZE", body);
  }

  return response;
};

export const getDrive = async ({ commit }) => {
  let response = await fetch(`${apiUrl}/drive`);

  if (response.status === 200) {
    let files = await response.json();
    commit("NEW_FILES", files);
  }

  return response;
};

export const updateUserUsername = async ({ commit }, data) => {
  let response = await fetch(`${apiUrl}/users/settings/profile`, {
    method: "PUT",
    body: JSON.stringify(data),
    headers: {
      "content-type": "application/json",
    },
  });

  if (response.status === 200) {
    commit("UPDATE_USERNAME", data.newUsername);
  }

  return response;
};

export const logout = async ({ commit }) => {
  let response = await fetch(`${apiUrl}/users/logout`, {
    method: "POST",
  });

  if (response.status === 204) {
    commit("LOGGED_OUT");
  }

  return response;
};
