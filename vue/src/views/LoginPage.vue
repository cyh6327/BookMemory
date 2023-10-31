<script setup>
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
</script>

<template>
  <GoogleLogin :callback="callback" prompt auto-login/>
</template>
