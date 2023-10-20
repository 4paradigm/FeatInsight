<template>

  <div>
    <br/>
    <a-button type="primary" @click="clickImportFromSql">{{ $t('Import From SQL') }}</a-button>

    &nbsp;&nbsp;
    <a-dropdown>
        <template #overlay>
          <a-menu>
            <a-menu-item key="1"><a-button type="link">{{ $t('Import From Parquet') }}</a-button></a-menu-item>
            <a-menu-item key="2"><a-button type="link">{{ $t('Import From Hive') }}</a-button></a-menu-item>
          </a-menu>
        </template>
        <a-button>{{ $t('Other Create Methods') }}</a-button>
    </a-dropdown>
    <!-- Create form modal -->
    <div>
      <a-modal v-model:visible="isOpenFormModal" width="1000px" :title="$t('Import From SQL')">
        <template #footer>
          <a-button @click="handleCancel">Cancel</a-button>
        </template>
        <LoadDataForm @close="closeModal"></LoadDataForm>
      </a-modal>
    </div>
  
    <br/>
    <OfflineTables></OfflineTables>
  
  </div>
  </template>
    
<script>
  import OfflineTables from '../tablecontent/OfflineTables.vue';
  import LoadDataForm from '@/components/form/LoadDataForm.vue';

  export default {
    components: {
      OfflineTables,
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
  
      clickImportFromSql() {
        this.isOpenFormModal = true;
      },
  
      closeModal() {
        this.isOpenFormModal = false;
      },
  
      handleCancel() {
        this.isOpenFormModal = false;
      },
    }
  };
  </script>