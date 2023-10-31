<template>
  <div class="right-drawer-res">
    <div class="tt">
      <span>{{ $t('Generated SQL')}}</span>
      <i class="el-icon-circle-close" title="关闭" @click="closeFn"></i>
    </div>
    <div class="wrap">
      <a-form label-width="80px">
          <a-textarea readonly v-model:value=this.outSql></a-textarea>
      </a-form>
    </div>
  </div>
</template>

<script>
export default {
  props: ['outSql'],
  setup(props){
  }
}
</script>

<style lang="scss" scoped>
.right-drawer-res {
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
