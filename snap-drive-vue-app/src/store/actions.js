const apiUrl = "http://localhost:9091";

export const login = async ({ commit }, userInfo) => {
  console.log("ACTIONS: LOGIN");
  let response = await fetch(`${apiUrl}/users/login`, {
    method: "POST",
    headers: {
      "content-type": "application/json",
    },
    body: userInfo,
  });

  console.log(userInfo);
  if (response.status === 200) {
    let body = await response.json();
    commit("LOGGED_IN", {
      user: {
        username: body.username,
      },
    });
  }

  return response;
};

export const unlock = async ({ commit }, masterPassword) => {
  console.log("ACTIONS: LOGIN");
  let response = await fetch(`${apiUrl}/users/unlock`, {
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
      user: {
        username: body.username,
        registerAt: body.registerAt,
      },
      files: body.files,
      size: body.size,
    });
  }
  return response;
};

export const deleteFile = async ({ commit }, fileId) => {
  console.log("ACTIONS: DELETEfile");

  let response = await fetch(`${apiUrl}/drive/${fileId}`, {
    method: "DELETE",
  });

  if (response.status === 200) {
    let body = await response.json();
    if (body.deleted) {
      commit("DELETE_FILE", fileId);
    }
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

    if (response.status === 200) {
      let addedFile = await response.json();
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
