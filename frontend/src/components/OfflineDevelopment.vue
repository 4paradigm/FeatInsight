<template>

<div>

  <TrainingSetsPage></TrainingSetsPage>
 
</div>
</template>
  
<script>
import axios from 'axios'
import { message } from 'ant-design-vue';
import TrainingSetsPage from './TrainingSetsPage.vue';

export default {
  components: {
    TrainingSetsPage
  },

  data() {
    return {
    }
  },

  mounted() {
    this.initData();
  },

  methods: {
    initData() {
    },
  },
};
</script>