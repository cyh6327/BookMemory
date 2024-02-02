<script setup>
import { decodeCredential } from 'vue3-google-login'
import { useRouter } from "vue-router";
import axios from "axios"

const router = useRouter();

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
    console.log("responed data list",response.data);
    
    const sentenceSortKey = localStorage.getItem('sentenceSortKey');
    if(!sentenceSortKey) {
      const createdSortKey = response.data.sentenceSortKey;
      if(createdSortKey) localStorage.setItem('sentenceSortKey', response.data.sentenceSortKey);
    }
    
    // setup 함수는 Vue 인스턴스가 생성되기 전에 호출되므로 router 인스턴스를 import해서 사용해야 한다.
    router.push({
      path: "/book",
    });
  })
}
</script>

<template>
  <GoogleLogin :callback="callback"/>
</template>