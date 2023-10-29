<template>

<div>
    <!-- Create Feature Form Modal -->
    <a-modal v-model:visible="isShowCreateFeatureModal" width="1000px" :title="$t('Create Feature View')" >
      <template #footer>
        <a-button @click="handleCancel">Cancel</a-button>
      </template>
      <CreateFeatureViewForm @submitted="submittedCreateFeatureForm"></CreateFeatureViewForm>
    </a-modal>

    <a-typography-paragraph>
      <pre>{{ $t("Text of introduce create offline sample") }} <a target="blank" href="https://openmldb.ai/docs/zh/main/openmldb_sql/dql/SELECT_INTO_STATEMENT.html">{{$t('OpenMLDB documents')}}</a></pre>
    </a-typography-paragraph>
    <br/>

    <!-- Create form -->
    <a-form
      :model="formState"
      layout="vertical"
      name="basic"
      @submit="submitForm">

      <a-form-item
        :label="$t('Choose Features')"
        :rules="[{ required: true, message: 'Please input feature list!' }]">
        <a-button type="primary" @click="clickCreateFeature">{{ $t('Create Feature') }}</a-button>
        
        <br/><br/>
        <a-select mode="multiple" v-model:value="formState.featureSet">
          <option v-for="featureOption in featureOptions" :value="featureOption">{{ featureOption }}</option>
        </a-select>
      </a-form-item>

      <a-form-item 
          :label="$t('Main Table Keys')">
          <a-tooltip>
            <template #title>{{$t('Text of introduce join keys')}}</template>
            <a-input id="mainTableKeys" v-model:value="formState.mainTableKeys"></a-input>
          </a-tooltip>
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

          <a-input id="exportPath" v-model:value="formState.path" :placeholder="$t('Path Hint')"></a-input>
      </a-form-item>

      <a-form-item 
          :label="$t('Export Options')">

          <a-tooltip>
            <template #title><a target="blank" href="https://openmldb.ai/docs/zh/main/openmldb_sql/dql/SELECT_INTO_STATEMENT.html">{{$t('Reference document of options')}}</a></template>
            <a-input id="exportOptions" v-model:value="formState.options"></a-input>
          </a-tooltip>
      </a-form-item>
    </a-form>

</div>
</template>
  
<script>
import axios from 'axios'
import { message } from 'ant-design-vue';
import CreateFeatureViewForm from '@/components/form/CreateFeatureViewForm.vue'

export default {
  components: {
    CreateFeatureViewForm
  },

  data() {
    return {
      isShowCreateFeatureModal: false,

      featureOptions: [],

      formatOptions:[
        {id: 'csv', name: 'CSV'},
        {id: 'parquet', name: 'Parquet'}
      ],

      formState: {
        featureSet: [],
        mainTableKeys: "",
        format: 'CSV',
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
      this.updateSelectFeatureOptions();
    },

    updateSelectFeatureOptions() {
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

    submitForm() {
      axios.post(`/api/offlinesamples`, {
        "featureNames": this.formState.featureSet.join(','),
        "path": this.formState.path,
        "options": this.formState.options,
        "mainTableKeys": this.formState.mainTableKeys,
      })
      .then(response => {
        //message.success(`Success to export offline sample for feature list ${this.formState.featureSet}`);

        console.log(response.data)
        const jobId = response.data.jobId;

        // Redirect to result page
        this.$router.push(`/offlinesamples/${jobId}/result`);
      })
      .catch(error => {
          if (error.response.data) {
            message.error(error.response.data);
          } else {
            message.error(error.message);
          }
      });
    },

    clickCreateFeature() {
      this.isShowCreateFeatureModal = true;
    },

    handleCancel() {
      this.isShowCreateFeatureModal = false;
    },

    submittedCreateFeatureForm(newFeatureViewName) {
      this.isShowCreateFeatureModal = false;
      this.updateSelectFeatureOptions();
      this.formState.featureSet = [newFeatureViewName]
    }
  },
};
</script>
