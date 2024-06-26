<script setup>

import UserBlock from "@/components/message/UserBlock.vue";
import {Search} from "@element-plus/icons-vue";
import {changeChat} from "@/scripts/chat/chat";
import {getAvatarURL} from "@/scripts/user";
import ChatBlock from "@/components/message/ChatBlock.vue";
import GroupMemberDialog from "@/components/message/GroupMemberDialog.vue";
import NewChatDialog from "@/components/message/NewChatDialog.vue";
</script>

<template>
  <el-container style="background: #eeeeee; height: 100%;">
    <el-header class="header-outer" style="padding-top: 20px">
      <UserBlock :name="username" :is-chat="false" :avatar="getAvatarURL()"/>
    </el-header>
    <div class="divider"/>
    <el-main style="padding: 10px 0 0 0; ">
      <div style="display: flex;">
        <div class="button-div">
          <el-button type="info" size="default" link @click="handleAddGroup">
            <el-icon>
              <Plus/>
            </el-icon>
            New Group
          </el-button>
        </div>
      </div>
      <div class="divider"/>
      <div style="margin: 10px 20px 10px 20px;">
        <el-input
            v-model="searchInput"
            placeholder="Search"
            :prefix-icon="Search"
            input-style="color: grey"
        />
      </div>
      <div style="height:calc(100% - 83px);">
        <el-scrollbar height="100%">
          <template v-for="(chat, idx) in chats">
            <div class="divider"/>
            <ChatBlock
                :activated="chat.id === client.activatedChatId"
                :chat-member-ids="chat.memberIds"
                :chat-name="chat.name"
                :newest-message="chat.newestMessage"
                :unread-count="chat.unreadCount"
                @click="handleChatClick(chat.id)"
            />
          </template>
        </el-scrollbar>
      </div>
    </el-main>
  </el-container>
  <NewChatDialog ref="newChatDialogRef"/>
  <GroupMemberDialog ref="groupMemberDialogRef"/>
</template>

<script>
import {changeChat, getChats} from "@/scripts/chat/chat";
import {ref} from "vue";

export default {
  name: 'MessageSidebar',
  props: {
    getClient: {
      type: Function,
      required: true
    }
  },
  computed: {
    client() {
      return this.getClient()
    },
    chats() {
      return getChats(this.client)
    }
  },
  data() {
    return {
      username: localStorage.getItem('username'),
      searchInput: undefined,
      groupMemberDialogRef: ref(),
      newChatDialogRef: ref(),
    }
  },
  methods: {
    handleChatClick(chatId) {
      if (chatId !== this.chats.activatedChatId) {
        changeChat(this.client, chatId)
      }
    },
    handleAddGroup() {
      this.$refs['groupMemberDialogRef'].openDialog()
    },
    handleAddChat() {
      this.$refs['newChatDialogRef'].openDialog()
    }
  }
}
</script>

<style scoped>
.header-outer {
  height: 80px;
}

.divider {
  display: block;
  height: 0;
  width: 100%;
  border-top: #d6d6d6 1px solid;
}

.button-div {
  width: 50%;
  display: flex;
  justify-content: center;
  margin-bottom: 10px;
}
</style>