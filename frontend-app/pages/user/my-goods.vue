<template>
  <view class="container">
    <view class="tab-bar">
      <view class="tab-item" :class="{ active: activeTab === 0 }" @click="handleTabChange(0)">
        <text>全部</text>
      </view>
      <view class="tab-item" :class="{ active: activeTab === 1 }" @click="handleTabChange(1)">
        <text>在售</text>
      </view>
      <view class="tab-item" :class="{ active: activeTab === 2 }" @click="handleTabChange(2)">
        <text>已下架</text>
      </view>
      <view class="tab-item" :class="{ active: activeTab === 3 }" @click="handleTabChange(3)">
        <text>卖出订单</text>
      </view>
    </view>
    
    <view class="goods-list">
      <view class="goods-item" v-for="goods in filteredGoods" :key="goods.id">
        <image :src="goods.images && goods.images[0] ? goods.images[0] : 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=product%20placeholder&image_size=square'" class="goods-image" mode="aspectFill"></image>
        <view class="goods-info">
          <text class="goods-title">{{ goods.title }}</text>
          <text class="goods-price">¥{{ goods.price }}</text>
          <view class="goods-meta">
            <text class="goods-status" :class="{ 'on-sale': goods.status === '在售', 'off-sale': goods.status === '已下架' }">{{ goods.status === '在售' ? '在售' : '已下架' }}</text>
            <text class="goods-time">{{ formatTime(goods.createdAt) }}</text>
          </view>
          <view v-if="goods.pendingOrders && goods.pendingOrders > 0" class="pending-orders">
            <text class="pending-count">待发货: {{ goods.pendingOrders }}</text>
            <button class="ship-all-btn" @click="shipAllOrders(goods.id)">去发货</button>
          </view>
        </view>
        <view class="goods-actions">
          <button class="action-btn" @click="previewGoods(goods.id)">预览</button>
          <button class="action-btn edit-btn" @click="editGoods(goods.id)">编辑</button>
          <button class="action-btn" :class="{ 'off-sale': goods.status === '在售', 'on-sale': goods.status === '已下架' }" @click="toggleStatus(goods)">
            {{ goods.status === '在售' ? '下架' : '上架' }}
          </button>
        </view>
      </view>
      <view v-if="goodsList.length === 0 && !loading && activeTab !== 3" class="empty-goods">
        <text>暂无商品</text>
      </view>
      <view v-if="loading" class="loading">
        <text>加载中...</text>
      </view>
    </view>
    
    <view v-if="activeTab === 3" class="order-list">
      <view class="order-item" v-for="order in orderList" :key="order.id">
        <view class="order-header">
          <text class="order-no">订单号: {{ order.orderNo }}</text>
          <text class="order-status" :class="getStatusClass(order.statusCode)">{{ order.statusText }}</text>
        </view>
        <view class="order-goods">
          <image :src="order.goodsImage || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=product%20placeholder&image_size=square'" class="goods-image"></image>
          <view class="goods-info">
            <text class="goods-title">{{ order.goodsTitle || '未知商品' }}</text>
            <text class="buyer-name">买家: {{ order.username }}</text>
          </view>
        </view>
        <view class="order-footer">
          <text class="total-price">¥{{ order.price }}</text>
          <view class="order-actions">
            <button v-if="order.statusCode === 1" class="action-btn primary" @click="shipOrder(order.id)">去发货</button>
            <text v-if="order.statusCode === 2" class="action-text">买家待收货</text>
            <text v-if="order.statusCode === 3" class="action-text">交易完成</text>
          </view>
        </view>
      </view>
      <view v-if="orderList.length === 0 && !loading" class="empty-order">
        <text>暂无卖出订单</text>
      </view>
    </view>
    
    <button v-if="activeTab !== 3" class="add-btn" @click="goToPublish">+</button>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../../utils/api.js'

const activeTab = ref(0)
const goodsList = ref([])
const orderList = ref([])
const loading = ref(true)

const statusTextMap = {
  '待支付': '待付款',
  '待发货': '待发货',
  '待收货': '待收货',
  '已完成': '已完成',
  '已取消': '已取消',
  '已退款': '已退款'
}

const statusCodeMap = {
  '待支付': 0,
  '待发货': 1,
  '待收货': 2,
  '已完成': 3,
  '已取消': 4,
  '已退款': 5
}

const getStatusClass = (statusCode) => {
  const classMap = {
    0: 'pending',
    1: 'shipping',
    2: 'receiving',
    3: 'completed',
    4: 'cancelled',
    5: 'refunded'
  }
  return classMap[statusCode] || 'pending'
}

const filteredGoods = computed(() => {
  if (activeTab.value === 0) {
    return goodsList.value
  } else if (activeTab.value === 1) {
    return goodsList.value.filter(item => item.status === '在售')
  } else if (activeTab.value === 2) {
    return goodsList.value.filter(item => item.status === '已下架')
  }
  return []
})

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${date.getMonth() + 1}/${date.getDate()}`
}

const fetchGoodsList = async () => {
  loading.value = true
  try {
    const res = await api.goods.getMyGoods({ page: 1, pageSize: 50 })
    if (res && res.code === 200) {
      const parsedList = res.data.list.map(item => {
        if (item.images && typeof item.images === 'string') {
          try {
            item.images = JSON.parse(item.images)
          } catch (e) {
            console.error('解析images字段失败', e)
            item.images = []
          }
        }
        if (item.tags && typeof item.tags === 'string') {
          try {
            item.tags = JSON.parse(item.tags)
          } catch (e) {
            item.tags = []
          }
        }
        return item
      })
      goodsList.value = parsedList || []
      
      await fetchPendingOrdersCount()
    }
  } catch (error) {
    console.error('获取商品列表失败', error)
    uni.showToast({ title: '获取商品列表失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

const fetchPendingOrdersCount = async () => {
  for (let goods of goodsList.value) {
    try {
      const res = await api.goods.getGoodsOrders(goods.id, '待发货')
      if (res && res.code === 200) {
        goods.pendingOrders = res.data.total || 0
      }
    } catch (error) {
      console.error(`获取商品 ${goods.id} 的待发货订单失败`, error)
      goods.pendingOrders = 0
    }
  }
}

const shipAllOrders = async (goodsId) => {
  uni.showModal({
    title: '确认发货',
    content: '确定要为该商品的所有待发货订单发货吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          const ordersRes = await api.goods.getGoodsOrders(goodsId, '待发货')
          if (ordersRes && ordersRes.code === 200) {
            const orders = ordersRes.data.list
            for (let order of orders) {
              await api.order.ship(order.id)
            }
            uni.showToast({ title: '发货成功', icon: 'success' })
            await fetchGoodsList()
          }
        } catch (error) {
          console.error('发货失败', error)
          uni.showToast({ title: '发货失败', icon: 'none' })
        }
      }
    }
  })
}

const toggleStatus = async (goods) => {
  const newStatus = goods.status === '在售' ? '已下架' : '在售'
  try {
    const res = await api.goods.update(goods.id, { status: newStatus })
    if (res && res.code === 200) {
      goods.status = newStatus
      uni.showToast({
        title: newStatus === '在售' ? '上架成功' : '下架成功',
        icon: 'success'
      })
    } else {
      uni.showToast({ title: res.message || '操作失败', icon: 'none' })
    }
  } catch (error) {
    console.error('修改状态失败', error)
    uni.showToast({ title: '操作失败', icon: 'none' })
  }
}

const previewGoods = (id) => {
  uni.navigateTo({
    url: `/pages/goods/detail?id=${id}`
  })
}

const editGoods = (id) => {
  uni.setStorageSync('editGoodsId', id)
  uni.switchTab({
    url: '/pages/goods/publish'
  })
}

const handleTabChange = async (tab) => {
  activeTab.value = tab
  if (tab === 3) {
    await fetchSellerOrders()
  }
}

const fetchSellerOrders = async () => {
  loading.value = true
  try {
    const res = await api.order.sellerList({ page: 1, pageSize: 50 })
    if (res && res.code === 200) {
      orderList.value = res.data.list.map(order => ({
        ...order,
        statusText: statusTextMap[order.status] || order.status || '未知',
        statusCode: statusCodeMap[order.status] || 0
      }))
    }
  } catch (error) {
    console.error('获取卖家订单失败', error)
    uni.showToast({ title: '获取订单失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

const shipOrder = async (orderId) => {
  uni.showModal({
    title: '确认发货',
    content: '确定已将商品发出？',
    success: async (res) => {
      if (res.confirm) {
        try {
          const result = await api.order.ship(orderId)
          if (result.code === 200) {
            uni.showToast({ title: '发货成功', icon: 'success' })
            await fetchSellerOrders()
          } else {
            uni.showToast({ title: '发货失败', icon: 'none' })
          }
        } catch (error) {
          console.error('发货失败', error)
          uni.showToast({ title: '发货失败', icon: 'none' })
        }
      }
    }
  })
}

const goToPublish = () => {
  uni.switchTab({
    url: '/pages/goods/publish'
  })
}

onMounted(() => {
  fetchGoodsList()
})
</script>

<style scoped>
.container {
  background-color: #f5f5f5;
  min-height: 100vh;
  overflow-x: hidden;
}

.tab-bar {
  display: flex;
  background-color: #fff;
  border-bottom: 1rpx solid #f0f0f0;
  position: sticky;
  top: 0;
  z-index: 90;
  padding: 10rpx 20rpx;
}

.tab-item {
  flex: 1;
  height: 70rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26rpx;
  margin: 0 10rpx;
  border-radius: 35rpx;
  color: #666;
  position: relative;
}

.tab-item.active {
  color: #fff;
  background-color: #ff4444;
}

.tab-item.active::after {
  display: none;
}

.goods-list {
  padding: 20rpx;
}

.goods-item {
  background-color: #fff;
  border-radius: 10rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  position: relative;
}

.goods-image {
  width: 160rpx;
  height: 160rpx;
  border-radius: 5rpx;
  margin-right: 20rpx;
  float: left;
}

.goods-info {
  margin-left: 180rpx;
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

.goods-status {
  padding: 2rpx 10rpx;
  border-radius: 10rpx;
  font-size: 18rpx;
}

.goods-status.on-sale {
  background-color: #e6f7ee;
  color: #52c41a;
}

.goods-status.off-sale {
  background-color: #fff2e8;
  color: #fa8c16;
}

.goods-actions {
  display: flex;
  gap: 10rpx;
  margin-top: 20rpx;
  clear: both;
  border-top: 1rpx solid #f0f0f0;
  padding-top: 20rpx;
}

.action-btn {
  flex: 1;
  height: 60rpx;
  line-height: 60rpx;
  font-size: 24rpx;
  border-radius: 30rpx;
  border: 1rpx solid #e0e0e0;
  background-color: #fff;
  color: #666;
}

.action-btn.off-sale {
  background-color: #ff4444;
  color: #fff;
  border-color: #ff4444;
}

.action-btn.edit-btn {
  background-color: #1890ff;
  color: #fff;
  border-color: #1890ff;
}

.action-btn.on-sale {
  background-color: #52c41a;
  color: #fff;
  border-color: #52c41a;
}

.empty-goods {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 400rpx;
  font-size: 28rpx;
  color: #999;
}

.loading {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 200rpx;
  font-size: 28rpx;
  color: #999;
}

.order-list {
  padding: 20rpx;
}

.order-item {
  background-color: #fff;
  border-radius: 10rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.order-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15rpx;
  padding-bottom: 10rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.order-no {
  font-size: 22rpx;
  color: #666;
}

.order-status {
  font-size: 22rpx;
  font-weight: bold;
  padding: 4rpx 12rpx;
  border-radius: 15rpx;
}

.order-status.pending {
  color: #ff6b35;
  background-color: #fff5f0;
}

.order-status.shipping {
  color: #fa8c16;
  background-color: #fff7e6;
}

.order-status.receiving {
  color: #1890ff;
  background-color: #e6f7ff;
}

.order-status.completed {
  color: #52c41a;
  background-color: #f6ffed;
}

.order-status.cancelled {
  color: #999;
  background-color: #f5f5f5;
}

.order-status.refunded {
  color: #9966cc;
  background-color: #f5e6ff;
}

.order-goods {
  display: flex;
  margin-bottom: 15rpx;
}

.order-goods .goods-image {
  width: 120rpx;
  height: 120rpx;
  border-radius: 8rpx;
  margin-right: 15rpx;
}

.order-goods .goods-info {
  flex: 1;
}

.order-goods .goods-title {
  font-size: 26rpx;
  color: #333;
  margin-bottom: 8rpx;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.buyer-name {
  font-size: 22rpx;
  color: #999;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 10rpx;
  border-top: 1rpx solid #f0f0f0;
}

.total-price {
  font-size: 28rpx;
  color: #ff4444;
  font-weight: bold;
}

.order-actions {
  display: flex;
  gap: 10rpx;
}

.action-btn {
  width: 120rpx;
  height: 56rpx;
  line-height: 56rpx;
  font-size: 24rpx;
  border-radius: 28rpx;
  border: none;
}

.action-btn.primary {
  background-color: #ff4444;
  color: #fff;
}

.action-text {
  font-size: 24rpx;
  color: #999;
  padding: 10rpx 20rpx;
}

.empty-order {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 400rpx;
  font-size: 28rpx;
  color: #999;
}

.add-btn {
  position: fixed;
  bottom: 120rpx;
  right: 30rpx;
  width: 80rpx;
  height: 80rpx;
  line-height: 80rpx;
  font-size: 40rpx;
  background-color: #ff4444;
  color: #fff;
  border: none;
  border-radius: 50%;
  z-index: 150;
  box-shadow: 0 4rpx 10rpx rgba(255, 68, 68, 0.3);
}

.pending-orders {
  display: flex;
  align-items: center;
  gap: 15rpx;
  margin-top: 15rpx;
  padding: 10rpx 15rpx;
  background-color: #fff7e6;
  border-radius: 8rpx;
}

.pending-count {
  font-size: 22rpx;
  color: #fa8c16;
}

.ship-all-btn {
  font-size: 22rpx;
  color: #fff;
  background-color: #fa8c16;
  padding: 6rpx 16rpx;
  border-radius: 15rpx;
  border: none;
}
</style>