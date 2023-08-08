<template>

<div>
  <br/>
  <h1>
    {{ $t('Feature Views') }}
    &nbsp;&nbsp;<a-button type="primary"><router-link to='/features/create'>{{ $t('Create Feature') }}</router-link></a-button>
  </h1>

  <br/>
  <!-- Data table -->
  <a-input v-model:value="searchText" :placeholder="$t('Search')" @change="handleSearch" />
  <br/><br/>

  <a-table :columns="columns" :data-source="searchFilteredFeatureViews" :loading="loading">
    <template #name="{ text, record }">
      <router-link :to="`/featureviews/${record.name}`">{{ text }}</router-link>
    </template>
    <template #db="{ text, record }">
      <router-link :to="`/databases/${record.db}`">{{ text }}</router-link>
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
import { message } from 'ant-design-vue';

export default {
  data() {
    return {
      searchText: "",
      searchFilteredFeatureViews: [],

      featureViews: [],

      loading: false,
      
      columns: [{
        title: 'Name',
        dataIndex: 'name',
        key: 'name',
        slots: { customRender: 'name' }
      },
      {
        title: 'Entities',
        dataIndex: 'entityNames',
        key: 'entityNames',
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
        title: 'Features',
        dataIndex: 'featureNames',
        key: 'featureNames',
      },
      {
        title: 'Description',
        dataIndex: 'description',
        key: 'description',
      },
      {
        title: 'Actions',
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
      this.loading = true;
      axios.get(`/api/featureviews`)
        .then(response => {
          this.featureViews = response.data;
          this.searchFilteredFeatureViews = this.featureViews;
        })
        .catch(error => {
          message.error(error.message);
        })
        .finally(() => {
          this.loading = false;
        });
    },

    handleDelete(name) {
      axios.delete(`/api/featureviews/${name}`)
      .then(response => {
        message.success(`Success to delete feature view: ${name}`);
        this.initData();
      })
      .catch(error => {
        message.error(error.message);
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
    }

  },
};
</script>