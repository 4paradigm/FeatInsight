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
        placeholder="hive://db1.t1"/>
    </a-form-item>
  </a-form>

</div>
</template>
  
<script>
import axios from 'axios'
import { notification } from 'ant-design-vue'

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
      const sql = `CREATE TABLE ${this.formState.db}.${this.formState.outputTable} LIKE HIVE '${this.formState.hivePath}'`;

      axios.post(`/api/sql/online`, {
        "sql": sql
      })
      .then(response => {
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

      notification["success"]({
        message: this.$t('Execute Success'),
        description: `Success to submit create table job, create sql: ${sql}`
      });

      this.$router.push(`/tables/${this.formState.db}/${this.formState.outputTable}/createresult`);
    },

  }
};
</script>