<template>

<div>
  <br/>
  <a-button type="primary" @click="clickCreateFromSql">{{ $t('Create From SQL') }}</a-button>

  &nbsp;&nbsp;
  <a-dropdown>
      <template #overlay>
        <a-menu>
          <a-menu-item key="1"><a-button type="link">{{ $t('Create From Parquet') }}</a-button></a-menu-item>
          <a-menu-item key="2"><a-button type="link">{{ $t('Create From Hive') }}</a-button></a-menu-item>
        </a-menu>
      </template>
      <a-button>{{ $t('Other Create Methods') }}</a-button>
  </a-dropdown>

  <!-- Create form modal -->
  <div>
    <a-modal v-model:visible="isOpenFormModal" width="1000px" :title="$t('Create From SQL')" @ok="handleOk">
      <CreateTableFromSqlForm @close="closeModal"></CreateTableFromSqlForm>
    </a-modal>
  </div>

  <OnlineTablesPage></OnlineTablesPage>

</div>
</template>
  
<script>
import OnlineTablesPage from '../OnlineTablesPage.vue';
import CreateTableFromSqlForm from '@/components/form/CreateTableFromSqlForm.vue'

export default {
  components: {
    OnlineTablesPage,
    CreateTableFromSqlForm
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