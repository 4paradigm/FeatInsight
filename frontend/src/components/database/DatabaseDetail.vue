<template>

  <div>
    <br/>
    <h1>{{ $t('Database') }}: {{ db }}</h1>
    <!-- Tables table -->
    <a-table :columns="columns" :data-source="tables">
      <template #table="{ text, record }">
        <router-link :to="`/tables/${record.db}/${record.table}`">{{ text }}</router-link>
      </template>
    </a-table>
  
  </div>
</template>

<script>
import axios from 'axios';
import { message } from 'ant-design-vue';
import { ref, onMounted } from 'vue';

export default {
  props: {
    db: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
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
  },
  mounted() {
    this.initData();
  },
};
</script>
