<template>
<div>
  <a-drawer
    v-model:visible="isOpenFeatureServiceVersionDrawer"
    size="large"
    :title="$t('Feature Service') + $t('Detail')">
    <FeatureServiceVersionDetail :name="currentDrawerFeatureService" :version="currentDrawerFeatureServiceVersion" :key="currentDrawerFeatureService+currentDrawerFeatureServiceVersion"></FeatureServiceVersionDetail>
  </a-drawer>

  <a-modal v-model:visible="isOpenTestModal" width="1000px" :title="$t('Test Feature Service')" @ok="clickTestModalOk" >
    <TestFeatureServiceForm ref="TestFeatureServiceForm" :featureServiceName="currentDrawerFeatureService" :featureServiceVersion="currentDrawerFeatureServiceVersion" ></TestFeatureServiceForm>
  </a-modal>

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
      <a-button type="default" @click="showTestFormModal(scope.record.name, scope.record.version)">{{ $t('Test Service Version') }}</a-button>
    
      <br/>
      <a-popconfirm
          title="Sure to update as latest version?"
          @confirm="handleUpdteLatestVersion(scope.record.name, scope.record.version)">
        <a-button>{{ $t('Set Latest Version') }}</a-button>
      </a-popconfirm>

      <br/>
      <a-popconfirm
          title="Sure to delete?"
          @confirm="handleDelete(scope.record.name, scope.record.version)">
        <a-button danger>{{ $t('Delete Service Version') }}</a-button>
      </a-popconfirm>
    </template>
  </a-table>

</div>
</template>
    
<script>
import axios from 'axios'
import { notification } from 'ant-design-vue'
import FeatureServiceVersionDetail from '@/components/featureservice/FeatureServiceVersionDetail.vue'
import TestFeatureServiceForm from '@/components/form/TestFeatureServiceForm.vue';

export default {
  components: {
    FeatureServiceVersionDetail,
    TestFeatureServiceForm
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

      isOpenTestModal: false,

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
          notification["success"]({
              message: this.$t('Execute Success'),
              description: `Success to update latest version: ${version}`
            });

          this.initData();
        })
        .catch(error => {
          var errorMessage = error.message;
          if (error.response && error.response.data) {
            errorMessage = error.response.data;
          }
          notification["error"]({
            message: this.$t('Execute Fail'),
            description: errorMessage
          });
        });
    },

    handleDelete(name, version) {
      axios.delete(`/api/featureservices/${name}/${version}`)
      .then(response => {
        notification["success"]({
              message: this.$t('Execute Success'),
              description: `Success to delete feature service: ${name} and version: ${version}`
            });

        this.initData();
      })
      .catch(error => {
        var errorMessage = error.message;
        if (error.response && error.response.data) {
          errorMessage = error.response.data;
        }
        notification["error"]({
          message: this.$t('Execute Fail'),
          description: errorMessage
        });
      });
    },

    openFeatureServiceVersionDrawer(name, version) {
      this.isOpenFeatureServiceVersionDrawer = true;
      this.currentDrawerFeatureService = name;
      this.currentDrawerFeatureServiceVersion = version;
    },


    showTestFormModal(name, version) {
      this.currentDrawerFeatureService = name;
      this.currentDrawerFeatureServiceVersion = version;

      this.isOpenTestModal = true;
    },

    clickTestModalOk() {
      this.$refs.TestFeatureServiceForm.submitForm();
    }

  }
}
</script>