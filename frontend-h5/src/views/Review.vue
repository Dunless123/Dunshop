<template>
  <div class="review-management">
    <div class="card">
      <div class="flex mb-20">
        <h3>评价管理</h3>
        <div class="search-box">
          <input type="text" v-model="searchKeyword" placeholder="搜索评价" @keyup.enter="searchReviews" />
          <select v-model="reviewLevel" @change="searchReviews">
            <option value="">所有等级</option>
            <option value="5">5星</option>
            <option value="4">4星</option>
            <option value="3">3星</option>
            <option value="2">2星</option>
            <option value="1">1星</option>
          </select>
          <button class="btn btn-primary" @click="searchReviews">搜索</button>
        </div>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>商品</th>
            <th>用户</th>
            <th>评价内容</th>
            <th>等级</th>
            <th>评价时间</th>
            <th>审核状态</th>
            <th>审核意见</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="review in reviews" :key="review.id">
            <td>{{ review.id }}</td>
            <td class="goods-cell">
              <div class="goods-info">
                <span class="goods-title">{{ review.goodsTitle || '-' }}</span>
                <span class="goods-price">¥{{ formatAmount(review.goodsPrice) }}</span>
              </div>
            </td>
            <td class="user-cell">
              <div class="user-info">
                <span class="user-name">{{ review.username || '-' }}</span>
                <span class="user-email">{{ review.userEmail || '-' }}</span>
              </div>
            </td>
            <td class="content-cell">{{ review.content }}</td>
            <td>
              <div class="rating-edit">
                <select :value="review.rating" @change="updateReviewRating(review, $event.target.value)" class="rating-select">
                  <option :value="1">1星</option>
                  <option :value="2">2星</option>
                  <option :value="3">3星</option>
                  <option :value="4">4星</option>
                  <option :value="5">5星</option>
                </select>
              </div>
            </td>
            <td>{{ formatTime(review.createTime) }}</td>
            <td><span :class="['status-tag', getReviewStatusClass(review.status)]">{{ review.status || '待审核' }}</span></td>
            <td>{{ review.auditComment || '-' }}</td>
            <td class="actions">
              <button class="btn btn-primary btn-sm" @click="viewReview(review)">查看</button>
              <button class="btn btn-secondary btn-sm" @click="auditReview(review)">审核</button>
              <button class="btn btn-danger btn-sm" @click="deleteReview(review)">删除</button>
            </td>
          </tr>
          <tr v-if="reviews.length === 0">
            <td colspan="9" class="empty">暂无评价数据</td>
          </tr>
        </tbody>
      </table>
      <div class="pagination-container">
        <div class="pagination">
          <button @click="prevPage" :disabled="currentPage === 1">上一页</button>
          <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
          <button @click="nextPage" :disabled="currentPage >= totalPages">下一页</button>
        </div>
      </div>
    </div>
    <div class="card">
      <h3>评价统计分析</h3>
      <div class="stats-grid">
        <div class="stat-item">
          <h4>总评价数</h4>
          <p>{{ reviewStats.totalReviews }}</p>
        </div>
        <div class="stat-item">
          <h4>平均评分</h4>
          <p>{{ reviewStats.averageRating }}星</p>
        </div>
        <div class="stat-item">
          <h4>好评率</h4>
          <p>{{ reviewStats.positiveRate }}%</p>
        </div>
        <div class="stat-item">
          <h4>差评率</h4>
          <p>{{ reviewStats.negativeRate }}%</p>
        </div>
      </div>
      <div class="chart-container">
        <div v-for="(data, index) in ratingDistribution" :key="index" class="chart-item">
          <div class="chart-bar-wrapper">
            <div class="chart-bar" :style="{ height: getBarHeight(data.count) + '%' }" :class="'rating-' + data.level"></div>
            <span class="chart-value">{{ data.count }}</span>
          </div>
          <div class="chart-label">{{ data.level }}星</div>
        </div>
      </div>
    </div>

    <!-- 评价详情弹窗 -->
    <div v-if="showDetailModal" class="modal-overlay" @click="closeDetailModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>评价详情</h3>
          <button class="close-btn" @click="closeDetailModal">×</button>
        </div>
        <div class="modal-body" v-if="currentReview">
          <div class="detail-item">
            <span class="label">评价ID：</span>
            <span>{{ currentReview.id }}</span>
          </div>
          <div class="detail-item">
            <span class="label">商品名称：</span>
            <span>{{ currentReview.goodsTitle || '-' }}</span>
          </div>
          <div class="detail-item">
            <span class="label">商品价格：</span>
            <span>¥{{ formatAmount(currentReview.goodsPrice) }}</span>
          </div>
          <div class="detail-item">
            <span class="label">评价用户：</span>
            <span>{{ currentReview.username || '-' }}</span>
          </div>
          <div class="detail-item">
            <span class="label">用户邮箱：</span>
            <span>{{ currentReview.userEmail || '-' }}</span>
          </div>
          <div class="detail-item">
            <span class="label">评价等级：</span>
            <span :class="['star-tag', 'star-' + currentReview.rating]">{{ currentReview.rating }}星</span>
          </div>
          <div class="detail-item">
            <span class="label">评价内容：</span>
            <p class="content">{{ currentReview.content }}</p>
          </div>
          <div class="detail-item">
            <span class="label">评价时间：</span>
            <span>{{ formatTime(currentReview.createTime) }}</span>
          </div>
          <div class="detail-item">
            <span class="label">审核状态：</span>
            <span :class="['status-tag', getReviewStatusClass(currentReview.status)]">{{ currentReview.status || '待审核' }}</span>
          </div>
          <div class="detail-item">
            <span class="label">审核意见：</span>
            <p class="content">{{ currentReview.auditComment || '无' }}</p>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-default" @click="closeDetailModal">关闭</button>
        </div>
      </div>
    </div>

    <!-- 审核弹窗 -->
    <div v-if="showAuditModal" class="modal-overlay" @click="closeAuditModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>审核评价</h3>
          <button class="close-btn" @click="closeAuditModal">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>评价内容</label>
            <p class="review-content">{{ auditReviewData?.content }}</p>
          </div>
          <div class="form-group">
            <label>审核结果</label>
            <div class="radio-group">
              <label>
                <input type="radio" v-model="auditResult" value="通过" /> 通过
              </label>
              <label>
                <input type="radio" v-model="auditResult" value="拒绝" /> 拒绝
              </label>
            </div>
          </div>
          <div class="form-group">
            <label>审核意见（选填）</label>
            <textarea v-model="auditComment" rows="3" placeholder="请输入审核意见"></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-default" @click="closeAuditModal">取消</button>
          <button class="btn btn-primary" @click="submitAudit">提交审核</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../utils/api.js'

const searchKeyword = ref('')
const reviewLevel = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const reviews = ref([])
const total = ref(0)
const reviewStats = ref({
  totalReviews: 0,
  averageRating: '0.0',
  positiveRate: '0.0',
  negativeRate: '0.0'
})
const ratingDistribution = ref([
  { level: 1, count: 0 },
  { level: 2, count: 0 },
  { level: 3, count: 0 },
  { level: 4, count: 0 },
  { level: 5, count: 0 }
])

// 弹窗状态
const showDetailModal = ref(false)
const showAuditModal = ref(false)
const currentReview = ref(null)
const auditReviewData = ref(null)
const auditResult = ref('')
const auditComment = ref('')

const totalPages = computed(() => {
  return Math.max(1, Math.ceil(total.value / pageSize.value))
})

const maxRatingCount = computed(() => {
  return Math.max(...ratingDistribution.value.map(d => d.count), 1)
})

const getBarHeight = (count) => {
  return Math.min((count / maxRatingCount.value) * 100, 100)
}

const getReviewStatusClass = (status) => {
  const classMap = {
    '待审核': 'status-pending',
    '通过': 'status-active',
    '拒绝': 'status-disabled'
  }
  return classMap[status] || 'status-pending'
}

const formatAmount = (amount) => {
  if (!amount) return '0.00'
  return typeof amount === 'number' ? amount.toFixed(2) : amount.toString()
}

const formatTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const fetchReviews = async () => {
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value
    }
    if (searchKeyword.value && searchKeyword.value.trim()) {
      params.keyword = searchKeyword.value.trim()
    }
    if (reviewLevel.value && reviewLevel.value.trim()) {
      params.rating = parseInt(reviewLevel.value)
    }
    
    const res = await api.comment.getAllComments(params)
    if (res.code === 200) {
      reviews.value = res.data.list || []
      total.value = res.data.total || 0
      calculateStats()
    }
  } catch (error) {
    console.error('获取评价数据失败', error)
    reviews.value = []
    total.value = 0
    calculateStats()
  }
}

const searchReviews = () => {
  currentPage.value = 1
  fetchReviews()
}

const calculateStats = () => {
  if (reviews.value.length === 0) {
    reviewStats.value = {
      totalReviews: 0,
      averageRating: '0.0',
      positiveRate: '0.0',
      negativeRate: '0.0'
    }
    ratingDistribution.value = [
      { level: 1, count: 0 },
      { level: 2, count: 0 },
      { level: 3, count: 0 },
      { level: 4, count: 0 },
      { level: 5, count: 0 }
    ]
    return
  }

  const sum = reviews.value.reduce((total, review) => total + (review.rating || 0), 0)
  const averageRating = (sum / reviews.value.length).toFixed(1)
  
  const positiveReviews = reviews.value.filter(review => (review.rating || 0) >= 4).length
  const positiveRate = ((positiveReviews / reviews.value.length) * 100).toFixed(1)
  
  const negativeReviews = reviews.value.filter(review => (review.rating || 0) <= 2).length
  const negativeRate = ((negativeReviews / reviews.value.length) * 100).toFixed(1)
  
  reviewStats.value = {
    totalReviews: total.value,
    averageRating,
    positiveRate,
    negativeRate
  }
  
  const distribution = [
    { level: 1, count: 0 },
    { level: 2, count: 0 },
    { level: 3, count: 0 },
    { level: 4, count: 0 },
    { level: 5, count: 0 }
  ]
  
  reviews.value.forEach(review => {
    const rating = review.rating || 0
    if (rating >= 1 && rating <= 5) {
      distribution[rating - 1].count++
    }
  })
  
  ratingDistribution.value = distribution
}

const viewReview = (review) => {
  currentReview.value = review
  showDetailModal.value = true
}

const closeDetailModal = () => {
  showDetailModal.value = false
  currentReview.value = null
}

const auditReview = (review) => {
  auditReviewData.value = review
  auditResult.value = ''
  auditComment.value = ''
  showAuditModal.value = true
}

const closeAuditModal = () => {
  showAuditModal.value = false
  auditReviewData.value = null
  auditResult.value = ''
  auditComment.value = ''
}

const submitAudit = async () => {
  if (!auditResult.value) {
    alert('请选择审核结果')
    return
  }
  
  try {
    const res = await api.comment.audit(auditReviewData.value.id, { 
      status: auditResult.value, 
      auditComment: auditComment.value.trim() 
    })
    if (res.code === 200) {
      alert('审核成功')
      const reviewIndex = reviews.value.findIndex(r => r.id === auditReviewData.value.id)
      if (reviewIndex !== -1) {
        reviews.value[reviewIndex].status = auditResult.value
        reviews.value[reviewIndex].auditComment = auditComment.value.trim()
      }
      closeAuditModal()
    } else {
      alert(res.message || '审核失败')
    }
  } catch (error) {
    console.error('审核评价失败', error)
    alert('审核失败: ' + (error.message || '未知错误'))
  }
}

const updateReviewRating = async (review, newRating) => {
  const rating = parseInt(newRating)
  if (rating === review.rating) return
  
  try {
    const res = await api.comment.updateRating(review.id, rating)
    if (res.code === 200) {
      review.rating = rating
      calculateStats()
      alert('评分修改成功')
    } else {
      alert(res.message || '修改失败')
      review.rating = review.rating
    }
  } catch (error) {
    console.error('修改评分失败', error)
    alert('修改失败: ' + (error.message || '未知错误'))
    review.rating = review.rating
  }
}

const deleteReview = async (review) => {
  if (!confirm(`确定要删除评价ID ${review.id} 吗？`)) {
    return
  }
  
  try {
    const res = await api.comment.delete(review.id)
    if (res.code === 200) {
      alert('删除成功')
      reviews.value = reviews.value.filter(r => r.id !== review.id)
      total.value--
      calculateStats()
    } else {
      alert(res.message || '删除失败')
    }
  } catch (error) {
    console.error('删除评价失败', error)
    alert('删除失败: ' + (error.message || '未知错误'))
  }
}

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
    fetchReviews()
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
    fetchReviews()
  }
}

onMounted(() => {
  fetchReviews()
})
</script>

<style scoped>
.review-management {
  padding: 20px;
}

.card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 25px;
  margin-bottom: 20px;
}

h3 {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.flex {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.mb-20 {
  margin-bottom: 20px;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-box input,
.search-box select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.search-box input {
  width: 200px;
}

.search-box select {
  width: 100px;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-primary {
  background-color: #34495e;
  color: #fff;
}

.btn-primary:hover:not(:disabled) {
  background-color: #2c3e50;
}

.btn-secondary {
  background-color: #6c757d;
  color: #fff;
}

.btn-secondary:hover:not(:disabled) {
  background-color: #5a6268;
}

.btn-danger {
  background-color: #dc3545;
  color: #fff;
}

.btn-danger:hover:not(:disabled) {
  background-color: #c82333;
}

.btn-default {
  background-color: #f0f0f0;
  color: #666;
}

.btn-default:hover {
  background-color: #e0e0e0;
}

.btn-sm {
  padding: 4px 10px;
  font-size: 12px;
  margin-right: 5px;
}

.table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
  font-size: 14px;
}

.table th,
.table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.table th {
  background-color: #f8f9fa;
  font-weight: 600;
  color: #666;
}

.table tbody tr:hover {
  background-color: #f8f9fa;
}

.content-cell {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.goods-cell, .user-cell {
  min-width: 150px;
}

.goods-info, .user-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.goods-title, .user-name {
  font-weight: 500;
}

.goods-price {
  color: #e74c3c;
  font-size: 12px;
}

.user-email {
  font-size: 12px;
  color: #666;
}

.empty {
  text-align: center;
  color: #999;
  padding: 40px;
}

.rating-edit {
  display: inline-block;
}

.rating-select {
  padding: 4px 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
}

.star-tag {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.star-1, .star-2 {
  background-color: #f8d7da;
  color: #721c24;
}

.star-3 {
  background-color: #fff3cd;
  color: #856404;
}

.star-4, .star-5 {
  background-color: #d4edda;
  color: #155724;
}

.status-tag {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status-active {
  background-color: #d4edda;
  color: #155724;
}

.status-pending {
  background-color: #fff3cd;
  color: #856404;
}

.actions {
  white-space: nowrap;
}

.pagination-container {
  display: flex;
  justify-content: center;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 15px;
}

.pagination button {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background: #fff;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.pagination button:hover:not(:disabled) {
  background-color: #f8f9fa;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #666;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-top: 20px;
}

.stat-item {
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
}

.stat-item h4 {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.stat-item p {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.chart-container {
  display: flex;
  justify-content: space-around;
  align-items: flex-end;
  height: 200px;
  margin-top: 20px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.chart-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 60px;
}

.chart-bar-wrapper {
  position: relative;
  width: 40px;
  height: 150px;
  display: flex;
  align-items: flex-end;
}

.chart-bar {
  width: 100%;
  border-radius: 4px 4px 0 0;
  min-height: 5px;
  transition: height 0.3s ease;
  position: relative;
}

.chart-bar.rating-1, .chart-bar.rating-2 {
  background-color: #dc3545;
}

.chart-bar.rating-3 {
  background-color: #ffc107;
}

.chart-bar.rating-4, .chart-bar.rating-5 {
  background-color: #28a745;
}

.chart-value {
  position: absolute;
  top: -20px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 12px;
  font-weight: bold;
  color: #333;
}

.chart-label {
  font-size: 12px;
  color: #666;
  margin-top: 8px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: #fff;
  border-radius: 8px;
  width: 90%;
  max-width: 450px;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  font-size: 16px;
  padding-bottom: 0;
  border-bottom: none;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #999;
  cursor: pointer;
}

.close-btn:hover {
  color: #333;
}

.modal-body {
  padding: 20px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 15px 20px;
  border-top: 1px solid #eee;
}

.detail-item {
  margin-bottom: 12px;
  font-size: 14px;
}

.detail-item .label {
  color: #666;
  margin-right: 10px;
}

.detail-item .content {
  color: #333;
  margin-top: 5px;
  white-space: pre-wrap;
  word-break: break-all;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
}

.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  resize: vertical;
}

.form-group textarea:focus {
  outline: none;
  border-color: #34495e;
}

.review-content {
  background-color: #f8f9fa;
  padding: 10px;
  border-radius: 4px;
  font-style: italic;
  color: #666;
}
</style>