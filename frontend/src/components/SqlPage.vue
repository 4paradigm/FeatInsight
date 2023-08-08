<template>

<div>
  <br />
  <div>
    <h1>{{ $t('Execute') }} {{ $t('SQL') }}</h1>
    <a-typography>
      <a-typography-paragraph>
        <p>Execute SQLs in OpenMLDB.</p>
        <p>eg. CREATE DATABASE IF NOT EXISTS db1</p>
        <p>eg. SELECT * FROM SYSTEM_FEATURE_PLATFORM.feature_services</p>
      </a-typography-paragraph>
    </a-typography>
    <!-- Execute SQL form -->
    <a-form
      :model="formState"
      name="basic"
      :label-col="{ span: 8 }"
      :wrapper-col="{ span: 16 }"
      @submit="handleSubmit">
      <a-form-item
        label="SQL"
        :rules="[{ required: true, message: 'Please input SQL!' }]">
        <a-input v-model:value="formState.sql" />
      </a-form-item>

      <a-form-item :wrapper-col="{ offset: 8, span: 16 }">
        <a-button type="primary" html-type="submit">{{ $t('Submit') }}</a-button>
      </a-form-item>
    </a-form>
  </div>

  <br />
  <div>
    <h1>{{ $t('Validate') }} {{ $t('SQL') }}</h1>
    <p>Validate DQL SQLs for online request mode.</p>
    <p>eg. SELECT * from entities</p>
    <!-- Validate SQL form -->
    <a-form
      :model="validateFormState"
      name="basic"
      :label-col="{ span: 8 }"
      :wrapper-col="{ span: 16 }"
      @submit="handleValidateFormSubmit">
      <a-form-item
        label="SQL"
        :rules="[{ required: true, message: 'Please input SQL!' }]">
        <a-input v-model:value="validateFormState.sql" />
      </a-form-item>

      <a-form-item :wrapper-col="{ offset: 8, span: 16 }">
        <a-button type="primary" html-type="submit">{{ $t('Submit') }}</a-button>
      </a-form-item>
    </a-form>
  </div>
</div>
</template>
  
<script>
import axios from 'axios'
import { message } from 'ant-design-vue';
import { Modal } from 'ant-design-vue';
import { h } from 'vue';

export default {
  data() {
    return {
      formState: {
        sql: '',
      },

      validateFormState: {
        sql: '',
      }
    };
  },

  mounted() {},

  methods: {
    handleSubmit() {
      axios.post(`/api/sql/execute`, {
        "sql": this.formState.sql,
      })
      .then(response => {
        message.success(`Success to execute SQL: ${this.formState.sql}`);
        console.log(response.data);

        Modal.success({
          title: 'Execute result',
          content: h('div', {}, [
            h('pre', JSON.stringify(response.data)),
          ]),
          onOk() {},
        });
      })
      .catch(error => {
        console.log(error);
        if ("response" in error && "data" in error.response) {
          message.error(error.response.data);
        } else {
          message.error(error.message);
        }
      });
    },

    handleValidateFormSubmit() {
      axios.post(`/api/sql/validate`, {
        "sql": this.validateFormState.sql,
      })
      .then(response => {
        message.success(`Success to validate SQL: ${this.validateFormState.sql}`);
      })
      .catch(error => {
        if ("response" in error && "data" in error.response) {
          message.error(error.response.data);
        } else {
          message.error(error.message);
        }
      });
    },
  },
};
</script>