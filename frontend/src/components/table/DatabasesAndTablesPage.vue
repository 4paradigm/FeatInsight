<template>
<div>

  <br/>
  <h1>
    {{ $t('All Databases') }}<router-link to='/developprocess/import/databases'><a-button type="link"><PlusCircleOutlined /></a-button></router-link>
  </h1>
  <DatabasesTable></DatabasesTable>

  

  <h1>{{ $t('All Tables') }}<router-link to='/developprocess/import/tables'><a-button type="link"><PlusCircleOutlined /></a-button></router-link></h1>
  <OnlineTables></OnlineTables>

</div>
</template>
  
<script>
import DatabasesTable from '@/components/database/DatabasesTable.vue';
import OnlineTables from '@/components/table/OnlineTables.vue';
import { PlusCircleOutlined } from '@ant-design/icons-vue';

export default {
  components: {
    DatabasesTable,
    OnlineTables,
    PlusCircleOutlined
  }
};
</script>