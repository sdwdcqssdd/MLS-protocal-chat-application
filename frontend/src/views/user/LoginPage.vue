<script setup>

import LoginDialog from "@/components/login/LoginDialog.vue";
import {onMounted, ref} from "vue";

const loginDialogRef = ref()

onMounted(() => {
  loginDialogRef.value.openDialog()
})

</script>

<template>

  <LoginDialog
      :closable="false"
      ref="loginDialogRef"
  />

</template>

<style scoped>

</style>