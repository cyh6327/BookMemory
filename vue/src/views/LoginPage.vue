<script setup>
import { decodeCredential } from 'vue3-google-login'
import axios from "axios"

// axios.interceptors.response.use(
//   (error) => {
//     if (error.response) {
//       if (error.response.status === 401) {
//         refreshToken();
//         return {
//           code: '401',
//           message: '401',
//         };
//       }
//     }
//     return Promise.reject(error);
//   }
// );

// function refreshToken() {
  
// }

const callback = (response) => {
  // decodeCredential will retrive the JWT payload from the credential
  const userData = decodeCredential(response.credential)
  console.log("Handle the userData", userData)
  sendUserInfo(userData);
}

function sendUserInfo(userData) {
  axios.post("/login", userData)
  .then((response) => {
    console.log("userInfo sended", response);
    this.$router.push({
      path: "/book",
    });
  })
}
</script>

<template>
  <GoogleLogin :callback="callback"/>
</template>