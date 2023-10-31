<template>
  <v-main id="book_detail">
    <h2 class="mb-5">{{ book.bookInfo.title }}</h2>
    <h4 style="color:gray"> {{ book.bookInfo.author }}</h4>
    <div class="btn_box d-flex justify-end">
      <v-btn-alt
        v-on:click="insertSentenceFromFile"
        rel="noopener noreferrer"
        text="파일로 문장 추가"
      />
    </div>
    <v-container class="rounded-shaped">
    <!-- <v-container style="padding: 0;"> -->
      <ul class="my-10" v-for="sentence in book.bookSentences" :key="sentence">
        <li class="pa-10" style="text-align:left;">{{ sentence.sentenceText }}</li>
      </ul>
    </v-container>
  </v-main>

</template>

<script>
export default {
  name: 'BookDetail',
  data() {
    return {
      book: {
        bookInfo: {
        },
        bookSentences: {},
      }, 
    };
  },
  mounted() {
    this.get();
  },
  methods: {
    get() {
        const bookId = this.$route.params.bookId;
        console.log(bookId);
        this.axios.get("/book/detail/"+bookId).then((response) => {
            this.book = response.data;
            console.log(response.data);
        });
    },
    insertSentenceFromFile() {
      console.log(this.book.bookInfo);
      const bookId = this.book.bookInfo.bookId;
      const title = this.book.bookInfo.title;
      console.log(bookId);
      console.log(title);

      this.axios.post("/book/sentence/file/"+bookId+"/"+title)
      .then((response) => {
          this.book = response.data;
          console.log(response.data);

          // this.$router.push({
          //     path: "/book/detail/"+this.book.bookId,
          // });
      });
    },
  }
};
</script>

<style>
.v-main {
  width:60%;
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
}
</style>