<template>

<div>
  <a-typography-paragraph>
    <pre>{{ $t("`Text of introduce load data infile`") }} <a target="blank" href="https://openmldb.ai/docs/zh/main/openmldb_sql/dml/LOAD_DATA_STATEMENT.html">{{$t('OpenMLDB documents')}}</a></pre>
  </a-typography-paragraph>

  <br/>
  <a-form
    :model="formState"
    layout="vertical"
    @submit="submitForm">

    <a-form-item
      :label="$t('Path')"
      :rules="[{ required: true, message: 'Please input path!' }]">
      <a-input v-model:value="formState.path" 
        placeholder="file:///tmp/data.csv or hive://db1.t1"/>
    </a-form-item>

    <a-form-item
      :label="$t('Table Name')"
      :rules="[{ required: true, message: 'Please input table name!' }]">
      <a-input v-model:value="formState.tableName" 
        placeholder="db1.t1"/>
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
import { message } from 'ant-design-vue';

export default {
  data() {
    return {
      formState: {
        path: "",
        tableName: "",
        options: ""
      },
    };
  },

  methods: {
    submitForm() {
      const sql = `LOAD DATA INFILE ${this.formState.path} INTO TABLE ${this.formState.tableName} OPTIONS (${this.formState.options})`;

      axios.post(`/api/sql/execute`, {
        "sql": sql
      })
      .then(response => {
        message.success(`Success to execute SQL: ${sql}`);

        this.$emit('submitted');
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