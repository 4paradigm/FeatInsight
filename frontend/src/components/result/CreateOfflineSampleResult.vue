<template>
  <div>
    <!-- TODO: :sub-title or :subTitle does not work -->
    <!-- :sub-title="$('Text of creating offline sample')" -->
    <a-result
      status="success"
      :title="$t('Success to create offline sample')"
      sub-title="离线导出样本任务需要1到10分钟，请等待。"
    >
      <template #extra>
        <a-button type="primary" @click="redirectToDetail">{{
          $t('More details')
        }}</a-button>
        <a-button @click="redirectToCreateMore">{{
          $t('Create more')
        }}</a-button>
      </template>
    </a-result>
  </div>
</template>

<script>
import { routerPropsIdValidator } from '../../utils';

export default {
  props: {
    id: {
      validator: routerPropsIdValidator,
      required: true,
    },
  },

  data() {
    return {};
  },

  mounted() {},

  methods: {
    redirectToDetail() {
      this.$router.push(`/offlinesamples/${this.id}`);
    },

    redirectToCreateMore() {
      this.$router.push(`/offlinesamples`);
    },
  },
};
</script>
