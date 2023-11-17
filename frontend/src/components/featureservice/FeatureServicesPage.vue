<template>
<div>
  <a-modal v-model:visible="isShowCreateFeatureServiceModal" width="1000px" :title="$t('Create Feature Service')" @ok="clickCreateFeatureServiceOk">
    <CreateFeatureServiceForm ref="CreateFeatureServiceForm"></CreateFeatureServiceForm>
  </a-modal>

  <br/>
  <h1>
    {{ $t('All Feature Services') }}<a-button type="link" @click="showCreateFeatureServiceFormModal"><PlusCircleOutlined /></a-button>
  </h1>

  <FeatureServicesData />

</div>
</template>
    
<script>
import FeatureServicesData from '@/components/featureservice/FeatureServicesData.vue';
import { PlusCircleOutlined } from '@ant-design/icons-vue';
import CreateFeatureServiceForm from '@/components/form/CreateFeatureServiceForm.vue';

export default {
  components: {
    FeatureServicesData,
    CreateFeatureServiceForm,
    PlusCircleOutlined
  },

  data() {
    return {
      isShowCreateFeatureServiceModal: false
    }
  },

  methods: {
    showCreateFeatureServiceFormModal() {
      this.isShowCreateFeatureServiceModal = true;
    },

    clickCreateFeatureServiceOk() {
      this.isShowCreateFeatureServiceModal = false;

      this.$refs.CreateFeatureServiceForm.submitForm();
    }
  }

};
</script>