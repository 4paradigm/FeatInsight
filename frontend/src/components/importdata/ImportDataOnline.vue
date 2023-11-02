<template>

<div>
  <a-modal v-model:visible="isShowSqlModal" width="1000px" :title="$t('Import From SQL')" @ok="clickSqlModalOk">
    <ImportTableFromSqlForm ref="ImportTableFromSqlForm" :isOnline=true></ImportTableFromSqlForm>
  </a-modal>

  <a-modal v-model:visible="isShowSingleRowModal" width="1000px" :title="$t('Insert Single Row')" @ok="clickSingleRowModalOk">
    <InsertSingleRowForm ref="InsertSingleRowForm"></InsertSingleRowForm>
  </a-modal>

  <a-modal v-model:visible="isShowParquetModal" width="1000px" :title="$t('Import From Parquet')" @ok="clickParquetModalOk">
    <LoadDataInfileForm ref="CreateTableFromParquetForm" :isOnline=true format="Parquet"></LoadDataInfileForm>
  </a-modal>

  <a-modal v-model:visible="isShowCsvModal" width="1000px" :title="$t('Import From CSV')" @ok="clickCsvModalOk">
    <LoadDataInfileForm ref="CreateTableFromCsvForm" :isOnline=true format="CSV"></LoadDataInfileForm>
  </a-modal>

  <a-modal v-model:visible="isShowHiveModal" width="1000px" :title="$t('Import From Hive')" @ok="clickHiveModalOk">
    <LoadDataInfileForm ref="CreateTableFromHiveForm" :isOnline=true format=""></LoadDataInfileForm>
  </a-modal>

  <br/>
  <a-button type="primary" @click="clickSqlButton">{{ $t('Import From SQL') }}</a-button>

  &nbsp;&nbsp;
  <a-dropdown>
      <template #overlay>
        <a-menu>
          <a-menu-item @click="clickSingleRowButton">{{ $t('Insert Single Row') }}</a-menu-item>
          <a-menu-item @click="clickParquetButton">{{ $t('Import From Parquet') }}</a-menu-item>
          <a-menu-item @click="clickCsvButton">{{ $t('Import From CSV') }}</a-menu-item>
          <a-menu-item @click="clickHiveButton">{{ $t('Import From Hive') }}</a-menu-item>
        </a-menu>
      </template>
      <a-button>{{ $t('Other Import Methods') }}</a-button>
  </a-dropdown>

  <br/><br/>
  <OnlineTables></OnlineTables>

</div>
</template>
  
<script>
import OnlineTables from '@/components/table/OnlineTables.vue';
import ImportTableFromSqlForm from '@/components/form/ImportTableFromSqlForm.vue'
import LoadDataInfileForm from '@/components/form/LoadDataInfileForm.vue';
import InsertSingleRowForm from '../form/InsertSingleRowForm.vue';

export default {
  components: {
    OnlineTables,
    ImportTableFromSqlForm,
    LoadDataInfileForm,
    InsertSingleRowForm
  },

  data() {
    return {
      isShowSqlModal: false,
      isShowSingleRowModal: false,
      isShowParquetModal: false,
      isShowCsvModal: false,
      isShowHiveModal: false,
    };
  },

  methods: {
    clickSqlButton() {
      this.isShowSqlModal = true;
    },

    clickSingleRowButton() {
      this.isShowSingleRowModal = true;
    },

    clickParquetButton() {
      this.isShowParquetModal = true;
    },

    clickCsvButton() {
      this.isShowCsvModal = true;
    },

    clickHiveButton() {
      this.isShowHiveModal = true;
    },

    clickSqlModalOk() {
      this.isShowSqlModal = false;
      this.$refs.ImportTableFromSqlForm.submitForm();
    },

    clickSingleRowModalOk() {
      this.isShowSingleRowModal = false;
      this.$refs.CreateTableFromSingleRowForm.submitForm();
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