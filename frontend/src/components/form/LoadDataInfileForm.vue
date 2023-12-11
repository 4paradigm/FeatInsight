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

    <a-button type="dashed" @click="switchShowMoreOptions">{{ $t('More Options') }}</a-button>

    <div v-if="isShowMoreOptions">
      <br/>
      <a-form-item 
        :label="$t('Spark Config')">
        <a-tooltip>
          <template #title>Spark config like 'spark.executor.memory=2g;spark.executor.cores=2'</template>
          <a-input v-model:value="formState.sparkConfig"></a-input>
        </a-tooltip>
      </a-form-item>

      <a-form-item
        :label="$t('File Format')" >
          <a-select show-search v-model:value="formState.format">
            <option v-for="format in formatOptions" :key="format.id" :value="format.name">{{ format.name}}</option>
          </a-select>
      </a-form-item>

      <a-form-item 
          :label="$t('Delimiter')">
          <a-input v-model:value="formState.delimiter"></a-input>
      </a-form-item>

      <a-form-item 
          :label="$t('Header')">
          <a-select show-search v-model:value="formState.header">
            <option v-for="item in booleanOptions" :key="item.id" :value="item.name">{{ item.name}}</option>
          </a-select>
      </a-form-item>

      <a-form-item 
          :label="$t('Null Value')">
          <a-input v-model:value="formState.nullValue"></a-input>
      </a-form-item>

      <a-form-item 
          :label="$t('Quote')">
          <a-input v-model:value="formState.quote"></a-input>
      </a-form-item>

      <a-form-item
        :label="$t('Mode')">
        <a-select show-search v-model:value="formState.mode">
          <option v-for="mode in modeOptions" :key="mode.id" :value="mode.name">{{ mode.name}}</option>
        </a-select>
      </a-form-item>

      <a-form-item 
          :label="$t('Deep Copy')">
          <a-select show-search v-model:value="formState.deepCopy">
            <option v-for="item in booleanOptions" :key="item.id" :value="item.name">{{ item.name}}</option>
          </a-select>
      </a-form-item>

      <a-form-item 
          :label="$t('Load Mode')">
          <a-select show-search v-model:value="formState.loadMode">
            <option v-for="item in loadModeOptions" :key="item.id" :value="item.name">{{ item.name}}</option>
          </a-select>
      </a-form-item>

      <a-form-item 
          :label="$t('Thread')">
          <a-input v-model:value="formState.thread"></a-input>
      </a-form-item>

      <!-- TODO: not support yet 
      <a-form-item 
          :label="$t('Writer Type')">
          <a-select show-search v-model:value="formState.writerType">
            <option v-for="item in writerTypeOptions" :key="item.id" :value="item.name">{{ item.name}}</option>
          </a-select>
      </a-form-item>
      -->
      
      <a-form-item 
          :label="$t('Filter SQL')">
          <a-input v-model:value="formState.sql"></a-input>
      </a-form-item>


    </div>

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
      default: "csv",
    }
  },

  data() {
    return {
      tableNames: [],

      formatOptions:[
        {id: 'csv', name: 'csv'},
        {id: 'parquet', name: 'parquet'}
      ],

      modeOptions:[
        {id: 'append', name: 'append'},
        {id: 'overwrite', name: 'overwrite'},
        {id: 'error_if_exists', name: 'error_if_exists'}
      ],

      booleanOptions:[
        {id: 'true', name: 'true'},
        {id: 'false', name: 'false'}
      ],

      loadModeOptions:[
        {id: 'cluster', name: 'cluster'},
        {id: 'local', name: 'local'}
      ],


      formState: {
        isOnlineMode: true,
        tableName: "",
        path: "",
        sparkConfig: "",
        format: "csv",
        delimiter: ",",
        header: "true",
        nullValue: "null",
        quote: '"',
        mode: "append",
        deepCopy: "true",
        loadMode: "cluster",
        thread: "1",
        sql: ""
      },

      isShowMoreOptions: false,

    };
  },

  mounted() {
    if (this.isOnline != null) {
      this.formState.isOnlineMode = this.isOnline;
    }
    
    if (this.format != null && this.format != "") {
      this.formState.format = this.format;
    }
    
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
          var errorMessage = error.message;
          if (error.response && error.response.data) {
            errorMessage = error.response.data;
          }
          notification["error"]({
            message: this.$t('Execute Fail'),
            description: errorMessage
          });
        });
    },

    submitForm() {
      var sql = "";

      /* Use single sql with spark config.
      if (this.formState.sparkConfig !== "") {
        sql = `SET @@spark_config='${}';`
      }
      */

      var optionString = "OPTIONS (";
      optionString += `format='${this.formState.format}'`;
      optionString += `, mode='${this.formState.mode}'`;
      optionString += `, delimiter='${this.formState.delimiter}'`;
      optionString += `, header=${this.formState.header}`;
      optionString += `, null_value='${this.formState.nullValue}'`;
      optionString += `, quote='${this.formState.quote}'`;
      optionString += `, deep_copy=${this.formState.deepCopy}`;
      optionString += `, load_mode='${this.formState.loadMode}'`;
      optionString += `, thread=${this.formState.thread}`;
      if (this.formState.sql !== "") {
        optionString += `, sql='${this.formState.sql}'`;
      }
      optionString += ")";

      sql += `LOAD DATA INFILE '${this.formState.path}' INTO TABLE ${this.formState.tableName} ${optionString}`;

      axios.post(`/api/sql/import`, {
        "sql": sql,
        "online": this.formState.isOnlineMode,
        "sparkConfig": this.formState.sparkConfig
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
        var errorMessage = error.message;
        if (error.response && error.response.data) {
          errorMessage = error.response.data;
        }
        notification["error"]({
          message: this.$t('Execute Fail'),
          description: errorMessage
        });
      });
    },

    switchShowMoreOptions() {
      if (this.isShowMoreOptions) {
        this.isShowMoreOptions = false;
      } else {
        this.isShowMoreOptions = true;
      }
    },

  }
};
</script>