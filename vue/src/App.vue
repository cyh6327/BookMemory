<template>
  <v-app>
    <VueHeader />
    <router-view/>
  </v-app>
</template>

<script>
import VueHeader from '@/components/layout/VueHeader.vue'

export default {
  name: 'App',
  components: {
    VueHeader,
  }
}
</script>

<style>
@font-face {
    font-family: 'GowunDodum';
    src: url('/src/assets/fonts/GowunDodum-Regular.ttf') format('woff');
     font-weight: normal; 
     font-style: normal;  
}

#app {
  font-family: GowunDodum, Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  /* color: #2c3e50; */
  color: #fff;
  margin: 80px;
  /* display: flex;
  justify-content: center;
  align-items: center; */
}

.v-dialog {
  font-family: GowunDodum, Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

nav {
  padding: 30px;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
}

nav a.router-link-exact-active {
  color: #42b983;
}

</style>