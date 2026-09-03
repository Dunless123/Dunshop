<template>
  <view class="container">
    <view class="form-section">
      <view class="form-item">
        <text class="label">头像</text>
        <view class="avatar-row">
          <image :src="form.avatar" class="avatar" mode="aspectFill"></image>
          <view class="avatar-action" @click="chooseAvatar">
            <text>更换头像</text>
          </view>
        </view>
      </view>
      
      <view class="form-item">
        <text class="label">昵称</text>
        <input type="text" class="input" v-model="form.username" placeholder="请输入昵称" />
      </view>
      
      <view class="form-item">
        <text class="label">学号</text>
        <input type="text" class="input" v-model="form.studentId" placeholder="请输入学号" />
      </view>
      
      <view class="form-item">
        <text class="label">手机号</text>
        <input type="number" class="input" v-model="form.phone" placeholder="请输入手机号" />
      </view>
      
      <view class="form-item">
        <text class="label">校区</text>
        <picker class="picker" :range="campusOptions" @change="handleCampusChange">
          <view class="picker-text">{{ form.campusName || '请选择校区' }}</view>
        </picker>
      </view>
      
      <view class="form-item">
        <text class="label">性别</text>
        <view class="gender-options">
          <view class="gender-option" :class="{ active: form.gender === '男' }" @click="form.gender = '男'">
            <text>男</text>
          </view>
          <view class="gender-option" :class="{ active: form.gender === '女' }" @click="form.gender = '女'">
            <text>女</text>
          </view>
          <view class="gender-option" :class="{ active: form.gender === '保密' }" @click="form.gender = '保密'">
            <text>保密</text>
          </view>
        </view>
      </view>
      
      <view class="form-item">
        <text class="label">个性签名</text>
        <textarea class="textarea" v-model="form.signature" placeholder="请输入个性签名" />
      </view>
    </view>
    
    <view class="save-section">
      <button class="save-btn" @click="saveProfile">保存</button>
    </view>
  </view>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import api from '../../utils/api.js'

const campuses = ref([])
const campusOptions = ref([])

const form = reactive({
  avatar: '',
  username: '',
  studentId: '',
  phone: '',
  campusId: 1,
  campusName: '',
  gender: '保密',
  signature: ''
})

const loadCampuses = async () => {
  try {
    const res = await api.campus.getAll()
    if (res && res.code === 200 && res.data) {
      campuses.value = res.data
      campusOptions.value = res.data.map(item => item.name)
    }
  } catch (error) {
    console.error('获取校区失败', error)
    campusOptions.value = ['清华大学', '北京大学', '复旦大学', '上海交通大学', '浙江大学']
  }
}

const loadUserInfo = async () => {
  const userInfo = uni.getStorageSync('user')
  if (userInfo) {
    const user = typeof userInfo === 'object' ? userInfo : JSON.parse(userInfo)
    form.avatar = user.avatar || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20portrait&image_size=square'
    form.username = user.username || ''
    form.studentId = user.studentId || ''
    form.phone = user.phone || ''
    form.campusId = user.campusId || 1
    form.campusName = user.campusName || campusOptions.value[0] || '请选择校区'
    form.gender = user.gender || '保密'
    form.signature = user.signature || ''
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
          form.avatar = result.data.url
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

const handleCampusChange = (e) => {
  const index = e.detail.value
  if (campuses.value[index]) {
    form.campusId = campuses.value[index].id
    form.campusName = campuses.value[index].name
  } else {
    form.campusId = index + 1
    form.campusName = campusOptions.value[index]
  }
}

const saveProfile = async () => {
  if (!form.username) {
    uni.showToast({ title: '请输入昵称', icon: 'none' })
    return
  }
  
  try {
    uni.showLoading({ title: '保存中...' })
    const res = await api.user.update({
      avatar: form.avatar,
      username: form.username,
      studentId: form.studentId,
      phone: form.phone,
      campusId: form.campusId,
      campusName: form.campusName,
      gender: form.gender,
      signature: form.signature
    })
    
    if (res && res.code === 200) {
      const userInfo = uni.getStorageSync('user')
      const user = typeof userInfo === 'object' ? userInfo : JSON.parse(userInfo)
      const updatedUser = { ...user, ...form }
      uni.setStorageSync('user', updatedUser)
      uni.hideLoading()
      uni.showToast({ title: '保存成功', icon: 'success' })
      setTimeout(() => {
        uni.navigateBack()
      }, 1000)
    } else {
      uni.hideLoading()
      uni.showToast({ title: res.message || '保存失败', icon: 'none' })
    }
  } catch (error) {
    uni.hideLoading()
    uni.showToast({ title: '网络错误', icon: 'none' })
  }
}

onMounted(async () => {
  await loadCampuses()
  loadUserInfo()
})
</script>

<style scoped>
.container {
  background-color: #f5f5f5;
  min-height: 100vh;
  overflow-x: hidden;
}

.form-section {
  padding: 24rpx;
}

.form-item {
  box-sizing: border-box;
  background-color: #fff;
  padding: 24rpx;
  margin-bottom: 20rpx;
  border-radius: 16rpx;
}

.label {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 16rpx;
  display: block;
}

.avatar-row {
  display: flex;
  align-items: center;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  margin-right: 24rpx;
}

.avatar-action {
  padding: 16rpx 32rpx;
  background-color: #f5f5f5;
  border-radius: 30rpx;
}

.avatar-action text {
  font-size: 26rpx;
  color: #666;
}

.input {
  width: 100%;
  height: 72rpx;
  border: 1rpx solid #e0e0e0;
  border-radius: 10rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}

.picker {
  width: 100%;
  height: 72rpx;
  border: 1rpx solid #e0e0e0;
  border-radius: 10rpx;
  padding: 0 20rpx;
  display: flex;
  align-items: center;
}

.picker-text {
  font-size: 28rpx;
  color: #333;
}

.gender-options {
  display: flex;
  gap: 24rpx;
}

.gender-option {
  flex: 1;
  height: 72rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1rpx solid #e0e0e0;
  border-radius: 10rpx;
  font-size: 28rpx;
  color: #666;
}

.gender-option.active {
  background-color: #ff4444;
  color: #fff;
  border-color: #ff4444;
}

.textarea {
  width: 100%;
  height: 160rpx;
  border: 1rpx solid #e0e0e0;
  border-radius: 10rpx;
  padding: 20rpx;
  font-size: 28rpx;
  resize: none;
  box-sizing: border-box;
}

.save-section {
  padding: 24rpx;
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
}

.save-btn {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  font-size: 32rpx;
  background-color: #ff4444;
  color: #fff;
  border: none;
  border-radius: 44rpx;
}
</style>