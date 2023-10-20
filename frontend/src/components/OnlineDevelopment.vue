<template>

<div>

  <br/>
  <h1>
    {{ $t('Online Data Tables') }}
    &nbsp;&nbsp;<a-button type="primary" @click="showExcuteSqlFormModal">{{ $t('Execute Online SQL') }}</a-button>
  </h1>

  <div>
    <a-modal v-model:visible="isOpenExecuteSqlModal" width="1000px" :title="$t('Execute Online SQL')" @ok="handleOk">
      <ExecuteSqlForm></ExecuteSqlForm>
    </a-modal>
  </div>

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
      isOpenExecuteSqlModal: false,
    }
  },

  mounted() {
  },

  methods: {
    handleOk() {
      this.isOpenExecuteSqlModal = false;
    },

    showExcuteSqlFormModal() {
      this.isOpenExecuteSqlModal = true;
    }
  },
};
</script>