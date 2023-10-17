<template>

<div>

  <!-- TODO: :sub-title or :subTitle does not work -->
  <!-- :sub-title="$('Text of creating training set')" -->
  <a-result
    status="success"
    :title="$t('Success to deploy feature service')"
    sub-title="部署特征服务成功，可通过浏览器或API访问在线特征。"
  >
    <template #extra>
      <a-button type="primary" @click="redirectToDetail">{{ $t('More details') }}</a-button>
      <a-button @click="redirectToCreateMore">{{ $t('Create more') }}</a-button>
    </template>
  </a-result>

</div>
</template>
  
<script>

export default {
  props: {
    name: {
      type: String,
      required: true
    },
    version: {
      type: String,
      required: true
    }
  },

  data() {
    return {
    };
  },

  mounted() {
  },

  methods: {
    redirectToDetail() {
      this.$router.push(`/featureservices/${this.name}/${this.version}`);
    },

    redirectToCreateMore() {
      this.$router.push(`/featureservices`);
    }
  },
};
</script>