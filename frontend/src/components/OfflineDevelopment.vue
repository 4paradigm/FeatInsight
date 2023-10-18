<template>

<div>

  <br/>
  <h1>
    {{ $t('Offline Data Tables') }}
    &nbsp;&nbsp;<a-button type="primary" @click="showExcuteSqlFormModal">{{ $t('Execute Offline SQL') }}</a-button>
  </h1>
  
  
  <div>
    <a-modal v-model:visible="isOpenExecuteSqlModal" width="1000px" :title="$t('Execute Offline SQL')" @ok="handleOk">
      <ExecuteSqlForm></ExecuteSqlForm>
    </a-modal>
  </div>

  <br/>
  <OfflineTables></OfflineTables>

  <TrainingSetsPage></TrainingSetsPage>
 
</div>
</template>
  
<script>
import TrainingSetsPage from './TrainingSetsPage.vue';
import OfflineTables from './tablecontent/OfflineTables.vue';
import ExecuteSqlForm from '@/components/form/ExecuteSqlForm.vue'

export default {
  components: {
    TrainingSetsPage,
    OfflineTables,
    ExecuteSqlForm
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