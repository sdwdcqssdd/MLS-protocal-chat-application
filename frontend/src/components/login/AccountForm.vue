<template>
  <div class="form-body">
    <el-form
        :model="form"
        ref="formRef"
        :rules="formRule"
        label-width="auto"
    >
      <el-form-item prop="username" label="Username">
        <el-input v-model="form.username"/>
      </el-form-item>
      <el-form-item prop="password" label="Password">
        <el-input v-model="form.password" show-password/>
      </el-form-item>
      <el-form-item prop="checkPassword" label="Repeat Password">
        <el-input v-model="form.checkPassword" show-password/>
      </el-form-item>
    </el-form>
    <el-button type="primary" @click="handleRegister">Register</el-button>
  </div>
</template>

<script>

import {ref} from "vue";
import {checkUsernameAvailable, userRegister} from "@/scripts/user";
import {ElNotification} from "element-plus";

const form = {
  username: '',
  password: '',
  checkPassword: '',
}

const usernameValidator = (rule, value, callback) => {
  if (value.length < 5) {
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
  if (value.length < 8) {
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

const formRule = {
  username: [
    {required: true, message: 'Please input the username', trigger: 'blur'},
    {validator: usernameValidator, trigger: 'blur'},
  ],
  password: [
    {required: true, message: 'Please input the password', trigger: 'blur'},
    {validator: passwordValidator, trigger: 'blur'},
  ],
  checkPassword: [
    {required: true, message: 'Please repeat the password', trigger: 'blur'},
    {validator: checkPasswordValidator, trigger: 'blur'},
  ],
}

export default {
  name: 'AccountForm',
  data() {
    return {
      form: form,
      formRef: ref(),
      formRule: formRule
    }
  },
  methods: {
    handleRegister() {
      const errorCallback = (message) => {
        ElNotification({
          title: 'Error',
          message: message,
          type: 'error',
        })
      }
      this.$refs['formRef'].validate((valid) => {
        if (valid) {
          userRegister(form.username, form.password).then((result) => {
            if (result.data.ok) {
              localStorage.setItem('username', form.username)
              localStorage.setItem('user_id', result.data.data.id)
              localStorage.setItem('user_token', result.data.data.token)
              this.notifyRegistered()
            } else {
              errorCallback(result.data.message)
            }
          }).catch(() => {
            errorCallback('')
          })
        }
      })
    }
  },
  setup(props, ctx) {
    const notifyRegistered = () => {
      ctx.emit('registered')
    }
    return {
      notifyRegistered
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