<template>
<div>

  <a-drawer
    v-model:visible="isOpenOfflineJobDrawer"
    size="large"
    :title="$t('Offline Job') + $t('Detail')">
    <OfflineJobDetail :id="currentDrawerOfflineJob" :key="currentDrawerOfflineJob"></OfflineJobDetail>
  </a-drawer>

  <a-input v-model:value="searchText" :placeholder="$t('Search')" @change="handleSearch" />
  <br/><br/>

  <a-table :columns="columns" :data-source="searchFilteredOfflineJobInfos" :loading="loading">
    <template #id="{ text, record }">
      <a-button type="link" @click="openOfflineJobDrawer(record.id)">{{ record.id }}</a-button>
    </template>
  </a-table>

</div>
</template>
  
<script>
import axios from 'axios'
import { message } from 'ant-design-vue';
import OfflineJobDetail from '@/components/offlinejob/OfflineJobDetail.vue'

export default {
  components: {
    OfflineJobDetail
  },

  data() {
    return {
      isOpenOfflineJobDrawer: false,
      currentDrawerOfflineJob: -1,

      searchText: "",
      searchFilteredOfflineJobInfos: [],

      offlineJobInfos: [],

      loading: false,


      columns: [{
        title: this.$t('ID'),
        dataIndex: 'id',
        key: 'id',
        slots: { customRender: 'id' }
      },
      {
        title: this.$t('State'),
        dataIndex: 'state',
        key: 'state',
      },
      {
        title: this.$t('Start Time'),
        dataIndex: 'startTime',
        key: 'startTime',
      },
      {
        title: this.$t('End Time'),
        dataIndex: 'endTime',
        key: 'endTime',
      },
      {
        title: this.$t('Parameter'),
        dataIndex: 'parameter',
        key: 'parameter',
      },
      {
        title: this.$t('Application ID'),
        dataIndex: 'applicationId',
        key: 'applicationId',
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
    },

    openOfflineJobDrawer(id) {
      this.isOpenOfflineJobDrawer = true;
      this.currentDrawerOfflineJob = id;
    }

  }
};
</script>