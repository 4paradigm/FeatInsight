<template>

<div>

  <FeatureServicesPage></FeatureServicesPage>
 
</div>
</template>
  
<script>
import axios from 'axios'
import { message } from 'ant-design-vue';
import FeatureServicesPage from './FeatureServicesPage.vue';

export default {
  components: {
    FeatureServicesPage
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