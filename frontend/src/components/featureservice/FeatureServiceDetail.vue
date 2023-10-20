<template>
  <div>
  
    <br/>
    <h1>
      {{ $t('Feature Service') }}: {{ data.name }} 
      &nbsp;&nbsp;<a-button type="primary"><router-link :to="`/featureservices/test?featureservice=${data.name}&version=${latestVersion}`">{{ $t('Test Service') }}</router-link></a-button>
    </h1>

    <br/>
    <a-descriptions bordered>
      <a-descriptions-item :span="24" :label="$t('Name')"> {{ data.name }}</a-descriptions-item>
      <a-descriptions-item :span="24" :label="$t('Latest Version')"><router-link :to="`/featureservices/${data.name}/${data.version}`">{{ data.version }}</router-link></a-descriptions-item>
      <a-descriptions-item :span="24" :label="$t('Feature List')">{{ data.featureList }}</a-descriptions-item>
      <a-descriptions-item :span="24" :label="$t('Database')"><router-link :to="`/databases/${data.db}`">{{ data.db }}</router-link></a-descriptions-item>
      <a-descriptions-item :span="24" :label="$t('SQL')">{{ data.sql }}</a-descriptions-item>
      <a-descriptions-item :span="24" :label="$t('Deployment')">{{ data.deployment }}</a-descriptions-item>
      <a-descriptions-item :span="24" :label='$t("Description")'>{{ data.description }}</a-descriptions-item>
    </a-descriptions>

    <br/><br/>
    <h1>{{ $t('Features Service Versions') }}</h1>
    <a-table :columns="columns" :data-source="featureServiceVersionList">
      <template #version="{ text, record }">
        <router-link :to="`/featureservices/${record.name}/${record.version}`">{{ text }}</router-link>
      </template>

      <template #isLatest="{ text, record }">
        <span>
          <div v-if="record.isLatest">
            <a-tag color="green">{{ $t('Latest') }}</a-tag>
          </div>
          <div v-else>
            <a-tag color="red">{{ $t('Not Latest') }}</a-tag>
          </div>
        </span>
      </template>

      <template v-slot:action="scope">
        <a-button type="primary"><router-link :to="`/featureservices/test?featureservice=${scope.record.name}&version=${scope.record.version}`">{{ $t('Test Service') }}</router-link></a-button>
        <br/>
        <a-popconfirm
            title="Sure to update as latest version?"
            @confirm="handleUpdteLatestVersion(scope.record.name, scope.record.version)">
          <a-button type="primary">{{ $t('Update Version') }}</a-button>
        </a-popconfirm>
        <br/>
        <a-popconfirm
            title="Sure to delete?"
            @confirm="handleDelete(scope.record.name, scope.record.version)">
          <a-button type="primary" danger>{{ $t('Delete Service') }}</a-button>
        </a-popconfirm>
      </template>
    </a-table>

  </div>
  </template>
    
<script>
import axios from 'axios'
import { message } from 'ant-design-vue';

export default {
  props: {
    name: {
      type: String,
      required: true
    }
  },

  data() {
    return {
      data: {},

      featureServiceVersionList: [],

      latestVersion: "",

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
        title: this.$t('Latest Version'),
        dataIndex: 'isLatest',
        key: 'isLatest',
        slots: { customRender: 'isLatest' }
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

    }
  },

  methods: {
    async initData() {
      const request1 = axios.get(`/api/featureservices/${this.name}`);
      const request2 = axios.get(`/api/featureservices/${this.name}/latestversion`);
      const request3 = axios.get(`/api/featureservices/${this.name}/versions`);

      const responses = await Promise.all([request1, request2, request3]);

      this.data = responses[0].data;
      this.latestVersion = responses[1].data;
      const versions = responses[2].data;

      this.featureServiceVersionList = [];
      versions.forEach(version => {
        this.featureServiceVersionList.push({
          "name": this.name,
          "version": version,
          "isLatest": version === this.latestVersion
        })
      });
    },

    handleUpdteLatestVersion(name, version) {
      axios.put(`/api/featureservices/${name}/latestversion`, {
          "version": version
        })
        .then(response => {
          message.success(`Success to update latest version: ${version}`);
          this.initData();
        })
        .catch(error => {
          message.error(error.message);
        });
    },

    handleDelete(name, version) {
      axios.delete(`/api/featureservices/${name}/${version}`)
      .then(response => {
        message.success(`Success to delete feature service: ${name} and version: ${version}`);
        this.initData();
      })
      .catch(error => {
        message.error(error.message);
      });
    }
  },

  mounted() {
    this.initData();
  }
  
}
</script>