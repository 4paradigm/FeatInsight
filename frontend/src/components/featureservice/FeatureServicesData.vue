<template>

<div>
  <a-modal v-model:visible="isOpenTestFormModal" width="1000px" :title="$t('Test Feature Service')" >
    <template #footer>
        <a-button @click="handleCancel">Cancel</a-button>
    </template>
    <TestFeatureService></TestFeatureService>
  </a-modal>

  <!-- Data table -->
  <a-input v-model:value="searchText" :placeholder="$t('Search')" @change="handleSearch" />
  <br/><br/>

  <div>
    <a-checkbox v-model:checked="isDisplayAllVersion" @change="initData">{{ $t('Display All Versions')}}</a-checkbox>
  </div>

  <a-table :columns="columns" :data-source="searchFilteredFeatureServices">
    <template #name="{ text, record }">
      <router-link :to="`/featureservices/${record.name}`">{{ text }}</router-link>
    </template>
    <template #version="{ text, record }">
      <router-link :to="`/featureservices/${record.name}/${record.version}`">{{ text }}</router-link>
    </template>
    <template #db="{ text, record }">
      <router-link :to="`/databases/${record.db}`">{{ text }}</router-link>
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

export default {
  components: {
    TestFeatureService
  },

  data() {
    return {
      isDisplayAllVersion: false,

      searchText: "",
      searchFilteredFeatureServices: [],

      featureServices: [],

      isOpenTestFormModal: false,
      
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
      this.isOpenTestFormModal = false;
    },

    showTestFormModal() {
      this.isOpenTestFormModal = true;
    },

  },
};
</script>