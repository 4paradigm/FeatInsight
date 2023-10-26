<template>

<div>

  <br/>
  <h1>
    {{ $t('Offline Data Tables') }}
    &nbsp;&nbsp;<a-button type="primary" @click="showExcuteSqlFormModal">{{ $t('Execute Offline SQL') }}</a-button>
  </h1>
  
  
  <div>
    <a-modal v-model:visible="isShowModal" width="1000px" :title="$t('Execute Offline SQL')" @ok="clickModalOk">
      <ExecuteSqlForm :isOnline=false ref="ExecuteSqlForm"></ExecuteSqlForm>
    </a-modal>
  </div>

  <br/>
  <OfflineTables></OfflineTables>

  <OfflineSamplesPage></OfflineSamplesPage>
 
</div>
</template>
  
<script>
import OfflineSamplesPage from '@/components/offlinesample/OfflineSamplesPage.vue';
import OfflineTables from './table/OfflineTables.vue';
import ExecuteSqlForm from '@/components/form/ExecuteSqlForm.vue'

export default {
  components: {
    OfflineSamplesPage,
    OfflineTables,
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