<template>

<div>

  <a-result
    status="success"
    :title="$t('Success to submit offline job')"
    sub-title="离线任务需要1到10分钟，请等待。"
  >
    <template #extra>
      <a-button type="primary" @click="redirectToDetail">{{ $t('More details') }}</a-button>
    </template>
  </a-result>

</div>
</template>
  
<script>

export default {
  props: {
        id: {
            type: Number,
            required: true,
        },
    },

  data() {
    return {
    };
  },

  mounted() {
  },

  methods: {
    redirectToDetail() {
      this.$router.push(`/offlinejobs/${this.id}`);
    }
  },
};
</script>