<template>
  <div>
  
    <h1>{{ $t('Offline Jobs') }}</h1>
    <OfflineJobsData></OfflineJobsData>
  
  </div>
  </template>
    
  <script>
  import OfflineJobsData from '@/components/offlinejob/OfflineJobsData.vue';

  export default {
    components: {
      OfflineJobsData
    }
  };
  </script>