<template>
  <div>
    <h2>
      {{ $t('Offline Job') }}:
      <router-link :to="`/offlinejobs/${data.id}`">{{ data.id }}</router-link>
    </h2>

    <a-button @click="refreshStatus" type="primary">{{
      $t('Refresh Status')
    }}</a-button>

    <br /><br />
    <a-descriptions bordered>
      <a-descriptions-item :span="24" :label="$t('Job ID')">
        {{ data.id }}
      </a-descriptions-item>
      <a-descriptions-item :span="24" :label="$t('Job Type')">{{
        data.jobType
      }}</a-descriptions-item>
      <a-descriptions-item :span="24" :label="$t('State')">
        {{ data.state }}
        <a-progress
          type="circle"
          :percent="statePercent"
          :status="stateStatus"
          :show-info="false"
          :width="20"
        />
      </a-descriptions-item>
      <a-descriptions-item :span="24" :label="$t('Start Time')">
        {{ data.startTime }}</a-descriptions-item
      >
      <a-descriptions-item :span="24" :label="$t('End Time')">
        {{ data.endTime }}</a-descriptions-item
      >
      <a-descriptions-item :span="24" :label="$t('Parameter')">
        {{ data.parameter }}</a-descriptions-item
      >
      <a-descriptions-item :span="24" :label="$t('Cluster')">
        {{ data.cluster }}</a-descriptions-item
      >
      <a-descriptions-item :span="24" :label="$t('Application ID')">
        {{ data.applicationId }}</a-descriptions-item
      >
      <a-descriptions-item :span="24" :label="$t('Error')">
        {{ data.error }}</a-descriptions-item
      >
    </a-descriptions>

    <br />
    <a-switch
      v-model:checked="isShowJobLog"
      :checked-children="$t('Display log')"
      :un-checked-children="$t('Hide log')"
      @click="showJobLog"
    />
    &nbsp;<router-link :to="`/offlinejobs/${id}/log`"
      ><a-button>{{ $t('Check Completed Log') }}</a-button></router-link
    >

    <br /><br />
    <div v-if="isShowJobLog">
      <pre>{{ jobLog }}</pre>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { notification } from 'ant-design-vue';

export default {
  props: {
    id: {
      type: Number,
      required: true,
    },
  },

  data() {
    return {
      data: '',

      statePercent: 0,
      stateStatus: 'active',

      jobLog: '',
      isShowJobLog: false,
    };
  },

  mounted() {
    this.initData();
  },

  methods: {
    initData() {
      axios
        .get(`/api/offlinejobs/${this.id}`)
        .then((response) => {
          this.data = response.data;

          if (this.data.state.toLowerCase() === 'failed') {
            (this.statePercent = 90), (this.stateStatus = 'exception');
          } else if (this.data.state.toLowerCase() === 'finished') {
            (this.statePercent = 100), (this.stateStatus = 'success');
          } else {
            (this.statePercent = 50), (this.stateStatus = 'active');
          }
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

    showJobLog() {
      if (this.isShowJobLog) {
        axios
          .get(`/api/offlinejobs/${this.id}/log`)
          .then((response) => {
            this.jobLog = response.data;
          })
          .catch((error) => {
            notification['error']({
              message: this.$t('Execute Fail'),
              description: error.message,
            });
          })
          .finally(() => {});
      }
    },

    refreshStatus() {
      this.initData();

      notification['success']({
        message: this.$t('Execute Success'),
        description: 'Success to refresh job status',
      });
    },
  },
};
</script>
