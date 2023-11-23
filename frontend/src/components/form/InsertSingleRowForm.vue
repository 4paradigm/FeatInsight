<template>

<div>
  <a-typography-paragraph>
    <pre>用户可以根据选择的数据表的 Schema，直接插入单行数据到在线数据库中。</pre>
  </a-typography-paragraph>

  <br/>
  <a-form
    :model="formState"
    layout="vertical"
    @submit="submitForm">

    <a-form-item
      :label="$t('Execute Mode')">
      <a-switch :disabled=true v-model:checked="formState.isOnlineMode" :checked-children="$t('Online')" :un-checked-children="$t('Offline')" />
    </a-form-item>

    <a-form-item
      :label="$t('Table Name')"
      :rules="[{ required: true, message: 'Please input table name!' }]">
      <a-select show-search v-model:value="formState.tableName" @change="updateSchema()">
        <option v-for="tableName in tableNames" :value="tableName">{{ tableName }}</option>
      </a-select>
    </a-form-item>

    <p><span style="color:red;">* </span>{{ $t('Row Data') }}</p>

    <a-space
      v-for="(column, index) in columns"
      :key="column.name"
      style="display: flex; margin-bottom: 0px"
      align="baseline"
    >
      <p style="min-width: 200px">{{ column.name }} ({{ column.type }}):</p>
    
      <a-form-item
        :name="['columns', index, 'name']">
        <a-input v-model:value="formState.columnDataList[index]" placeholder="" />
      </a-form-item>
    </a-space>

  </a-form>

</div>
</template>
  
<script>
import axios from 'axios'
import { notification } from 'ant-design-vue'

export default {
  props: {
    isOnline: {
      type: Boolean,
      default: true,
    },
    format: {
      type: String,
      default: "Parquet",
    }
  },

  data() {
    return {
      tableNames: [],

      formState: {
        tableName: "",
        columnDataList: []
      },

      // Example: [{"name": "", "type": ""}]
      columns: [],

    };
  },

  mounted() {
    this.formState.isOnlineMode = this.isOnline;
    this.formState.format = this.format;

    this.initData();
  },

  methods: {
    initData() {
      axios.get(`/api/tables`)
        .then(response => {
          this.tableNames = [];
          response.data.forEach((table) => {
            this.tableNames.push(`${table.db}.${table.table}`);
          });
        })
        .catch(error => {
          notification["error"]({
              message: this.$t('Execute Fail'),
              description: error.message
            });
        })
        .finally(() => {});
    },

    updateSchema() {
      const db = this.formState.tableName.split(".")[0];
      const table = this.formState.tableName.split(".")[1];

      axios.get(`/api/tables/${db}/${table}/schema`)
        .then(response => {
          const columnList = response.data.split(",").map(obj => {
            return {
              "name": obj.split(":")[0],
              "type": obj.split(":")[1]
            }
          })

          this.columns = [...columnList]
        })
        .catch(error => {
          notification["error"]({
              message: this.$t('Execute Fail'),
              description: error.message
            });
        })
        .finally(() => {});
    },

    submitForm() {
      var valueString = ``;

      for (var i=0; i < this.formState.columnDataList.length; ++i) {
        const columnValue = this.formState.columnDataList[i];

        if (this.columns[i].type === "string") {
          valueString += `'${columnValue}'`;
        } else {
          valueString += columnValue
        }

        if (i != this.formState.columnDataList.length-1) {
          valueString += ", "
        }

      }

      const sql = `INSERT INTO ${this.formState.tableName} VALUES (${valueString})`;

      axios.post(`/api/sql/online`, {
        "sql": sql
      })
      .then(response => {
        notification["success"]({message: 
          `${this.$t('Success to insert data')}: ${this.formState.columnDataList}`
        });
      })
      .catch(error => {
        if (error.response.data) {
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
  }
};
</script>