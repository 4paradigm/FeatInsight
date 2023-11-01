<template>
<div>

  <a-modal v-model:visible="isShowCreateFeatureModal" width="1000px" :title="$t('Create Feature')" >
    <template #footer>
      <a-button @click="handleCancel">Cancel</a-button>
    </template>
    <CreateFeatureForm @submitted="submittedCreateFeatureForm"></CreateFeatureForm>
  </a-modal>

  <a-modal v-model:visible="isShowCreateFeatureServiceModal" width="1000px" :title="$t('Create Feature Service')" @ok="clickCreateFeatureServiceOk">
    <CreateFeatureServiceForm ref="CreateFeatureServiceForm"></CreateFeatureServiceForm>
  </a-modal>

  <br/>
  <a-typography>
    <a-typography-title :level="2">{{ $t('Online Scenario') }}</a-typography-title>
    <a-typography-paragraph>
      <blockquote>
        在线场景是上线特征服务，使用在线数据提供硬实时的在线特征抽取接口，用户首先<a-button type="text" @click="clickCreateFeature">{{ $t('Create Feature') }}</a-button>，然后选择特征来<a-button type="text" @click="showCreateFeatureServiceFormModal">{{ $t('Create Feature Service') }}</a-button>。
      </blockquote>
    </a-typography-paragraph>
  </a-typography>

  <br/>
  <h1>
    {{ $t('All Feature Services') }}
  </h1>
  <FeatureServicesData></FeatureServicesData>
 
</div>
</template>
  
<script>
import FeatureServicesData from '@/components/featureservice/FeatureServicesData.vue';
import OnlineTables from '@/components/table/OnlineTables.vue';
import CreateFeatureServiceForm from '@/components/form/CreateFeatureServiceForm.vue';
import CreateFeatureForm from '@/components/form/CreateFeatureForm.vue'

export default {
  components: {
    FeatureServicesData,
    OnlineTables,
    CreateFeatureServiceForm,
    CreateFeatureForm
  },

  data() {
    return {
      isShowCreateFeatureModal: false,
      isShowCreateFeatureServiceModal: false
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

    showCreateFeatureServiceFormModal() {
      this.isShowCreateFeatureServiceModal = true;
    },

    clickCreateFeatureServiceOk() {
      this.isShowCreateFeatureServiceModal = false;

      this.$refs.CreateFeatureServiceForm.submitForm();
    },
  }
};
</script>