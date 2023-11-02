<template>

<div>
  <a-typography-paragraph>
    <pre>{{ $t("Text of introduce create table from parquet") }} <a target="blank" href="https://openmldb.ai/docs/">{{$t('OpenMLDB documents')}}</a></pre>
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

    <a-form-item
      :label="$t('Path')"
      :rules="[{ required: true, message: 'Please input path!' }]">
      <a-input v-model:value="formState.path" 
        placeholder="file:///tmp/t1_parquet/"/>
    </a-form-item>
  </a-form>

</div>
</template>
  
<script>
import axios from 'axios'
import { message } from 'ant-design-vue';

export default {
  data() {
    return {
      databases: [],

      formState: {
        db: '',
        table: '',
        path: '',
      },
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
          message.error(error.message);
        })
        .finally(() => {});
    },

    submitForm() {
      message.success(`Create table from parquet may take 1 minute, please wait`);

      const sql = `CREATE TABLE ${this.formState.db}.${this.formState.table} LIKE PARQUET '${this.formState.path}'`;

      axios.post(`/api/sql/online`, {
        "sql": sql
      })
      .then(response => {
        message.success(`Success to execute SQL: ${sql}`);

        this.$router.push(`/tables/${this.formState.db}/${this.formState.table}/createresult`);
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