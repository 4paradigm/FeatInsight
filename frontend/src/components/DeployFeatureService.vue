<template>

<div>

  <br />
  <div>
    <h1>{{ $t('Create') }} {{ $t('Feature Service') }}</h1>
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
        :label="$t('Feature Collection')"
        :rules="[{ required: true, message: 'Please input feature list!' }]">

        <a-select mode="multiple" v-model:value="formState.featureCollection">
            <option v-for="featureOption in featureOptions" :value="featureOption">{{ featureOption }}</option>
        </a-select>
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

</div>
</template>
  
<script>
import axios from 'axios'
import { message } from 'ant-design-vue';

export default {
  data() {
    return {
      featureOptions: [],

      formState: {
        name: '',
        version: '',
        featureCollection: [],
        description: '',
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
            console.log(feature);
            // Append complete feature name
            const completeFeatureName = feature.featureViewName + ":" + feature.featureName;
            this.featureOptions.push(completeFeatureName);
          });
        })
        .catch(error => {
          message.error(error.message);
        })
        .finally(() => {});

      if (this.$route.query.featureview != null) {
        this.formState.featureCollection = [this.$route.query.featureview];
      }

    },

    handleSubmit() {
      axios.post(`/api/featureservices`, {
        "name": this.formState.name,
        "version": this.formState.version,
        "featureList": this.formState.featureCollection.join(','),
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