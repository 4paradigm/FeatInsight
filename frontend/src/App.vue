<template>
    
    <div v-if="userStore.isAuthenticated">
        <Home />    
    </div>

    <div v-else>
        <LandingPage />
    </div>

</template>

<script>
import Home from "@/components/Home.vue";
import LandingPage from '@/components/LandingPage.vue'
import { useUserStore } from '@/stores/user';

export default {
  components: {
    Home,
    LandingPage
  },

  data() {
    return {
      userStore: useUserStore(),
    }
  }
}
</script>



