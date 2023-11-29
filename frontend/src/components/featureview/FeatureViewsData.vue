<template>
<div>

  <a-drawer
    v-model:visible="isOpenFeatureViewDrawer"
    size="large"
    :title="$t('Feature View') + $t('Detail')">
    <FeatureViewDetail :name="currentDrawerFeatureView" :key="currentDrawerFeatureView"></FeatureViewDetail>
  </a-drawer>

  <a-drawer
    v-model:visible="isOpenDatabaseDrawer"
    size="large"
    :title="$t('Database') + $t('Detail')">
    <DatabaseDetail :db="currentDrawerDatabase" :key="currentDrawerDatabase"></DatabaseDetail>
  </a-drawer>

  <!-- Data table -->
  <a-input v-model:value="searchText" :placeholder="$t('Search')" @change="handleSearch" />
  <br/><br/>

  <a-table :columns="columns" :data-source="searchFilteredFeatureViews">
    <template #name="{ text, record }">
      <a-button type="link" @click="openFeatureViewDrawer(record.name)">{{ record.name }}</a-button>
    </template>
    <template #db="{ text, record }">
      <a-button type="link" @click="openDatabaseDrawer(record.db)">{{ record.db }}</a-button>
    </template>  
    <!-- The delete column-->
    <template v-slot:custom="scope">
      <a-popconfirm
          title="Sure to delete?"
          @confirm="handleDelete(scope.record.name)">
        <a>{{ $t('Delete') }}</a>
      </a-popconfirm>
    </template>
  </a-table>

</div>
</template>
  
<script>
import axios from 'axios'
import { notification } from 'ant-design-vue'
import FeatureViewDetail from '@/components/featureview/FeatureViewDetail.vue'
import DatabaseDetail from '@/components/database/DatabaseDetail.vue'

export default {
  components: {
    DatabaseDetail,
    FeatureViewDetail
  },

  data() {
    return {
      isOpenFeatureViewDrawer: false,
      currentDrawerFeatureView: "",
      isOpenDatabaseDrawer: false,
      currentDrawerDatabase: "",

      searchText: "",
      searchFilteredFeatureViews: [],

      featureViews: [],
      
      columns: [{
        title: 'Name',
        dataIndex: 'name',
        key: 'name',
        slots: { customRender: 'name' }
      },
      {
        title: 'Database',
        dataIndex: 'db',
        key: 'db',
        slots: { customRender: 'db' }
      },
      {
        title: 'SQL',
        dataIndex: 'sql',
        key: 'sql',
      },
      {
        title: 'Description',
        dataIndex: 'description',
        key: 'description',
      },
      {
        title: this.$t('Actions'),
        key: 'actions',
        slots: { customRender: 'custom' },
      }],

    };
  },

  mounted() {
    this.initData();

  },

  methods: {
    initData() {
      axios.get(`/api/featureviews`)
        .then(response => {
          this.featureViews = response.data;
          this.searchFilteredFeatureViews = this.featureViews;
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

    handleDelete(name) {
      axios.delete(`/api/featureviews/${name}`)
      .then(response => {
        notification["success"]({
              message: this.$t('Execute Success'),
              description: `Success to delete feature view: ${name}`
            });

        this.initData();
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

    matchSearch(item) {
        return item.name.toLowerCase().includes(this.searchText.toLowerCase())
          || item.entityNames.toLowerCase().includes(this.searchText.toLowerCase())
          || item.db.toLowerCase().includes(this.searchText.toLowerCase())
          || item.featureNames.toLowerCase().includes(this.searchText.toLowerCase())
          || item.description.toLowerCase().includes(this.searchText.toLowerCase());
    },

    handleSearch() {
      if (this.searchText === "") {
        this.searchFilteredFeatureViews = this.featureViews;
      } else {
        this.searchFilteredFeatureViews = this.featureViews.filter((item) => this.matchSearch(item));
      }
    },

    openFeatureViewDrawer(featureView) {
      this.isOpenFeatureViewDrawer = true;
      this.currentDrawerFeatureView = featureView;
    },

    openDatabaseDrawer(db) {
      this.isOpenDatabaseDrawer = true;
      this.currentDrawerDatabase = db;
    }

  },
};
</script>