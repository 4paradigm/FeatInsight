<template>

<div>
  <br/>
  <h1>
    {{ $t('Training Set') }}
    &nbsp;&nbsp;&nbsp;<a-button type="primary" @click="showFormModal">{{ $t('Create Training Set') }}</a-button>
</h1>

  <!-- Form modal -->
  <div>
    <a-modal v-model:visible="isOpenFormModal" width="1000px" :title="$t('Offline Train Set Generation')" @ok="handleOk">

      <CreateTrainingSetForm></CreateTrainingSetForm>

    </a-modal>
  </div>

  <br/>
  <!-- Data table -->
  <a-input v-model:value="searchText" :placeholder="$t('Search')" @change="handleSearch" />
  <br/><br/>

  <a-table :columns="columns" :data-source="searchFilteredTrainingSets" :loading="loading">
    <template #jobId="{ text, record }">
      <router-link :to="`/trainingsets/${record.jobId}`">{{ text }}</router-link>
    </template>
  </a-table>

</div>
</template>
  
<script>
import axios from 'axios'
import { message } from 'ant-design-vue';

import CreateTrainingSetForm from './form/CreateTrainingSetForm.vue';

export default {
  components: { 
    CreateTrainingSetForm
  },
  data() {
    return {

      searchText: "",
      searchFilteredTrainingSets: [],
      trainingSets: [],

      isOpenFormModal: false,

      loading: false,

      columns: [{
        title: this.$t('Training Set ID'),
        dataIndex: 'jobId',
        key: 'jobId',
        slots: { customRender: 'jobId' }
      },
      {
        title: this.$t('Feature List'),
        dataIndex: 'featureList',
        key: 'featureList',
      },
      {
        title: this.$t('Format'),
        dataIndex: 'format',
        key: 'format',
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

      let requestUrl = "/api/trainingsets"

      axios.get(requestUrl)
        .then(response => {
          this.trainingSets = response.data;
          this.searchFilteredTrainingSets = this.trainingSets;
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
        this.searchFilteredTrainingSets = this.trainingSets;
      } else {
        this.searchFilteredTrainingSets = this.trainingSets.filter((item) => this.matchSearch(item));
      }
    },

    showFormModal() {
      this.isOpenFormModal = true;
    },

    handleOk() {
      this.isOpenFormModal = false;
    }

  },
};
</script>