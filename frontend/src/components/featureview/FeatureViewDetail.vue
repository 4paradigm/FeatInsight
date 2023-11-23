<template>
<div>

  <a-drawer
    v-model:visible="isOpenDatabaseDrawer"
    size="large"
    :title="$t('Database') + $t('Detail')">
    <DatabaseDetail :db="currentDrawerDatabase" :key="currentDrawerDatabase"></DatabaseDetail>
  </a-drawer>

  <a-drawer
    v-model:visible="isOpenFeatureDrawer"
    size="large"
    :title="$t('Feature') + $t('Detail')">
    <FeatureDetail :featureViewName="currentDrawerFeatureView" :featureName="currentDrawerFeature" :key="currentDrawerFeatureView+currentDrawerFeature"></FeatureDetail>
  </a-drawer>

  <a-drawer
    v-model:visible="isOpenTableDrawer"
    size="large"
    :title="$t('Table') + $t('Detail')">
    <TableDetail :db="currentDrawerDatabase" :name="currentDrawerTable" :key="currentDrawerDatabase+currentDrawerTable"></TableDetail>
  </a-drawer>

  <h2>{{ $t('Feature View') }}: {{ data.name }}</h2>
  
  <a-descriptions bordered>
    <a-descriptions-item :span="24" :label='$t("Name")'> {{ data.name }} </a-descriptions-item>
    <a-descriptions-item :span="24" :label='$t("Database")'>
      <a-button type="link" @click="openDatabaseDrawer(data.db)">{{ data.db }}</a-button>
    </a-descriptions-item>
    <a-descriptions-item :span="24" :label='$t("SQL")'>{{ data.sql }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label='$t("Features")'>{{ data.featureNames }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label='$t("Description")'>{{ data.description }}</a-descriptions-item>
  </a-descriptions>

  <br/><br/>
  <h2>{{ $t('Features') }}</h2>
  <a-table :columns="columns" :data-source="features">
    <template #featureView="{ text, record }">
      {{ record.featureViewName }}
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
import axios from 'axios';
import { notification } from 'ant-design-vue'
import DatabaseDetail from '@/components/database/DatabaseDetail.vue'
import FeatureDetail from '@/components/feature/FeatureDetail.vue'
import TableDetail from '@/components/table/TableDetail.vue'

export default {
  components: {
    DatabaseDetail,
    FeatureDetail,
    TableDetail
  },

  props: {
    name: {
      type: String,
      required: true,
    },
  },

  data() {
    return {
      isOpenDatabaseDrawer: false,
      currentDrawerDatabase: "",
      currentDrawerFeatureView: "",
      isOpenFeatureDrawer: false,
      currentDrawerFeature: "",
      isOpenTableDrawer: false,
      currentDrawerTable: "",

      data: "",
      features: [],
      columns: [
        {
          title: this.$t('Feature View'),
          dataIndex: 'featureViewName',
          key: 'featureViewName',
          slots: { customRender: 'featureView' },
        },
        {
          title: this.$t('Feature Name'),
          dataIndex: 'featureName',
          key: 'featureName',
          slots: { customRender: 'name' },
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
        },
      ],
      tables: [],
      tableColumns: [
        {
          title: this.$t('Database'),
          dataIndex: 'db',
          key: 'db',
          slots: { customRender: 'db' },
        },
        {
          title: this.$t('Table'),
          dataIndex: 'table',
          key: 'table',
          slots: { customRender: 'table' },
        },
      ],
    };
  },

  mounted() {
    this.initData();
  },

  methods: {
    initData() {
      axios
        .get(`/api/featureviews/${this.name}`)
        .then((response) => {
          this.data = response.data;
        })
        .catch((error) => {
          if (error.response != null) {
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

      // Request features from feature view
      axios
        .get(`/api/features/${this.name}`)
        .then((response) => {
          this.features = response.data;
        })
        .catch((error) => {
          if (error.response != null) {
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

      axios
        .get(`/api/featureviews/${this.name}/tables`)
        .then((response) => {
          response.data.forEach((str) => {
            let [db, table] = str.split('.');
            this.tables.push({ db: db, table: table });
          });
        })
        .catch((error) => {
          if (error.response != null) {
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

    openDatabaseDrawer(db) {
      this.isOpenDatabaseDrawer = true;
      this.currentDrawerDatabase = db;
    },

    openFeatureDrawer(featureView, feature) {
      this.isOpenFeatureDrawer = true;
      this.currentDrawerFeatureView = featureView;
      this.currentDrawerFeature = feature;
    },

    openTableDrawer(db, table) {
      this.isOpenTableDrawer = true;
      this.currentDrawerDatabase = db;
      this.currentDrawerTable = table;
    }

  }

};
</script>