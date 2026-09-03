<template>
  <view class="container">
    <view class="address-list" v-if="addresses.length > 0">
      <view class="address-item" v-for="(address, index) in addresses" :key="address.id">
        <view class="address-info">
          <view class="address-header">
            <text class="name">{{ address.name }}</text>
            <text class="phone">{{ address.phone }}</text>
            <text class="default-tag" v-if="address.isDefault">默认</text>
          </view>
          <text class="address-detail">{{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}</text>
        </view>
        <view class="address-actions">
          <text class="action-btn" @click="editAddress(address)">编辑</text>
          <text class="action-btn" @click="deleteAddress(address.id)">删除</text>
          <text class="action-btn" @click="setDefault(address.id)" v-if="!address.isDefault">设为默认</text>
        </view>
      </view>
    </view>
    <view class="empty-address" v-else>
      <text class="empty-text">暂无收货地址</text>
    </view>
    <button class="add-btn" @click="addAddress">+ 添加收货地址</button>
    
    <!-- 添加/编辑地址弹窗 -->
    <view class="address-popup" v-if="showAddressPopup">
      <view class="popup-content">
        <view class="popup-header">
          <text class="popup-title">{{ isEdit ? '编辑地址' : '添加地址' }}</text>
          <text class="popup-close" @click="closePopup">×</text>
        </view>
        
        <view class="form-group">
          <text class="label">收货人</text>
          <input class="input" v-model="form.name" placeholder="请输入收货人姓名" />
        </view>
        
        <view class="form-group">
          <text class="label">联系电话</text>
          <input class="input" type="number" v-model="form.phone" placeholder="请输入联系电话" />
        </view>
        
        <view class="form-group">
          <text class="label">省份</text>
          <input class="input" v-model="form.province" placeholder="请输入省份" />
        </view>
        
        <view class="form-group">
          <text class="label">城市</text>
          <input class="input" v-model="form.city" placeholder="请输入城市" />
        </view>
        
        <view class="form-group">
          <text class="label">区/县</text>
          <input class="input" v-model="form.district" placeholder="请输入区/县" />
        </view>
        
        <view class="form-group">
          <text class="label">详细地址</text>
          <textarea class="textarea" v-model="form.detail" placeholder="请输入详细地址"></textarea>
        </view>
        
        <view class="form-group">
          <view class="checkbox-group" @click="form.isDefault = !form.isDefault">
            <view class="checkbox" :class="{ checked: form.isDefault }">
              <view class="check-inner" v-if="form.isDefault">✓</view>
            </view>
            <text class="checkbox-label">设为默认地址</text>
          </view>
        </view>
        
        <view class="popup-footer">
          <button class="cancel-btn" @click="closePopup">取消</button>
          <button class="confirm-btn" @click="submitAddress">{{ isEdit ? '保存修改' : '添加地址' }}</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import api from '../../utils/api.js'

const addresses = ref([])
const showAddressPopup = ref(false)
const isEdit = ref(false)
const currentEditId = ref(null)

const form = reactive({
  name: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
  isDefault: false
})

const fetchAddresses = async () => {
  try {
    const res = await api.user.getAddresses()
    if (res.code === 200) {
      addresses.value = res.data
    }
  } catch (error) {
    console.error('获取地址列表失败', error)
  }
}

const addAddress = () => {
  isEdit.value = false
  currentEditId.value = null
  resetForm()
  showAddressPopup.value = true
}

const editAddress = (address) => {
  isEdit.value = true
  currentEditId.value = address.id
  form.name = address.name
  form.phone = address.phone
  form.province = address.province
  form.city = address.city
  form.district = address.district
  form.detail = address.detail
  form.isDefault = address.isDefault
  showAddressPopup.value = true
}

const closePopup = () => {
  showAddressPopup.value = false
  resetForm()
}

const resetForm = () => {
  form.name = ''
  form.phone = ''
  form.province = ''
  form.city = ''
  form.district = ''
  form.detail = ''
  form.isDefault = false
}

const submitAddress = async () => {
  if (!form.name.trim()) {
    uni.showToast({ title: '请输入收货人姓名', icon: 'none' })
    return
  }
  if (!form.phone.trim()) {
    uni.showToast({ title: '请输入联系电话', icon: 'none' })
    return
  }
  if (!form.province.trim()) {
    uni.showToast({ title: '请输入省份', icon: 'none' })
    return
  }
  if (!form.city.trim()) {
    uni.showToast({ title: '请输入城市', icon: 'none' })
    return
  }
  if (!form.district.trim()) {
    uni.showToast({ title: '请输入区/县', icon: 'none' })
    return
  }
  if (!form.detail.trim()) {
    uni.showToast({ title: '请输入详细地址', icon: 'none' })
    return
  }

  try {
    uni.showLoading({ title: isEdit.value ? '保存中...' : '添加中...' })
    
    let res
    if (isEdit.value) {
      res = await api.user.updateAddress(currentEditId.value, {
        name: form.name,
        phone: form.phone,
        province: form.province,
        city: form.city,
        district: form.district,
        detail: form.detail,
        isDefault: form.isDefault
      })
    } else {
      res = await api.user.addAddress({
        name: form.name,
        phone: form.phone,
        province: form.province,
        city: form.city,
        district: form.district,
        detail: form.detail,
        isDefault: form.isDefault
      })
    }
    
    uni.hideLoading()
    
    if (res.code === 200) {
      uni.showToast({ title: isEdit.value ? '修改成功' : '添加成功', icon: 'success' })
      closePopup()
      fetchAddresses()
    } else {
      uni.showToast({ title: res.message || (isEdit.value ? '修改失败' : '添加失败'), icon: 'none' })
    }
  } catch (error) {
    uni.hideLoading()
    console.error('操作失败', error)
    uni.showToast({ title: isEdit.value ? '修改失败' : '添加失败', icon: 'none' })
  }
}

const deleteAddress = async (id) => {
  uni.showModal({
    title: '删除地址',
    content: '确定要删除这个地址吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          const result = await api.user.deleteAddress(id)
          if (result.code === 200) {
            uni.showToast({ title: '地址已删除', icon: 'success' })
            fetchAddresses()
          } else {
            uni.showToast({ title: '删除失败', icon: 'none' })
          }
        } catch (error) {
          console.error('删除失败', error)
          uni.showToast({ title: '删除失败', icon: 'none' })
        }
      }
    }
  })
}

const setDefault = async (id) => {
  try {
    const res = await api.user.updateAddress(id, { isDefault: true })
    if (res.code === 200) {
      uni.showToast({ title: '已设为默认地址', icon: 'success' })
      fetchAddresses()
    } else {
      uni.showToast({ title: '设置失败', icon: 'none' })
    }
  } catch (error) {
    console.error('设置默认地址失败', error)
    uni.showToast({ title: '设置失败', icon: 'none' })
  }
}

onMounted(() => {
  fetchAddresses()
})
</script>

<style scoped>
.container {
  background-color: #f5f5f5;
  min-height: 100vh;
  padding: 20rpx;
}

.address-list {
  margin-bottom: 20rpx;
}

.address-item {
  background-color: #fff;
  border-radius: 10rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.address-header {
  display: flex;
  align-items: center;
  margin-bottom: 15rpx;
}

.name {
  font-size: 28rpx;
  font-weight: bold;
  margin-right: 20rpx;
}

.phone {
  font-size: 26rpx;
  color: #666;
  margin-right: 20rpx;
}

.default-tag {
  font-size: 20rpx;
  color: #ff4444;
  border: 1rpx solid #ff4444;
  border-radius: 10rpx;
  padding: 2rpx 10rpx;
}

.address-detail {
  font-size: 26rpx;
  color: #333;
  line-height: 40rpx;
}

.address-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 20rpx;
  border-top: 1rpx solid #f0f0f0;
  padding-top: 20rpx;
}

.action-btn {
  font-size: 24rpx;
  color: #666;
  margin-left: 30rpx;
}

.empty-address {
  text-align: center;
  padding: 100rpx 0;
  color: #999;
}

.empty-text {
  font-size: 28rpx;
}

.add-btn {
  width: 100%;
  height: 80rpx;
  line-height: 80rpx;
  font-size: 28rpx;
  background-color: #ff4444;
  color: #fff;
  border: none;
  border-radius: 40rpx;
  margin-top: 40rpx;
}

.address-popup {
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
  max-height: 85vh;
  overflow-y: auto;
}

.popup-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 30rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
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

.form-group {
  margin-bottom: 20rpx;
}

.label {
  font-size: 26rpx;
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

.textarea {
  width: 100%;
  height: 150rpx;
  border: 1rpx solid #e0e0e0;
  border-radius: 10rpx;
  padding: 20rpx;
  font-size: 28rpx;
  resize: none;
  box-sizing: border-box;
}

.checkbox-group {
  display: flex;
  align-items: center;
}

.checkbox {
  width: 36rpx;
  height: 36rpx;
  border: 2rpx solid #ccc;
  border-radius: 6rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15rpx;
}

.checkbox.checked {
  background-color: #ff4444;
  border-color: #ff4444;
}

.check-inner {
  font-size: 24rpx;
  color: #fff;
}

.checkbox-label {
  font-size: 28rpx;
  color: #333;
}

.popup-footer {
  display: flex;
  gap: 20rpx;
  margin-top: 30rpx;
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
</style>