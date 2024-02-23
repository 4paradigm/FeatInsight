<script setup lang="ts">
import { ref } from 'vue';
import {
  ChromeOutlined,
  MailOutlined,
  GithubOutlined,
  WechatOutlined,
  ZhihuOutlined,
  YoutubeOutlined,
  SlackOutlined,
} from '@ant-design/icons-vue';
import LoginForm from '../form/LoginForm.vue';
import LandingPageBtn from './LandingPageBtn.vue';
import FeatureListItem from './FeatureListItem.vue';

const isShowLogin = ref(false);
const loginForm = ref();

const handleClickLogin = () => {
  isShowLogin.value = true;
};

const handleOk = () => {
  isShowLogin.value = false;
  loginForm.value.submitForm();
};
</script>

<template>
  <a-modal v-model:visible="isShowLogin" title="登录" @ok="handleOk">
    <LoginForm ref="loginForm" />
  </a-modal>

  <a-layout class="layout">
    <a-layout-header :style="{ background: '#181818', padding: '0 50px' }">
      <div class="header-content">
        <LandingPageBtn @click="handleClickLogin" text="Login" />
      </div>
    </a-layout-header>

    <a-layout-content>
      <div class="content">
        <div class="main-content">
          <div class="title">
            <h1>FeatInsight，</h1>
            <h1>为更简单高效的机器学习助力</h1>
          </div>
          <p class="intro">
            FeatInsight 是一个先进的特征存储(<strong>Feature Store</strong
            >)服务，基于
            <strong>OpenMLDB 数据库</strong
            >实现高效的特征管理和编排功能。FeatInsight 提供简便易用的 UI
            界面，用户可以进行机器学习特征开发的全流程，包括数据的导入、查看、编辑，特征的生成、存储、上线等功能。
            针对离线场景中，用户可以选择特征生成离线样本用于后续的机器学习开发；针对在线场景中，用户可以选择特征创建特征服务，实现实时特征计算。
          </p>

          <div class="btn-wrapper">
            <a-space size="large">
              <!-- TODO：注册弹窗 -->
              <a-button type="primary">Try For Free</a-button>
              <LandingPageBtn
                href="https://www.openmldb.com"
                target="__blank"
                text="Learn More"
              />
            </a-space>
          </div>

          <div class="screens-wrapper">
            <img src="@/assets/images/screens.png" style="width: 100%" />
          </div>

          <div class="feature-wrapper">
            <p>核心功能</p>
            <p>
              FeatInsight
              主要包括的功能有：数据管理、特征管理、在线场景、离线场景、SQL
              实验室和预计算特征
            </p>
            <div class="feature-list-wrapper">
              <img src="@/assets/images/feature.png" style="width: 50%" />
              <div class="feature-list">
                <FeatureListItem
                  title="数据管理"
                  content="用于导入和管理特征工程需要的原始数据和在线数据"
                  :style="{ marginRight: '70px' }"
                />
                <FeatureListItem
                  title="特征管理"
                  content="用于存储原始特征数据和派生特征数据的存储系统"
                />
                <FeatureListItem
                  title="在线场景"
                  content="上线特征服务，使用在线数据提供硬实时的在线特征抽取接口"
                  :style="{ marginRight: '70px' }"
                />
                <FeatureListItem
                  title="离线场景"
                  content="对离线数据进行特征计算并导出样本文件，提供离线样本、任务管理功能"
                />
                <FeatureListItem
                  title="SQL 实验室"
                  content="可调试和执行任意的 OpenMLDB SQL 语句，使用在线模式或离线模型完成特征计算任务"
                  :style="{ marginRight: '70px' }"
                />
                <FeatureListItem
                  title="预计算特征"
                  content="用户可以通过预计算把特征值直接存入 OpenMLDB 在线表中，然后访问在线表数据进行读写特征"
                />
              </div>
            </div>
          </div>
        </div>

        <!-- <div class="partners-wrapper">
          <p>服务伙伴</p>
          <a-space>
            <img src="@/assets/images/transsion.png" style="width: 70%" />
            <img src="@/assets/images/huawei.png" style="width: 70%" />
            <img src="@/assets/images/sanqi.png" style="width: 70%" />
            <img src="@/assets/images/akulaku.png" style="width: 70%" />
            <img src="@/assets/images/intel.png" style="width: 70%" />
          </a-space>
          <LandingPageBtn
            href="https://www.openmldb.com/case/"
            target="__blank"
            text="All Partners"
            :style="{ marginTop: '55px' }"
          />
        </div> -->
      </div>
    </a-layout-content>

    <a-layout-footer :style="{ display: 'flex', justifyContent: 'center' }">
      <div class="link-wrapper">
        <div>
          <p>
            <ChromeOutlined
              :style="{ color: '#fff', marginRight: '5px', fontSize: '18px' }"
            />
            <a href="https://www.openmldb.com" target="_blank"
              >https://www.openmldb.com</a
            >
          </p>
          <p>
            <MailOutlined
              :style="{ color: '#fff', marginRight: '5px', fontSize: '16px' }"
            /><a href="" target="_blank">contact@openmldb.ai</a>
          </p>
        </div>
        <p class="bottom-icon-wrapper">
          <a-space size="middle">
            <a href="https://github.com/4paradigm/OpenMLDB" target="_blank"
              ><GithubOutlined :style="{ color: '#fff', fontSize: '16px' }"
            /></a>
            <a-popover placement="top">
              <template #content>
                <img src="@/assets/images/qrcode.png" style="width: 100px" />
              </template>
              <WechatOutlined :style="{ color: '#fff', fontSize: '16px' }" />
            </a-popover>
            <a
              href="https://www.zhihu.com/column/c_1417199590352916480"
              target="_blank"
              ><ZhihuOutlined :style="{ color: '#fff', fontSize: '16px' }"
            /></a>
            <a
              href="https://www.youtube.com/channel/UCf1xOOSzlh63DurvOhwnnZA/featured"
              target="_blank"
              ><YoutubeOutlined :style="{ color: '#fff', fontSize: '16px' }"
            /></a>
            <a
              href="https://blog.csdn.net/weixin_48409843/category_11340293.html"
              target="_blank"
            >
              <img src="@/assets/images/csdn-icon.png" style="width: 50px" />
            </a>
            <a
              href="https://join.slack.com/t/openmldb/shared_invite/zt-ozu3llie-K~hn9Ss1GZcFW2~K_L5sMg"
              target="_blank"
              ><SlackOutlined :style="{ color: '#fff', fontSize: '16px' }"
            /></a>
          </a-space>
        </p>
      </div>
      <!-- <a href="https://github.com/4paradigm/OpenMLDB" target="_blank"
        >OpenMLDB</a
      >
      &
      <a href="https://github.com/4paradigm/FeatInsight" target="_blank"
        >FeatInsight</a
      >
      © 2021-2024 -->
    </a-layout-footer>
  </a-layout>
</template>

<style lang="less" scoped>
.layout {
  min-height: 100vh;
  background: #181818;
}
.header-content {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  height: 100%;
}
.content {
}

.main-content {
  .title {
    padding: 95px 310px 0 310px;
    h1 {
      font-size: 60px;
      font-weight: normal;
      line-height: 60px;
      letter-spacing: 0.2px;
      color: #fff;
    }
  }

  .intro {
    padding: 0 310px;
    color: #fff;
    font-size: 16px;
    font-weight: 250;

    strong {
      font-weight: 600;
    }
  }
}
.btn-wrapper {
  width: 100%;
  display: flex;
  text-align: center;
  justify-content: center;
  margin-top: 80px;
}

.screens-wrapper {
  padding: 0 200px 100px;
  margin-top: 50px;
}

.feature-wrapper {
  padding: 162px 50px;
  text-align: center;
  p:nth-child(1) {
    font-size: 52px;
    font-weight: 500;
    color: #fff;
  }
  p:nth-child(2) {
    font-size: 16px;
    font-weight: 250;
    color: #fff;
  }
  .feature-list-wrapper {
    display: flex;

    .feature-list {
      width: 50%;
      display: flex;
      flex-wrap: wrap;
      text-align: left;
      margin-top: 160px;
      .ant-card {
        background: transparent;
        margin-right: 50px;
        width: 270px;
        height: 170px;

        .ant-card-head {
          border: none;
        }
      }
    }
  }
}

.partners-wrapper {
  padding: 0 50px 162px;
  text-align: center;
  p {
    font-size: 52px;
    font-weight: 500;
    color: #fff;
  }
}

.ant-layout-footer {
  background: #181818;
  padding: 0 162px 162px;

  .link-wrapper {
    // text-align: left;
    // width: 100%;
    margin: 0 auto;

    // display: flex;
    // flex-direction: column;
    // justify-content: center;
  }

  a {
    font-size: 16px;
    font-weight: 250;
    color: #fff;
  }
}
</style>
