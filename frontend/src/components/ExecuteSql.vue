<template>

<div>
    <a-typography-paragraph>
      <pre>{{ $t("Text of introduce execute sql") }} <a target="blank" href="https://openmldb.ai/docs/zh/main/openmldb_sql/index.html">{{$t('OpenMLDB documents')}}</a></pre>
    </a-typography-paragraph>
    <br/>

    <!-- Create form -->
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
          placeholder="Please input SQL" 
          :rows="5" />
      </a-form-item>

      <a-form-item>
        <a-button type="primary" html-type="submit">{{ $t('Submit') }}</a-button>
      </a-form-item>
    </a-form>


  
  <h2>SQL Result: </h2>
  <a-table :dataSource="resultData" :columns="resultColumns" />
  
</div>
</template>
  
<script>
import axios from 'axios'
import { message } from 'ant-design-vue';

export default {
  data() {
    return {
      formState: {
        sql: 'select 100',
        isOnlineMode: true,
      },


      schemaList: [],
      dataList: [],

      resultColumns: [
          {
            title: 'Name',
            dataIndex: 'name',
            key: 'name',
          }
        ],

      resultData: [
          {
            key: '1',
            name: 'Mike',
            age: 32,
            address: '10 Downing Street',
          }
        ]
    };
  },

  methods: {

    handleSubmit() {
      axios
        .post(`/api/sql/online`, {
          sql: this.formState.sql
        })
        .then((response) => {
          message.success(`Success to execute SQL: ${this.formState.sql}`);

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

                //rowDataMap.set(columnName, columnData);
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
    }

  },
};
</script>