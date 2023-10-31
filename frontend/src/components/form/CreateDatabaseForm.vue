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
import { message } from 'ant-design-vue';

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
  },
};
</script>