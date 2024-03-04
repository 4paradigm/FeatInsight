<script lang="ts" setup>
import { useUserStore } from './stores/user';
import Home from './components/Home.vue';
import LandingPage from './components/LandingPage/index.vue';

const userStore = useUserStore();
</script>

<template>
  <div v-if="userStore.isAuthenticated">
    <Home />
  </div>

  <div v-else>
    <LandingPage />
  </div>
</template>
