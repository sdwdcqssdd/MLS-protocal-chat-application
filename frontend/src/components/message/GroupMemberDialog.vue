<template>
  <el-dialog
      title="New chat"
      v-model="dialogOpen"
      :show-close="true"
      width="720px"
  >
    <el-row :gutter="20">
      <el-col :span="12" style="border-right: 1px solid #d6d6d6; padding-right: 10px;">
        <h3 class="section-title">Find users</h3>
        <UserSearchForm
            :disabled-user-ids="disabledUserIds"
            @add-user="handleAdd"
            ref="userSearchFormRef"
        />
      </el-col>
      <el-col :span="12" style="padding-left: 10px;">
        <h3 class="section-title">Current Members</h3>
        <div style="flex-grow: 1;">
          <el-scrollbar height="300px" style="width: 100%;">
            <el-divider style="margin: 5px 0;"/>
            <div>
              <div style="display: flex; align-items: center; ">
                <el-avatar shape="square" :size="35" :src="getAvatarURL()"/>
                <span style="margin-left: 5px;">
                  {{ currentUsername }}
                </span>
                <div style="flex-grow: 1"/>
                <div>
                  <span>You</span>
                </div>
              </div>
              <el-divider style="margin: 5px 0;"/>
            </div>
            <div v-for="(user, idx) in selectedUsers">
              <div style="display: flex; align-items: center; ">
                <el-avatar shape="square" :size="35" :src="getAvatarURL(user.id)"/>
                <span style="margin-left: 5px;">
                  {{ user.name }}
                </span>
                <div style="flex-grow: 1"/>
                <div>
                  <el-button type="danger" size="small" :icon="Remove" @click="handleRemove(idx)"/>
                </div>
              </div>
              <el-divider style="margin: 5px 0;"/>
            </div>
          </el-scrollbar>
        </div>
        <small>Total: {{ selectedUsers.length + 1 }} members.</small>
      </el-col>
    </el-row>
    <template #footer>
      <div style="text-align: right;">
        <el-button type="primary" @click="handleConfirm" :disabled="selectedUsers.length < 2">Create Chat</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script>

import {Remove} from "@element-plus/icons-vue";
import {ElNotification} from "element-plus";
import {getAvatarURL} from "@/scripts/user";
import {addNewChat} from "@/scripts/chat/chat";
import UserSearchForm from "@/components/message/UserSearchForm.vue";
import {ref} from "vue";

let instance = null

export default {
  name: 'GroupMemberDialog',
  computed: {
    Remove() {
      return Remove
    },
    disabledUserIds() {
      let ids = []
      for (let idx in this.selectedUsers) {
        ids.push(this.selectedUsers[idx].id)
      }
      return ids
    }
  },
  components: {UserSearchForm},
  data() {
    return {
      dialogOpen: false,
      selectedUsers: [],
      currentUsername: localStorage.getItem('username'),
      userSearchFormRef: ref(),
    }
  },
  mounted() {
    instance = this
  },
  methods: {
    getAvatarURL,
    handleAdd(user) {
      this.selectedUsers.push(user)
    },
    handleRemove(idx) {
      this.selectedUsers.splice(idx, 1)
    },
    handleConfirm() {
      let memberIds = []
      for (let idx in this.selectedUsers) {
        memberIds.push(this.selectedUsers[idx].id)
      }
      const callback = (ok, message) => {
        if (ok) {
          ElNotification({
            title: 'Success',
            message: message,
            type: 'success',
          })
          this.dialogOpen = false
          this.selectedUsers = []
        } else {
          ElNotification({
            title: 'Error',
            message: message,
            type: 'error',
          })
        }
      }
      addNewChat(memberIds).then((result) => {
        callback(result.data.ok, result.data.message)
      })
    },
    clearSearch() {
      if (this.$refs['userSearchFormRef'] !== undefined) {
        this.$refs['userSearchFormRef'].clear()
      }
    }
  },
  setup(props, ctx) {
    const openDialog = () => {
      if (instance !== null) {
        instance.clearSearch()
        instance.dialogOpen = true
      }
    }
    ctx.expose({
      openDialog,
    })
    return {
      openDialog,
    }
  }
}
</script>

<style scoped>
.section-title {
  margin: 5px 0 5px 0;
}
</style>