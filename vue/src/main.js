import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import vuetify from './plugins/vuetify'
import { loadFonts } from './plugins/webfontloader'
import axios from "axios"

// 데이트피커
import VueDatePicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css'

loadFonts();

//백엔드 서버 포트 지정
axios.defaults.baseURL = "http://localhost:8000";

const app = createApp(App);
//registerPlugins(app)
app.config.globalProperties.axios = axios;
app.component('VueDatePicker', VueDatePicker);
app.use(vuetify).use(router).mount("#app");