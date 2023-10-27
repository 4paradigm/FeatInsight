<template>
  <div>
  
    <h1>{{ $t('All Feature Services') }}</h1>
    <br/>
    <FeatureServicesData></FeatureServicesData>
  
  </div>
  </template>
    
  <script>
  import FeatureServicesData from '@/components/featureservice/FeatureServicesData.vue';

  export default {
    components: {
      FeatureServicesData
    }
  };
  </script>