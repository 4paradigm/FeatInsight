<script setup>
</script>

<template>

<a-layout class="layout">
  <a-layout-sider :style="{ overflow: 'auto', height: '100vh', position: 'fixed', left: 0, top: 0, bottom: 0 }">
    <a-menu theme="dark" mode="inline" class="navi-menu" :openKeys="['whole_process_wizard', 'advanced_management', 'languages']">

      <a-menu-item key="bigscrren">
        <router-link to='/'>{{ $t('Feature Big Screen') }}</router-link>
      </a-menu-item>

      <a-sub-menu key="whole_process_wizard">
        <template #title>{{ $t('Development Process') }}</template>

        <a-menu-item key="import">
          <router-link to='/developprocess/import/databases'>{{ $t('Data Import')}}</router-link>
        </a-menu-item>

        <a-menu-item key="offline">
          <router-link to='/developprocess/offline'>{{ $t('Offline')}}{{$t('Development')}}</router-link>
        </a-menu-item>

        <a-menu-item key="online">
          <router-link to='/developprocess/online'>{{ $t('Online') }}{{ $t('Development') }}</router-link>
        </a-menu-item>

        <a-menu-item key="executesql">
          <router-link to='/sql'>{{ $t('Execute SQL') }}</router-link>
        </a-menu-item>
      </a-sub-menu>

      <a-sub-menu key="advanced_management">
        <template #title>{{ $t('Management') }}{{ $t('Center') }}</template>
       
        <a-menu-item key="features">
          <router-link to='/features'>{{ $t('Features') }}</router-link>
        </a-menu-item>
       
        <a-menu-item key="tables">
          <router-link to='/tables'>{{ $t('Data Tables') }}</router-link>
        </a-menu-item>

        <a-menu-item key="offlinesamples">
          <router-link to='/offlinepage'>{{ $t('Offline Samples') }}</router-link>
        </a-menu-item>

        <a-menu-item key="featureservices">
          <router-link to='/featureservices'>{{ $t('Feature Services') }}</router-link>
        </a-menu-item>
      </a-sub-menu>

      <a-sub-menu key="languages">
        <template #title>{{ $t('Languages') }}</template>
        <a-menu-item key="100" @click="changeLocale('en')">English</a-menu-item>
        <a-menu-item key="101" @click="changeLocale('zh')">中文</a-menu-item>
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
        <a href="https://github.com/4paradigm/OpenMLDB" target="_blank">OpenMLDB</a> © 2021-2023
      </div>
    </a-layout-footer>
  </a-layout>

</a-layout>
</template>

<script>
export default {
  methods: {
    changeLocale(locale) {
      this.$i18n.locale = locale;
    }
  }
}
</script>
