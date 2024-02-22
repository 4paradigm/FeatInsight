<script setup lang="ts">
import { ref, defineExpose } from 'vue';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import axios from 'axios';
import { notification } from 'ant-design-vue';
import { useUserStore } from '../../stores/user';

const router = useRouter();
const { t } = useI18n();

const formState = ref({
  username: '',
  password: '',
});

const submitForm = () => {
  axios
    .post(`/api/login`, {
      username: formState.value.username,
      password: formState.value.password,
    })
    .then(() => {
      notification['success']({
        message: t('Execute Success'),
        description: `Success to login`,
      });

      // TODO: use token instead of username
      const userStore = useUserStore();
      userStore.login(
        { name: formState.value.username },
        formState.value.username
      );

      router.push('/bigscreen');
    })
    .catch((error) => {
      var errorMessage = error.message;
      if (error.response && error.response.data) {
        errorMessage = error.response.data;
      }
      notification['error']({
        message: t('Execute Fail'),
        description: errorMessage,
      });
    });
};

// 组合式 api 需要手动 expose
defineExpose({
  submitForm,
});
</script>

<template>
  <div>
    <div>
      <a-form :model="formState" layout="vertical" @submit="submitForm">
        <a-form-item
          :label="t('Username')"
          :rules="[{ required: true, message: 'Please input database name!' }]"
        >
          <a-input v-model:value="formState.username" />
        </a-form-item>

        <a-form-item
          :label="t('Password')"
          :rules="[{ required: true, message: 'Please input password!' }]"
        >
          <a-input-password v-model:value="formState.password" />
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>
