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
                <v-row>

                    <v-col cols="3" v-for="book in bookList" :key="book">
                    <v-card
                        color="#952175"
                        theme="dark"
                    >
                        <div class="card-container d-flex flex-no-wrap justify-space-between">
                        <div>
                            <v-card-title>
                                <RouterLink
                                    :to="{ path: '/book/detail/'+book.bookId }"
                                    active-class="active"
                                    class="nav-link"
                                >
                                {{ book.title }}
                                </RouterLink>
                            </v-card-title>

                            <v-card-subtitle>{{ book.author }}</v-card-subtitle>

                            <v-card-text>
                                <!-- <v-icon>mdi-tag-outline</v-icon> -->
                                <span class="tag"><a href="">#{{ book.genre }}</a></span>
                                <!-- <span class="tag">
                                    #{{ book.genre }}
                                </span> -->
                            </v-card-text>
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
                            <v-img src="@/assets/book.jpg"></v-img>
                        </v-avatar>
                        </div>
                    </v-card>
                    </v-col>

                </v-row>
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
        this.axios.get("/book").then((response) => {
            //this.book = response.data;
            console.log(response.data);
            this.bookList = response.data;
            console.log(this.bookList);
            console.log(this.bookList[0]);
        });
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
.v-card-title {
    padding: 20px;
}
.card-container {
    height: 200px;
}
.v-avatar {
    width: 150px !important; 
    height: 200px !important; 
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
.v-btn {
    color: #D0ADF0 !important;
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
</style>