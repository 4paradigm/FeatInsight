<template>

<div>
  <br/>
  <a-button type="primary" @click="clickCreateButton">{{ $t('Create Database') }}</a-button>

  <!-- Create database modal -->
  <a-modal v-model:visible="isShowModal" width="1000px" :title="$t('Create Database')" @ok="clickModalOk">
    <CreateDatabaseForm ref="CreateDatabaseForm" @submitted="submittedForm"></CreateDatabaseForm>
  </a-modal>

  <DatabasesTable :key="refreshDataKey"></DatabasesTable>

</div>
</template>
  
<script>
import CreateDatabaseForm from '@/components/form/CreateDatabaseForm.vue'
import DatabasesTable from '@/components/database/DatabasesTable.vue'

export default {
  components: {
    CreateDatabaseForm,
    DatabasesTable
  },

  data() {
    return {
      isShowModal: false,

      refreshDataKey: 0
    };
  },

  methods: {
    clickCreateButton() {
      this.isShowModal = true;
    },

    submittedForm() {
      this.isShowModal = false;
      // Update data when closing modal
      this.refreshDataKey++;
    },

    clickModalOk() {
      this.isShowModal = false;
      // Submit the form in child component
      this.$refs.CreateDatabaseForm.submitForm();
    }
  }
};
</script>