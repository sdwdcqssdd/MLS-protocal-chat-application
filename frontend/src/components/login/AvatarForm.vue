<template>
  <div class="form-body">
    <el-alert title="Your account has been successfully created" type="success" show-icon/>
    <span style="margin: 10px 0 10px 0;">Please set your avatar</span>
    <AvatarUploader @uploaded="notifyUploaded"/>
  </div>
</template>

<script>
import {Plus} from "@element-plus/icons-vue";
import AvatarUploader from "@/components/user/AvatarUploader.vue";

export default {
  name: 'AvatarForm',
  components: {AvatarUploader, Plus},
  setup(props, ctx) {
    const notifyUploaded = () => {
      ctx.emit('uploaded')
    }
    return {
      notifyUploaded
    }
  }
}
</script>

<style scoped>
.form-body {
  display: flex;
  flex-direction: column;
  align-items: center;
}
</style>