<template>

<div>

  <a-result
    status="success"
    title="成功创建训练样本"
    sub-title="离线导出样本任务需要1到10分钟，请等待。"
  >
    <template #extra>
      <a-button type="primary" @click="routerToTrainingSetDetail">查看详情</a-button>
      <a-button @click="routerToTrainingSetPage">继续创建</a-button>
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
    routerToTrainingSetDetail() {
      this.$router.push(`/trainingsets/${this.id}`);
    },

    routerToTrainingSetPage() {
      this.$router.push(`/trainingsets`);
    }
  },
};
</script>