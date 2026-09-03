<template>
  <view class="container">
    <view class="tab-bar">
      <view class="tab-item" :class="{ active: activeTab === 'my' }" @click="handleTabChange('my')">
        <text>我的评价</text>
      </view>
      <view class="tab-item" :class="{ active: activeTab === 'seller' }" @click="handleTabChange('seller')">
        <text>收到评价</text>
      </view>
    </view>
    
    <view v-if="loading" class="loading">
      <text>加载中...</text>
    </view>
    
    <view class="comments-list" v-else-if="filteredComments.length > 0">
      <view class="comment-item" v-for="comment in filteredComments" :key="comment.id">
        <view class="comment-header">
          <image :src="comment.avatar || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20portrait&image_size=square'" class="avatar"></image>
          <view class="user-info">
            <text class="username">{{ comment.username || '匿名用户' }}</text>
            <text class="time">{{ formatTime(comment.createTime) }}</text>
          </view>
          <view class="rating">
            <text class="star" v-for="i in 5" :key="i" :class="{ active: i <= comment.rating }">{{ i <= comment.rating ? '★' : '☆' }}</text>
          </view>
        </view>
        <view class="goods-info" v-if="comment.goodsTitle">
          <image :src="getGoodsImage(comment.goodsImages)" class="goods-image"></image>
          <view class="goods-detail">
            <text class="goods-title">{{ comment.goodsTitle }}</text>
          </view>
        </view>
        <view class="comment-content">
          <text class="content">{{ comment.content }}</text>
          <view class="images" v-if="getCommentImages(comment.images).length > 0">
          <image :src="img" class="comment-image" v-for="(img, idx) in getCommentImages(comment.images)" :key="idx"></image>
        </view>
        </view>
        <view class="seller-reply" v-if="comment.reply">
          <text class="reply-label">卖家回复：</text>
          <text class="reply-content-show">{{ comment.reply }}</text>
        </view>
        <view class="comment-actions" v-if="comment.canReply">
          <text class="action-btn" @click="replyComment(comment)">回复评价</text>
        </view>
      </view>
    </view>
    <view class="empty-comments" v-else>
      <text class="empty-text">暂无评价</text>
    </view>
    
    <view class="reply-popup" v-if="showReplyPopup">
      <view class="reply-content">
        <view class="reply-header">
          <text class="reply-title">回复评价</text>
          <text class="reply-close" @click="showReplyPopup = false">×</text>
        </view>
        <textarea class="reply-textarea" v-model="replyContent" placeholder="请输入回复内容" />
        <view class="reply-buttons">
          <button class="cancel-btn" @click="showReplyPopup = false">取消</button>
          <button class="confirm-btn" @click="submitReply">提交回复</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../../utils/api.js'

const activeTab = ref('my')
const comments = ref([])
const loading = ref(true)

const showReplyPopup = ref(false)
const replyContent = ref('')
const currentComment = ref(null)

const filteredComments = computed(() => {
  return comments.value
})

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const getGoodsImage = (images) => {
  if (!images) return ''
  try {
    const imgArray = JSON.parse(images)
    if (Array.isArray(imgArray) && imgArray.length > 0) {
      const firstImg = imgArray[0]
      if (firstImg.startsWith('/')) {
        return 'http://localhost:8080' + firstImg
      }
      return firstImg
    }
    return ''
  } catch (e) {
    if (images.startsWith('/')) {
      return 'http://localhost:8080' + images
    }
    return images
  }
}

const getCommentImages = (images) => {
  if (!images) return []
  try {
    const imgArray = typeof images === 'string' ? JSON.parse(images) : images
    if (Array.isArray(imgArray)) {
      return imgArray.filter(img => img && img.trim() !== '')
    }
    return []
  } catch (e) {
    return []
  }
}

const fetchComments = async () => {
  loading.value = true
  try {
    let res
    if (activeTab.value === 'my') {
      res = await api.comment.getMyComments({ page: 1, pageSize: 50 })
    } else {
      res = await api.comment.getSellerComments({ page: 1, pageSize: 50 })
    }
    if (res && res.code === 200) {
      comments.value = res.data.list || []
    }
  } catch (error) {
    console.error('获取评价列表失败', error)
    uni.showToast({ title: '获取评价列表失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

const handleTabChange = (tab) => {
  activeTab.value = tab
  fetchComments()
}

const replyComment = (comment) => {
  currentComment.value = comment
  replyContent.value = ''
  showReplyPopup.value = true
}

const submitReply = async () => {
  if (!replyContent.value.trim()) {
    uni.showToast({ title: '请输入回复内容', icon: 'none' })
    return
  }
  
  uni.showLoading({ title: '提交中...' })
  
  try {
    const res = await api.comment.reply(currentComment.value.id, { reply: replyContent.value })
    
    if (res.code === 200) {
      uni.hideLoading()
      uni.showToast({ title: '回复成功', icon: 'success' })
      showReplyPopup.value = false
      currentComment.value.reply = replyContent.value
      replyContent.value = ''
    } else {
      uni.hideLoading()
      uni.showToast({ title: res.message || '回复失败', icon: 'none' })
    }
  } catch (error) {
    uni.hideLoading()
    console.error('回复失败', error)
    uni.showToast({ title: '回复失败', icon: 'none' })
  }
}

onMounted(() => {
  fetchComments()
})
</script>

<style scoped>
.container {
  background-color: #f5f5f5;
  min-height: 100vh;
}

.loading {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 200rpx;
  font-size: 28rpx;
  color: #999;
}

.tab-bar {
  display: flex;
  background-color: #fff;
  border-bottom: 1rpx solid #f0f0f0;
  padding: 0 20rpx;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 20rpx 0;
  font-size: 28rpx;
  color: #666;
  position: relative;
}

.tab-item.active {
  color: #ff4444;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 25%;
  width: 50%;
  height: 4rpx;
  background-color: #ff4444;
  border-radius: 2rpx;
}

.comments-list {
  padding: 20rpx;
}

.comment-item {
  background-color: #fff;
  border-radius: 10rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.comment-header {
  display: flex;
  align-items: center;
  margin-bottom: 15rpx;
}

.avatar {
  width: 60rpx;
  height: 60rpx;
  border-radius: 50%;
  margin-right: 15rpx;
}

.user-info {
  flex: 1;
}

.username {
  font-size: 26rpx;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 5rpx;
}

.time {
  font-size: 20rpx;
  color: #999;
}

.rating {
  display: flex;
}

.star {
  font-size: 24rpx;
  color: #ddd;
  margin-left: 5rpx;
}

.star.active {
  color: #ffcc00;
}

.goods-info {
  display: flex;
  align-items: center;
  background-color: #f9f9f9;
  border-radius: 10rpx;
  padding: 15rpx;
  margin-bottom: 15rpx;
}

.goods-image {
  width: 100rpx;
  height: 100rpx;
  border-radius: 8rpx;
  margin-right: 15rpx;
}

.goods-detail {
  flex: 1;
  overflow: hidden;
}

.goods-title {
  font-size: 24rpx;
  color: #666;
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.comment-content {
  margin-bottom: 15rpx;
}

.content {
  font-size: 26rpx;
  color: #333;
  line-height: 40rpx;
  margin-bottom: 15rpx;
  display: block;
}

.images {
  display: flex;
  flex-wrap: wrap;
}

.comment-image {
  width: 120rpx;
  height: 120rpx;
  border-radius: 10rpx;
  margin-right: 10rpx;
  margin-bottom: 10rpx;
}

.seller-reply {
  background-color: #f5f5f5;
  border-radius: 10rpx;
  padding: 15rpx;
  margin-bottom: 15rpx;
}

.reply-label {
  font-size: 24rpx;
  color: #666;
  font-weight: bold;
}

.reply-content {
  font-size: 24rpx;
  color: #333;
  line-height: 35rpx;
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
  border-top: 1rpx solid #f0f0f0;
  padding-top: 15rpx;
}

.action-btn {
  font-size: 24rpx;
  color: #ff4444;
  padding: 10rpx 20rpx;
  border: 1rpx solid #ff4444;
  border-radius: 20rpx;
}

.empty-comments {
  text-align: center;
  padding: 100rpx 0;
  color: #999;
}

.empty-text {
  font-size: 28rpx;
}

.reply-popup {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 1000;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

.reply-content {
  background-color: #fff;
  border-top-left-radius: 20rpx;
  border-top-right-radius: 20rpx;
  width: 100%;
  padding: 30rpx;
}

.reply-content-show{
	border-top-left-radius: 20rpx;
	border-top-right-radius: 20rpx;
	width: 100%;
	padding: 30rpx;
}

.reply-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20rpx;
}

.reply-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.reply-close {
  font-size: 40rpx;
  color: #999;
}

.reply-textarea {
  width: 100%;
  height: 200rpx;
  border: 1rpx solid #e0e0e0;
  border-radius: 10rpx;
  padding: 20rpx;
  font-size: 28rpx;
  resize: none;
  box-sizing: border-box;
  margin-bottom: 20rpx;
}

.reply-buttons {
  display: flex;
  gap: 20rpx;
}

.cancel-btn {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  font-size: 28rpx;
  background-color: #f0f0f0;
  color: #333;
  border: 1rpx solid #e0e0e0;
  border-radius: 40rpx;
}

.confirm-btn {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  font-size: 28rpx;
  background-color: #ff4444;
  color: #fff;
  border: none;
  border-radius: 40rpx;
}
</style>