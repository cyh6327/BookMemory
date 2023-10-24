<template>
    <v-app-bar>
        <v-container class="d-flex align-center py-0">
            <v-app-bar-title class="pl-0">
            <div class="d-flex align-center">
                <v-avatar
                class="me-3"
                image="https://cdn.vuetifyjs.com/docs/images/logos/v.png"
                rounded="0"
                />

                BookWhisper
            </div>
            </v-app-bar-title>

            <v-spacer />

            <v-btn-alt
            append-icon="mdi-open-in-new"
            to="/book/create"
            rel="noopener noreferrer"
            text="책 추가하기"
            />
        </v-container>
    </v-app-bar>
</template>

<script>
export default {
    name: "VueHeader",
}
</script>