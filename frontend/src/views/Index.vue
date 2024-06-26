<script setup>
import Header from '@/components/Header.vue'
</script>

<template>
  <div>
    <Header active-index="0"/>
    <div class="welcome-container">
      <h1>Welcome to the MLS Protocol Based Group Chat System</h1>
      <p>This system leverages the Messaging Layer Security (MLS) protocol to provide secure group communication. Whether you're chatting with friends, collaborating with colleagues, or participating in large group discussions, our system ensures your messages remain private and secure.</p>
    </div>
  </div>
</template>

<style>
body {
  margin: 0;
}

.welcome-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: calc(100vh - 80px);
  text-align: center;
  padding: 0 20px;
  box-sizing: border-box;
}

h1 {
  font-size: 2.5em;
  margin-bottom: 0.5em;
}

p {
  font-size: 1.2em;
  max-width: 800px;
  margin: 0 auto;
}
</style>
