<template>
  <v-container class="w-50">
    <h1 class="mb-5">책 검색 {{ testData }}</h1>
    <v-row justify="center">
      <v-col>
        <v-text-field
            v-model="keyword"
            :rules="nameRules"
            :counter="20"
            label="검색어를 입력해주세요."
            clear-icon="mdi mdi-close"
            clearable
            required
            append-inner-icon="mdi-magnify"
            @click:append-inner="searchBookInfo"
          ></v-text-field>
        </v-col>
    </v-row>
    <v-row v-if="this.searchBook.length != 0">
        <v-col cols="4" v-for="book in searchBook" :key="book">
        <v-card
            color="#952175"
            theme="dark"
        >
            <div class="card-container d-flex">
            <div style="width:60%">
                <v-card-title style="padding: 20px 20px 5px 20px;font-size: 18px;">
                    {{ book.title }}
                </v-card-title>

                <v-card-subtitle>{{ book.author }}</v-card-subtitle>
                
                <v-card-actions class="d-flex justify-center">
                    <span v-for="i in book.rating" :key="i" class="material-symbols-outlined">kid_star</span>
                </v-card-actions>
    
                <!-- <span class="tag"><a href="">#{{ book.genre }}</a></span> -->

                <v-card-actions>
                <v-btn
                    class="ms-2"
                ></v-btn>
                </v-card-actions>
            </div>

            <v-avatar
                class="ma-3"
                size="125"
                rounded="0"
            >
            <v-img :src="book.img"></v-img>
            </v-avatar>
            </div>
        </v-card>
        </v-col>
    </v-row>
  </v-container>
    <!-- <v-sheet>
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
      </v-sheet> -->
      <!-- <v-form id="bookInfoForm" @submit.prevent="onSubmit"> -->
        <!-- <v-checkbox 
            v-model="book.favoriteFlag"
            label="즐겨찾기"
            color="orange"
        ></v-checkbox> -->

        <!-- <v-text-field
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
      </v-form> -->
  <!-- </v-main> -->
</template>
  
<script>
  export default {
    name: "BookInfo",
  
    data() {
      return {
        testData : "",
        book: {}, 
        keyword : "",
        searchBook : {},
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
              this.searchBook = res.data;
          })
        }
    },
  };
</script>

<style>
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
.v-card {
    max-width: 1800px !important;
    min-height: 200px;
    background-color: transparent;
}
.card-container {
    height: 200px;
}
.v-avatar {
    width: 40% !important; 
    height: 100% !important; 
    padding: 15px; 
    margin: 0 !important;
}
.nav-link {
  text-decoration: none;
  color: white;
}
.nav-link:hover {
  text-decoration: none;
  color: #D0ADF0 !important;
}
.card-container{
    background-color: rgba(33, 33, 33);
}
.v-icon {
    /* color: #D0ADF0 !important; */
    color: #CFADF0
}
.tag a{
    color: #fff;
    text-decoration: none;
}
.tag a:hover {
    color: #D0ADF0 !important;
    text-decoration: none;
}
.material-symbols-outlined {
  font-variation-settings:
  'FILL' 1,
  'wght' 200,
  'GRAD' 0,
  'opsz' 24;
  color: #D0ADF0;
  font-size: 15px;
}
.v-btn {
    color: #D0ADF0 !important;
}
</style>