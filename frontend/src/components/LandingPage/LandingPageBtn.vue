<template>
  <a-button class="landing-button">{{ text }}</a-button>
</template>

<script setup lang="ts">
import { ButtonProps } from 'ant-design-vue';
defineProps<{
  text: string;
  click: () => void;
  params?: ButtonProps;
}>();
</script>

<script lang="ts"></script>

<style lang="less" scoped>
.landing-button {
  color: #fff;
  background: #181818;
  border-color: #fff;

  &:hover,
  &:active,
  &:focus {
    color: #fff;
    background: #181818;
    border-color: #fff;
  }
}
</style>
