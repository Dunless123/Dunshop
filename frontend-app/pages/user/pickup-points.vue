<template>
  <view class="container">
    <view class="page-header">
      <text class="page-title">常用自提点</text>
      <text class="page-desc">选择常用自提点，下单时优先展示</text>
    </view>

    <view class="favorite-section" v-if="favoritePoints.length > 0">
      <view class="section-header">
        <text class="section-title">我的常用自提点</text>
        <text class="section-count">共 {{ favoritePoints.length }} 个</text>
      </view>
      <view class="favorite-list">
        <view class="favorite-item" v-for="point in favoritePoints" :key="point.id">
          <view class="favorite-info">
            <text class="favorite-name">{{ point.name }}</text>
            <text class="favorite-desc">{{ getCampusName(point.campusId) }} · {{ point.address }}</text>
          </view>
          <view class="favorite-action">
            <text class="remove-btn" @click="removeFavorite(point.id)">移除</text>
          </view>
        </view>
      </view>
    </view>
	
	<view class="empty-favorite" v-if="favoritePoints.length === 0">
	  <!-- <text class="empty-icon">📦</text> -->
	  <text class="empty-title">暂无常用自提点</text>
	  <text class="empty-desc">点击下方自提点右侧的「+ 添加」按钮添加</text>
	</view>

    <view class="campus-section">
      <view class="section-header">
        <text class="section-title">选择自提点</text>
        <text class="expand-all" @click="toggleAllCampuses">{{ expandedCampusList.length === campuses.length ? '收起全部' : '展开全部' }}</text>
      </view>

      <view class="campus-list">
        <view class="campus-item" v-for="campus in campuses" :key="campus.id">
          <view class="campus-header" @click="toggleCampus(campus.id)">
            <view class="campus-info">
              <text class="campus-name">{{ campus.name }}</text>
              <text class="campus-count">{{ getCampusPickupCount(campus.id) }} 个自提点</text>
            </view>
            <view class="campus-action">
              <text class="expand-icon" :class="{ expanded: isCampusExpanded(campus.id) }">▼</text>
            </view>
          </view>

          <view class="pickup-content" v-if="isCampusExpanded(campus.id)">
            <view class="pickup-list">
              <view 
                class="pickup-item" 
                v-for="pickup in getPickupPointsByCampus(campus.id)" 
                :key="pickup.id"
              >
                <view class="pickup-info">
                  <view class="pickup-header">
                    <text class="pickup-name">{{ pickup.name }}</text>
                    <view v-if="isFavorite(pickup.id)" class="favorite-tag">常用</view>
                  </view>
                  <text class="pickup-address">{{ pickup.address }}</text>
                  <text v-if="pickup.phone" class="pickup-phone">{{ pickup.phone }}</text>
                </view>
                <view class="pickup-action">
                  <button 
                    class="action-btn" 
                    :class="{ active: isFavorite(pickup.id) }"
                    @click="toggleFavorite(pickup)"
                  >
                    {{ isFavorite(pickup.id) ? '已添加' : '+ 添加' }}
                  </button>
                </view>
              </view>
            </view>

            <view v-if="getPickupPointsByCampus(campus.id).length === 0" class="empty-pickup">
              <text class="empty-text">该校区暂无自提点</text>
            </view>
          </view>
        </view>
      </view>

      <view v-if="campuses.length === 0" class="empty-campus">
        <text class="empty-icon">🏫</text>
        <text class="empty-text">暂无校区数据</text>
      </view>
    </view>

    
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../../utils/api.js'

const campuses = ref([])
const pickupPoints = ref([])
const favoritePoints = ref([])
const expandedCampusList = ref([])
const loading = ref(false)

const fetchCampuses = async () => {
  try {
    loading.value = true
    const res = await api.campus.getAll()
    if (res && res.code === 200 && res.data && res.data.length > 0) {
      campuses.value = res.data
    } else {
      campuses.value = []
    }
  } catch (error) {
    console.error('获取校区列表失败', error)
    campuses.value = []
    uni.showToast({ title: '获取校区失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

const fetchPickupPoints = async () => {
  try {
    loading.value = true
    const res = await api.pickup.getAll()
    if (res && res.code === 200 && res.data && res.data.length > 0) {
      pickupPoints.value = res.data
    } else {
      pickupPoints.value = []
    }
  } catch (error) {
    console.error('获取自提点列表失败', error)
    pickupPoints.value = []
    uni.showToast({ title: '获取自提点失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

const fetchFavorites = async () => {
  try {
    loading.value = true
    const res = await api.user.getFavoritePickupPoints()
    if (res.code === 200) {
      favoritePoints.value = res.data || []
    } else {
      favoritePoints.value = []
    }
  } catch (error) {
    console.error('获取常用自提点失败', error)
    favoritePoints.value = []
    uni.showToast({ title: '获取常用自提点失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

const getPickupPointsByCampus = (campusId) => {
  return pickupPoints.value.filter(p => String(p.campusId) === String(campusId))
}

const getCampusPickupCount = (campusId) => {
  return getPickupPointsByCampus(campusId).length
}

const getCampusName = (campusId) => {
  const campus = campuses.value.find(c => c.id === campusId)
  return campus ? campus.name : ''
}

const isFavorite = (pickupId) => {
  return favoritePoints.value.some(p => p.id === pickupId)
}

const isCampusExpanded = (campusId) => {
  return expandedCampusList.value.includes(campusId)
}

const toggleCampus = (campusId) => {
  const index = expandedCampusList.value.indexOf(campusId)
  if (index > -1) {
    expandedCampusList.value.splice(index, 1)
  } else {
    expandedCampusList.value.push(campusId)
  }
}

const toggleAllCampuses = () => {
  if (expandedCampusList.value.length === campuses.value.length) {
    expandedCampusList.value = []
  } else {
    expandedCampusList.value = campuses.value.map(c => c.id)
  }
}

const toggleFavorite = async (pickup) => {
  if (isFavorite(pickup.id)) {
    await removeFavorite(pickup.id)
  } else {
    try {
      const res = await api.user.addFavoritePickupPoint(pickup.id)
      if (res.code === 200) {
        uni.showToast({ title: '添加成功', icon: 'success' })
        favoritePoints.value.push(pickup)
      } else {
        uni.showToast({ title: res.message || '添加失败', icon: 'none' })
      }
    } catch (error) {
      console.error('添加失败', error)
      uni.showToast({ title: '添加失败', icon: 'none' })
    }
  }
}

const removeFavorite = async (pickupId) => {
  try {
    const res = await api.user.removeFavoritePickupPoint(pickupId)
    if (res.code === 200) {
      uni.showToast({ title: '已移除', icon: 'success' })
      favoritePoints.value = favoritePoints.value.filter(p => p.id !== pickupId)
    } else {
      uni.showToast({ title: '移除失败', icon: 'none' })
    }
  } catch (error) {
    console.error('移除失败', error)
    uni.showToast({ title: '移除失败', icon: 'none' })
  }
}

onMounted(() => {
  fetchCampuses()
  fetchPickupPoints()
  fetchFavorites()
})
</script>

<style scoped>
.container {
  background-color: #f5f5f5;
  min-height: 100vh;
  padding: 20rpx;
  padding-bottom: 40rpx;
}

.page-header {
  margin-bottom: 20rpx;
}

.page-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  display: block;
}

.page-desc {
  font-size: 24rpx;
  color: #999;
  margin-top: 8rpx;
}

.favorite-section {
  background-color: #fff;
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
  margin-bottom: 20rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.section-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
}

.section-count {
  font-size: 24rpx;
  color: #999;
}

.expand-all {
  font-size: 24rpx;
  color: #ff4444;
}

.favorite-list {
  padding: 10rpx 0;
}

.favorite-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx;
  border-bottom: 1rpx solid #f5f5f5;
}

.favorite-item:last-child {
  border-bottom: none;
}

.favorite-info {
  flex: 1;
}

.favorite-name {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 6rpx;
}

.favorite-desc {
  font-size: 24rpx;
  color: #999;
}

.favorite-action {
  margin-left: 20rpx;
}

.remove-btn {
  font-size: 26rpx;
  color: #ff4444;
}

.campus-section {
  background-color: #fff;
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
}

.campus-list {
  padding: 10rpx 0;
}

.campus-item {
  border-bottom: 1rpx solid #f0f0f0;
}

.campus-item:last-child {
  border-bottom: none;
}

.campus-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 28rpx 24rpx;
  background-color: #fff;
}

.campus-info {
  flex: 1;
}

.campus-name {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  display: block;
}

.campus-count {
  font-size: 22rpx;
  color: #999;
  margin-top: 4rpx;
}

.campus-action {
  margin-left: 20rpx;
}

.expand-icon {
  font-size: 20rpx;
  color: #999;
  transition: transform 0.3s ease;
}

.expand-icon.expanded {
  transform: rotate(180deg);
}

.pickup-content {
  background-color: #fafafa;
  padding: 0 24rpx 20rpx;
}

.pickup-list {
  padding-top: 10rpx;
}

.pickup-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.pickup-item:last-child {
  border-bottom: none;
}

.pickup-info {
  flex: 1;
}

.pickup-header {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 8rpx;
}

.pickup-name {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
}

.favorite-tag {
  font-size: 20rpx;
  color: #ff4444;
  background-color: #fff0f0;
  padding: 4rpx 12rpx;
  border-radius: 10rpx;
}

.pickup-address {
  font-size: 24rpx;
  color: #666;
  display: block;
  margin-bottom: 4rpx;
}

.pickup-phone {
  font-size: 22rpx;
  color: #999;
}

.pickup-action {
  margin-left: 20rpx;
}

.action-btn {
  padding: 12rpx 28rpx;
  font-size: 24rpx;
  color: #ff4444;
  border: 1rpx solid #ff4444;
  border-radius: 30rpx;
  background-color: #fff;
}

.action-btn.active {
  background-color: #ff4444;
  color: #fff;
  border: none;
}

.empty-pickup {
  padding: 30rpx 0;
  text-align: center;
}

.empty-pickup .empty-text {
  font-size: 24rpx;
  color: #999;
}

.empty-campus {
  padding: 60rpx 0;
  text-align: center;
}

.empty-campus .empty-icon {
  font-size: 60rpx;
  margin-bottom: 16rpx;
}

.empty-campus .empty-text {
  font-size: 26rpx;
  color: #999;
}

.empty-favorite {
  margin-bottom: 20rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80rpx 40rpx;
  margin-top: 40rpx;
  background-color: #fff;
  border-radius: 16rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
}

.empty-icon {
  font-size: 60rpx;
  margin-bottom: 20rpx;
}

.empty-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 10rpx;
}

.empty-desc {
  font-size: 24rpx;
  color: #999;
  text-align: center;
}
</style>