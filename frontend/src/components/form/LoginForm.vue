<template>
<div>

  <div>
    <a-form
      :model="formState"
      layout="vertical"
      @submit="submitForm">
 
      <a-form-item
          :label="$t('Username')"
          :rules="[{ required: true, message: 'Please input database name!' }]">
          <a-input v-model:value="formState.username" />
        </a-form-item>

        <a-form-item
          :label="$t('Password')"
          :rules="[{ required: true, message: 'Please input password!' }]">
          <a-input-password v-model:value="formState.password" />
        </a-form-item>

    </a-form>
  </div>

</div>
</template>
  
<script>
import axios from 'axios'
import { notification } from 'ant-design-vue'
import { useUserStore } from '@/stores/user';

export default {
  data() {
    return {
      formState: {
        username: '',
        password: '',
      },
    };
  },

  mounted() {
  },

  methods: {
    submitForm() {
      axios.post(`/api/login`, {
        "username": this.formState.username,
        "password": this.formState.password
      })
      .then(response => {
        notification["success"]({
              message: this.$t('Execute Success'),
              description: `Success to login`
            });

        // TODO: use token instead of username
        const userStore = useUserStore();
        userStore.login({ name: this.formState.username }, this.formState.username);
        
        this.$router.push('/bigscreen')
      })
      .catch(error => {
        var errorMessage = error.message;
        if (error.response && error.response.data) {
          errorMessage = error.response.data;
        }
        notification["error"]({
          message: this.$t('Execute Fail'),
          description: errorMessage
        });
      });
    },
  },
};
</script>