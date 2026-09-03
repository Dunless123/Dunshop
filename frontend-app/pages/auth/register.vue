<template>
  <view class="container">
    <view class="register-form">
      <view class="form-group">
        <text class="label">学号</text>
        <input type="text" class="input" v-model="form.studentId" placeholder="请输入学号" />
      </view>
      
      <view class="form-group">
        <text class="label">姓名</text>
        <input type="text" class="input" v-model="form.name" placeholder="请输入姓名" />
      </view>
      
      <view class="form-group">
        <text class="label">密码</text>
        <input type="password" class="input" v-model="form.password" placeholder="请输入密码（至少6位）" />
      </view>
      
      <view class="form-group">
        <text class="label">确认密码</text>
        <input type="password" class="input" v-model="form.confirmPassword" placeholder="请再次输入密码" />
      </view>
      
      <view class="form-group">
        <text class="label">手机号</text>
        <input type="tel" class="input" v-model="form.phone" placeholder="请输入手机号" />
      </view>
      
      <view class="form-group">
        <text class="label">邮箱</text>
        <input type="email" class="input" v-model="form.email" placeholder="请输入邮箱" />
      </view>
      
      <view class="form-group">
        <text class="label">校区</text>
        <picker class="picker" :range="campuses.map(c => c.name)" @change="handleCampusChange">
          <view class="picker-text">{{ selectedCampus }}</view>
        </picker>
      </view>
      
      <button class="register-btn" @click="register">注册</button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../../utils/api.js'

const form = ref({
  studentId: '',
  name: '',
  password: '',
  confirmPassword: '',
  campusId: 1,
  phone: '',
  email: ''
})

const campuses = ref([])
const selectedCampus = ref('')

const register = async () => {
  if (!form.value.studentId) {
    uni.showToast({ title: '请输入学号', icon: 'none' })
    return
  }
  if (!form.value.name) {
    uni.showToast({ title: '请输入姓名', icon: 'none' })
    return
  }
  if (!form.value.password) {
    uni.showToast({ title: '请输入密码', icon: 'none' })
    return
  }
  if (form.value.password.length < 6) {
    uni.showToast({ title: '密码至少6位', icon: 'none' })
    return
  }
  if (form.value.password !== form.value.confirmPassword) {
    uni.showToast({ title: '两次输入的密码不一致', icon: 'none' })
    return
  }
  
  try {
    uni.showLoading({ title: '注册中...' })
    const res = await api.auth.register({
      studentId: form.value.studentId,
      name: form.value.name,
      password: form.value.password,
      phone: form.value.phone,
      email: form.value.email,
      campusId: form.value.campusId
    })
    
    if (res.code === 200) {
      uni.showToast({ title: '注册成功', icon: 'success' })
      setTimeout(() => {
        uni.navigateTo({ url: '/pages/auth/login' })
      }, 1000)
    } else {
      uni.showToast({ title: res.message || '注册失败', icon: 'none' })
    }
  } catch (error) {
    uni.showToast({ title: '网络错误，请稍后重试', icon: 'none' })
  } finally {
    uni.hideLoading()
  }
}

const handleCampusChange = (e) => {
  form.value.campusId = campuses.value[e.detail.value].id
  selectedCampus.value = campuses.value[e.detail.value].name
}

onMounted(async () => {
  try {
    const res = await api.campus.list()
    if (res.code === 200) {
      campuses.value = res.data
      if (campuses.value.length > 0) {
        form.value.campusId = campuses.value[0].id
        selectedCampus.value = campuses.value[0].name
      }
    }
  } catch (error) {
    console.error('获取校区列表失败', error)
  }
})
</script>

<style scoped>
.container {
  background-color: #f5f5f5;
  min-height: 100vh;
}

.register-form {
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

.picker {
  box-sizing: border-box;
  width: 100%;
  height: 70rpx;
  border: 1rpx solid #e0e0e0;
  border-radius: 5rpx;
  padding: 0 20rpx;
  display: flex;
  align-items: center;
  font-size: 26rpx;
}

.picker-text {
  flex: 1;
  color: #333;
}

.register-btn {
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