import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import vuetify from './plugins/vuetify'
import { loadFonts } from './plugins/webfontloader'
import axios from "axios"
//import { registerPlugins } from '@/plugins'

loadFonts();

//백엔드 서버 포트 지정
axios.defaults.baseURL = "http://localhost:8000";

const app = createApp(App);
//registerPlugins(app)
app.config.globalProperties.axios = axios;
app.use(vuetify).use(router).mount("#app");
