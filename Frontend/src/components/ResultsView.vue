<template>
  <div class="resultsView">
    <nav class="navbar navbar-light bg-light">
      <router-link to="/" class="navbar-brand">&larr; Go back to the search page</router-link>
    </nav>


    <div class="col-md-12">

      <template v-if="results.length > 0">
        <h1>Results for "{{this.$route.query.input}}"</h1>
        <h6>{{results.length}} results found</h6>

        <b-form-group>
          <b-form-radio-group id="btnradios1" buttons v-model="selected" :options="options" name="radiosBtnDefault" ></b-form-radio-group>
        </b-form-group>

        <template v-if="selected === 'cards'">
          <div class="col-md-8 offset-2">
            <div class="row" v-for="i in Math.ceil(results.length / splitBy)">
              <b-card-group deck>
                <movie-card v-for="(entry, index) in results.slice((i - 1) * splitBy, i * splitBy)" v-if="entry" v-bind:entry="entry" :key="index"></movie-card>
                <movie-card v-for="(entry, index) in splitBy - results.slice((i - 1) * splitBy, i * splitBy).length" v-bind:entry="'undefined'" :key="computeSecondaryKey(index)"></movie-card>
              </b-card-group>
            </div>
          </div>
        </template>
        <template v-else>
          <table-view :entries="results"></table-view>
        </template>

      </template>

      <h1 v-else>No results found!</h1>
      <br>
      <router-link to="/"><span>Go back to the search page</span></router-link>




  </div>
  </div>
</template>

<script>
  import axios from 'axios';
  import SearchView from "./SearchView";
  import MovieCard from "./MovieCard";
  import TableView from "./TableView";

  export default {
    name: 'ResultsView',
    components: {TableView, MovieCard, SearchView},
    data () {
      return {
        selected: 'cards',
        options: [
          { text: 'Cards', value: 'cards' },
          { text: 'Table', value: 'table' },
        ],
        results: [],
        splitBy: 4,
      }
    },
    mounted: function(){
      this.fetchResults();
    },
    updated: function() {
      // this.fetchResults();
    },
    methods: {

      computeSecondaryKey(idx) {
        return 1000 + idx;
      },
      fetchResults() {
        // take the data in input, split it into its components
        var userQuery = this.$route.query.input;



        // Remove the stopwords
        var stopwords = ["a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any","are","aren't","as","at","be","because","been","before","being","below","between","both","but","by","can't","cannot","could","couldn't","did","didn't","do","does","doesn't","doing","don't","down","during","each","few","for","from","further","had","hadn't","has","hasn't","have","haven't","having","he","he'd","he'll","he's","her","here","here's","hers","herself","him","himself","his","how","how's","i","i'd","i'll","i'm","i've","if","in","into","is","isn't","it","it's","its","itself","let's","me","more","most","mustn't","my","myself","no","nor","not","of","off","on","once","only","or","other","ought","our","ours","ourselves","out","over","own","same","shan't","she","she'd","she'll","she's","should","shouldn't","so","some","such","than","that","that's","the","their","theirs","them","themselves","then","there","there's","these","they","they'd","they'll","they're","they've","this","those","through","to","too","under","until","up","very","was","wasn't","we","we'd","we'll","we're","we've","were","weren't","what","what's","when","when's","where","where's","which","while","who","who's","whom","why","why's","with","won't","would","wouldn't","you","you'd","you'll","you're","you've","your","yours","yourself","yourselves"];
        // Remove symbols and stopwords
        var cleanQuery = userQuery.replace(/[^a-zA-Z 0-9]/mg, " ");

        var cleanQueryComponents = cleanQuery.split(" ");
        var cleanQueryList = [];
        for (var i = 0; i < cleanQueryComponents.length; i++) {
          if (!stopwords.includes(cleanQueryComponents[i].toLowerCase()) && cleanQueryComponents[i] !== "" && cleanQueryList.includes(cleanQueryComponents[i].toLowerCase())) {
            cleanQueryList.push(cleanQueryComponents[i].toLowerCase());
          }
        }



        console.log(cleanQueryList);

        if (cleanQueryList.length === 0) {
          cleanQueryList = userQuery.split(" ");
        }

        var queryString = "";
        for (var i = 0; i < cleanQueryList.length; i++) {
          queryString += "fq=title:" + cleanQueryList[i];

          if (i < cleanQueryList.length - 1) {
            queryString += "&";
          }
        }


        queryString += "&q=*:*";

        console.log("Sending query: " + queryString);

        axios.get("http://localhost:8983/solr/nutch/select?" + queryString)
          .then(response => {
            this.results = response.data.response.docs;
            console.log(this.results)
          })
          .catch(e => {
            console.log(e);
          });
      },

    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
