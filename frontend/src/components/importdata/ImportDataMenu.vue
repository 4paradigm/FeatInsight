<template>

<a-menu v-model:selectedKeys="current" mode="horizontal" :items="items" />

<a-menu mode="horizontal">

  <a-menu-item key="1">
    <router-link to='/developprocess/import/databases'>{{ $t('Import Databases') }}</router-link>
  </a-menu-item>

  <a-menu-item key="2">
    <router-link to='/developprocess/import/tables'>{{ $t('Import Tables')}}</router-link>
  </a-menu-item>

  <a-menu-item key="3">
    <router-link to='/developprocess/import/online'>{{ $t('Online Import')}}</router-link>
  </a-menu-item>

  <a-menu-item key="4">
    <router-link to='/developprocess/import/offline'>{{ $t('Offline Import')}}</router-link>
  </a-menu-item>

</a-menu>

<router-view></router-view>

</template>
  
<script>


export default {
  components: {
    
  },

  data() {
    return {
    };
  },

  mounted() {
  },

  methods: {
  }
}
</script>
