<template>
    <v-app-bar>
        <div class="px-3" style="width:160px; height:100%;">
                <v-img src="@/assets/logo.png" style="width: 100%; height: 100%;"></v-img>
        </div>
        <v-spacer />
        <div class="d-flex justify-end px-3">
            <v-btn-alt
                to="/book/create"
                rel="noopener noreferrer"
                text="책 추가하기"
            />
        </div>
    </v-app-bar>
</template>

<script>
export default {
    name: "VueHeader",
}
</script>

<style>
.v-app-bar .v-container {
    margin:0 !important;
}
</style>