<template>

<div>

  <br/>
  <a-button type="primary" @click="handleCreateDatabase">{{ $t('Create Database') }}</a-button>

  <!-- Create form modal -->
  <div>
    <a-modal v-model:visible="isOpenFormModal" width="1000px" :title="$t('Create Database')" @ok="handleOk">
      <CreateDatabaseForm @close="closeModal"></CreateDatabaseForm>
    </a-modal>
  </div>

  <!-- Databases table -->
  <br/>
  <a-table :columns="databaseColumns" :data-source="databases">
    <template #database="{ text, record }">
      <router-link :to="`/databases/${record}`">{{ text }}</router-link>
    </template>
  </a-table>

</div>
</template>
  
<script>
import axios from 'axios'
import { message } from 'ant-design-vue';
import CreateDatabaseForm from '@/components/form/CreateDatabaseForm.vue'

export default {
  components: {
    CreateDatabaseForm
  },

  data() {
    return {
      isOpenFormModal: false,

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

    handleCreateDatabase() {
      this.isOpenFormModal = true;
    },

    closeModal() {
      // Update data when closing modal for submission
      this.initData();

      this.isOpenFormModal = false;
    },

    handleOk() {
      this.isOpenFormModal = false;
    },

  }
};
</script>