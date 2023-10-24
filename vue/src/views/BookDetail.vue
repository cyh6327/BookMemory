<template>
    <h4>{{ book.bookInfo.title }}</h4>
</template>

<script>
export default {
  name: 'BookDetail',
  data() {
    return {
      book: {
        bookInfo: {},
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
  }
};
</script>

<style>

</style>