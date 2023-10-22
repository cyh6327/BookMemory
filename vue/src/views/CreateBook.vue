<template>
    <h1>책 정보 등록 {{ testData }}</h1>
    <form id="bookInfoForm" @submit.prevent="onSubmit">
      <label for="title">제목:</label>
      <input type="text" id="title" name="title" v-model="book.title" required><br>
  
      <label for="author">저자:</label>
      <input type="text" id="author" name="author" v-model="book.author" required><br>
  
      <div>
        <label for="genre">장르:</label>
        <select id="genre" name="genre" v-model="book.genre">
          <option value="자기계발">자기계발</option>
          <option value="인문학">인문학</option>
          <option value="에세이">에세이</option>
          <option value="소설">소설</option>
        </select>
      </div>
  
      <label for="readingStartDate">독서 시작일:</label>
      <input type="date" id="readingStartDate" name="readingStartDate" v-model="book.readingStartDate">
  
      <label for="readingEndDate">독서 종료일:</label>
      <input type="date" id="readingEndDate" name="readingEndDate" v-model="book.readingEndDate">
  
      <label for="rating">평점:</label>
      <input type="number" id="rating" name="rating" v-model="book.rating" min="1" max="5" step="0.1">
  
      <label for="review">리뷰:</label><br>
      <textarea id="review" name="review" v-model="book.review" rows="4" cols="50"></textarea><br>
  
      <label for="memo">메모:</label><br>
      <textarea id="memo" name="memo" v-model="book.memo" rows="4" cols="50"></textarea><br>
  
      <label for="favoriteFlag">즐겨찾기 여부:</label>
      <input type="checkbox" id="favoriteFlag" name="favoriteFlag" v-model="book.favoriteFlag">
  
      <button type="submit">등록</button>
    </form>
  </template>
  
  <script>
  export default {
    name: "BookInfo",
  
    data() {
      return {
        testData : "",
        book: {}, 
      };
    },
    mounted() {
        this.get();
    },
    methods: {
        get() {
            this.axios.get("/book/test").then((response) => {
                this.testData = response.data;
            });
        },
        onSubmit() {
            const bookInfoForm = document.getElementById("bookInfoForm");
            const formData = new FormData(bookInfoForm);
            console.log([...formData]);

            this.axios.post("/book/create", formData)
            .then((response) => {
                console.log(response);
                this.book = response.data;
                console.log(this.book);
                this.$router.push({
                    path: "/book/detail/"+this.book.bookId,
                    //params: this.book,
                    params: {
                        title: this.book.title,
                    }
                });
            });
        },
    },
  };
  </script>

<style>

</style>