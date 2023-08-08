<template>

<div>

  <br />
  <div>
    <h1>{{ $t('Create Database') }}</h1>
    <a-form
      :model="createDatabaseFormState"
      name="basic"
      :label-col="{ span: 8 }"
      :wrapper-col="{ span: 16 }"
      @submit="handleSubmitCreateDatabase">
      <a-form-item
        :label="$t('Database')"
        :rules="[{ required: true, message: 'Please input database!' }]">
        <a-input v-model:value="createDatabaseFormState.database" />
      </a-form-item>

      <a-form-item :wrapper-col="{ offset: 8, span: 16 }">
        <a-button type="primary" html-type="submit">{{ $t('Create') }}</a-button>
      </a-form-item>

    </a-form>
  </div>

  <br />
  <div>
    <h1>{{ $t('Map Hive Table') }}</h1>
    <a-form
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
    <h1>{{ $t('Load Hive Data') }}</h1>
    <a-form
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

      <a-form-item :wrapper-col="{ offset: 8, span: 16 }">
        <a-button type="primary" html-type="submit">{{ $t('Submit') }}</a-button>
        &nbsp;&nbsp;<a-button type="primary" @click="clickDisplayMoreOptions()">{{ $t('Display More Options') }}</a-button>
      </a-form-item>
    </a-form>
  </div>

  <div v-if="isDisplayImportSql">
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

  <a-button type="primary" @click="clickDisplayImportSql()">{{ $t('Display Import SQL') }}</a-button>

  &nbsp;&nbsp;<a-button type="primary" @click="clickDisplayTables()">{{ $t('Display Tables') }}</a-button>

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
      const sql = "LOAD DATA INFILE 'hive://" + this.loadHiveDataFormState.hivePath + "' INTO TABLE " + this.loadHiveDataFormState.openmldbTable + " OPTIONS(deep_copy=" + this.loadHiveDataFormState.isDeepCopy + ", mode='append')"
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