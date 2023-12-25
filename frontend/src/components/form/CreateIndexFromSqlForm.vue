<template>

<div>
  <a-typography-paragraph>
    <pre>{{ $t("Text of introduce create table from sql") }} <a target="blank" href="https://openmldb.ai/docs/">{{$t('OpenMLDB documents')}}</a></pre>
  </a-typography-paragraph>

  <br/>
  <a-form
    :model="formState"
    layout="vertical"
    @submit="submitForm">

    <a-form-item
      :label="$t('SQL')"
      :rules="[{ required: true, message: 'Please input SQL!' }]">
      <a-input v-model:value="formState.sql" 
        placeholder="CREATE TABLE db1.t1 (name string, age int)"/>
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
      formState: {
        sql: '',
      },
    };
  },

  methods: {
    submitForm() {
      axios.post(`/api/sql/online`, {
        "sql": this.formState.sql
      })
      .then(response => {
        notification["success"]({
          message: this.$t('Execute Success'),
          description: `Success to execute SQL: ${this.formState.sql}`
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
  }
};
</script>