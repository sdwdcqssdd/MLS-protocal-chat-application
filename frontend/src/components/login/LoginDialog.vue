<template>
  <el-dialog
      :title="succeed ? '' : 'Login'"
      v-model="dialogOpen"
      :show-close="dialogCloseable && !succeed"
      :before-close="handleClose"
      width="400px"
  >
    <template v-if="!succeed">
      <div class="form-body">
        <el-form
            ref="formRef"
            :model="form"
            :rules="formRules"
            label-width="auto"
        >
          <el-form-item prop="username" label="Username">
            <el-input v-model="form.username"/>
          </el-form-item>
          <el-form-item prop="password" label="Password">
            <el-input v-model="form.password" show-password/>
          </el-form-item>
        </el-form>
        <el-button type="primary" @click="handleSubmit">Login</el-button>
      </div>
      <!--      <div style="text-align: right">-->
      <!--        <el-button type="primary" link @click="router.push('/register')">Register</el-button>-->
      <!--      </div>-->
    </template>
    <el-result
        v-else
        icon="success"
        :title="`Welcome, ${ form.username }!`"
        :sub-title="`The page will refresh in ${ countdown } seconds.`"
    >
      <template #extra>
        <el-button type="primary" @click="doRefresh()">
          <el-icon>
            <Refresh/>
          </el-icon>
          Refresh Now
        </el-button>
      </template>
    </el-result>
  </el-dialog>
</template>

<script>

import {ref} from "vue";
import {router} from "@/router";
import {userLogin} from "@/scripts/user";
import {ElNotification} from "element-plus";
import {Refresh} from "@element-plus/icons-vue";

const form = {
  username: '',
  password: ''
}

let isSubmit = false
let instance = undefined

const passwordValidator = (rule, value, callback) => {
  if (isSubmit) {
    userLogin(form.username, form.password).then((result) => {
      if (result.data.ok) {
        localStorage.clear()
        for (let key in result.data.data) {
          localStorage.setItem(key, result.data.data[key])
        }
        localStorage.setItem('username', form.username)
        callback()
      } else {
        callback(new Error('Wrong username or password'))
      }
    }).catch(() => {
      ElNotification({
        title: 'Error',
        message: '',
        type: 'error',
      })
    })
  } else {
    callback()
  }
}

const formRules = {
  username: [
    {required: true, message: 'Please input the username', trigger: 'blur'},
  ],
  password: [
    {required: true, message: 'Please input the password', trigger: 'blur'},
    {validator: passwordValidator, trigger: 'blur'},
  ],
}

export default {
  name: 'LoginDialog',
  components: {Refresh},
  props: {
    closable: {
      type: Boolean
    }
  },
  computed: {
    dialogCloseable() {
      if (this.closable === undefined) {
        return true
      }
      return this.closable
    }
  },
  mounted() {
    instance = this
  },
  data() {
    return {
      dialogOpen: false,
      form: form,
      formRef: ref(),
      router: router,
      formRules: formRules,
      succeed: false,
      countdown: 3,
    }
  },
  methods: {
    handleClose(done) {
      if (this.dialogCloseable && !this.succeed) {
        done()
      }
    },
    handleSubmit() {
      isSubmit = true
      this.$refs['formRef'].validate((valid) => {
        isSubmit = false
        if (valid) {
          this.refreshPage()
        }
      })
    },
    doRefresh() {
      if (window.location.href.endsWith('/login')) {
        this.router.push('/')
      } else {
        this.router.go(0)
      }
    },
    refreshPage() {
      this.succeed = true
      const handler = () => {
        this.countdown -= 1
        if (this.countdown === 0) {
          this.doRefresh()
        } else {
          setTimeout(handler, 1000)
        }
      }
      setTimeout(handler, 1000)
    }
  },
  setup(props, ctx) {
    const openDialog = () => {
      if (instance !== undefined) {
        instance.dialogOpen = true
      }
    }
    ctx.expose({
      openDialog
    })
    return {
      openDialog,
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