<template>
  <view class="container">
    <view class="search-bar">
      <view class="search-input">
        <!-- <text class="search-icon">🔍</text> -->
		<image class="search-icon" src="/static/搜索.png" mode=""></image>
        <input type="text" v-model="searchKeyword" placeholder="搜索商品" @input="handleSearch" />
      </view>
      <button class="filter-btn" @click="showFilter">筛选</button>
    </view>
    
    <view class="category-bar">
      <view class="category-item" v-for="category in categories" :key="category.id" @click="selectCategory(category.id)">
        <image :src="category.icon || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=category%20icon&image_size=square'" class="category-icon" :class="{ active: selectedCategory === category.id }"></image>
        <text class="category-name">{{ category.name }}</text>
      </view>
    </view>
    
    <view class="recommend-section" v-if="selectedCategory === 0 && hotGoods.length > 0">
      <text class="section-title">热门推荐</text>
      <view class="recommend-list">
        <view class="recommend-item" v-for="goods in hotGoods" :key="goods.id" @click="goToDetail(goods.id)">
          <image :src="Array.isArray(goods.images) ? goods.images[0] : goods.images" class="recommend-image"></image>
          <text class="recommend-title">{{ goods.title }}</text>
          <text class="recommend-price">¥{{ goods.price }}</text>
          <view class="recommend-meta">
            <text class="recommend-view">{{ goods.viewCount || 0 }}浏览</text>
          </view>
        </view>
      </view>
    </view>
    
    <view class="recommend-section" v-if="selectedCategory === 0 && recommendedGoods.length > 0">
      <text class="section-title">猜你喜欢</text>
      <view class="recommend-list">
        <view class="recommend-item" v-for="goods in recommendedGoods" :key="goods.id" @click="goToDetail(goods.id)">
          <image :src="Array.isArray(goods.images) ? goods.images[0] : goods.images" class="recommend-image"></image>
          <text class="recommend-title">{{ goods.title }}</text>
          <text class="recommend-price">¥{{ goods.price }}</text>
          <view class="recommend-meta">
            <text class="recommend-favorite">{{ goods.favoriteCount || 0 }}收藏</text>
          </view>
        </view>
      </view>
    </view>
    
    <view class="goods-list" v-if="!loading">
      <view class="goods-item" v-for="goods in filteredGoods" :key="goods.id" @click="goToDetail(goods.id)">
        <image :src="Array.isArray(goods.images) ? goods.images[0] : goods.images" class="goods-image"></image>
        <view class="goods-info">
          <text class="goods-title">{{ goods.title }}</text>
          <text class="goods-price">¥{{ goods.price }}</text>
          <view class="goods-meta">
            <text class="goods-campus">{{ goods.campusName || '未知校区' }}</text>
            <text class="goods-time">{{ formatTime(goods.createTime) }}</text>
          </view>
          <view class="goods-tags" v-if="Array.isArray(goods.tags) && goods.tags.length > 0">
            <text class="goods-tag" v-for="(tag, index) in goods.tags.slice(0, 2)" :key="index">{{ tag }}</text>
          </view>
        </view>
      </view>
      <view v-if="filteredGoods.length === 0" class="empty-goods">
        <image src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=empty%20state%20illustration&image_size=square" class="empty-image"></image>
        <text>暂无商品</text>
      </view>
      <view class="load-more" v-if="hasMore" @click="loadMore">
        <text class="load-more-text">加载更多</text>
      </view>
    </view>
    
    <view v-else class="loading">
      <!-- <text class="loading-icon">⌛</text> -->
	  <!-- <uni-icons class="loading-icon" type="refreshempty"></uni-icons> -->
	  <uni-load-more status="more"></uni-load-more>
      <text>加载中...</text>
    </view>
    
    <!-- 筛选弹窗 -->
    <view class="filter-popup" v-if="showFilterPopup">
      <view class="filter-content">
        <view class="filter-header">
          <text class="filter-title">筛选</text>
          <text class="filter-close" @click="showFilterPopup = false">×</text>
        </view>
        
        <view class="filter-section">
          <text class="filter-section-title">价格范围</text>
          <view class="price-range">
            <input type="number" v-model="filter.minPrice" placeholder="最低" class="price-input" />
            <text class="price-separator">-</text>
            <input type="number" v-model="filter.maxPrice" placeholder="最高" class="price-input" />
          </view>
        </view>
        
        <view class="filter-section">
          <text class="filter-section-title">商品成色</text>
          <view class="quality-options">
            <view class="quality-option" v-for="quality in qualities" :key="quality.value" :class="{ active: filter.quality === quality.value }" @click="filter.quality = quality.value">
              <text>{{ quality.label }}</text>
            </view>
          </view>
        </view>
        
        <view class="filter-section">
          <text class="filter-section-title">排序方式</text>
          <view class="sort-options">
            <view class="sort-option" :class="{ active: filter.sort === 'time' }" @click="filter.sort = 'time'">
              <text>最新发布</text>
            </view>
            <view class="sort-option" :class="{ active: filter.sort === 'price_asc' }" @click="filter.sort = 'price_asc'">
              <text>价格从低到高</text>
            </view>
            <view class="sort-option" :class="{ active: filter.sort === 'price_desc' }" @click="filter.sort = 'price_desc'">
              <text>价格从高到低</text>
            </view>
          </view>
        </view>
        
        <view class="filter-buttons">
          <button class="reset-btn" @click="resetFilter">重置</button>
          <button class="confirm-btn" @click="confirmFilter">确定</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../../utils/api.js'

const loading = ref(false)
const selectedCategory = ref(0)
const searchKeyword = ref('')
const showFilterPopup = ref(false)
const hasMore = ref(true)
const currentPage = ref(1)

// 下拉刷新相关
const refreshing = ref(false)

// 筛选条件
const filter = ref({
  minPrice: '',
  maxPrice: '',
  quality: '',
  sort: 'time'
})

// 分类和商品数据
const categories = ref([])
const goodsList = ref([])
const hotGoods = ref([])
const recommendedGoods = ref([])

// 校区数据
const campusList = ref([])

// 获取校区数据
const loadCampuses = async () => {
  try {
    const res = await api.campus.getAll()
    if (res && res.code === 200 && res.data) {
      campusList.value = res.data
    }
  } catch (error) {
    console.error('获取校区数据失败', error)
  }
}

// 根据campusId获取校区名称
const getCampusName = (campusId) => {
  const campus = campusList.value.find(c => c.id === campusId)
  return campus ? campus.name : '未知校区'
}

// 填充商品的校区名称
const fillCampusName = (goodsList) => {
  return goodsList.map(item => {
    if (!item.campusName || item.campusName === '未知校区') {
      item.campusName = getCampusName(item.campusId)
    }
    return item
  })
}

// 初始化数据
const initData = async () => {
  try {
    loading.value = true
    // 获取校区数据
    await loadCampuses()
    
    // 获取分类列表
    const categoryRes = await api.category.list()
    if (categoryRes.code === 200) {
      categories.value = [{ id: 0, name: '全部', icon: '/static/category/全部.png' }, 
	  { id: 1, name: '数码产品', icon: '/static/category/数码产品 .png' },
	  { id: 2, name: '服装鞋包', icon: '/static/category/服装鞋包.png' },
	  { id: 3, name: '图书教材', icon: '/static/category/纸质图书.png' },
	  { id: 4, name: '运动户外', icon: '/static/category/运动户外.png' },
	  { id: 5, name: '生活用品', icon: '/static/category/生活用品.png' },
	  { id: 6, name: '美妆护肤', icon: '/static/category/美妆护肤.png' },
	  { id: 7, name: '乐器设备', icon: '/static/category/乐器设备.png' },
	  { id: 8, name: '电子产品', icon: '/static/category/电子产品.png' },
	  { id: 9, name: '文具用品', icon: '/static/category/铅笔.png' },
	  { id: 10, name: '其他', icon: '/static/category/其他.png' }]
    }
    
    // 获取商品列表
    await fetchGoodsList()
    
    // 获取推荐商品
    await fetchRecommendations()
  } catch (error) {
    console.error('初始化数据失败', error)
  } finally {
    loading.value = false
  }
}

// 获取推荐商品
const fetchRecommendations = async () => {
  try {
    const user = uni.getStorageSync('user')
    const userId = user ? user.id : 1
    const res = await api.recommend.hybrid(userId, 6)
    if (res.code === 200) {
      const parsedList = res.data.map(item => {
        if (item.images && typeof item.images === 'string') {
          try {
            item.images = JSON.parse(item.images)
          } catch (e) {
            item.images = []
          }
        }
        return item
      })
      recommendedGoods.value = fillCampusName(parsedList)
    }
  } catch (error) {
    console.error('获取推荐商品失败', error)
  }
}

// 获取商品列表
const fetchGoodsList = async (isLoadMore = false) => {
  try {
    const params = {
      page: isLoadMore ? currentPage.value + 1 : 1,
      pageSize: 10
    }
    
    if (selectedCategory.value > 0) {
      params.categoryId = selectedCategory.value
    }
    
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }
    
    // 排序参数
    switch (filter.value.sort) {
      case 'price_asc':
        params.sortBy = 'price'
        params.order = 'asc'
        break
      case 'price_desc':
        params.sortBy = 'price'
        params.order = 'desc'
        break
      case 'time':
      default:
        params.sortBy = 'createTime'
        params.order = 'desc'
        break
    }
    
    const res = await api.goods.list(params)
    if (res.code === 200) {
      // 解析商品数据中的JSON字符串字段
      const parsedList = res.data.list.map(item => {
        // 解析images字段
        if (item.images && typeof item.images === 'string') {
          try {
            item.images = JSON.parse(item.images)
          } catch (e) {
            console.error('解析images字段失败', e)
            item.images = []
          }
        }
        // 解析tags字段
        if (item.tags && typeof item.tags === 'string') {
          try {
            item.tags = JSON.parse(item.tags)
          } catch (e) {
            console.error('解析tags字段失败', e)
            item.tags = []
          }
        }
        // 解析tradeMethods字段
        if (item.tradeMethods && typeof item.tradeMethods === 'string') {
          try {
            item.tradeMethods = JSON.parse(item.tradeMethods)
          } catch (e) {
            console.error('解析tradeMethods字段失败', e)
            item.tradeMethods = []
          }
        }
        return item
      })
      
      const filledList = fillCampusName(parsedList)
      
      if (isLoadMore) {
        goodsList.value = [...goodsList.value, ...filledList]
        currentPage.value++
      } else {
        goodsList.value = filledList
        currentPage.value = 1
        // 按浏览量降序排序，取前10个作为热门推荐
        const sortedByViewCount = [...filledList].sort((a, b) => {
          const viewA = a.viewCount || 0
          const viewB = b.viewCount || 0
          return viewB - viewA
        })
        hotGoods.value = sortedByViewCount.slice(0, 10)
      }
      hasMore.value = res.data.list.length === 10
    }
  } catch (error) {
    console.error('获取商品列表失败', error)
  } finally {
    loading.value = false
  }
}

const qualities = [
  { value: '1', label: '全新' },
  { value: '2', label: '九成新' },
  { value: '3', label: '八成新' },
  { value: '4', label: '七成新' },
  { value: '5', label: '六成新及以下' }
]

// 筛选后的商品列表
const filteredGoods = computed(() => {
  let result = goodsList.value
  
  // 价格范围筛选
  if (filter.value.minPrice) {
    result = result.filter(item => item.price >= parseFloat(filter.value.minPrice))
  }
  if (filter.value.maxPrice) {
    result = result.filter(item => item.price <= parseFloat(filter.value.maxPrice))
  }
  
  // 成色筛选
  if (filter.value.quality) {
    result = result.filter(item => item.quality === filter.value.quality)
  }
  
  return result
})

const selectCategory = async (categoryId) => {
  selectedCategory.value = categoryId
  loading.value = true
  await fetchGoodsList()
}

const handleSearch = async () => {
  loading.value = true
  await fetchGoodsList()
}

const showFilter = () => {
  showFilterPopup.value = true
}

const resetFilter = () => {
  filter.value = {
    minPrice: '',
    maxPrice: '',
    quality: '',
    sort: 'time'
  }
}

const confirmFilter = async () => {
  showFilterPopup.value = false
  loading.value = true
  await fetchGoodsList()
}

const loadMore = async () => {
  if (loading.value || !hasMore.value) return
  
  loading.value = true
  await fetchGoodsList(true)
}

const goToDetail = (id) => {
  uni.navigateTo({
    url: `/pages/goods/detail?id=${id}`
  })
}

const goToPublish = () => {
  uni.navigateTo({
    url: '/pages/goods/publish'
  })
}

const goToUser = () => {
  uni.navigateTo({
    url: '/pages/user/index'
  })
}

const onPullDownRefresh = async () => {
  refreshing.value = true
  try {
    await fetchGoodsList()
    uni.showToast({ title: '刷新成功', icon: 'success' })
  } catch (error) {
    uni.showToast({ title: '刷新失败', icon: 'none' })
  } finally {
    refreshing.value = false
    uni.stopPullDownRefresh()
  }
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const minutes = Math.floor(diff / (1000 * 60))
  
  if (days > 0) {
    return `${days}天前`
  } else if (hours > 0) {
    return `${hours}小时前`
  } else if (minutes > 0) {
    return `${minutes}分钟前`
  } else {
    return '刚刚'
  }
}

onMounted(() => {
  initData()
})
</script>

<style scoped>
.container {
  background-color: #f5f5f5;
  min-height: 100vh;
}

.search-bar {
  padding: 20rpx;
  background-color: #ff4444;
  display: flex;
  align-items: center;
  gap: 10rpx;
}

.search-input {
  flex: 1;
  display: flex;
  align-items: center;
  background-color: #fff;
  border-radius: 30rpx;
  padding: 0 20rpx;
  height: 60rpx;
}

.search-icon {
  width: 24rpx;
  height: 24rpx;
  font-size: 24rpx;
  margin-right: 10rpx;
  color: #999;
}

.search-input input {
  flex: 1;
  font-size: 24rpx;
  height: 100%;
}

.filter-btn {
  width: 100rpx;
  height: 60rpx;
  line-height: 60rpx;
  font-size: 24rpx;
  background-color: #fff;
  color: #333;
  border: none;
  border-radius: 30rpx;
  text-align: center;
}

.recommend-section {
  background-color: #fff;
  padding: 20rpx;
  margin-bottom: 10rpx;
}

.section-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
}

.recommend-list {
  display: flex;
  gap: 15rpx;
  overflow-x: auto;
  padding-bottom: 10rpx;
  scrollbar-width: none;
}

.recommend-item {
  width: 200rpx;
  flex-shrink: 0;
}

.recommend-image {
  width: 200rpx;
  height: 200rpx;
  border-radius: 10rpx;
  margin-bottom: 10rpx;
}

.recommend-title {
  font-size: 24rpx;
  color: #333;
  margin-bottom: 10rpx;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.recommend-price {
  font-size: 26rpx;
  color: #ff4444;
  font-weight: bold;
  margin-bottom: 5rpx;
}

.recommend-meta {
  font-size: 18rpx;
  color: #999;
}

.load-more {
  text-align: center;
  padding: 30rpx;
  font-size: 24rpx;
  color: #999;
}

.load-more-text {
  display: inline-block;
  padding: 10rpx 20rpx;
  background-color: #f0f0f0;
  border-radius: 20rpx;
}

.goods-tags {
  display: flex;
  gap: 10rpx;
  margin-top: 10rpx;
}

.goods-tag {
  font-size: 18rpx;
  color: #ff4444;
  background-color: #fff0f0;
  padding: 2rpx 10rpx;
  border-radius: 10rpx;
}

.empty-image {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 20rpx;
  opacity: 0.5;
}

.filter-popup {
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

.filter-content {
  background-color: #fff;
  border-top-left-radius: 20rpx;
  border-top-right-radius: 20rpx;
  width: 100%;
  max-height: 80vh;
  overflow-y: auto;
  padding-bottom: 30rpx;
}

.filter-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.filter-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
}

.filter-close {
  font-size: 36rpx;
  color: #999;
}

.filter-section {
  padding: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.filter-section-title {
  font-size: 26rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 15rpx;
}

.price-range {
  display: flex;
  align-items: center;
  gap: 10rpx;
}

.price-input {
  flex: 1;
  height: 60rpx;
  border: 1rpx solid #e0e0e0;
  border-radius: 5rpx;
  padding: 0 20rpx;
  font-size: 24rpx;
}

.price-separator {
  font-size: 24rpx;
  color: #999;
}

.quality-options {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
}

.quality-option {
  padding: 10rpx 20rpx;
  border: 1rpx solid #e0e0e0;
  border-radius: 20rpx;
  font-size: 24rpx;
  color: #666;
}

.quality-option.active {
  background-color: #ff4444;
  color: #fff;
  border-color: #ff4444;
}

.sort-options {
  display: flex;
  flex-direction: column;
  gap: 10rpx;
}

.sort-option {
  padding: 15rpx;
  border: 1rpx solid #e0e0e0;
  border-radius: 5rpx;
  font-size: 24rpx;
  color: #666;
}

.sort-option.active {
  background-color: #ff4444;
  color: #fff;
  border-color: #ff4444;
}

.filter-buttons {
  display: flex;
  gap: 20rpx;
  padding: 20rpx;
  margin-top: 10rpx;
}

.reset-btn {
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

.category-bar {
  display: flex;
  padding: 20rpx;
  background-color: #fff;
  margin-bottom: 10rpx;
  overflow-x: auto;
  scrollbar-width: none;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-right: 30rpx;
  min-width: 80rpx;
}

.category-icon {
  width: 60rpx;
  height: 60rpx;
  border-radius: 50%;
  margin-bottom: 10rpx;
}

.category-name {
  font-size: 20rpx;
  color: #333;
}

.category-icon.active {
  /* border: 2rpx solid #ff4444; */
  transform: scale(1.1);
}

.goods-list {
  padding: 20rpx;
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

.empty-goods {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 400rpx;
  font-size: 28rpx;
  color: #999;
}

.goods-item {
  display: flex;
  background-color: #fff;
  border-radius: 10rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.goods-image {
  width: 160rpx;
  height: 160rpx;
  border-radius: 5rpx;
  margin-right: 20rpx;
}

.goods-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.goods-title {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 10rpx;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.goods-price {
  font-size: 32rpx;
  color: #ff4444;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.goods-meta {
  display: flex;
  justify-content: space-between;
  font-size: 20rpx;
  color: #999;
}

.tab-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  background-color: #fff;
  border-top: 1rpx solid #f0f0f0;
  height: 100rpx;
  align-items: center;
}

.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.tab-item.active {
  color: #ff4444;
}

.tab-icon {
  font-size: 32rpx;
  margin-bottom: 5rpx;
}

.publish-icon {
  font-size: 40rpx;
  background-color: #ff4444;
  color: #fff;
  width: 60rpx;
  height: 60rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: -20rpx;
}

.tab-text {
  font-size: 20rpx;
}
</style>
