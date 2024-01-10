<template>

  <a-input v-model:value="searchText" :placeholder="$t('Search')" @change="handleSearch" />
  <br/><br/>

  <a-table :dataSource="searchFilteredDataSource" :columns="columns" :pagination="paginationConfig">
    <template v-slot:content="{ text }">
      <div v-html="renderMultilineString(text)"></div>
    </template>

    <template v-slot:custom="scope">
      <a-popconfirm
          title="确定复制到输入框？"
          @confirm="handleCopy(scope.record.sql)">
        <a-button type="primary">{{ $t('Copy') }}</a-button>
      </a-popconfirm>
    </template>
  </a-table>

</template>

<script>
export default {
  props: {
    defaultSearchText: {
      type: String,
      required: false
    }
  },

  data() {
    return {
      searchText: "",

      paginationConfig: {
        pageSize: 100,
      },

      columns: [
          {
            title: this.$t('Function'),
            dataIndex: 'function',
            key: 'function',
            fixed: 'left',
          },
          {
            title: this.$t('Example'),
            dataIndex: 'sql',
            key: 'sql',
            slots: { customRender: 'content' }
          },
          {
            title: this.$t('Actions'),
            key: 'actions',
            fixed: 'right',
            slots: { customRender: 'custom' },
          }
      ],

      dataSource: [
          {
            function: "查看所有数据库",
            sql: "SHOW DATABASES",
          },
          {
            function: "创建数据库",
            sql: "CREATE DATABASE db1",
          },
          {
            function: "使用数据库",
            sql: "USE db1",
          },
          {
            function: "删除数据库",
            sql: "DROP DATABASE db1",
          },
          {
            function: "查看所有组件",
            sql: "SHOW COMPONENTS",
          },
          {
            function: "查看所有变量",
            sql: "SHOW VARIABLES",
          },
          {
            function: "创建数据表",
            sql: "CREATE TABLE db1.t1 (name string, age int)",
          },
          {
            function: "查看数据库所有表",
            sql: `
            USE db1;
            SHOW TABLES;
            `,
          },
          {
            function: "创建表指定时间列和索引",
            sql: `CREATE TABLE t1 (
              col0 STRING, 
              col1 int, 
              std_time TIMESTAMP, 
              INDEX(KEY=col1, TS=std_time, TTL_TYPE=latest, TTL=1)
              )`,
          },
          {
            function: "使用 Hive 建表",
            sql: "CREATE TABLE db1.t1 LIKE HIVE 'hive://hive_db.t1'",
          },
          {
            function: "使用 Paruqet 文件建表",
            sql: "CREATE TABLE t1 LIKE PARQUET 'file://t1.parquet'",
          },
          {
            function: "删除数据表",
            sql: "DROP TABLE db1.t1",
          },
          {
            function: "插入单行数据",
            sql: "INSERT INTO t1 values(1, 2, 3.0, 4.0, 'hello')",
          },
          {
            function: "插入多行数据",
            sql: "INSERT INTO t1(COL1, COL2, COL5) values(1, 2, 'hello'), (10, 20, 'world')",
          },
          {
            function: "加载 CSV 数据",
            sql: "LOAD DATA INFILE 'data.csv' INTO TABLE t1 OPTIONS(delimiter = ',', null_value='NA')",
          },
          {
            function: "加载 Parquet 数据",
            sql: "LOAD DATA INFILE 'hdfs:///foo/bar/parquet/' INTO TABLE t1 OPTIONS(format = 'parquet')",
          },
          {
            function: "软链接 Parquet 数据",
            sql: "LOAD DATA INFILE 'hdfs:///foo/bar/parquet/' INTO TABLE t1 OPTIONS(format = 'parquet', deep_copy = false)",
          },
          {
            function: "加载 Hive 数据",
            sql: "LOAD DATA INFILE 'hive://db1.t1' INTO TABLE t1",
          },
          {
            function: "删除索引数据",
            sql: "DELETE FROM t1 WHERE col1 = 'aaaa' and ts_col = 1687145994000",
          },
          {
            function: "清空表数据",
            sql: "TRUNCATE TABLE t1",
          },
          {
            function: "查询常量",
            sql: "SELECT 100, 'foo'",
          },
          {
            function: "查询测试函数",
            sql: "SELECT substr('hello world', 3, 6)",
          },
          {
            function: "查询数据表所有数据",
            sql: "SELECT * FROM db1.t1",
          },
          {
            function: "使用 Where",
            sql: "SELECT * FROM db1.t1 WHERE id = 1",
          },
          {
            function: "使用 Limit",
            sql: "SELECT * FROM db1.t1 LIMIT 10",
          },
          {
            function: "使用 LastJoin",
            sql: "ELECT * from t1 LAST JOIN t2 ON t1.col1 = t2.col1",
          },
          {
            function: "使用 LastJoin 并指定排序列",
            sql: "SELECT * from t1 LAST JOIN t2 ORDER BY t2.std_ts ON t1.col1 = t2.col1",
          },
          {
            function: "使用 LeftJoin",
            sql: "SELECT * from t1 LEFT JOIN t2 ON t1.col1 = t2.col1",
          },
          {
            function: "使用 Window",
            sql: `SELECT 
            sum(col2) OVER w1 as w1_col2_sum 
            FROM t1
WINDOW w1 AS (PARTITION BY col1 ORDER BY col5 ROWS BETWEEN 3 PRECEDING AND CURRENT ROW)`,
          },
          {
            function: "使用匿名 Window",
            sql: `SELECT 
            id, 
            pk1, 
            col1, 
            std_ts,
sum(col1) OVER (PARTITION BY pk1 ORDER BY std_ts ROWS BETWEEN 1 PRECEDING AND CURRENT ROW) as w1_col1_sum
from t1`,
          },
          {
            function: "使用 ROWS_RANGE 类型窗口",
            sql: `SELECT 
            sum(col2) OVER w1 as w1_col2_sum 
            FROM t1
WINDOW w1 AS (PARTITION BY col1 ORDER BY col5 ROWS_RANGE BETWEEN 10s PRECEDING AND CURRENT ROW)`,
          },
          {
            function: "使用 Window Union",
            sql: `SELECT 
            col1, 
            col5, 
            sum(col2) OVER w1 as w1_col2_sum 
            FROM t1
WINDOW w1 AS (UNION t2 PARTITION BY col1 ORDER BY col5 ROWS_RANGE BETWEEN 10s PRECEDING AND CURRENT ROW)`,
          },
          {
            function: "使用多副表 Window Union",
            sql: `SELECT 
            col1, 
            col5, 
            sum(col2) OVER w1 as w1_col2_sum 
            FROM t1
WINDOW w1 AS (UNION t2, t3 PARTITION BY col1 ORDER BY col5 ROWS_RANGE BETWEEN 10s PRECEDING AND CURRENT ROW)`,
          },
          {
            function: "带筛选子查询的 Window Union",
            sql: `SELECT 
            col1, 
            col5, 
            sum(col2) OVER w1 as w1_col2_sum 
            FROM t1
WINDOW w1 AS
(UNION (select c1 as col1, c2 as col2, 0.0 as col3, 0.0 as col4, c5 as col5, "NA" as col6 from t2),
(select c1 as col1, c2 as col2, 0.0 as col3, 0.0 as col4, c5 as col5, "NA" as col6 from t3)
PARTITION BY col1 ORDER BY col5 ROWS_RANGE BETWEEN 10s PRECEDING AND CURRENT ROW)`,
          },
          {
            function: "使用 GroupBy",
            sql: "SELECT COL1, SUM(COL2), AVG(COL2) FROM t1 group by COL1",
          },
          {
            function: "使用 Having",
            sql: "SELECT COL1, SUM(COL2), AVG(COL2) FROM t1 group by COL1 HAVING SUM(COL2) > 1000",
          },
          {
            function: "使用 With",
            sql: `WITH t1 as (select col1 + 1 as id, std_ts from t1)
            select * from t1`,
          },
          {
            function: "使用嵌套 With",
            sql: `WITH q1 AS (my_query)
SELECT *
FROM
  (WITH q2 AS (SELECT * FROM q1),  # q1 resolves to my_query
        q3 AS (SELECT * FROM q1),  # q1 resolves to my_query
        q1 AS (SELECT * FROM q1),  # q1 (in the query) resolves to my_query
        q4 AS (SELECT * FROM q1)   # q1 resolves to the WITH subquery on the previous line.
    SELECT * FROM q1)              # q1 resolves to the third inner WITH subquery`,
          },
          {
            function: "指定 Spark 资源参数",
            sql: `
            SET @@spark_config = 'spark.executor.memory=2g;spark.executor.cores=2';
            SELECT * FROM db1.t1
            `,
          }, 
          {
            function: "导出数据为 CSV 文件",
            sql: "SELECT col1, col2, col3 FROM t1 INTO OUTFILE 'data.csv' OPTIONS ( delimiter = ',' )",
          },
          {
            function: "导出数据为 Parquet 文件",
            sql: "SELECT col1, col2, col3 FROM t1 INTO OUTFILE 'hdfs:///data/parquet' OPTIONS ( format = 'parquet' )",
          },
          {
            function: "导出数据到 Hive",
            sql: "SELECT col1, col2, col3 FROM t1 INTO OUTFILE 'hive://db1.t1'",
          },
          {
            function: "导出数据到在线表",
            sql: "SELECT col1, col2, col3 FROM t1 INTO OUTFILE 'openmldb://db1.t1'",
          },
          {
            function: "修改表离线路径",
            sql: "ALTER TABLE t1 ADD offline_path 'hdfs://foo/bar', DROP offline_path 'hdfs://foo/bar2'",
          }, 
          {
            function: "创建索引",
            sql: "CREATE INDEX index3 ON t5 (col3) OPTIONS (ts=ts1, ttl_type=absolute, ttl=30d)",
          },
          {
            function: "删除索引",
            sql: "DROP INDEX t5.index2",
          }, 
          {
            function: "查看所有 Deployment",
            sql: "SHOW DEPLOYMENTS",
          },
          {
            function: "查看所有自定义函数",
            sql: "SHOW FUNCTIONS",
          },
          {
            function: "查看所有任务",
            sql: "SHOW JOBS",
          },
          {
            function: "查看任务日志",
            sql: "SHOW JOBLOG 1",
          },
          {
            function: "使用数学计算函数",
            sql: `
            SELECT ABS(-32);
            SELECT ACOS(1);
            SELECT add(1, 2);
            SELECT avg(value) OVER w;
            SELECT avg_cate(value, catagory) OVER w;
            SELECT avg_cate_where(value, condition, category) OVER w;
            `,
          },
          {
            function: "使用日期相关函数",
            sql: `
            SELECT add_months('2016-08-31', 1);
            SELECT date(timestamp(1590115420000));
            SELECT date("2020-05-22");
            SELECT date_format(date(timestamp(1590115420000)),"%Y-%m-%d");
            SELECT datediff("2021-05-10", "2021-05-01");
            SELECT dayofmonth(timestamp(1590115420000));
            SELECT day(timestamp(1590115420000));
            SELECT dayofweek(timestamp(1590115420000));
            SELECT dayofyear(date("2020-05-22"));
            `,
          },
          {
            function: "使用地理相关函数",
            sql: `
            SELECT earth_distance(40, 73, 41, 74);
            `,
          },
        ],

        searchFilteredDataSource: [],
      }
    },

  mounted() {
    this.initData();
  },

  methods: {
    initData() {
      if (this.defaultSearchText) {
        this.searchText = this.defaultSearchText;
        this.handleSearch();
      } else {
        this.searchFilteredDataSource = this.dataSource;
      }
    },

    renderMultilineString(text) {
      // Handle the rendering of the multiple-line string with line breaks
      return text.replace(/\n/g, '<br>');
    },

    handleCopy(sql) {
      this.$emit('clickCopy', sql);
    },

    matchSearch(item) {
        return item.function.toString().toLowerCase().includes(this.searchText.toLowerCase()) ||
          item.sql.toString().toLowerCase().includes(this.searchText.toLowerCase());
    },

    handleSearch() {
      if (this.searchText === "") {
        this.searchFilteredDataSource = this.dataSource;
      } else {
        this.searchFilteredDataSource = this.dataSource.filter((item) => this.matchSearch(item));
      }
    },

  },

};
</script>