<template>

<div>

  <br />
  <div>
    <h1>{{ $t('Deploy Feature Service') }}</h1>
    <!-- Create form -->
    <a-form
      :model="formState"
      name="basic"
      :label-col="{ span: 8 }"
      :wrapper-col="{ span: 10 }"
      @submit="handleSubmit">
      <a-form-item
        :label='$t("Feature Service Name")'
        :rules="[{ required: true, message: 'Please input feature service name!' }]">
        <a-select show-search @search="updateServiceName" id="itemSelect" v-model:value="testFormState.name" @change="updateSelectedService">
          <option v-for="featureViewItem in featureServices" :value="featureViewItem.name">{{ featureViewItem.name }}</option>
        </a-select>
      </a-form-item>

      <a-form-item
        :label='$t("Feature Service Version")'
        :rules="[{ required: true, message: 'Please input feature service version!' }]">
        <a-select id="itemSelect" v-model:value="testFormState.version" @change="updateSelectedService">
          <option v-for="version in featureServiceVersions" :value="version">{{ version }}</option>
        </a-select>
      </a-form-item>

      <a-form-item
        :label="$t('Choose Features')"
        :rules="[{ required: true, message: 'Please input feature list!' }]">

        <a-select mode="multiple" v-model:value="formState.featureList">
            <option v-for="featureview in featureViews" :value="featureview.name">{{ featureview.name }}</option>
        </a-select>
        <a-button type="primary"><router-link to='/features/create'>{{ $t('Create') }}</router-link></a-button>
      </a-form-item>


      <a-form-item
          :label="$t('Feature Service Description')">
          <a-input v-model:value="formState.description" />
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
import TestFeatureService from './TestFeatureService.vue';

export default {
  components: {
    TestFeatureService
  },
  data() {
    return {
      featureViews: [],
      databases:[],
      featureServices: [],
      featureServiceVersions: [],

      formState: {
        name: '',
        version: '',
        featureList: [],
        description: '',
        db: '',
      },

      testFormState: {
        name: "",
        version: "",
        testData: "",
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
      axios.get(`/api/databases`)
        .then(response => {
          this.databases = response.data;
        })
        .catch(error => {
          message.error(error.message);
        })
        .finally(() => {});

      if (this.$route.query.featureview != null) {
        this.formState.featureList = [this.$route.query.featureview];
      }

      if (this.$route.query.featureservice != null) {
        // Url provides feature service name
        this.testFormState.name = this.$route.query.featureservice;
        if (this.$route.query.version != null) {
          this.testFormState.version = this.$route.query.version;
        }
        this.updateSelectedService();
      }

      axios.get(`/api/featureservices`)
        .then(response => {
          this.featureServices = response.data;
        })
        .catch(error => {
          message.error(error.message);
        })
        .finally(() => {}); 

    },

    updateSelectedService() {
      if (this.testFormState.name != "") {

        axios.get(`/api/featureservices/${this.testFormState.name}/versions`)
          .then(response => {
            this.featureServiceVersions = response.data;
            this.featureServiceVersions.push("");
          })
          .catch(error => {
            message.error(error.message);
          });

        axios.get(`/api/featureservices/${this.testFormState.name}/${this.testFormState.version}/request/schema`)
          .then(response => {
            this.requestSchema = response.data;

          })
          .catch(error => {
            message.error(error.message);
          });

        axios.get(`/api/featureservices/${this.testFormState.name}/${this.testFormState.version}/request/demo`)
          .then(response => {
            this.requestDemoData = response.data;
          })
          .catch(error => {
            message.error(error.message);
          });   

          
      }
    },

    handleSubmit() {
      axios.post(`/api/featureservices`, {
        "name": this.formState.name,
        "version": this.formState.version,
        "featureList": this.formState.featureList.join(','),
        "description": this.formState.description,
      })
      .then(response => {
        message.success(`Success to add feature service ${this.formState.name} and version ${this.formState.version}`);

        // Redirect to FeatureView detail page
        this.$router.push(`/featureservices/${this.formState.name}/${this.formState.version}`);
      })
      .catch(error => {
          if (error.response.data) {
            message.error(error.response.data);
          } else {
            message.error(error.message);
          }
      });
    },

    updateServiceName(value){
      if (value){
       this.testFormState.name=value
      }
    }

  },
};
</script>