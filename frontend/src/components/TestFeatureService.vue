<template>

<div>

  <div>
    <a-typography-paragraph>
      <pre>{{ $t("Text of introduce test feature service") }} <a target="blank" href="https://openmldb.ai/docs/zh/main/quickstart/sdk/rest_api.html">{{$t('OpenMLDB documents')}}</a></pre>
    </a-typography-paragraph>
    <br/>
    
    <!-- Test form -->
    <a-form
      :model="testFormState"
      layout="vertical">
      <a-form-item
        :label='$t("Feature Service Name")'
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

      <a-form-item
        :label='$t("Request Schema")'>
        <a-typography>
          <a-typography-paragraph>
            <pre>{{ requestSchema }}</pre>
          </a-typography-paragraph>
        </a-typography>
      </a-form-item>

      <a-form-item
        :label='$t("Request Demo Data")'>
        <a-typography>
          <a-typography-paragraph>
            <pre>{{ requestDemoData }}</pre>
          </a-typography-paragraph>
        </a-typography>
      </a-form-item>

      <a-form-item
        :label='$t("Test Data")'
        :rules="[{ required: true, message: 'Please input test data!' }]">
        <a-textarea v-model:value="testFormState.testData" rows="5"></a-textarea>
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

    submitForm() {
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