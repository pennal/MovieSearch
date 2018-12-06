<template>
  <div class="parent">
    <div class="container">
      <img :src=imageUrl>
    </div>
  </div>
</template>
<script>
  export default {
    name: 'ScalingImageContainer',
    props: ["imageUrl"]
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  .parent {
    width: 100px;
  }

  .container {
    display: block;
    width: 100%;
    height: auto;
    position: relative;
    overflow: hidden;
    padding: 34.37% 0 0 0; /* 34.37% = 100 / (w / h) = 100 / (640 / 220) */
  }

  .container img {
    display: block;
    max-width: 100%;
    max-height: 100%;
    position: absolute;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
  }

</style>
