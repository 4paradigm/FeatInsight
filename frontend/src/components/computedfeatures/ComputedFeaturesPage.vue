<template>
<div>

  <br/>
  <a-typography>
    <a-typography-title :level="2">{{ $t('Computed Features') }}</a-typography-title>
    <a-typography-paragraph>
      <blockquote>
        {{ $t('Text of Computed Features') }}
      </blockquote>
    </a-typography-paragraph>
  </a-typography>

  <br/>
  <a-form>
    <a-form-item
      :label="$t('Feature Table')"
      :rules="[{ required: true, message: 'Please input table name!' }]">
      <a-select show-search v-model:value="chooseTableName" @change="updateChooseTableName()">
        <option v-for="name in allTableNames" :value="name"></option>
      </a-select>
    </a-form-item>

    <a-form-item
      :label="$t('Feature Columns')">
      <a-select show-search mode="multiple" v-model:value="chooseColumnNames">
        <option v-for="name in allColumnNames" :value="name"></option>
      </a-select>
    </a-form-item>
  </a-form>

    <a-button type="primary" @click="generateSampleFeatures">{{ $t('Display Sample Features') }}</a-button>
    &nbsp;<a-button type="primary" @click="showForm">{{ $t('Filter with Index') }}</a-button>
    
    <div v-if="isShowForm">
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
    </div>

    <div v-if="isShowResult">
      <br/>
      <h2>{{ $t('Feature Result') }}</h2>
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


export default {
  data() {
    return {
      allTableNames: [],
      allColumnNames: [],

      chooseTableName: "",
      chooseColumnNames: [],

      isShowForm: false,

    

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
      axios.get(`/api/tables`)
        .then(response => {
          this.tableNames = [];
          response.data.forEach((table) => {
            this.allTableNames.push(`${table.db}.${table.table}`);
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

    updateChooseTableName() {
      if (this.chooseTableName === "") {
        // Check if pass table name
        notification["error"]({
            message: this.$t('Please select feature table'),
            description: errorMessage
          });
      } else {
        // Set columns list for user to choose
        const db = this.chooseTableName.split(".")[0];
        const table = this.chooseTableName.split(".")[1];

        axios.get(`/api/tables/${db}/${table}/schema`)
          .then(response => {
            this.allColumnNames = response.data.split(",").map(obj => obj.split(":")[0]);

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
          })


        axios.get(`/api/tables/${db}/${table}/indexes`)
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

      }
    },

    executeOnlineSql(sql) {
      axios
        .post(`/api/sql/online`, {
          sql: sql
        })
        .then((response) => {
          notification["success"]({
              message: this.$t('Execute Success'),
              description: `Success to execute SQL: ${sql}`
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
        .catch((error) => {
          if ('response' in error && 'data' in error.response) {
            notification["error"]({
              message: this.$t('Execute Fail'),
              description: error.response.data
            });
          } else {
            notification["error"]({
              message: this.$t('Execute Fail'),
              description: error.message
            });
          }
        });
    },

    generateSampleFeatures() {
      if (this.chooseTableName === "") {
        // Check if pass table name
        notification["error"]({
            message: this.$t('Please select feature table'),
            description: errorMessage
          });
      } else {
        if (this.chooseColumnNames.length == 0) {
          const sql = `SELECT * FROM ${this.chooseTableName} LIMIT 10`;
          this.executeOnlineSql(sql);
        } else {
          const sql = `SELECT ${this.chooseColumnNames.join(',')} FROM ${this.chooseTableName} LIMIT 10`;
          this.executeOnlineSql(sql);
        }

      }
    },

    changeFilterColumnNames() {
      const columnNameList = this.formState.chooseColumnNames.split(",")

      this.formInputColumns = this.mainTableColumns.filter(item => columnNameList.includes(item.name));
    },

    showForm() {
      if (this.isShowForm) {
        this.isShowForm = false;
      } else {
        this.isShowForm = true;
      }
    },

    submitForm() {
        let condiction = "";

        for (var i=0; i< this.formInputColumns.length; ++i) {
          const columnName = this.formInputColumns[i]["name"];
          const type = this.formInputColumns[i]["type"];
          
          let columnDataString = this.formState.columnDataList[i];

          if (type == "string") {
            columnDataString =  `'${columnDataString}'`;
          } 


          if (i == 0) {
            condiction += `${columnName} = ${columnDataString}`
          } else {
            condiction += ` AND ${columnName} = ${columnDataString}`
          }
        }

        let selectColumnsString = "*";
        if (this.chooseColumnNames.length != 0) {
          selectColumnsString = this.chooseColumnNames.join(',')
        }

        const sql = `SELECT ${selectColumnsString} FROM ${this.chooseTableName} WHERE ${condiction}`;

        this.executeOnlineSql(sql);
      },
      
  }
}
</script>