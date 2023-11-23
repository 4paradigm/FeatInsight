<template>

<div>

  <div>
    <a-typography-paragraph>
      <pre>{{ $t("Text of introduce create database") }} <a target="blank" href="https://openmldb.ai/docs/">{{$t('OpenMLDB documents')}}</a></pre>
    </a-typography-paragraph>
    <br/>

    <!-- Create form -->
    <a-form
      :model="formState"
      layout="vertical"
      @submit="submitForm">
 
      <a-form-item
          :label="$t('Name')"
          :rules="[{ required: true, message: 'Please input database name!' }]">
          <a-input v-model:value="formState.name" 
            placeholder="db1"/>
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
        name: '',
      },
    };
  },

  mounted() {
  },

  methods: {
    submitForm() {
      const sql = "CREATE DATABASE IF NOT EXISTS " + this.formState.name;
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
  },
};
</script>