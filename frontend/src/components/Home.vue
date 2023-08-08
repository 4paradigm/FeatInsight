<script setup>
</script>

<template>

<a-layout class="layout">
  <a-layout-sider :style="{ overflow: 'auto', height: '100vh', position: 'fixed', left: 0, top: 0, bottom: 0 }">
    <a-menu theme="dark" mode="inline" class="navi-menu" :openKeys="['whole_process_wizard', 'advanced_management']">

      <a-menu-item key="1">
        <router-link to='/'>{{ $t('Feature Big Screen') }}</router-link>
      </a-menu-item>

      <a-sub-menu key="whole_process_wizard">
        <template #title>{{ $t('Whole Process Wizard') }}</template>

        <a-menu-item key="2">
          <router-link to='/tables/import'>{{ $t('Step') }}1: {{ $t('Import Table') }}</router-link>
        </a-menu-item>

        <a-menu-item key="3">
          <router-link to='/features/create'>{{ $t('Step') }}2: {{ $t('Develop Feature') }}</router-link>
        </a-menu-item>

        <a-menu-item key="4">
          <router-link to='/featureservices/deploy'>{{ $t('Step') }}3: {{ $t('Create Service') }}</router-link>
        </a-menu-item>

        <a-menu-item key="5">
          <router-link to='/featureservices/test'>{{ $t('Step') }}4: {{ $t('Test Service') }}</router-link>
        </a-menu-item>
      </a-sub-menu>

      <a-sub-menu key="advanced_management">
        <template #title>{{ $t('Advanced Management') }}</template>
        <a-menu-item key="6">
          <router-link to='/tables'>{{ $t('Data Tables') }}</router-link>
        </a-menu-item>
        <a-menu-item key="7">
          <router-link to='/entities'>{{ $t('Entities') }}</router-link>
        </a-menu-item>
        <a-menu-item key="8">
          <router-link to='/features'>{{ $t('Feature List') }}</router-link>
        </a-menu-item>
        <a-menu-item key="9">
          <router-link to='/featureviews'>{{ $t('Feature View List') }}</router-link>
        </a-menu-item>
        <a-menu-item key="10">
          <router-link to='/featureservices'>{{ $t('Feature Services') }}</router-link>
        </a-menu-item>
      </a-sub-menu>

      <!--
      <a-sub-menu key="others">
        <template #title>{{ $t('Others') }}</template>
          <a-menu-item key="1000">
          <router-link to='/sql'>{{ $t('SQL') }}</router-link>
        </a-menu-item>
        <a-menu-item key="1001">
          <router-link to='/tutorial'>{{ $t('Tutorial') }}</router-link>
        </a-menu-item>
      </a-sub-menu>
      -->

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
