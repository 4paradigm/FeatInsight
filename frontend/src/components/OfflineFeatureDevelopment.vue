<template>

<div>

  <br />
  <div>
    
    <h1>{{ $t('Offline Train Set Generation') }}</h1>
    <br/>

    <!-- Create form -->
    <a-form
      :model="formState"
      name="basic"
      @submit="handleSubmit">

      <a-form-item
        :label="$t('Choose Features')"
        :rules="[{ required: true, message: 'Please input feature list!' }]">

        <a-space>
        <a-select mode="multiple" style="width: 680px" v-model:value="formState.featureList">
          <option v-for="featureOption in featureOptions" :value="featureOption">{{ featureOption }}</option>
        </a-select>
        <a-button type="primary"><router-link to='/features/create'>{{ $t('Create') }}</router-link></a-button>
      </a-space>
      </a-form-item>

      <a-form-item
          :label="$t('File Format')"
          :rules="[{ required: true, message: 'Please choose file format!' }]">
          <a-select v-model:value="formState.format">
            <option v-for="format in formatOptions" :key="format.id" :value="format.name">{{ format.name}}</option>
          </a-select>
      </a-form-item>

      <a-form-item 
          :label="$t('Export Path')" 
          :rules="[{ required: true, message: 'Please input export path!' }]">

          <a-input id="exportPath" v-model:value="formState.path" :placeholder="$t('path')"></a-input>
      </a-form-item>

      <a-form-item 
          :label="$t('Export Options')">

          <a-tooltip>
            <template #title><a target="blank" href="https://openmldb.ai/docs/zh/main/openmldb_sql/dql/SELECT_INTO_STATEMENT.html">{{$t('Document of options')}}</a></template>
            <a-input id="exportOptions" v-model:value="formState.options"></a-input>
          </a-tooltip>
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
      featureOptions: [],

      formatOptions:[
        {id: 'csv', name: 'CSV'},
        {id: 'parquet', name: 'Parquet'}
      ],

      tables: [],

      formState: {
        featureList: [],
        format: '',
        path: '',
        options: '',
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
          response.data.forEach(featureView => {
            // Append feature view name
            this.featureOptions.push(featureView.name)
          });
        })
        .catch(error => {
          message.error(error.message);
        })
        .finally(() => {});

      axios.get(`/api/features`)
        .then(response => {
          response.data.forEach(feature => {
            // Append complete feature name
            const completeFeatureName = feature.featureViewName + ":" + feature.featureName;
            this.featureOptions.push(completeFeatureName);
          });
        })
        .catch(error => {
          message.error(error.message);
        })
        .finally(() => {});

    },

    handleSubmit() {
      axios.post(`/api/trainingsets`, {
        "featureList": this.formState.featureList.join(','),
        "format": this.formState.format,
        "path": this.formState.path,
        "options": this.formState.options,
      })
      .then(response => {
        message.success(`Success to export training set for feature list ${this.formState.featureList}`);

        console.log(response.data)

        // Redirect to FeatureView detail page
        //this.$router.push(`/featureservices/${this.formState.name}/${this.formState.version}`);
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
