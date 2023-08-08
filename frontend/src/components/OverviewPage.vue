<script setup>
</script>

<template>

  <div>
    <br />
    <h1 style="display: inline-block; vertical-align: middle; font-size: 30px;">
      {{ $t('Feature Platform') }}
    </h1>

    <br/><br/>


    <a-row :gutter="16">

      <a-col :span="6">
        <router-link to='/tables'>
          <a-card :title="$t('Data Tables')" :bordered="false">
            <h1>{{ tableCount }}</h1>
          </a-card>
        </router-link>
      </a-col>

      <a-col :span="6">
        <router-link to='/featureviews'>
          <a-card :title="$t('Feature Views')" :bordered="false">
            <h1>{{ featureViewCount }}</h1>
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
        <router-link to='/featureservices'>
          <a-card :title="$t('Feature Services')" :bordered="false">
            <h1>{{ featureServiceCount }}</h1>
          </a-card>
        </router-link>
      </a-col>
    </a-row>


    <div>
    <div ref="chartContainer" style="width: 100%; height: 600px"></div>
  </div>
  
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
        featureviews: [],
        featureservices: [],
        
        tableCount: 0,
        featureCount: 0,
        featureViewCount: 0,
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

          await axios.get(`/api/featureviews`)
          .then(response => {
            this.featureviews = response.data;
            this.featureViewCount = this.featureviews.length;
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
          { name: this.$t('Feature Views'), value: this.featureViewCount },
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
