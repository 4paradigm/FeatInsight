<template>
<div>
  
  <br/>
  <h1>{{ $t('All Offline Samples') }}</h1>
  <br/>
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