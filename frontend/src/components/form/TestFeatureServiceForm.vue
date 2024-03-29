<template>

<div>

  <div>
    <a-typography-paragraph>
      <!-- <pre>{{ $t("Text of introduce test feature service") }} <a target="blank" href="https://openmldb.ai/docs/zh/main/quickstart/sdk/rest_api.html">{{$t('OpenMLDB documents')}}</a></pre> -->
      <pre>用户可以使用“请求模式”或“在线查询模式”来访问特征服务，请求模式下用户需要输入整行主表数据通过 LastJoin 等方法提取副表特征；在线查询模式则不需要传入数据，直接查询在线表的特征数据，并且可以通过主键进行数据过滤。</pre>
    </a-typography-paragraph>
    <br/>
    
    <!-- Test form -->
    <a-form
      :model="testFormState"
      layout="vertical">
      <a-form-item
        :label='$t("Feature Service Name")'
        :rules="[{ required: true, message: 'Please input feature service name!' }]">
        <a-select show-search id="itemSelect" v-model:value="testFormState.name" @change="updateSelectedService">
          <option v-for="featureViewItem in featureServices" :value="featureViewItem.name">{{ featureViewItem.name }}</option>
        </a-select>
      </a-form-item>

      <a-form-item
        :label='$t("Version")'
        :rules="[{ required: false, message: 'Please input feature service version!' }]">
        <a-select show-search id="itemSelect" v-model:value="testFormState.version" @change="updateSelectedService">
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

      <p>
        <span style="color:red;">* </span>{{ $t('Request Data') }}
        &nbsp;&nbsp;<a-button size="small" @click="switchJsonTestData">{{ $t('Switch Json Data') }}</a-button>
      </p>



      <div v-if="isUseJsonTestData"> 
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
          <a-textarea v-model:value="testFormState.jsonTestData" :rows="5"></a-textarea>
        </a-form-item>
      </div>


      <div v-else>
        <a-space
        v-for="(column, index) in columns"
        :key="column.name"
        style="display: flex; margin-bottom: 0px"
        align="baseline"
      >
        <p style="min-width: 200px">{{ column.name }} ({{ column.type }}):</p>

        <a-form-item
          :name="['columns', index, 'name']">
          <a-input v-model:value="testFormState.columnDataList[index]" placeholder="" />
        </a-form-item>
      </a-space>
      </div>

    </a-form>
  </div>

</div>
</template>
  
<script>
import axios from 'axios'
import { notification } from 'ant-design-vue'
import { Modal } from 'ant-design-vue'
import { h } from 'vue';

export default {
  props: {
    featureServiceName: {
      type: String,
      default: "",
    },
    featureServiceVersion: {
      type: String,
      default: "",
    }
  },
  
  data() {
    return {
      featureServices: [],

      featureServiceVersions: [],

      isUseJsonTestData: false,

      testFormState: {
        name: "",
        version: "",
        jsonTestData: "",
        columnDataList: []
      },

      requestSchema: "",
      requestDemoData: "",

      // Example: [{"name": "", "type": ""}]
      columns: [],

    };
  },

  mounted() {
    this.initData();
  },

  // Use updated() instead mounted() for using latest feature service and version
  updated() {
    this.initData();
  },

  methods: {
    updateSelectedService() {
      if (this.testFormState.name != "") {

        axios.get(`/api/featureservices/${this.testFormState.name}/versions`)
          .then(response => {
            this.featureServiceVersions = response.data;
          })
          .catch(error => {
            var errorMessage = error.message;
            if (error.response && error.response.data) {
              errorMessage = error.response.data;
            }
            notification["error"]({
              message: this.$t('Execute Fail'),
              description: errorMessage
            });
          });

        axios.get(`/api/featureservices/${this.testFormState.name}/${this.testFormState.version}/request/schema`)
          .then(response => {
            this.requestSchema = response.data;

            const columnList = response.data.split(",").map(obj => {
              return {
                "name": obj.split(":")[0],
                "type": obj.split(":")[1]
              }
            })

            this.columns = [...columnList]
          })
          .catch(error => {
            var errorMessage = error.message;
            if (error.response && error.response.data) {
              errorMessage = error.response.data;
            }
            notification["error"]({
              message: this.$t('Execute Fail'),
              description: errorMessage
            });
          });

          
        axios.get(`/api/featureservices/${this.testFormState.name}/${this.testFormState.version}/request/demo`)
          .then(response => {
            this.requestDemoData = response.data;
          })
          .catch(error => {
            var errorMessage = error.message;
            if (error.response && error.response.data) {
              errorMessage = error.response.data;
            }
            notification["error"]({
              message: this.$t('Execute Fail'),
              description: errorMessage
            });
          });   
          
          
      }
    },

    initData() {
      if (this.featureServiceName) {
        this.testFormState.name = this.featureServiceName;

        if (this.featureServiceVersion) {
          this.testFormState.version = this.featureServiceVersion;
        }

        this.updateSelectedService();
      }

      axios.get(`/api/featureservices`)
        .then(response => {
          this.featureServices = response.data;
        })
        .catch(error => {
          var errorMessage = error.message;
          if (error.response && error.response.data) {
            errorMessage = error.response.data;
          }
          notification["error"]({
            message: this.$t('Execute Fail'),
            description: errorMessage
          });
        }); 
    },

    handleDelete(name) {
      axios.delete(`/api/featureservices/${name}`)
      .then(response => {
        notification["success"]({
              message: this.$t('Execute Success'),
              description: `Success to delete feature service: ${name}`
            });

        this.initData();
      })
      .catch(error => {
        var errorMessage = error.message;
        if (error.response && error.response.data) {
          errorMessage = error.response.data;
        }
        notification["error"]({
          message: this.$t('Execute Fail'),
          description: errorMessage
        });
      });
    },

    submitForm() {
      var requestJson = {}

      if (this.isUseJsonTestData) {
        try {
          requestJson = JSON.parse(this.testFormState.jsonTestData);
        } catch (e) {
          notification["error"]({
            message: this.$t('Request Fail'),
            description: e.message
          });
        }

      } else {
        // Construct single row data, example: {input: [ ["foo"] ]}
        requestJson = {"input": []};
        requestJson["input"][0] = [];

        const columnStringList = this.requestSchema.split(",")
        for (var i=0; i< columnStringList.length; ++i) {
          const type = columnStringList[i].split(":")[1];

          const columnDataString = this.testFormState.columnDataList[i];
          var columnData = columnDataString;

          if (type === "int16") {
            columnData = parseInt(columnDataString);
          } else if (type === "int32") {
            columnData = parseInt(columnDataString);
          } else if (type == "int64") {
            columnData = parseInt(columnDataString);
          } else if (type === "float") {
            columnData = parseFloat(columnDataString);
          } else if (type === "double") {
            columnData = parseFloat(columnDataString);
          } else if (type === "timestamp") {
            columnData = new Date(columnDataString).getTime();
          } else if (type === "date") {
            // Use string date to request OpenMLDB api server
          } else if (type == "bool") {
            columnData = Boolean(columnDataString);
          } else if (type == "string") {

          } else {
            notification["error"]({
              message: this.$t('Execute Fail'),
              description: "Get unsupport type: " + type
            });
          }


          requestJson["input"][0].push(columnData);
        }
      }

      axios.post(`/api/featureservices/${this.testFormState.name}/${this.testFormState.version}/request`,
        requestJson
      )
      .then(response => {
        notification["success"]({
              message: this.$t('Execute Success'),
              description: `Success to request feature service ${this.testFormState.name}`
            });

        if (response.data.code == 0) {
          Modal.success({
            title: 'Request result',
            content: h('div', {}, [
              h('p', JSON.stringify(response.data.data)),
            ]),
            onOk() {},
          });
        } else {
          notification["error"]({
              message: this.$t('Request Fail'),
              description: `Error message: ${response.data.msg}, request json: ${requestJson}`
            });
        }
      })
      .catch(error => {
        var errorMessage = error.message;
        if (error.response && error.response.data) {
          errorMessage = error.response.data;
        }
        notification["error"]({
          message: this.$t('Execute Fail'),
          description: errorMessage
        });
      });
    },

    switchJsonTestData() {
      if (this.isUseJsonTestData) {
        this.isUseJsonTestData = false;
      } else {
        this.isUseJsonTestData = true;
      }
      
    }

  }
};
</script>