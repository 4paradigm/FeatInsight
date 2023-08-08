<template>

<div>
  <br/>
  <h1>{{ $t('Tutorial') }}</h1>
  <p>These show the basic cases of using OpenMLDB Feature Platform for feature extration.</p>

  <a-collapse v-model:activeKey="activeKey" accordion>
    <a-collapse-panel key="1" header="Single Table And Single Feature View">
      <pre>
// Create test db and tables
client.executeSql("CREATE DATABASE IF NOT EXISTS t1v1");
client.executeSql("CREATE TABLE IF NOT EXISTS t1v1.user (name string, age int)");

// Create feature view
client.createFeatureView("t1v1_v1", "", "t1v1", "SELECT name, age + 10 AS new_age FROM user");

// Create feature service
client.createFeatureService("t1v1_s1", "t1v1_v1");

// Test feature service
HttpResponse response = client.requestFeatureService("t1v1_s1", "{\"input\": [[\"abc\", 22]]}");
System.out.println(EntityUtils.toString(response.getEntity()));
      </pre>
    </a-collapse-panel>
    <a-collapse-panel key="2" header="Multiple Tables And Single Feature View">
      <pre>
// Create test db and tables
client.executeSql("CREATE DATABASE IF NOT EXISTS t2v1");
client.executeSql("CREATE TABLE IF NOT EXISTS t2v1.user (name string, age int)");
client.executeSql("CREATE TABLE IF NOT EXISTS t2v1.trade (user_name string, amount float)");

// Create feature view
client.createFeatureView("t2v1_v1", "", "t2v1", "SELECT name, age, amount FROM user LAST JOIN trade ON user.name = trade.user_name");

// Create feature service
client.createFeatureService("t2v1_s1", "t2v1_v1");

// Insert test data for trade table
// client.executeSql("INSERT INTO t2v1.trade VALUES (\"foo\", 11.1)");

// Test feature service
HttpResponse response = client.requestFeatureService("t2v1_s1", "{\"input\": [[\"abc\", 22]]}");
System.out.println(EntityUtils.toString(response.getEntity()));
      </pre>
    </a-collapse-panel>
    <a-collapse-panel key="3" header="Single Table And Multiple Feature Views">
      <pre>
// Create test db and tables
client.executeSql("CREATE DATABASE IF NOT EXISTS t1v2");
client.executeSql("CREATE TABLE IF NOT EXISTS t1v2.user (name string, age int)");

// Create entity
client.createEntity("t1v2_name", "name");

// Create feature view
client.createFeatureView("t1v2_v1", "t1v2_name","t1v2", "SELECT name FROM user");
client.createFeatureView("t1v2_v2", "t1v2_name", "t1v2", "SELECT age + 10 AS new_age FROM user");

// Create feature service
client.createFeatureService("t1v2_s1", "t1v2_v1, t1v2_v2");

// Test feature service
HttpResponse response = client.requestFeatureService("t1v2_s1", "{\"input\": [[\"abc\", 22]]}");
System.out.println(EntityUtils.toString(response.getEntity()));
      </pre>
    </a-collapse-panel>
    <a-collapse-panel key="4" header="Multiple Tables And Multiple Feature Views">
      <pre>
// Create test db and tables
client.executeSql("CREATE DATABASE IF NOT EXISTS t2v2");
client.executeSql("CREATE TABLE IF NOT EXISTS t2v2.user (name string, age int)");
client.executeSql("CREATE TABLE IF NOT EXISTS t2v2.trade (user_name string, amount float)");

// Create entity
client.createEntity("t2v2_name", "name");

// Create feature view
client.createFeatureView("t2v2_v1", "t2v2_name", "t2v2", "SELECT age + 10 AS new_age FROM user");
client.createFeatureView("t2v2_v2", "t2v2_name", "t2v2", "SELECT name, age, amount FROM user LAST JOIN trade ON user.name = trade.user_name");

// Create feature service
client.createFeatureService("t2v2_s1", "t2v2_v1, t2v2_v2");

// Insert test data for trade table
// client.executeSql("INSERT INTO t2v2.trade VALUES (\"foo\", 11.1)");

// Test feature service
HttpResponse response = client.requestFeatureService("t2v2_s1", "{\"input\": [[\"foo\", 22]]}");
System.out.println(EntityUtils.toString(response.getEntity()));
      </pre>
    </a-collapse-panel>
    <a-collapse-panel key="5" header="Create From Existing OpenMLDB Deployment">
      <pre>
// Create test db and table
client.executeSql("CREATE DATABASE IF NOT EXISTS demo_db");
client.executeSql("CREATE TABLE IF NOT EXISTS demo_db.demo_table (c1 int, c2 int, c3 bigint)");

// Create test deployment
client.executeSql("USE demo_db");
client.executeSql("DEPLOY demo_table_deploy SELECT c1 + 10 AS c1, c2 + 100 AS c2, c3 + 1000 AS c3 FROM demo_db.demo_table");

// Create feature service
client.createFeatureServiceFromDeployment("demo_table_deploy", "demo_db", "demo_table_deploy");

// Test feature service
String demoData  = client.getFeatureServiceRequestDemoData("demo_table_deploy");
HttpResponse response = client.requestFeatureService("demo_table_deploy", demoData);
System.out.println(EntityUtils.toString(response.getEntity()));
      </pre>
    </a-collapse-panel>    
  </a-collapse>

</div>
</template>
  
<script>
import { message } from 'ant-design-vue';
import { defineComponent } from 'vue';

export default {
  components() {
    return {
    }
  }

};
</script>
