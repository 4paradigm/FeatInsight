<template>

<a-modal v-model:visible="isShowLogin" :title="$t('Login')" @ok="clickLoginOk" >
  <LoginForm ref="LoginForm" />
</a-modal>

<a-layout class="layout">

  <a-layout-header :style="{ background: '#fff', padding: '0 50px' }">
    <div class="header-content">
      <div class="logo">FeatInsight</div>
      <a-button type="primary" class="login-button" @click="clickLoginButton">{{ $t('Login') }}</a-button>
    </div>
  </a-layout-header>

  <a-layout-content :style="{ padding: '0 300px' }">
    <div class="content">
      <h1 class="title">欢迎使用 FeatInsight 服务！</h1>
      <p class="intro">FeatInsight 是一个先进的特征存储(Feature Store)服务，基于 OpenMLDB 数据库实现高效的特征管理和编排功能。FeatInsight 提供简便易用的 UI 界面，用户可以进行机器学习特征开发的全流程，包括数据的导入、查看、编辑，特征的生成、存储、上线等功能。 针对离线场景中，用户可以选择特征生成离线样本用于后续的机器学习开发；针对在线场景中，用户可以选择特征创建特征服务，实现实时特征计算。</p>
      
      <br/>
      <a-carousel autoplay>
        <div v-for="image in projectImages" :key="image">
          <img :src="image" style="width: 100%;" />
        </div>
      </a-carousel>
    </div>
  </a-layout-content>

  <a-layout-footer :style="{ textAlign: 'center' }">
    <a href="https://github.com/4paradigm/OpenMLDB" target="_blank">OpenMLDB</a> & <a href="https://github.com/4paradigm/FeatInsight" target="_blank">FeatInsight</a> © 2021-2024
  </a-layout-footer>

</a-layout>
</template>

<script>
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
  min-height: 100vh;
}
.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.logo {
  color: #1890ff;
  font-size: 1.5em;
  font-weight: bold;
}
.login-button {
  margin-left: 20px;
}
.content {
  text-align: center;
  margin-top: 50px;
}

.title {
  font-size: 2em;
  color: #1890ff;
}

.intro {
  margin-top: 20px;
  color: #666;
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