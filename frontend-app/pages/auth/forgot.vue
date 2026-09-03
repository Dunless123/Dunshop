<template>
  <view class="container">
    <view class="forgot-form">
      <view class="form-group">
        <text class="label">学号</text>
        <input type="text" class="input" v-model="form.studentId" placeholder="请输入学号" />
      </view>
      
      <view class="form-group">
        <text class="label">新密码</text>
        <input type="password" class="input" v-model="form.newPassword" placeholder="请输入新密码（至少6位）" />
      </view>
      
      <view class="form-group">
        <text class="label">确认新密码</text>
        <input type="password" class="input" v-model="form.confirmPassword" placeholder="请再次输入新密码" />
      </view>
      
      <button class="reset-btn" @click="resetPassword">重置密码</button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import api from '../../utils/api.js'

const form = ref({
  studentId: '',
  newPassword: '',
  confirmPassword: ''
})

const resetPassword = async () => {
  if (!form.value.studentId) {
    uni.showToast({ title: '请输入学号', icon: 'none' })
    return
  }
  if (!form.value.newPassword) {
    uni.showToast({ title: '请输入新密码', icon: 'none' })
    return
  }
  if (form.value.newPassword.length < 6) {
    uni.showToast({ title: '新密码至少6位', icon: 'none' })
    return
  }
  if (form.value.newPassword !== form.value.confirmPassword) {
    uni.showToast({ title: '两次输入的密码不一致', icon: 'none' })
    return
  }
  
  try {
    uni.showLoading({ title: '重置中...' })
    const res = await api.auth.forgot({
      studentId: form.value.studentId,
      newPassword: form.value.newPassword
    })
    
    if (res.code === 200) {
      uni.showToast({ title: '密码重置成功', icon: 'success' })
      setTimeout(() => {
        uni.navigateTo({ url: '/pages/auth/login' })
      }, 1000)
    } else {
      uni.showToast({ title: res.message || '密码重置失败', icon: 'none' })
    }
  } catch (error) {
    uni.showToast({ title: '网络错误，请稍后重试', icon: 'none' })
  } finally {
    uni.hideLoading()
  }
}

</script>

<style scoped>
.container {
  background-color: #f5f5f5;
  min-height: 100vh;
}

.forgot-form {
  background-color: #fff;
  margin: 20rpx;
  border-radius: 10rpx;
  padding: 30rpx 20rpx;
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

.reset-btn {
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
</style>