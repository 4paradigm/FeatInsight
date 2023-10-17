<template>
<div>

  <br/>
  <h1>{{ $t('Offline Job') }}: {{ data.id }} </h1>

  <br/>
  <a-descriptions bordered>
    <a-descriptions-item :span="24" :label="$t('Job ID')"> {{ data.id }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Job Type')">{{ data.jobType }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('State')">
      {{ data.state }} &nbsp;
      <a-progress type="circle" :percent="statePercent" :status="stateStatus" :size="60" />
    </a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Start Time')"> {{ data.startTime }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('End Time')"> {{ data.endTime }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Parameter')"> {{ data.parameter }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Cluster')"> {{ data.cluster }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Application ID')"> {{ data.applicationId }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Error')"> {{ data.error }}</a-descriptions-item>
  </a-descriptions>


  <br/>
  <a-switch v-model:checked="isShowJobLog" checked-children="$t('Diskplay log')" un-checked-children="Hide log" @click="showJobLog"/>
  <br/><br/>
  <div v-if="isShowJobLog">
    <pre>{{ jobLog }}</pre>
  </div>


</div>
</template>

<script>
import axios from 'axios';
import { message } from 'ant-design-vue';
import { ref, onMounted } from 'vue';

export default {
  props: {
    id: {
      type: Number,
      required: true,
    },
  },
  data() {
    return {
      data: "",

      statePercent: 0,
      stateStatus: 'active',

      jobLog: "",
      isShowJobLog: false,
    };
  },
  methods: {
    initData() {
      axios
        .get(`/api/offlinejobs/${this.id}`)
        .then((response) => {
          this.data = response.data;

          if (this.data.state.toLowerCase() === "failed") {
            this.statePercent = 90,
            this.stateStatus = 'exception'
          } else if (this.data.state.toLowerCase() === "completed") {
            this.statePercent = 100,
            this.stateStatus = 'success'
          } else {
            this.statePercent = 50,
            this.stateStatus = 'active'
          }
        })
        .catch((error) => {
          message.error(error.message);
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
            message.error(error.message);
          })
          .finally(() => {});
      }
    },


  },
  mounted() {
    this.initData();
  },
};
</script>