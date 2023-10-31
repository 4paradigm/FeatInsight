<template>

<div>
  <a-modal v-model:visible="isShowExecuteSqlModal" width="1000px" :title="$t('Execute Offline SQL')" @ok="clickExecuteSqlOk">
    <ExecuteSqlForm :isOnline=false ref="ExecuteSqlForm"></ExecuteSqlForm>
  </a-modal>

  <a-modal v-model:visible="isShowCreateOfflineSampleModal" width="1000px" :title="$t('Create Offline Sample')" @ok="clickCreateOfflineSampleOk">
    <CreateOfflineSampleForm ref="CreateOfflineSampleForm"></CreateOfflineSampleForm>
  </a-modal>

  <br/>
  <h1>
    {{ $t('Offline Sample') }}
    &nbsp;&nbsp;&nbsp;<a-button type="primary" @click="showCreateOfflineSampleFormModal">{{ $t('Create Offline Sample') }}</a-button>
  </h1>
  
  <br/>
  <OfflineSamplesData></OfflineSamplesData>
 
</div>
</template>
  
<script>
import OfflineSamplesData from '@/components/offlinesample/OfflineSamplesData.vue';
import OfflineTables from '@/components/table/OfflineTables.vue';
import ExecuteSqlForm from '@/components/form/ExecuteSqlForm.vue'
import CreateOfflineSampleForm from '@/components/form/CreateOfflineSampleForm.vue';

export default {
  components: {
    OfflineSamplesData,
    OfflineTables,
    ExecuteSqlForm,
    CreateOfflineSampleForm
  },

  data() {
    return {
      isShowExecuteSqlModal: false,

      isShowCreateOfflineSampleModal: false,
    }
  },

  methods: {
    showExecuteSqlFormModal() {
      this.isShowExecuteSqlModal = true;
    },
    
    clickExecuteSqlOk() {
      this.isShowExecuteSqlModal = false;

      this.$refs.ExecuteSqlForm.submitForm();
    },

    showCreateOfflineSampleFormModal() {
      this.isShowCreateOfflineSampleModal = true;
    },
    
    clickCreateOfflineSampleOk() {
      this.isShowCreateOfflineSampleModal = false;

      this.$refs.CreateOfflineSampleForm.submitForm();
    }

  }
};
</script>