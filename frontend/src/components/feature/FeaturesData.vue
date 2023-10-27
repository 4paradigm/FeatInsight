<template>
<div>

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
  
  <!-- Data table -->
  <a-input v-model:value="searchText" :placeholder="$t('Search')" @change="handleSearch" />
  <br/><br/>

  <a-table :columns="columns" :data-source="searchFilteredFeatures" :loading="loading">
    <template #featureView="{ text, record }">
      <a-button type="link" @click="openFeatureViewDrawer(record.featureViewName)">{{ record.featureViewName }}</a-button>
      </template>
    <template #name="{ text, record }">
      <a-button type="link" @click="openFeatureDrawer(record.featureViewName, record.featureName)">{{ record.featureName }}</a-button>
    </template>
  </a-table>

</div>
</template>
  
<script>
import axios from 'axios'
import { message } from 'ant-design-vue';
import FeatureViewDetail from '@/components/featureview/FeatureViewDetail.vue'
import FeatureDetail from '@/components/feature/FeatureDetail.vue'

export default {
  components: {
    FeatureViewDetail,
    FeatureDetail
  },

  data() {
    return {
      isOpenFeatureViewDrawer: false,
      currentDrawerFeatureView: "",
      isOpenFeatureDrawer: false,
      currentDrawerFeature: "",

      searchText: "",
      searchFilteredFeatures: [],

      features: [],

      loading: false,
      
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

      formState: {
        sql: '',
      }
    };
  },

  mounted() {
    this.initData();
  },

  methods: {
    initData() {
      this.loading = true;
      axios.get(`/api/features`)
        .then(response => {
          this.features = response.data;
          this.searchFilteredFeatures = this.features;
        })
        .catch(error => {
          message.error(error.message);
        })
        .finally(() => {
          this.loading = false;
        });
    },

    matchSearch(item) {
        return item.featureViewName.toLowerCase().includes(this.searchText.toLowerCase())
          || item.featureName.toLowerCase().includes(this.searchText.toLowerCase())
          || item.description.toLowerCase().includes(this.searchText.toLowerCase());
    },

    handleSearch() {
      if (this.searchText === "") {
        this.searchFilteredFeatures = this.features;
      } else {
        this.searchFilteredFeatures = this.features.filter((item) => this.matchSearch(item));
      }
    },

    openFeatureViewDrawer(featureView) {
      this.isOpenFeatureViewDrawer = true;
      this.currentDrawerFeatureView = featureView;
    },

    openFeatureDrawer(featureView, feature) {
      this.isOpenFeatureDrawer = true;
      this.currentDrawerFeatureView = featureView;
      this.currentDrawerFeature = feature;
    }
  }
};
</script>