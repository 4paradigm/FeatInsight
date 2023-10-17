<template>
  <div>
  
    <br/>
    <h1>
      {{ $t('Feature View') }}: {{ data.name }} 
      &nbsp;&nbsp;<a-button type="primary"><router-link :to='`/featureservices/create?featureview=${data.name}`'>{{ $t('Create Service') }}</router-link></a-button>
    </h1>
    <a-descriptions bordered>
      <a-descriptions-item :span="24" :label='$t("Name")'> {{ data.name }} </a-descriptions-item>
      <a-descriptions-item :span="24" :label='$t("Entities")'>
        <router-link v-for="entity in entities" :to="`/entities/${entity}`" :key="entity">{{ entity }} </router-link>
      </a-descriptions-item>
      <a-descriptions-item :span="24" :label='$t("Database")'><router-link :to="`/databases/${data.db}`">{{ data.db }}</router-link></a-descriptions-item>
      <a-descriptions-item :span="24" :label='$t("SQL")'>{{ data.sql }}</a-descriptions-item>
      <a-descriptions-item :span="24" :label='$t("Features")'>
        <router-link v-for="feature in features" :to="`/features/${feature.featureViewName}/${feature.featureName}`" :key="feature.featureName">{{ feature.featureName }}, </router-link>
      </a-descriptions-item>
      <a-descriptions-item :span="24" :label='$t("Description")'>{{ data.description }}</a-descriptions-item>
    </a-descriptions>
  
    <br/><br/>
    <h1>{{ $t('Features') }}</h1>
    <a-table :columns="columns" :data-source="features">
      <template #featureView="{ text, record }">
        <router-link :to="`/featureviews/${record.featureViewName}`">{{ text }}</router-link>
      </template>
      <template #name="{ text, record }">
        <router-link :to="`/features/${record.featureViewName}/${record.featureName}`">{{ text }}</router-link>
      </template>
    </a-table>

    <br/>
    <h1>{{ $t('Dependent Tables') }}</h1>
    <a-table :columns="tableColumns" :data-source="tables">
      <template #db="{ text, record }">
        <router-link :to="`/databases/${record.db}`">{{ text }}</router-link>
      </template>
      <template #table="{ text, record }">
        <router-link :to="`/tables/${record.db}/${record.table}`">{{ text }}</router-link>
      </template>
    </a-table>
  </div>

  </template>
    
<script>
import axios from 'axios';
import { message } from 'ant-design-vue';

export default {
  props: {
    name: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      data: "",
      entities: [],
      features: [],
      columns: [
        {
          title: this.$t('Feature View'),
          dataIndex: 'featureViewName',
          key: 'featureViewName',
          slots: { customRender: 'featureView' },
        },
        {
          title: this.$t('Feature Name'),
          dataIndex: 'featureName',
          key: 'featureName',
          slots: { customRender: 'name' },
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
        },
      ],
      tables: [],
      tableColumns: [
        {
          title: this.$t('Database'),
          dataIndex: 'db',
          key: 'db',
          slots: { customRender: 'db' },
        },
        {
          title: this.$t('Table'),
          dataIndex: 'table',
          key: 'table',
          slots: { customRender: 'table' },
        },
      ],
    };
  },
  methods: {        
    initData() {

      axios
        .get(`/api/featureviews/${this.name}`)
        .then((response) => {
          this.data = response.data;
          this.entities = this.data.entityNames.split(',').map((item) => item.trim());
        })
        .catch((error) => {
          if (error.response != null) {
            message.error(error.response.data);
          } else {
            message.error(error.message);
          }
        });

      // Request features from feature view
      axios
        .get(`/api/features/${this.name}`)
        .then((response) => {
          this.features = response.data;
        })
        .catch((error) => {
          if (error.response != null) {
            message.error(error.response.data);
          } else {
            message.error(error.message);
          }
        });

      axios
        .get(`/api/featureviews/${this.name}/tables`)
        .then((response) => {
          response.data.forEach((str) => {
            let [db, table] = str.split('.');
            this.tables.push({ db: db, table: table });
          });
        })
        .catch((error) => {
          if (error.response != null) {
            message.error(error.response.data);
          } else {
            message.error(error.message);
          }
        });

    },
  },
  mounted() {
    this.initData();
  },
};
</script>