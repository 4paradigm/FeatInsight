<template>

<div>

  <!--
  <a-typography-text mark>{{ $t('Notice CLTV Text') }}</a-typography-text>
  -->
  

  <br /><br/>
  <div>
    <h1>{{ $t('Data Import') }}</h1>
    <a-form
      :model="createDatabaseFormState"
      name="basic"
      :label-col="{ span: 8 }"
      :wrapper-col="{ span: 16 }"
      @submit="handleSubmitCreateDatabase">
      <a-form-item v-if="showSelect"
        :label="$t('Database')"
        :rules="[{ required: true, message: 'Please input database!' }]">
        <a-select v-model:value="formState.db">
          <option v-for="database in databases" :value="database">{{ database }}</option>
        </a-select>
      </a-form-item>

      <a-form-item v-if="showSelect" :wrapper-col="{ offset: 8, span: 16 }">
        <a-button type="primary" @click="toggleShowField">{{ $t('Create New Database') }}</a-button>
      </a-form-item>

      <a-form-item v-if="showInput"
        :label="$t('Database')"
        :rules="[{ required: true, message: 'Please input database!' }]">
        <a-input placeholder="Enter database name" v-model:value="createDatabaseFormState.database" /> 
      </a-form-item>

      <a-form-item v-if="showInput" :wrapper-col="{ offset: 8, span: 16 }">
        <a-button type="primary" html-type="submit">{{ $t('Create') }}</a-button>
        &nbsp;&nbsp;
        <a-button type="primary" @click="toggleShowField">{{ $t("Return to Choose Database") }}</a-button>
      </a-form-item>

      <a-form-item 
        :label="$t('Execution Mode')"
        :rules="[{ required: true, message: 'Please choose execution mode!' }]">
        <a-select v-model:value="executionMode">
          <option value="online">{{ $t("online") }} </option>
          <option value="offline">{{ $t("offline") }} </option>
        </a-select>
      </a-form-item>

      <a-form-item v-if="executionMode=='online'" :wrapper-col="{ offset: 8, span: 16 }">
        <a-button type="primary"> Test </a-button>
      </a-form-item>

    </a-form>
  </div>

  <br />
  <div>
    <h1 v-if="executionMode=='offline'">{{ $t('Load Offline Data') }}</h1>
    <a-form v-if="executionMode=='offline'"
      :model="importOfflineTableFromState"
      name="basic"
      :label-col="{ span: 8 }"
      :wrapper-col="{ span: 16 }"
      @submit="handleLoadHiveData">
      <a-form-item
        :label="$t('Table Name')"
        :rules="[{ required: true, message: 'Please input hive table!' }]">
        <a-input v-model:value="importOfflineTableFromState.tableName" />
      </a-form-item>

      <a-form-item
        :label="$t('Path')"
        :rules="[{ required: true, message: 'Please input data path!' }]">
        <a-input v-model:value="importOfflineTableFromState.path" />
      </a-form-item>

        <!-- Only display when click the button -->
        <div v-if="isDisplayMoreOptions">
          <a-form-item
            label="SQL"
            :rules="[{ required: false, message: 'Please input import SQL!' }]">
            <a-input v-model:value="importOfflineTableFromState.sql" />
          </a-form-item>
        </div>

      <a-form-item  :wrapper-col="{ offset: 8, span: 16 }">
        <a-button type="primary" html-type="submit">{{ $t('Submit') }}</a-button>
        &nbsp;&nbsp;<a-button type="primary" @click="clickDisplayMoreOptions()">{{ $t('Display More Options') }}</a-button>
      </a-form-item>
    </a-form>
  </div>

  <br />
  <div>
    <h1 v-if="executionMode=='online'">{{ $t('Map Hive Table') }}</h1>
    <a-form v-if="executionMode=='online'"
      :model="importHiveTableFormState"
      name="basic"
      :label-col="{ span: 8 }"
      :wrapper-col="{ span: 16 }"
      @submit="handleImportHiveTable">
      <a-form-item
        :label="'Hive ' + $t('Table Name')"
        :rules="[{ required: true, message: 'Please input SQL!' }]">
        <a-input v-model:value="importHiveTableFormState.hivePath" />
      </a-form-item>

      <a-form-item
        :label="'OpenMLDB ' + $t('Table Name')"
        :rules="[{ required: true, message: 'Please input SQL!' }]">
        <a-input v-model:value="importHiveTableFormState.openmldbTable" />
      </a-form-item>

      <a-form-item :wrapper-col="{ offset: 8, span: 16 }">
        <a-button type="primary" html-type="submit">{{ $t('Submit') }}</a-button>
      </a-form-item>
    </a-form>
  </div>

  <br />
  <div>
    <h1 v-if="executionMode=='online'">{{ $t('Load Hive Data') }}</h1>
    <a-form v-if="executionMode=='online'"
      :model="loadHiveDataFormState"
      name="basic"
      :label-col="{ span: 8 }"
      :wrapper-col="{ span: 16 }"
      @submit="handleLoadHiveData">
      <a-form-item
        :label="'Hive ' + $t('Table Name')"
        :rules="[{ required: true, message: 'Please input hive table!' }]">
        <a-input v-model:value="loadHiveDataFormState.hivePath" />
      </a-form-item>

      <a-form-item
          :label="'OpenMLDB ' + $t('Table Name')"
          :rules="[{ required: true, message: 'Please input table!' }]">
          <a-select v-model:value="loadHiveDataFormState.openmldbTable">
            <option v-for="tableItem in tables" :value="tableItem.db + '.' + tableItem.table">{{ tableItem.db }}.{{ tableItem.table }}</option>
          </a-select>
        </a-form-item>

        <!-- Only display when click the button -->
        <div v-if="isDisplayMoreOptions">
          <a-form-item 
            :label="$t('Deep Copy')"
            :rules="[{ required: false, message: 'Please select is deep copy!' }]">
            <a-select v-model="loadHiveDataFormState.isDeepCopy">
              <a-select-option :value="true">True</a-select-option>
              <a-select-option :value="false">False</a-select-option>
            </a-select>
          </a-form-item>

          <a-form-item
            label="SQL"
            :rules="[{ required: false, message: 'Please input import SQL!' }]">
            <a-input v-model:value="loadHiveDataFormState.sql" />
          </a-form-item>
        </div>

      <a-form-item  :wrapper-col="{ offset: 8, span: 16 }">
        <a-button type="primary" html-type="submit">{{ $t('Submit') }}</a-button>
        &nbsp;&nbsp;<a-button type="primary" @click="clickDisplayMoreOptions()">{{ $t('Display More Options') }}</a-button>
      </a-form-item>
    </a-form>
  </div>

  <div v-if="isDisplayImportSql && executionMode=='online'">
  <br />
  <div>
    <h1>{{ $t('Import Table') }}</h1>
    <a-typography>
      <a-typography-paragraph>
        <p>Use SQL to create or delete the databases or tables.</p>
        <p>eg. CREATE TABLE db1.user (name varchar, age int)</p>
        <p>eg. CREATE TABLE db1.t1 LIKE HIVE 'hive://hive_db.t1'</p>
        <p>eg. LOAD DATA INFILE 'hive://db1.t1' INTO TABLE db1.t1 OPTIONS(deep_copy=false, mode='append')</p>
      </a-typography-paragraph>
    </a-typography>
    <!-- Create form -->
    <a-form
      :model="formState"
      name="basic"
      :label-col="{ span: 8 }"
      :wrapper-col="{ span: 16 }"
      @submit="handleSubmit">
      <a-form-item
        label="SQL"
        :rules="[{ required: true, message: 'Please input SQL!' }]">
        <a-input v-model:value="formState.sql" />
      </a-form-item>

      <a-form-item :wrapper-col="{ offset: 8, span: 16 }">
        <a-button type="primary" html-type="submit">{{ $t('Submit') }}</a-button>
      </a-form-item>

    </a-form>
  </div>
  </div>

  <div v-if="executionMode=='online'">
  <a-button type="primary" @click="clickDisplayImportSql()">{{ $t('Display Import SQL') }}</a-button>
  </div>
  <br />
  <div>
  <a-button type="primary" @click="clickDisplayTables()">{{ $t('Display Tables') }}</a-button>
  </div>

  <div v-if="isDisplayTable">
    <TablesPage></TablesPage>
  </div>

</div>
</template>
  
<script>
import axios from 'axios'
import { message } from 'ant-design-vue';

import TablesPage from './TablesPage.vue';

export default {
  components: {
    TablesPage
  },

  data() {
    return {
      isDisplayTable: false,

      isDisplayImportSql: false,

      isDisplayMoreOptions: false,

      tables: [],

      databases: [],

      showInput: false,

      showSelect: true,

      test: 'online',

      executionMode: '',

      createDatabaseFormState: {
        database: '',
      },

      formState: {
        sql: '',
      },

      importHiveTableFormState: {
        hivePath: '',
        openmldbTable: ''
      },

      importOfflineTableFromState: {
        Table: '',
        path: '',
        sql:'',
      },

      loadHiveDataFormState: {
        hivePath: '',
        openmldbTable: '',
        isDeepCopy: true,
        sql: ''
      },
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
        })
        .catch(error => {
          message.error(error.message);
        })
        .finally(() => {});
      axios.get(`/api/databases`)
        .then(response => {
          this.databases = response.data;
        })
        .catch(error => {
          message.error(error.message);
        })
        .finally(() => {});
    },

    handleSubmitCreateDatabase() {
      const sql = "CREATE DATABASE IF NOT EXISTS " + this.createDatabaseFormState.database;
      axios.post(`/api/sql/execute`, {
        "sql": sql
      })
      .then(response => {
        message.success(`Success to execute SQL: ${sql}`);
      })
      .catch(error => {
        if (error.response.data) {
            message.error(error.response.data);
          } else {
            message.error(error.message);
          }
      });
    },

    toggleShowField(){
      this.showInput = !this.showInput;
      this.showSelect = !this.showSelect;

    },

    handleSubmit() {
      axios.post(`/api/sql/execute`, {
        "sql": this.formState.sql,
      })
      .then(response => {
        message.success(`Success to execute SQL: ${this.formState.sql}`);
        this.initData();
      })
      .catch(error => {
        if (error.response.data) {
            message.error(error.response.data);
          } else {
            message.error(error.message);
          }
      });
    },

    handleImportHiveTable() {
      const sql = "CREATE TABLE " + this.importHiveTableFormState.openmldbTable + " LIKE HIVE 'hive://" + this.importHiveTableFormState.hivePath + "'"
      axios.post(`/api/sql/execute`, {
        "sql": sql
      })
      .then(response => {
        message.success(`Success to execute SQL: ${sql}`);
        this.initData();
      })
      .catch(error => {
        if (error.response.data) {
            message.error(error.response.data);
          } else {
            message.error(error.message);
          }
      });
    },

    handleLoadHiveData() {
      let sql = "LOAD DATA INFILE 'hive://" + this.loadHiveDataFormState.hivePath + "' INTO TABLE " + this.loadHiveDataFormState.openmldbTable + " OPTIONS(deep_copy=" + this.loadHiveDataFormState.isDeepCopy + ", mode='append')"
      if (this.loadHiveDataFormState.sql !== "") {
        sql = "LOAD DATA INFILE 'hive://" + this.loadHiveDataFormState.hivePath + "' INTO TABLE " + this.loadHiveDataFormState.openmldbTable + " OPTIONS(deep_copy=" + this.loadHiveDataFormState.isDeepCopy + ", mode='append', sql='" + this.loadHiveDataFormState.sql + "')"
      }
      
      axios.post(`/api/sql/execute`, {
        "sql": sql
      })
      .then(response => {
        message.success(`Success to submit SQL: ${sql}`);
        this.initData();
      })
      .catch(error => {
        if (error.response.data) {
            message.error(error.response.data);
          } else {
            message.error(error.message);
          }
      });
    },

    clickDisplayImportSql() {
      if (this.isDisplayImportSql) {
        this.isDisplayImportSql = false;
      } else {
        this.isDisplayImportSql = true;
      }
    },

    clickDisplayTables() {
      if (this.isDisplayTable) {
        this.isDisplayTable = false;
      } else {
        this.isDisplayTable = true;
      }
    },

    clickDisplayMoreOptions() {
      if (this.isDisplayMoreOptions) {
        this.isDisplayMoreOptions = false;
      } else {
        this.isDisplayMoreOptions = true;
      }
    }

  },
};
</script>
