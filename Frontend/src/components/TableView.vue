<template>
  <table class="table table-striped">
    <thead>
    <tr>
      <th scope="col">Title</th>
      <th scope="col">Description</th>
      <th scope="col">Genres</th>
      <th scope="col">Duration</th>
      <th scope="col">Release Date</th>
      <th scope="col">IMDB Link</th>
      <th scope="col">IMDB Rating</th>
      <th scope="col">AllMovie Link</th>
      <th scope="col">AllMovie Rating</th>
    </tr>
    </thead>
    <tbody>
    <tr v-for="(entry, index) in entries">
      <td>{{entry.title[0]}}</td>
      <td>{{getDescription(entry)}}</td>
      <td>{{getGenres(entry).join(", ")}}</td>

      <td>
        <template v-if='entry.duration !== undefined'>
          {{entry.duration[0] || ""}} Minutes
        </template>
        <template v-else></template>
      </td>


      <td>{{getReleaseDate(entry)}}</td>

      <td>
        <template v-if='entry["imdbData.link"] !== undefined'>
          <a target="_blank" :href='entry["imdbData.link"][0]'>Link</a>
        </template>
        <template v-else></template>
      </td>

      <td>
        <template v-if='entry["imdbData.rating"] !== undefined'>
          {{entry["imdbData.rating"][0] || ""}}
        </template>
        <template v-else></template>
      </td>


      <td>
        <template v-if='entry["allMoviesData.link"] !== undefined'>
          <a target="_blank" :href='entry["allMoviesData.link"][0]'>Link</a>
        </template>
        <template v-else></template>
      </td>

      <td>
        <template v-if='entry["allMoviesData.rating"] !== undefined'>
          {{entry["allMoviesData.rating"][0] || ""}}
        </template>
        <template v-else></template>
      </td>
    </tr>
    </tbody>
  </table>
</template>

<script>
  export default {
    name: 'TableView',
    props: ['entries'],
    methods: {
      truncate: function(text) {
        if (text === undefined) {
          return "";
        }
        return (text.length > 200) ? text.substr(0, 200-1) + '...' : text;
      },
      getDescription: function(entry) {
        if (entry.description === undefined) {
          return "No description available";
        }
        return this.truncate(entry.description[0])
      },
      getGenres: function(entry) {
        if (entry.genres === undefined) {
          return [''];
        }

        return entry.genres;
      },
      getReleaseDate: function(entry) {
        if (entry.releaseDate === undefined) {
          return "No release date available";
        }
        return entry.releaseDate[0]
      }

    }
  }
</script>

<style>

</style>
