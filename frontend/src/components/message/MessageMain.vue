<script setup>

import MessageSidebar from "@/components/message/MessageSidebar.vue";
import MessageContent from "@/components/message/MessageContent.vue";
</script>

<template>
  <el-container style="height: calc(100vh - 60px);">
    <el-aside width="250px">
      <MessageSidebar
          :get-client="() => client"
      />
    </el-aside>
    <el-main style="padding: 0">
      <MessageContent
          :get-client="() => client"
          ref="contentRef"
      />
    </el-main>
  </el-container>
</template>

<script>
import {initChat} from "@/scripts/chat/chat";
import {ref} from "vue";

export default {
  name: 'MessageMain',
  data() {
    return {
      client: initChat(this.notifyNewMessage),
      contentRef: ref(),
    }
  },
  methods: {
    notifyNewMessage() {
      this.$refs['contentRef'].scrollToBottom()
    }
  }
}
</script>

<style scoped>

</style>