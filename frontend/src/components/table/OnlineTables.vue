<template>
<div>
  
  <a-drawer
    v-model:visible="isOpenDatabaseDrawer"
    size="large"
    :title="$t('Database') + $t('Detail')">
    <DatabaseDetail :db="currentDrawerDatabase" :key="currentDrawerDatabase"></DatabaseDetail>
  </a-drawer>

  <a-drawer
    v-model:visible="isOpenTableDrawer"
    size="large"
    :title="$t('Table') + $t('Detail')">
    <TableDetail :db="currentDrawerDatabase" :name="currentDrawerTable" :key="currentDrawerDatabase+currentDrawerTable"></TableDetail>
  </a-drawer>

  <!-- Tables table -->
  <a-input v-model:value="searchText" :placeholder="$t('Search')" @change="handleSearch" />
  <br/><br/>

  <a-table :columns="columns" :data-source="searchFilteredTables">
    <template #database="{ text, record }">
      <a-button type="link" @click="openDatabaseDrawer(record.db)">{{ record.db }}</a-button>
    </template>    
    <template #table="{ text, record }">
      <a-button type="link" @click="openTableDrawer(record.db, record.table)">{{ record.table }}</a-button>
    </template>
    <template v-slot:custom="scope">
      <a-popconfirm
          title="Sure to delete?"
          @confirm="handleDelete(scope.record.db, scope.record.table)">
        <a>{{ $t('Delete') }}</a>
      </a-popconfirm>
    </template>
  </a-table>

</div>
</template>
  
<script>
import axios from 'axios'
import { notification } from 'ant-design-vue'
import DatabaseDetail from '@/components/database/DatabaseDetail.vue'
import TableDetail from '@/components/table/TableDetail.vue'

export default {
  components: {
    DatabaseDetail,
    TableDetail
  },

  data() {
    return {
      isOpenDatabaseDrawer: false,
      currentDrawerDatabase: "",
      isOpenTableDrawer: false,
      currentDrawerTable: "",

      searchText: "",
      searchFilteredTables: [],

      tables: [],

      isOpenPreviewTableModal: false,
      previewTableContent: "",
      
      columns: [{
        title: this.$t('Database'),
        dataIndex: 'db',
        key: 'db',
        slots: { customRender: 'database' }
      },
      {
        title: this.$t('Table'),
        dataIndex: 'table',
        key: 'table',
        slots: { customRender: 'table' }
      },
      {
        title: this.$t('Actions'),
        key: 'actions',
        slots: { customRender: 'custom' },
      }]
    };
  },

  mounted() {
    this.initData();
  },

  methods: {
    initData() {
      axios.get(`/api/tables`)
        .then(response => {
          this.tables = response.data;
          this.searchFilteredTables = this.tables;
        })
        .catch(error => {
          var errorMessage = error.message;
          if (error.response && error.response.data) {
            errorMessage = error.response.data;
          }
          notification["error"]({
            message: this.$t('Execute Fail'),
            description: errorMessage
          });
        });
    },

    matchSearch(item) {
        return item.table.toLowerCase().includes(this.searchText.toLowerCase())
          || item.db.toLowerCase().includes(this.searchText.toLowerCase());
    },

    handleSearch() {
      if (this.searchText === "") {
        this.searchFilteredTables = this.tables;
      } else {
        this.searchFilteredTables = this.tables.filter((item) => this.matchSearch(item));
      }
    },

    previewTableData(db, table) {
      const sql = "SELECT * FROM " + db + "." + table + " LIMIT 10";

      axios
        .post(`/api/sql/online`, {
          sql: sql
        })
        .then((response) => {
          notification["success"]({
              message: this.$t('Execute Success'),
              description: "Success to preview table (limit 10 rows)"
            });

          this.isOpenPreviewTableModal = true;
          this.previewTableContent = response.data.replace(/\n/g, '<br>');
        })
        .catch((error) => {
          if ('response' in error && 'data' in error.response) {
            notification["error"]({
              message: this.$t('Execute Fail'),
              description: error.response.data
            });
          } else {
            notification["error"]({
              message: this.$t('Execute Fail'),
              description: error.message
            });
          }
        });

    },

    handleCancel() {
      this.isOpenPreviewTableModal = false;
    },

    openDatabaseDrawer(db) {
      this.isOpenDatabaseDrawer = true;
      this.currentDrawerDatabase = db;
    },

    openTableDrawer(db, table) {
      this.isOpenTableDrawer = true;
      this.currentDrawerDatabase = db;
      this.currentDrawerTable = table;
    },

    handleDelete(db, table) {
      if (db === "SYSTEM_FEATURE_PLATFORM") {
        notification["error"]({
            message: this.$t('Delete Fail'),
            description: "The tables in system database are not allowed to delete"
        });
        return;
      }

    axios.delete(`/api/tables/${db}/${table}`)
      .then(response => {
        notification["success"]({
              message: this.$t('Execute Success'),
              description: `Success to delete table: ${table}`
            });

        this.initData();
      })
      .catch(error => {
        var errorMessage = error.message;
        if (error.response && error.response.data) {
          errorMessage = error.response.data;
        }
        notification["error"]({
          message: this.$t('Execute Fail'),
          description: errorMessage
        });
      });
    },

  }
};
</script>