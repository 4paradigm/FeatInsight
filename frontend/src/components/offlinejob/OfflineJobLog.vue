<template>
<div>

  <h2>{{ $t('Offline Job Log') }}: {{ id }} </h2>

  <pre>{{ jobLog }}</pre>

</div>
</template>

<script>
import axios from 'axios';
import { notification } from 'ant-design-vue'

export default {
  props: {
    id: {
      type: Number,
      required: true,
    }
  },

  data() {
    return {
      jobLog: ""
    };
  },

  mounted() {
    this.initData();
  },

  methods: {
    initData() {
      axios
        .get(`/api/offlinejobs/${this.id}/log`)
        .then((response) => {
          this.jobLog = response.data;
        })
        .catch((error) => {
          notification["error"]({
              message: this.$t('Execute Fail'),
              description: error.message
            });

        })
        .finally(() => {});
    }
  }

};
</script>