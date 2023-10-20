<template>
<div>

  <br/>
  <h1>{{ $t('Offline Sample') }}: {{ data.jobId }} </h1>
  <a-descriptions bordered>
    <a-descriptions-item :span="24" :label="$t('Job ID')"> {{ data.jobId }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Feature List')">{{ data.featureList }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Format')"> {{ data.format }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Path')"> {{ data.path }}</a-descriptions-item>
    <a-descriptions-item :span="24" :label="$t('Options')"> {{ data.options }}</a-descriptions-item>
</a-descriptions>


  <OfflineJobDetail :id=id></OfflineJobDetail>

</div>
</template>

<script>
import axios from 'axios';
import { message } from 'ant-design-vue';
import { ref, onMounted } from 'vue';
import OfflineJobDetail from '@/components/offlinejob/OfflineJobDetail.vue';


export default {
    components: {
        OfflineJobDetail
    },
    props: {
        id: {
            type: Number,
            required: true,
        },
    },
    data() {
        return {
            data: "",
        };
    },
    methods: {
        initData() {
            axios
                .get(`/api/offlinesamples/${this.id}`)
                .then((response) => {
                this.data = response.data;
            })
                .catch((error) => {
                message.error(error.message);
            })
                .finally(() => {
                // You can perform any additional logic here after the request completes.
            });
        },
    },
    mounted() {
        this.initData();
    }
    
};
</script>