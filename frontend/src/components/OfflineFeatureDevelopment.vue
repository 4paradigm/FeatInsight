<template>

<div>

  <br />
  <div>
    <h1>{{ $t('Offline Train Set Generation') }}</h1>
    <!-- Create form -->
    <a-form
      :model="formState"
      name="basic"
      :label-col="{ span: 8 }"
      :wrapper-col="{ span: 10 }"
      @submit="handleSubmit">
      <a-form-item
        :label="$t('Choose Features')"
        :rules="[{ required: true, message: 'Please input feature list!' }]">

        <a-select mode="multiple" v-model:value="formState.featureList">
            <option v-for="featureview in featureViews" :value="featureview.name">{{ featureview.name }}</option>
        </a-select>
        <a-button type="primary"><router-link to='/features/create'>{{ $t('Create') }}</router-link></a-button>
        </a-form-item>

      <a-form-item
          :label="$t('File Format')"
          :rules="[{ required: true, message: 'Please choose file format!' }]">
          <a-select v-model:value="formState.fileformat">
            <option v-for="format in fileFormats" :key="format.id" :value="format.name">{{ format.name}}</option>
          </a-select>
      </a-form-item>

      <a-form-item 
          :label="$t('Export Path')" 
          :rules="[{ required: true, message: 'Please input export path!' }]">
          <a-input id="exportPath" v-model:value="formState.exportPath" :placeholder="$t('path')"></a-input>
      </a-form-item>

      <a-form-item 
          :label="$t('Export Options')">
          <a-input id="exportOptions" v-model:value="formState.exportOptions"></a-input>
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
      featureViews: [],
      fileFormats:[{id: 'csv', name: 'CSV'}, {id: 'parquet', name: 'Parquet'}],
      tables: [],

      formState: {
        featureList: [],
        key: '',
        table: '',
        fileFormat: '',
        exportPath: '',
        exportOptions: '',
      },

    };
  },

  mounted() {
    this.initData();
  },

  methods: {
    initData() {
      axios.get(`/api/featureviews`)
        .then(response => {
          this.featureViews = response.data;
        })
        .catch(error => {
          message.error(error.message);
        })
        .finally(() => {});

      if (this.$route.query.featureview != null) {
        this.formState.featureList = [this.$route.query.featureview];
      }

    },


    handleSubmit() {

    },

  },
};
</script>
