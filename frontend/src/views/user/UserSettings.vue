<template>
  <Header active-index="User"/>
  <div class="content-container">
    <el-row>
      <el-col :span="16" :offset="4">
        <h3>Account Setting</h3>
        <div style="max-width: 740px; text-align: center; margin: auto;">
          <div style="display: flex; justify-content: left;">
            <div class="avatar-container">
              <el-form label-width="auto">
                <el-form-item label="Avatar">
                  <AvatarUploader :current-avatar="getAvatarURL()"/>
                </el-form-item>
              </el-form>
              <p><small style="color: #808080;">*The avatar must be PNG format, and its size must not exceed
                256KB.</small></p>
            </div>
            <div class="form-container">
              <el-form label-width="auto" :model="form" :rules="formRule" ref="formRef">
                <el-form-item label="Username" prop="username">
                  <el-input placeholder="No change" v-model="form.username"/>
                </el-form-item>
                <el-form-item label="New Password" prop="password">
                  <el-input show-password placeholder="No change" v-model="form.password"/>
                </el-form-item>
                <el-form-item label="Repeat Password" prop="checkPassword">
                  <el-input show-password placeholder="No change" v-model="form.checkPassword"/>
                </el-form-item>
              </el-form>
              <el-button type="primary" :disabled="!formFilled" @click="handleSubmit">Submit</el-button>
              <p><small style="color: #808080;">*Please leave the fields you don't want to change empty.</small></p>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import Header from "@/components/Header.vue";
import {ref} from "vue";
import {changePassword, changeUsername, checkUsernameAvailable, getAvatarURL} from "@/scripts/user";
import {ElNotification} from "element-plus";
import {Refresh} from "@element-plus/icons-vue";
import AvatarUploader from "@/components/user/AvatarUploader.vue";

const usernameValidator = (rule, value, callback) => {
  if (value === '') {
    callback()
  } else if (value === localStorage.getItem('username')) {
    callback(new Error('Same as your current username'))
  } else if (value.length < 5) {
    callback(new Error('Username too short'))
  } else if (value.length > 18) {
    callback(new Error('Username too long'))
  } else if (value.match(/^[0-9a-zA-Z_]+$/) === null) {
    callback(new Error('Illegal character'))
  } else {
    checkUsernameAvailable(value).then((result) => {
      if (result.data.ok) {
        callback()
      } else {
        callback(new Error('Username not available'))
      }
    })
  }
}

const passwordValidator = (rule, value, callback) => {
  if (value === '') {
    callback()
  } else if (value.length < 8) {
    callback(new Error('Password too short'))
  } else if (value.length > 18) {
    callback(new Error('Password too long'))
  } else if (value.match(/^[0-9a-zA-Z_]+$/) === null) {
    callback(new Error('Illegal character'))
  } else {
    callback()
  }
}

const checkPasswordValidator = (rule, value, callback) => {
  if (form.password !== form.checkPassword) {
    callback(new Error('Password inconsistent'))
  } else {
    callback()
  }
}

const form = {
  username: '',
  password: '',
  checkPassword: '',
}

const formRule = {
  username: [
    {validator: usernameValidator, trigger: 'blur'},
  ],
  password: [
    {validator: passwordValidator, trigger: 'blur'},
  ],
  checkPassword: [
    {validator: checkPasswordValidator, trigger: 'blur'},
  ],
}

export default {
  name: 'UserSettings',
  components: {
    AvatarUploader,
    Refresh,
    Header
  },
  data() {
    return {
      form: form,
      formRule: formRule,
      formRef: ref(),
    }
  },
  computed: {
    formFilled() {
      return this.form.password !== '' || this.form.checkPassword !== '' || this.form.username !== ''
    }
  },
  methods: {
    getAvatarURL,
    handleSubmit() {
      const finish = (ok, message) => {
        if (!ok) {
          ElNotification({
            title: 'Error',
            message: message,
            type: 'error',
          })
        } else {
          ElNotification({
            title: 'Success',
            message: message,
            type: 'success',
          })
          this.$refs['formRef'].resetFields()
        }
      }
      this.$refs['formRef'].validate((valid) => {
        if (!valid) {
          return
        }
        let count = 0
        const callback = (ok, message) => {
          if (ok) {
            count -= 1
            if (count === 0) {
              finish(true, message)
            }
          } else {
            finish(false, message)
          }
        }
        if (form.username !== '') {
          count += 1
          changeUsername(form.username).then((result) => {
            if (result.data.ok) {
              localStorage.setItem('username', form.username)
            }
            callback(result.data.ok, result.data.message)
          }).catch(() => {
            callback(false, "")
          })
        }
        if (form.password !== '') {
          count += 1
          changePassword(form.password).then((result) => {
            callback(result.data.ok, result.data.message)
          }).catch(() => {
            callback(false, "")
          })
        }
      })
    }
  }
}

</script>

<style scoped>
.avatar-container {
  max-width: 250px;
  padding: 20px 20px 20px 0;
  border-right: #d0d0d0 1px solid;
}

.form-container {
  max-width: 450px;
  padding: 20px 0 20px 20px;
}
</style>