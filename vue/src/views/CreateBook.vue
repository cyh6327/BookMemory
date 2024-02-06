<template>
  <v-main>
    <h1>책 정보 등록 {{ testData }}</h1>
    <v-container>
      <v-form id="bookInfoForm" @submit.prevent="onSubmit">
        <v-row>
          <v-col>
            <v-text-field
                v-model="keyword"
                :rules="nameRules"
                :counter="20"
                label="책 검색"
                clear-icon="mdi mdi-close"
                clearable
                required
              ></v-text-field>
            </v-col>
            <v-col cols="1">
              <v-btn-alt
                    @click="searchBookInfo"
                    rel="noopener noreferrer"
                    text="검색"
              />
            </v-col>
        </v-row>
        <!-- <v-checkbox 
            v-model="book.favoriteFlag"
            label="즐겨찾기"
            color="orange"
        ></v-checkbox> -->

        <v-text-field
            v-model="book.title"
            :rules="nameRules"
            :counter="20"
            label="제목"
            clear-icon="mdi mdi-close"
            clearable
            required
        ></v-text-field>

        <v-text-field
            v-model="book.author"
            :rules="nameRules"
            :counter="10"
            label="저자"
            clear-icon="mdi mdi-close"
            clearable
            required
        ></v-text-field>

        <v-combobox
            label="장르"
            v-model="book.genre"
            :items="['자기계발', '인문학', '에세이', '소설']"
            required
        ></v-combobox>
        
        <v-container class="d-flex">
          <VueDatePicker
              v-model="readingStartDate" 
              class="flex-1"
              locale="ko" 
              range 
              dark 
              placeholder="독서 시작일"
          />
          <VueDatePicker 
              v-model="readingEndDate"
              locale="ko" 
              range 
              dark 
              placeholder="독서 종료일"
          />
        </v-container>

        <v-text-field
            v-model="book.rating"
            type="number"
            min="1" max="5" step="0.1"
            label="평점"
        ></v-text-field>

        <v-textarea
            v-model="book.review"
            label="한줄평"
            :counter="100"
        ></v-textarea>

        <v-textarea
            v-model="book.memo"
            label="메모"
            :counter="100"
        ></v-textarea>
  
        <v-btn type="submit" block class="mt-2 float-end" color="#2c3e50">등록</v-btn>
      </v-form>
      </v-container>
  </v-main>
</template>
  
<script>
  export default {
    name: "BookInfo",
  
    data() {
      return {
        testData : "",
        book: {}, 
        keyword : "",
      };
    },
    // mounted() {
    //     this.get();
    // },
    methods: {
        // get() {
        //     this.axios.get("/book/test").then((response) => {
        //         this.testData = response.data;
        //     });
        // },
        onSubmit() {
            console.log(this.book);

            this.axios.post("/book/create", this.book)
            .then((response) => {
                this.book = response.data;

                this.$router.push({
                    path: "/book/detail/"+this.book.bookId,
                });
            });
        },
        searchBookInfo() {
          this.axios.post("/book/search/yes24/"+this.keyword)
          .then((res) => {
              console.log(res);
          })
        }
    },
  };
</script>

<style>
.v-main {
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
}
</style>