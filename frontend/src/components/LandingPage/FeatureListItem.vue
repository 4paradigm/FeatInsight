<script setup lang="ts">
import { CSSProperties } from 'vue';

const props = defineProps<{
  title: string;
  content: string;
  style?: CSSProperties;
}>();
</script>

<template>
  <div class="wrapper" :style="style">
    <div class="title">
      {{ props.title }}
    </div>
    <div class="content">
      {{ props.content }}
    </div>
  </div>
</template>

<style scoped lang="less">
.wrapper {
  width: 40%;
  height: 170px;

  .title {
    font-size: 16px;
    line-height: 30px;
    color: #fff;
  }

  .content {
    font-size: 14px;
    font-weight: 250;
    line-height: 30px;
    color: #fff;
  }
}
</style>
