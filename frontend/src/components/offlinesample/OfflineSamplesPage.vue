<template>

<div>
  <br/>
  <h1>
    {{ $t('Offline Sample') }}
    &nbsp;&nbsp;&nbsp;<a-button type="primary" @click="showFormModal">{{ $t('Create Offline Sample') }}</a-button>
</h1>

  <!-- Form modal -->
  <div>
    <a-modal v-model:visible="isOpenFormModal" width="1000px" :title="$t('Offline Sample Generation')" >
      <template #footer>
          <a-button @click="handleCancel">Cancel</a-button>
        </template>
      <CreateOfflineSampleForm></CreateOfflineSampleForm>

    </a-modal>
  </div>

  <br/>
  <!-- Data table -->
  <a-input v-model:value="searchText" :placeholder="$t('Search')" @change="handleSearch" />
  <br/><br/>

  <a-table :columns="columns" :data-source="searchFilteredOfflineSamples" :loading="loading">
    <template #jobId="{ text, record }">
      <router-link :to="`/offlinesamples/${record.jobId}`">{{ text }}</router-link>
    </template>
  </a-table>

</div>
</template>
  
<script>
import axios from 'axios'
import { message } from 'ant-design-vue';

import CreateOfflineSampleForm from '@/components/form/CreateOfflineSampleForm.vue';

export default {
  components: { 
    CreateOfflineSampleForm
  },
  data() {
    return {

      searchText: "",
      searchFilteredOfflineSamples: [],
      offlineSamples: [],

      isOpenFormModal: false,

      loading: false,

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
      this.loading = true;

      axios.get("/api/offlinesamples")
        .then(response => {
          this.offlineSamples = response.data;
          this.searchFilteredOfflineSamples = this.offlineSamples;
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
        this.searchFilteredOfflineSamples = this.offlineSamples;
      } else {
        this.searchFilteredOfflineSamples = this.offlineSamples.filter((item) => this.matchSearch(item));
      }
    },

    showFormModal() {
      this.isOpenFormModal = true;
    },

    handleCancel() {
      this.isOpenFormModal = false;
    }

  },
};
</script>