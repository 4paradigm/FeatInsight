<template>

<div>

  <br />
  <div>
    <h1>{{ $t('Offline Train Set Generation') }}</h1>
    <!-- Create form -->
    <a-form
      :model="formState"
      name="basic"
      :label-col="{ span: 8 }"
      :wrapper-col="{ span: 16 }"
      @submit="handleSubmit">
      <a-form-item 
        :label="$t('Database')"
        :rules="[{ required: true, message: 'Please input database!' }]">
        <a-select v-model:value="formState.db" @blur="loadDataTables">
          <option v-for="database in databases" :value="database">{{ database }}</option>
        </a-select>
      </a-form-item>

      <a-form-item
        :label="$t('Feature View List')"
        :rules="[{ required: true, message: 'Please input feature list!' }]">

        <a-select mode="multiple" v-model:value="formState.featureList">
            <option v-for="featureview in featureViews" :value="featureview.name">{{ featureview.name }}</option>
        </a-select>
        <br/><br/>
        <a-button type="primary"><router-link to='/features/create'>{{ $t('Create New Feature') }}</router-link></a-button>

      </a-form-item>
    </a-form>
  </div>


  <TestFeatureService></TestFeatureService>

</div>
</template>
  
<script>
import axios from 'axios'
import { message } from 'ant-design-vue';

export default {
  data() {
    return {
      featureViews: [],
      databases:[],
      tables: [],

      formState: {
        name: '',
        version: '',
        featureList: [],
        key: '',
        table: '',
        db: '',
      },

    };
  },

  mounted() {
    this.initData();
  },

  methods: {
    initData() {
      axios.get(`/api/featureviews`)
        .then(response => {
          this.featureViews = response.data;
        })
        .catch(error => {
          message.error(error.message);
        })
        .finally(() => {});
      axios.get(`/api/databases`)
        .then(response => {
          this.databases = response.data;
        })
        .catch(error => {
          message.error(error.message);
        })
        .finally(() => {});

      if (this.$route.query.featureview != null) {
        this.formState.featureList = [this.$route.query.featureview];
      }

    },

    loadDataTables(){
        axios.get(`/api/databases/${this.formState.db}/tables`)
        .then((response) => {
          this.tables_list = response.data;
        })
        .catch((error) => {
          message.error(error.message);
        })
        .finally(() => {
          // You can perform any additional logic here after the request completes.
        });
        console.log(this.tables_list)
    },

    handleSubmit() {
      axios.post(`/api/featureservices`, {
        "name": this.formState.name,
        "version": this.formState.version,
        "featureList": this.formState.featureList.join(','),
        "description": this.formState.description,
      })
      .then(response => {
        message.success(`Success to add feature service ${this.formState.name} and version ${this.formState.version}`);

        // Redirect to FeatureView detail page
        this.$router.push(`/featureservices/${this.formState.name}/${this.formState.version}`);
      })
      .catch(error => {
          if (error.response.data) {
            message.error(error.response.data);
          } else {
            message.error(error.message);
          }
      });
    },

  },
};
</script>