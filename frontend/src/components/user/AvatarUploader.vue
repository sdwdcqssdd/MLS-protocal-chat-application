<template>
  <el-upload class="avatar-uploader" action="#"
             :show-file-list="false" ref="avatarRef"
             :on-change="beforeUpload" :auto-upload="false"
  >
    <el-icon v-if="avatarURL === undefined" class="avatar-uploader-icon">
      <Plus/>
    </el-icon>
    <div v-else :style="avatarStyle" class="avatar">
      <div class="avatar-update-div">
        <el-icon class="avatar-uploader-icon">
          <Refresh/>
        </el-icon>
      </div>
    </div>
  </el-upload>
</template>

<script>
import {ElNotification} from "element-plus";
import {changeAvatar, getAvatarURL} from "@/scripts/user";

export default {
  name: 'AvatarUploader',
  props: {
    currentAvatar: {
      type: String
    }
  },
  data() {
    return {
      avatarURL: this.currentAvatar,
    }
  },
  computed: {
    avatarStyle() {
      return `background-image: url("${this.avatarURL}"); background-size: cover;`
    }
  },
  methods: {
    async beforeUpload(file) {
      const callback = (ok, message) => {
        if (ok) {
          ElNotification({
            title: 'Success',
            message: message,
            type: 'success',
          })
        } else {
          ElNotification({
            title: 'Error',
            message: message,
            type: 'error',
          })
        }
      }
      if (file.raw.type !== 'image/png') {
        callback(false, 'Avatar must be PNG format!')
        return false
      }
      if (file.raw.size / 1024 > 256) {
        callback(false, 'Avatar size can not exceed 256KB!')
        return false
      }
      const result = await changeAvatar(file.raw)
      if (result.data.ok) {
        callback(true, result.data.message)
        this.avatarURL = getAvatarURL() + "&r=" + Math.random().toString()
        this.notifyUploaded()
        return true
      } else {
        callback(false, result.data.message)
        return false
      }
    }
  },
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

<style>
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}

.avatar-update-div {
  opacity: 0;
  transition: opacity var(--el-transition-duration-fast);
}

.avatar-update-div:hover {
  opacity: 1;
  background: rgba(255, 255, 255, .5);
}
</style>