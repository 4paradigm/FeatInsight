<template>

<div>
  <br/>
  <h1>
    {{ $t('Feature Services') }}
    &nbsp;&nbsp;<a-button type="primary"><router-link to='/featureservices/deploy'>{{ $t('Create Service') }}</router-link></a-button>
  </h1>

  <br/>
  <!-- Data table -->
  <a-input v-model:value="searchText" :placeholder="$t('Search')" @change="handleSearch" />
  <br/><br/>

  <div>
    <a-checkbox v-model:checked="isDisplayAllVersion" @change="initData">{{ $t('Display All Versions')}}</a-checkbox>
  </div>


  <a-table :columns="columns" :data-source="searchFilteredFeatureServices" :loading="loading">
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
      <a-button type="primary"><router-link :to="`/featureservices/test?featureservice=${scope.record.name}`">{{ $t('Test Service') }}</router-link></a-button>
    </template>
  </a-table>

  <!-- Support create from deployment in other page
  <br />
  <div>
    <h1>{{ $t('Create') }} {{ $t('From') }} {{ $t('Deployment') }}</h1>
    <a-form
      :model="createFromDeploymentFormState"
      name="basic"
      :label-col="{ span: 8 }"
      :wrapper-col="{ span: 16 }"
      @submit="handleCreateFromDeploymentSubmit">
      <a-form-item
        label="Name"
        :rules="[{ required: true, message: 'Please input name!' }]">
        <a-input v-model:value="createFromDeploymentFormState.name" />
      </a-form-item>

      <a-form-item
        label="Database"
        :rules="[{ required: true, message: 'Please input database!' }]">
        <a-input v-model:value="createFromDeploymentFormState.db" />
      </a-form-item>
      
      <a-form-item
        label="Deployment"
        :rules="[{ required: true, message: 'Please input deployment!' }]">
        <a-input v-model:value="createFromDeploymentFormState.deploymentName" />
      </a-form-item>

      <a-form-item :wrapper-col="{ offset: 8, span: 16 }">
        <a-button type="primary" html-type="submit">{{ $t('Submit') }}</a-button>
      </a-form-item>
    </a-form>
  </div>
  -->

</div>
</template>
  
<script>
import axios from 'axios'
import { message } from 'ant-design-vue';

export default {
  data() {
    return {
      isDisplayAllVersion: false,

      searchText: "",
      searchFilteredFeatureServices: [],

      featureServices: [],

      loading: false,
      
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
        title: this.$t('Feature List'),
        dataIndex: 'featureList',
        key: 'featureList',
      },
      {
        title: 'Description',
        dataIndex: 'description',
        key: 'description',
      },
      {
        title: this.$t('Actions'),
        key: 'actions',
        slots: { customRender: 'action' },
      }],

      createFromDeploymentFormState: {
        name: '',
        version: '',
        db: '',
        deploymentName: '',
        description: ''
      },
    };
  },

  mounted() {
    this.initData();

  },

  methods: {
    initData() {
      this.loading = true;

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
        .finally(() => {
          this.loading = false;
        });
    },

    handleCreateFromDeploymentSubmit() {
      axios.post(`/api/featureservices/deployments`, {
        "name": this.createFromDeploymentFormState.name,
        "version": this.createFromDeploymentFormState.version,
        "db": this.createFromDeploymentFormState.featureList,
        "deploymentName": this.createFromDeploymentFormState.deploymentName
      })
      .then(response => {
        message.success(`Success to add feature service ${this.formState.name}`);
        this.initData();
      })
      .catch(error => {
        message.error(error.message);
      });
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
    }

  },
};
</script>