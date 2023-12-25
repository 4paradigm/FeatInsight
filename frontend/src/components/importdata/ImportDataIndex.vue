<template>

<div>

  <a-modal v-model:visible="isShowCreateModal" width="1000px" :title="$t('Create Index')" @ok="clickCreateModalOk">
    <CreateIndexForm ref="CreateIndexForm"></CreateIndexForm>
  </a-modal>

  <a-modal v-model:visible="isShowSqlModal" width="1000px" :title="$t('Create From SQL')" @ok="clickSqlModalOk">
    <CreateIndexFromSqlForm ref="CreateIndexFromSqlForm"></CreateIndexFromSqlForm>
  </a-modal>

  <br/>
  <a-button type="primary" @click="clickCreateButton">{{ $t('Add Index') }}</a-button>

  &nbsp;&nbsp;
  <a-dropdown>
      <template #overlay>
        <a-menu>
          <a-menu-item @click="clickCreateFromSqlButton">{{ $t('Create From SQL') }}</a-menu-item>
        </a-menu>
      </template>
      <a-button>{{ $t('Other Create Methods') }}</a-button>
  </a-dropdown>

  <br/><br/>
  <OnlineTables :key="refreshDataKey"></OnlineTables>

</div>
</template>
  
<script>
import OnlineTables from '@/components/table/OnlineTables.vue';
import CreateIndexForm from '@/components/form/CreateIndexForm.vue';
import CreateIndexFromSqlForm from '@/components/form/CreateIndexFromSqlForm.vue'

export default {
  components: {
    OnlineTables,
    CreateIndexForm,
    CreateIndexFromSqlForm
  },

  data() {
    return {
      isShowCreateModal: false,
      isShowSqlModal: false
    };
  },

  methods: {

    clickCreateButton() {
      this.isShowCreateModal = true;
    },

    clickCreateFromSqlButton() {
      this.isShowSqlModal = true;
    },

    submittedForm() {
      this.isShowCreateModal = false;
      this.isShowSqlModal = false;
    },

    clickCreateModalOk() {
      this.isShowCreateModal = false;
      this.$refs.CreateIndexForm.submitForm();
    },

    clickSqlModalOk() {
      this.isShowSqlModal = false;
      this.$refs.CreateIndexFromSqlForm.submitForm();
    },


  }
};
</script>