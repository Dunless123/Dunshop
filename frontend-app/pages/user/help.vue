<template>
  <view class="container">
    <view class="search-box">
      <input type="text" placeholder="搜索问题" class="search-input" />
      <text class="search-icon">🔍</text>
	  <!-- <image class="search-icon" src="/static/搜索.png" mode=""></image> -->
    </view>
    
    <view class="help-section">
      <text class="section-title">常见问题</text>
      <view class="help-item" v-for="(item, index) in commonQuestions" :key="index" @click="showAnswer(item)">
        <text class="question">{{ item.question }}</text>
        <text class="arrow">＞</text>
      </view>
    </view>
    
    <view class="help-section">
      <text class="section-title">使用指南</text>
      <view class="help-item" v-for="(item, index) in guides" :key="index" @click="showAnswer(item)">
        <text class="question">{{ item.question }}</text>
        <text class="arrow">＞</text>
      </view>
    </view>
    
    <view class="help-section">
      <text class="section-title">联系我们</text>
      <view class="contact-item">
        <text class="contact-icon">📧</text>
        <text class="contact-text">service@schoolmarket.com</text>
      </view>
      <view class="contact-item">
        <text class="contact-icon">📞</text>
        <text class="contact-text">400-123-4567</text>
      </view>
      <view class="contact-item">
        <text class="contact-icon">💬</text>
        <text class="contact-text">在线客服</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const commonQuestions = ref([
  { question: '如何发布商品？', answer: '在首页点击发布按钮，填写商品信息并上传图片即可发布商品。' },
  { question: '如何修改个人信息？', answer: '在个人中心页面点击编辑资料按钮，即可修改个人信息。' },
  { question: '如何取消订单？', answer: '在订单详情页面点击取消订单按钮，按照提示操作即可。' },
  { question: '如何申请退款？', answer: '在订单详情页面点击申请退款按钮，填写退款原因并提交。' },
  { question: '如何联系卖家？', answer: '在商品详情页面点击聊天按钮，即可与卖家进行沟通。' }
])

const guides = ref([
  { question: '新手指南', answer: '欢迎使用校园二手市场，这里是新手指南，帮助你快速上手。' },
  { question: '交易流程', answer: '交易流程包括：浏览商品、联系卖家、下单支付、确认收货。' },
  { question: '安全提示', answer: '请在交易过程中注意保护个人信息，避免线下交易风险。' },
  { question: '隐私政策', answer: '我们重视用户隐私，会严格保护用户个人信息。' }
])

const showAnswer = (item) => {
  uni.showModal({
    title: item.question,
    content: item.answer,
    showCancel: false
  })
}
</script>

<style scoped>
.container {
  background-color: #f5f5f5;
  min-height: 100vh;
  padding: 20rpx;
}

.search-box {
  position: relative;
  margin-bottom: 30rpx;
}

.search-input {
  width: 85%;
  height: 60rpx;
  background-color: #fff;
  border-radius: 30rpx;
  padding: 0 70rpx 0 30rpx;
  font-size: 26rpx;
  border: none;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.search-icon {
  position: absolute;
  right: 30rpx;
  top: 50%;
  transform: translateY(-50%);
  font-size: 28rpx;
  color: #999;
}

.help-section {
  background-color: #fff;
  border-radius: 10rpx;
  overflow: hidden;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.section-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  padding: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.help-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 25rpx 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.help-item:last-child {
  border-bottom: none;
}

.question {
  font-size: 26rpx;
  color: #333;
  flex: 1;
}

.arrow {
  font-size: 24rpx;
  color: #999;
}

.contact-item {
  display: flex;
  align-items: center;
  padding: 25rpx 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.contact-item:last-child {
  border-bottom: none;
}

.contact-icon {
  font-size: 32rpx;
  margin-right: 20rpx;
}

.contact-text {
  font-size: 26rpx;
  color: #333;
}
</style>