<template>
  <view class="container">
    <view class="order-type-bar">
      <view class="type-item" :class="{ active: orderType === 'buy' }" @click="switchOrderType('buy')">
        <text>我的购买</text>
      </view>
      <view class="type-item" :class="{ active: orderType === 'sell' }" @click="switchOrderType('sell')">
        <text>我的售卖</text>
      </view>
    </view>
    
    <view class="tab-bar" :class="{ 'sell-mode': orderType === 'sell' }">
      <view class="tab-item" :class="{ active: activeTab === 0 }" @click="activeTab = 0">
        <text>全部</text>
      </view>
      <view v-if="orderType === 'buy'" class="tab-item" :class="{ active: activeTab === 1 }" @click="activeTab = 1">
        <text>待付款</text>
      </view>
      <view class="tab-item" :class="{ active: orderType === 'buy' ? activeTab === 2 : activeTab === 1 }" @click="activeTab = orderType === 'buy' ? 2 : 1">
        <text>待发货</text>
      </view>
      <view class="tab-item" :class="{ active: orderType === 'buy' ? activeTab === 3 : activeTab === 2 }" @click="activeTab = orderType === 'buy' ? 3 : 2">
        <text>待收货</text>
      </view>
      <view class="tab-item" :class="{ active: orderType === 'buy' ? activeTab === 4 : activeTab === 3 }" @click="activeTab = orderType === 'buy' ? 4 : 3">
        <text>已完成</text>
      </view>
    </view>
    
    <view v-if="loading" class="loading">
      <text class="loading-icon">⌛</text>
      <text>加载中...</text>
    </view>
    
    <view v-else class="order-list">
      <view class="order-item" v-for="order in filteredOrders" :key="order.id">
        <view class="order-header">
          <text class="order-no">订单号: {{ order.orderNo }}</text>
          <text class="order-status" :class="order.statusCode === 0 ? 'pending' : order.statusCode === 1 ? 'processing' : order.statusCode === 2 ? 'completed' : 'cancelled'">{{ order.statusText }}</text>
        </view>
        
        <view class="order-goods">
          <image :src="order.goodsImage || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=product%20placeholder&image_size=square'" class="goods-image"></image>
          <view class="goods-info">
            <text class="goods-title">{{ order.goodsTitle || '未知商品' }}</text>
            <text class="trade-method">交易方式: {{ order.tradeMethod || '当面交易' }}</text>
          </view>
        </view>
        
        <view class="order-detail" v-if="order.pickupPointName || order.pickupTime">
          <view class="detail-row" v-if="order.pickupPointName">
            <text class="detail-label">📍 自提点:</text>
            <text class="detail-value">{{ order.pickupPointName }}</text>
          </view>
          <view class="detail-row" v-if="order.pickupTime">
            <text class="detail-label">⏰ 预约时间:</text>
            <text class="detail-value">{{ order.pickupTime }}</text>
          </view>
        </view>
        
        <view class="order-footer">
          <text class="total-price">实付: ¥{{ order.price }}</text>
          <view class="order-actions">
            <template v-if="orderType === 'buy'">
              <button v-if="order.statusCode === 0" class="action-btn primary" @click="openPayPopup(order)">立即支付</button>
              <button v-if="order.statusCode === 0" class="action-btn secondary" @click="openChangePickupPopup(order)">修改订单</button>
              <button v-if="order.statusCode === 0" class="action-btn secondary" @click="cancelOrder(order.id)">取消订单</button>
              <button v-if="order.statusCode === 1" class="action-btn secondary" @click="contactSeller(order)">联系卖家</button>
              <button v-if="order.statusCode === 2" class="action-btn primary" @click="confirmPickup(order.id)">确认收货</button>
              <button v-if="order.statusCode === 2" class="action-btn secondary" @click="applyRefund(order)">申请退款</button>
              <button v-if="order.statusCode === 3 && !order.hasComment" class="action-btn secondary" @click="leaveComment(order)">评价</button>
              <text v-if="order.statusCode === 3 && order.hasComment" class="commented-text">已评价</text>
            </template>
            <template v-else>
              <button v-if="order.statusCode === 1" class="action-btn primary" @click="shipOrder(order.id)">去发货</button>
              <text v-if="order.statusCode === 2" class="action-text">买家待收货</text>
              <text v-if="order.statusCode === 3" class="action-text">交易完成</text>
            </template>
          </view>
        </view>
      </view>
      <view v-if="filteredOrders.length === 0" class="empty-order">
        <text>暂无订单</text>
      </view>
    </view>
    
    <!-- 评价弹窗 -->
    <view class="comment-popup" v-if="showCommentPopup">
      <view class="comment-content">
        <view class="comment-header">
          <text class="comment-title">商品评价</text>
          <text class="comment-close" @click="showCommentPopup = false">×</text>
        </view>
        
        <view class="comment-form">
          <view class="rating-section">
            <text class="rating-label">评分</text>
            <view class="star-rating">
              <text class="star" v-for="i in 5" :key="i" :class="{ active: i <= commentRating }" @click="commentRating = i">{{ i <= commentRating ? '★' : '☆' }}</text>
            </view>
          </view>
          
          <text class="comment-label">评价内容</text>
          <textarea class="comment-textarea" v-model="commentContent" placeholder="请输入评价内容" />
          
          <view class="image-section">
            <text class="comment-label">图片评价</text>
            <view class="image-upload">
              <view class="upload-btn" v-if="commentImages.length < 9" @click="chooseImage">
                <text class="upload-icon">+</text>
              </view>
              <view class="image-item" v-for="(img, index) in commentImages" :key="index">
                <image :src="img" mode="aspectFill" class="uploaded-image" />
                <text class="remove-icon" @click="removeImage(index)">×</text>
              </view>
            </view>
          </view>
        </view>
        
        <view class="comment-buttons">
          <button class="cancel-btn" @click="showCommentPopup = false">取消</button>
          <button class="confirm-btn" @click="submitComment">提交评价</button>
        </view>
      </view>
    </view>
    
    <!-- 修改订单弹窗 -->
    <view class="popup" v-if="showChangePickupPopup" @click="showChangePickupPopup = false">
      <view class="popup-content" @click.stop>
        <view class="popup-header">
          <text class="popup-title">修改订单</text>
          <text class="popup-close" @click="showChangePickupPopup = false">×</text>
        </view>
        
        <view class="popup-body">
          <view class="form-section">
            <view class="trade-options">
              <view class="trade-option" :class="{ active: editTradeMethod === 'pickup' }" @click="editTradeMethod = 'pickup'">
                <text class="trade-icon">📍</text>
				<!-- <image class="trade-icon" src="/static/自提点.png" mode=""></image> -->
                <text class="trade-label">自提</text>
              </view>
              <view class="trade-option" :class="{ active: editTradeMethod === 'express' }" @click="editTradeMethod = 'express'">
                <text class="trade-icon">📦</text>
                <text class="trade-label">快递</text>
              </view>
              <view class="trade-option" :class="{ active: editTradeMethod === 'meeting' }" @click="editTradeMethod = 'meeting'">
                <text class="trade-icon">🤝</text>
                <text class="trade-label">面交</text>
              </view>
            </view>
          </view>
          
          <view class="form-section" v-if="editTradeMethod === 'pickup'">
            <view class="section-header">
              <text class="section-title">常用自提点</text>
              <text class="add-pickup-btn" @click="goToPickupPoints">+ 添加常用点</text>
            </view>
            <view class="pickup-list" v-if="pickupPoints.length > 0">
              <view 
                class="pickup-item" 
                v-for="pickup in pickupPoints" 
                :key="pickup.id"
                :class="{ active: newPickupPoint === pickup.id }"
                @click="newPickupPoint = pickup.id"
              >
                <view class="pickup-radio" :class="{ checked: newPickupPoint === pickup.id }">
                  <view class="radio-inner" v-if="newPickupPoint === pickup.id"></view>
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
            
            <view class="time-section">
              <text class="section-title">预约时间</text>
              <view class="time-slots">
                <view 
                  class="time-slot" 
                  v-for="(slot, index) in timeSlots" 
                  :key="index"
                  :class="{ active: newTimeSlot === slot }"
                  @click="newTimeSlot = slot"
                >
                  <text>{{ slot }}</text>
                </view>
              </view>
            </view>
          </view>
          
          <view class="form-section" v-if="editTradeMethod === 'express'">
            <view class="section-header">
              <text class="section-title">收货地址</text>
              <text class="add-address-btn" @click="goToAddAddress">+ 添加地址</text>
            </view>
            <view class="address-list" v-if="addresses.length > 0">
              <view 
                class="address-item" 
                v-for="address in addresses" 
                :key="address.id"
                :class="{ active: newAddress === address.id }"
                @click="newAddress = address.id"
              >
                <view class="address-radio" :class="{ checked: newAddress === address.id }">
                  <view class="radio-inner" v-if="newAddress === address.id"></view>
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
          
          <view class="form-section" v-if="editTradeMethod === 'meeting'">
            <view class="meeting-info">
              <text class="meeting-icon">🤝</text>
              <text class="meeting-text">面交方式：请与卖家协商见面地点和时间</text>
            </view>
          </view>
        </view>
        
        <view class="popup-footer">
          <button class="cancel-btn" @click="showChangePickupPopup = false">取消</button>
          <button class="confirm-btn" @click="changeOrder">确认修改</button>
        </view>
      </view>
    </view>
    
    <!-- 退款申请弹窗 -->
    <view class="popup" v-if="showRefundPopup">
      <view class="popup-content">
        <view class="popup-header">
          <text class="popup-title">申请退款</text>
          <text class="popup-close" @click="showRefundPopup = false">×</text>
        </view>
        
        <view class="popup-body">
          <text class="refund-label">退款原因</text>
          <textarea class="refund-textarea" v-model="refundReason" placeholder="请输入退款原因"></textarea>
        </view>
        
        <view class="popup-footer">
          <button class="cancel-btn" @click="showRefundPopup = false">取消</button>
          <button class="confirm-btn" @click="submitRefund">提交申请</button>
        </view>
      </view>
    </view>
    
    <!-- 支付密码弹窗 -->
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
              <text class="amount-value">{{ currentPayOrder?.price || 0 }}</text>
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
            <view class="password-tip" v-if="!hasPayPassword" @click="goToSetPassword">
              <text class="tip-icon">⚠️</text>
              <text class="tip-text">您还未设置交易密码，点击这里设置</text>
            </view>
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
import { ref, computed, onMounted } from 'vue'
import api from '../../utils/api.js'

const activeTab = ref(0)
const orders = ref([])
const loading = ref(true)
const orderType = ref('buy') // 'buy' or 'sell'

const showCommentPopup = ref(false)
const commentRating = ref(5)
const commentContent = ref('')
const currentCommentOrder = ref(null)
const commentImages = ref([])

const showChangePickupPopup = ref(false)
const currentOrderForPickup = ref(null)
const newPickupPoint = ref('')
const newTimeSlot = ref('')
const newAddress = ref('')
const editTradeMethod = ref('pickup')
const currentOrderForEdit = ref(null)

const showRefundPopup = ref(false)
const currentOrderForRefund = ref(null)
const refundReason = ref('')

// 支付相关
const showPayModal = ref(false)
const currentPayOrder = ref(null)
const payPassword = ref('')
const hasPayPassword = ref(true)
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

const filteredOrders = computed(() => {
  let statusFilter = null
  if (activeTab.value === 0) {
    statusFilter = null
  } else if (orderType.value === 'buy') {
    statusFilter = activeTab.value - 1
  } else {
    statusFilter = activeTab.value
  }
  if (statusFilter === null) {
    return orders.value
  } else {
    return orders.value.filter(item => item.statusCode === statusFilter)
  }
})

const fetchOrders = async () => {
  try {
    loading.value = true
    let res
    if (orderType.value === 'buy') {
      res = await api.order.list({ page: 1, pageSize: 100 })
    } else {
      res = await api.order.sellerList({ page: 1, pageSize: 100 })
    }
    if (res.code === 200) {
      orders.value = res.data.list.map(order => ({
        ...order,
        statusText: statusTextMap[order.status] || order.status || '未知',
        statusCode: statusCodeMap[order.status] || 0
      }))
    }
  } catch (error) {
    console.error('获取订单列表失败', error)
    uni.showToast({ title: '获取订单列表失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

const switchOrderType = async (type) => {
  orderType.value = type
  activeTab.value = 0
  await fetchOrders()
}

const openPayPopup = (order) => {
  currentPayOrder.value = order
  payPassword.value = ''
  showPayModal.value = true
}

const closePayModal = () => {
  showPayModal.value = false
  payPassword.value = ''
}

const goToSetPassword = () => {
  uni.navigateTo({ url: '/pages/user/set-pay-password' })
  closePayModal()
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
    const confirmRes = await api.mockPay.confirm(currentPayOrder.value.id, userId.value, payPassword.value)
    
		if (confirmRes.success) {
		  uni.hideLoading()
		  closePayModal()
		  uni.showToast({ title: '支付成功', icon: 'success' })
		  fetchOrders()
		} 
		else if (confirmRes.needSetPassword){
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
		} 
		else {
			uni.showToast({ title: confirmRes.message || '支付密码错误', icon: 'none' })
			payPassword.value = ''
		}
    }
	catch (error) {
    uni.hideLoading()
    console.error('支付失败', error)
    uni.showToast({ title: '支付失败', icon: 'none' })
  }
}

const cancelOrder = (orderId) => {
  uni.showModal({
    title: '取消订单',
    content: '确定要取消订单吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          const result = await api.order.cancel(orderId)
          if (result.code === 200) {
            uni.showToast({ title: '订单已取消', icon: 'success' })
            fetchOrders()
          } else {
            uni.showToast({ title: '取消订单失败', icon: 'none' })
          }
        } catch (error) {
          console.error('取消订单失败', error)
          uni.showToast({ title: '取消订单失败', icon: 'none' })
        }
      }
    }
  })
}

const shipOrder = async (orderId) => {
  uni.showModal({
    title: '确认发货',
    content: '确定要发货吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          const result = await api.order.ship(orderId)
          if (result.code === 200) {
            uni.showToast({ title: '发货成功', icon: 'success' })
            fetchOrders()
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

const contactSeller = async (order) => {
  try {
    uni.showLoading({ title: '正在打开聊天...' })
    
    const res = await api.chat.getSession(order.sellerId)
    
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

const confirmPickup = (orderId) => {
  uni.showModal({
    title: '确认收货',
    content: '确定已经收到商品了吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          const result = await api.order.confirm(orderId)
          if (result.code === 200) {
            uni.showToast({ title: '确认收货成功', icon: 'success' })
            fetchOrders()
          } else {
            uni.showToast({ title: '确认失败', icon: 'none' })
          }
        } catch (error) {
          console.error('确认收货失败', error)
          uni.showToast({ title: '确认失败', icon: 'none' })
        }
      }
    }
  })
}

const openChangePickupPopup = async (order) => {
  currentOrderForEdit.value = order
  
  if (order.tradeMethod === '自提') {
    editTradeMethod.value = 'pickup'
    newPickupPoint.value = order.pickupPointId || pickupPoints[0]?.id || ''
    newTimeSlot.value = order.pickupTime || timeSlots[0] || ''
    newAddress.value = ''
  } else if (order.tradeMethod === '快递') {
    editTradeMethod.value = 'express'
    newAddress.value = order.addressId || (addresses.value[0]?.id || '')
    newPickupPoint.value = ''
    newTimeSlot.value = ''
  } else {
    editTradeMethod.value = 'meeting'
    newPickupPoint.value = ''
    newTimeSlot.value = ''
    newAddress.value = ''
  }
  
  await Promise.all([loadAddresses(), loadPickupPoints()])
  showChangePickupPopup.value = true
}

const loadAddresses = async () => {
  try {
    const res = await api.user.getAddresses()
    if (res.code === 200) {
      addresses.value = res.data
    }
  } catch (error) {
    console.error('获取地址失败', error)
  }
}

const loadPickupPoints = async () => {
  try {
    const res = await api.user.getFavoritePickupPoints()
    if (res && res.code === 200 && res.data) {
      pickupPoints.value = res.data
    } else {
      pickupPoints.value = []
    }
  } catch (error) {
    console.error('获取常用自提点失败', error)
    pickupPoints.value = []
  }
}

const changeOrder = async () => {
  const orderId = currentOrderForEdit.value.id
  const updateData = {
    tradeMethod: editTradeMethod.value === 'pickup' ? '自提' : editTradeMethod.value === 'express' ? '快递' : '面交'
  }
  
  if (editTradeMethod.value === 'pickup') {
    if (!newPickupPoint.value) {
      uni.showToast({ title: '请选择自提点', icon: 'none' })
      return
    }
    if (!newTimeSlot.value) {
      uni.showToast({ title: '请选择预约时间', icon: 'none' })
      return
    }
    updateData.pickupPointId = newPickupPoint.value
    updateData.pickupTime = newTimeSlot.value
    updateData.pickupPointName = pickupPoints.value.find(p => p.id === newPickupPoint.value)?.name || ''
  } else if (editTradeMethod.value === 'express') {
    if (!newAddress.value) {
      uni.showToast({ title: '请选择收货地址', icon: 'none' })
      return
    }
    updateData.addressId = newAddress.value
  }
  
  try {
    const result = await api.order.update(orderId, updateData)
    if (result.code === 200) {
      uni.showToast({ title: '订单修改成功', icon: 'success' })
      showChangePickupPopup.value = false
      fetchOrders()
    } else {
      uni.showToast({ title: result.message || '修改失败', icon: 'none' })
    }
  } catch (error) {
    console.error('修改订单失败', error)
    uni.showToast({ title: '修改失败', icon: 'none' })
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

const applyRefund = (order) => {
  currentOrderForRefund.value = order
  refundReason.value = ''
  showRefundPopup.value = true
}

const submitRefund = async () => {
  if (!refundReason.value.trim()) {
    uni.showToast({ title: '请输入退款原因', icon: 'none' })
    return
  }
  
  try {
    const result = await api.order.applyRefund(currentOrderForRefund.value.id, {
      reason: refundReason.value
    })
    if (result.code === 200) {
      uni.showToast({ title: '退款申请已提交', icon: 'success' })
      showRefundPopup.value = false
      fetchOrders()
    } else {
      uni.showToast({ title: result.message || '提交失败', icon: 'none' })
    }
  } catch (error) {
    console.error('提交退款申请失败', error)
    uni.showToast({ title: '提交失败', icon: 'none' })
  }
}

const leaveComment = (order) => {
  currentCommentOrder.value = order
  commentRating.value = 5
  commentContent.value = ''
  commentImages.value = []
  showCommentPopup.value = true
}

const submitComment = async () => {
  if (!commentContent.value.trim()) {
    uni.showToast({ title: '请输入评价内容', icon: 'none' })
    return
  }
  
  uni.showLoading({ title: '提交中...' })
  
  try {
    const uploadedImages = await uploadImages(commentImages.value)
    
    const res = await api.comment.create({
      goodsId: currentCommentOrder.value.goodsId,
      orderId: currentCommentOrder.value.id,
      rating: commentRating.value,
      content: commentContent.value,
      images: uploadedImages
    })
    
    if (res.code === 200) {
      uni.hideLoading()
      uni.showToast({ title: '评价成功', icon: 'success' })
      showCommentPopup.value = false
      commentContent.value = ''
      commentImages.value = []
      fetchOrders()
    } else {
      uni.hideLoading()
      uni.showToast({ title: res.message || '评价失败', icon: 'none' })
    }
  } catch (error) {
    uni.hideLoading()
    console.error('评价失败', error)
    uni.showToast({ title: '评价失败', icon: 'none' })
  }
}

const uploadImages = async (images) => {
  if (!images || images.length === 0) {
    return []
  }
  
  const uploadedUrls = []
  for (const imagePath of images) {
    try {
      const res = await api.upload.image({ filePath: imagePath })
      if (res.code === 200 && res.data.url) {
        uploadedUrls.push(res.data.url)
      }
    } catch (error) {
      console.error('上传图片失败', error)
    }
  }
  return uploadedUrls
}

const chooseImage = () => {
  uni.chooseImage({
    count: 9,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: (res) => {
      const tempFilePaths = res.tempFilePaths
      tempFilePaths.forEach(path => {
        if (commentImages.value.length < 9) {
          commentImages.value.push(path)
        }
      })
    },
    fail: (err) => {
      console.error('选择图片失败', err)
    }
  })
}

const removeImage = (index) => {
  commentImages.value.splice(index, 1)
}

onMounted(() => {
  fetchOrders()
  
  const user = uni.getStorageSync('user')
  if (user && user.id) {
    userId.value = user.id
  }
})
</script>

<style scoped>
.container {
  background-color: #f5f5f5;
  min-height: 100vh;
}

.order-type-bar {
  display: flex;
  background-color: #fff;
  padding: 20rpx 40rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.type-item {
  flex: 1;
  height: 72rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30rpx;
  color: #666;
  background-color: #f5f5f5;
  border-radius: 36rpx;
  margin: 0 10rpx;
  transition: all 0.3s;
}

.type-item.active {
  color: #fff;
  background-color: #ff4444;
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
  position: sticky;
  top: 0;
  z-index: 100;
}

.tab-item {
  flex: 1;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
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
  left: 30%;
  right: 30%;
  height: 4rpx;
  background-color: #ff4444;
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
  margin-bottom: 20rpx;
  padding-bottom: 10rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.order-no {
  font-size: 24rpx;
  color: #666;
}

.order-status {
  font-size: 24rpx;
  font-weight: bold;
  padding: 4rpx 16rpx;
  border-radius: 20rpx;
}

.order-status.pending {
  color: #ff6b35;
  background-color: #fff5f0;
}

.order-status.processing{
	color: #ff6b35;
	background-color: #fff5f1;
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

.order-goods {
  display: flex;
  margin-bottom: 20rpx;
}

.goods-image {
  width: 120rpx;
  height: 120rpx;
  border-radius: 5rpx;
  margin-right: 20rpx;
}

.goods-info {
  flex: 1;
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
  font-size: 28rpx;
  color: #ff4444;
  font-weight: bold;
  margin-bottom: 5rpx;
}

.trade-method {
  font-size: 22rpx;
  color: #666;
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
  color: #333;
  font-weight: bold;
}

.order-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
}

.action-btn {
  width: 120rpx;
  height: 60rpx;
  line-height: 60rpx;
  font-size: 24rpx;
  border-radius: 30rpx;
}

.action-btn.primary {
  background-color: #ff4444;
  color: #fff;
  border: none;
}

.action-btn.secondary {
  background-color: #fff;
  color: #666;
  border: 1rpx solid #e0e0e0;
}

.commented-text {
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

/* 评价弹窗 */
.comment-popup {
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

.comment-content {
  background-color: #fff;
  border-top-left-radius: 20rpx;
  border-top-right-radius: 20rpx;
  width: 100%;
  padding: 30rpx;
}

.comment-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 30rpx;
}

.comment-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.comment-close {
  font-size: 40rpx;
  color: #999;
}

.comment-form {
  margin-bottom: 30rpx;
}

.rating-section {
  margin-bottom: 20rpx;
}

.rating-label {
  display: block;
  font-size: 26rpx;
  color: #666;
  margin-bottom: 10rpx;
}

.star-rating {
  display: flex;
  gap: 20rpx;
}

.star {
  font-size: 48rpx;
  color: #ddd;
}

.star.active {
  color: #ffcc00;
}

.comment-label {
  display: block;
  font-size: 26rpx;
  color: #666;
  margin-bottom: 10rpx;
}

.comment-textarea {
  width: 100%;
  height: 200rpx;
  border: 1rpx solid #e0e0e0;
  border-radius: 10rpx;
  padding: 20rpx;
  font-size: 28rpx;
  resize: none;
  box-sizing: border-box;
}

.image-section {
  margin-top: 20rpx;
}

.image-upload {
  display: flex;
  flex-wrap: wrap;
  gap: 15rpx;
}

.upload-btn {
  width: 120rpx;
  height: 120rpx;
  border: 2rpx dashed #ccc;
  border-radius: 10rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.upload-icon {
  font-size: 48rpx;
  color: #ccc;
}

.image-item {
  position: relative;
  width: 120rpx;
  height: 120rpx;
}

.uploaded-image {
  width: 100%;
  height: 100%;
  border-radius: 10rpx;
}

.remove-icon {
  position: absolute;
  top: -10rpx;
  right: -10rpx;
  width: 32rpx;
  height: 32rpx;
  background-color: rgba(0, 0, 0, 0.6);
  color: #fff;
  border-radius: 50%;
  font-size: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.comment-buttons {
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

.order-detail {
  padding: 15rpx;
  background-color: #fafafa;
  border-radius: 8rpx;
  margin-bottom: 15rpx;
}

.detail-row {
  display: flex;
  align-items: center;
  margin-bottom: 8rpx;
}

.detail-row:last-child {
  margin-bottom: 0;
}

.detail-label {
  font-size: 24rpx;
  color: #999;
  margin-right: 10rpx;
}

.detail-value {
  font-size: 24rpx;
  color: #333;
}

.order-status.refunded {
  color: #9966cc;
  background-color: #f5e6ff;
}

.popup {
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

.popup-content {
  background-color: #fff;
  border-top-left-radius: 20rpx;
  border-top-right-radius: 20rpx;
  width: 100%;
  padding: 30rpx;
}

.popup-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 30rpx;
}

.popup-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.popup-close {
  font-size: 40rpx;
  color: #999;
}

.popup-body {
  margin-bottom: 30rpx;
}

.popup-footer {
  display: flex;
  gap: 20rpx;
}

.pickup-list {
  display: flex;
  flex-direction: column;
  gap: 15rpx;
}

.pickup-item {
  display: flex;
  align-items: center;
  padding: 15rpx;
  border: 2rpx solid #e0e0e0;
  border-radius: 10rpx;
}

.pickup-item.active {
  border-color: #ff4444;
  background-color: #fff5f0;
}

.pickup-radio {
  width: 32rpx;
  height: 32rpx;
  border: 2rpx solid #ccc;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15rpx;
}

.pickup-radio.checked {
  border-color: #ff4444;
}

.form-section {
  margin-bottom: 30rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
}

.add-address-btn, .add-pickup-btn {
  font-size: 26rpx;
  color: #ff4444;
}

.empty-address, .empty-pickup {
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

.add-address-btn-inline, .add-pickup-btn-inline {
  width: 200rpx;
  height: 60rpx;
  line-height: 60rpx;
  font-size: 26rpx;
  background-color: #ff4444;
  color: #fff;
  border: none;
  border-radius: 30rpx;
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

.time-section {
  margin-top: 30rpx;
}

.time-slots {
  display: flex;
  flex-wrap: wrap;
  gap: 15rpx;
}

.time-slot {
  padding: 10rpx 20rpx;
  border: 2rpx solid #e0e0e0;
  border-radius: 8rpx;
  font-size: 24rpx;
}

.time-slot.active {
  border-color: #ff4444;
  background-color: #fff5f0;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 15rpx;
}

.address-item {
  display: flex;
  padding: 15rpx;
  border: 2rpx solid #e0e0e0;
  border-radius: 10rpx;
}

.address-item.active {
  border-color: #ff4444;
  background-color: #fff5f0;
}

.address-radio {
  width: 32rpx;
  height: 32rpx;
  border: 2rpx solid #ccc;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15rpx;
  flex-shrink: 0;
}

.address-info {
  flex: 1;
}

.address-name {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 5rpx;
  display: block;
}

.address-detail {
  font-size: 26rpx;
  color: #666;
  display: block;
}

.address-radio.checked {
  border-color: #ff4444;
}

.meeting-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx;
  background-color: #fafafa;
  border-radius: 10rpx;
}

.meeting-icon {
  font-size: 60rpx;
  margin-bottom: 20rpx;
}

.meeting-text {
  font-size: 26rpx;
  color: #666;
  text-align: center;
}

.radio-inner {
  width: 18rpx;
  height: 18rpx;
  background-color: #ff4444;
  border-radius: 50%;
}

.pickup-info {
  flex: 1;
}

.pickup-name {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 5rpx;
}

.pickup-address {
  font-size: 24rpx;
  color: #666;
}

.refund-label {
  display: block;
  font-size: 26rpx;
  color: #666;
  margin-bottom: 15rpx;
}

.refund-textarea {
  width: 100%;
  height: 200rpx;
  border: 1rpx solid #e0e0e0;
  border-radius: 10rpx;
  padding: 20rpx;
  font-size: 28rpx;
  resize: none;
  box-sizing: border-box;
}

/* 支付密码弹窗 */
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

.password-tip {
  display: flex;
  align-items: center;
  padding: 15rpx 20rpx;
  background-color: #fff8e1;
  border-radius: 10rpx;
  margin-bottom: 20rpx;
}

.tip-icon {
  font-size: 28rpx;
  margin-right: 10rpx;
}

.tip-text {
  font-size: 26rpx;
  color: #ff9800;
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