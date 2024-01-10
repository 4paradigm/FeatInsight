<template>
  <div>

    <h2>{{ $t('Feature') }}: {{ feature.featureName }} </h2>
    <a-descriptions bordered>
      <a-descriptions-item :span="24" :label="$t('Feature View Name')">{{ feature.featureViewName }}</a-descriptions-item>
      <a-descriptions-item :span="24" :label="$t('Feature Name')">{{ feature.featureName }}</a-descriptions-item>
      <a-descriptions-item :span="24" :label="$t('Type')">{{ feature.type}}</a-descriptions-item>
      <a-descriptions-item :span="24" :label="$t('Description')">{{ feature.description}}</a-descriptions-item>

      <a-descriptions-item :span="24" :label="$t('Source SQL')">{{ featureSourceSql }}</a-descriptions-item>

    </a-descriptions>
  </div>

  <br/>
  <a-button type="primary" @click="randomlyGenerateFeatures">{{ $t('Display Sample Features') }}</a-button>
  <!-- &nbsp;<a-button type="primary" @click="showForm">通过索引过滤特征</a-button> -->

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

</template>
  
<script>
import axios from 'axios';
import { notification } from 'ant-design-vue'
import { onMounted, ref } from 'vue'

export default {
  props: {
    featureViewName: {
      type: String,
      required: true,
    },
    featureName: {
      type: String,
      required: true,
    },
  },

  data() {
    return {
      feature: {},

      featureSourceSql: "",

      isShowResult: false,
      resultColumns: [],
      resultData: [],

      isShowForm: false,
      formState: {
        chooseColumnNames: [],
        columnDataList: []
      },

        // For exmaples: ["name", "name,age"]
        allIndexColumnNamesList: [],
        mainTableColumns: [],
        formInputColumns: [],

    };
  },

  mounted() {
    this.initData();
  },

  methods: {
    initData() {
      axios.get(`/api/features/${this.featureViewName}/${this.featureName}`)
        .then(response => {
          this.feature = response.data;
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

      axios.get(`/api/features/${this.featureViewName}/${this.featureName}/sourcesql`)
        .then(response => {
          this.featureSourceSql = response.data;
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

    randomlyGenerateFeatures() {

      axios.post(`/api/features/${this.featureViewName}/${this.featureName}/onlinequerymode/samples`)
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
    },

    showForm() {
      if (this.isShowForm) {
        this.isShowForm = false;
      } else {
        this.isShowForm = true;
      }
    },

    changeFilterColumnNames() {

    },

    submitForm() {
      
    }

  }


}
</script>
