<template>
    <v-app-bar>
        <div class="px-3" style="width:160px; height:100%;">
                <v-img src="@/assets/logo.png" style="width: 100%; height: 100%;"></v-img>
        </div>
        <v-spacer />
        <div class="d-flex justify-end px-3">
            <v-btn-alt
                v-if="isLogined()"
                to="/book/create"
                rel="noopener noreferrer"
                text="책 추가"
            />
            <v-btn-alt
                v-if="!isLogined()"
                to="/login"
                rel="noopener noreferrer"
                text="로그인"
            />
            <v-btn-alt
                v-if="isLogined()"
                rel="noopener noreferrer"
                text="로그아웃"
            />
        </div>
    </v-app-bar>
</template>

<script>
export default {
    name: "VueHeader",
    data() {
    },
    methods: {
        isLogined() {
            const value = document.cookie.match('(^|;) ?' + "accessToken" + '=([^;]*)(;|$)');
            return value ? true : false;
        },
    },
}
</script>

<style>
.v-app-bar .v-container {
    margin:0 !important;
}
</style>