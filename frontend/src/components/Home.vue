<template>
  <a-layout class="layout">
    <a-layout-sider
      :style="{
        overflow: 'auto',
        height: '100vh',
        position: 'fixed',
        left: 0,
        top: 0,
        bottom: 0,
      }"
    >
      <a-menu
        theme="dark"
        mode="inline"
        class="navi-menu"
        :openKeys="[
          'whole_process_wizard',
          'advanced_usage',
          'advanced_management',
          'languages',
        ]"
      >
        <a-menu-item key="bigscreen">
          <router-link to="/bigscreen">{{
            $t('Feature Big Screen')
          }}</router-link>
        </a-menu-item>

        <a-sub-menu key="whole_process_wizard">
          <template #title>{{ $t('Development Process') }}</template>

          <a-menu-item key="import">
            <router-link to="/developprocess/import/databases">{{
              $t('Data Import')
            }}</router-link>
          </a-menu-item>

          <a-menu-item key="offline">
            <router-link to="/developprocess/offline">{{
              $t('Offline Scenario')
            }}</router-link>
          </a-menu-item>

          <a-menu-item key="online">
            <router-link to="/developprocess/online">{{
              $t('Online Scenario')
            }}</router-link>
          </a-menu-item>
        </a-sub-menu>

        <a-sub-menu key="advanced_usage">
          <template #title>{{ $t('Advanced Usage') }}</template>

          <a-menu-item key="sql">
            <router-link to="/sql">{{ $t('SQL Playground') }}</router-link>
          </a-menu-item>

          <a-menu-item key="computedfeatures">
            <router-link to="/computedfeatures">{{
              $t('Computed Features')
            }}</router-link>
          </a-menu-item>
        </a-sub-menu>

        <a-sub-menu key="advanced_management">
          <template #title>{{ $t('Management') }}{{ $t('Center') }}</template>

          <a-menu-item key="features">
            <router-link to="/features">{{ $t('Features') }}</router-link>
          </a-menu-item>

          <a-menu-item key="tables">
            <router-link to="/tables">{{ $t('Data Tables') }}</router-link>
          </a-menu-item>

          <a-menu-item key="offlinesamples">
            <router-link to="/offlinesamples">{{
              $t('Offline Samples')
            }}</router-link>
          </a-menu-item>

          <a-menu-item key="offlinejobs">
            <router-link to="/offlinejobs">{{
              $t('Offline Jobs')
            }}</router-link>
          </a-menu-item>

          <a-menu-item key="featureservices">
            <router-link to="/featureservices">{{
              $t('Feature Services')
            }}</router-link>
          </a-menu-item>
        </a-sub-menu>

        <a-sub-menu key="languages">
          <template #title>{{ $t('Languages') }}</template>
          <a-menu-item key="en" @click="changeLocale('en')"
            >English</a-menu-item
          >
          <a-menu-item key="zh" @click="changeLocale('zh')">中文</a-menu-item>
        </a-sub-menu>

        <a-sub-menu key="login">
          <template #title>{{ $t('Authentication') }}</template>
          <a-menu-item @click="logout()">{{ $t('Logout') }}</a-menu-item>
        </a-sub-menu>
      </a-menu>
    </a-layout-sider>

    <a-layout :style="{ marginLeft: '200px' }" style="padding: 20px 50px">
      <a-layout-content>
        <router-view></router-view>
      </a-layout-content>

      <a-layout-footer style="text-align: center">
        <br />
        <div>
          <a href="https://github.com/4paradigm/OpenMLDB" target="_blank"
            >OpenMLDB</a
          >
          © 2021-2024
        </div>
      </a-layout-footer>
    </a-layout>
  </a-layout>
  <a-modal
    v-model:visible="visible"
    title="Confirm Logout"
    @ok="handleOk"
    @cancel="handleCancel"
  >
    Are you sure you want to log out?
  </a-modal>
</template>

<script setup>
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useUserStore } from '@/stores/user';
import { notification } from 'ant-design-vue';

const { locale: localeI18n } = useI18n();

const visible = ref(false);

const changeLocale = (locale) => {
  localeI18n.value = locale;
};

const logout = () => {
  visible.value = true;
};

const handleOk = () => {
  // TODO: Use $t('Execute Success')
  notification['success']({
    message: 'Execute Success',
    description: `Success to logout`,
  });

  const userStore = useUserStore();
  userStore.logout();

  visible.value = false;
};

const handleCancel = () => {
  notification['success']({
    message: 'Execute Success',
    description: `Cancel logout`,
  });

  visible.value = false;
};
</script>
