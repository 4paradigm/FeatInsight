<template>

<div>
  <!-- Tables table -->
  <a-input v-model:value="searchText" :placeholder="$t('Search')" @change="handleSearch" />
  <br/><br/>

  <a-table :columns="columns" :data-source="searchFilteredTables">
    <template #database="{ text, record }">
      <router-link :to="`/databases/${record.db}`">{{ text }}</router-link>
    </template>    
    <template #table="{ text, record }">
      <router-link :to="`/tables/${record.db}/${record.table}`">{{ text }}</router-link>
    </template>
  </a-table>

  <div>
    <a-modal v-model:visible="isOpenPreviewTableModal" width="1000px" :title="$t('Preview Data')" @ok="handleOk">
      <h3>{{$t('Preview Data')}} ({{$t('Limit')}} 10 {{$t('Rows')}})</h3>
      <p v-html="previewTableContent"></p>
    </a-modal>
  </div>


</div>
</template>
  
<script>
import axios from 'axios'
import { notification } from 'ant-design-vue'

export default {
  components: { 
  },

  data() {
    return {
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
        title: this.$t('Schema'),
        dataIndex: 'schema',
        key: 'schema',
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
          notification["error"]({
              message: this.$t('Execute Fail'),
              description: error.message
            });
        })
        .finally(() => {});
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
          console.log(error);
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

    handleOk() {
      this.isOpenPreviewTableModal = false;
    },

  }
};
</script>