<template>

<div>

  <!-- TODO: :sub-title or :subTitle does not work -->
  <!-- :sub-title="$('Text of creating offline sample')" -->
  <a-result
    status="success"
    :title="$t('Success to create table')"
    sub-title="创建表需要1到3分钟，请等待。"
  >
    <template #extra>
      <a-button type="primary" @click="redirectToTablesPage">{{ $t('Data Tables') }}</a-button>
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
        name: {
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
      this.$router.push(`/tables`);
    }
  },
};
</script>