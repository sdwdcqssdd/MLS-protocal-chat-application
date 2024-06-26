<template>
  <el-dialog
      v-model="dialogOpen"
      :show-close="false"
      :before-close="handleClose"
      width="500px"
  >
    <template #header>
      <el-steps
          style="max-width: 500px"
          simple
          :active="stage"
      >
        <el-step title="Account" :icon="Edit"/>
        <el-step title="Avatar" :icon="User"/>
        <el-step title="Done" :icon="CircleCheck"/>
      </el-steps>
    </template>
    <div>
      <AccountForm
          v-if="stage === 0"
          @registered="stage = 1"
      />
      <AvatarForm
          v-if="stage === 1" @uploaded="handleAvatarUploaded"
      />
      <template v-else-if="stage === 2">
        <el-result
            icon="success"
            :title="`Welcome, ${ username }!`"
            :sub-title="`The page will refresh in ${ countdown } seconds.`"
        >
          <template #extra>
            <el-button type="primary" @click="router.go(0)">
              <el-icon>
                <Refresh/>
              </el-icon>
              Refresh Now
            </el-button>
          </template>
        </el-result>
      </template>
    </div>
    <template #footer v-if="stage !== 2">
      <el-divider style="margin: 0 0 15px 0;"/>
      <el-button v-if="stage === 0" @click="dialogOpen = false" type="default">Cancel</el-button>
      <el-button v-else-if="stage === 1 && !avatarUploaded" @click="handleRegisterFinished" type="default">Skip
      </el-button>
      <el-button v-else-if="stage === 1 && avatarUploaded" @click="handleRegisterFinished" type="primary">Next
      </el-button>
    </template>
  </el-dialog>
</template>

<script>

import {CircleCheck, Edit, Refresh, User} from "@element-plus/icons-vue";
import AccountForm from "@/components/login/AccountForm.vue";
import {router} from "@/router";
import AvatarForm from "@/components/login/AvatarForm.vue";

let instance = undefined

export default {
  name: 'RegisterDialog',
  components: {AvatarForm, Refresh, AccountForm},
  props: {
    closable: {
      type: Boolean
    }
  },
  computed: {
    CircleCheck() {
      return CircleCheck
    },
    Edit() {
      return Edit
    },
    User() {
      return User
    },
    dialogCloseable() {
      if (this.closable === undefined) {
        return true
      }
      return this.closable
    },
    username() {
      return localStorage.getItem('username')
    }
  },
  mounted() {
    instance = this
  },
  data() {
    return {
      dialogOpen: false,
      stage: 0,
      countdown: 3,
      router: router,
      avatarUploaded: false,
    }
  },
  methods: {
    handleClose(done) {
      if (this.dialogCloseable && this.stage === 0) {
        done()
      }
    },
    handleRegisterFinished() {
      this.stage = 2
      const handler = () => {
        this.countdown -= 1
        if (this.countdown === 0) {
          this.router.go(0)
        }
        setTimeout(handler, 1000)
      }
      setTimeout(handler, 1000)
    },
    handleAvatarUploaded() {
      this.avatarUploaded = true
    }
  },
  setup(props, ctx) {
    const openDialog = () => {
      if (instance !== undefined) {
        instance.stage = 0
        instance.dialogOpen = true
        instance.avatarUploaded = false
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

</style>