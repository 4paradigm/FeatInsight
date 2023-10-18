<template>

<div>

  <div>
    <a-typpography>
      <a-typography-paragraph>
        <pre>{{ $t("Text of introduce create database") }} <a target="blank" href="https://openmldb.ai/docs/">{{$t('OpenMLDB documents')}}</a></pre>
      </a-typography-paragraph>
    </a-typpography>
    <br/>

    <!-- Create form -->
    <a-form
      :model="formState"
      layout="vertical"
      @submit="handleSubmit">
 
      <a-form-item
          :label="$t('Name')"
          :rules="[{ required: true, message: 'Please input database name!' }]">
          <a-input v-model:value="formState.name" 
            placeholder="db1"/>
        </a-form-item>

      <a-form-item>
        <a-button type="primary" html-type="submit">{{ $t('Submit') }}</a-button>
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
    handleSubmit() {
      const sql = "CREATE DATABASE IF NOT EXISTS " + this.formState.name;
      axios.post(`/api/sql/execute`, {
        "sql": sql,
        isOnline: true
      })
      .then(response => {
        message.success(`Success to execute SQL: ${sql}`);

        this.$emit('close');
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