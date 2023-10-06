<template>

<div>

  <br />
  <div>
    <h1>{{ $t('Create') }}{{ $t('Online')}}{{ $t('Feature Service') }}</h1>
    <!-- Create form -->
    <a-form
      :model="formState"
      name="basic"
      :label-col="{ span: 8 }"
      :wrapper-col="{ span: 16 }"
      @submit="handleSubmit">
      <a-form-item
        :label="$t('Database')"
        :rules="[{ required: true, message: 'Please input database!' }]">
        <a-select v-model:value="formState.db">
          <option v-for="database in databases" :value="database">{{ database }}</option>
        </a-select>
      </a-form-item>

      <a-form-item
        :label="$t('Feature View List')"
        :rules="[{ required: true, message: 'Please input feature list!' }]">

        <a-select mode="multiple" v-model:value="formState.featureList">
            <option v-for="featureview in featureViews" :value="featureview.name">{{ featureview.name }}</option>
        </a-select>
        <br /><br/>
        <a-button type="primary"><router-link to='/features/create'>{{ $t('Create New Feature') }}</router-link></a-button>
      </a-form-item>
    </a-form>
  </div>

<div>
    <h1>{{ $t('Online')}}{{ $t('Feature Service') }}{{ $t('Information')}}</h1>
    <!-- Create form -->
<a-form
      :model="formState"
      name="basic"
      :label-col="{ span: 8 }"
      :wrapper-col="{ span: 16 }"
      @submit="handleSubmit">

      <a-form-item
        :label="$t('Feature Service Name')"
        :rules="[{ required: true, message: 'Please input name!' }]">
        <a-input v-model:value="formState.name" />
      </a-form-item>

      <a-form-item
        :label="$t('Feature Service Version')"
        :rules="[{ required: true, message: 'Please input version!' }]">
        <a-input v-model:value="formState.version" />
      </a-form-item>

      <a-form-item
          :label="$t('Description')">
          <a-input v-model:value="formState.description" />
        </a-form-item>

      <a-form-item :wrapper-col="{ offset: 8, span: 16 }">
        <a-button type="primary" html-type="submit">{{ $t('Submit') }}</a-button>
      </a-form-item>
    </a-form>
  </div>

  <TestFeatureService></TestFeatureService>

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

      formState: {
        name: '',
        version: '',
        featureList: [],
        description: '',
        db: '',
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

  },
};
</script>