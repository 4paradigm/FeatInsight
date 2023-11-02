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
      <a-select v-model:value="formState.tableName" @change="updateSchema()">
        <option v-for="tableName in tableNames" :value="tableName">{{ tableName }}</option>
      </a-select>
    </a-form-item>


    <a-form-item
      :label='$t("Schema")'>
      <a-typography>
        <a-typography-paragraph>
          <pre>{{ schemaString }}</pre>
        </a-typography-paragraph>
      </a-typography>
    </a-form-item>

    <p><span style="color:red;">* </span>{{ $t('Row Data') }}</p>

    <a-space
      v-for="(column, index) in columns"
      :key="column.id"
      style="display: flex; margin-bottom: 8px"
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
        <a-select v-model:value="column.type">
          <option v-for="columnType in columnTypes" :key="columnType" :value="columnType">{{ columnType }}</option>
        </a-select>
      </a-form-item>
      <MinusCircleOutlined @click="removeColumn(column)" />
    </a-space>


  </a-form>

</div>
</template>
  
<script>
import axios from 'axios'
import { message } from 'ant-design-vue';

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

      schemaString: "",

      formState: {
        tableName: "SYSTEM_FEATURE_PLATFORM.features",
        columnDataList: []
      },

      columns: [{
        name: '',
        type: ''
      }],

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

          // tobe
          this.updateSchema();
        })
        .catch(error => {
          message.error(error.message);
        })
        .finally(() => {});
    },

    updateSchema() {

      console.log("---------- tobe");
      console.log(this.formState.tableName);

      const db = this.formState.tableName.split(".")[0];
      const table = this.formState.tableName.split(".")[1];

      axios.get(`/api/tables/${db}/${table}/schema`)
        .then(response => {
          this.columns = [];
          this.schemaString = response.data;

          const schemaItems = response.data.split(",");

          for (var i=0; i < schemaItems.length; ++i) {
            const schemaItem = schemaItems[i].split(":");
            this.columns.push({"name": schemaItem[0], "type": schemaItem[1]});
          }

        })
        .catch(error => {
          message.error(error.message);
        })
        .finally(() => {});
      

    },

    submitForm() {
      // TODO: merge options
      const sql = `LOAD DATA INFILE '${this.formState.path}' INTO TABLE ${this.formState.tableName} OPTIONS (format='${this.formState.format}', mode='${this.formState.mode}')`;

      axios.post(`/api/sql/import`, {
        "sql": sql,
        "online": this.formState.isOnlineMode
      })
      .then(response => {
        message.success(`Success to execute SQL: ${sql}`);

        const jobId = response.data;
        this.$router.push(`/offlinejobs/${jobId}/result`);
      })
      .catch(error => {
        if (error.response.data) {
            message.error(error.response.data);
        } else {
            message.error(error.message);
        }
      });
    },
  }
};
</script>