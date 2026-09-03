<template>
  <view class="chat-list-container">
    <!-- <view class="chat-header">
      <text class="header-title">消息</text>
    </view> -->
    
    <scroll-view class="chat-list" scroll-y>
      <view 
        class="chat-item" 
        v-for="item in chatList" 
        :key="item.sessionId"
        @click="goToChat(item)"
      >
        <image :src="item.otherUserAvatar" class="avatar"></image>
        <view class="chat-info">
          <view class="chat-top">
            <text class="chat-name">{{ item.otherUserName }}</text>
            <text class="chat-time">{{ formatTime(item.updatedAt) }}</text>
          </view>
          <view class="chat-bottom">
            <text class="chat-preview">{{ item.lastMessage || '暂无消息' }}</text>
            <view v-if="item.unreadCount > 0" class="unread-badge">
              <text>{{ item.unreadCount }}</text>
            </view>
          </view>
        </view>
      </view>
      
      <view v-if="chatList.length === 0" class="empty-state">
        <text class="empty-text">暂无消息</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import api from '../../utils/api.js'

const chatList = ref([])

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`
  
  return `${date.getMonth() + 1}/${date.getDate()}`
}

const fetchChatList = async () => {
  try {
    const res = await api.chat.getSessions()
    if (res && res.code === 200) {
      chatList.value = res.data
    }
  } catch (error) {
    console.error('获取聊天列表失败', error)
  }
}

const goToChat = (item) => {
  uni.navigateTo({
    url: `/pages/chat/index?sessionId=${item.sessionId}&otherUserId=${item.otherUserId}&otherUserName=${encodeURIComponent(item.otherUserName)}&otherUserAvatar=${encodeURIComponent(item.otherUserAvatar || '')}`
  })
}

onShow(() => {
  fetchChatList()
})
</script>

<style lang="scss" scoped>
.chat-list-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  box-sizing: border-box;
  overflow-x: hidden;
}

.chat-header {
  padding: 60rpx 30rpx 20rpx;
  background-color: #fff;
}

.header-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.chat-list {
  width: 100%;
  box-sizing: border-box;
  height: calc(100vh - 80rpx);
  padding: 20rpx;
  padding-bottom: calc(140rpx + env(safe-area-inset-bottom));
  overflow-x: hidden;
}

.chat-item {
  display: flex;
  align-items: center;
  padding: 20rpx;
  background-color: #fff;
  border-radius: 16rpx;
  margin-bottom: 16rpx;
  box-sizing: border-box;
}

.avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 20rpx;
}

.chat-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.chat-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8rpx;
}

.chat-name {
  font-size: 28rpx;
  font-weight: 500;
  color: #333;
}

.chat-time {
  font-size: 22rpx;
  color: #999;
}

.chat-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-preview {
  font-size: 24rpx;
  color: #666;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.unread-badge {
  background-color: #FF4757;
  color: #fff;
  font-size: 20rpx;
  padding: 4rpx 12rpx;
  border-radius: 20rpx;
  margin-left: 12rpx;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}
</style>