<template>

  <div>  
    <br />
    <div>
      <h1>{{ $t('Create') }} {{ $t('Entity') }}</h1>
      <!-- Create form -->
      <a-form
        :model="formState"
        name="basic"
        :label-col="{ span: 8 }"
        :wrapper-col="{ span: 16 }"
        @submit="handleSubmit">
        <a-form-item
          :label='$t("Entity Name")'
          :rules="[{ required: true, message: 'Please input name!' }]">
          <a-input v-model:value="formState.name" />
        </a-form-item>
  
        <a-form-item
          :label='$t("Primary Keys")'
          :rules="[{ required: true, message: 'Please input primary keys!' }]">
          <a-input v-model:value="formState.primaryKeys" />
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
  
  export default {
    data() {
      return {
  
        formState: {
          name: '',
          primaryKeys: '',
        }
      };
    },
  
    mounted() {
    },
  
    methods: {
  
      handleSubmit() {
        axios.post(`/api/entities`, {
          "name": this.formState.name,
          "primaryKeys": this.formState.primaryKeys
        })
        .then(response => {
          message.success(`Success to add entity ${this.formState.name}`);

          this.$router.push(`/entities/${this.formState.name}`);
        })
        .catch(error => {
          message.error(error.message);
        });
      },
    
    },
  };
  </script>