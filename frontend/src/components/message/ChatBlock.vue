<template>
  <div class="block-container" :style="(activated? 'background: #d6d6d6;' : '')">
    <div class="img-div">
      <template v-for="id in displayedAvatarIds">
        <el-image :style="avatarStyle" :src="getAvatarURL(id)" fit="cover"/>
      </template>
    </div>
    <div class="chat-div">
      <div class="chat-line">
        <el-text class="chat-name" truncated>{{ chatName }}</el-text>
        <small class="chat-time"> {{ displayedTime }} </small>
      </div>
      <div class="chat-line">
        <small class="chat-content"> {{ displayedContent }} </small>
        <div class="unread-tag" v-if="unreadCount !== 0"><small class="unread-count">{{ unreadCount }}</small></div>
      </div>
    </div>
  </div>
</template>

<script>
import {getAvatarURL} from "@/scripts/user";

export default {
  name: 'ChatBlock',
  methods: {getAvatarURL},
  props: {
    chatName: {
      type: String,
      required: true
    },
    chatMemberIds: {
      type: Array,
      required: true,
    },
    newestMessage: {
      type: Object
    },
    unreadCount: {
      type: Number,
      required: true
    },
    activated: {
      type: Boolean,
      required: true
    }
  },
  computed: {
    displayedContent() {
      if (this.newestMessage === undefined) {
        return ''
      } else if (this.newestMessage.content.length > 8) {
        return this.newestMessage.content.slice(0, 8) + '...'
      } else {
        return this.newestMessage.content
      }
    },
    displayedTime() {
      if (this.newestMessage === undefined) {
        return ''
      }
      const add0 = (x) => {
        return (x < 10 ? '0' : '') + x
      }
      const today = new Date()
      const yesterday = new Date()
      yesterday.setDate(yesterday.getDate() - 1)
      const thatDay = new Date(this.newestMessage.time)
      if (today.toDateString() === thatDay.toDateString()) {
        return `${add0(thatDay.getHours())}:${add0(thatDay.getMinutes())}`
      } else if (yesterday.toDateString() === thatDay.toDateString()) {
        return 'yesterday'
      } else {
        return `${thatDay.getMonth()}-${thatDay.getDate()}`
      }
    },
    displayedAvatarIds() {
      if (this.chatMemberIds.length === 2) {
        return [this.chatMemberIds[0] + this.chatMemberIds[1] - parseInt(localStorage.getItem("user_id"))]
      } else {
        return this.chatMemberIds.sort().slice(0, 9)
      }
    },
    avatarStyle() {
      let size
      if (this.displayedAvatarIds.length > 4) {
        size = 12
      } else if (this.displayedAvatarIds.length > 1) {
        size = 19
      } else {
        return 'width: 40px; height: 40px; border-radius: var(--el-border-radius-base);'
      }
      return `width: ${size}px; height: ${size}px; margin: .5px;`
    }
  }
}
</script>

<style scoped>
.block-container {
  padding: 10px 20px 10px 20px;
  width: 210px;
  height: 40px;
}

.img-div {
  float: left;
  width: 40px;
  height: 40px;
  display: flex;
  flex-direction: row;
  flex-wrap: wrap-reverse;
  align-content: center;
  justify-content: center;
  background-color: #fff;
  border-radius: var(--el-border-radius-base);
}

.chat-div {
  float: right;
  width: 160px;
  height: 100%;
}

.chat-name {
  color: #000;
  display: block;
}

.chat-content {
  color: #858585;
}

.chat-line {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-time {
  color: #858585;
  white-space: nowrap;
}

.unread-tag {
  height: 16px;
  min-width: 16px;
  border-radius: 8px;
  background: #ff0000;
  display: table;
}

.unread-count {
  color: #fff;
  display: table-cell;
  vertical-align: middle;
  text-align: center;
  font-size: 12px;
  padding: 0 2px 0 2px;
}
</style>