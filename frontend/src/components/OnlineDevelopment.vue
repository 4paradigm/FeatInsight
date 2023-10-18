<template>

<div>

  <br/>
  <h1>
    {{ $t('Online Data Tables') }}
    &nbsp;&nbsp;<a-button type="primary" @click="showExcuteSqlFormModal">{{ $t('Execute Online SQL') }}</a-button>
  </h1>

  <div>
    <a-modal v-model:visible="isOpenExecuteSqlModal" width="1000px" :title="$t('Execute Online SQL')" @ok="handleOk">
      <ExecuteSql></ExecuteSql>
    </a-modal>
  </div>

  <OnlineTablesPage></OnlineTablesPage>

  <FeatureServicesPage></FeatureServicesPage>
 
</div>
</template>
  
<script>
import FeatureServicesPage from './FeatureServicesPage.vue';
import OnlineTablesPage from './OnlineTablesPage.vue';
import ExecuteSql from '@/components/ExecuteSql.vue'

export default {
  components: {
    FeatureServicesPage,
    OnlineTablesPage,
    ExecuteSql
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