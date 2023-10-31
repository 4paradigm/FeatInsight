import './assets/main.css'

import axios from 'axios'
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/antd.css';
import { createI18n } from 'vue-i18n'

import en from './locales/en.json'
import zh from './locales/zh.json'

axios.defaults.baseURL = import.meta.env.VITE_API_ENDPOINT;

const i18n = createI18n({
legacy: false,
locale: 'zh',
fallbackLocale: 'en',
messages: {
    en,
    zh
}
})
  
const app = createApp(App)
app.use(router)
app.use(Antd)
app.use(i18n)
app.mount('#app')
