<template>
  <view class="container">
    <view class="auth-header">
      <text class="auth-title">学号实名认证</text>
      <text class="auth-desc">提交学号和姓名进行实名认证</text>
    </view>
    
    <view class="auth-card" v-if="!hasAuthRecord">
      <view class="form-group">
        <text class="label">学号</text>
        <input class="input" v-model="form.studentId" placeholder="请输入学号" />
      </view>
      
      <view class="form-group">
        <text class="label">姓名</text>
        <input class="input" v-model="form.name" placeholder="请输入姓名" />
      </view>
      
      <button class="submit-btn" @click="submitAuth">提交申请</button>
    </view>
    
    <view class="status-card" v-else>
      <view class="status-icon" :class="authStatusClass">
        <text class="icon-text">{{ statusIcon }}</text>
      </view>
      <text class="status-title">{{ statusTitle }}</text>
      <text class="status-desc">{{ statusDesc }}</text>
      
      <view class="auth-info" v-if="authRecord">
        <view class="info-row">
          <text class="info-label">学号</text>
          <text class="info-value">{{ authRecord.studentId }}</text>
        </view>
        <view class="info-row">
          <text class="info-label">姓名</text>
          <text class="info-value">{{ authRecord.name }}</text>
        </view>
        <view class="info-row">
          <text class="info-label">申请时间</text>
          <text class="info-value">{{ authRecord.createTime }}</text>
        </view>
      </view>
      
      <button class="retry-btn" v-if="authRecord.status === 'rejected'" @click="retryAuth">重新申请</button>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import api from '../../utils/api.js'

const hasAuthRecord = ref(false)
const authRecord = ref(null)
const form = reactive({
  studentId: '',
  name: ''
})

const authStatusClass = computed(() => {
  const status = authRecord.value?.status || 'pending'
  switch (status) {
    case 'approved': return 'approved'
    case 'rejected': return 'rejected'
    default: return 'pending'
  }
})

const statusIcon = computed(() => {
  const status = authRecord.value?.status || 'pending'
  switch (status) {
    case 'approved':
      return 'OK'
    case 'rejected':
      return 'NO'
    default:
      return 'WAIT'
  }
})

const statusTitle = computed(() => {
  const status = authRecord.value?.status || 'pending'
  switch (status) {
    case 'approved':
      return '认证成功'
    case 'rejected':
      return '认证失败'
    default:
      return '审核中'
  }
})

const statusDesc = computed(() => {
  const status = authRecord.value?.status || 'pending'
  switch (status) {
    case 'approved':
      return '您的实名认证已通过审核'
    case 'rejected':
      return '您的实名认证申请未通过，请检查信息后重新申请'
    default:
      return '您的实名认证申请已提交，请耐心等待审核'
  }
})

const checkAuthStatus = async () => {
  try {
    const res = await api.user.getAuthStatus()
    if (res.code === 200 && res.data) {
      hasAuthRecord.value = true
      authRecord.value = res.data
    }
  } catch (error) {
    console.error('Get auth status failed', error)
  }
}

const submitAuth = async () => {
  if (!form.studentId.trim()) {
    uni.showToast({ title: '请输入学号', icon: 'none' })
    return
  }
  if (!form.name.trim()) {
    uni.showToast({ title: '请输入姓名', icon: 'none' })
    return
  }
  
  try {
    uni.showLoading({ title: '提交中...' })
    const res = await api.user.submitAuth(form.studentId, form.name)
    uni.hideLoading()
    
    if (res.code === 200) {
      uni.showToast({ title: '申请提交成功', icon: 'success' })
      hasAuthRecord.value = true
      authRecord.value = {
        studentId: form.studentId,
        name: form.name,
        status: 'pending',
        createTime: new Date().toLocaleString()
      }
    } else {
      uni.showToast({ title: res.message || '提交失败', icon: 'none' })
    }
  } catch (error) {
    uni.hideLoading()
    console.error('Submit failed', error)
    uni.showToast({ title: '提交失败', icon: 'none' })
  }
}

const retryAuth = () => {
  hasAuthRecord.value = false
  form.studentId = ''
  form.name = ''
}


onShow(() => {
  checkAuthStatus()
})

</script>

<style scoped>
.container {
  background-color: #f5f5f5;
  min-height: 100vh;
  padding: 30rpx;
}

.auth-header {
  text-align: center;
  padding: 40rpx 0;
}

.auth-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 10rpx;
}

.auth-desc {
  font-size: 26rpx;
  color: #999;
}

.auth-card {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
}

.form-group {
  margin-bottom: 25rpx;
}

.label {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 10rpx;
  display: block;
}

.input {
  width: 100%;
  height: 80rpx;
  border: 1rpx solid #e0e0e0;
  border-radius: 10rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}

.submit-btn {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  font-size: 30rpx;
  background-color: #ff4444;
  color: #fff;
  border: none;
  border-radius: 44rpx;
  margin-top: 20rpx;
}

.status-card {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 40rpx 30rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
  text-align: center;
}

.status-icon {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20rpx;
}

.status-icon.pending {
  background-color: #fff3e0;
}

.status-icon.approved {
  background-color: #e8f5e9;
}

.status-icon.rejected {
  background-color: #ffebee;
}

.icon-text {
  font-size: 32rpx;
  font-weight: bold;
}

.status-icon.pending .icon-text {
  color: #ff9800;
}

.status-icon.approved .icon-text {
  color: #4caf50;
}

.status-icon.rejected .icon-text {
  color: #f44336;
}

.status-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 10rpx;
}

.status-desc {
  font-size: 26rpx;
  color: #999;
  display: block;
}

.auth-info {
  background-color: #f9f9f9;
  border-radius: 12rpx;
  padding: 20rpx;
  margin-top: 20rpx;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 12rpx 0;
  border-bottom: 1rpx solid #eee;
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 26rpx;
  color: #999;
}

.info-value {
  font-size: 26rpx;
  color: #333;
}

.retry-btn {
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