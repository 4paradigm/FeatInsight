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
        placeholder="LOAD DATA INFILE 'file:///data/' INTO TABLE db1.t1 OPTIONS (format='parquet', mode='append')"/>
    </a-form-item>
  </a-form>

</div>
</template>
  
<script>
import axios from 'axios'
import { message } from 'ant-design-vue';
import { notification } from 'ant-design-vue';

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
      console.log(this.formState.sql);
      if (this.formState.sql.toLowerCase().startsWith("insert")) {
        axios.post(`/api/sql/online`, {
          "sql": this.formState.sql,
        })
        .then(response => {
          message.success(`Success to execute SQL: ${this.formState.sql}`);
        })
        .catch(error => {
          if (error.response.data) {
              message.error(error.response.data);
          } else {
              message.error(error.message);
          }
        });

      } else if (this.formState.sql.toLowerCase().startsWith("load")) {
        axios.post(`/api/sql/import`, {
          "sql": this.formState.sql,
          "online": this.formState.isOnlineMode
        })
        .then(response => {
          message.success(`Success to execute SQL: ${this.formState.sql}`);

          const jobId = response.data;
          this.$router.push(`/offlinejobs/${jobId}/result`);
        })
        .catch(error => {
          if (error.response.data) {
              message.error(error.response.data);
          } else {
              message.error(error.message);
          }
        });

      } else {
        notification["error"]({
            message: this.$t('Exeucte Fail'),
            description: "Only support SQL like 'INSERT' or 'LOAD DATA'"
          });
      }
    }

  }
};
</script>