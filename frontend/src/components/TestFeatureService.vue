<template>

<div>
  
  <br />
  <div>
    <h1>{{ $t('Test Service') }}</h1>
    <!-- Test form -->
    <a-form
      :model="testFormState"
      name="basic"
      :label-col="{ span: 8 }"
      :wrapper-col="{ span: 16 }"
      @submit="handleTestFormSubmit">
      <a-form-item
        :label='$t("Feature Service")'
        :rules="[{ required: true, message: 'Please input feature service name!' }]">
        <a-select id="itemSelect" v-model:value="testFormState.name" @change="updateSelectedService">
          <option v-for="featureViewItem in featureServices" :value="featureViewItem.name">{{ featureViewItem.name }}</option>
        </a-select>
      </a-form-item>

      <a-form-item
        :label='$t("Version")'
        :rules="[{ required: false, message: 'Please input feature service version!' }]">
        <a-select id="itemSelect" v-model:value="testFormState.version" @change="updateSelectedService">
          <option v-for="version in featureServiceVersions" :value="version">{{ version }}</option>
        </a-select>
      </a-form-item>

      <div v-if="testFormState.name != ''">
        <br/>
        <h1>{{ $t('Request') }} {{ $t('Schema') }}</h1>
        <p>{{ requestSchema }}</p>

        <h1>{{ $t('Request') }} {{ $t('Demo Data') }}</h1>
        <p>{{ requestDemoData }}</p>
        <br/>
      </div>

      <a-form-item
        :label='$t("Test Data")'
        :rules="[{ required: true, message: 'Please input test data!' }]">
        <a-textarea v-model:value="testFormState.testData" rows="5"></a-textarea>
      </a-form-item>
      
      <a-form-item :wrapper-col="{ offset: 8, span: 16 }">
        <a-button type="primary" html-type="submit">{{ $t('Test') }}</a-button>
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
      featureServices: [],

      featureServiceVersions: [],

      testFormState: {
        name: "",
        version: "",
        testData: "",
      },

      requestSchema: "",
      requestDemoData: "",
    };
  },

  mounted() {
    this.initData();
  },

  methods: {
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

    initData() {

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

    handleDelete(name) {
      axios.delete(`/api/featureservices/${name}`)
      .then(response => {
        message.success(`Success to delete feature service: ${name}`);
        this.initData();
      })
      .catch(error => {
        message.error(error.message);
      });
    },

    handleTestFormSubmit() {
      axios.post(`/api/featureservices/${this.testFormState.name}/${this.testFormState.version}/request`,
        JSON.parse(this.testFormState.testData)
      )
      .then(response => {
        message.success(`Success to request feature service ${this.testFormState.name}`);

        if (response.data.code == 0) {
          Modal.success({
            title: 'Request result',
            content: h('div', {}, [
              h('p', JSON.stringify(response.data.data)),
            ]),
            onOk() {},
          });
        } else {
          message.error(response.data.msg);
        }
      })
      .catch(error => {
        message.error(error.message);
      });
    },

  },
};
</script>