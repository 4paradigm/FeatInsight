<template>

  <div>
    <!-- Create form SQL modal -->
    <a-modal v-model:visible="isShowSqlModal" width="1000px" :title="$t('Import From SQL')" @ok="clickSqlModalOk">
      <ImportTableFromSqlForm ref="ImportTableFromSqlForm" @submitted="submittedForm"></ImportTableFromSqlForm>
    </a-modal>
  
    <a-modal v-model:visible="isShowParquetModal" width="1000px" :title="$t('Create From Parquet')" @ok="clickParquetModalOk">
      <LoadDataInfileForm ref="CreateTableFromParquetForm" @submitted="submittedForm"></LoadDataInfileForm>
    </a-modal>
  
    <a-modal v-model:visible="isShowCsvModal" width="1000px" :title="$t('Create From CSV')" @ok="clickCsvModalOk">
      <LoadDataInfileForm ref="CreateTableFromCsvForm" @submitted="submittedForm"></LoadDataInfileForm>
    </a-modal>
  
    <a-modal v-model:visible="isShowHiveModal" width="1000px" :title="$t('Create From Hive')" @ok="clickHiveModalOk">
      <LoadDataInfileForm ref="CreateTableFromHiveForm" @submitted="submittedForm"></LoadDataInfileForm>
    </a-modal>
  
    <br/>
    <a-button type="primary" @click="clickImportFromSqlButton">{{ $t('Import From SQL') }}</a-button>
  
    &nbsp;&nbsp;
    <a-dropdown>
        <template #overlay>
          <a-menu>
            <a-menu-item @click="clickCreateFromParquetButton">{{ $t('Import From Parquet') }}</a-menu-item>
            <a-menu-item @click="clickCreateFromCsvButton">{{ $t('Import From CSV') }}</a-menu-item>
            <a-menu-item @click="clickCreateFromHiveButton">{{ $t('Import From Hive') }}</a-menu-item>
          </a-menu>
        </template>
        <a-button>{{ $t('Other Import Methods') }}</a-button>
    </a-dropdown>
  
    <OnlineTables :key="refreshDataKey"></OnlineTables>
  
  </div>
  </template>
    
  <script>
  import OnlineTables from '@/components/table/OnlineTables.vue';
  import ImportTableFromSqlForm from '@/components/form/ImportTableFromSqlForm.vue'
  import LoadDataInfileForm from '@/components/form/LoadDataInfileForm.vue';
  
  export default {
    components: {
      OnlineTables,
      ImportTableFromSqlForm,
      LoadDataInfileForm
    },
  
    data() {
      return {
        isShowSqlModal: false,
        isShowParquetModal: false,
        isShowCsvModal: false,
        isShowHiveModal: false,
  
        refreshDataKey: 0
      };
    },
  
    methods: {
  
      clickImportFromSqlButton() {
        this.isShowSqlModal = true;
      },
  
      clickCreateFromParquetButton() {
        this.isShowParquetModal = true;
      },
  
      clickCreateFromCsvButton() {
        this.isShowCsvModal = true;
      },
  
      clickCreateFromHiveButton() {
        this.isShowHiveModal = true;
      },
  
      submittedForm() {
        this.isShowSqlModal = false;
        this.isShowParquetModal = false;
        this.isShowSqlModal = false;
        this.isShowHiveModal = false;
  
        // Update data when closing modal
        this.refreshDataKey++;
      },
  
      clickSqlModalOk() {
        this.isShowSqlModal = false;
        this.$refs.ImportTableFromSqlForm.submitForm();
      },
  
      clickParquetModalOk() {
        this.isShowParquetModal = false;
        this.$refs.CreateTableFromParquetForm.submitForm();
      },
  
      clickCsvModalOk() {
        this.isShowCsvModal = false;
        this.$refs.CreateTableFromCsvForm.submitForm();
      },
  
      clickHiveModalOk() {
        this.isShowHiveModal = false;
        this.$refs.CreateTableFromHiveForm.submitForm();
      },
    }
  };
  </script>