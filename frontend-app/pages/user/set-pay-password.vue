<template>
  <view class="container">
    <view class="form-section">
      <text class="section-title">{{ isChange ? '修改交易密码' : '设置交易密码' }}</text>
      <view class="tip-text">请设置6位数字交易密码，用于支付确认</view>
    </view>

    <view class="form-section">
      <view class="password-section">
        <text class="password-label">{{ isChange ? '原交易密码' : '交易密码' }}</text>
        <view class="password-input-box">
          <view 
            class="password-input" 
            v-for="(item, index) in oldPassword.length" 
            :key="index"
          >
            <text>{{ oldPassword[index] }}</text>
          </view>
          <view 
            class="password-input empty" 
            v-for="(item, index) in (6 - oldPassword.length)" 
            :key="'empty-' + index"
          ></view>
        </view>
      </view>
    </view>

    <view class="form-section" v-if="isChange">
      <view class="password-section">
        <text class="password-label">新交易密码</text>
        <view class="password-input-box">
          <view 
            class="password-input" 
            v-for="(item, index) in newPassword.length" 
            :key="index"
          >
            <text>{{ newPassword[index] }}</text>
          </view>
          <view 
            class="password-input empty" 
            v-for="(item, index) in (6 - newPassword.length)" 
            :key="'empty-' + index"
          ></view>
        </view>
      </view>
    </view>

    <view class="form-section" v-if="!isChange">
      <view class="password-section">
        <text class="password-label">确认交易密码</text>
        <view class="password-input-box">
          <view 
            class="password-input" 
            v-for="(item, index) in confirmPassword.length" 
            :key="index"
          >
            <text>{{ confirmPassword[index] }}</text>
          </view>
          <view 
            class="password-input empty" 
            v-for="(item, index) in (6 - confirmPassword.length)" 
            :key="'empty-' + index"
          ></view>
        </view>
      </view>
    </view>

    <button class="submit-btn" :disabled="!canSubmit" @click="submitPassword">{{ isChange ? '确认修改' : '确认设置' }}</button>

    <view class="num-pad">
      <view class="num-btn" v-for="num in ['1','2','3','4','5','6','7','8','9','.','0','del']" :key="num" @click="handleNumInput(num)">
        <text v-if="num !== 'del'">{{ num }}</text>
        <text v-else>⌫</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import api from '../../utils/api.js'

const isChange = ref(false)
const oldPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const userId = ref(0)

const canSubmit = computed(() => {
  if (isChange.value) {
    return oldPassword.value.length === 6 && newPassword.value.length === 6
  }
  return oldPassword.value.length === 6 && confirmPassword.value.length === 6 && oldPassword.value === confirmPassword.value
})

const handleNumInput = (num) => {
  const target = isChange.value ? newPassword : confirmPassword
  
  if (num === 'del') {
    if (isChange.value) {
      if (newPassword.value.length > 0) {
        newPassword.value = newPassword.value.slice(0, -1)
      } else {
        oldPassword.value = oldPassword.value.slice(0, -1)
      }
    } else {
      if (confirmPassword.value.length > 0) {
        confirmPassword.value = confirmPassword.value.slice(0, -1)
      } else {
        oldPassword.value = oldPassword.value.slice(0, -1)
      }
    }
  } else if (num === '.') {
    return
  } else {
    if (isChange.value) {
      if (oldPassword.value.length < 6) {
        oldPassword.value += num
      } else if (newPassword.value.length < 6) {
        newPassword.value += num
      }
    } else {
      if (oldPassword.value.length < 6) {
        oldPassword.value += num
      } else if (confirmPassword.value.length < 6) {
        confirmPassword.value += num
      }
    }
  }
}

const submitPassword = async () => {
  uni.showLoading({ title: '处理中...' })
  
  try {
    const user = uni.getStorageSync('user')
    userId.value = user && user.id ? user.id : 0
    
    let result
    if (isChange.value) {
      result = await api.payPassword.change(userId.value, oldPassword.value, newPassword.value)
    } else {
      result = await api.payPassword.set(userId.value, oldPassword.value)
    }
    
    uni.hideLoading()
    
    if (result.success) {
      uni.showToast({ title: result.message, icon: 'success' })
      setTimeout(() => {
        uni.navigateBack()
      }, 1500)
    } else {
      uni.showToast({ title: result.message, icon: 'none' })
    }
  } catch (error) {
    uni.hideLoading()
    console.error('设置密码失败', error)
    uni.showToast({ title: '设置失败', icon: 'none' })
  }
}

const onLoad = (options) => {
  if (options && options.type === 'change') {
    isChange.value = true
  }
}

defineExpose({ onLoad })
</script>

<style scoped>
.container {
  background-color: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 400rpx;
}

.form-section {
  background-color: #fff;
  padding: 30rpx;
  margin-bottom: 15rpx;
}

.section-title {
  font-size: 34rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
  display: block;
}

.tip-text {
  font-size: 26rpx;
  color: #999;
}

.password-section {
  margin-top: 20rpx;
}

.password-label {
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
  background-color: #f8f8f8;
}

.password-input.empty {
  background-color: #fff;
}

.submit-btn {
  width: 90%;
  height: 88rpx;
  line-height: 88rpx;
  font-size: 32rpx;
  background-color: #ff4444;
  color: #fff;
  border: none;
  border-radius: 44rpx;
  margin: 30rpx auto;
  display: block;
}

.submit-btn:disabled {
  background-color: #ccc;
}

.num-pad {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  flex-wrap: wrap;
  background-color: #fff;
  border-top: 1rpx solid #e0e0e0;
  padding-bottom: env(safe-area-inset-bottom);
}

.num-btn {
  width: 25%;
  height: 100rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  color: #333;
  border-bottom: 1rpx solid #e0e0e0;
  border-right: 1rpx solid #e0e0e0;
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