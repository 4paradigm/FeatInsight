<template>
<div>

  <br/>
  <h1>{{ $t('All Features') }}</h1>
  <br/>
  <FeaturesData></FeaturesData>

  <br/>
  <h1>{{ $t('All Feature Views') }}</h1>
  <br/>
  <FeatureViewsData></FeatureViewsData>

</div>
</template>
  
<script>
import FeaturesData from '@/components/feature/FeaturesData.vue';
import FeatureViewsData from '@/components/featureview/FeatureViewsData.vue'
import { PlusOutlined } from '@ant-design/icons-vue';

export default {
  components: {
    FeaturesData,
    FeatureViewsData,
    PlusOutlined
  }
};
</script>