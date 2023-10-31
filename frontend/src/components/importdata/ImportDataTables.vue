<template>

<div>

  <a-modal v-model:visible="isShowColumnsModal" width="1000px" :title="$t('Create Data Table')" @ok="clickColumnsModalOk">
    <CreateTableFromColumnsForm ref="CreateTableFromColumnsForm" @submitted="submittedForm"></CreateTableFromColumnsForm>
  </a-modal>

  <a-modal v-model:visible="isShowSqlModal" width="1000px" :title="$t('Create From SQL')" @ok="clickSqlModalOk">
    <CreateTableFromSqlForm ref="CreateTableFromSqlForm" @submitted="submittedForm"></CreateTableFromSqlForm>
  </a-modal>

  <a-modal v-model:visible="isShowParquetModal" width="1000px" :title="$t('Create From Parquet')" @ok="clickParquetModalOk">
    <CreateTableFromParquetForm ref="CreateTableFromParquetForm"></CreateTableFromParquetForm>
  </a-modal>

  <a-modal v-model:visible="isShowHiveModal" width="1000px" :title="$t('Create From Hive')" @ok="clickHiveModalOk">
    <CreateTableFromHiveForm ref="CreateTableFromHiveForm"></CreateTableFromHiveForm>
  </a-modal>

  <br/>
  <a-button type="primary" @click="clickCreateFromColumnsButton">{{ $t('Create Data Table') }}</a-button>

  &nbsp;&nbsp;
  <a-dropdown>
      <template #overlay>
        <a-menu>
          <a-menu-item @click="clickCreateFromSqlButton">{{ $t('Create From SQL') }}</a-menu-item>
          <a-menu-item @click="clickCreateFromParquetButton">{{ $t('Create From Parquet') }}</a-menu-item>
          <a-menu-item @click="clickCreateFromHiveButton">{{ $t('Create From Hive') }}</a-menu-item>
        </a-menu>
      </template>
      <a-button>{{ $t('Other Create Methods') }}</a-button>
  </a-dropdown>

  <br/><br/>
  <OnlineTables :key="refreshDataKey"></OnlineTables>

</div>
</template>
  
<script>
import OnlineTables from '@/components/table/OnlineTables.vue';
import CreateTableFromSqlForm from '@/components/form/CreateTableFromSqlForm.vue'
import CreateTableFromParquetForm from '@/components/form/CreateTableFromParquetForm.vue';
import CreateTableFromHiveForm from '@/components/form/CreateTableFromHiveForm.vue';
import CreateTableFromColumnsForm from '@/components/form/CreateTableFromColumnsForm.vue';

export default {
  components: {
    OnlineTables,
    CreateTableFromSqlForm,
    CreateTableFromParquetForm,
    CreateTableFromHiveForm,
    CreateTableFromColumnsForm
  },

  data() {
    return {
      isShowColumnsModal: false,
      isShowSqlModal: false,
      isShowParquetModal: false,
      isShowHiveModal: false,

      refreshDataKey: 0
    };
  },

  methods: {

    clickCreateFromColumnsButton() {
      this.isShowColumnsModal = true;
    },

    clickCreateFromSqlButton() {
      this.isShowSqlModal = true;
    },

    clickCreateFromParquetButton() {
      this.isShowParquetModal = true;
    },

    clickCreateFromHiveButton() {
      this.isShowHiveModal = true;
    },

    submittedForm() {
      this.isShowColumnsModal = false;
      this.isShowSqlModal = false;
      this.isShowParquetModal = false;
      this.isShowSqlModal = false;
      this.isShowHiveModal = false;

      // Update data when closing modal
      this.refreshDataKey++;
    },

    clickColumnsModalOk() {
      this.isShowColumnsModal = false;
      this.$refs.CreateTableFromColumnsForm.submitForm();
    },

    clickSqlModalOk() {
      this.isShowSqlModal = false;
      this.$refs.CreateTableFromSqlForm.submitForm();
    },

    clickParquetModalOk() {
      this.isShowParquetModal = false;
      this.$refs.CreateTableFromParquetForm.submitForm();
    },

    clickHiveModalOk() {
      this.isShowHiveModal = false;
      this.$refs.CreateTableFromHiveForm.submitForm();
    },
  }
};
</script>