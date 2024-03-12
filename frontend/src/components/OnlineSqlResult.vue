<template>
  <div>
    <a-table
      :dataSource="resultData"
      :columns="resultColumns"
      :scroll="{ x: 'max-content' }"
    />
  </div>
</template>

<script>
import axios from 'axios';
import { notification } from 'ant-design-vue';

export default {
  props: {
    sql: {
      type: String,
      required: true,
    },
  },

  data() {
    return {
      resultColumns: [],
      resultData: [],
    };
  },

  mounted() {
    this.initData();
  },

  methods: {
    initData() {
      axios
        .post(`/api/sql/online`, {
          sql: this.sql,
        })
        .then((response) => {
          if (response.data.length > 0) {
            const columnCount = response.data[0].length;

            this.resultColumns = [];
            for (var i = 0; i < columnCount; i++) {
              const columnName = response.data[0][i];
              this.resultColumns.push({
                title: columnName,
                dataIndex: columnName,
                key: columnName,
              });
            }

            this.resultData = [];
            for (var i = 1; i < response.data.length; i++) {
              const row = response.data[i];
              const rowDataMap = {};

              for (var j = 0; j < columnCount; j++) {
                const columnName = response.data[0][j];
                const columnData = row[j];
                rowDataMap[columnName] = columnData;
              }

              this.resultData.push(rowDataMap);
            }
          } else {
          }
        })
        .catch((error) => {
          if ('response' in error && 'data' in error.response) {
            notification['error']({
              message: this.$t('Execute Fail'),
              description: error.response.data,
            });
          } else {
            notification['error']({
              message: this.$t('Execute Fail'),
              description: error.message,
            });
          }
        });
    },
  },
};
</script>
