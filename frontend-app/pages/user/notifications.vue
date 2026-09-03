<template>
  <view class="container">
    <view class="header">
      <view class="filter-bar">
        <view 
          v-for="item in filterOptions" 
          :key="item.value"
          :class="['filter-item', { active: currentFilter === item.value }]"
          @click="setFilter(item.value)"
        >
          {{ item.label }}
        </view>
      </view>
    </view>
    
    <scroll-view 
      class="notification-list" 
      scroll-y 
      :refresher-enabled="true"
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onRefresh"
      @scrolltolower="loadMore"
    >
      <view v-if="notifications.length === 0" class="empty-state">
        <!-- <image class="empty-icon" src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=empty%20notification%20bell%20icon%20minimal&image_size=square" mode="aspectFit"></image> -->
        <text class="empty-text">暂无通知</text>
      </view>
      
      <view v-for="notification in notifications" :key="notification.id" class="notification-item" @click="viewDetail(notification)">
        <view class="notification-icon" :class="getTypeClass(notification.type)">
          <text>{{ getTypeIcon(notification.type) }}</text>
        </view>
        <view class="notification-content">
          <view class="notification-header">
            <text class="notification-type">{{ notification.type }}</text>
            <text class="notification-time">{{ formatTime(notification.sendTime) }}</text>
          </view>
          <text class="notification-text">{{ notification.content }}</text>
        </view>
        <view class="notification-arrow">＞</view>
      </view>
      
      <view v-if="loading" class="loading-more">
        <text>加载中...</text>
      </view>
      <view v-if="!loading && !hasMore" class="no-more">
        <text>已加载全部</text>
      </view>
    </scroll-view>
    
    <view v-if="showDetail" class="modal-overlay" @click="closeDetail">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">通知详情</text>
          <view class="modal-close" @click="closeDetail">×</view>
        </view>
        <view class="modal-body" v-if="currentNotification">
          <view class="detail-section">
            <text class="detail-label">通知类型</text>
            <text class="detail-value">{{ currentNotification.type }}</text>
          </view>
          <view class="detail-section">
            <text class="detail-label">发送时间</text>
            <text class="detail-value">{{ formatTime(currentNotification.sendTime) }}</text>
          </view>
          <view class="detail-section">
            <text class="detail-label">通知内容</text>
            <text class="detail-content">{{ currentNotification.content }}</text>
          </view>
          <view class="detail-section">
            <text class="detail-label">状态</text>
            <text :class="['detail-value', getStatusClass(currentNotification.status)]">{{ currentNotification.status }}</text>
          </view>
        </view>
        <view class="modal-footer">
          <view class="btn btn-primary" @click="closeDetail">关闭</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import api from '../../utils/api.js'

const filterOptions = [
  { label: '全部', value: '' },
  { label: '订单状态变更', value: '订单状态变更' },
  { label: '系统通知', value: '系统通知' },
  { label: '优惠活动', value: '优惠活动' }
]

const currentFilter = ref('')
const notifications = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const hasMore = ref(true)
const loading = ref(false)
const isRefreshing = ref(false)
const showDetail = ref(false)
const currentNotification = ref(null)

const getTypeIcon = (type) => {
  const icons = {
    '订单状态变更': '◆',
    '系统通知': '●',
    '优惠活动': '★'
  }
  return icons[type] || '◎'
}

const getTypeClass = (type) => {
  const classes = {
    '订单状态变更': 'order',
    '系统通知': 'system',
    '优惠活动': 'promotion'
  }
  return classes[type] || ''
}

const getStatusClass = (status) => {
  const classes = {
    '已送达': 'delivered',
    '未送达': 'undelivered'
  }
  return classes[status] || ''
}

const formatTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

const fetchNotifications = async (page = 1, refresh = false) => {
  if (loading.value) return
  
  loading.value = true
  
  try {
    const res = await api.notification.list({
      page: page,
      pageSize: pageSize.value,
      type: currentFilter.value
    })
    
    if (res && res.code === 200) {
      const data = res.data
      if (refresh) {
        notifications.value = data.list || []
      } else {
        notifications.value = [...notifications.value, ...(data.list || [])]
      }
      hasMore.value = page < data.totalPages
      currentPage.value = page
    }
  } catch (error) {
    console.error('获取通知失败', error)
    uni.showToast({ title: '获取通知失败', icon: 'none' })
  } finally {
    loading.value = false
    isRefreshing.value = false
  }
}

const setFilter = (value) => {
  currentFilter.value = value
  currentPage.value = 1
  hasMore.value = true
  notifications.value = []
  fetchNotifications(1)
}

const onRefresh = () => {
  isRefreshing.value = true
  currentPage.value = 1
  hasMore.value = true
  notifications.value = []
  fetchNotifications(1, true)
}

const loadMore = () => {
  if (hasMore.value && !loading.value) {
    fetchNotifications(currentPage.value + 1)
  }
}

const viewDetail = (notification) => {
  currentNotification.value = notification
  showDetail.value = true
}

const closeDetail = () => {
  showDetail.value = false
  currentNotification.value = null
}

onMounted(() => {
  fetchNotifications(1)
})
</script>

<style scoped>
.container {
  background-color: #f5f5f5;
  min-height: 100vh;
}

.header {
  background-color: #fff;
  padding: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.filter-bar {
  display: flex;
  gap: 20rpx;
}

.filter-item {
  padding: 16rpx 32rpx;
  background-color: #f5f5f5;
  border-radius: 30rpx;
  font-size: 26rpx;
  color: #666;
  transition: all 0.3s;
  white-space: nowrap;
}

.filter-item.active {
  background-color: #ff4444;
  color: #fff;
}

.notification-list {
  height: calc(100vh - 180rpx);
  padding: 20rpx;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 40rpx;
}

.empty-icon {
  width: 160rpx;
  height: 160rpx;
  opacity: 0.5;
  margin-bottom: 30rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

.notification-item {
  display: flex;
  align-items: center;
  background-color: #fff;
  border-radius: 16rpx;
  padding: 28rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.notification-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36rpx;
  margin-right: 24rpx;
  background-color: #fff3e0;
}

.notification-icon.order {
  background-color: #e3f2fd;
}

.notification-icon.system {
  background-color: #f3e5f5;
}

.notification-icon.promotion {
  background-color: #e8f5e9;
}

.notification-content {
  flex: 1;
  overflow: hidden;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.notification-type {
  font-size: 26rpx;
  font-weight: 600;
  color: #333;
}

.notification-time {
  font-size: 22rpx;
  color: #999;
}

.notification-text {
  font-size: 26rpx;
  color: #666;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notification-arrow {
  font-size: 32rpx;
  color: #ccc;
  margin-left: 16rpx;
}

.loading-more, .no-more {
  text-align: center;
  padding: 30rpx;
  font-size: 24rpx;
  color: #999;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  width: 85%;
  background-color: #fff;
  border-radius: 20rpx;
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.modal-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
}

.modal-close {
  font-size: 48rpx;
  color: #999;
  line-height: 1;
}

.modal-body {
  padding: 30rpx;
}

.detail-section {
  margin-bottom: 24rpx;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.detail-label {
  font-size: 26rpx;
  color: #999;
  margin-bottom: 10rpx;
  display: block;
}

.detail-value {
  font-size: 28rpx;
  color: #333;
}

.detail-value.delivered {
  color: #4caf50;
}

.detail-value.undelivered {
  color: #ff9800;
}

.detail-content {
  font-size: 28rpx;
  color: #333;
  line-height: 1.6;
  white-space: pre-wrap;
}

.modal-footer {
  padding: 24rpx 30rpx;
  border-top: 1rpx solid #f0f0f0;
}

.btn {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  text-align: center;
  border-radius: 44rpx;
  font-size: 30rpx;
}

.btn-primary {
  background-color: #ff4444;
  color: #fff;
}
</style>