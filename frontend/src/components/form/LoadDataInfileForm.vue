<template>

<div>
  <a-typography-paragraph>
    <pre>{{ $t("Text of introduce load data infile") }} <a target="blank" href="https://openmldb.ai/docs/zh/main/openmldb_sql/dml/LOAD_DATA_STATEMENT.html">{{$t('OpenMLDB documents')}}</a></pre>
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
      <a-select show-search v-model:value="formState.tableName">
        <option v-for="tableName in tableNames" :value="tableName">{{ tableName }}</option>
      </a-select>
    </a-form-item>

    <a-form-item
      :label="$t('Path')"
      :rules="[{ required: true, message: 'Please input path!' }]">
      <a-input v-model:value="formState.path" 
        placeholder="file:///tmp/data.csv or hive://db1.t1"/>
    </a-form-item>

    <a-form-item
        :label="$t('File Format')"
        :rules="[{ required: true, message: 'Please choose file format!' }]">
        <a-select show-search v-model:value="formState.format">
          <option v-for="format in formatOptions" :key="format.id" :value="format.name">{{ format.name}}</option>
        </a-select>
    </a-form-item>

    <a-form-item
        :label="$t('Write Mode')"
        :rules="[{ required: true, message: 'Please choose write mode!' }]">
        <a-select show-search v-model:value="formState.mode">
          <option v-for="mode in writeModeOptions" :key="mode.id" :value="mode.name">{{ mode.name}}</option>
        </a-select>
    </a-form-item>

    <a-form-item
      :label="$t('Options')">
      <a-input v-model:value="formState.options" 
        placeholder=""/>
    </a-form-item>

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

      formatOptions:[
        {id: '', name: ''},
        {id: 'CSV', name: 'CSV'},
        {id: 'Parquet', name: 'Parquet'}
      ],

      writeModeOptions:[
        {id: 'append', name: 'append'},
        {id: 'overwrite', name: 'overwrite'},
        {id: 'error_if_exists', name: 'error_if_exists'}
      ],

      formState: {
        isOnlineMode: true,
        tableName: "",
        path: "",
        format: "",
        mode: "",
        options: ""
      },
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

    submitForm() {
      // TODO: merge options
      const sql = `LOAD DATA INFILE '${this.formState.path}' INTO TABLE ${this.formState.tableName} OPTIONS (format='${this.formState.format}', mode='${this.formState.mode}')`;

      axios.post(`/api/sql/import`, {
        "sql": sql,
        "online": this.formState.isOnlineMode
      })
      .then(response => {
        notification["success"]({
              message: this.$t('Execute Success'),
              description: `Success to submit load data job, the load data SQL: ${sql}`
            });

        const jobId = response.data;
        this.$router.push(`/offlinejobs/${jobId}/result`);
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