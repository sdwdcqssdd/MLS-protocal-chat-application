<template>
  <el-dialog
      title="New chat"
      v-model="dialogOpen"
      :show-close="true"
      width="350px"
  >
    <h3 class="section-title">Find users</h3>
    <UserSearchForm
        :disabled-user-ids="[]"
        @add-user="handleAdd"
        ref="userSearchFormRef"
    />
  </el-dialog>
</template>

<script>
import UserSearchForm from "@/components/message/UserSearchForm.vue";
import {ElNotification} from "element-plus";
import {addNewChat} from "@/scripts/chat/chat";
import {ref} from "vue";

let instance = undefined

export default {
  name: 'NewChatDialog',
  components: {UserSearchForm},
  data() {
    return {
      dialogOpen: false,
      userSearchFormRef: ref(),
    }
  },
  mounted() {
    instance = this
  },
  methods: {
    handleAdd(user) {
      let memberIds = [parseInt(localStorage.getItem('user_id')), user.id]
      const callback = (ok, message) => {
        if (ok) {
          ElNotification({
            title: 'Success',
            message: message,
            type: 'success',
          })
          this.dialogOpen = false
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