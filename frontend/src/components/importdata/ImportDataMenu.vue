<template>

<a-menu mode="horizontal">

  <a-menu-item key="1">
    <router-link to='/developprocess/import/databases'>{{ $t('Create Database') }}</router-link>
  </a-menu-item>

  <a-menu-item key="2">
    <router-link to='/developprocess/import/tables'>{{ $t('Create Data Table')}}</router-link>
  </a-menu-item>

  <a-menu-item key="3">
    <router-link to='/developprocess/import/online'>{{ $t('Import Online Data')}}</router-link>
  </a-menu-item>

  <a-menu-item key="4">
    <router-link to='/developprocess/import/offline'>{{ $t('Import Offline Data')}}</router-link>
  </a-menu-item>

  <a-menu-item key="5">
    <router-link to='/developprocess/import/index'>{{ $t('Add Index')}}</router-link>
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
