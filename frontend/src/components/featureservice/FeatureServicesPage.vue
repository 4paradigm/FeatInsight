<template>

<div>
  <br/>
  <h1>
    {{ $t('Feature Services') }}
    &nbsp;&nbsp;<a-button type="primary" @click="showFormModal">{{ $t('Create Feature Service') }}</a-button>
  </h1>

  <!-- Create form modal -->
  <div>
    <a-modal v-model:visible="isOpenFormModal" width="1000px" :title="$t('Deploy Feature Service')" @ok="handleOk">
      <CreateFeatureServiceForm></CreateFeatureServiceForm>
    </a-modal>
  </div>

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
      <a-button type="default" @click="showTestFormModal">{{ $t('Test Service') }}</a-button>
    </template>
  </a-table>

  <div>
    <a-modal v-model:visible="isOpenTestFormModal" width="1000px" :title="$t('Test Feature Service')" @ok="handleOk">
      <TestFeatureService></TestFeatureService>
    </a-modal>
  </div>

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
import CreateFeatureServiceForm from '@/components/form/CreateFeatureServiceForm.vue';
import TestFeatureService from '@/components/TestFeatureService.vue';

export default {
  components: { 
    CreateFeatureServiceForm,
    TestFeatureService
  },

  data() {
    return {
      isDisplayAllVersion: false,

      searchText: "",
      searchFilteredFeatureServices: [],

      featureServices: [],

      isOpenFormModal: false,

      isOpenTestFormModal: false,

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
        title: this.$t('Description'),
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
    },

    showFormModal() {
      this.isOpenFormModal = true;
    },

    handleOk() {
      this.isOpenFormModal = false;
      this.isOpenTestFormModal = false;
    },

    showTestFormModal() {
      this.isOpenTestFormModal = true;
    },

  },
};
</script>