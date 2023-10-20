<template>

<div>

  <br/>
  <h1>
    {{ $t('Offline Data Tables') }}
    &nbsp;&nbsp;<a-button type="primary" @click="showExcuteSqlFormModal">{{ $t('Execute Offline SQL') }}</a-button>
  </h1>
  
  
  <div>
    <a-modal v-model:visible="isOpenExecuteSqlModal" width="1000px" :title="$t('Execute Offline SQL')" @ok="handleOk">
      <template #footer>
          <a-button @click="handleCancel">Cancel</a-button>
        </template>
      <ExecuteSqlForm></ExecuteSqlForm>
    </a-modal>
  </div>

  <br/>
  <OfflineTables></OfflineTables>

  <OfflineSamplesPage></OfflineSamplesPage>
 
</div>
</template>
  
<script>
import OfflineSamplesPage from './OfflineSamplesPage.vue';
import OfflineTables from './tablecontent/OfflineTables.vue';
import ExecuteSqlForm from '@/components/form/ExecuteSqlForm.vue'

export default {
  components: {
    OfflineSamplesPage,
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