<template>
  <view class="container">
    <view class="menu-section">
      <view class="menu-item">
        <text class="menu-text">消息通知</text>
        <switch :checked="settings.notification" @change="toggleNotification"></switch>
      </view>
      <view class="menu-item">
        <text class="menu-text">位置服务</text>
        <switch :checked="settings.location" @change="toggleLocation"></switch>
      </view>
      <view class="menu-item">
        <text class="menu-text">深色模式</text>
        <switch :checked="settings.darkMode" @change="toggleDarkMode"></switch>
      </view>
    </view>
    
    <view class="menu-section">
      <view class="menu-item" @click="clearCache">
        <text class="menu-text">清除缓存</text>
        <text class="menu-value">{{ cacheSize }}</text>
      </view>
      <view class="menu-item" @click="checkUpdate">
        <text class="menu-text">检查更新</text>
        <text class="menu-value">{{ appVersion }}</text>
      </view>
      <view class="menu-item" @click="aboutUs">
        <text class="menu-text">关于我们</text>
        <text class="menu-arrow">＞</text>
      </view>
    </view>
    
    <button class="logout-btn" @click="logout">退出登录</button>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const settings = ref({
  notification: true,
  location: true,
  darkMode: false
})

const cacheSize = ref('12.3MB')
const appVersion = ref('v1.0.0')

onMounted(() => {
  const savedNotification = uni.getStorageSync('notification')
  const savedLocation = uni.getStorageSync('location')
  const savedDarkMode = uni.getStorageSync('darkMode')
  
  if (savedNotification !== '') {
    settings.value.notification = savedNotification === 'true'
  }
  if (savedLocation !== '') {
    settings.value.location = savedLocation === 'true'
  }
  if (savedDarkMode !== '') {
    settings.value.darkMode = savedDarkMode === 'true'
  }
})

const toggleNotification = (e) => {
  const value = e.detail.value
  settings.value.notification = value
  uni.setStorageSync('notification', String(value))
  uni.showToast({ title: value ? '消息通知已开启' : '消息通知已关闭', icon: 'success' })
}

const toggleLocation = (e) => {
  const value = e.detail.value
  settings.value.location = value
  uni.setStorageSync('location', String(value))
  uni.showToast({ title: value ? '位置服务已开启' : '位置服务已关闭', icon: 'success' })
}

const toggleDarkMode = (e) => {
  const value = e.detail.value
  settings.value.darkMode = value
  uni.setStorageSync('darkMode', String(value))
  uni.showToast({ title: value ? '深色模式已开启' : '深色模式已关闭', icon: 'success' })
  
  if (value) {
    document.documentElement.classList.add('dark')
    uni.setNavigationBarColor({
      frontColor: '#ffffff',
      backgroundColor: '#1a1a1a'
    })
  } else {
    document.documentElement.classList.remove('dark')
    uni.setNavigationBarColor({
      frontColor: '#000000',
      backgroundColor: '#f8f8f8'
    })
  }
  
  uni.$emit('darkModeChange', value)
}

const clearCache = () => {
  uni.showModal({
    title: '清除缓存',
    content: '确定要清除缓存吗？',
    success: (res) => {
      if (res.confirm) {
        cacheSize.value = '0MB'
        uni.showToast({ title: '缓存已清除', icon: 'success' })
      }
    }
  })
}

const checkUpdate = () => {
  uni.showToast({ title: '当前已是最新版本', icon: 'success' })
}

const aboutUs = () => {
  uni.showModal({
    title: '关于我们',
    content: '校园二手市场 v1.0.0\n让闲置物品找到新主人',
    showCancel: false
  })
}

const logout = () => {
  uni.showModal({
    title: '退出登录',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        uni.removeStorageSync('token')
        uni.removeStorageSync('user')
        uni.showToast({ title: '已退出登录', icon: 'success' })
        uni.navigateTo({
          url: '/pages/auth/login'
        })
      }
    }
  })
}
</script>

<style>
:root {
  --bg-color: #f5f5f5;
  --text-color: #333;
  --text-color-secondary: #666;
  --text-color-muted: #999;
  --card-bg: #fff;
  --border-color: #f0f0f0;
  --primary-color: #ff4444;
}

:root.dark {
  --bg-color: #1a1a1a;
  --text-color: #fff;
  --text-color-secondary: #aaa;
  --text-color-muted: #666;
  --card-bg: #2d2d2d;
  --border-color: #3d3d3d;
  --primary-color: #ff4444;
}
</style>

<style scoped>
.container {
  background-color: var(--bg-color);
  min-height: 100vh;
  padding: 20rpx;
}

.menu-section {
  background-color: var(--card-bg);
  border-radius: 10rpx;
  overflow: hidden;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.menu-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 25rpx 20rpx;
  border-bottom: 1rpx solid var(--border-color);
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-text {
  font-size: 28rpx;
  color: var(--text-color);
}

.menu-value {
  font-size: 24rpx;
  color: var(--text-color-muted);
  margin-right: 20rpx;
}

.menu-arrow {
  font-size: 24rpx;
  color: var(--text-color-muted);
}

.logout-btn {
  width: 100%;
  height: 80rpx;
  line-height: 80rpx;
  font-size: 28rpx;
  background-color: var(--card-bg);
  color: #ff4444;
  border: 1rpx solid #ff4444;
  border-radius: 40rpx;
  margin-top: 40rpx;
}

switch {
  transform: scale(0.8);
}
</style>