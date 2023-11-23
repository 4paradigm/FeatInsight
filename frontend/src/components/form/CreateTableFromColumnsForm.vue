<template>

<div>
  <a-typography-paragraph>
    <pre>{{ $t("Text of introduce create table") }} <a target="blank" href="https://openmldb.ai/docs/">{{$t('OpenMLDB documents')}}</a></pre>
  </a-typography-paragraph>

  <br/>
  <a-form
    :model="formState"
    layout="vertical"
    @submit="submitForm">

    <a-form-item
      :label="$t('Database')"
      :rules="[{ required: true, message: 'Please select database!' }]">
      <a-select show-search v-model:value="formState.db">
        <option v-for="database in databases" :value="database">{{ database }}</option>
      </a-select>
    </a-form-item>

    <a-form-item
      :label="$t('Table Name')"
      :rules="[{ required: true, message: 'Please input table name!' }]">
      <a-input v-model:value="formState.table" 
        placeholder="t1"/>
    </a-form-item>

    <p><span style="color:red;">* </span>{{ $t('Data Columns') }}</p>

    <a-space
      v-for="(column, index) in formState.columns"
      :key="column.id"
      style="display: flex; margin-bottom: 0px"
      align="baseline"
    >
      <a-form-item
        :name="['columns', index, 'name']"
        :rules="{
          required: true,
          message: 'Missing column name',
        }"
      >
        <a-input v-model:value="column.name" placeholder="Column Name" />
      </a-form-item>

      <a-form-item
        :name="['columns', index, 'type']"
        :rules="{
          required: true,
          message: 'Missing column type',
        }"
      >
        <a-select show-search style="width: 200px" v-model:value="column.type">
          <option v-for="columnType in columnTypes" :key="columnType" :value="columnType">{{ columnType }}</option>
        </a-select>
      </a-form-item>
      <MinusCircleOutlined @click="removeColumn(column)" />
    </a-space>

    <a-form-item>
      <a-button type="dashed" block @click="addColumn">
        <PlusOutlined />
        {{ $t('Add Column') }}
      </a-button>
    </a-form-item>

  </a-form>

</div>
</template>
  
<script>
import axios from 'axios'
import { notification } from 'ant-design-vue'
import { MinusCircleOutlined, PlusOutlined } from '@ant-design/icons-vue';

export default {
  components: {
    MinusCircleOutlined,
    PlusOutlined
  },

  data() {
    return {
      databases: [],

      formState: {
        db: '',
        table: '',
        path: '',
        columns: [{
          name: '',
          type: 'Int32',
          id: Date.now(),
        }],
      },

      columnTypes:["Int16", "Int32", "Int64", "Float", "Double", "Timestamp", "Date", "Bool", "String"],

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

    submitForm() {
      const db = this.formState.db;
      const table = this.formState.table;
      const columns = this.formState.columns;

      if (db === "") {
        notification["error"]({
              message: this.$t('Execute Fail'),
              description: "The database should not be empty"
            });
        return;
      }

      if (table === "") {
        notification["error"]({
              message: this.$t('Execute Fail'),
              description: "The table should not be empty"
            });
        return;
      }

      if (columns.length == 0) {
        notification["error"]({
              message: this.$t('Execute Fail'),
              description: "The columns should not be empty"
            });
        return;
      }

      var columnString = "";
      for (var i = 0; i < columns.length; ++i) {
        columnString += columns[i].name + " " + columns[i].type;
        if (i != columns.length - 1) {
          columnString += ", ";
        }
      }

      const sql = `CREATE TABLE ${db}.${table} (${columnString})`;

      axios.post(`/api/sql/online`, {
        "sql": sql
      })
      .then(response => {
        notification["success"]({
              message: this.$t('Execute Success'),
              description: `Success to create table, create sql: ${sql}`
            });

        this.$emit('submitted');
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

    removeColumn(item) {
      const index = this.formState.columns.indexOf(item);
      if (index !== -1) {
        this.formState.columns.splice(index, 1);
      }
    },

    addColumn() {
      this.formState.columns.push({
        name: '',
        type: 'Int32',
        id: Date.now(),
      });
    },

  }
};
</script>