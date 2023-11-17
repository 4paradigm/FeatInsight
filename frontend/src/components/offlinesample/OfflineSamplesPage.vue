<template>
<div>
  <a-modal v-model:visible="isShowCreateOfflineSampleModal" width="1000px" :title="$t('Create Offline Sample')" @ok="clickCreateOfflineSampleOk">
    <CreateOfflineSampleForm ref="CreateOfflineSampleForm"></CreateOfflineSampleForm>
  </a-modal>

  <br/>
  <h1>
    {{ $t('All Offline Samples') }}<a-button type="link" @click="showCreateOfflineSampleFormModal"><PlusCircleOutlined /></a-button>
  </h1>

  <OfflineSamplesData />
  
</div>
</template>
    
<script>
import OfflineSamplesData from '@/components/offlinesample/OfflineSamplesData.vue';
import { PlusCircleOutlined } from '@ant-design/icons-vue';
import CreateOfflineSampleForm from '@/components/form/CreateOfflineSampleForm.vue'

export default {
  components: {
    OfflineSamplesData,
    CreateOfflineSampleForm,
    PlusCircleOutlined
  },

  data() {
    return {
      isShowCreateOfflineSampleModal: false
    }
  },

  methods: {
    showCreateOfflineSampleFormModal() {
      this.isShowCreateOfflineSampleModal = true;
    },

    clickCreateOfflineSampleOk() {
      this.$refs.CreateOfflineSampleForm.submitForm();

      this.isShowCreateOfflineSampleModal = false;
    }
  }

};
</script>