<template>
<div>

  <a-table :dataSource="resultData" :columns="resultColumns"></a-table>

</div>
</template>
  
<script>
import axios from 'axios'
import { message } from 'ant-design-vue';

export default {
  props: {
    sql: {
      type: String,
      required: true,
    }
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
          sql: this.sql
        })
        .then((response) => {
          message.success(`Success to execute SQL: ${this.sql}`);

          if (response.data.length > 0) {
            const columnCount = response.data[0].length;

            this.resultColumns = []
            for (var i = 0; i < columnCount; i++) {
              const columnName = response.data[0][i];
              this.resultColumns.push({
                title: columnName,
                dataIndex: columnName,
                key: columnName
              })
            }

            this.resultData = []
            for (var i = 1; i < response.data.length; i++) {
              const row = response.data[i]
              const rowDataMap = {}

              for (var j = 0; j < columnCount; j++) {
                const columnName = response.data[0][j];
                const columnData = row[j];
                rowDataMap[columnName] = columnData;
              }

              this.resultData.push(rowDataMap);
            }
            
          } else {
            console.log("No result")
          }
        })
        .catch((error) => {
          console.log(error);
          if ('response' in error && 'data' in error.response) {
            message.error(error.response.data);
          } else {
            message.error(error.message);
          }
        });
    }

  }
};
</script>