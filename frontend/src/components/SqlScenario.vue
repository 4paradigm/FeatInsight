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
    :title="$t('SQL Usage Document')">
    <SqlUsageDoc @clickCopy="handleClickCopy"/>
  </a-drawer>

  <br/>
  <a-typography>
    <a-typography-title :level="2">{{ $t('SQL Scenario') }}</a-typography-title>
    <a-typography-paragraph>
      <blockquote>
        用户可以执行任意的 OpenMLDB SQL 语句，在线模式下会请求在线数据库，可进行在线数据的增删改查；离线模式下会提交分布式执行的 SQL，可进行离线探索或样本生成。
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
      :label="$t('SQL')"
      :rules="[{ required: true, message: 'Please input sql!' }]">
      
      <a-button type="dashed" @click="openSqlUsageDocDrawer">{{ $t('SQL Usage Document') }}</a-button>
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
import { notification } from 'ant-design-vue';

export default {
  components: {
    OfflineJobDetail,
    SqlUsageDoc
  },
  
  data() {
    return {
      isOpenOfflineJobDrawer: false,
      currentDrawerOfflineJob: -1,

      isOpenSqlUsageDocDrawer: false,

      isShowResult: false,

      isOpenDrawer: false,

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
          console.log(error);
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
          sql: this.formState.sql
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
          console.log(error);
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