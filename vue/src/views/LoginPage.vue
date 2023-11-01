<!-- <script setup>
import axios from "axios"

  const callback = (response) => {
    console.log(response);
    verifyUser(response);
  }

  function verifyUser(jwt) {
    console.log(jwt);
    axios.post("/login", jwt)
    .then((response) => {
      console.log(response);
    });
  }
</script> -->

<template>
  <!-- <GoogleLogin :callback="callback" prompt auto-login/> -->
  <h1>login page</h1>
  <button v-on:click="get">google login</button>
</template>

<script>
  export default {
    name: "LoginPage",

    mounted() {
    },
    methods: {
        get() {
            this.axios.get("/login")
            .then((response) => {
                console.log("request sended",response);
            });
        },
    },
  }
</script>