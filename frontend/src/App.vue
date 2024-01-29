<template>
    
    <div v-if="userStore.isAuthenticated">
        <Home />    
    </div>

    <div v-else>
        <WelcomePage />
    </div>

</template>

<script>
import Home from "@/components/Home.vue";
import WelcomePage from '@/components/WelcomePage.vue'
import { useUserStore } from '@/stores/user';

export default {
  components: {
    Home,
    WelcomePage
  },

  data() {
    return {
      userStore: useUserStore(),
    }
  }
}
</script>



