<template>
  <div id="loader">
    <div
      class="spinner-border text-primary"
      style="width: 3rem; height: 3rem"
      role="status"
    >
      <span class="sr-only">Loading...</span>
    </div>
  </div>
</template>

<script>
export default {};
</script>

<style scoped>
#loader {
  position: fixed;
  left: 50%;
  top: 50%;
}
</style>