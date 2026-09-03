<template>
  <view class="container">
    <view v-if="loading" class="loading">
      <text class="loading-icon">⌛</text>
      <text>加载中...</text>
    </view>
    <view v-else>
      <view class="tab-bar">
        <view class="tab-item" :class="{ active: activeTab === 'all' }" @click="activeTab = 'all'">
          <text>全部</text>
        </view>
        <view class="tab-item" :class="{ active: activeTab === 'electronics' }" @click="activeTab = 'electronics'">
          <text>数码</text>
        </view>
        <view class="tab-item" :class="{ active: activeTab === 'clothing' }" @click="activeTab = 'clothing'">
          <text>服饰</text>
        </view>
        <view class="tab-item" :class="{ active: activeTab === 'books' }" @click="activeTab = 'books'">
          <text>图书</text>
        </view>
      </view>
      
      <view class="favorites-list" v-if="filteredFavorites.length > 0">
        <view class="favorite-item" v-for="(item, index) in filteredFavorites" :key="item.id">
          <image :src="getFirstImage(item.images)" class="item-image"></image>
          <view class="item-info">
            <text class="item-title">{{ item.title }}</text>
            <text class="item-price">¥{{ item.price }}</text>
            <text class="item-status" :class="{ sold: item.status === '已售' }">{{ item.status }}</text>
          </view>
          <view class="item-actions">
            <text class="action-btn" @click="goToDetail(item.id)">查看</text>
            <text class="action-btn delete" @click="removeFavorite(item.id, index)">取消收藏</text>
          </view>
        </view>
      </view>
      <view class="empty-favorites" v-else>
        <text class="empty-text">暂无收藏商品</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../../utils/api.js'

const activeTab = ref('all')
const favorites = ref([])
const loading = ref(true)

const filteredFavorites = computed(() => {
  if (activeTab.value === 'all') {
    return favorites.value
  }
  return favorites.value.filter(item => item.category === activeTab.value)
})

const getFirstImage = (images) => {
  if (!images) return 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=product%20placeholder&image_size=square'
  if (typeof images === 'string') {
    try {
      const parsed = JSON.parse(images)
      return parsed.length > 0 ? parsed[0] : 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=product%20placeholder&image_size=square'
    } catch {
      return images
    }
  }
  return images.length > 0 ? images[0] : 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=product%20placeholder&image_size=square'
}

const goToDetail = (id) => {
  uni.navigateTo({
    url: `/pages/goods/detail?id=${id}`
  })
}

const removeFavorite = async (goodsId, index) => {
  uni.showModal({
    title: '取消收藏',
    content: '确定要取消收藏这个商品吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          const result = await api.goods.cancelFavorite(goodsId)
          if (result.code === 200) {
            favorites.value.splice(index, 1)
            uni.showToast({ title: '已取消收藏', icon: 'success' })
          } else {
            uni.showToast({ title: '取消收藏失败', icon: 'none' })
          }
        } catch (error) {
          console.error('取消收藏失败', error)
          uni.showToast({ title: '取消收藏失败', icon: 'none' })
        }
      }
    }
  })
}

const fetchFavorites = async () => {
  try {
    loading.value = true
    const res = await api.goods.getFavorites({ page: 1, pageSize: 100 })
    if (res.code === 200) {
      favorites.value = res.data.list
    }
  } catch (error) {
    console.error('获取收藏列表失败', error)
    uni.showToast({ title: '获取收藏列表失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchFavorites()
})
</script>

<style scoped>
.container {
  background-color: #f5f5f5;
  min-height: 100vh;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400rpx;
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

.favorites-list {
  padding: 20rpx;
}

.favorite-item {
  display: flex;
  background-color: #fff;
  border-radius: 10rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.item-image {
  width: 160rpx;
  height: 160rpx;
  border-radius: 10rpx;
  margin-right: 20rpx;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.item-title {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 10rpx;
  line-height: 40rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.item-price {
  font-size: 32rpx;
  font-weight: bold;
  color: #ff4444;
  margin-bottom: 10rpx;
}

.item-status {
  font-size: 20rpx;
  color: #999;
}

.item-status.sold {
  color: #ff4444;
}

.item-actions {
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  align-items: flex-end;
  margin-left: 20rpx;
}

.action-btn {
  font-size: 24rpx;
  color: #666;
  padding: 10rpx 20rpx;
  border: 1rpx solid #ddd;
  border-radius: 20rpx;
  margin-bottom: 10rpx;
}

.action-btn.delete {
  color: #ff4444;
  border-color: #ff4444;
}

.empty-favorites {
  text-align: center;
  padding: 100rpx 0;
  color: #999;
}

.empty-text {
  font-size: 28rpx;
}
</style>