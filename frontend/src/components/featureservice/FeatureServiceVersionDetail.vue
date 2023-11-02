<template>
<div>

  <a-modal v-model:visible="isOpenTestModal" width="1000px" :title="$t('Test Feature Service')" @ok="clickTestModalOk" >
    <TestFeatureServiceForm ref="TestFeatureServiceForm" :featureServiceName="name" :featureServiceVersion="version" ></TestFeatureServiceForm>
  </a-modal>

  <a-drawer
    v-model:visible="isOpenFeatureViewDrawer"
    size="large"
    :title="$t('Feature View') + $t('Detail')">
    <FeatureViewDetail :name="currentDrawerFeatureView" :key="currentDrawerFeatureView"></FeatureViewDetail>
  </a-drawer>

  <a-drawer
    v-model:visible="isOpenFeatureDrawer"
    size="large"
    :title="$t('Feature') + $t('Detail')">
    <FeatureDetail :featureViewName="currentDrawerFeatureView" :featureName="currentDrawerFeature" :key="currentDrawerFeatureView+currentDrawerFeature"></FeatureDetail>
  </a-drawer>

  <a-drawer
    v-model:visible="isOpenDatabaseDrawer"
    size="large"
    :title="$t('Database') + $t('Detail')">
    <DatabaseDetail :db="currentDrawerDatabase" :key="currentDrawerDatabase"></DatabaseDetail>
  </a-drawer>

  <a-drawer
    v-model:visible="isOpenTableDrawer"
    size="large"
    :title="$t('Table') + $t('Detail')">
    <TableDetail :db="currentDrawerDatabase" :name="currentDrawerTable" :key="currentDrawerTable"></TableDetail>
  </a-drawer>

  <h2>{{ $t('Feature Service') }}: {{ data.name }}</h2>
  <h2>{{ $t('Service Version') }}: {{ data.version }}</h2>

  <br/>
  <a-descriptions bordered>
    <a-descriptions-item :span="24" :label="$t('Name')">{{ data.name }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Version')"> {{ data.version }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Feature Names')">{{ data.featureNames }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Database')">
      <a-button type="link" @click="openDatabaseDrawer(data.db)">{{ data.db }}</a-button>
    </a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('SQL')">{{ data.sql }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Deployment')">{{ data.deployment }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label='$t("Description")'>{{ data.description }}</a-descriptions-item>
  </a-descriptions>

  <br/>
  <a-button type="default" @click="showTestFormModal()">{{ $t('Test Service Version') }}</a-button>
    

  <br/><br/>
  <h2>{{ $t('Features') }}</h2>
  <a-table :columns="columns" :data-source="features">
    <template #featureView="{ text, record }">
      <a-button type="link" @click="openFeatureViewDrawer(record.featureViewName)">{{ record.featureViewName }}</a-button>
    </template>
    <template #name="{ text, record }">
      <a-button type="link" @click="openFeatureDrawer(record.featureViewName, record.featureName)">{{ record.featureName }}</a-button>
    </template>
  </a-table>

  <br/>
  <h2>{{ $t('Dependent Tables') }}</h2>
  <a-table :columns="tableColumns" :data-source="tables">
    <template #db="{ text, record }">
      <a-button type="link" @click="openDatabaseDrawer(record.db)">{{ record.db }}</a-button>
    </template>
    <template #table="{ text, record }">
      <a-button type="link" @click="openTableDrawer(record.db, record.table)">{{ record.table }}</a-button>
    </template>
  </a-table>
</div>
</template>
    
<script>
import axios from 'axios'
import { message } from 'ant-design-vue';
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
              message.error(error.message);
            });
        })
        .catch(error => {
          message.error(error.message);
        })

      axios.get(`/api/featureservices/${this.name}/${this.version}/tables`)
        .then(response => {            
          response.data.forEach(str => {
            let [db, table] = str.split('.');
            this.tables.push({"db": db, "table": table});
          });
        })
        .catch(error => {
          message.error(error.message);
        });

      axios.get(`/api/featureservices/${this.name}/${this.version}/request/schema`)
        .then(response => {
          this.requestSchema = response.data;

        })
        .catch(error => {
          message.error(error.message);
        });

      axios.get(`/api/featureservices/${this.name}/${this.version}/request/demo`)
        .then(response => {
          this.requestDemoData = response.data;
        })
        .catch(error => {
          message.error(error.message);
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