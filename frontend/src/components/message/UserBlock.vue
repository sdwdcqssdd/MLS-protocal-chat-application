<script setup>

</script>

<template>
  <div class="block-container">
    <div class="img-div">
      <el-avatar shape="square" size="" :src="avatar"/>
    </div>
    <div class="username-div">
      <span class="username">{{ name }}</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'UserBlock',
  props: {
    name: {
      type: String,
      required: true,
    },
    avatar: {
      type: String,
      required: true,
    }
  }
}
</script>

<style scoped>
.block-container {
  width: 210px;
  height: 40px;
}

.img-div {
  float: left;
  width: 40px;
  height: 40px;
}

.username-div {
  display: table;
  float: right;
  width: 160px;
  height: 100%;
}

.username {
  display: table-cell;
  vertical-align: middle;
  color: #000;
}
</style>