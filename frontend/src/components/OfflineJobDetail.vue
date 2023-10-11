<template>
<div>

  <br/>
  <h1>{{ $t('Offline Job') }}: {{ data.id }} </h1>
  <a-descriptions bordered>
    <a-descriptions-item :span="24" :label="$t('ID')"> {{ data.id }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Job Type')">{{ data.jobType }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('State')"> {{ data.state }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Start Time')"> {{ data.startTime }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('End Time')"> {{ data.endTime }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Parameter')"> {{ data.parameter }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Cluster')"> {{ data.cluster }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Application ID')"> {{ data.applicationId }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Error')"> {{ data.error }}</a-descriptions-item>
  </a-descriptions>

</div>
</template>

<script>
import axios from 'axios';
import { message } from 'ant-design-vue';
import { ref, onMounted } from 'vue';

export default {
  props: {
    id: {
      // TODO: Change to int type
      type: String,
      required: true,
    },
  },
  data() {
    return {
      data: "",
    };
  },
  methods: {
    initData() {
      axios
        .get(`/api/offlinejobs/${this.id}`)
        .then((response) => {
          this.data = response.data;
        })
        .catch((error) => {
          message.error(error.message);
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