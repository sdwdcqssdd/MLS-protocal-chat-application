import {createRouter, createWebHistory} from 'vue-router'
import IndexPage from "@/views/Index.vue"
import MessageIndex from "@/views/message/MessageIndex.vue";
import UserSettings from "@/views/user/UserSettings.vue";
import LoginPage from "@/views/user/LoginPage.vue";

const routes = [
    {path: '/', component: IndexPage},

    {path: '/login', component: LoginPage},

    {path: '/message', component: MessageIndex},

    {path: '/user/settings', component: UserSettings},

]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export {
    router
}