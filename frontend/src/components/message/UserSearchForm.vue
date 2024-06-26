<template>
  <div>
    <div style="display: flex; flex-direction: row; margin-top: 10px;">
      <el-form :model="form" ref="formRef" :rules="formRules" style="width: 100%;">
        <el-form-item prop="pattern">
          <el-input v-model="form.pattern" placeholder="Find a user">
            <template #append>
              <el-button :icon="Search" :disabled="form.pattern === ''" @click="handleSearch"/>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
    </div>
    <div>
      <el-scrollbar height="245px">
        <el-divider style="margin: 5px 0;"/>
        <div v-for="user in availableUsers">
          <div style="display: flex; align-items: center; ">
            <el-avatar shape="square" :size="35" :src="getAvatarURL(user.id)"/>
            <span style="margin-left: 5px;">
              {{ user.name.slice(0, user.name.indexOf(currentPattern)) }}
            </span>
            <span style="color:#409eff">{{ currentPattern }}</span>
            <span>
              {{ user.name.slice(user.name.indexOf(currentPattern) + currentPattern.length) }}
            </span>
            <div style="flex-grow: 1"/>
            <div>
              <el-button
                  type="primary" size="small" :icon="Plus"
                  @click="handleClick(user)" :disabled="userDisabled(user)"
              />
            </div>
          </div>
          <el-divider style="margin: 5px 0;"/>
        </div>
      </el-scrollbar>
    </div>
    <small>*At most 10 results are displayed.</small>
  </div>
</template>

<script>

import {getAvatarURL, searchUser} from "@/scripts/user";
import {ref} from "vue";
import {ElNotification} from "element-plus";
import {Plus, Search} from "@element-plus/icons-vue";

const patternValidator = (rule, value, callback) => {
  if (value === '') {
    callback()
  } else if (value.length > 18) {
    callback(new Error('Pattern too long'))
  } else if (!value.match(/^[0-9a-zA-Z_]+$/)) {
    callback(new Error('Illegal Character'))
  } else {
    callback()
  }
}

const form = {
  pattern: '',
}

const formRules = {
  pattern: [
    {validator: patternValidator, trigger: 'blur'}
  ]
}

let instance = undefined

export default {
  computed: {
    Plus() {
      return Plus
    },
    Search() {
      return Search
    }
  },
  components: {
    Plus,
  },
  mounted() {
    instance = this
  },
  data() {
    return {
      form: form,
      formRef: ref(),
      formRules: formRules,
      availableUsers: [],
      currentPattern: '',
    }
  },
  props: {
    disabledUserIds: {
      type: Array,
      required: true,
    }
  },
  methods: {
    getAvatarURL,
    handleSearch() {
      this.currentPattern = form.pattern.slice()
      const errorCallback = (message) => {
        ElNotification({
          title: 'Error',
          message: message,
          type: 'error',
        })
      }
      this.$refs['formRef'].validate((valid) => {
        if (valid) {
          searchUser(form.pattern).then((result) => {
            if (!result.data.ok) {
              errorCallback(result.data.message)
              return
            }
            this.availableUsers = result.data.data
          })
        }
      })
    },
    userDisabled(user) {
      return this.disabledUserIds.find((value) => {
        return user.id === value
      }) !== undefined
    }
  },
  setup(props, ctx) {
    const handleClick = (user) => {
      ctx.emit('addUser', user)
    }
    const clear = () => {
      if (instance !== undefined) {
        instance.form.pattern = ''
        instance.availableUsers = []
      }
    }
    ctx.expose({
      clear,
    })
    return {
      handleClick,
      clear
    }
  }
}

</script>

<style scoped>
.section-title {
  margin: 5px 0 5px 0;
}
</style>
