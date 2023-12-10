<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from "vue-router";
import axios from "axios"

let bookId = null;
const title = ref('');
const author = ref('');
const sentences = ref([]);

onMounted(() => {
  const route = useRoute();
  bookId = route.params.bookId;

  axios.get("/book/detail/"+bookId)
  .then((res) => {
    console.log(res.data);
    title.value = res.data.bookInfo.title;
    author.value = res.data.bookInfo.author;

    const bookSentences = res.data.bookSentences;
    for(let sentence of bookSentences) {
      console.log(sentence.sentenceText);
      sentence.sentenceText = sentence.sentenceText.replaceAll("<br>", "\r\n");
      console.log("replaced:",sentence.sentenceText);
      sentences.value.push(sentence.sentenceText);
      console.log(sentences.value);
    }
  })
});

function insertSentenceFromFile() {
  axios.post("/book/sentence/file/"+bookId+"/"+title.value)
  .then((res) => {
      console.log(res.data);
  });
}
</script>

<template>
  <v-main id="book_detail">
      <h2 class="mb-5">{{ title }}</h2>
      <h4 style="color:gray"> {{ author }}</h4>
    <div class="btn_box d-flex justify-end">
      <v-btn-alt
        @click="insertSentenceFromFile"
        rel="noopener noreferrer"
        text="파일로 문장 추가"
      />
    </div>
    <v-container class="rounded-shaped">
      <!-- <ul lass="my-10">
        <li class="pa-10">dsfdsfsdfdsfsdsdf</li>
      </ul> -->
    <!-- <v-container style="padding: 0;"> -->
      <ul class="py-10" v-for="sentence in sentences" :key="sentence">
        <li class="pa-10" style="text-align:left;">{{ sentence }}</li>
      </ul>
      <!-- <ul>
        <li>{{ sentences }}</li>
      </ul> -->
    </v-container>
  </v-main>

</template>

<style>
.v-main {
  width:80%;
  margin:50px auto;
}
.v-container {
  border:1px solid #D0ADF0;
  background-color: rgba(33, 33, 33);
}
.btn_box {
  margin: 40px 0;
}
.v-btn {
    color: #D0ADF0 !important;
}
ul {
  list-style:none;
  border-bottom: 1px solid rgba(208,173,240,0.3);
}
li {
	white-space: pre-wrap;
}
</style>