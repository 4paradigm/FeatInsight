<template>

<div>

  <br/>
  <OnlineTablesPage></OnlineTablesPage>

  <FeatureServicesPage></FeatureServicesPage>
 
</div>
</template>
  
<script>
import FeatureServicesPage from './FeatureServicesPage.vue';
import OnlineTablesPage from './OnlineTablesPage.vue';

export default {
  components: {
    FeatureServicesPage,
    OnlineTablesPage
  },

  data() {
    return {
    }
  },

  mounted() {
  },

  methods: {
  },
};
</script>