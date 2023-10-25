<template>

<div>
  <a-typography-paragraph>
    <pre>{{ $t("Text of introduce import table from sql") }} <a target="blank" href="https://openmldb.ai/docs/zh/main/openmldb_sql/dml/LOAD_DATA_STATEMENT.html">{{$t('OpenMLDB documents')}}</a></pre>
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
        placeholder="LOAD DATA INFILE 'file:///data/' INTO TABLE db1.t1"/>
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
        sql: '',
      },
    };
  },

  methods: {
    submitForm() {
      axios.post(`/api/sql/execute`, {
        "sql": this.formState.sql
      })
      .then(response => {
        message.success(`Success to execute SQL: ${this.formState.sql}`);

        // TODO: get job id
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