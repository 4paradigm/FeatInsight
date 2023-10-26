<template>

<div>

  <div>
    <a-typography-paragraph>
      <pre>{{ $t("Text of introduce execute sql") }} <a target="blank" href="https://openmldb.ai/docs/zh/main/openmldb_sql/index.html">{{$t('OpenMLDB documents')}}</a></pre>
    </a-typography-paragraph>
    <br/>

    <!-- Create form -->
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
        :rules="[{ required: true, message: 'Please input sql!' }]">
        <a-input
          v-model:value="formState.sql"
          placeholder="Please input SQL" />
      </a-form-item>

    </a-form>
  </div>


  <div>
    <a-modal v-model:visible="isOpenPreviewTableModal" width="1000px" :title="$t('Preview Data')" @ok="handleOk">
      <h3>{{$t('Preview Data')}} ({{$t('Limit')}} 10 {{$t('Rows')}})</h3>
      <p v-html="previewTableContent"></p>
    </a-modal>
  </div>
  
</div>
</template>
  
<script>
import axios from 'axios'
import { message } from 'ant-design-vue';

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
        sql: '',
        isOnlineMode: true,
      },

      isOpenPreviewTableModal: false,
      previewTableContent: "",
    };
  },

  mounted() {
    this.formState.isOnlineMode = this.isOnline;
  },

  methods: {
    submitForm() {
      axios
        .post(`/api/sql/execute`, {
          sql: this.formState.sql,
          online: this.formState.isOnlineMode
        })
        .then((response) => {
          message.success(`Success to execute SQL: ${this.formState.sql}`);

          if (this.formState.isOnlineMode) {
            this.isOpenPreviewTableModal = true;
            this.previewTableContent = response.data.replace(/\n/g, '<br>');
          } else {
            const jobId = response.data;
            this.$router.push(`/offlinejobs/${jobId}/result`);
          }

        })
        .catch((error) => {
          console.log(error);
          if ('response' in error && 'data' in error.response) {
            message.error(error.response.data);
          } else {
            message.error(error.message);
          }
        });
    },

    handleOk() {
      this.isOpenPreviewTableModal = false;
    },

  },
};
</script>