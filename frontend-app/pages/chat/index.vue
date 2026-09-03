<template>
  <view class="container">
    <view class="chat-header">
      <view class="back-btn" @click="goBack">
        <text>＜</text>
      </view>
      <image :src="otherUser.avatar" class="header-avatar"></image>
      <view class="chat-info">
        <text class="chat-name">{{ otherUser.username }}</text>
        <text class="chat-status">离线</text>
      </view>
      <view class="more-btn">
        <text>⋯</text>
      </view>
    </view>
    
    <scroll-view class="chat-content" scroll-y :scroll-into-view="scrollToId" scroll-with-animation>
      <view v-if="messages.length === 0" class="empty-message">
        <!-- <image src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=empty%20chat%20illustration%20minimal&image_size=square" class="empty-icon"></image> -->
        <text>开始聊天吧</text>
      </view>
      
      <view v-for="(message, index) in messages" :key="index" :id="'msg-' + index" class="message-row" :class="{ 'self-row': message.isSelf }">
        <image v-if="!message.isSelf" :src="otherUser.avatar" class="avatar" mode="aspectFill"></image>
        <view class="message-wrapper">
          <view class="message-content" :class="{ 'self-content': message.isSelf }">
            <text class="message-text">{{ message.content }}</text>
          </view>
          <text class="message-time">{{ formatTime(message.createdAt) }}</text>
        </view>
        <image v-if="message.isSelf" :src="user.avatar" class="avatar" mode="aspectFill"></image>
      </view>
    </scroll-view>
    
    <view class="chat-input-bar">
      <view class="input-wrapper">
        <input type="text" class="input" v-model="inputMessage" placeholder="输入消息..." />
      </view>
      <button class="send-btn" @click="sendMessage" :disabled="!inputMessage.trim()">
        <text>发送</text>
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, nextTick } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import api from '../../utils/api.js'

const inputMessage = ref('')
const messages = ref([])
const scrollToId = ref('')

const user = reactive({
  avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20portrait%20friendly&image_size=square'
})

const otherUser = reactive({
  id: null,
  username: '',
  avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20portrait%20professional&image_size=square'
})

const sessionId = ref(null)

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

const fetchMessages = async () => {
  if (!sessionId.value) return
  
  try {
    const res = await api.chat.getMessages(sessionId.value, 1, 50)
    
    if (res && res.code === 200) {
      const storedUser = uni.getStorageSync('user')
      const currentUserId = storedUser ? (typeof storedUser === 'object' ? storedUser.id : parseInt(storedUser.id) || 0) : 0
      messages.value = res.data.list.map(item => ({
        ...item,
        isSelf: item.senderId === currentUserId
      }))
      
      nextTick(() => {
        if (messages.value.length > 0) {
          scrollToId.value = 'msg-' + (messages.value.length - 1)
        }
      })
    }
  } catch (error) {
    console.error('获取消息失败', error)
    uni.showToast({ title: '获取消息失败', icon: 'none' })
  }
}

const sendMessage = async () => {
  if (!inputMessage.value.trim() || !otherUser.id) return
  
  const content = inputMessage.value.trim()
  const storedUser = uni.getStorageSync('user')
  const currentUserId = storedUser ? (typeof storedUser === 'object' ? storedUser.id : parseInt(storedUser.id) || 0) : 0
  
  messages.value.push({
    isSelf: true,
    content: content,
    createdAt: new Date().toISOString()
  })
  
  inputMessage.value = ''
  
  nextTick(() => {
    scrollToId.value = 'msg-' + (messages.value.length - 1)
  })
  
  try {
    const res = await api.chat.sendMessage(otherUser.id, content)
    
    if (res && res.code !== 200) {
      uni.showToast({ title: '发送失败', icon: 'none' })
    }
  } catch (error) {
    console.error('发送消息失败', error)
    uni.showToast({ title: '发送失败', icon: 'none' })
  }
}

const goBack = () => {
  uni.navigateBack()
}

onLoad((options) => {
  const storedUser = uni.getStorageSync('user')
  if (storedUser) {
    const userData = typeof storedUser === 'object' ? storedUser : JSON.parse(storedUser)
    user.avatar = userData.avatar || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20portrait%20friendly%20young&image_size=square'
  }
  
  if (options) {
    if (options.sessionId) sessionId.value = parseInt(options.sessionId)
    if (options.otherUserId) otherUser.id = parseInt(options.otherUserId)
    if (options.otherUserName) otherUser.username = decodeURIComponent(options.otherUserName)
    if (options.otherUserAvatar && options.otherUserAvatar !== 'undefined') {
      otherUser.avatar = decodeURIComponent(options.otherUserAvatar)
    }
  }
  fetchMessages()
})
</script>

<style lang="scss" scoped>
.container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f5f5;
  box-sizing: border-box;
  overflow: hidden;
}

.chat-header {
  display: flex;
  align-items: center;
  padding: 20rpx 10rpx 20rpx;
  padding-top: calc(20rpx + env(safe-area-inset-top));
  background-color: #fff;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  flex-shrink: 0;
}

.back-btn {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  color: #333;
}

.header-avatar {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  margin-left: 16rpx;
}

.chat-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-left: 20rpx;
}

.chat-name {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
}

.chat-status {
  font-size: 22rpx;
  color: #999;
  margin-top: 4rpx;
}

.more-btn {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36rpx;
  color: #666;
}

.chat-content {
  flex: 1;
  padding: 20rpx;
  box-sizing: border-box;
  overflow-y: auto;
}

.message-row {
  display: flex;
  justify-content: flex-start;
  margin-bottom: 24rpx;
  overflow: hidden;
}

.message-row.self-row {
  justify-content: flex-end;
}

.avatar {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  flex-shrink: 0;
}

.message-row:not(.self-row) .avatar {
  margin-right: 16rpx;
}

.message-row.self-row .avatar {
  margin-left: 16rpx;
}

.message-wrapper {
  display: flex;
  flex-direction: column;
  max-width: 70%;
  flex-shrink: 1;
}

.message-content {
  padding: 20rpx 24rpx;
  border-radius: 24rpx;
  background-color: #fff;
  border-bottom-left-radius: 8rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
  word-break: break-all;
  white-space: pre-wrap;
}

.message-content.self-content {
  background-color: #007AFF;
  color: #fff;
  border-bottom-right-radius: 8rpx;
  border-top-left-radius: 24rpx;
}

.message-text {
  font-size: 28rpx;
  line-height: 1.6;
  word-wrap: break-word;
}

.message-time {
  font-size: 20rpx;
  color: #999;
  margin-top: 8rpx;
}

.message-row:not(.self-row) .message-time {
  padding-left: 8rpx;
}

.message-row.self-row .message-time {
  text-align: right;
  padding-right: 8rpx;
}

.empty-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 40rpx;
  color: #999;
}

.empty-icon {
  width: 160rpx;
  height: 160rpx;
  margin-bottom: 24rpx;
  opacity: 0.6;
}

.empty-message text {
  font-size: 28rpx;
}

.chat-input-bar {
  display: flex;
  align-items: center;
  padding: 16rpx 20rpx;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
  background-color: #fff;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.input-wrapper {
  flex: 1;
  background-color: #f5f5f5;
  border-radius: 36rpx;
  padding: 0 8rpx;
}

.input {
  width: 100%;
  height: 72rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
  background: transparent;
}

.send-btn {
  width: 100rpx;
  height: 72rpx;
  margin-left: 16rpx;
  background: linear-gradient(135deg, #007AFF 0%, #0066CC 100%);
  color: #fff;
  border-radius: 36rpx;
  border: none;
  font-size: 28rpx;
  font-weight: 500;
}

.send-btn[disabled] {
  background: #ccc;
}
</style>