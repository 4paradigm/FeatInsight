<template>
  <a-drawer
    v-model:visible="isOpenTableDrawer"
    size="large"
    :title="$t('Table') + $t('Detail')">
    <TableDetail :db="currentDrawerDatabase" :name="currentDrawerTable" :key="currentDrawerDatabase+currentDrawerTable"></TableDetail>
  </a-drawer>

  <div>
  
    <br/>
    <a-descriptions bordered>
      <a-descriptions-item :span="24" :label="$t('Name')">{{ name }}</a-descriptions-item>
      <a-descriptions-item :span="24" :label="$t('Version')"> {{ version }}</a-descriptions-item>
      <a-descriptions-item :span="24" :label="$t('Feature Names')">{{ featureNames }}</a-descriptions-item>
      <a-descriptions-item :span="24" :label="$t('Feature Table')">
        <a-button type="link" @click="openTableDrawer(mainTable.database, mainTable.table)">{{ mainTable.database + "." + mainTable.table }}</a-button>
      </a-descriptions-item>
    </a-descriptions>

    <br/>
    <!-- Test form -->
    <a-form :model="formState"
      @submit="submitForm">
    
      <a-form-item
        :label="$t('Choose Index')">
        <!-- TODO: Do not support mode="multiple" now -->
        <a-select show-search 
          v-model:value="formState.chooseColumnNames"
          @change="changeFilterColumnNames">
          <option v-for="columnNames in allIndexColumnNamesList" :value="columnNames"></option>
        </a-select>
      </a-form-item>


      <a-space
        v-for="(column, index) in formInputColumns"
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
  
        <a-form-item>
          <a-button type="primary" html-type="submit">{{ $t('Submit') }}</a-button>
        </a-form-item>
    </a-form>
  
    <div v-if="isShowResult">
      <br/>
      <h2>{{ $t('Online Query Feature Result') }}</h2>
      <a-table :dataSource="resultData" :columns="resultColumns">
        <template #id="{ text, record }">
          <a-button type="link" @click="openOfflineJobDrawer(record.id)">{{ record.id }}</a-button>
        </template>
      </a-table>
    </div>
  
  </div>
  </template>
    
  <script>
  import axios from 'axios'
  import { notification } from 'ant-design-vue'
  import TableDetail from '@/components/table/TableDetail.vue'
  
  export default {
    components: {
      TableDetail
    },

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

        mainTable: {},

        isOpenTableDrawer: false,
        currentDrawerDatabase: "",
        currentDrawerTable: "",

        formState: {
          chooseColumnNames: [],

          columnDataList: []
        },

        // For exmaples: ["name", "name,age"]
        allIndexColumnNamesList: [],
        mainTableColumns: [],
        formInputColumns: [],

        isShowResult: false,
        resultColumns: [],
        resultData: [],
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

        axios.get(`/api/featureservices/${this.name}/${this.version}/maintable`)
          .then(response => {
            this.mainTable = response.data;
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
  

        axios.get(`/api/featureservices/${this.name}/${this.version}/indexes`)
          .then(response => {
            this.allIndexColumnNamesList = response.data;
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

          


          axios.get(`/api/featureservices/${this.name}/${this.version}/request/schema`)
          .then(response => {
            this.mainTableColumns = response.data.split(",").map(pair => {
              return {
                "name": pair.split(":")[0],
                "type": pair.split(":")[1]
              }
            });
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

      changeFilterColumnNames() {
        const columnNameList = this.formState.chooseColumnNames.split(",")

        this.formInputColumns = this.mainTableColumns.filter(item => columnNameList.includes(item.name));
      },
  
      openTableDrawer(db, table) {
        this.isOpenTableDrawer = true;
        this.currentDrawerDatabase = db;
        this.currentDrawerTable = table;
      },

      submitForm() {
        var requestJson = {}
  
        // Construct single row data, example: {"index": ["name, age"], "data": [["John", 28]]}
        requestJson = {};
        requestJson["data"] = [];
        requestJson["data"][0] = [];
        requestJson["index"] = this.formInputColumns.map(item => item.name);

        
        for (var i=0; i< this.formInputColumns.length; ++i) {
          console.log("index: " + i);
          const type = this.formInputColumns[i]["type"];
          console.log("type: " + type);
          
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

          requestJson["data"][0].push(columnData);
        }

        axios.post(`/api/featureservices/${this.name}/${this.version}/request/onlinequerymode`,
          requestJson
        )
        .then(response => {
            notification["success"]({
              message: this.$t('Execute Success'),
              description: "Success to request feature service"
            });
  
            this.isShowResult = true;

            if (response.data.length > 0) {
              const columnCount = response.data[0].length;

              this.resultColumns = []
              for (var i = 0; i < columnCount; i++) {
                const columnName = response.data[0][i];
                this.resultColumns.push({
                  title: columnName,
                  dataIndex: columnName,
                  key: columnName
                })
              }

              this.resultData = []
              for (var i = 1; i < response.data.length; i++) {
                const row = response.data[i]
                const rowDataMap = {}

                for (var j = 0; j < columnCount; j++) {
                  const columnName = response.data[0][j];
                  const columnData = row[j];
                  rowDataMap[columnName] = columnData;
                }

                this.resultData.push(rowDataMap);
              }
              
            } else {
              this.resultColumns = [];
              this.resultData = [];
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
      }
  
    }
  };
  </script>