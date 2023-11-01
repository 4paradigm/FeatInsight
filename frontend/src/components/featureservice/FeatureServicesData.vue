<template>
<div>

  <a-drawer
    v-model:visible="isOpenFeatureServiceDrawer"
    size="large"
    :title="$t('Feature Service') + $t('Detail')">
    <FeatureServiceDetail :name="currentDrawerFeatureService" :key="currentDrawerFeatureService"></FeatureServiceDetail>
  </a-drawer>

  <a-drawer
    v-model:visible="isOpenFeatureServiceVersionDrawer"
    size="large"
    :title="$t('Feature Service') + $t('Detail')">
    <FeatureServiceVersionDetail :name="currentDrawerFeatureService" :version="currentDrawerFeatureServiceVersion" :key="currentDrawerFeatureService+currentDrawerFeatureServiceVersion"></FeatureServiceVersionDetail>
  </a-drawer>

  <a-modal v-model:visible="isOpenTestModal" width="1000px" :title="$t('Test Feature Service')" @ok="clickTestModalOk" >
    <TestFeatureService ref="TestFeatureService"></TestFeatureService>
  </a-modal>

  <!-- Data table -->
  <a-input v-model:value="searchText" :placeholder="$t('Search')" @change="handleSearch" />
  <br/><br/>

  <div>
    <a-checkbox v-model:checked="isDisplayAllVersion" @change="initData">{{ $t('Display All Versions')}}</a-checkbox>
  </div>

  <a-table :columns="columns" :data-source="searchFilteredFeatureServices">
    <template #name="{ text, record }">
      <a-button type="link" @click="openFeatureServiceDrawer(record.name)">{{ record.name }}</a-button>
    </template>
    <template #version="{ text, record }">
      <a-button type="link" @click="openFeatureServiceVersionDrawer(record.name, record.version)">{{ record.version }}</a-button>
    </template>
    <template v-slot:action="scope">
      <a-button type="default" @click="showTestFormModal">{{ $t('Test Service') }}</a-button>
    </template>
  </a-table>

</div>
</template>
  
<script>
import axios from 'axios'
import { message } from 'ant-design-vue';
import TestFeatureService from '@/components/TestFeatureService.vue';
import FeatureServiceDetail from '@/components/featureservice/FeatureServiceDetail.vue'
import FeatureServiceVersionDetail from '@/components/featureservice/FeatureServiceVersionDetail.vue'

export default {
  components: {
    TestFeatureService,
    FeatureServiceDetail,
    FeatureServiceVersionDetail
  },

  data() {
    return {
      isOpenFeatureServiceDrawer: false,
      currentDrawerFeatureService: "",
      isOpenFeatureServiceVersionDrawer: false,
      currentDrawerFeatureServiceVersion: "",

      isDisplayAllVersion: false,

      searchText: "",
      searchFilteredFeatureServices: [],

      featureServices: [],

      isOpenTestModal: false,
      
      columns: [{
        title: this.$t('Name'),
        dataIndex: 'name',
        key: 'name',
        slots: { customRender: 'name' }
      },
      {
        title: this.$t('Version'),
        dataIndex: 'version',
        key: 'version',
        slots: { customRender: 'version' }
      },
      {
        title: this.$t('Feature Names'),
        dataIndex: 'featureNames',
        key: 'featureNames',
      },
      {
        title: this.$t('Description'),
        dataIndex: 'description',
        key: 'description',
      },
      {
        title: this.$t('Actions'),
        key: 'actions',
        slots: { customRender: 'action' },
      }],

    };
  },

  mounted() {
    this.initData();
  },

  methods: {
    initData() {
      let requestUrl = "/api/featureservices/latest"
      if (this.isDisplayAllVersion) {
        requestUrl = "/api/featureservices"
      }

      axios.get(requestUrl)
        .then(response => {
          this.featureServices = response.data;
          this.searchFilteredFeatureServices = this.featureServices;
        })
        .catch(error => {
          message.error(error.message);
        })
        .finally(() => {});
    },

    matchSearch(item) {
        return item.name.toLowerCase().includes(this.searchText.toLowerCase())
          || item.db.toLowerCase().includes(this.searchText.toLowerCase())
          || item.featureList.toLowerCase().includes(this.searchText.toLowerCase())
          || item.deployment.toLowerCase().includes(this.searchText.toLowerCase())
          || item.description.toLowerCase().includes(this.searchText.toLowerCase());
    },

    handleSearch() {
      if (this.searchText === "") {
        this.searchFilteredFeatureServices = this.featureServices;
      } else {
        this.searchFilteredFeatureServices = this.featureServices.filter((item) => this.matchSearch(item));
      }
    },

    handleCancel() {
      this.isOpenTestModal = false;
    },

    showTestFormModal() {
      this.isOpenTestModal = true;
    },

    openFeatureServiceDrawer(name) {
      this.isOpenFeatureServiceDrawer = true;
      this.currentDrawerFeatureService = name;
    },

    openFeatureServiceVersionDrawer(name, version) {
      this.isOpenFeatureServiceVersionDrawer = true;
      this.currentDrawerFeatureService = name;
      this.currentDrawerFeatureServiceVersion = version;
    },

    clickTestModalOk() {
      this.$refs.TestFeatureService.submitForm();
    }

  }
};
</script>