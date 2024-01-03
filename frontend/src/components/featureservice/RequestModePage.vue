<template>

<div>

  <br/>
  <a-descriptions bordered>
    <a-descriptions-item :span="24" :label="$t('Name')">{{ name }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Version')"> {{ version }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Feature Names')">{{ featureNames }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Request Schema')"> {{ requestSchema }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Request Demo Data')"> {{ requestDemoData }}</a-descriptions-item>
  </a-descriptions>

  <br/>
  <!-- Test form -->
  <a-form :model="formState"
    @submit="submitForm">

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
        <a-textarea v-model:value="formState.jsonTestData" :rows="5"></a-textarea>
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
          <a-input v-model:value="formState.columnDataList[index]" placeholder="" />
        </a-form-item>
      </a-space>
      </div>

      <a-form-item>
        <a-button type="primary" html-type="submit">{{ $t('Submit') }}</a-button>
      </a-form-item>
  </a-form>

  <div v-if="resultJson">
    <br/>
    <h2>Result</h2>
    <pre>{{ resultJson }}</pre>
  </div>

</div>
</template>
  
<script>
import axios from 'axios'
import { notification } from 'ant-design-vue'

export default {
  props: {
    name: {
      type: String,
      default: "",
    },
    version: {
      type: String,
      default: "",
    }
  },
  
  data() {
    return {
      featureNames: null,

      isUseJsonTestData: false,

      formState: {
        jsonTestData: "",
        columnDataList: []
      },

      requestSchema: "",
      requestDemoData: "",

      // Example: [{"name": "", "type": ""}]
      columns: [],

      resultJson: ""
    };
  },

  mounted() {
    this.init();
  },

  methods: {
    init() {
      axios.get(`/api/featureservices/${this.name}/${this.version}`)
        .then(response => {
          this.featureNames = response.data.featureNames;

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
        })

        axios.get(`/api/featureservices/${this.name}/${this.version}/request/schema`)
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

          
        axios.get(`/api/featureservices/${this.name}/${this.version}/request/demo`)
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
    },

    submitForm() {
      var requestJson = {}

      if (this.isUseJsonTestData) {
        try {
          requestJson = JSON.parse(this.formState.jsonTestData);
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

          const columnDataString = this.formState.columnDataList[i];
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

      axios.post(`/api/featureservices/${this.name}/${this.version}/request`,
        requestJson
      )
      .then(response => {
        if (response.data.code == 0) {
          notification["success"]({
            message: this.$t('Execute Success'),
            description: "Success to request feature service"
          });

          this.resultJson = response.data.data;
        } else {
          this.resultJson = response.data.msg;
        }
      })
      .catch(error => {
        var errorMessage = error.message;
        if (error.response && error.response.data) {
          errorMessage = error.response.data;
          this.resultJson = error.response.data;
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