<script setup>
</script>

<template>
<div>

  <br/>
  <a-typography>
    <a-typography-title>{{ $t('Feature Platform') }}</a-typography-title>
    <a-typography-paragraph>
      <blockquote>
        OpenMLDB 特征平台是基于特征数据库实现的 Feature Store 服务，用户使用 SQL 来定义特征，离线场景中选择特征生成离线样本，在线场景中选择特征实现实时特征计算。
      </blockquote>
    </a-typography-paragraph>
  </a-typography>

  <br/>
  <a-row :gutter="16">

    <a-col :span="6">
      <router-link to='/tables'>
        <a-card :title="$t('Data Tables')" :bordered="false">
          <h1>{{ tableCount }}</h1>
        </a-card>
      </router-link>
    </a-col>

    <a-col :span="6">
      <router-link to='/features'>
        <a-card :title="$t('Features')" :bordered="false">
          <h1>{{ featureCount }}</h1>
        </a-card>
      </router-link>
    </a-col>

    <a-col :span="6">
      <router-link to='/offlinepage'>
        <a-card :title="$t('Offline Sample')" :bordered="false">
          <h1>{{ offlineSampleCount }}</h1>
        </a-card>
      </router-link>
    </a-col>

    <a-col :span="6">
      <router-link to='/featureservices'>
        <a-card :title="$t('Feature Services')" :bordered="false">
          <h1>{{ featureServiceCount }}</h1>
        </a-card>
      </router-link>
    </a-col>
  </a-row>

  <div ref="chartContainer" style="width: 100%; height: 600px"></div>
  
</div>
</template>

<script>
import axios from 'axios'

import * as echarts from 'echarts';

export default {
    data() {
      return {
        tables: [],
        features: [],
        offlineSamples: [],
        featureservices: [],
        
        tableCount: 0,
        featureCount: 0,
        offlineSampleCount: 0,
        featureServiceCount: 0,

        chartContainer: null,

        chartData: [],

      };
    },

    mounted() {
      this.initData();

      this.chartContainer = this.$refs.chartContainer;
      //this.renderChart();

      this.fetchChartData();
    },

    methods: {

      async fetchChartData() {
        try {
          await this.initData();
          this.renderChart();
        } catch (error) {
          console.error(error);
        }
      },


      renderChart() {
        const chart = echarts.init(this.chartContainer);

        const option = {
          tooltip: {
            trigger: 'item',
            formatter: '{b}: {c} ({d}%)',
          },
          series: [
            {
              name: 'Donut Chart',
              type: 'pie',
              radius: ['50%', '70%'],
              avoidLabelOverlap: false,
              label: {
                show: false,
                position: 'center',
              },
              emphasis: {
                label: {
                  show: true,
                  fontSize: '20',
                  fontWeight: 'bold',
                },
              },
              labelLine: {
                show: false,
              },
              data: this.chartData.map((item) => ({ name: item.name, value: item.value })),
            },
          ],
        };

        chart.setOption(option);
      },


      async initData() {
        await axios.get(`/api/tables`)
          .then(response => {
            this.tables = response.data;
            this.tableCount = this.tables.length;
          })
          .catch(error => {
            console.log(error.message);
          });

          await axios.get(`/api/features`)
          .then(response => {
            this.features = response.data;
            this.featureCount = this.features.length;
          })
          .catch(error => {
            console.log(error.message);
          });

          await axios.get(`/api/offlinesamples`)
          .then(response => {
            this.offlineSamples = response.data;
            this.offlineSampleCount = this.offlineSamples.length;
          })
          .catch(error => {
            console.log(error.message);
          });

          await axios.get(`/api/featureservices`)
          .then(response => {
            this.featureservices = response.data;
            this.featureServiceCount = this.featureservices.length;
          })
          .catch(error => {
            console.log(error.message);
          });


        this.chartData = [
          { name: this.$t('Data Tables'), value: this.tableCount },
          { name: this.$t('Offline Samples'), value: this.offlineSampleCount },
          { name: this.$t('Features'), value: this.featureCount },
          { name: this.$t('Feature Services'), value: this.featureServiceCount },
        ];
      },



    },
  };
</script>

<style scoped>
.steps-content {
  margin-top: 16px;
  border: 1px dashed #e9e9e9;
  border-radius: 6px;
  background-color: #fafafa;
  min-height: 200px;
  text-align: center;
  padding-top: 80px;
}

.steps-action {
  margin-top: 24px;
}

[data-theme='dark'] .steps-content {
  background-color: #2f2f2f;
  border: 1px dashed #404040;
}
</style>
