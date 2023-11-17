<template>
<div>

  <a-modal v-model:visible="isShowCreateFeatureModal" width="1000px" :title="$t('Create Feature')" >
    <template #footer>
      <a-button @click="handleCancel">Cancel</a-button>
    </template>
    <CreateFeatureForm @submitted="submittedCreateFeatureForm"></CreateFeatureForm>
  </a-modal>

  <br/>
  <h1>
    {{ $t('All Features') }}<a-button type="link" @click="clickCreateFeature"><PlusCircleOutlined /></a-button>
  </h1>

  <FeaturesData :key="refreshDataKey"></FeaturesData>

  <br/>
  <h1>{{ $t('All Feature Views') }}</h1>
  <FeatureViewsData :key="refreshDataKey"></FeatureViewsData>

</div>
</template>
  
<script>
import FeaturesData from '@/components/feature/FeaturesData.vue';
import FeatureViewsData from '@/components/featureview/FeatureViewsData.vue'
import CreateFeatureForm from '@/components/form/CreateFeatureForm.vue'
import { PlusCircleOutlined } from '@ant-design/icons-vue';

export default {
  components: {
    FeaturesData,
    FeatureViewsData,
    CreateFeatureForm,
    PlusCircleOutlined
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