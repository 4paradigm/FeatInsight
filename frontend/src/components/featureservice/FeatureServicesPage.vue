<template>
  <div>
  
    <h1>{{ $t('Feature Services') }}</h1>
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