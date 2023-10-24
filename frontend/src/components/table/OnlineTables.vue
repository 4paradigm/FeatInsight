<template>

<div>
  <!-- Tables table -->
  <br/>
  <a-input v-model:value="searchText" :placeholder="$t('Search')" @change="handleSearch" />
  <br/><br/>

  <a-table :columns="columns" :data-source="searchFilteredTables">
    <template #database="{ text, record }">
      <router-link :to="`/databases/${record.db}`">{{ text }}</router-link>
    </template>    
    <template #table="{ text, record }">
      <router-link :to="`/tables/${record.db}/${record.table}`">{{ text }}</router-link>
    </template>
    <template #action="{ text, record }">
      <a-button type="default" @click="previewTableData(record.db, record.table)">{{ $t('Preview Data') }}</a-button>
    </template>
  </a-table>

  <div>
    <a-modal v-model:visible="isOpenPreviewTableModal" width="1000px" :title="$t('Preview Data')" >
      <template #footer>
          <a-button @click="handleCancel">Cancel</a-button>
      </template>
      <h3>{{$t('Preview Data')}} ({{$t('Limit')}} 10 {{$t('Rows')}})</h3>
      <p v-html="previewTableContent"></p>
    </a-modal>
  </div>
</div>
</template>
  
<script>
import axios from 'axios'
import { message } from 'ant-design-vue';

export default {
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
      }, {
        title: this.$t('Actions'),
        key: 'actions',
        slots: { customRender: 'action' },
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
          message.error(error.message);
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
        .post(`/api/sql/execute`, {
          sql: sql,
          isOnline: true
        })
        .then((response) => {
          message.success(`Success to execute SQL: ${sql}`);

          this.isOpenPreviewTableModal = true;
          this.previewTableContent = response.data.replace(/\n/g, '<br>');
        })
        .catch((error) => {
          console.log(error);
          if ('response' in error && 'data' in error.response) {
            message.error(error.response.data);
          } else {
            message.error(error.message);
          }
        });

    },

    handleCancel() {
      this.isOpenPreviewTableModal = false;
    },

  }
};
</script>