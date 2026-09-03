<template>
  <view class="container">
    <view class="user-info">
      <view class="avatar-wrapper">
        <image :src="user.avatar" class="avatar" mode="aspectFill"></image>
        <view class="avatar-edit" @click="chooseAvatar">
          <text class="edit-icon">📷</text>
        </view>
      </view>
      <view class="user-meta">
        <text class="username">{{ user.username }}</text>
        <text class="campus">{{ user.campusName }}</text>
        <view class="user-stats">
          <view class="stat-item">
            <text class="stat-value">{{ user.goodsCount || 0 }}</text>
            <text class="stat-label">商品</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-value">{{ user.commentCount || 0 }}</text>
            <text class="stat-label">评价</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-value">{{ user.orderCount || 0 }}</text>
            <text class="stat-label">订单</text>
          </view>
        </view>
      </view>
      <button class="edit-btn" @click="editProfile">编辑资料</button>
    </view>
    
    <view class="menu-section">
      <view class="menu-item" @click="goToMyGoods">
        <image class="menu-icon" src="/static/我的商品.png" mode="aspectFit"></image>
        <text class="menu-text">我的商品</text>
        <text class="menu-arrow">＞</text>
      </view>
      <view class="menu-item" @click="goToMyOrders">
        <image class="menu-icon" src="/static/我的订单.png" mode="aspectFit"></image>
        <text class="menu-text">我的订单</text>
        <text class="menu-arrow">＞</text>
      </view>
      <view class="menu-item" @click="goToMyFavorites">
        <image class="menu-icon" src="/static/我的收藏.png" mode="aspectFit"></image>
        <text class="menu-text">我的收藏</text>
        <text class="menu-arrow">＞</text>
      </view>
      <view class="menu-item" @click="goToMyComments">
        <image class="menu-icon" src="/static/我的评价.png" mode="aspectFit"></image>
        <text class="menu-text">我的评价</text>
        <text class="menu-arrow">＞</text>
      </view>
      <view class="menu-item" @click="goToNotifications">
        <!-- <text class="menu-icon-text"></text> -->
		<image class="menu-icon" src="/static/消息通知.png" mode="aspectFit"></image>
        <text class="menu-text">我的通知</text>
        <view class="notification-badge" v-if="unreadCount > 0">{{ unreadCount }}</view>
        <text class="menu-arrow">＞</text>
      </view>
    </view>
    
    <view class="menu-section">
      <view class="menu-item" @click="goToAddress">
        <image class="menu-icon" src="/static/我的地址.png" mode="aspectFit"></image>
        <text class="menu-text">收货地址</text>
        <text class="menu-arrow">＞</text>
      </view>
      <view class="menu-item" @click="goToAuth">
        <image class="menu-icon" src="/static/设置.png" mode="aspectFit"></image>
        <text class="menu-text">实名认证</text>
        <view class="auth-badge" v-if="user.authStatus === '待审核'">审核中</view>
        <text class="menu-arrow">＞</text>
      </view>
      <view class="menu-item" @click="goToPickupPoints">
        <image class="menu-icon" src="/static/我的地址.png" mode="aspectFit"></image>
        <text class="menu-text">常用自提点</text>
        <text class="menu-arrow">＞</text>
      </view>
      <view class="menu-item" @click="goToSettings">
        <image class="menu-icon" src="/static/设置.png" mode="aspectFit"></image>
        <text class="menu-text">设置</text>
        <text class="menu-arrow">＞</text>
      </view>
      <view class="menu-item" @click="goToHelp">
        <image class="menu-icon" src="/static/帮助.png" mode="aspectFit"></image>
        <text class="menu-text">帮助中心</text>
        <text class="menu-arrow">＞</text>
      </view>
      <view class="menu-item" @click="goToAbout">
        <image class="menu-icon" src="/static/关于_o.png" mode="aspectFit"></image>
        <text class="menu-text">关于我们</text>
        <text class="menu-arrow">＞</text>
      </view>
    </view>
    
    <button class="logout-btn" @click="logout">退出登录</button>
  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import api from '../../utils/api.js'

const user = reactive({
  id: null,
  avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20portrait%20friendly&image_size=square',
  username: '未登录',
  campusName: '',
  authStatus: '',
  goodsCount: 0,
  commentCount: 0,
  orderCount: 0
})

const unreadCount = ref(0)

const checkLoginStatus = () => {
  const token = uni.getStorageSync('token')
  const userInfo = uni.getStorageSync('user')
  if (!token || !userInfo) {
    uni.navigateTo({
      url: '/pages/auth/login'
    })
  } else {
    const userData = typeof userInfo === 'object' ? userInfo : JSON.parse(userInfo)
    Object.assign(user, userData)
    if (!user.avatar || user.avatar === 'undefined') {
      user.avatar = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20portrait%20friendly&image_size=square'
    }
    fetchUserStats()
    fetchUnreadCount()
  }
}

const fetchUserStats = async () => {
  try {
    const res = await api.user.getStats(user.id)
    if (res && res.code === 200) {
      user.goodsCount = res.data.goodsCount || 0
      user.commentCount = res.data.commentCount || 0
      user.orderCount = res.data.orderCount || 0
    }
  } catch (error) {
    console.error('获取用户统计失败', error)
    user.goodsCount = 0
    user.commentCount = 0
    user.orderCount = 0
  }
}

const fetchUnreadCount = async () => {
  try {
    const res = await api.notification.list({ page: 1, pageSize: 1, status: '未送达' })
    if (res && res.code === 200) {
      unreadCount.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取未读通知数量失败', error)
    unreadCount.value = 0
  }
}

const chooseAvatar = () => {
  uni.chooseImage({
    count: 1,
    success: async (res) => {
      try {
        uni.showLoading({ title: '上传中...' })
        const tempFile = res.tempFilePaths[0]
        const uploadRes = await uni.uploadFile({
          url: 'http://localhost:8080/api/upload/image',
          filePath: tempFile,
          name: 'file',
          header: {
            'Authorization': `Bearer ${uni.getStorageSync('token')}`
          }
        })
        const result = JSON.parse(uploadRes.data)
        if (result.code === 200) {
          user.avatar = result.data.url
          uni.setStorageSync('user', { ...user })
          uni.showToast({ title: '头像上传成功', icon: 'success' })
        } else {
          uni.showToast({ title: '上传失败', icon: 'none' })
        }
      } catch (error) {
        console.error('上传失败', error)
        uni.showToast({ title: '上传失败', icon: 'none' })
      } finally {
        uni.hideLoading()
      }
    }
  })
}

const editProfile = () => {
  uni.navigateTo({
    url: '/pages/user/edit-profile'
  })
}

const goToMyGoods = () => {
  uni.navigateTo({
    url: '/pages/user/my-goods'
  })
}

const goToMyOrders = () => {
  uni.navigateTo({ url: '/pages/order/index' })
}

const goToMyFavorites = () => {
  uni.navigateTo({
    url: '/pages/user/favorites'
  })
}

const goToMyComments = () => {
  uni.navigateTo({
    url: '/pages/user/comments'
  })
}

const goToAddress = () => {
  uni.navigateTo({
    url: '/pages/user/address'
  })
}

const goToAuth = () => {
  uni.navigateTo({
    url: '/pages/user/auth'
  })
}

const goToPickupPoints = () => {
  uni.navigateTo({
    url: '/pages/user/pickup-points'
  })
}

const goToSettings = () => {
  uni.navigateTo({
    url: '/pages/user/settings'
  })
}

const goToHelp = () => {
  uni.navigateTo({
    url: '/pages/user/help'
  })
}

const goToNotifications = () => {
  uni.navigateTo({
    url: '/pages/user/notifications'
  })
}

const goToAbout = () => {
  uni.showModal({
    title: '关于我们',
    content: '校园二手市场 v1.0.0\n让闲置物品找到新主人',
    showCancel: false
  })
}

const logout = () => {
  uni.showModal({
    title: '退出登录',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        uni.removeStorageSync('token')
        uni.removeStorageSync('user')
        uni.showToast({ title: '已退出登录', icon: 'success' })
        setTimeout(() => {
          uni.navigateTo({
            url: '/pages/auth/login'
          })
        }, 1000)
      }
    }
  })
}

onMounted(() => {
  checkLoginStatus()
})


onShow(()=>{
	checkLoginStatus()
})

</script>

<style scoped>
.container {
  background-color: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 20rpx;
  overflow-x: hidden;
}

.user-info {
  background: linear-gradient(135deg, #ff4444 0%, #ff6b6b 100%);
  color: #fff;
  padding: 60rpx 30rpx 30rpx;
  display: flex;
  align-items: flex-start;
}

.avatar-wrapper {
  position: relative;
  margin-right: 20rpx;
}

.avatar {
  width: 140rpx;
  height: 140rpx;
  border-radius: 50%;
  border: 4rpx solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.15);
}

.avatar-edit {
  position: absolute;
  bottom: -5rpx;
  right: -5rpx;
  width: 48rpx;
  height: 48rpx;
  background-color: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 3rpx solid #fff;
}

.edit-icon {
  font-size: 24rpx;
}

.user-meta {
  flex: 1;
  padding-top: 10rpx;
}

.username {
  font-size: 36rpx;
  font-weight: bold;
  margin-bottom: 8rpx;
  display: block;
}

.campus {
  font-size: 24rpx;
  opacity: 0.85;
  margin-bottom: 16rpx;
  display: block;
}

.user-stats {
  display: flex;
  align-items: center;
  background-color: rgba(255, 255, 255, 0.15);
  border-radius: 20rpx;
  padding: 12rpx 20rpx;
}

.stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 30rpx;
  font-weight: bold;
}

.stat-label {
  font-size: 20rpx;
  opacity: 0.85;
  margin-top: 4rpx;
}

.stat-divider {
  width: 1rpx;
  height: 36rpx;
  background-color: rgba(255, 255, 255, 0.3);
}

.edit-btn {
  width: 120rpx;
  height: 60rpx;
  line-height: 60rpx;
  font-size: 24rpx;
  background-color: rgba(255, 255, 255, 0.2);
  color: #fff;
  border: none;
  border-radius: 30rpx;
  margin-top: 10rpx;
}

.menu-section {
  background-color: #fff;
  margin-top: 20rpx;
  margin-left: 20rpx;
  margin-right: 20rpx;
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
  padding: 10rpx 0;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 28rpx 24rpx;
  border-bottom: 1rpx solid #f5f5f5;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-icon {
  width: 44rpx;
  height: 44rpx;
  margin-right: 20rpx;
}

.menu-icon-text {
  font-size: 44rpx;
  margin-right: 20rpx;
}

.menu-text {
  flex: 1;
  font-size: 28rpx;
  color: #333;
}

.menu-arrow {
  font-size: 26rpx;
  color: #ccc;
}

.auth-badge {
  font-size: 20rpx;
  color: #ff9800;
  background-color: #fff3e0;
  padding: 4rpx 12rpx;
  border-radius: 10rpx;
  margin-right: 10rpx;
}

.notification-badge {
  font-size: 20rpx;
  color: #fff;
  background-color: #ff4444;
  padding: 4rpx 12rpx;
  border-radius: 20rpx;
  margin-right: 10rpx;
  min-width: 40rpx;
  text-align: center;
}

.logout-btn {
  width: 90%;
  height: 88rpx;
  line-height: 88rpx;
  font-size: 28rpx;
  background-color: #fff;
  color: #ff4444;
  border: 1rpx solid #ff4444;
  border-radius: 44rpx;
  margin-top: 40rpx;
  margin-left: 5%;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}
</style>