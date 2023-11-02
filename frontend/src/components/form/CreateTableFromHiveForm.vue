<template>

<div>
  <a-typography-paragraph>
    <pre>{{ $t("Text of introduce create table from hive") }} <a target="blank" href="https://openmldb.ai/docs/">{{$t('OpenMLDB documents')}}</a></pre>
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
      :label="$t('Output Table Name')"
      :rules="[{ required: true, message: 'Please input output table name!' }]">
      <a-input v-model:value="formState.outputTable" 
        placeholder="t1"/>
    </a-form-item>

    <a-form-item
      :label="$t('Hive Path')"
      :rules="[{ required: true, message: 'Please input hive path!' }]">
      <a-input v-model:value="formState.hivePath" 
        placeholder="hive:///db1/t1"/>
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
        outputTable: '',
        hivePath: '',
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
      const sql = `CREATE TABLE ${this.formState.db}.${this.formState.outputTable} LIKE HIVE '${this.formState.hivePath}'`;

      axios.post(`/api/sql/online`, {
        "sql": sql
      })
      .then(response => {
        message.success(`Success to execute SQL: ${sql}`);

        this.$router.push(`/tables/${this.formState.db}/${this.formState.outputTable}/createresult`);
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