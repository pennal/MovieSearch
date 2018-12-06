<template>

  <div v-if="entry === 'undefined'" class="card" style="visibility: hidden"></div>

  <b-card v-else :title=entry.title[0]
          :img-src=getImageUrl()
          img-alt="Img"
          img-top
          id="poster">

    <div class="card-text">
      <div class="row">
        <h6 class="col-md-12">Description</h6>
        <p class="col-md-12">{{getDescription()}}</p>
      </div>

      <div class="row" v-if="entry.genres !== undefined">
        <h6 class="col-md-12">Genres</h6>
        <p class="col-md-12">{{concatArray(entry.genres)}}</p>
      </div>

      <div class="row" v-if="entry.releaseDate !== undefined">
        <h6 class="col-md-12">Release Date</h6>
        <p class="col-md-12">{{entry.releaseDate[0]}}</p>
      </div>

      <div class="row" v-if="entry.duration !== undefined">
        <h6 class="col-md-12">Duration</h6>
        <p class="col-md-12">{{entry.duration[0]}} Minutes</p>
      </div>

      <div class="row" v-if="entry['imdbData.rating'] !== undefined">
        <h6 class="col-md-12">IMDB Rating (<a target="_blank" :href="entry['imdbData.link']">Link</a>)</h6>
        <p class="col-md-12">{{entry['imdbData.rating'][0]}}</p>
      </div>

      <div class="row" v-if="entry['allMoviesData.rating'] !== undefined">
        <h6 class="col-md-12">AllMovie Rating (<a target="_blank" :href="entry['allMoviesData.link']">Link</a>)</h6>
        <p class="col-md-12">{{entry['allMoviesData.rating'][0]}}</p>
      </div>
    </div>
  </b-card>

</template>

<script>
  export default {
    name: 'MovieCard',
    props: ["entry"],
    created: function(){
      console.log(JSON.stringify(this.entry["allMoviesData.link"]));
    },
    methods: {
      concatArray: function(arr) {
        return (arr || ['']).join(', ');
      },
      getImageUrl: function () {
        if (this.entry.imageUrl === undefined) {
          return "http://www.theprintworks.com/wp-content/themes/psBella/assets/img/film-poster-placeholder.png";
        }

        return this.entry.imageUrl[0];
      },
      getDescription: function() {
        if (this.entry.description === undefined) {
          return "No Description Available";
        }
        return this.truncate(this.entry.description[0]);
      },
      truncate: function(text) {
        return (text.length > 200) ? text.substr(0, 200-1) + '...' : text;
      }
    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  .card-img-top {
    min-width: 100%;
    height: 15vw;
    object-fit: cover;
  }

  .card-footer {
    padding: 0px;
    padding-top: 10px;
    padding-bottom: 10px;
  }


  h6 {
    text-align: left;
  }

  p {
    text-align: left;
  }


  .card-title {
    min-height: 56px
  }

  .card {
    margin-bottom: 15px;
  }





</style>
