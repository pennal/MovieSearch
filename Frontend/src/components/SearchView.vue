<template>
  <div class="searchView">
    <div class="row h-100 ">
      <div class="col-md-8 offset-2 my-auto">
        <h1>MovieSearch</h1>
        <b-form-input @keydown.native="onEnter" v-model="input" type="text" placeholder="Enter your query"></b-form-input>

      </div>
    </div>
  </div>




</template>

<script>




  export default {

    name: 'SearchView',
    data () {
      return {
        input: ''
      }
    },
    methods: {
      onEnter(event) {
        if (event.which === 13) {
          let q = {
            input: this.input
          };
          this.$router.push({path:'results', query: q });
        }
      }
    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  .searchView {
    height: 100vh;
  }

</style>
