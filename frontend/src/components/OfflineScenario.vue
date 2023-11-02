<template>
<div>

  <a-modal v-model:visible="isShowCreateFeatureModal" width="1000px" :title="$t('Create Feature')" >
    <template #footer>
      <a-button @click="handleCancel">Cancel</a-button>
    </template>
    <CreateFeatureForm @submitted="submittedCreateFeatureForm"></CreateFeatureForm>
  </a-modal>

  <a-modal v-model:visible="isShowCreateOfflineSampleModal" width="1000px" :title="$t('Create Offline Sample')" @ok="clickCreateOfflineSampleOk">
    <CreateOfflineSampleForm ref="CreateOfflineSampleForm"></CreateOfflineSampleForm>
  </a-modal>

  <br/>
  <a-typography>
    <a-typography-title :level="2">{{ $t('Offline Scenario') }}</a-typography-title>
    <a-typography-paragraph>
      <blockquote>
        离线场景是使用分布式计算，对离线数据进行特征计算并导出机器学习所需的样本文件，用户首先<a-button type="link" @click="clickCreateFeature">{{ $t('Create Feature') }}</a-button>，然后选择特征来<a-button type="link" @click="showCreateOfflineSampleFormModal">{{ $t('Export Offline Sample') }}</a-button>。
      </blockquote>
    </a-typography-paragraph>
  </a-typography>

  <br/>
  <h1>{{ $t('Offline Samples') }}</h1>
  <OfflineSamplesData></OfflineSamplesData>
 
</div>
</template>
  
<script>
import OfflineSamplesData from '@/components/offlinesample/OfflineSamplesData.vue'
import OfflineTables from '@/components/table/OfflineTables.vue'
import CreateOfflineSampleForm from '@/components/form/CreateOfflineSampleForm.vue'
import CreateFeatureForm from '@/components/form/CreateFeatureForm.vue'

export default {
  components: {
    OfflineSamplesData,
    OfflineTables,
    CreateOfflineSampleForm,
    CreateFeatureForm
  },

  data() {
    return {
      isShowCreateFeatureModal: false,
      isShowCreateOfflineSampleModal: false,
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
    },

    showCreateOfflineSampleFormModal() {
      this.isShowCreateOfflineSampleModal = true;
    },
    
    clickCreateOfflineSampleOk() {
      this.isShowCreateOfflineSampleModal = false;

      this.$refs.CreateOfflineSampleForm.submitForm();
    }

  }
};
</script>