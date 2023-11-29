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
      :label="$t('Execute Mode')">
      <a-switch :disabled=true v-model:checked="formState.isOnlineMode" :checked-children="$t('Online')" :un-checked-children="$t('Offline')" />
    </a-form-item>

    <a-form-item
      :label="$t('SQL')"
      :rules="[{ required: true, message: 'Please input SQL!' }]">
      <a-input v-model:value="formState.sql" 
        placeholder="'INSERT INTO db1.t1 VALUES (1)' or 'LOAD DATA INFILE 'file:///data/' INTO TABLE db1.t1'"/>
    </a-form-item>
  </a-form>

</div>
</template>
  
<script>
import axios from 'axios'
import { notification } from 'ant-design-vue'

export default {
  props: {
    isOnline: {
      type: Boolean,
      default: true,
    }
  },

  data() {
    return {
      formState: {
        isOnlineMode: true,
        sql: ''
      },
    }
  },

  mounted() {
    this.formState.isOnlineMode = this.isOnline;
  },

  methods: {

    submitForm() {
      if (this.formState.sql.toLowerCase().startsWith("insert")) {
        axios.post(`/api/sql/online`, {
          "sql": this.formState.sql,
        })
        .then(response => {
          notification["success"]({
              message: this.$t('Execute Success'),
              description: `Success to execute SQL: ${this.formState.sql}`
            });
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

      } else if (this.formState.sql.toLowerCase().startsWith("load")) {
        axios.post(`/api/sql/import`, {
          "sql": this.formState.sql,
          "online": this.formState.isOnlineMode
        })
        .then(response => {
          notification["success"]({
                message: this.$t('Success Fail'),
                description: `Success to execute SQL: ${this.formState.sql}`
              });

          const jobId = response.data;
          this.$router.push(`/offlinejobs/${jobId}/result`);
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

      } else {
        notification["error"]({
          message: this.$t('Execute Fail'),
          description: "Only support SQL like 'INSERT' or 'LOAD DATA'"
        });
      }
    }

  }
};
</script>