<template>
  <el-container style="background: #f5f5f5; height: 100%; padding: 20px">
    <el-header height="20px">
      <div class="chat-name-div">
        {{ chat === undefined ? '' : chat.name }}
      </div>
    </el-header>
    <div class="divider" style="margin: 20px 0 20px 0"/>
    <template v-if="activated">
      <el-main height="calc(100% - 100px)" style="padding: 0">
        <el-scrollbar height="100%" style="padding: 0 10px 0 10px;" ref="scrollbarRef">
          <div ref="scrollbarDivRef">
            <div v-for="(message, idx) in messages" style="margin-bottom: 15px;">
              <div
                  v-if="idx === 0 || messages[idx - 1].time <= messages[idx].time - 600 * 1000"
                  style="text-align: center; margin-top: 15px;"
              >
                <small class="chat-time">
                  {{ getTimeString(new Date(message.time)) }}
                </small>
              </div>
              <div style="display: flex; flex-direction: row">
                <el-avatar
                    v-if="message.senderId !== userId"
                    class="avatar-div-others"
                    shape="square" size=""
                    :src="getAvatarURL(message.senderId)"
                />
                <div v-else style="flex-grow: 1"/>

                <div style="display: flex; flex-direction: column;">
                  <template v-if="message.senderId !== userId">
                    <div>
                      <small class="message-sender">{{ message.senderName }}</small>
                    </div>
                    <div style="margin-right: auto;">
                      <el-card shadow="never" body-style="padding: 8px;" class="message-other">
                        <span>{{ message.content }}</span>
                      </el-card>
                    </div>
                  </template>
                  <template v-else>
                    <div style="text-align: right;">
                      <small class="message-sender">{{ message.senderName }}</small>
                    </div>
                    <div style="margin-left: auto;">
                      <el-card shadow="never" body-style="padding: 8px;" class="message-mine">
                        <span>{{ message.content }}</span>
                      </el-card>
                    </div>
                  </template>
                </div>

                <el-avatar
                    v-if="message.senderId === userId"
                    class="avatar-div-mine"
                    shape="square" size=""
                    :src="getAvatarURL(message.senderId)"/>
              </div>
            </div>
          </div>
        </el-scrollbar>
      </el-main>
      <div class="divider" style="margin: 20px 0 20px 0"/>
      <el-footer height="100px" style="display: flex; flex-direction: column">
        <div class="input-div">
          <el-input v-model="messageInput" type="textarea" resize="none" input-style="height: 100%;"
                    style="height: 100%;"/>
        </div>
        <div class="button-div">
          <el-button type="primary" size="small" v-if="messageInput.length === 0" disabled>
            <el-icon>
              <Promotion/>
            </el-icon>
            Send
          </el-button>
          <el-button type="primary" size="small" v-else @click="handleSendMessage()">
            <el-icon>
              <Promotion/>
            </el-icon>
            Send
          </el-button>
        </div>
      </el-footer>
    </template>
    <el-main v-else style="text-align: center; display: table; ">
      <p style="display: table-cell; vertical-align: middle; color: #989898;">No Data</p>
    </el-main>
  </el-container>
</template>

<script>
import {Promotion} from "@element-plus/icons-vue";
import {getActivatedChat, getMessages, sendMessage} from "@/scripts/chat/chat";
import {nextTick, ref} from "vue";
import {getAvatarURL} from "@/scripts/user";

let instance = undefined

const scrollToBottom = () => {
  if (instance !== undefined) {
    instance.scrollToBottom()
  }
}

export default {
  name: 'MessageContent',
  props: {
    getClient: {
      type: Function,
      required: true,
    }
  },
  data() {
    return {
      messageInput: '',
      client: this.getClient(),
      userId: parseInt(localStorage.getItem('user_id')),
      username: localStorage.getItem('username'),
      scrollbarRef: ref(),
      scrollbarDivRef: ref(),
    }
  },
  mounted() {
    instance = this
  },
  computed: {
    activated() {
      return this.chat !== undefined
    },
    messages() {
      return getMessages(this.client)
    },
    chat() {
      return getActivatedChat(this.client)
    }
  },
  methods: {
    getAvatarURL,
    getTimeString(date) {
      const dateString = date.toDateString()
      let yesterday = new Date()
      yesterday.setDate(yesterday.getDate() - 1)
      const add0 = (x) => {
        return (x < 10 ? '0' : '') + x
      }
      if (dateString !== new Date().toDateString()) {
        if (dateString === yesterday.toDateString()) {
          return `yesterday ${add0(date.getHours())}:${add0(date.getMinutes())}`
        }
        return `${add0(date.getMonth())}-${add0(date.getDate())} ${add0(date.getHours())}:${add0(date.getMinutes())}`
      }
      return `${add0(date.getHours())}:${add0(date.getMinutes())}`
    },
    handleSendMessage() {
      sendMessage(this.client, this.messageInput)
      this.messageInput = ''
    },
    scrollToBottom() {
      nextTick(() => {
        this.$refs['scrollbarRef'].setScrollTop(Math.max(0,
            this.$refs['scrollbarDivRef'].clientHeight - this.$refs['scrollbarRef'].wrapRef.clientHeight + 20
        ))
      })
    }
  },
  setup(props, ctx) {
    ctx.expose({
      scrollToBottom
    })
  }
}
</script>

<style scoped>
.divider {
  display: block;
  height: 0;
  width: 100%;
  border-top: #e0e0e0 1px solid;
}

.chat-name-div {
  text-align: center;
}

.input-div {
  flex-grow: 1;
}

.button-div {
  margin-top: 5px;
  display: flex;
  flex-direction: row-reverse;
}

.message-other {
  color: #000;
  background: #fff;
  border-color: #fff;
}

.message-mine {
  color: #fff;
  background: #409eff;
  border-color: #409eff;
}

.avatar-div-mine {
  height: 40px;
  margin: 5px 0 0 10px;
}

.avatar-div-others {
  height: 40px;
  margin: 5px 10px 0 0;
}

.message-sender {
  color: #989898;
}

.chat-time {
  color: #989898;
}
</style>