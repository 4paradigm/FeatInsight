<template>

<div>

  <h1>
    {{ $t('Offline Data Tables') }}
    &nbsp;&nbsp;<a-button type="primary" @click="showExcuteSqlFormModal">{{ $t('Execute Offline SQL') }}</a-button>
  </h1>
  
  
  <div>
    <a-modal v-model:visible="isOpenExecuteSqlModal" width="1000px" :title="$t('Execute Offline SQL')" @ok="handleOk">
      <ExecuteSql></ExecuteSql>
    </a-modal>
  </div>

  <br/>
  <OfflineTablesPage></OfflineTablesPage>

  <TrainingSetsPage></TrainingSetsPage>
 
</div>
</template>
  
<script>
import TrainingSetsPage from './TrainingSetsPage.vue';
import OfflineTablesPage from './OfflineTablesPage.vue';
import ExecuteSql from '@/components/ExecuteSql.vue'

export default {
  components: {
    TrainingSetsPage,
    OfflineTablesPage,
    ExecuteSql
  },

  data() {
    return {
      isOpenExecuteSqlModal: false,
    }
  },

  mounted() {
    this.initData();
  },

  methods: {
    initData() {
    },

    handleOk() {
      this.isOpenExecuteSqlModal = false;
    },

    showExcuteSqlFormModal() {
      this.isOpenExecuteSqlModal = true;
    }
  },
};
</script>