<template>
  <div>
    <h2>{{ $t('Offline Sample') }}: {{ data.jobId }}</h2>
    <a-descriptions bordered>
      <a-descriptions-item :span="24" :label="$t('Job ID')">
        {{ data.jobId }}</a-descriptions-item
      >
      <a-descriptions-item :span="24" :label="$t('Feature Names')">{{
        data.featureNames
      }}</a-descriptions-item>
      <a-descriptions-item :span="24" :label="$t('Path')">
        {{ data.path }}</a-descriptions-item
      >
      <a-descriptions-item :span="24" :label="$t('Options')">
        {{ data.options }}</a-descriptions-item
      >
      <a-descriptions-item :span="24" :label="$t('Database')">
        {{ data.db }}</a-descriptions-item
      >
      <a-descriptions-item :span="24" :label="$t('SQL')">
        {{ data.sql }}</a-descriptions-item
      >
    </a-descriptions>

    <br />
    <OfflineJobDetail :id="id"></OfflineJobDetail>
  </div>
</template>

<script>
import axios from 'axios';
import { notification } from 'ant-design-vue';
import OfflineJobDetail from '@/components/offlinejob/OfflineJobDetail.vue';
import { routerPropsIdValidator } from '../../utils';

export default {
  components: {
    OfflineJobDetail,
  },

  props: {
    id: {
      validator: routerPropsIdValidator,
      required: true,
    },
  },

  data() {
    return {
      data: '',
    };
  },
  methods: {
    initData() {
      axios
        .get(`/api/offlinesamples/${this.id}`)
        .then((response) => {
          this.data = response.data;
        })
        .catch((error) => {
          notification['error']({
            message: this.$t('Execute Fail'),
            description: error.message,
          });
        })
        .finally(() => {
          // You can perform any additional logic here after the request completes.
        });
    },
  },
  mounted() {
    this.initData();
  },
};
</script>
