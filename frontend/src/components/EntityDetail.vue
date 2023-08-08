<template>
<div>

  <br/>
  <h1>{{ $t('Entity') }}: {{ data.name }} </h1>
  <a-descriptions bordered>
    <a-descriptions-item :span="24" :label="$t('Name')"> {{ data.name }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Primary Keys')">{{ data.primaryKeys }}</a-descriptions-item>
  </a-descriptions>

</div>
</template>

<script>
import axios from 'axios';
import { message } from 'ant-design-vue';
import { ref, onMounted } from 'vue';

export default {
  props: {
    name: {
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
        .get(`/api/entities/${this.name}`)
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