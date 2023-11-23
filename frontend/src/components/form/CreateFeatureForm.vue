<template>
<div>

  <a-modal v-model:visible="isShowDagPageModal" width="1000px" :title="$t('Visual SQL Tool')" @ok="clickModalOk">
    <DagPage ref="DagPage" @updateSql="updateOutputSql"></DagPage>
  </a-modal>

  <a-typography-paragraph>
    <!--
    <pre>{{ $t("Text of introduce create feature view") }} <a target="blank" href="https://openmldb.ai/docs/zh/main/openmldb_sql/dql/index.html">{{$t('OpenMLDB documents')}}</a></pre>
    -->
    <pre>用户使用 SQL SELECT 语句创建特征，每个 SQL 对应一个特征组，系统自动分析出 SQL 对应的特征数量和类型，更多用法参考<a target="blank" href="https://openmldb.ai/docs/zh/main/openmldb_sql/dql/index.html">官方文档</a>。</pre>
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
      <a-select show-search v-model:value="formState.db">
        <option v-for="database in databases" :value="database">{{ database }}</option>
      </a-select>
    </a-form-item>

    <a-form-item
      :label="$t('Description')">
      <a-input v-model:value="formState.description" />
    </a-form-item>

    <a-form-item
      :label="$t('SQL')"
      @change="changeSql"
      :rules="[{ required: true, message: 'Please input SQL!' }]">

      <a-button @click="clickDagPage"> {{ $t('Visual SQL Tool') }}</a-button>
      <br/><br/>
      <a-textarea v-model:value="formState.sql" :rows="5" placeholder="select * from t1">
      </a-textarea>
    </a-form-item>

    <div v-if="!isShowAnalyseButton">
      <h3>{{ $t('Feature List') }}</h3>

      <ul>
        <li v-for="(featureName, index) in validatedFeatureNames" :key="featureName">
          <a-row :gutter="16">
            <a-col :span="6">
              {{ $t('Feature') }}{{index + 1}}: {{ featureName }}
            </a-col>
            <a-col :span="2">
              {{ $t('Feature Description') }}:
            </a-col>
            <a-col :span="8">
              <a-input v-model:value="formState.featureDescriptionMap[featureName]" />
            </a-col>
          </a-row>
        </li>
      </ul>
    </div>

    <a-form-item>
        <a-button v-if="isShowAnalyseButton" type="primary" @click="validateForm()">{{ $t('Analyze') }} {{ $t('SQL') }}</a-button>
        <a-button v-else type="primary" @click="handleSubmit()">{{ $t('Submit') }}</a-button>
    </a-form-item>
  </a-form>
  
</div>
</template>
  
<script>
import axios from 'axios'
import { notification } from 'ant-design-vue'
import DagPage from '@/components/DAG/DagPage.vue'
import { notification } from 'ant-design-vue'

export default {
  components: {
    DagPage
  },

  data() {
    return {
      databases: [],

      validatedFeatureNames: [],

      isShowAnalyseButton: true,
      isShowDagPageModal: false,

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
          notification["error"]({
              message: this.$t('Execute Fail'),
              description: error.message
            });
        })
        .finally(() => {});
    },

    validateForm() {
      axios.post(`/api/featureviews/validate`, {
        "name": this.formState.name,
        "db": this.formState.db,
        "sql": this.formState.sql
      })
      .then(response => {
        message.success(`Success to validate feature view ${this.formState.name}`);
        notification["error"]({
              message: this.$t('Execute Fail'),
              description: error.response.data
            });

        this.validatedFeatureNames = response.data.split(",").map(str => str.trim());
        this.isShowAnalyseButton = false;
      })
      .catch(error => {
        if (error.response.data) {
          notification["error"]({
            message: this.$t('Validate Fail'),
            description: error.response.data
          });
        } else {
          notification["error"]({
            message: this.$t('Validate Fail'),
            description: error.message
          });
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
        notification["success"]({
          message: this.$t('Execute Fail'),
          description: `${this.$t('Success to create feature view')}: ${this.formState.name}`
        });

        this.$emit('submitted', this.formState.name);
      })
      .catch(error => {
        if (error.response.data) {
          notification["error"]({
            message: this.$t('Validate Fail'),
            description: error.response.data
          });
        } else {
          notification["error"]({
            message: this.$t('Validate Fail'),
            description: error.message
          });
        }
      });
    },

    clickDagPage() {
      this.isShowDagPageModal = true;
    },

    updateOutputSql(outSql){
      this.isShowDagPageModal = false;
      this.formState.sql = outSql;
    },

    clickModalOk() {
      this.isShowDagPageModal = false;
      this.$refs.DagPage.updateValue();
    },

    changeSql() {      
      this.isShowAnalyseButton = true;
    }

  },
};
</script>
