<template>
<div>
  <a-modal v-model:visible="isShowPreviewModal" width="1000px" @ok="handleOk" 
    :title="$t('Preview Table Data') + ' (' + $t('Limit') + '10' + $t('Rows') + ')'" >
    <OnlineSqlResult :sql="currentPreviewSql" :key="refreshDataKey"></OnlineSqlResult>
  </a-modal>

  <h2>{{ $t('Table') }}: {{ data.table }} </h2>
  <a-descriptions bordered>
    <a-descriptions-item :span="24" :label="$t('Database')">{{ data.db }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Table Name')">{{ data.table }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="`${$t('Table')}${$t('ID')}`">{{ data.id }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('TTL')">{{ data.columnKey?.join(',') }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Partition')">{{ data.partition }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Unalive Partition')">{{ data.partitionUnalive }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Replica')">{{ data.replica }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Table Rows')">{{ data.rows }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Use Memory')">{{ data.useMemory }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Schema')">
      <a-list size="small" item-layout="horizontal" :data-source="tableSchemaList">
        <template #renderItem="{ item }">
          <a-list-item>{{ item }}</a-list-item>
        </template>
      </a-list>
    </a-descriptions-item>
  </a-descriptions>

  <br/>
  <a-button type="primary" @click="clickPreviewButton()">{{ $t('Preview Data') }}</a-button>

  <br/><br/><br/>
  <h2>{{$t('Related Feature Views')}}</h2>
  <a-table :columns="featureViewColumns" :data-source="featureViews"></a-table>

  <br/>
  <h2>{{$t('Related Feature Services')}}</h2>
  <a-table :columns="featureServiceColumns" :data-source="featureServices"></a-table>

</div>
</template>

<script>
import axios from 'axios';
import { notification } from 'ant-design-vue'
import OnlineSqlResult from '@/components/OnlineSqlResult.vue'

export default {
  components: {
    OnlineSqlResult
  },

  props: {
    db: {
      type: String,
      required: true,
    },
    name: {
      type: String,
      required: true,
    },
  },

  data() {
    return {
      refreshDataKey: 0,

      isOpenFeatureViewDrawer: false,
      currentDrawerFeatureView: "",

      isShowPreviewModal: false,
      currentPreviewSql: "",
      isPreviewData: false,

      data: "",
      tableData: "",
      tableSchemaList: [],

      featureViews: [],

      featureViewColumns: [{
        title: this.$t('Name'),
        dataIndex: 'name',
        key: 'name',
        slots: { customRender: 'name' }
      },
      {
        title: this.$t('Database'),
        dataIndex: 'db',
        key: 'db',
        slots: { customRender: 'db' }
      },
      {
        title: this.$t('SQL'),
        dataIndex: 'sql',
        key: 'sql',
      },
      {
        title: this.$t('Features'),
        dataIndex: 'featureNames',
        key: 'featureNames',
      },
      {
        title: this.$t('Description'),
        dataIndex: 'description',
        key: 'description',
      }],

      featureServices: [],

      featureServiceColumns: [{
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
      }],

    };
  },

  mounted() {
    this.initData();
  },

  methods: {
    initData() {
      axios.get(`/api/tables/${this.db}/${this.name}`)
        .then((response) => {
          this.data = response.data;

          this.tableSchemaList = response.data.schema.split(",");
        })
        .catch((error) => {
          notification["error"]({
              message: this.$t('Execute Fail'),
              description: error.message
            });
        });

      axios.get(`/api/tables/${this.db}/${this.name}/featureviews`)
        .then(response => {
          this.featureViews = response.data;
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

      axios.get(`/api/tables/${this.db}/${this.name}/featureservices`)
        .then(response => {
          this.featureServices = response.data;
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

    clickPreviewButton() {
      this.refreshDataKey++;

      this.currentPreviewSql = "SELECT * FROM " + this.db + "." + this.name + " LIMIT 10";

      this.isShowPreviewModal = true;
    },

    click_preview_data() {
      if (this.isPreviewData) {
        this.isPreviewData = false;
        return;
      }

      const sql = "SELECT * FROM " + this.db + "." + this.name + " LIMIT 10";

      axios
        .post(`/api/sql/online`, {
          sql: sql
        })
        .then((response) => {
          notification["success"]({
              message: this.$t('Execute Success'),
              description: `Success to preview table data (limit 10 rows)`
            });

          this.isPreviewData = true;
          this.tableData = response.data.replace(/\n/g, '<br>');
        })
        .catch((error) => {
          if ('response' in error && 'data' in error.response) {
            notification["error"]({
              message: this.$t('Execute Fail'),
              description: error.response.data
            });
          } else {
            notification["error"]({
              message: this.$t('Execute Fail'),
              description: error.message
            });
          }
        });
    },

    handleOk() {
      this.isShowPreviewModal = false;
    }

  }
  
};
</script>
