<template>
  <v-container class="w-50">
    <h1 class="mb-5">책 검색 {{ testData }}</h1>
    <v-btn 
                    class="material-symbols-outlined float-end" 
                    style="position: absolute; right:30px; opacity: 1 !important; min-width: 0;" 
                    variant="plain"
                    @click="dialog = true"
                  >add_circle  
                  </v-btn>
    <v-row justify="center">
      <v-col>
        <v-text-field
            v-model="keyword"
            :rules="nameRules"
            :counter="20"
            label="검색"
            clear-icon="mdi mdi-close"
            clearable
            required
            append-inner-icon="mdi-magnify"
            @click:append-inner="searchBookInfo"
          ></v-text-field>
        </v-col>
    </v-row>
    
    <v-row v-if="this.searchBook.length != 0">
        <v-col cols="12" v-for="(book, index) in searchBook" :key="book">
          <v-form id="bookInfoForm" @submit.prevent="openBookDetailModal(index)">
          <v-card
              color="#952175"
              theme="dark"
          >
          <div class="card-container d-flex">
            <v-avatar
                class="ma-3"
                size="125"
                rounded="0"
            >
            <v-img :src="book.img"></v-img>
            </v-avatar>
            <div style="width:60%">
                <v-card-title style="padding: 20px 20px 5px 20px;font-size: 18px;" v-model="book.title">
                  <RouterLink
                    :to="{ path: '/book/search/detail/'+book.bookId }"
                    active-class="active"
                    class="nav-link"
                  >
                    {{ book.title }}
                  </RouterLink>
                  <v-btn 
                    class="material-symbols-outlined float-end" 
                    style="position: absolute; right:30px; opacity: 1 !important; min-width: 0;" 
                    variant="plain"
                    type="submit"
                  >add_circle
                  </v-btn>
                </v-card-title>

                <v-card-subtitle style="opacity: 1;" v-model="book.author">{{ book.author }}</v-card-subtitle>
                
                <v-card-text style="height: 40%; padding: 20px; overflow:hidden; text-overflow:ellipsis; word-break: break-word; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; opacity: var(--v-medium-emphasis-opacity);">
                    <!-- <span v-for="i in book.rating" :key="i" class="material-symbols-outlined">kid_star</span> -->
                    {{ book.desc }}
                </v-card-text>
    
                <!-- <span><a href="">#{{ book.desc }}</a></span> -->

                <v-card-actions>
                <v-btn
                    class="ms-2"
                ></v-btn>
                </v-card-actions>
            </div>
          </div>
        </v-card>
        </v-form>
        </v-col>
    </v-row>
  </v-container>





  <v-dialog v-model="dialog" width="500">
      <v-card theme="dark" style="background-color: black; padding:3rem;">

        <v-text-field
            v-model="book.title"
            variant="plain"
            :rules="nameRules"
            :counter="20"
            clear-icon="mdi mdi-close"
            clearable
            required
            single-line
            hide-details
            :readonly="isReadOnly"
            @click="isReadOnly = false"
            style="align-items: center;"
        >{{ this.detailBook.title }}</v-text-field>

        <v-text-field
            v-model="book.author"
            variant="solo"
            :rules="nameRules"
            :counter="10"
            label="저자"
            clear-icon="mdi mdi-close"
            clearable
            required
            :readonly="isReadOnly"
            @click="isReadOnly = false"
        ></v-text-field>

        <v-combobox
            v-model="book.author"
            variant="solo"
            :rules="nameRules"
            :counter="10"
            label="분류"
            clear-icon="mdi mdi-close"
            clearable
            required
            :readonly="isReadOnly"
            @click="isReadOnly = false"
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
            variant="solo"
            type="number"
            min="1" max="5" step="0.1"
            label="평점"
        ></v-text-field>

        <v-textarea
            rows="2"
            v-model="book.review"
            variant="solo"
            label="한줄평"
            :counter="100"
        ></v-textarea>

        <!-- <v-textarea
            v-model="book.memo"
            variant="solo"
            label="메모"
            :counter="100"
        ></v-textarea> -->

        <v-card-actions style="padding:1rem;">
          <v-spacer></v-spacer>
          <v-btn color="primary" block @click="dialog = false">Close Dialog</v-btn>
        </v-card-actions>
      </v-card>
  </v-dialog>
  
</template>
  
<script>
  export default {
    name: "BookInfo",
  
    data() {
      return {
        dialog: false,
        testData : "",
        book: {}, 
        keyword : "",
        searchBook : {},
        isReadOnly: true,
        detailBook : {},
      };
    },
    mounted() {
      // 초기에는 입력란이 읽기 전용 상태로 시작
      this.isReadOnly = true;
    },
    methods: {
        // get() {
        //     this.axios.get("/book/test").then((response) => {
        //         this.testData = response.data;
        //     });
        // },
        onsubmit() {
          console.log(this.book);
            // console.log(this.book);

            // this.axios.post("/book/create", this.book)
            // .then((response) => {
            //     this.book = response.data;

            //     this.$router.push({
            //         path: "/book/detail/"+this.book.bookId,
            //     });
            // });
        },
        searchBookInfo() {
          this.axios.post("/book/search/yes24/"+this.keyword)
          .then((res) => {
              console.log(res);

              let result = res.data;
              for(const obj of result) {
                const parser = new DOMParser();
                const doc = parser.parseFromString(obj.desc, "text/html");
                const text = doc.body.textContent;
                console.log("parsed", text);
                obj.desc = text;
              }
              
              this.searchBook = res.data;
          })
        },
        openBookDetailModal(index) {
          this.dialog = true;
          this.detailBook = this.searchBook[index];
        }
    },
  };
</script>

<style>
.v-container.d-flex {
  max-width: 700px;
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
    width: 30% !important; 
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
  /* font-variation-settings:
  'FILL' 1,
  'wght' 200,
  'GRAD' 0,
  'opsz' 24;
  color: #D0ADF0;
  font-size: 15px; */
  font-variation-settings:
  'FILL' 0,
  'wght' 300,
  'GRAD' 0,
  'opsz' 24;
  font-size: 30px;
}
.v-btn {
    color: #D0ADF0 !important;
}
</style>