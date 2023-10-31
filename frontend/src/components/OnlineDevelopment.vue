<template>

<div>
  <a-modal v-model:visible="isShowExecuteSqlModal" width="1000px" :title="$t('Execute Online SQL')" @ok="clickExecuteSqlOk" >
    <ExecuteSqlForm :isOnline=true ref="ExecuteSqlForm"></ExecuteSqlForm>
  </a-modal>

  <a-modal v-model:visible="isShowCreateFeatureServiceModal" width="1000px" :title="$t('Create Feature Service')" @ok="clickCreateFeatureServiceOk">
    <CreateFeatureServiceForm ref="CreateFeatureServiceForm"></CreateFeatureServiceForm>
  </a-modal>

  <br/>
  <h1>
    {{ $t('Feature Services') }}
    &nbsp;&nbsp;<a-button type="primary" @click="showCreateFeatureServiceFormModal">{{ $t('Create Feature Service') }}</a-button>
  </h1>

  <br/>
  <FeatureServicesData></FeatureServicesData>
 
</div>
</template>
  
<script>
import FeatureServicesData from '@/components/featureservice/FeatureServicesData.vue';
import OnlineTables from '@/components/table/OnlineTables.vue';
import ExecuteSqlForm from '@/components/form/ExecuteSqlForm.vue'
import CreateFeatureServiceForm from '@/components/form/CreateFeatureServiceForm.vue';


export default {
  components: {
    FeatureServicesData,
    OnlineTables,
    ExecuteSqlForm,
    CreateFeatureServiceForm
  },

  data() {
    return {
      isShowExecuteSqlModal: false,

      isShowCreateFeatureServiceModal: false
    }
  },

  methods: {
    showExcuteSqlFormModal() {
      this.isShowExecuteSqlModal = true;
    },

    clickExecuteSqlOk() {
      this.isShowExecuteSqlModal = false;

      this.$refs.ExecuteSqlForm.submitForm();
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