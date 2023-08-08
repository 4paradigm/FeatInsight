<template>

<div>
  <br/>
  <h1>
    {{ $t('Entities') }}
    &nbsp;&nbsp;<a-button type="primary"><router-link to='/entities/create'>{{ $t('Create Entity') }}</router-link></a-button>
  </h1>

  <br/>
  <!-- Data table -->

  <a-input v-model:value="searchText" :placeholder="$t('Search')" @change="handleSearch" />
  <br/><br/>

  <a-table :columns="columns" :data-source="searchFilteredEntities" :loading="loading">
    <template #name="{ text, record }">
      <router-link :to="`/entities/${record.name}`">{{ text }}</router-link>
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
      searchFilteredEntities: [],

      entities: [],

      loading: false,
      
      columns: [{
        title: this.$t('Name'),
        dataIndex: 'name',
        key: 'name',
        slots: { customRender: 'name' }
      },
      {
        title: this.$t('Primary Keys'),
        dataIndex: 'primaryKeys',
        key: 'primaryKeys',
      },
      {
        title: this.$t('Actions'),
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
      axios.get(`/api/entities`)
        .then(response => {
          this.entities = response.data;
          this.searchFilteredEntities = this.entities;
        })
        .catch(error => {
          message.error(error.message);
        })
        .finally(() => {
          this.loading = false;
        });
    },

    handleDelete(name) {
      axios.delete(`/api/entities/${name}`)
      .then(response => {
        message.success(`Success to delete entity: ${name}`);
        this.initData();
      })
      .catch(error => {
        message.error(error.message);
      });
    },

    matchSearch(item) {
        return item.name.toLowerCase().includes(this.searchText.toLowerCase())
          || item.primaryKeys.toLowerCase().includes(this.searchText.toLowerCase());
    },

    handleSearch() {
      if (this.searchText === "") {
        this.searchFilteredEntities = this.entities;
      } else {
        this.searchFilteredEntities = this.entities.filter((item) => this.matchSearch(item));
      }
    }

  },
};
</script>