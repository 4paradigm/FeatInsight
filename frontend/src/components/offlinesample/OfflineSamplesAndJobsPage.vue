<template>
  <div>
  
    <h1>{{ $t('All Offline Samples') }}</h1>
    <br/>
    <OfflineSamplesData></OfflineSamplesData>
  
    <h1>{{ $t('All Offline Jobs') }}</h1>
    <br/>
    <OfflineJobsData></OfflineJobsData>

  </div>
  </template>
    
  <script>
  import OfflineSamplesData from '@/components/offlinesample/OfflineSamplesData.vue';
  import OfflineJobsData from '@/components/offlinejob/OfflineJobsData.vue';

  export default {
    components: {
      OfflineSamplesData,
      OfflineJobsData
    }
  };
  </script>