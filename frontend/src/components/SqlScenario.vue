<template>
<div>

  <a-drawer
    v-model:visible="isOpenOfflineJobDrawer"
    size="large"
    :title="$t('Offline Job') + $t('Detail')">
    <OfflineJobDetail :id="currentDrawerOfflineJob" :key="currentDrawerOfflineJob"></OfflineJobDetail>
  </a-drawer>

  <a-drawer
    v-model:visible="isOpenSqlUsageDocDrawer"
    width="80%"
    :title="$t('SQL Usage Examples')">
    <SqlUsageDoc @clickCopy="handleClickCopy"/>
  </a-drawer>

  <a-drawer
    v-model:visible="isOpenTableDrawer"
    width="50%"
    :title="$t('Data Tables')">
    <OnlineTables />
  </a-drawer>

  <br/>
  <a-typography>
    <a-typography-title :level="2">{{ $t('SQL Playground') }}</a-typography-title>
    <a-typography-paragraph>
      <blockquote>
        {{ $t('Text of SQL Playground') }}
      </blockquote>
    </a-typography-paragraph>
  </a-typography>


  <!-- Create form -->
  <br/>
  <a-form
    :model="formState"
    @submit="handleSubmit">

    <a-form-item
      :label="$t('Execute Mode')">
      <a-switch v-model:checked="formState.isOnlineMode" :checked-children="$t('Online')" :un-checked-children="$t('Offline')" />
    </a-form-item>

    <a-form-item 
      v-if="!formState.isOnlineMode"
      :label="$t('Spark Config')">
      <a-tooltip>
        <template #title>Spark config like 'spark.executor.memory=2g;spark.executor.cores=2'</template>
        <a-input v-model:value="formState.sparkConfig"></a-input>
      </a-tooltip>
    </a-form-item>

    <a-form-item
      :label="$t('SQL')"
      :rules="[{ required: true, message: 'Please input sql!' }]">
      
      <a-button type="dashed" @click="openSqlUsageDocDrawer">{{ $t('SQL Usage Examples') }}</a-button>
      &nbsp;<a-button type="dashed" @click="openTablesDrawer">{{ $t('Data Tables') }}</a-button>
      <br/><br/>

      <a-textarea
        v-model:value="formState.sql"
        placeholder="SELECT * FROM db1.t1" 
        :rows="10" />
    </a-form-item>

    <a-form-item>
      <a-button type="primary" html-type="submit">{{ $t('Submit') }}</a-button>
    </a-form-item>
  </a-form>

  <div v-if="isShowResult">
    <h2>{{ $t('SQL Result') }}</h2>
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
import OfflineJobDetail from '@/components/offlinejob/OfflineJobDetail.vue'
import SqlUsageDoc from '@/components/SqlUsageDoc.vue'
import OnlineTables from '@/components/table/OnlineTables.vue'
import { notification } from 'ant-design-vue';

export default {
  components: {
    OfflineJobDetail,
    SqlUsageDoc,
    OnlineTables
  },
  
  data() {
    return {
      isOpenOfflineJobDrawer: false,
      currentDrawerOfflineJob: -1,

      isOpenSqlUsageDocDrawer: false,

      isShowResult: false,

      isOpenTableDrawer: false,

      formState: {
        sql: '',
        isOnlineMode: true,
        sparkConfig: ''
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
          notification["success"]({
              message: this.$t('Execute Success'),
              description: `Success to execute SQL: ${this.formState.sql}`
            });

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
            this.resultColumns = [];
            this.resultData = [];
          }
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

    executeOfflineSql() {
      axios
        .post(`/api/sql/offline`, {
          sql: this.formState.sql,
          sparkConfig: this.formState.sparkConfig
        })
        .then((response) => {
          notification["success"]({
              message: this.$t('Execute Success'),
              description: `Success to execute SQL: ${this.formState.sql}`
            });

          this.isShowResult = true;

          this.resultColumns = this.offlineJobInfoColumns;
          this.resultData = [response.data];
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

    openOfflineJobDrawer(id) {
      this.isOpenOfflineJobDrawer = true;
      this.currentDrawerOfflineJob = id;
    },
    
    openSqlUsageDocDrawer() {
      this.isOpenSqlUsageDocDrawer = true;
    },

    openTablesDrawer() {
      this.isOpenTableDrawer = true;
    },

    handleClickCopy(sql) {
      this.isOpenSqlUsageDocDrawer = false;

      notification["success"]({
        message: this.$t('Execute Success'),
        description: `Success to copy SQL: ${sql}`
      });

      this.formState.sql = sql;
    }

  }
};
</script>