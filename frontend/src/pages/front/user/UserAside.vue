<template>
  <el-scrollbar>
    <el-menu :default-active="router.currentRoute.value.fullPath" class="el-menu-vertical-demo" @open="handleOpen"
      @close="handleClose" router style="border-radius: 15px">
      <el-menu-item index="/user/order">
        <el-icon>
          <Message />
        </el-icon>
        <span>我的订单</span>
      </el-menu-item>
      <el-menu-item index="/user/collection">
        <el-icon>
          <Star />
        </el-icon>
        <span>我的收藏</span>
      </el-menu-item>
      <el-menu-item index="/user/setting">
        <el-icon>
          <Setting />
        </el-icon>
        <span>我的信息</span>
      </el-menu-item>
    </el-menu>
  </el-scrollbar>
</template>

<script lang="ts" setup>
import { HomeFilled, Star, Setting, Message } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
const handleOpen = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}
const handleClose = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}

const router = useRouter()
</script>
