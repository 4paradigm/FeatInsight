<template>
<div>

  <a-drawer
    v-model:visible="isOpenDatabaseDrawer"
    size="large"
    :title="$t('Database') + $t('Detail')">
    <DatabaseDetail :db="currentDrawerDatabase" :key="currentDrawerDatabase"></DatabaseDetail>
  </a-drawer>

  <a-table :columns="databaseColumns" :data-source="databases">
    <template #database="{ text, record }">
      <a-button type="link" @click="openDatabaseDrawer(text)">{{ text }}</a-button>
    </template>
    <template v-slot:custom="scope">
      <a-popconfirm
          title="Sure to delete?"
          @confirm="handleDelete(scope.record)">
        <a>{{ $t('Delete') }}</a>
      </a-popconfirm>
    </template>
  </a-table>

</div>
</template>
  
<script>
import axios from 'axios'
import DatabaseDetail from '@/components/database/DatabaseDetail.vue'
import { notification } from 'ant-design-vue'

export default {
  components: {
    DatabaseDetail
  },

  data() {
    return {
      isOpenDatabaseDrawer: false,
      currentDrawerDatabase: "",

      databases: [],

      databaseColumns: [{
        title: this.$t('Database'),
        slots: { customRender: 'database' }
      },
      {
        title: this.$t('Actions'),
        key: 'actions',
        slots: { customRender: 'custom' },
      }],
    };
  },

  mounted() {
    this.initData();
  },

  methods: {
    initData() {
      axios.get(`/api/databases`)
        .then(response => {
          this.databases = response.data;
        })
        .catch(error => {
          notification["error"]({
              message: this.$t('Execute Fail'),
              description: error.message
            });
        })
        .finally(() => {});
    },

    openDatabaseDrawer(db) {
      this.isOpenDatabaseDrawer = true;
      this.currentDrawerDatabase = db;
    },

    handleDelete(database) {
      if (database === "SYSTEM_FEATURE_PLATFORM") {
        notification["error"]({
            message: this.$t('Delete Fail'),
            description: "The system database is not allowed to delete"
        });
        return;
      }
      
      axios.delete(`/api/databases/${database}`)
        .then(response => {
          notification["success"]({
              message: this.$t('Execute Success'),
              description: `Success to delete database: ${database}`
            });

          this.initData();
        })
        .catch(error => {
          notification["error"]({
              message: this.$t('Execute Fail'),
              description: error.message
            });
        });
    },

  }
};
</script>