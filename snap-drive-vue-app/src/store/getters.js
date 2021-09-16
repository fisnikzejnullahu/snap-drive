export const driveFiles = (state) => {
  console.log(state.drive.files);
  return state.drive.files;
};

export const driveSize = (state) => {
  console.log('GETTERSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS');
  console.log(state.drive.size);
  return state.drive.size;
};

export const currentUser = (state) => {
  return state.drive.user;
};
