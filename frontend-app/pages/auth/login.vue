<template>
  <view class="container">
    <view class="logo">
      <image src="/static/二手icon.png" class="logo-image"></image>
      <text class="app-name">校园二手市场</text>
    </view>
    
    <view class="login-form">
      <view class="form-group">
        <text class="label">学号</text>
        <input type="text" class="input" v-model="form.studentId" placeholder="请输入学号" />
      </view>
      
      <view class="form-group">
        <text class="label">密码</text>
        <input type="password" class="input" v-model="form.password" placeholder="请输入密码" />
      </view>
      
      <button class="login-btn" @click="login">登录</button>
      <text class="forgot-password" @click="goToForgot">忘记密码？</text>
    </view>
    
    <view class="other-login">
      <text class="divider">其他登录方式</text>
      <view class="login-options">
        <button class="wechat-login" @click="handleWechatLogin">
          <image class="wechat-icon" src="/static/微信.png" mode=""></image>
          <text>微信登录</text>
        </button>
      </view>
    </view>
    
    <view class="register">
      <text>还没有账号？</text>
      <text class="register-link" @click="goToRegister">立即注册</text>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import api from '../../utils/api.js'

const form = ref({
  studentId: '',
  password: ''
})

const login = async () => {
  if (!form.value.studentId) {
    uni.showToast({ title: '请输入学号', icon: 'none' })
    return
  }
  if (!form.value.password) {
    uni.showToast({ title: '请输入密码', icon: 'none' })
    return
  }
  
  try {
    uni.showLoading({ title: '登录中...' })
    const res = await api.auth.login({
      studentId: form.value.studentId,
      password: form.value.password
    })
    
    if (res.code === 200) {
      uni.setStorageSync('token', res.data.token)
      uni.setStorageSync('user', res.data.user)
      uni.showToast({ title: '登录成功', icon: 'success' })
      uni.switchTab({ url: '/pages/index/index' })
    } else {
      uni.showToast({ title: res.message || '登录失败', icon: 'none' })
    }
  } catch (error) {
    uni.showToast({ title: error.message, icon: 'none' })
  } finally {
    uni.hideLoading()
  }
}

const handleWechatLogin = async () => {
  try {
    const userProfile = await showAuthModal()
    
    if (userProfile) {
      const code = await getLoginCode()
      
      if (code) {
        await wechatAuth(code, userProfile)
      }
    }
  } catch (error) {
    console.error('微信登录失败:', error)
    uni.showToast({ title: '微信登录失败', icon: 'none' })
  }
}

const showAuthModal = async () => {
  return new Promise((resolve) => {
    uni.showModal({
      title: '微信授权',
      content: '请授权获取您的微信昵称和头像，以便完成登录',
      confirmText: '同意',
      cancelText: '拒绝',
      success: async (modalRes) => {
        if (modalRes.confirm) {
          try {
            const userProfile = await getUserProfile()
            resolve(userProfile)
          } catch (error) {
            console.log('获取用户信息失败:', error.message)
            resolve(null)
          }
        } else {
          uni.showToast({ title: '您已拒绝授权', icon: 'none' })
          resolve(null)
        }
      },
      fail: () => {
        resolve(null)
      }
    })
  })
}

const getUserProfile = async () => {
  if (typeof uni.getUserProfile !== 'function') {
    console.log('当前环境不支持 uni.getUserProfile')
    return { nickname: '微信用户_' + Date.now(), avatar: '' }
  }
  
  try {
    const res = await uni.getUserProfile({
      desc: '用于完善会员资料'
    })
    
    // console.log('uni.getUserProfile 返回:', res)
    
    if (res && res.userInfo) {
      const nickname = res.userInfo.nickName
      const avatar = res.userInfo.avatarUrl
      
      if (nickname && nickname.trim() && nickname !== '微信用户' && nickname !== '用户') {
        console.log('成功获取微信用户信息:', nickname, avatar)
        return { nickname, avatar }
      }
      
      console.log('获取到的昵称无效:', nickname)
    }
  } catch (error) {
    console.log('uni.getUserProfile 调用失败:', error.message)
  }
  
  console.log('无法获取微信用户信息，使用默认值')
  return { nickname: '微信用户_' + Date.now(), avatar: '' }
}

const getLoginCode = async () => {
  try {
    const res = await uni.login({
      provider: 'weixin'
    })
    
    if (res && res.code) {
      console.log('微信登录 - 获取临时登录凭证code成功:', res.code)
      return res.code
    }
    
    throw new Error('获取临时登录凭证失败')
  } catch (error) {
    console.log('微信登录 - 获取code失败，使用模拟code:', error.message)
    return 'TEST_CODE_' + Date.now()
  }
}

const wechatAuth = async (code, userProfile) => {
  uni.showLoading({ title: '登录中...' })
  
  try {
    const loginRes = await api.auth.wechatLogin(code)
    
    if (loginRes && loginRes.success) {
      uni.setStorageSync('token', loginRes.token)
      uni.setStorageSync('user', loginRes.user)
      uni.showToast({ title: '登录成功', icon: 'success' })
      setTimeout(() => {
        uni.switchTab({ url: '/pages/index/index' })
      }, 1500)
    } else if (loginRes && loginRes.needRegister && loginRes.openId) {
      await wechatRegister(loginRes.openId, userProfile)
    } else {
      uni.showToast({ title: loginRes && loginRes.message || '登录失败', icon: 'none' })
    }
  } catch (error) {
    console.error('微信授权失败:', error)
    uni.showToast({ title: '微信授权失败', icon: 'none' })
  } finally {
    uni.hideLoading()
  }
}

const wechatRegister = async (openId, userProfile) => {
  const nickname = userProfile?.nickname || '微信用户_' + Date.now()
  const avatar = userProfile?.avatar || ''
  
  const registerRes = await api.auth.wechatRegister(openId, nickname, avatar)
  
  if (registerRes && registerRes.success) {
    uni.setStorageSync('token', registerRes.token)
    uni.setStorageSync('user', registerRes.user)
    uni.showToast({ title: '注册登录成功', icon: 'success' })
    setTimeout(() => {
      uni.switchTab({ url: '/pages/index/index' })
    }, 1500)
  } else {
    uni.showToast({ title: registerRes && registerRes.message || '注册失败', icon: 'none' })
  }
}

const goToForgot = () => {
  uni.navigateTo({ url: '/pages/auth/forgot' })
}

const goToRegister = () => {
  uni.navigateTo({ url: '/pages/auth/register' })
}
</script>

<style scoped>
.container {
  padding: 40rpx 20rpx;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.logo {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 60rpx;
  margin-top: 100rpx;
}

.logo-image {
  width: 160rpx;
  height: 160rpx;
  margin-bottom: 20rpx;
}

.app-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.login-form {
  background-color: #fff;
  border-radius: 10rpx;
  padding: 30rpx 20rpx;
  margin-bottom: 40rpx;
}

.form-group {
  margin-bottom: 25rpx;
}

.label {
  display: block;
  font-size: 28rpx;
  font-weight: bold;
  margin-bottom: 10rpx;
  color: #333;
}

.input {
  box-sizing: border-box;
  width: 100%;
  height: 70rpx;
  border: 1rpx solid #e0e0e0;
  border-radius: 5rpx;
  padding: 0 20rpx;
  font-size: 26rpx;
}

.login-btn {
  width: 100%;
  height: 80rpx;
  line-height: 80rpx;
  font-size: 28rpx;
  background-color: #ff4444;
  color: #fff;
  border: none;
  border-radius: 40rpx;
  margin-top: 20rpx;
}

.forgot-password {
  display: block;
  text-align: right;
  margin-top: 15rpx;
  font-size: 24rpx;
  color: #ff4444;
}

.other-login {
  margin-bottom: 40rpx;
}

.divider {
  display: block;
  text-align: center;
  color: #999;
  font-size: 24rpx;
  margin-bottom: 30rpx;
  position: relative;
}

.divider::before,
.divider::after {
  content: '';
  position: absolute;
  top: 50%;
  width: 30%;
  height: 1rpx;
  background-color: #e0e0e0;
}

.divider::before {
  left: 0;
}

.divider::after {
  right: 0;
}

.login-options {
  display: flex;
  justify-content: center;
}

.wechat-login {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 80%;
  height: 80rpx;
  line-height: 80rpx;
  font-size: 28rpx;
  background-color: #07C160;
  color: #fff;
  border: none;
  border-radius: 40rpx;
}

.wechat-icon {
  width: 36rpx;
  height: 36rpx;
  font-size: 36rpx;
  margin-right: 10rpx;
}

.register {
  text-align: center;
  font-size: 24rpx;
  color: #666;
}

.register-link {
  color: #ff4444;
  margin-left: 10rpx;
}
</style>