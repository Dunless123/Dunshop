<template>
  <view class="container">
    <view v-if="loading" class="loading">
      <!-- <text class="loading-icon">⌛</text> -->
	  <uni-load-more status="more"></uni-load-more>
      <text>加载中...</text>
    </view>
    <view v-else>
      <swiper class="swiper" :indicator-dots="true" :autoplay="true" :interval="3000" :duration="1000">
        <swiper-item v-for="(image, index) in (Array.isArray(goods.images) ? goods.images : [goods.images])" :key="index">
          <image :src="image" class="swiper-image"></image>
        </swiper-item>
      </swiper>
      
      <view class="goods-info">
        <text class="goods-title">{{ goods.title }}</text>
        <view class="price-container">
          <text class="price">¥{{ goods.price }}</text>
          <text class="original-price" v-if="goods.originalPrice > 0">¥{{ goods.originalPrice }}</text>
        </view>
        <view class="goods-meta">
          <text>发布时间: {{ formatTime(goods.createTime) }}</text>
          <text>浏览: {{ goods.viewCount }}</text>
          <text>收藏: {{ goods.favoriteCount }}</text>
        </view>
      </view>
      
      <view class="seller-info">
        <image :src="seller.avatar" class="seller-avatar"></image>
        <view class="seller-meta">
          <text class="seller-name">{{ seller.username }}</text>
          <text class="seller-campus">{{ seller.campusName }}</text>
        </view>
        <button class="chat-btn" @click="goToChat">联系卖家</button>
      </view>
      
      <view class="goods-detail">
        <text class="section-title">商品详情</text>
        <view class="detail-container">
          <text class="detail-content">{{ goods.description }}</text>
        </view>
      </view>
      
      <!-- 商品评价 -->
      <view class="comments-section">
        <view class="section-header">
          <text class="section-title">商品评价</text>
          <text class="section-more" @click="viewAllComments">查看全部</text>
        </view>
        <view class="comment-item" v-for="(comment, index) in comments" :key="index">
          <image :src="comment.avatar || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20portrait&image_size=square'" class="comment-avatar"></image>
          <view class="comment-content">
            <view class="comment-header">
              <text class="comment-username">{{ comment.username || '用户' }}</text>
              <text class="comment-time">{{ formatTime(comment.createTime) }}</text>
            </view>
            <view class="comment-rating">
              <text class="star" v-for="i in 5" :key="i" :class="{ active: i <= comment.rating }">{{ i <= comment.rating ? '★' : '☆' }}</text>
            </view>
            <text class="comment-text">{{ comment.content }}</text>
            <view class="comment-images" v-if="getCommentImages(comment.images).length > 0">
              <image :src="img" class="comment-image" v-for="(img, idx) in getCommentImages(comment.images)" :key="idx"></image>
            </view>
            <view class="seller-reply" v-if="comment.reply">
              <text class="reply-label">卖家回复：</text>
              <text class="reply-text">{{ comment.reply }}</text>
            </view>
          </view>
        </view>
        <view v-if="comments.length === 0" class="no-comments">
          <text>暂无评价</text>
        </view>
      </view>
      
      <view class="bottom-bar">
        <button class="favorite-btn" :class="{ active: isFavorite }" @click="toggleFavorite">
          <text>{{ isFavorite ? '已收藏' : '收藏' }}</text>
        </button>
        <button v-if="!isOwnGoods" class="buy-btn" @click="buyGoods">立即购买</button>
        <text v-else class="own-goods-text">这是您发布的商品</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../../utils/api.js'

const goods = ref({
  id: 1,
  title: '',
  price: 0,
  originalPrice: 0,
  images: [],
  description: '',
  viewCount: 0,
  favoriteCount: 0,
  createTime: '',
  sellerId: 0
})

const seller = ref({
  id: 0,
  avatar: '',
  username: '',
  campusId: 0,
  campusName: ''
})

const isFavorite = ref(false)
const comments = ref([])
const loading = ref(true)
const isOwnGoods = ref(false)
const userId = ref(0)

// 获取卖家信息
const getSellerInfo = async (sellerId) => {
  try {
    const res = await api.user.getById(sellerId)
    if (res.code === 200) {
      const userData = res.data
      seller.value = {
        id: userData.id,
        avatar: userData.avatar || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20portrait&image_size=square',
        username: userData.username || '用户',
        campusId: userData.campusId || 0,
        campusName: ''
      }
      
      // 获取校区名称
      if (seller.value.campusId) {
        const campusRes = await api.campus.detail(seller.value.campusId)
        if (campusRes.code === 200) {
          seller.value.campusName = campusRes.data.name || '未知校区'
        }
      } else {
        seller.value.campusName = '未知校区'
      }
    }
  } catch (error) {
    console.error('获取卖家信息失败', error)
  }
}

// 获取商品详情
const getGoodsDetail = async (id) => {
  try {
    loading.value = true
    const res = await api.goods.detail(id)
    if (res.code === 200) {
      let goodsData = res.data
      // 解析images字段，从JSON字符串转换为数组
      if (goodsData.images && typeof goodsData.images === 'string') {
        try {
          goodsData.images = JSON.parse(goodsData.images)
        } catch (e) {
          console.error('解析images字段失败', e)
          goodsData.images = []
        }
      }
      // 解析tags字段，从JSON字符串转换为数组
      if (goodsData.tags && typeof goodsData.tags === 'string') {
        try {
          goodsData.tags = JSON.parse(goodsData.tags)
        } catch (e) {
          console.error('解析tags字段失败', e)
          goodsData.tags = []
        }
      }
      // 解析tradeMethods字段，从JSON字符串转换为数组
      if (goodsData.tradeMethods && typeof goodsData.tradeMethods === 'string') {
        try {
          goodsData.tradeMethods = JSON.parse(goodsData.tradeMethods)
        } catch (e) {
          console.error('解析tradeMethods字段失败', e)
          goodsData.tradeMethods = []
        }
      }
      goods.value = goodsData
      
      // 获取卖家信息
      await getSellerInfo(goods.value.sellerId)
      
      // 检查是否是自己的商品
      isOwnGoods.value = goods.value.sellerId === userId.value
      
      // 增加浏览量
      incrementViewCount(id)
    }
  } catch (error) {
    console.error('获取商品详情失败', error)
    uni.showToast({ title: '获取商品详情失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

// 增加浏览量
const incrementViewCount = async (goodsId) => {
  try {
    await api.goods.addView(goodsId)
    goods.value.viewCount++
  } catch (error) {
    console.error('增加浏览量失败', error)
  }
}

// 获取商品评价
const getComments = async (goodsId) => {
  try {
    const res = await api.comment.list({ goodsId, page: 1, pageSize: 5 })
    if (res.code === 200) {
      comments.value = res.data.list
    }
  } catch (error) {
    console.error('获取商品评价失败', error)
  }
}

// 检查是否已收藏
const checkFavorite = async (goodsId) => {
  try {
    const res = await api.goods.checkFavorite(goodsId)
    if (res.code === 200) {
      isFavorite.value = res.data.isFavorited
    }
  } catch (error) {
    console.error('检查收藏失败', error)
  }
}

const goToChat = async () => {
  try {
    uni.showLoading({ title: '正在打开聊天...' })
    
    const res = await api.chat.getSession(seller.value.id)
    
    if (res.code === 200) {
      uni.hideLoading()
      uni.navigateTo({
        url: `/pages/chat/index?sessionId=${res.data.sessionId}&otherUserId=${res.data.otherUserId}&otherUserName=${encodeURIComponent(res.data.otherUserName)}&otherUserAvatar=${encodeURIComponent(res.data.otherUserAvatar || '')}`
      })
    } else {
      uni.hideLoading()
      uni.showToast({ title: '创建聊天失败', icon: 'none' })
    }
  } catch (error) {
    uni.hideLoading()
    console.error('创建聊天失败', error)
    uni.showToast({ title: '创建聊天失败', icon: 'none' })
  }
}

const toggleFavorite = async () => {
  try {
    if (isFavorite.value) {
      await api.goods.cancelFavorite(goods.value.id)
      uni.showToast({ title: '取消收藏成功', icon: 'success' })
      goods.value.favoriteCount--
    } else {
      await api.goods.favorite(goods.value.id)
      uni.showToast({ title: '收藏成功', icon: 'success' })
      goods.value.favoriteCount++
    }
    isFavorite.value = !isFavorite.value
  } catch (error) {
    console.error('操作失败', error)
    uni.showToast({ title: '操作失败', icon: 'none' })
  }
}

const buyGoods = () => {
  uni.navigateTo({
    url: `/pages/goods/order-create?id=${goods.value.id}`
  })
}

const viewAllComments = () => {
  uni.navigateTo({
    url: `/pages/user/comments?goodsId=${goods.value.id}`
  })
}



// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const formattedTime = timeStr.replace('T', ' ')
  return formattedTime.substring(0, 19)
}

// 处理评论图片
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

onMounted(() => {
  // 获取当前用户ID
  const user = uni.getStorageSync('user')
  if (user && user.id) {
    userId.value = user.id
  }
  
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const goodsId = currentPage.options.id
  if (goodsId) {
    getGoodsDetail(goodsId)
    getComments(goodsId)
    checkFavorite(goodsId)
  }
})
</script>

<style scoped>
.container {
  padding-bottom: 100rpx;
}

.swiper {
  width: 100%;
  height: 500rpx;
}

.swiper-image {
  width: 100%;
  height: 100%;
}

.goods-info {
  padding: 20rpx;
  background-color: #fff;
  margin-bottom: 10rpx;
}

.goods-title {
  font-size: 32rpx;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.price-container {
  display: flex;
  align-items: baseline;
  margin-bottom: 10rpx;
}

.price {
  font-size: 40rpx;
  font-weight: bold;
  color: #ff4444;
  margin-right: 20rpx;
}

.original-price {
  font-size: 24rpx;
  color: #999;
  text-decoration: line-through;
}

.goods-meta {
  display: flex;
  font-size: 24rpx;
  color: #999;
}

.goods-meta text {
  margin-right: 20rpx;
}

.seller-info {
  display: flex;
  align-items: center;
  padding: 20rpx;
  background-color: #fff;
  margin-bottom: 10rpx;
}

.seller-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  margin-right: 20rpx;
}

.seller-meta {
  flex: 1;
}

.seller-name {
  font-size: 28rpx;
  font-weight: bold;
  margin-bottom: 5rpx;
}

.seller-campus {
  font-size: 24rpx;
  color: #999;
}

.chat-btn {
  width: 120rpx;
  height: 60rpx;
  line-height: 60rpx;
  font-size: 24rpx;
  background-color: #f0f0f0;
  color: #333;
  border: none;
  border-radius: 30rpx;
}

.goods-detail {
  padding: 20rpx;
  background-color: #fff;
  margin-bottom: 10rpx;
}

.section-title {
  font-size: 28rpx;
  font-weight: bold;
  margin-bottom: 20rpx;
  color: #333;
  padding-bottom: 10rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.detail-container {
  background-color: #f9f9f9;
  border-radius: 10rpx;
  padding: 25rpx;
  margin-top: 10rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.detail-content {
  font-size: 26rpx;
  line-height: 1.6;
  color: #333;
  white-space: pre-wrap;
}

.detail-content::first-letter {
  margin-left: 2em;
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  background-color: #fff;
  padding: 20rpx;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.1);
}

.favorite-btn {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  font-size: 28rpx;
  background-color: #f0f0f0;
  color: #333;
  border: none;
  border-radius: 40rpx;
  margin-right: 20rpx;
}

.favorite-btn.active {
  background-color: #ff4444;
  color: #fff;
}

.buy-btn {
  flex: 2;
  height: 80rpx;
  line-height: 80rpx;
  font-size: 28rpx;
  background-color: #ff4444;
  color: #fff;
  border: none;
  border-radius: 40rpx;
}

.comments-section {
  background-color: #fff;
  padding: 20rpx;
  margin-bottom: 10rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.section-more {
  font-size: 24rpx;
  color: #ff4444;
}

.comment-item {
  display: flex;
  margin-bottom: 20rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.comment-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.comment-avatar {
  width: 60rpx;
  height: 60rpx;
  border-radius: 50%;
  margin-right: 15rpx;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10rpx;
}

.comment-username {
  font-size: 26rpx;
  font-weight: bold;
  color: #333;
}

.comment-time {
  font-size: 20rpx;
  color: #999;
}

.comment-rating {
  margin-bottom: 10rpx;
}

.star {
  font-size: 24rpx;
  color: #ddd;
  margin-right: 5rpx;
}

.star.active {
  color: #ffcc00;
}

.comment-text {
  font-size: 26rpx;
  color: #333;
  line-height: 40rpx;
  margin-bottom: 15rpx;
}

.comment-images {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 15rpx;
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
}

.reply-label {
  font-size: 24rpx;
  color: #666;
  font-weight: bold;
}

.reply-text {
  font-size: 24rpx;
  color: #333;
  line-height: 35rpx;
}

.add-comment {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20rpx;
  background-color: #f5f5f5;
  border-radius: 10rpx;
  margin-top: 20rpx;
}

.add-comment-icon {
  font-size: 32rpx;
  margin-right: 10rpx;
}

.add-comment-text {
  font-size: 26rpx;
  color: #ff4444;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 600rpx;
  font-size: 28rpx;
  color: #666;
}

.loading-icon {
  font-size: 60rpx;
  margin-bottom: 20rpx;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.no-comments {
  text-align: center;
  padding: 40rpx;
  color: #999;
  font-size: 24rpx;
}

.own-goods-text {
  font-size: 26rpx;
  color: #999;
  padding: 20rpx 40rpx;
  background-color: #f5f5f5;
  border-radius: 30rpx;
}
</style>