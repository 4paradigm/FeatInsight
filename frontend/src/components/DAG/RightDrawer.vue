<template>
  <div class="right-drawer">
    <div class="tt">
      <span>{{$t('Node Settings')}}</span>
      <i class="el-icon-circle-close" title="关闭" @click="closeFn"></i>
    </div>
    <div class="wrap">
      <a-form label-width="80px">
        <a-form-item :label="$t('Node Name')">
          <a-input id="node-name" v-model:value="nodeData.name" :placeholder="$t('Please input')+$t('Node Name')" @blur="inputBlur"></a-input>
        </a-form-item>
        <a-form-item :label="$t('Node SQL')">
          <a-textarea id="node-code" v-model:value="nodeData.desc" :placeholder="$t('Please input')+$t('Node SQL')" :rows="10" @blur="inputBlur2"></a-textarea>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    selectCell: {
      default: ''
    },
    nodeData: {
      type: Object
    },
  },
  methods: {
    // 关闭
    closeFn() {
      this.$emit('updateVisable', false)
    },
    inputBlur() {
      this.selectCell.data.name = this.nodeData.name
      this.selectCell.attr('label/text', this.selectCell.data.name)
    },
    inputBlur2() {
      this.selectCell.data.desc = this.nodeData.desc
    }
  }
}
</script>

<style lang="scss" scoped>
.right-drawer {
  max-width: 350px;
  min-width: 350px;
  height: 100%;
  background: #fff;
  position: fixed;
  top: 0;
  right: 0;
  z-index: 99999;
  border-left: 1px solid #e8eaec;
  .tt {
    border-bottom: 1px solid #e8eaec;
    box-sizing: border-box;
    padding: 14px 16px;
    color: #333;
    font-size: 16px;
    display: flex;
    justify-content: space-between;
    .el-icon-circle-close {
      font-size: 20px;
      cursor: pointer;
      &:hover {
        color: #1989fa;
      }
    }
  }
  .wrap {
    box-sizing: border-box;
    padding: 20px 10px 20px 20px;
}
}
</style>