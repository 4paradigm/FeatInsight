<template>

<div>

  <div>
    <a-typpography>
      <a-typography-paragraph>
        <pre>{{ $t("Text of introduce load data") }} <a target="blank" href="https://openmldb.ai/docs/zh/main/openmldb_sql/index.html">{{$t('OpenMLDB documents')}}</a></pre>
      </a-typography-paragraph>
    </a-typpography>
    <br/>

    <!-- Create form -->
    <a-form
      :model="formState"
      layout="vertical"
      @submit="handleSubmit">

      <a-form-item
        :label="$t('Execute Mode')">
        <a-switch v-model:checked="formState.isOnlineMode" :checked-children="$t('Online')" :un-checked-children="$t('Offline')" />
      </a-form-item>

      <a-form-item
        :label="$t('SQL')"
        :rules="[{ required: true, message: 'Please input sql!' }]">
        <a-input
          v-model:value="formState.sql"
          placeholder="Please input SQL" />
      </a-form-item>

      <a-form-item>
        <a-button type="primary" html-type="submit">{{ $t('Submit') }}</a-button>
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
  },

  methods: {

    handleSubmit() {
      axios
        .post(`/api/sql/execute`, {
          sql: this.formState.sql,
          isOnline: this.formState.isOnlineMode
        })
        .then((response) => {
          message.success(`Success to execute SQL: ${this.formState.sql}`);

          this.isOpenPreviewTableModal = true;
          this.previewTableContent = response.data.replace(/\n/g, '<br>');
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