<template>
<div>

  <br/>
  <h1>{{ $t('All Databases') }}</h1>
  <br/>
  <DatabasesTable></DatabasesTable>

  <h1>{{ $t('All Tables') }}</h1>
  <br/>
  <OnlineTables></OnlineTables>

</div>
</template>
  
<script>
import DatabasesTable from '@/components/database/DatabasesTable.vue';
import OnlineTables from '@/components/table/OnlineTables.vue';

export default {
  components: {
    DatabasesTable,
    OnlineTables
  }
};
</script>