<script setup>

import Header from "@/components/Header.vue";
import MessageMain from "@/components/message/MessageMain.vue";
import {onMounted} from "vue";
import {ensureLogin} from "@/scripts/user";

onMounted(ensureLogin)
</script>

<template>
  <Header/>
  <div style="margin-top: 60px;">
    <el-row>
      <MessageMain/>
    </el-row>
  </div>
</template>

<style scoped>

</style>