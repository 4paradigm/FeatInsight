<template>

<div>
  <br/>
  <h1>
    {{ $t('Offline Jobs') }}
  </h1>

  <br/>
  <!-- Data table -->
  <a-input v-model:value="searchText" :placeholder="$t('Search')" @change="handleSearch" />
  <br/><br/>


  <a-table :columns="columns" :data-source="searchFilteredOfflineJobInfos" :loading="loading">
    <template #id="{ text, record }">
      <router-link :to="`/offlinejobs/${record.id}`">{{ text }}</router-link>
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
      searchFilteredOfflineJobInfos: [],

      offlineJobInfos: [],

      loading: false,


      columns: [{
        title: this.$t('id'),
        dataIndex: 'id',
        key: 'id',
        slots: { customRender: 'id' }
      },
      {
        title: this.$t('state'),
        dataIndex: 'state',
        key: 'state',
      },
      {
        title: this.$t('startTime'),
        dataIndex: 'startTime',
        key: 'startTime',
      },
      {
        title: 'endTime',
        dataIndex: 'endTime',
        key: 'endTime',
      },
      {
        title: 'parameter',
        dataIndex: 'parameter',
        key: 'parameter',
      },
      {
        title: 'cluster',
        dataIndex: 'cluster',
        key: 'cluster',
      },
      {
        title: 'applicationId',
        dataIndex: 'applicationId',
        key: 'applicationId',
      },
      {
        title: 'error',
        dataIndex: 'error',
        key: 'error',
      }],

    };
  },

  mounted() {
    this.initData();

  },

  methods: {
    initData() {
      this.loading = true;

      let requestUrl = "/api/offlinejobs"

      axios.get(requestUrl)
        .then(response => {
          this.offlineJobInfos = response.data;
          this.searchFilteredOfflineJobInfos = this.offlineJobInfos;
        })
        .catch(error => {
          message.error(error.message);
        })
        .finally(() => {
          this.loading = false;
        });
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
        this.searchFilteredOfflineJobInfos = this.offlineJobInfos;
      } else {
        this.searchFilteredOfflineJobInfos = this.offlineJobInfos.filter((item) => this.matchSearch(item));
      }
    }

  },
};
</script>