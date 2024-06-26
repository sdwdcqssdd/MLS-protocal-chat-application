<template>
  <div>
    <el-menu
        :default-active="activeIndex"
        class="header"
        mode="horizontal"
        :ellipsis="false"
    >
      <template v-for="(page, index) in pages">
        <el-menu-item
            v-if="page.children === undefined"
            :index="index.toString()"
            @click="handleClick(page.path)"
        >
          {{ page.name }}
        </el-menu-item>
        <el-sub-menu v-else :index="index.toString()">
          <template #title>{{ page.name }}</template>
          <el-menu-item
              v-for="(subPage, subIndex) in page.children"
              :index="index.toString() + '-' + subIndex.toString()"
              @click="handleClick(subPage.path)"
          >
            {{ subPage.name }}
          </el-menu-item>
        </el-sub-menu>
      </template>
      <div class="menu-spacer"/>
      <el-menu-item v-if="username === null" index="">
        <el-button type="primary" @click="handleLogin">Login</el-button>
        <el-button @click="handleRegister">Register</el-button>
      </el-menu-item>
      <el-menu-item v-else index="User">
        <el-dropdown>
          <div style="cursor: pointer; outline: none; display: flex; align-items: center;">
            <el-avatar :size="30" :src="getAvatarURL()" shape="square"/>
            <span style="margin-left: 5px;">{{ username }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="router.push('/user/settings')">Settings</el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">Logout</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-menu-item>
    </el-menu>
  </div>
  <LoginDialog
      :closable="true"
      ref="loginDialogRef"
  />
  <RegisterDialog
      :closable="true"
      ref="registerDialogRef"
  />
</template>

<script>
import {router} from "@/router";
import {User} from "@element-plus/icons-vue";
import LoginDialog from "@/components/login/LoginDialog.vue";
import {ref} from "vue";
import RegisterDialog from "@/components/login/ResgisterDialog.vue";
import {getAvatarURL} from "@/scripts/user";

export default {
  name: 'Header',
  components: {RegisterDialog, LoginDialog, User},
  data() {
    return {
      pages: pages,
      router: router,
      username: localStorage.getItem('username'),
      loginDialogRef: ref(),
      registerDialogRef: ref(),
    }
  },
  props: {
    activeIndex: {
      type: String,
      required: false
    }
  },
  methods: {
    getAvatarURL,
    handleClick(path) {
      router.push(path)
    },
    handleLogin() {
      this.$refs['loginDialogRef'].openDialog()
    },
    handleRegister() {
      this.$refs['registerDialogRef'].openDialog()
    },
    handleLogout() {
      localStorage.clear()
      this.router.go(0)
    }
  }
}

const pages = [
  {
    name: 'Home Page',
    path: '/'
  },
  {
    name: 'Chat Page',
    path: '/message'
  },
]

</script>

<style scoped>
.header {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 16;
  width: 100%;
}

.menu-spacer {
  flex-grow: 1;
}
</style>