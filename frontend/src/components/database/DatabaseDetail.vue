<template>
<div>
  
  <a-drawer
    v-model:visible="isOpenTableDrawer"
    size="large"
    :title="$t('Table') + $t('Detail')">
    <TableDetail :db="currentDrawerDatabase" :name="currentDrawerTable" :key="currentDrawerTable"></TableDetail>
  </a-drawer>

    <h2>{{ $t('Database') }}: {{ db }}</h2>
    
    <!-- Tables table -->
    <a-table :columns="columns" :data-source="tables">
      <template #table="{ text, record }">
        <a-button type="link" @click="openTableDrawer(record.db, record.table)">{{ record.table }}</a-button>
      </template>
    </a-table>
  
  </div>
</template>

<script>
import axios from 'axios';
import { message } from 'ant-design-vue';
import TableDetail from '@/components/table/TableDetail.vue'

export default {
  components: {
    TableDetail
  },

  props: {
    db: {
      type: String,
      required: true,
    },
  },

  data() {
    return {
      isOpenTableDrawer: false,
      currentDrawerDatabase: "",
      currentDrawerTable: "",
      
      tables: [],
      columns: [
        {
          title: this.$t('Table'),
          dataIndex: 'table',
          key: 'table',
          slots: { customRender: 'table' },
        },
        {
          title: this.$t('Schema'),
          dataIndex: 'schema',
          key: 'schema',
        },
      ],
    };
  },
  mounted() {
    this.initData();
  },
  methods: {
    initData() {
      axios
        .get(`/api/databases/${this.db}/tables`)
        .then((response) => {
          this.tables = response.data;
        })
        .catch((error) => {
          message.error(error.message);
        })
        .finally(() => {
          // You can perform any additional logic here after the request completes.
        });
    },

    openTableDrawer(db, table) {
      this.isOpenTableDrawer = true;
      this.currentDrawerDatabase = db;
      this.currentDrawerTable = table;
    }
  }

};
</script>
