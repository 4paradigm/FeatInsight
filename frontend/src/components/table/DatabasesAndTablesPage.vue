<template>

<div>
  <h1>{{ $t('Databases') }}</h1>
  <DatabasesTable></DatabasesTable>

  <h1>{{ $t('Data Tables') }}</h1>
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