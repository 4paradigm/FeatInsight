import './assets/main.css'

import axios from 'axios'
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/antd.css';
import { createI18n } from 'vue-i18n'
import { createPinia } from 'pinia';

import en from './locales/en.json'
import zh from './locales/zh.json'

import { useUserStore } from '@/stores/user';

// axios.defaults.baseURL = import.meta.env.VITE_API_ENDPOINT;
axios.defaults.baseURL = 'http://172.17.0.2:8888'

axios.interceptors.request.use(config => {
    const userStore = useUserStore();
    const uuid = userStore.uuid;
    if(uuid) {
        config.headers['UUID'] = uuid;
    }
    return config;
}, error => {
    return Promise.reject(error);
});

const i18n = createI18n({
legacy: false,
locale: 'zh',
fallbackLocale: 'en',
messages: {
    en,
    zh
}
})

const pinia = createPinia();

const app = createApp(App)
app.use(router)
app.use(pinia)
app.use(Antd)
app.use(i18n)

const userStore = useUserStore();
userStore.checkAuth();

app.mount('#app')
