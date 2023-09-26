<template>

</template>

<script>
export default {
  props: {
    nodeData: Array
  },
  inject: ['getGraph', 'getNode'],
  data() {
    return {
      nodeInfo: {
        name: '',
        desc: ''
      }
    }
  },
  created() {
    const node = this.getNode()
    const data = node.store.data.data

    if (data) {
      this.nodeInfo = data
    }
  }
}
</script>
