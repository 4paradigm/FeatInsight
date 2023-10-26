<template>
<div>
  <!-- Databases table -->
  <a-table :columns="databaseColumns" :data-source="databases">
    <template #database="{ text, record }">
      <router-link :to="`/databases/${record}`">{{ text }}</router-link>
    </template>
  </a-table>

</div>
</template>
  
<script>
import axios from 'axios'

export default {
  data() {
    return {
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

  }
};
</script>