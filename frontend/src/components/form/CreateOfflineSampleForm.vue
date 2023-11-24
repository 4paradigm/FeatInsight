<template>

<div>
    <!-- Create Feature Form Modal -->
    <a-modal v-model:visible="isShowCreateFeatureModal" width="1000px" :title="$t('Create Feature View')" >
      <template #footer>
        <a-button @click="handleCancel">Cancel</a-button>
      </template>
      <CreateFeatureForm @submitted="submittedCreateFeatureForm"></CreateFeatureForm>
    </a-modal>

    <a-typography-paragraph>
      <pre>{{ $t("Text of introduce create offline sample") }} <a target="blank" href="https://openmldb.ai/docs/zh/main/openmldb_sql/dql/SELECT_INTO_STATEMENT.html">{{$t('OpenMLDB documents')}}</a></pre>
    </a-typography-paragraph>
    <br/>

    <!-- Create form -->
    <a-form
      :model="formState"
      layout="vertical"
      name="basic"
      @submit="submitForm">

      <a-form-item
        :label="$t('Choose Features')"
        :rules="[{ required: true, message: 'Please input feature list!' }]">

        <a-select mode="multiple" v-model:value="formState.featureSet"
          @change="handleFeatureSet"
          :options="featureOptions.map(featureOption => ({ value: featureOption }))">
        
          <template #dropdownRender="{ menuNode: menu }">
            <VNodes :vnodes="menu" />
            <a-divider style="margin: 4px 0" />
            <a-space style="padding: 4px 8px">
              <a-button type="text" @click="clickCreateFeature">
                <template #icon>
                  <PlusOutlined />
                </template>
                {{ $t('Create Feature') }}
              </a-button>
            </a-space>
          </template>
        </a-select>
      </a-form-item>

      <a-form-item
          v-if="isShowMainTableKeys"
          :label="$t('Main Table Keys')"
          :rules="[{ required: true, message: 'Please input main table keys' }]">
          <a-tooltip>
            <template #title>{{$t('Text of introduce join keys')}}</template>
            <a-input id="mainTableKeys" v-model:value="formState.mainTableKeys"></a-input>
          </a-tooltip>
      </a-form-item>

      <a-form-item
          :label="$t('File Format')"
          :rules="[{ required: true, message: 'Please choose file format!' }]">
          <a-select show-search v-model:value="formState.format">
            <option v-for="format in formatOptions" :key="format.id" :value="format.name">{{ format.name}}</option>
          </a-select>
      </a-form-item>

      <a-form-item 
          :label="$t('Export Path')" 
          :rules="[{ required: true, message: 'Please input export path!' }]">
          <a-tooltip>
            <template #title>{{$t('Text of introduce path')}}</template>
            <a-input id="exportPath" v-model:value="formState.path" :placeholder="$t('Path Hint')"></a-input>
          </a-tooltip>
      </a-form-item>

      <a-form-item 
          :label="$t('Spark Config')">
          <a-tooltip>
            <template #title>Spark config like 'spark.executor.memory=2g;spark.executor.cores=2'</template>
            <a-input v-model:value="formState.sparkConfig"></a-input>
          </a-tooltip>
      </a-form-item>

      <a-form-item 
          :label="$t('Export Options')">
          <a-tooltip>
            <template #title><a target="blank" href="https://openmldb.ai/docs/zh/main/openmldb_sql/dql/SELECT_INTO_STATEMENT.html">{{$t('Reference document of options')}}</a></template>
            <a-input id="exportOptions" v-model:value="formState.options"></a-input>
          </a-tooltip>
      </a-form-item>
    </a-form>

</div>
</template>
  
<script>
import axios from 'axios'
import { notification } from 'ant-design-vue'
import CreateFeatureForm from '@/components/form/CreateFeatureForm.vue'
import VNodes from '@/components/VNodes.vue'
import { PlusOutlined } from '@ant-design/icons-vue'

export default {
  components: {
    CreateFeatureForm,
    VNodes,
    PlusOutlined
  },

  data() {
    return {
      isShowCreateFeatureModal: false,

      isShowMainTableKeys: false,

      featureOptions: [],

      formatOptions:[
        {id: 'csv', name: 'CSV'},
        {id: 'parquet', name: 'Parquet'}
      ],

      formState: {
        featureSet: [],
        mainTableKeys: "",
        format: 'CSV',
        path: '',
        sparkConfig: '',
        options: '',
      }

    };
  },

  mounted() {
    this.initData();
  },

  methods: {
    initData() {
      this.updateSelectFeatureOptions();
    },

    updateSelectFeatureOptions() {
      this.featureOptions = [];

      axios.get(`/api/featureviews`)
        .then(response => {
          response.data.forEach(featureView => {
            // Append feature view name
            this.featureOptions.push(featureView.name)
          });
        })
        .catch(error => {
          notification["error"]({
              message: this.$t('Execute Fail'),
              description: error.message
            });
        })
        .finally(() => {});

      axios.get(`/api/features`)
        .then(response => {
          response.data.forEach(feature => {
            // Append complete feature name
            const completeFeatureName = feature.featureViewName + ":" + feature.featureName;
            this.featureOptions.push(completeFeatureName);
          });
        })
        .catch(error => {
          notification["error"]({
              message: this.$t('Execute Fail'),
              description: error.message
            });
        })
        .finally(() => {});
    },

    submitForm() {
      axios.post(`/api/offlinesamples`, {
        "featureNames": this.formState.featureSet.join(','),
        "path": this.formState.path,
        "options": this.formState.options,
        "mainTableKeys": this.formState.mainTableKeys,
        "sparkConfig": this.formState.sparkConfig
      })
      .then(response => {
        notification["success"]({
          message: this.$t('Execute Success'),
          description: `Success to export offline sample for feature list ${this.formState.featureSet}`
        });

        const jobId = response.data.jobId;
        // Redirect to result page
        this.$router.push(`/offlinesamples/${jobId}/result`);
      })
      .catch(error => {
          if (error.response.data) {
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

    clickCreateFeature() {
      this.isShowCreateFeatureModal = true;
    },

    handleCancel() {
      this.isShowCreateFeatureModal = false;
    },

    submittedCreateFeatureForm(newFeatureViewName) {
      this.isShowCreateFeatureModal = false;
      this.updateSelectFeatureOptions();
    },

    handleFeatureSet() {
      // Check if we have two different feature views
      var firstFeatureViewName = "";

      for (var i=0; i < this.formState.featureSet.length; ++i) {
        const featureViewName = this.formState.featureSet[i].split(":")[0];
        if (firstFeatureViewName === "") {
          firstFeatureViewName = featureViewName;

        } else {
          if (firstFeatureViewName !== featureViewName) {
            this.isShowMainTableKeys = true;
            return;
          }
        }
      }

      this.isShowMainTableKeys = false;
    }

  }
};
</script>
