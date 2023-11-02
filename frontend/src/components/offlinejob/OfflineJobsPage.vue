<template>
<div>

  <br/>
  <h1>{{ $t('All Offline Jobs') }}</h1>
  <br/>
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