<template>

<div>
  <!-- Data table -->
  <a-input v-model:value="searchText" :placeholder="$t('Search')" @change="handleSearch" />
  <br/><br/>

  <a-table :columns="columns" :data-source="searchFilteredOfflineSamples">
    <template #jobId="{ text, record }">
      <router-link :to="`/offlinesamples/${record.jobId}`">{{ text }}</router-link>
    </template>
  </a-table>

</div>
</template>
  
<script>
import axios from 'axios'
import { message } from 'ant-design-vue';

export default {
  data() {
    return {
      searchText: "",
      searchFilteredOfflineSamples: [],
      offlineSamples: [],

      columns: [{
        title: this.$t('Offline Sample ID'),
        dataIndex: 'jobId',
        key: 'jobId',
        slots: { customRender: 'jobId' }
      },
      {
        title: this.$t('Features'),
        dataIndex: 'featureNames',
        key: 'featureName',
      },
      {
        title: this.$t('Path'),
        dataIndex: 'path',
        key: 'path',
      },
      {
        title: this.$t('Options'),
        dataIndex: 'options',
        key: 'options',
      }],

    };
  },

  mounted() {
    this.initData();
  },

  methods: {
    initData() {
      axios.get("/api/offlinesamples")
        .then(response => {
          this.offlineSamples = response.data;
          this.searchFilteredOfflineSamples = this.offlineSamples;
        })
        .catch(error => {
          message.error(error.message);
        })
        .finally(() => {});
    },

    matchSearch(item) {
        return item.id.toString().toLowerCase().includes(this.searchText.toLowerCase())
          || item.state.toLowerCase().includes(this.searchText.toLowerCase())
          || item.startTime.toLowerCase().includes(this.searchText.toLowerCase())
          || item.endTime.toLowerCase().includes(this.searchText.toLowerCase())
          || item.parameter.toLowerCase().includes(this.searchText.toLowerCase())
          || item.cluster.toLowerCase().includes(this.searchText.toLowerCase())
          || item.applicationId.toLowerCase().includes(this.searchText.toLowerCase())
          || item.error.toLowerCase().includes(this.searchText.toLowerCase());
    },

    handleSearch() {
      if (this.searchText === "") {
        this.searchFilteredOfflineSamples = this.offlineSamples;
      } else {
        this.searchFilteredOfflineSamples = this.offlineSamples.filter((item) => this.matchSearch(item));
      }
    }

  }
};
</script>