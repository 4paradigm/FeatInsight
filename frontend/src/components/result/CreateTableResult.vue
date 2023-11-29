<template>

<div>

  <!-- TODO: :sub-title or :subTitle does not work -->
  <!-- :sub-title="$('Text of creating offline sample')" -->
  <a-result
    status="success"
    title="成功提交创建表任务"
    sub-title="请稍等0到2分钟，创建表任务属于离线任务，如果创建失败可以在离线任务查看最新任务的日志。"
  >
    <template #extra>
      <a-button type="primary" @click="redirectToTablesPage">{{ $t('Data Tables') }}</a-button>
      <a-button @click="redirectToOfflineJobsPage">{{ $t('Offline Jobs') }}</a-button>
    </template>
  </a-result>

</div>
</template>
  
<script>

export default {
  props: {
        db: {
            type: String,
            required: true,
        },
        table: {
            type: String,
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
    redirectToTablesPage() {
      this.$router.push(`/tables/${this.db}/${this.table}`);
    },

    redirectToOfflineJobsPage() {
      this.$router.push(`/offlinejobs`);
    }
  },
};
</script>