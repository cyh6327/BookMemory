<template>
  <v-main>
    <h1>이메일로 문장 받기</h1>
    <form v-on:submit="sendEmail">
        <v-text-field
        label="이메일"
        v-model="email"
        :rules="rules"
        hide-details="auto"
      ></v-text-field>
      <v-btn-alt
          v-on:click="sendEmail"
          rel="noopener noreferrer"
          text="전송"
        />
    </form>
  </v-main>
</template>
  
<script>
  export default {
    name: "SendEmail",
    data() {
      return {
        email : "",
      };
    },
    methods: {
      sendEmail(e) {
        e.preventDefault();
        const email = this.email;
        const sentenceSortKey = localStorage.getItem('sentenceSortKey');
        this.axios.post("/email/send/"+email+"/"+sentenceSortKey)
        .then((response) => {
          console.log(response);
        });
      }
    },
  };
</script>

<style>
/* .v-main {
  max-width: 700px;
}
.v-container.d-flex {
  padding: 0;
  margin-bottom: 10px;
}
.v-input {
  margin-bottom: 10px;
}
.dp__main {
  margin-bottom: 20px;
} */
</style>