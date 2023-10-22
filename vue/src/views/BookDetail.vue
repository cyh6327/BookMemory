<template>
    <h4>제목:{{ book.title }}</h4>
</template>

<script>
export default {
  data() {
    return {
        book: {
            title: "",
        },
    };
  },
  mounted() {
    const params = this.$router.params;
    console.log(params);
  },
};
</script>

<style>

</style>