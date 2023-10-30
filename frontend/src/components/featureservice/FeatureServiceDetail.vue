<template>
<div>
  <a-drawer
    v-model:visible="isOpenFeatureServiceVersionDrawer"
    size="large"
    :title="$t('Feature Service') + $t('Detail')">
    <FeatureServiceVersionDetail :name="currentDrawerFeatureService" :version="currentDrawerFeatureServiceVersion" :key="currentDrawerFeatureService+currentDrawerFeatureServiceVersion"></FeatureServiceVersionDetail>
  </a-drawer>

  <h2>{{ $t('Feature Service') }}: {{ data.name }} </h2>

  <a-table :columns="columns" :data-source="featureServiceVersionList">
    <template #version="{ text, record }">
      <a-button type="link" @click="openFeatureServiceVersionDrawer(record.name, record.version)">{{ record.version }}</a-button>
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
      <a-button><router-link :to="`/featureservices/test?featureservice=${scope.record.name}&version=${scope.record.version}`">{{ $t('Test Service') }}</router-link></a-button>
      <br/>
      <a-popconfirm
          title="Sure to update as latest version?"
          @confirm="handleUpdteLatestVersion(scope.record.name, scope.record.version)">
        <a-button>{{ $t('Update Version') }}</a-button>
      </a-popconfirm>
      <br/>
      <a-popconfirm
          title="Sure to delete?"
          @confirm="handleDelete(scope.record.name, scope.record.version)">
        <a-button danger>{{ $t('Delete Service') }}</a-button>
      </a-popconfirm>
    </template>
  </a-table>

</div>
</template>
    
<script>
import axios from 'axios'
import { message } from 'ant-design-vue';
import FeatureServiceVersionDetail from '@/components/featureservice/FeatureServiceVersionDetail.vue'

export default {
  components: {
    FeatureServiceVersionDetail
  },

  props: {
    name: {
      type: String,
      required: true
    }
  },

  data() {
    return {
      isOpenFeatureServiceVersionDrawer: false,
      currentDrawerFeatureService: "",
      currentDrawerFeatureServiceVersion: "",

      data: {},

      featureServiceVersionList: [],

      latestVersion: "",

      columns: [
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

  mounted() {
    this.initData();
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
    },

    openFeatureServiceVersionDrawer(name, version) {
      this.isOpenFeatureServiceVersionDrawer = true;
      this.currentDrawerFeatureService = name;
      this.currentDrawerFeatureServiceVersion = version;
    }

  }
}
</script>