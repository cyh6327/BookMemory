import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import vuetify from './plugins/vuetify'
import { loadFonts } from './plugins/webfontloader'
import axios from "axios"
import { createStore } from 'vuex';

// 데이트피커
import VueDatePicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css'
import vue3GoogleLogin from 'vue3-google-login'

const store = createStore({
  state: {
    isLogined: false,
  },
  mutations: {
    setIsLogined(state, value) {
      state.isLogined = value;
    },
  },
});

loadFonts();

//백엔드 서버 포트 지정
axios.defaults.baseURL = "http://localhost:8000";
// CORS 정책 허용
axios.defaults.withCredentials = true;

// axios.interceptors.request.use(
//   (config) => {
//     console.log(document.cookie)
//     const accessToken = document.cookie
//     .split("; ")
//     .find((row) => row.startsWith("accessToken="))
//     ?.split("=")[1];

//     console.log("accessToken.....",accessToken);

//     if (!accessToken) {
//       router.push({
//         path: "/book",
//       });
//       // window.location.href = '/login';
//       return config;
//     }

//     config.headers['Content-Type'] = 'application/json';
//     config.headers['Authorization'] = `Bearer ${accessToken}`;
//     config.headers['ngrok-skip-browser-warning'] = '69420';
//     // config.headers['Access-Control-Allow-Origin'] = "*";

//     return config;
//   },
//   (error) => {
//     console.log(error);
//     return Promise.reject(error);
//   },
// )

const app = createApp(App);
//registerPlugins(app)
app.config.globalProperties.axios = axios;
app.component('VueDatePicker', VueDatePicker);
app.use(vuetify).use(router).use(store).use(vue3GoogleLogin, {
    clientId: '258045023864-7m0goa8e9hpbhhl3kqgvh04ief6gajqi.apps.googleusercontent.com'
  }).mount("#app");