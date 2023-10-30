<template>
  <div>
  
    <h1>{{ $t('Offline Samples') }}</h1>
    <OfflineSamplesData></OfflineSamplesData>
  
  </div>
  </template>
    
  <script>
  import OfflineSamplesData from '@/components/offlinesample/OfflineSamplesData.vue';

  export default {
    components: {
      OfflineSamplesData
    }
  };
  </script>