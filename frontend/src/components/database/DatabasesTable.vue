<template>
<div>

  <a-drawer
    v-model:visible="isOpenDatabaseDrawer"
    size="large"
    :title="$t('Database') + $t('Detail')">
    <DatabaseDetail :db="currentDrawerDatabase" :key="currentDrawerDatabase"></DatabaseDetail>
  </a-drawer>

  <a-table :columns="databaseColumns" :data-source="databases">
    <template #database="{ text, record }">
      <a-button type="link" @click="openDatabaseDrawer(text)">{{ text }}</a-button>
    </template>
  </a-table>

</div>
</template>
  
<script>
import axios from 'axios'
import DatabaseDetail from '@/components/database/DatabaseDetail.vue'

export default {
  components: {
    DatabaseDetail
  },

  data() {
    return {
      isOpenDatabaseDrawer: false,
      currentDrawerDatabase: "",

      databases: [],

      databaseColumns: [{
        title: this.$t('Database'),
        slots: { customRender: 'database' }
      }],
    };
  },

  mounted() {
    this.initData();
  },

  methods: {
    initData() {
      axios.get(`/api/databases`)
        .then(response => {
          this.databases = response.data;
        })
        .catch(error => {
          message.error(error.message);
        })
        .finally(() => {});
    },

    openDatabaseDrawer(db) {
      this.isOpenDatabaseDrawer = true;
      this.currentDrawerDatabase = db;
    }

  }
};
</script>