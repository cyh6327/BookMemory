<script setup>
import { decodeCredential } from 'vue3-google-login'
import { useRouter } from "vue-router";
import axios from "axios"
import { onMounted } from 'vue';
import {useStore} from 'vuex';

const router = useRouter();
const store = useStore();

onMounted(() => {
  console.log(store); // 스토어 확인
});

const callback = (response) => {
  // decodeCredential will retrive the JWT payload from the credential
  const userData = decodeCredential(response.credential)
  console.log("Handle the userData", userData)
  sendUserInfo(userData);
}

function sendUserInfo(userData) {
  axios.post("/login", userData)
  .then((response) => {
      store.commit('setIsLogined', true);
      // this.$store.commit('setIsLogined', true); // 전역 변수 변경
      //console.log(this.$store.state.isLogined);

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
  // .catch((error) => {
  //   console.log(error);
  //   alert("로그인에 실패하였습니다. 다른 방법으로 로그인 해주세요.");
  // });
}
</script>

<template>
  <GoogleLogin :callback="callback"/>
</template>