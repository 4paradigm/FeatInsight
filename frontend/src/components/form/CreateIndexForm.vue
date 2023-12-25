<template>

<div>

  <div>
    <a-typography-paragraph>
      <pre>用户可以创建</pre>
    </a-typography-paragraph>
    <br/>

    <!-- Create form -->
    <a-form
      :model="formState"
      layout="vertical"
      @submit="submitForm">
 
      <a-form-item
        :label="$t('Index Name')"
        :rules="[{ required: true, message: 'Please input index name!' }]">
        <a-input v-model:value="formState.indexName" 
          placeholder="index1"/>
      </a-form-item>

      <a-form-item
        :label="$t('Table Name')"
        :rules="[{ required: true, message: 'Please input index name!' }]">
        <a-input v-model:value="formState.indexName" 
          placeholder="index1"/>
      </a-form-item>

      <a-form-item
        :label="$t('Index Name')"
        :rules="[{ required: true, message: 'Please input index name!' }]">
        <a-input v-model:value="formState.indexName" 
          placeholder="index1"/>
      </a-form-item>

      <a-form-item
        :label="$t('Index Name')"
        :rules="[{ required: true, message: 'Please input index name!' }]">
        <a-input v-model:value="formState.indexName" 
          placeholder="index1"/>
      </a-form-item>

    </a-form>
  </div>

</div>
</template>
  
<script>
import axios from 'axios'
import { notification } from 'ant-design-vue'

export default {
  data() {
    return {
      formState: {
        indexName: '',
        table: '',
        columnNames: '',
        tsColumn: '',
        ttlType: '',
        ttl: ''
      },
    };
  },

  mounted() {
  },

  methods: {
    submitForm() {
      const sql = `CREATE DATABASE ${this.formState.name}`;

      axios.post(`/api/sql/online`, {
        "sql": sql
      })
      .then(response => {
        notification["success"]({
              message: this.$t('Execute Success'),
              description: `Success to create database, create sql: ${sql}`
            });

        this.$emit('submitted');
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
  },
};
</script>