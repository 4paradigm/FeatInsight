<template>

<div>

  <div>
    <a-typpography>
      <a-typography-paragraph>
        <pre>{{ $t("Text of introduce create table from SQL") }} <a target="blank" href="https://openmldb.ai/docs/">{{$t('OpenMLDB documents')}}</a></pre>
      </a-typography-paragraph>
    </a-typpography>
    <br/>

    <!-- Create form -->
    <a-form
      :model="formState"
      layout="vertical"
      @submit="handleSubmit">
 
      <a-form-item
          :label="$t('SQL')"
          :rules="[{ required: true, message: 'Please input SQL!' }]">
          <a-input v-model:value="formState.sql" 
            placeholder="CREATE TABLE t1 (name string, age int)"/>
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
        sql: '',
      },
    };
  },

  mounted() {
  },

  methods: {
    handleSubmit() {
      axios.post(`/api/sql/execute`, {
        "sql": this.formState.sql,
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