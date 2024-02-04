<template>
    <v-main>
    <!-- <ul>
        <li v-for="book in bookList" :key="book">
            {{ book.title }}
        </li>
    </ul> -->
        <v-card
        max-width="400"
        class="mx-auto"
        >
            <v-container>
                <v-row v-if="this.bookList.length != 0">
                    <v-col cols="4" v-for="book in bookList" :key="book">
                    <v-card
                        color="#952175"
                        theme="dark"
                    >
                        <div class="card-container d-flex">
                        <div style="width:60%">
                            <v-card-title style="padding: 20px 20px 5px 20px;font-size: 18px;">
                                <RouterLink
                                    :to="{ path: '/book/detail/'+book.bookId }"
                                    active-class="active"
                                    class="nav-link"
                                >
                                {{ book.title }}
                                </RouterLink>
                            </v-card-title>

                            <v-card-subtitle>{{ book.author }}</v-card-subtitle>
                            
                            <v-card-actions class="d-flex justify-center">
                                <span v-for="i in book.rating" :key="i" class="material-symbols-outlined">kid_star</span>
                            </v-card-actions>
               
                            <span class="tag"><a href="">#{{ book.genre }}</a></span>

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
                        <v-img :src="require(`@/assets/${book.title}.jpg`)"></v-img>
                            <!-- <v-img src="@/assets/book.jpg"></v-img> -->
                        </v-avatar>
                        </div>
                    </v-card>
                    </v-col>
                </v-row>
                <div v-else>
                    <p>추가한 책이 없습니다.</p>
                </div>
            </v-container>

        </v-card>
    </v-main>
</template>

<script>
export default {
  name: 'DashBoard',
  data() {
    return {
        bookList: [],
    };
  },
  mounted() {
    this.get();
  },
  methods: {
    get() {
        this.axios.get("/book")
        .then((response) => {
            // 로그인 전 최초 메인 페이지 접속시 상태코드=204(no content), 로그인 이후 통신 성공시 상태코드=200
            if(response.status == 200) {
                console.log(response)
                this.bookList = response.data;
                console.log(this.bookList);
                console.log(this.bookList[0]);
            }
        })
        .catch((error) => {
            console.log(error);
            this.bookList = null;
        })
    },
  }
};
</script>

<style>
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