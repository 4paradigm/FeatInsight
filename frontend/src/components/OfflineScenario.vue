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
        {{ $t('Text of Offline Scenario') }}
      </blockquote>
    </a-typography-paragraph>
  </a-typography>

  <br/>
  <h1>
    {{ $t('Offline Samples') }}<a-button type="link" @click="showCreateOfflineSampleFormModal"><PlusCircleOutlined /></a-button>
  </h1>
  <OfflineSamplesData></OfflineSamplesData>
 
</div>
</template>
  
<script>
import { PlusCircleOutlined } from '@ant-design/icons-vue';
import OfflineSamplesData from '@/components/offlinesample/OfflineSamplesData.vue'
import OfflineTables from '@/components/table/OfflineTables.vue'
import CreateOfflineSampleForm from '@/components/form/CreateOfflineSampleForm.vue'
import CreateFeatureForm from '@/components/form/CreateFeatureForm.vue'

export default {
  components: {
    PlusCircleOutlined,
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