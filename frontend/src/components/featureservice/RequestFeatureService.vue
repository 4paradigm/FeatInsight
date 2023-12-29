<template>
<div>

  <br/>
  <a-typography>
    <a-typography-title :level="2">{{ $t('Request Feature Service') }}</a-typography-title>
    <a-typography-paragraph>
    </a-typography-paragraph>
  </a-typography>

  <a-typography-paragraph>
    <!-- <pre>{{ $t("Text of introduce test feature service") }} <a target="blank" href="https://openmldb.ai/docs/zh/main/quickstart/sdk/rest_api.html">{{$t('OpenMLDB documents')}}</a></pre> -->
    <pre>用户可以使用“请求模式”或“在线查询模式”来访问特征服务，请求模式下用户需要输入整行主表数据通过 LastJoin 等方法提取副表特征；在线查询模式则不需要传入数据，直接查询在线表的特征数据，并且可以通过主键进行数据过滤。</pre>
  </a-typography-paragraph>


  <a-menu mode="horizontal">
    <a-menu-item key="1">
      <router-link :to="`/featureservices/${name}/${version}/request/requestmode`">请求模式</router-link>
    </a-menu-item>

    <a-menu-item key="2">
      <router-link :to="`/featureservices/${name}/${version}/request/onlinequerymode`">在线查询模式</router-link>
    </a-menu-item>
  </a-menu>


  <router-view></router-view>

<!--
  <a-typography>
    <a-typography-paragraph>
      <blockquote>
        用户可以使用“请求模式”或“在线查询模式”来访问特征服务。
        请求模式下用户需要输入整行主表数据通过 LastJoin 等方法提取副表特征。
        在线查询模式则不需要传入数据，直接查询在线表的特征数据，并且可以通过主键进行数据过滤。
      </blockquote>
    </a-typography-paragraph>
  </a-typography>


  <br/>
  <a-descriptions bordered>
    <a-descriptions-item :span="24" :label="$t('Name')">{{ data.name }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Version')"> {{ data.version }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Feature Names')">{{ data.featureNames }}</a-descriptions-item>
  </a-descriptions>
-->
</div>
</template>
    
<script>
import axios from 'axios'
import { notification } from 'ant-design-vue'
import TableDetail from '@/components/table/TableDetail.vue'
import DatabaseDetail from '@/components/database/DatabaseDetail.vue'
import FeatureViewDetail from '@/components/featureview/FeatureViewDetail.vue'
import FeatureDetail from '@/components/feature/FeatureDetail.vue'
import TestFeatureServiceForm from '@/components/form/TestFeatureServiceForm.vue';

export default {
  components: {
    FeatureViewDetail,
    FeatureDetail,
    DatabaseDetail,
    TableDetail,
    TestFeatureServiceForm
  },

  props: {
    name: {
      type: String,
      required: true
    },
    version: {
      type: String,
      required: true
    }
  },

  mounted() {
    this.initData();
  },
  
  data() {
    return {
      isOpenFeatureViewDrawer: false,
      currentDrawerFeatureView: "",
      isOpenFeatureDrawer: false,
      currentDrawerFeature: "",
      isOpenDatabaseDrawer: false,
      currentDrawerDatabase: "",
      isOpenTableDrawer: false,
      currentDrawerTable: "",

      isOpenTestModal: false,

      data: {},
      features: [],

      columns: [{
          title: this.$t('Feature View'),
          dataIndex: 'featureViewName',
          key: 'featureViewName',
          slots: { customRender: 'featureView' }
        },
        {
          title: this.$t('Feature Name'),
          dataIndex: 'featureName',
          key: 'featureName',
          slots: { customRender: 'name' }
        },
        {
          title: this.$t('Type'),
          dataIndex: 'type',
          key: 'type',
        },
        {
          title: this.$t('Description'),
          dataIndex: 'description',
          key: 'description',
      }],

      tables: [],

      tableColumns: [{
          title: this.$t('Database'),
          dataIndex: 'db',
          key: 'db',
          slots: { customRender: 'db' }
        },
        {
          title: this.$t('Table'),
          dataIndex: 'table',
          key: 'table',
          slots: { customRender: 'table' }
        }],
          
      requestSchema: "",
      requestDemoData: ""
    };
  },

  methods: {
    initData() {
      axios.get(`/api/featureservices/${this.name}/${this.version}`)
        .then(response => {
          this.data = response.data;

          // Request features from feature view
          axios.get(`/api/features?featureServiceName=${this.data.name}&featureServiceVersion=${this.data.version}`)
            .then(response => {
              this.features = response.data;
            })
            .catch(error => {
              notification["error"]({
                message: this.$t('Execute Fail'),
                description: error.message
              });
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

      axios.get(`/api/featureservices/${this.name}/${this.version}/tables`)
        .then(response => {            
          response.data.forEach(str => {
            let [db, table] = str.split('.');
            this.tables.push({"db": db, "table": table});
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

      axios.get(`/api/featureservices/${this.name}/${this.version}/request/schema`)
        .then(response => {
          this.requestSchema = response.data;
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

    openFeatureViewDrawer(featureView) {
      this.isOpenFeatureViewDrawer = true;
      this.currentDrawerFeatureView = featureView;
    },

    openFeatureDrawer(featureView, feature) {
      this.isOpenFeatureDrawer = true;
      this.currentDrawerFeatureView = featureView;
      this.currentDrawerFeature = feature;
    },

    openDatabaseDrawer(db) {
      this.isOpenDatabaseDrawer = true;
      this.currentDrawerDatabase = db;
    },

    openTableDrawer(db, table) {
      this.isOpenTableDrawer = true;
      this.currentDrawerDatabase = db;
      this.currentDrawerTable = table;
    },

    showTestFormModal(name, version) {
      this.chooseFeatureServiceName = name;
      this.chooseFeatureServiceVersion = version;

      this.isOpenTestModal = true;
    },

    clickTestModalOk() {
      this.$refs.TestFeatureServiceForm.submitForm();
    },

    showTestFormModal() {
      this.isOpenTestModal = true;
    },

    clickTestModalOk() {
      this.$refs.TestFeatureServiceForm.submitForm();
    }

  }
}
</script>