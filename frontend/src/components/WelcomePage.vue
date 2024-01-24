<template>

  <a-modal v-model:visible="isShowLogin" width="1000px" :title="$t('Login')" @ok="clickLoginOk" >
    <LoginForm ref="LoginForm" />
  </a-modal>

  <a-layout class="layout">
    <a-layout-header class="header">
      <div class="header-content">
        <div class="logo">Feat Insight</div>
        <div class="nav-buttons">
          <a-button type="primary" @click="clickLoginButton">{{ $t('Login')}}</a-button>
        </div>
      </div>
    </a-layout-header>
    <a-layout-content>
      <a-row :gutter="16">
        <a-col :span="24">
          <a-card>
            <a-carousel autoplay>
              <div v-for="image in projectImages" :key="image">
                <img :src="image" style="width: 100%;" />
              </div>
            </a-carousel>
          </a-card>
        </a-col>
      </a-row>
    </a-layout-content>
  </a-layout>
</template>

<script>
import { ref } from 'vue';
import LoginForm from '@/components/form/LoginForm.vue';

export default {
  components: {
    LoginForm
  },

  data() {
      return {
        isShowLogin: false,

        projectImages: [
          'bigscreen.png',
          'online_scenario.png',
          'offline_scenario.png',
          'sql_playground.png',
          'computed_feature.png'
        ]
      }
  },

  methods: {
    clickLoginButton() {
      this.isShowLogin = true;
    },

    clickLoginOk() {
      this.isShowLogin = false;

      this.$refs.LoginForm.submitForm();
    }
  }
};


</script>

<style scoped>
.layout {
  height: 100vh;
}

.header {
  background-color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 50px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.logo {
  font-size: 20px;
  font-weight: bold;
}

.nav-buttons a {
  margin-left: 20px;
}

/* Adjust the carousel image height as needed */
.a-carousel .slick-slide {
  text-align: center;
  height: 400px;
  line-height: 400px;
  background: #364d79;
  overflow: hidden;
}

.a-carousel img {
  max-height: 100%;
}
</style>