import {createApp} from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import axios from 'axios'
import VueAxios from 'vue-axios'

import App from './App.vue'
import {router} from './router'
import {apiBaseAddress} from "@/scripts/api";

const app = createApp(App);

app.config.globalProperties.$baseUrl = apiBaseAddress

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}
app.use(router)
app.use(ElementPlus)
app.use(VueAxios, axios)
app.mount('#app')
