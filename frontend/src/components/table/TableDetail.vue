<template>
<div>

  <h2>{{ $t('Table') }}: {{ data.table }} </h2>
  <a-descriptions bordered>
    <a-descriptions-item :span="24" :label="$t('Database')">{{ data.db }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Table Name')">{{ data.table }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Schema')">{{ data.schema}}</a-descriptions-item>
  </a-descriptions>

  <br/>
  <a-button type="primary" @click="click_preview_data()">{{ $t('Preview Data') }}</a-button>

  <div v-if="isPreviewData">
    <br/>
    <h3>{{$t('Preview Data')}} ({{$t('Limit')}} 10 {{$t('Rows')}})</h3>
    <p v-html="tableData"></p>
  </div>

  <br/><br/>
  <h2>{{$t('Related Feature Views')}}</h2>
  <a-table :columns="featureViewColumns" :data-source="featureViews"></a-table>

  <br/>
  <h2>{{$t('Related Feature Services')}}</h2>
  <a-table :columns="featureServiceColumns" :data-source="featureServices"></a-table>

</div>
</template>

<script>
import axios from 'axios';
import { message } from 'ant-design-vue';

export default {
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
      isOpenFeatureViewDrawer: false,
      currentDrawerFeatureView: "",
      
      data: "",
      isPreviewData: false,
      tableData: "",

      featureViews: [],

      featureViewColumns: [{
        title: 'Name',
        dataIndex: 'name',
        key: 'name',
        slots: { customRender: 'name' }
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
        })
        .catch((error) => {
          message.error(error.message);
        });

      axios.get(`/api/tables/${this.db}/${this.name}/featureviews`)
        .then(response => {
          this.featureViews = response.data;
        })
        .catch(error => {
          message.error(error.message);
        });

      axios.get(`/api/tables/${this.db}/${this.name}/featureservices`)
        .then(response => {
          this.featureServices = response.data;
        })
        .catch(error => {
          message.error(error.message);
        });
    },
    click_preview_data() {
      if (this.isPreviewData) {
        this.isPreviewData = false;
        return;
      }

      const sql = "SELECT * FROM " + this.db + "." + this.name + " LIMIT 10";

      axios
        .post(`/api/sql/execute`, {
          sql: sql,
          online: true
        })
        .then((response) => {
          message.success(`Success to execute SQL: ${sql}`);
          console.log(response.data);

          this.isPreviewData = true;
          this.tableData = response.data.replace(/\n/g, '<br>');
        })
        .catch((error) => {
          console.log(error);
          if ('response' in error && 'data' in error.response) {
            message.error(error.response.data);
          } else {
            message.error(error.message);
          }
        });
    }
  }
  
};
</script>
