<template>
  <view class="container">
    <view class="form-group">
      <text class="label">商品标题</text>
      <input type="text" class="input" v-model="form.title" placeholder="请输入商品标题" />
    </view>
    
    <view class="form-group">
      <text class="label">商品描述</text>
      <textarea class="textarea" v-model="form.description" placeholder="请详细描述商品信息" />
    </view>
    
    <view class="form-group">
      <text class="label">商品价格</text>
      <input type="number" class="input" v-model="form.price" placeholder="请输入商品价格" />
    </view>
    
    <view class="form-group">
      <text class="label">原价</text>
      <input type="number" class="input" v-model="form.originalPrice" placeholder="请输入商品原价" />
    </view>
    
    <view class="form-group">
      <text class="label">商品图片</text>
      <view class="upload-container">
        <view class="upload-item" v-for="(image, index) in form.images" :key="index">
          <image :src="image" class="upload-image"></image>
          <view class="delete-btn" @click="deleteImage(index)">×</view>
        </view>
        <view class="upload-btn" @click="chooseImage" v-if="form.images.length < 5">
          <text>+</text>
        </view>
      </view>
      <text class="hint">最多上传5张图片</text>
    </view>
    
    <view class="form-group">
      <text class="label">商品分类</text>
      <picker class="picker" :range="categories" @change="handleCategoryChange">
        <view class="picker-text">{{ selectedCategory }}</view>
      </picker>
    </view>
    
    <view class="form-group">
      <text class="label">商品标签</text>
      <view class="tag-container">
        <view class="tag" v-for="tag in tags" :key="tag" :class="{ active: form.tags.includes(tag) }" @click="toggleTag(tag)">
          <text>{{ tag }}</text>
        </view>
      </view>
    </view>
    
    <view class="form-group">
      <text class="label">校区</text>
      <picker class="picker" :range="campusNames" @change="handleCampusChange">
        <view class="picker-text">{{ selectedCampus }}</view>
      </picker>
    </view>
    
    <view class="form-group">
      <text class="label">商品成色</text>
      <view class="quality-options">
        <view class="quality-option" v-for="quality in qualities" :key="quality.value" @click="toggleQuality(quality.value)">
          <view class="radio-custom" :class="{ checked: form.quality === quality.value }">
            <view class="radio-inner" v-if="form.quality === quality.value"></view>
          </view>
          <text>{{ quality.label }}</text>
        </view>
      </view>
    </view>
    
    <view class="form-group">
      <text class="label">交易方式</text>
      <checkbox-group @change="handleTradeMethodChange">
        <view class="trade-options">
          <label class="trade-option" v-for="trade in tradeMethods" :key="trade.value">
            <checkbox :value="trade.value" :checked="form.tradeMethods.includes(trade.value)" />
            <text>{{ trade.label }}</text>
          </label>
        </view>
      </checkbox-group>
    </view>
    
    <view class="button-group">
      <button class="preview-btn" @click="previewGoods">预览</button>
      <button class="submit-btn" @click="submitForm">{{ isEdit ? '保存修改' : '发布商品' }}</button>
    </view>
  </view>

  <view class="preview-modal" v-if="showPreview" @click="closePreview">
    <view class="preview-content" @click.stop>
      <view class="preview-header">
        <text class="preview-title">商品预览</text>
        <view class="close-btn" @click="closePreview">×</view>
      </view>
      
      <scroll-view scroll-y class="preview-body">
        <view class="preview-images">
          <swiper class="preview-swiper" indicator-dots indicator-color="rgba(255,255,255,0.5)" indicator-active-color="#fff">
            <swiper-item v-for="(image, index) in form.images" :key="index">
              <image :src="image" class="preview-swiper-image" mode="aspectFill"></image>
            </swiper-item>
          </swiper>
        </view>
        
        <view class="preview-info">
          <view class="preview-tags" v-if="form.tags.length > 0">
            <text class="preview-tag" v-for="tag in form.tags" :key="tag">{{ tag }}</text>
          </view>
          
          <text class="preview-price">¥{{ form.price }}</text>
          <text class="preview-original-price" v-if="form.originalPrice">¥{{ form.originalPrice }}</text>
          
          <text class="preview-title-text">{{ form.title }}</text>
          
          <view class="preview-meta">
            <text class="preview-category">{{ selectedCategory }}</text>
            <text class="preview-campus">{{ selectedCampus }}</text>
          </view>
          
          <text class="preview-quality">{{ getQualityLabel(form.quality) }}</text>
          
          <view class="preview-trade">
            <text class="trade-label">交易方式：</text>
            <text class="trade-value">{{ getTradeMethodsText() }}</text>
          </view>
          
          <view class="preview-description">
            <text class="desc-label">商品描述：</text>
            <text class="desc-content">{{ form.description }}</text>
          </view>
        </view>
      </scroll-view>
      
      <view class="preview-footer">
        <button class="back-btn" @click="closePreview">返回修改</button>
        <button class="confirm-btn" @click="submitForm">确认发布</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { onShow as onShowHook } from '@dcloudio/uni-app'
import {reactive, ref, onMounted,} from 'vue'
import api from '../../utils/api.js'

const form = reactive({
  id: '',
  title: '',
  description: '',
  price: '',
  originalPrice: '',
  images: [],
  categoryId: 1,
  tags: [],
  campusId: 1,
  quality: '',
  tradeMethods: []
})

const isEdit = ref(false)
const categories = ['数码产品', '服装鞋包', '图书教材', '运动户外', '生活用品']
const selectedCategory = ref('数码产品')

const tags = ['包邮', '急售', '可小刀', '全新', '二手', '闲置']

const campusList = ref([])
const campusNames = ref([])
const selectedCampus = ref('清华大学')

const qualities = [
  { value: '1', label: '全新' },
  { value: '2', label: '九成新' },
  { value: '3', label: '八成新' },
  { value: '4', label: '七成新' },
  { value: '5', label: '六成新及以下' }
]

const toggleQuality = (value) => {
  if (form.quality === value) {
    form.quality = ''
  } else {
    form.quality = value
  }
}

const tradeMethods = [
  { value: '1', label: '自提' },
  { value: '2', label: '快递' },
  { value: '3', label: '面交' }
]

const showPreview = ref(false)

const getQualityLabel = (value) => {
  const quality = qualities.find(q => q.value === value)
  return quality ? quality.label : ''
}

const getTradeMethodsText = () => {
  if (!Array.isArray(form.tradeMethods)) {
    return ''
  }
  return form.tradeMethods.map(value => {
    const trade = tradeMethods.find(t => t.value === value)
    return trade ? trade.label : ''
  }).join('、')
}

const chooseImage = () => {
  uni.chooseImage({
    count: 5 - form.images.length,
    success: async (res) => {
      // 上传图片
      for (let i = 0; i < res.tempFilePaths.length; i++) {
        try {
          uni.showLoading({ title: '上传图片中...' })
          const tempFile = res.tempFilePaths[i]
          const uploadTask = uni.uploadFile({
            url: 'http://localhost:8080/api/upload/image',
            filePath: tempFile,
            name: 'file',
            header: {
              'Authorization': `Bearer ${uni.getStorageSync('token')}`
            },
            success: (uploadRes) => {
              const result = JSON.parse(uploadRes.data)
              if (result.code === 200) {
                form.images.push(result.data.url)
              } else {
                uni.showToast({ title: '图片上传失败', icon: 'none' })
              }
            },
            fail: (error) => {
              console.error('上传失败', error)
              uni.showToast({ title: '图片上传失败', icon: 'none' })
            },
            complete: () => {
              uni.hideLoading()
            }
          })
        } catch (error) {
          console.error('上传失败', error)
          uni.showToast({ title: '图片上传失败', icon: 'none' })
        }
      }
    }
  })
}

const deleteImage = (index) => {
  form.images.splice(index, 1)
}

const handleCategoryChange = (e) => {
  form.categoryId = e.detail.value + 1
  selectedCategory.value = categories[e.detail.value]
}

const handleCampusChange = (e) => {
  const index = e.detail.value
  if (campusList.value[index]) {
    form.campusId = campusList.value[index].id
    selectedCampus.value = campusList.value[index].name
  } else {
    form.campusId = index + 1
    selectedCampus.value = campusNames.value[index]
  }
}

const loadCampuses = async () => {
  try {
    const res = await api.campus.getAll()
    if (res && res.code === 200 && res.data) {
      campusList.value = res.data
      campusNames.value = res.data.map(item => item.name)
      if (campusNames.value.length > 0) {
        selectedCampus.value = campusNames.value[0]
        form.campusId = campusList.value[0].id
      }
    }
  } catch (error) {
    console.error('获取校区失败', error)
    campusNames.value = ['清华大学', '北京大学', '复旦大学', '上海交通大学', '浙江大学']
    selectedCampus.value = campusNames.value[0]
  }
}

const toggleTag = (tag) => {
  const index = form.tags.indexOf(tag)
  if (index > -1) {
    form.tags.splice(index, 1)
  } else {
    form.tags.push(tag)
  }
}

const handleTradeMethodChange = (e) => {
  form.tradeMethods = e.detail.value
}

const previewGoods = () => {
  if (!form.title) {
    uni.showToast({ title: '请输入商品标题', icon: 'none' })
    return
  }
  if (!form.description) {
    uni.showToast({ title: '请输入商品描述', icon: 'none' })
    return
  }
  if (!form.price) {
    uni.showToast({ title: '请输入商品价格', icon: 'none' })
    return
  }
  if (form.images.length === 0) {
    uni.showToast({ title: '请上传商品图片', icon: 'none' })
    return
  }
  
  showPreview.value = true
}

const closePreview = () => {
  showPreview.value = false
}

const submitForm = async () => {
  if (!form.title) {
    uni.showToast({ title: '请输入商品标题', icon: 'none' })
    return
  }
  if (form.title.length < 3) {
    uni.showToast({ title: '商品标题至少3个字符', icon: 'none' })
    return
  }
  if (form.title.length > 50) {
    uni.showToast({ title: '商品标题最多50个字符', icon: 'none' })
    return
  }
  if (!form.description) {
    uni.showToast({ title: '请输入商品描述', icon: 'none' })
    return
  }
  if (form.description.length < 10) {
    uni.showToast({ title: '商品描述至少10个字符', icon: 'none' })
    return
  }
  if (form.description.length > 500) {
    uni.showToast({ title: '商品描述最多500个字符', icon: 'none' })
    return
  }
  if (!form.price) {
    uni.showToast({ title: '请输入商品价格', icon: 'none' })
    return
  }
  if (parseFloat(form.price) <= 0) {
    uni.showToast({ title: '商品价格必须大于0', icon: 'none' })
    return
  }
  if (parseFloat(form.price) > 999999) {
    uni.showToast({ title: '商品价格不能超过999999', icon: 'none' })
    return
  }
  if (form.originalPrice && parseFloat(form.originalPrice) < parseFloat(form.price)) {
    uni.showToast({ title: '原价不能低于现价', icon: 'none' })
    return
  }
  if (form.images.length === 0) {
    uni.showToast({ title: '请上传商品图片', icon: 'none' })
    return
  }
  if (form.images.length > 5) {
    uni.showToast({ title: '最多上传5张图片', icon: 'none' })
    return
  }
  if (!Array.isArray(form.tradeMethods) || form.tradeMethods.length === 0) {
    uni.showToast({ title: '请选择交易方式', icon: 'none' })
    return
  }
  if (!form.quality) {
    uni.showToast({ title: '请选择商品成色', icon: 'none' })
    return
  }
  
  try {
    uni.showLoading({ title: isEdit.value ? '保存中...' : '发布中...' })
    
    const requestData = {
      title: form.title,
      description: form.description,
      price: form.price,
      originalPrice: form.originalPrice,
      categoryId: form.categoryId,
      campusId: form.campusId,
      quality: form.quality,
      tags: form.tags,
      tradeMethods: form.tradeMethods,
      images: form.images
    }
    
    const res = isEdit.value 
      ? await api.goods.update(form.id, requestData)
      : await api.goods.publish(requestData)
    
    if (res.code === 200) {
      uni.hideLoading()
      uni.showToast({ title: isEdit.value ? '修改成功' : '发布成功', icon: 'success' })
      showPreview.value = false
      setTimeout(() => {
        uni.navigateBack()
      }, 1000)
    } else {
      uni.hideLoading()
      uni.showToast({ title: res.message || (isEdit.value ? '修改失败' : '发布失败'), icon: 'none' })
    }
  } catch (error) {
    uni.hideLoading()
    uni.showToast({ title: '网络错误，请稍后重试', icon: 'none' })
  }
}

const loadGoodsDetail = async (id) => {
  try {
    const res = await api.goods.detail(id)
    if (res && res.code === 200 && res.data) {
      const goods = res.data
      form.id = goods.id
      form.title = goods.title || ''
      form.description = goods.description || ''
      form.price = goods.price ? goods.price.toString() : ''
      form.originalPrice = goods.originalPrice ? goods.originalPrice.toString() : ''
      form.categoryId = goods.categoryId || 1
      form.campusId = goods.campusId || 1
      form.quality = goods.quality || ''
      if (goods.tradeMethods && typeof goods.tradeMethods === 'string') {
        try {
          form.tradeMethods = JSON.parse(goods.tradeMethods)
        } catch (e) {
          form.tradeMethods = []
        }
      } else {
        form.tradeMethods = goods.tradeMethods || []
      }
      
      if (goods.images && typeof goods.images === 'string') {
        try {
          form.images = JSON.parse(goods.images)
        } catch (e) {
          form.images = []
        }
      } else {
        form.images = goods.images || []
      }
      
      if (goods.tags && typeof goods.tags === 'string') {
        try {
          form.tags = JSON.parse(goods.tags)
        } catch (e) {
          form.tags = []
        }
      } else {
        form.tags = goods.tags || []
      }
      
      selectedCategory.value = categories[form.categoryId - 1] || '数码产品'
      
      const campusIndex = campusNames.value.findIndex(c => c === goods.campusName)
      if (campusIndex !== -1) {
        selectedCampus.value = campusNames.value[campusIndex]
      } else if (goods.campusName) {
        selectedCampus.value = goods.campusName
      }
    }
  } catch (error) {
    console.error('获取商品详情失败', error)
  }
}

const checkEditGoods = async () => {
  const goodsId = uni.getStorageSync('editGoodsId')
  if (goodsId) {
    uni.removeStorageSync('editGoodsId')
    isEdit.value = true
    await loadGoodsDetail(goodsId)
  } else {
    isEdit.value = false
    resetForm()
  }
}

const resetForm = () => {
  form.id = ''
  form.title = ''
  form.description = ''
  form.price = ''
  form.originalPrice = ''
  form.images = []
  form.categoryId = 1
  form.tags = []
  form.campusId = campusList.value[0]?.id || 1
  form.quality = ''
  form.tradeMethods = []
  selectedCategory.value = '数码产品'
  selectedCampus.value = campusNames.value[0] || '清华大学'
}

onMounted(() => {
  loadCampuses().then(() => {
    checkEditGoods()
  })
})

onShowHook(() => {
  const goodsId = uni.getStorageSync('editGoodsId')
  if (goodsId) {
    uni.removeStorageSync('editGoodsId')
    isEdit.value = true
    loadGoodsDetail(goodsId)
  }
})
</script>

<style scoped>
.container {
  padding: 20rpx;
  background-color: #f5f5f5;
  min-height: 100vh;
  box-sizing: border-box;
  overflow: hidden;
}

.form-group {
  background-color: #fff;
  padding: 20rpx;
  margin-bottom: 20rpx;
  border-radius: 10rpx;
  box-sizing: border-box;
}

.label {
  font-size: 28rpx;
  font-weight: bold;
  margin-bottom: 10rpx;
  display: block;
}

.input {
  width: 100%;
  height: 60rpx;
  border: 1rpx solid #e0e0e0;
  border-radius: 5rpx;
  padding: 0 20rpx;
  font-size: 26rpx;
  box-sizing: border-box;
}

.textarea {
  width: 100%;
  height: 200rpx;
  border: 1rpx solid #e0e0e0;
  border-radius: 5rpx;
  padding: 20rpx;
  font-size: 26rpx;
  resize: none;
  box-sizing: border-box;
}

.upload-container {
  display: flex;
  flex-wrap: wrap;
  margin-top: 10rpx;
}

.upload-item {
  width: 160rpx;
  height: 160rpx;
  margin-right: 20rpx;
  margin-bottom: 20rpx;
  position: relative;
}

.upload-image {
  width: 100%;
  height: 100%;
  border-radius: 5rpx;
}

.delete-btn {
  position: absolute;
  top: -10rpx;
  right: -10rpx;
  width: 40rpx;
  height: 40rpx;
  background-color: rgba(0, 0, 0, 0.5);
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  font-weight: bold;
}

.upload-btn {
  width: 160rpx;
  height: 160rpx;
  border: 2rpx dashed #e0e0e0;
  border-radius: 5rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 60rpx;
  color: #999;
}

.picker {
  /* width: 100%; */
  height: 60rpx;
  border: 1rpx solid #e0e0e0;
  border-radius: 5rpx;
  padding: 0 20rpx;
  display: flex;
  align-items: center;
  font-size: 26rpx;
}

.picker-text {
  flex: 1;
}

.quality-options {
  display: flex;
  flex-wrap: wrap;
  margin-top: 10rpx;
}

.quality-option {
  display: flex;
  align-items: center;
  margin-right: 30rpx;
  margin-bottom: 10rpx;
}

.quality-option text {
  margin-left: 10rpx;
  font-size: 26rpx;
}

.radio-custom {
  width: 32rpx;
  height: 32rpx;
  border: 2rpx solid #ccc;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.radio-custom.checked {
  border-color: #ff4444;
}

.radio-inner {
  width: 18rpx;
  height: 18rpx;
  background-color: #ff4444;
  border-radius: 50%;
}

.hint {
  font-size: 20rpx;
  color: #999;
  margin-top: 10rpx;
  display: block;
}

.tag-container {
  display: flex;
  flex-wrap: wrap;
  margin-top: 10rpx;
}

.tag {
  padding: 10rpx 20rpx;
  border: 1rpx solid #e0e0e0;
  border-radius: 20rpx;
  margin-right: 15rpx;
  margin-bottom: 10rpx;
  font-size: 24rpx;
  color: #666;
}

.tag.active {
  background-color: #ff4444;
  color: #fff;
  border-color: #ff4444;
}

.trade-options {
  display: flex;
  flex-wrap: wrap;
  margin-top: 10rpx;
}

.trade-option {
  display: flex;
  align-items: center;
  margin-right: 30rpx;
  margin-bottom: 10rpx;
}

.trade-option text {
  margin-left: 5rpx;
  font-size: 26rpx;
}

.button-group {
  display: flex;
  margin-top: 30rpx;
  gap: 20rpx;
}

.preview-btn {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  font-size: 28rpx;
  background-color: #f0f0f0;
  color: #333;
  border: 1rpx solid #e0e0e0;
  border-radius: 40rpx;
}

.submit-btn {
  flex: 2;
  height: 80rpx;
  line-height: 80rpx;
  font-size: 28rpx;
  background-color: #ff4444;
  color: #fff;
  border: none;
  border-radius: 40rpx;
}

.preview-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.7);
  z-index: 1000;
  display: flex;
  align-items: flex-end;
}

.preview-content {
  width: 100%;
  max-height: 90vh;
  background-color: #fff;
  border-radius: 30rpx 30rpx 0 0;
  display: flex;
  flex-direction: column;
}

.preview-header {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 30rpx;
  position: relative;
  border-bottom: 1rpx solid #e0e0e0;
}

.preview-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.close-btn {
  position: absolute;
  right: 30rpx;
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48rpx;
  color: #999;
}

.preview-body {
  flex: 1;
  overflow-y: auto;
}

.preview-images {
  width: 100%;
  height: 500rpx;
}

.preview-swiper {
  width: 100%;
  height: 100%;
}

.preview-swiper-image {
  width: 100%;
  height: 100%;
}

.preview-info {
  padding: 30rpx;
}

.preview-tags {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 20rpx;
}

.preview-tag {
  padding: 8rpx 16rpx;
  background-color: #fff0f0;
  color: #ff4444;
  font-size: 24rpx;
  border-radius: 20rpx;
  margin-right: 15rpx;
  margin-bottom: 10rpx;
}

.preview-price {
  font-size: 40rpx;
  font-weight: bold;
  color: #ff4444;
}

.preview-original-price {
  font-size: 26rpx;
  color: #999;
  text-decoration: line-through;
  margin-left: 15rpx;
}

.preview-title-text {
  display: block;
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-top: 20rpx;
  margin-bottom: 20rpx;
  line-height: 1.5;
}

.preview-meta {
  display: flex;
  gap: 30rpx;
  margin-bottom: 20rpx;
}

.preview-category,
.preview-campus {
  font-size: 26rpx;
  color: #666;
  padding: 8rpx 16rpx;
  background-color: #f5f5f5;
  border-radius: 8rpx;
}

.preview-quality {
  display: block;
  font-size: 26rpx;
  color: #666;
  margin-bottom: 20rpx;
}

.preview-trade {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.trade-label {
  font-size: 26rpx;
  color: #999;
}

.trade-value {
  font-size: 26rpx;
  color: #333;
}

.preview-description {
  background-color: #fafafa;
  padding: 20rpx;
  border-radius: 10rpx;
}

.desc-label {
  font-size: 26rpx;
  color: #999;
}

.desc-content {
  display: block;
  font-size: 28rpx;
  color: #333;
  line-height: 1.8;
  margin-top: 10rpx;
}

.preview-footer {
  display: flex;
  gap: 20rpx;
  padding: 20rpx 30rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  border-top: 1rpx solid #e0e0e0;
}

.back-btn {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  font-size: 28rpx;
  background-color: #f0f0f0;
  color: #333;
  border: none;
  border-radius: 40rpx;
}

.confirm-btn {
  flex: 2;
  height: 80rpx;
  line-height: 80rpx;
  font-size: 28rpx;
  background-color: #ff4444;
  color: #fff;
  border: none;
  border-radius: 40rpx;
}
</style>