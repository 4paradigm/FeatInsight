<template>
<div>

  <h1>{{ $t('Features') }}</h1>
  <FeaturesData></FeaturesData>

  <h1>{{ $t('Feature Views') }}</h1>
  <FeatureViewsData></FeatureViewsData>

</div>
</template>
  
<script>
import FeaturesData from '@/components/feature/FeaturesData.vue';
import FeatureViewsData from '@/components/featureview/FeatureViewsData.vue'

export default {
  components: {
    FeaturesData,
    FeatureViewsData
  }
};
</script>