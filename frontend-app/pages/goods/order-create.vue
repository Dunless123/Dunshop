<template>
  <view class="container">
    <view class="form-section">
      <text class="section-title">商品信息</text>
      <view class="goods-card">
        <image :src="goods.images && goods.images[0]" class="goods-image"></image>
        <view class="goods-info">
          <text class="goods-title">{{ goods.title }}</text>
          <text class="goods-price">¥{{ goods.price }}</text>
          <text class="original-price" v-if="goods.originalPrice">¥{{ goods.originalPrice }}</text>
        </view>
      </view>
    </view>

    <view class="form-section">
      <text class="section-title">交易方式</text>
      <view class="trade-options">
        <view class="trade-option" :class="{ active: tradeMethod === 'pickup' }" @click="tradeMethod = 'pickup'">
          <text class="trade-icon">📍</text>
          <text class="trade-label">自提</text>
        </view>
        <view class="trade-option" :class="{ active: tradeMethod === 'express' }" @click="tradeMethod = 'express'">
          <text class="trade-icon">📦</text>
          <text class="trade-label">快递</text>
        </view>
        <view class="trade-option" :class="{ active: tradeMethod === 'meeting' }" @click="tradeMethod = 'meeting'">
          <text class="trade-icon">🤝</text>
          <text class="trade-label">面交</text>
        </view>
      </view>
    </view>

    <view class="form-section" v-if="tradeMethod === 'pickup'">
      <view class="section-header">
        <text class="section-title">选择自提点</text>
        <text class="add-pickup-btn" @click="goToPickupPoints">+ 添加常用点</text>
      </view>
      <view class="pickup-list" v-if="pickupPoints.length > 0">
        <view 
          class="pickup-item" 
          v-for="pickup in pickupPoints" 
          :key="pickup.id"
          :class="{ active: selectedPickupPoint === pickup.id }"
          @click="selectedPickupPoint = pickup.id"
        >
          <view class="pickup-radio" :class="{ checked: selectedPickupPoint === pickup.id }">
            <view class="radio-inner" v-if="selectedPickupPoint === pickup.id"></view>
          </view>
          <view class="pickup-info">
            <text class="pickup-name">{{ pickup.name }}</text>
            <text class="pickup-address">{{ pickup.address }}</text>
          </view>
        </view>
      </view>
      <view class="empty-pickup" v-else>
        <text class="empty-text">暂无常用自提点</text>
        <button class="add-pickup-btn-inline" @click="goToPickupPoints">立即添加</button>
      </view>
    </view>

    <view class="form-section" v-if="tradeMethod === 'pickup'">
      <text class="section-title">预约时间</text>
      <view class="time-slots">
        <view 
          class="time-slot" 
          v-for="(slot, index) in timeSlots" 
          :key="index"
          :class="{ active: selectedTimeSlot === slot }"
          @click="selectedTimeSlot = slot"
        >
          <text>{{ slot }}</text>
        </view>
      </view>
    </view>

    <view class="form-section" v-if="tradeMethod === 'express'">
      <view class="section-header">
        <text class="section-title">收货地址</text>
        <text class="add-address-btn" @click="goToAddAddress">+ 添加地址</text>
      </view>
      <view class="address-list" v-if="addresses.length > 0">
        <view 
          class="address-item" 
          v-for="address in addresses" 
          :key="address.id"
          :class="{ active: selectedAddress === address.id }"
          @click="selectedAddress = address.id"
        >
          <view class="address-radio" :class="{ checked: selectedAddress === address.id }">
            <view class="radio-inner" v-if="selectedAddress === address.id"></view>
          </view>
          <view class="address-info">
            <text class="address-name">{{ address.name }} {{ address.phone }}</text>
            <text class="address-detail">{{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}</text>
          </view>
        </view>
      </view>
      <view class="empty-address" v-else>
        <text class="empty-text">暂无收货地址</text>
        <button class="add-address-btn-inline" @click="goToAddAddress">立即添加</button>
      </view>
    </view>

    <view class="form-section">
      <text class="section-title">订单备注</text>
      <textarea class="remark-input" v-model="remark" placeholder="请输入订单备注（选填）"></textarea>
    </view>

    <view class="bottom-bar">
      <view class="total-info">
        <text class="total-label">合计:</text>
        <text class="total-price">¥{{ goods.price }}</text>
      </view>
      <button class="submit-btn" @click="submitOrder">提交订单</button>
    </view>

    <view class="pay-modal" v-if="showPayModal" @click="closePayModal">
      <view class="pay-content" @click.stop>
        <view class="pay-header">
          <view class="pay-icon">💳</view>
          <text class="pay-title">支付</text>
          <text class="pay-close" @click="closePayModal">×</text>
        </view>
        
        <view class="pay-body">
          <view class="pay-amount-section">
            <text class="pay-amount-label">支付金额</text>
            <view class="pay-amount">
              <text class="amount-symbol">¥</text>
              <text class="amount-value">{{ payAmount }}</text>
            </view>
          </view>
          
          <view class="pay-method-section">
            <text class="pay-method-label">支付方式</text>
            <view class="pay-method">
              <text class="method-icon">💰</text>
              <text class="method-name">微信支付</text>
            </view>
          </view>
          
          <view class="pay-password-section">
            <text class="pay-password-label">支付密码</text>
            <view class="password-input-box">
              <view 
                class="password-input" 
                v-for="(item, index) in 6" 
                :key="index"
                :class="{ filled: payPassword.length > index }"
              >
                <text v-if="payPassword.length > index">{{ payPassword[index] }}</text>
              </view>
            </view>
          </view>
        </view>
        
        <view class="pay-footer">
          <button class="pay-submit-btn" :disabled="payPassword.length !== 6" @click="submitPay">确认支付</button>
        </view>
        
        <view class="num-pad">
          <view 
            class="num-btn" 
            v-for="num in ['1','2','3','4','5','6','7','8','9','.','0','del']" 
            :key="num"
            @click="handleNumInput(num)"
          >
            <text v-if="num !== 'del'">{{ num }}</text>
            <text v-else>⌫</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted  } from 'vue'
import {onShow} from "@dcloudio/uni-app"
import api from '../../utils/api.js'

const goods = ref({
  id: 0,
  title: '',
  price: 0,
  originalPrice: 0,
  images: [],
  sellerId: 0
})

const tradeMethod = ref('pickup')
const selectedPickupPoint = ref('')
const selectedTimeSlot = ref('')
const selectedAddress = ref('')
const remark = ref('')

const showPayModal = ref(false)
const payPassword = ref('')
const payAmount = ref(0)
const currentOrderId = ref(0)
const userId = ref(0)

const pickupPoints = ref([])

const timeSlots = [
  '09:00—10:00',
  '10:00—11:00',
  '11:00—12:00',
  '14:00—15:00',
  '15:00—16:00',
  '16:00—17:00',
  '17:00—18:00',
  '18:00—19:00',
  '19:00—20:00'
]

const addresses = ref([])

const loadGoodsDetail = async (goodsId) => {
  try {
    const res = await api.goods.detail(goodsId)
    if (res.code === 200) {
      let goodsData = res.data
      if (goodsData.images && typeof goodsData.images === 'string') {
        try {
          goodsData.images = JSON.parse(goodsData.images)
        } catch (e) {
          goodsData.images = []
        }
      }
      goods.value = goodsData
    }
  } catch (error) {
    console.error('获取商品详情失败', error)
    uni.showToast({ title: '获取商品详情失败', icon: 'none' })
  }
}

const loadAddresses = async () => {
  try {
    const res = await api.user.getAddresses()
    if (res.code === 200) {
      addresses.value = res.data
      if (addresses.value.length > 0 && !selectedAddress.value) {
        selectedAddress.value = addresses.value[0].id
      }
    }
  } catch (error) {
    console.error('获取收货地址失败', error)
  }
}

const loadPickupPoints = async () => {
  try {
    const res = await api.user.getFavoritePickupPoints()
    if (res && res.code === 200 && res.data && res.data.length > 0) {
      pickupPoints.value = res.data
      if (!selectedPickupPoint.value) {
        selectedPickupPoint.value = pickupPoints.value[0].id
      }
    } else {
      pickupPoints.value = []
    }
  } catch (error) {
    console.error('获取常用自提点失败', error)
    pickupPoints.value = []
  }
}

const goToPickupPoints = () => {
  uni.navigateTo({
    url: '/pages/user/pickup-points'
  })
}

const goToAddAddress = () => {
  uni.navigateTo({
    url: '/pages/user/address'
  })
}

const checkExistingOrder = async () => {
  try {
    const res = await api.order.list({ page: 1, pageSize: 100 })
    if (res.code === 200) {
      const orders = res.data.list
      const existingOrder = orders.find(order => 
        order.goodsId === goods.value.id && order.status === '待支付'
      )
      if (existingOrder) {
        return existingOrder
      }
    }
  } catch (error) {
    console.error('检查订单失败', error)
  }
  return null
}

const submitOrder = async () => {
  const existingOrder = await checkExistingOrder()
  if (existingOrder) {
    uni.showModal({
      title: '提示',
      content: '您已存在该商品的待支付订单，是否跳转到订单页面？',
      success: (res) => {
        if (res.confirm) {
          uni.navigateTo({ url: '/pages/order/index' })
        }
      }
    })
    return
  }
  
  if (tradeMethod.value === 'pickup') {
    if (!selectedPickupPoint.value) {
      uni.showToast({ title: '请选择自提点', icon: 'none' })
      return
    }
    if (!selectedTimeSlot.value) {
      uni.showToast({ title: '请选择预约时间', icon: 'none' })
      return
    }
  } else if (tradeMethod.value === 'express') {
    if (!selectedAddress.value) {
      uni.showToast({ title: '请选择收货地址', icon: 'none' })
      return
    }
  }
  
  if (!selectedAddress.value && addresses.value.length > 0) {
    selectedAddress.value = addresses.value[0].id
  }
  
  if (!selectedAddress.value) {
    uni.showToast({ title: '请添加收货地址', icon: 'none' })
    return
  }

  uni.showLoading({ title: '创建订单...' })

  try {
    const orderData = {
      sellerId: goods.value.sellerId,
      goodsId: goods.value.id,
      goodsTitle: goods.value.title,
      goodsImage: goods.value.images && goods.value.images[0],
      price: goods.value.price,
      tradeMethod: tradeMethod.value === 'pickup' ? '自提' : tradeMethod.value === 'express' ? '快递' : '面交',
      pickupPointId: tradeMethod.value === 'pickup' ? selectedPickupPoint.value : null,
      pickupTime: tradeMethod.value === 'pickup' ? selectedTimeSlot.value : null,
      addressId: selectedAddress.value,
      remark: remark.value
    }

    const orderRes = await api.order.create(orderData)

    if (orderRes.code !== 200) {
      uni.hideLoading()
      uni.showToast({ title: orderRes.message || '订单创建失败', icon: 'none' })
      return
    }

    const orderId = orderRes.data.id
    
    uni.hideLoading()
    
    uni.showModal({
      title: '订单创建成功',
      content: '是否立即支付？',
      success: async (res) => {
        if (res.confirm) {
          await payOrder(orderId)
        } else {
          uni.navigateTo({ url: '/pages/order/index' })
        }
      }
    })
  } catch (error) {
    uni.hideLoading()
    console.error('创建订单失败', error)
    uni.showToast({ title: '创建订单失败', icon: 'none' })
  }
}

const payOrder = async (orderId) => {
  currentOrderId.value = orderId
  payAmount.value = goods.value.price
  payPassword.value = ''
  showPayModal.value = true
}

const closePayModal = () => {
  showPayModal.value = false
  payPassword.value = ''
}

const handleNumInput = (num) => {
  if (num === 'del') {
    payPassword.value = payPassword.value.slice(0, -1)
  } else if (num === '.') {
    if (!payPassword.value.includes('.')) {
      payPassword.value += num
    }
  } else {
    if (payPassword.value.length < 6) {
      payPassword.value += num
    }
  }
}

const submitPay = async () => {
  if (payPassword.value.length !== 6) {
    return
  }
  
  uni.showLoading({ title: '支付中...' })
  
  try {
    const confirmRes = await api.mockPay.confirm(currentOrderId.value, userId.value, payPassword.value)
    
    if (confirmRes.success) {
      uni.hideLoading()
      closePayModal()
      uni.showToast({ title: '支付成功', icon: 'success' })
      setTimeout(() => {
        uni.navigateTo({ url: '/pages/order/index' })
      }, 1500)
    } else if (confirmRes.needSetPassword) {
      uni.hideLoading()
      uni.showModal({
        title: '提示',
        content: '您还未设置交易密码，请先设置',
        showCancel: false,
        success: () => {
          closePayModal()
          uni.navigateTo({ url: '/pages/user/set-pay-password' })
        }
      })
    } else {
      uni.showToast({ title: confirmRes.message || '支付密码错误', icon: 'none' })
      payPassword.value = ''
    }
    
  } catch (error) {
    uni.hideLoading()
    console.error('支付失败', error)
    uni.showToast({ title: '支付失败', icon: 'none' })
  }
}

onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const goodsId = currentPage.options.id
  if (goodsId) {
    loadGoodsDetail(goodsId)
    loadAddresses()
    loadPickupPoints()
  }
  
  const user = uni.getStorageSync('user')
  if (user && user.id) {
    userId.value = user.id
  }
})

onShow(() => {
  loadAddresses()
  loadPickupPoints()
})
</script>

<style scoped>
.container {
  background-color: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 120rpx;
}

.form-section {
  background-color: #fff;
  padding: 20rpx;
  margin-bottom: 15rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15rpx;
  padding-bottom: 10rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.section-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
}

.add-address-btn {
  font-size: 26rpx;
  color: #ff4444;
}

.empty-address {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx;
}

.empty-text {
  font-size: 26rpx;
  color: #999;
  margin-bottom: 20rpx;
}

.add-address-btn-inline {
  width: 200rpx;
  height: 60rpx;
  line-height: 60rpx;
  font-size: 26rpx;
  background-color: #ff4444;
  color: #fff;
  border: none;
  border-radius: 30rpx;
}

.goods-card {
  display: flex;
  padding: 10rpx;
  background-color: #fafafa;
  border-radius: 10rpx;
}

.goods-image {
  width: 160rpx;
  height: 160rpx;
  border-radius: 10rpx;
}

.goods-info {
  flex: 1;
  padding-left: 20rpx;
  display: flex;
  flex-direction: column;
  justify-content: center;
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
  font-size: 36rpx;
  font-weight: bold;
  color: #ff4444;
}

.original-price {
  font-size: 24rpx;
  color: #999;
  text-decoration: line-through;
  margin-left: 10rpx;
}

.trade-options {
  display: flex;
  gap: 20rpx;
}

.trade-option {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx;
  border: 2rpx solid #e0e0e0;
  border-radius: 10rpx;
  transition: all 0.3s;
}

.trade-option.active {
  border-color: #ff4444;
  background-color: #fff5f0;
}

.trade-icon {
  font-size: 40rpx;
  margin-bottom: 10rpx;
}

.trade-label {
  font-size: 26rpx;
  color: #333;
}

.pickup-list, .address-list {
  display: flex;
  flex-direction: column;
  gap: 15rpx;
}

.pickup-item, .address-item {
  display: flex;
  align-items: center;
  padding: 15rpx;
  border: 2rpx solid #e0e0e0;
  border-radius: 10rpx;
}

.pickup-item.active, .address-item.active {
  border-color: #ff4444;
  background-color: #fff5f0;
}

.pickup-radio, .address-radio {
  width: 32rpx;
  height: 32rpx;
  border: 2rpx solid #ccc;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15rpx;
}

.pickup-radio.checked, .address-radio.checked {
  border-color: #ff4444;
}

.radio-inner {
  width: 18rpx;
  height: 18rpx;
  background-color: #ff4444;
  border-radius: 50%;
}

.pickup-info, .address-info {
  flex: 1;
}

.pickup-name, .address-name {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 5rpx;
}

.pickup-address, .address-detail {
  font-size: 24rpx;
  color: #666;
}

.add-pickup-btn {
  font-size: 26rpx;
  color: #ff4444;
}

.empty-pickup {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx;
}

.add-pickup-btn-inline {
  width: 200rpx;
  height: 60rpx;
  line-height: 60rpx;
  font-size: 26rpx;
  background-color: #ff4444;
  color: #fff;
  border: none;
  border-radius: 30rpx;
  margin-top: 20rpx;
}

.time-slots {
  display: flex;
  flex-wrap: wrap;
  gap: 15rpx;
}

.time-slot {
  padding: 15rpx 25rpx;
  border: 2rpx solid #e0e0e0;
  border-radius: 25rpx;
  font-size: 24rpx;
  color: #333;
  transition: all 0.3s;
}

.time-slot.active {
  border-color: #ff4444;
  background-color: #ff4444;
  color: #fff;
}

.remark-input {
  width: 100%;
  height: 120rpx;
  border: 1rpx solid #e0e0e0;
  border-radius: 10rpx;
  padding: 15rpx;
  font-size: 26rpx;
  resize: none;
  box-sizing: border-box;
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #fff;
  padding: 20rpx;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.1);
}

.total-info {
  display: flex;
  align-items: baseline;
}

.total-label {
  font-size: 26rpx;
  color: #666;
}

.total-price {
  font-size: 40rpx;
  font-weight: bold;
  color: #ff4444;
  margin-left: 10rpx;
}

.submit-btn {
  width: 280rpx;
  height: 80rpx;
  line-height: 80rpx;
  font-size: 30rpx;
  background-color: #ff4444;
  color: #fff;
  border: none;
  border-radius: 40rpx;
}

.pay-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  z-index: 100;
}

.pay-content {
  width: 100%;
  background-color: #fff;
  border-radius: 30rpx 30rpx 0 0;
  padding-bottom: env(safe-area-inset-bottom);
}

.pay-header {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
  position: relative;
}

.pay-icon {
  font-size: 48rpx;
  margin-right: 15rpx;
}

.pay-title {
  font-size: 34rpx;
  font-weight: bold;
  color: #333;
}

.pay-close {
  position: absolute;
  right: 30rpx;
  font-size: 48rpx;
  color: #999;
  line-height: 1;
}

.pay-body {
  padding: 30rpx;
}

.pay-amount-section {
  text-align: center;
  margin-bottom: 30rpx;
}

.pay-amount-label {
  font-size: 28rpx;
  color: #999;
  margin-bottom: 15rpx;
  display: block;
}

.pay-amount {
  display: flex;
  align-items: baseline;
  justify-content: center;
}

.amount-symbol {
  font-size: 36rpx;
  color: #ff4444;
  font-weight: bold;
}

.amount-value {
  font-size: 64rpx;
  color: #ff4444;
  font-weight: bold;
  margin-left: 5rpx;
}

.pay-method-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx;
  background-color: #f8f8f8;
  border-radius: 10rpx;
  margin-bottom: 30rpx;
}

.pay-method-label {
  font-size: 28rpx;
  color: #333;
}

.pay-method {
  display: flex;
  align-items: center;
}

.method-icon {
  font-size: 32rpx;
  margin-right: 10rpx;
}

.method-name {
  font-size: 28rpx;
  color: #333;
}

.pay-password-section {
  margin-bottom: 30rpx;
}

.pay-password-label {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 20rpx;
  display: block;
}

.password-input-box {
  display: flex;
  justify-content: center;
  gap: 15rpx;
}

.password-input {
  width: 80rpx;
  height: 80rpx;
  border: 2rpx solid #e0e0e0;
  border-radius: 10rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36rpx;
  color: #333;
  font-weight: bold;
}

.password-input.filled {
  border-color: #ff4444;
  background-color: #fff5f0;
}

.pay-footer {
  padding: 0 30rpx;
  margin-bottom: 20rpx;
}

.pay-submit-btn {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  font-size: 32rpx;
  background-color: #ff4444;
  color: #fff;
  border: none;
  border-radius: 44rpx;
}

.pay-submit-btn:disabled {
  background-color: #ccc;
}

.num-pad {
  display: flex;
  flex-wrap: wrap;
  border-top: 1rpx solid #f0f0f0;
}

.num-btn {
  width: 25%;
  height: 100rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  color: #333;
  border-bottom: 1rpx solid #f0f0f0;
  border-right: 1rpx solid #f0f0f0;
  box-sizing: border-box;
}

.num-btn:nth-child(4n) {
  border-right: none;
}

.num-btn:last-child {
  color: #999;
}

.num-btn:active {
  background-color: #f5f5f5;
}
</style>