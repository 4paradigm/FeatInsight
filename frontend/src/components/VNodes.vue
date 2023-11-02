<template>

</template>

<script>
import { defineComponent, ref } from "vue";

export default {
    props: {
        vnodes: {
            type: Object,
            required: true,
        }
    },

    setup(props) {
        return () => props.vnodes;
    }
}
</script>