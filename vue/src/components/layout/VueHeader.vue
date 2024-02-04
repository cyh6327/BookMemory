<template>
    <v-app-bar>
        <div class="px-3" style="width:160px; height:100%;">
                <v-img src="@/assets/logo.png" style="width: 100%; height: 100%;"></v-img>
        </div>
        <v-spacer />
        <div class="d-flex justify-end px-3">
            <v-btn-alt
                v-if="isLogined"
                to="/email"
                rel="noopener noreferrer"
                text="오늘의 문장"
            />
            <v-btn-alt
            v-if="isLogined"
                to="/book/create"
                rel="noopener noreferrer"
                text="책 추가"
            />
            <v-btn-alt
            v-if="!isLogined"
                to="/login"
                rel="noopener noreferrer"
                text="로그인"
            />
            <v-btn-alt
            v-if="isLogined"
                rel="noopener noreferrer"
                text="로그아웃"
            />
        </div>
    </v-app-bar>
</template>

<script>
export default {
    name: "VueHeader",
    computed: {
        isLogined() {
            return this.$store.state.isLogined; // 전역 변수 가져오기
        },
    },
    data() {
    },
}
</script>

<style>
.v-app-bar .v-container {
    margin:0 !important;
}
</style>