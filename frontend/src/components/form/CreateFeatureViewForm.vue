<template>

  <div>
    <!-- Create Feature Form Modal -->
    <a-modal v-model:visible="isShowDagPageModal" width="1000px" :title="$t('Visual SQL Tool')" >
      <template #footer>
        <a-button @click="handleCancel">Update</a-button>
      </template>
      <DagPage></DagPage>
    </a-modal>

    <a-typography-paragraph>
      <pre>{{ $t("Text of introduce create feature view") }} <a target="blank" href="https://openmldb.ai/docs/zh/main/openmldb_sql/dql/index.html">{{$t('OpenMLDB documents')}}</a></pre>
    </a-typography-paragraph>
    <br/>

      <!-- Create form -->
      <a-form
        :model="formState"
        layout="vertical">

        <a-form-item
          :label="$t('Feature View Name')"
          :rules="[{ required: true, message: 'Please input name!' }]">
          <a-input v-model:value="formState.name" 
            placeholder="featureview1"/>
        </a-form-item>
  
        <a-form-item
          :label="$t('Database')"
          :rules="[{ required: true, message: 'Please select database!' }]">
          <a-select v-model:value="formState.db">
            <option v-for="database in databases" :value="database">{{ database }}</option>
          </a-select>
        </a-form-item>

        <a-form-item
          :label="$t('Description')">
          <a-input v-model:value="formState.description" />
        </a-form-item>
  
        <a-form-item
          :label="$t('SQL')"
          :rules="[{ required: true, message: 'Please input SQL!' }]">

          <a-button type="primary" @click="clickDagPage"> {{ $t('Visual SQL Tool') }}</a-button>
          <br/><br/>
          
          <a-textarea v-model:value="formState.sql" :rows="5" @blur="updateInputSQL"
            placeholder="select * from t1">
          </a-textarea>
        </a-form-item>

        <div v-if="validatedFeatureNames.length > 0">
          <h3>
            {{ $t('Feature List') }}:
            &nbsp;<a-button type="primary" @click="displayAddFeatureDescription()">{{ $t('Add Feature Description') }}</a-button>
          </h3>

          <ul>
            <li v-for="(featureName, index) in validatedFeatureNames" :key="featureName">
              <a-row :gutter="16">
                <a-col :span="6">
                  {{ $t('Feature') }}{{index + 1}}: {{ featureName }}
                </a-col>
                <a-col v-if="isDisplayAddFeatureDescription" :span="2">
                  {{ $t('Feature Description') }}:
                </a-col>
                <a-col v-if="isDisplayAddFeatureDescription" :span="8">
                  <a-input v-model:value="formState.featureDescriptionMap[featureName]" />
                </a-col>
              </a-row>
            </li>
          </ul>
        </div>

        <a-form-item>
            <a-button v-if="validatedFeatureNames.length == 0" type="primary" @click="validateForm()">{{ $t('Validate') }} {{ $t('SQL') }}</a-button>
            <a-button v-else type="primary" @click="handleSubmit()">{{ $t('Submit') }}</a-button>
        </a-form-item>
      </a-form>
      
    </div>
  </template>
    
  <script>
  import axios from 'axios'
  import { message } from 'ant-design-vue';
  import { SQLStore} from '@/pinia/store';
  import { computed } from 'vue';
  import DagPage from '@/components/DAG/DagPage.vue';

  export default {
    components: {
      DagPage
    },

    data() {
      return {
        databases: [],
  
        validatedFeatureNames: [],

        isDisplayAddFeatureDescription: false,
        isShowDagPageModal: false,

        sharedSQL: '',
        SQLStore: '',
  
        formState: {
          name: '',
          db: '',
          sql: '',
          description: '',
          featureDescriptionMap: {}
        }
      };
    },
  
    mounted() {
      this.initData();
    },
  
    methods: {
      initData() {
  
        axios.get(`/api/databases`)
          .then(response => {
            this.databases = response.data;
          })
          .catch(error => {
            message.error(error.message);
          })
          .finally(() => {});

        this.SQLStore = SQLStore();
        this.sharedSQL = computed(() => this.SQLStore.sharedSQL);  
        this.formState.sql = this.sharedSQL;
      },
  
      validateForm() {
        axios.post(`/api/featureviews/validate`, {
          "name": this.formState.name,
          "db": this.formState.db,
          "sql": this.formState.sql
        })
        .then(response => {
          message.success(`Success to validate feature view ${this.formState.name}`);
          this.validatedFeatureNames = response.data.split(",").map(str => str.trim());
        })
        .catch(error => {
          if (error.response.data) {
            message.error(error.response.data);
          } else {
            message.error(error.message);
          }
        });      
      },
  
      handleSubmit() {

        axios.post(`/api/featureviews`, {
          "name": this.formState.name,
          "db": this.formState.db,
          "sql": this.formState.sql,
          "description": this.formState.description,
          "featureNames": this.validatedFeatureNames,
          "featureDescriptionMap": this.formState.featureDescriptionMap
        })
        .then(response => {
          message.success(`Success to add feature view ${this.formState.name}`);
  
          this.$emit('submitted', this.formState.name);
        })
        .catch(error => {
          if (error.response.data) {
            message.error(error.response.data);
          } else {
            message.error(error.message);
          }
        });
      },

      displayAddFeatureDescription() {
        this.isDisplayAddFeatureDescription = true;
      },

      updateInputSQL(){
        this.SQLStore.setSharedVariable(this.formState.sql);
      },

      clickDagPage() {
        this.isShowDagPageModal = true;
      },

      handleCancel() {
      this.isShowDagPageModal = false;
      //update SQL code
      this.sharedSQL = computed(() => this.SQLStore.sharedSQL);  
      this.formState.sql = this.sharedSQL;
      },
  
    },
  };
  </script>