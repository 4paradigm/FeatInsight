<template>

<div>

  <br/>
  <h1>
    {{ $t('Online Data Tables') }}
    &nbsp;&nbsp;<a-button type="primary" @click="showExcuteSqlFormModal">{{ $t('Execute Online SQL') }}</a-button>
  </h1>

  <div>
    <a-modal v-model:visible="isShowModal" width="1000px" :title="$t('Execute Online SQL')" @ok="clickModalOk" >
      <ExecuteSqlForm :isOnline=true ref="ExecuteSqlForm"></ExecuteSqlForm>
    </a-modal>
  </div>

  <br/>
  <OnlineTables></OnlineTables>

  <FeatureServicesPage></FeatureServicesPage>
 
</div>
</template>
  
<script>
import FeatureServicesPage from '@/components/featureservice/FeatureServicesPage.vue';
import OnlineTables from '@/components/table/OnlineTables.vue';
import ExecuteSqlForm from '@/components/form/ExecuteSqlForm.vue'

export default {
  components: {
    FeatureServicesPage,
    OnlineTables,
    ExecuteSqlForm
  },

  data() {
    return {
      isShowModal: false,
    }
  },

  methods: {
    showExcuteSqlFormModal() {
      this.isShowModal = true;
    },

    clickModalOk() {
      this.isShowModal = false;
      // Submit the form in child component
      this.$refs.ExecuteSqlForm.submitForm();
    }
  },
};
</script>