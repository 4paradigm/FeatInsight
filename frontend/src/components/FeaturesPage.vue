<template>

<div>
  <br/>
  <h1>
    {{ $t('Features') }}
    <!-- &nbsp;&nbsp;<a-button type="primary"><router-link to='/features/create'>{{ $t('Create Feature') }}</router-link></a-button> -->
  </h1>

  <br/>
  <!-- Data table -->
  <a-input v-model:value="searchText" :placeholder="$t('Search')" @change="handleSearch" />
  <br/><br/>

  <a-table :columns="columns" :data-source="searchFilteredFeatures" :loading="loading">
    <template #featureView="{ text, record }">
        <router-link :to="`/featureviews/${record.featureViewName}`">{{ text }}</router-link>
      </template>
    <template #name="{ text, record }">
      <router-link :to="`/features/${record.featureViewName}/${record.featureName}`">{{ text }}</router-link>
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
    }

  },
};
</script>