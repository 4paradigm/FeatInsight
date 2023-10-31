<template>
<div>

  <a-modal v-model:visible="isShowCreateFeatureModal" width="1000px" :title="$t('Create Feature View')" >
    <template #footer>
      <a-button @click="handleCancel">Cancel</a-button>
    </template>
    <CreateFeatureViewForm @submitted="submittedCreateFeatureForm"></CreateFeatureViewForm>
  </a-modal>

  <br/>
  <a-button type="primary" @click="clickCreateFeature">{{ $t('Create Feature') }}</a-button>
        
  <br/><br/>
  <h1>{{ $t('All Features') }}</h1>
  <br/>
  <FeaturesData :key="refreshDataKey"></FeaturesData>

  <br/>
  <h1>{{ $t('All Feature Views') }}</h1>
  <br/>
  <FeatureViewsData :key="refreshDataKey"></FeatureViewsData>

</div>
</template>
  
<script>
import FeaturesData from '@/components/feature/FeaturesData.vue';
import FeatureViewsData from '@/components/featureview/FeatureViewsData.vue'
import CreateFeatureViewForm from '@/components/form/CreateFeatureViewForm.vue'

export default {
  components: {
    FeaturesData,
    FeatureViewsData,
    CreateFeatureViewForm
  },

  data() {
    return {
      isShowCreateFeatureModal: false,

      refreshDataKey: 0
    }
  },

  methods: {
    clickCreateFeature() {
      this.isShowCreateFeatureModal = true;
    },

    handleCancel() {
      this.isShowCreateFeatureModal = false;
    },

    submittedCreateFeatureForm(newFeatureViewName) {
      this.isShowCreateFeatureModal = false;
      this.refreshDataKey++;
    }

  }
};
</script>