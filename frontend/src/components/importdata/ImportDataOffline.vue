<template>

  <div>
    <br/>
    <a-button type="primary" @click="clickCreateFromSql">{{ $t('Offline Import') }}</a-button>
  
    <!-- Create form modal -->
    <div>
      <a-modal v-model:visible="isOpenFormModal" width="1000px" :title="$t('Create From SQL')" @ok="handleOk">
        <LoadDataForm @close="closeModal"></LoadDataForm>
      </a-modal>
    </div>
  
    <br/>
    <OfflineTablesPage></OfflineTablesPage>
  
  </div>
  </template>
    
<script>
  import OfflineTablesPage from '../OfflineTablesPage.vue';
  import LoadDataForm from '@/components/form/LoadDataForm.vue';

  export default {
    components: {
      OfflineTablesPage,
      LoadDataForm
    },
  
    data() {
      return {
        isOpenFormModal: false,
      };
    },
  
    mounted() {
    },
  
    methods: {
  
  
      clickCreateFromSql() {
        this.isOpenFormModal = true;
      },
  
      closeModal() {
        this.isOpenFormModal = false;
      },
  
      handleOk() {
        this.isOpenFormModal = false;
      },
    }
  };
  </script>