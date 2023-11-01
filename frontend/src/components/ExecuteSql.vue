<template>
<div>

  <a-drawer
    v-model:visible="isOpenOfflineJobDrawer"
    size="large"
    :title="$t('Offline Job') + $t('Detail')">
    <OfflineJobDetail :id="currentDrawerOfflineJob" :key="currentDrawerOfflineJob"></OfflineJobDetail>
  </a-drawer>

  <a-typography-paragraph>
    <pre>{{ $t("Text of introduce execute sql") }} <a target="blank" href="https://openmldb.ai/docs/zh/main/openmldb_sql/index.html">{{$t('OpenMLDB documents')}}</a></pre>
  </a-typography-paragraph>

  <a-typography>
    <a-typography-title :level="2">{{ $t('SQL Scenario') }}</a-typography-title>
    <a-typography-paragraph>
      <blockquote>
        。
      </blockquote>
    </a-typography-paragraph>
  </a-typography>

  <!-- Create form -->
  <br/>
  <a-form
    :model="formState"
    layout="vertical"
    @submit="handleSubmit">

    <a-form-item
      :label="$t('Execute Mode')">
      <a-switch v-model:checked="formState.isOnlineMode" :checked-children="$t('Online')" :un-checked-children="$t('Offline')" />
    </a-form-item>

    <a-form-item
      :label="$t('SQL')"
      :rules="[{ required: true, message: 'Please input sql!' }]">
      <a-textarea
        v-model:value="formState.sql"
        placeholder="SELECT * FROM db1.t1" 
        :rows="5" />
    </a-form-item>

    <a-form-item>
      <a-button type="primary" html-type="submit">{{ $t('Submit') }}</a-button>
    </a-form-item>
  </a-form>

  <div v-if="isShowResult">
    <h2>SQL Result: </h2>
    <a-table :dataSource="resultData" :columns="resultColumns">
      <template #id="{ text, record }">
        <a-button type="link" @click="openOfflineJobDrawer(record.id)">{{ record.id }}</a-button>
      </template>
    </a-table>

  </div>

</div>
</template>
  
<script>
import axios from 'axios'
import { message } from 'ant-design-vue';
import OfflineJobDetail from '@/components/offlinejob/OfflineJobDetail.vue'

export default {
  components: {
    OfflineJobDetail
  },
  
  data() {
    return {
      isOpenOfflineJobDrawer: false,
      currentDrawerOfflineJob: -1,

      isShowResult: false,

      formState: {
        sql: '',
        isOnlineMode: true,
      },

      resultColumns: [],
      resultData: [],

      offlineJobInfoColumns: [{
        title: this.$t('ID'),
        dataIndex: 'id',
        key: 'id',
        slots: { customRender: 'id' }
      },
      {
        title: this.$t('State'),
        dataIndex: 'state',
        key: 'state',
      },
      {
        title: this.$t('Job Type'),
        dataIndex: 'jobType',
        key: 'jobType',
      },
      {
        title: this.$t('Start Time'),
        dataIndex: 'startTime',
        key: 'startTime',
      },
      {
        title: this.$t('Parameter'),
        dataIndex: 'parameter',
        key: 'parameter',
      },
      {
        title: this.$t('Cluster'),
        dataIndex: 'cluster',
        key: 'cluster',
      }]

    };
  },

  methods: {

    handleSubmit() {
      if (this.formState.isOnlineMode) {
        this.executeOnlineSql();
      } else {
        this.executeOfflineSql();
      }
    },

    executeOnlineSql() {
      axios
        .post(`/api/sql/online`, {
          sql: this.formState.sql
        })
        .then((response) => {
          message.success(`Success to execute SQL: ${this.formState.sql}`);
          this.isShowResult = true;

          if (response.data.length > 0) {
            const columnCount = response.data[0].length;

            this.resultColumns = []
            for (var i = 0; i < columnCount; i++) {
              const columnName = response.data[0][i];
              this.resultColumns.push({
                title: columnName,
                dataIndex: columnName,
                key: columnName
              })
            }

            this.resultData = []
            for (var i = 1; i < response.data.length; i++) {
              const row = response.data[i]
              const rowDataMap = {}

              for (var j = 0; j < columnCount; j++) {
                const columnName = response.data[0][j];
                const columnData = row[j];
                rowDataMap[columnName] = columnData;
              }

              this.resultData.push(rowDataMap);
            }
            
          } else {
            console.log("No result")
          }
        })
        .catch((error) => {
          console.log(error);
          if ('response' in error && 'data' in error.response) {
            message.error(error.response.data);
          } else {
            message.error(error.message);
          }
        });
    },

    executeOfflineSql() {
      axios
        .post(`/api/sql/offline`, {
          sql: this.formState.sql
        })
        .then((response) => {
          message.success(`Success to execute SQL: ${this.formState.sql}`);
          this.isShowResult = true;

          this.resultColumns = this.offlineJobInfoColumns;
          this.resultData = [response.data];
        })
        .catch((error) => {
          console.log(error);
          if ('response' in error && 'data' in error.response) {
            message.error(error.response.data);
          } else {
            message.error(error.message);
          }
        });
    },

    openOfflineJobDrawer(id) {
      this.isOpenOfflineJobDrawer = true;
      this.currentDrawerOfflineJob = id;
    }

  }
};
</script>