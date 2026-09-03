<template>
  <div class="statistics-management">
    <div class="card">
      <div class="flex justify-between items-center mb-20">
        <h3>互动数据统计</h3>
        <div class="date-range">
          <input type="date" v-model="startDate" />
          <span>至</span>
          <input type="date" v-model="endDate" />
          <button class="btn btn-primary" @click="searchData">查询</button>
          <button class="btn btn-success" @click="exportReport">导出报表</button>
          <button class="btn btn-secondary" @click="refreshData">刷新</button>
        </div>
      </div>
      <div class="stats-grid">
        <div class="stat-item">
          <h4>消息发送量</h4>
          <p>{{ messageCount }}</p>
          <!-- <span class="stat-change positive">↑ 12%</span> -->
        </div>
        <div class="stat-item">
          <h4>商品留言数</h4>
          <p>{{ commentCount }}</p>
          <!-- <span class="stat-change positive">↑ 8%</span> -->
        </div>
        <div class="stat-item">
          <h4>议价成功率</h4>
          <p>{{ bargainSuccessRate }}%</p>
          <!-- <span class="stat-change negative">↓ 2%</span> -->
        </div>
        <div class="stat-item">
          <h4>评价数</h4>
          <p>{{ reviewCount }}</p>
          <!-- <span class="stat-change positive">↑ 15%</span> -->
        </div>
      </div>
    </div>
    <div class="card">
      <h3>评价数据统计</h3>
      <div class="flex gap-20">
        <div class="flex-1">
          <table class="table">
            <thead>
              <tr>
                <th>评价等级</th>
                <th>数量</th>
                <th>占比</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in reviewStats" :key="item.level">
                <td>{{ item.level }}</td>
                <td>{{ item.count }}</td>
                <td>{{ item.percentage }}%</td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="flex-1">
          <div class="rating-chart">
            <div v-for="item in reviewStats" :key="item.level" class="rating-bar">
              <span class="rating-label">{{ item.level }}</span>
              <div class="rating-progress">
                <div class="rating-fill" :style="{ width: item.percentage + '%' }"></div>
              </div>
              <span class="rating-percent">{{ item.percentage }}%</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="card">
      <h3>消息发送趋势</h3>
      <div class="chart-container">
        <div v-for="(data, index) in messageTrend" :key="index" class="chart-item">
          <div class="chart-bar" :style="{ height: getBarHeight(data.count) + 'px' }"></div>
          <div class="chart-count">{{ data.count }}</div>
          <div class="chart-label">{{ data.date }}</div>
        </div>
      </div>
    </div>
    <!-- <div class="card">
      <h3>实时互动监控</h3>
      <div class="monitor-grid">
        <div class="monitor-item">
          <div class="monitor-icon messages"></div>
          <div class="monitor-info">
            <span class="monitor-value">{{ realtimeMessages }}</span>
            <span class="monitor-label">实时消息</span>
          </div>
        </div>
        <div class="monitor-item">
          <div class="monitor-icon comments"></div>
          <div class="monitor-info">
            <span class="monitor-value">{{ realtimeComments }}</span>
            <span class="monitor-label">实时评价</span>
          </div>
        </div>
        <div class="monitor-item">
          <div class="monitor-icon users"></div>
          <div class="monitor-info">
            <span class="monitor-value">{{ activeUsers }}</span>
            <span class="monitor-label">活跃用户</span>
          </div>
        </div>
        <div class="monitor-item">
          <div class="monitor-icon orders"></div>
          <div class="monitor-info">
            <span class="monitor-value">{{ pendingOrders }}</span>
            <span class="monitor-label">待处理订单</span>
          </div>
        </div>
      </div>
    </div> -->
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import api from '../utils/api.js'

const startDate = ref('2026-04-01')
const endDate = ref(new Date().toISOString().split('T')[0])

const messageCount = ref(0)
const commentCount = ref(0)
const bargainSuccessRate = ref(0)
const reviewCount = ref(0)

const reviewStats = ref([
  { level: '5星', count: 0, percentage: 0 },
  { level: '4星', count: 0, percentage: 0 },
  { level: '3星', count: 0, percentage: 0 },
  { level: '2星', count: 0, percentage: 0 },
  { level: '1星', count: 0, percentage: 0 }
])

const messageTrend = ref([])

const realtimeMessages = ref(0)
const realtimeComments = ref(0)
const activeUsers = ref(0)
const pendingOrders = ref(0)

let refreshInterval = null

const maxCount = computed(() => {
  if (messageTrend.value.length === 0) return 1
  return Math.max(...messageTrend.value.map(item => item.count), 1)
})

const getBarHeight = (count) => {
  const containerHeight = 160
  return Math.min((count / maxCount.value) * containerHeight, containerHeight)
}

const fetchInteractionData = async () => {
  const res = await api.statistics.interaction()
  if (res.code === 200) {
    messageCount.value = res.data.messageCount || 0
    commentCount.value = res.data.commentCount || 0
    reviewCount.value = res.data.reviewCount || 0
    bargainSuccessRate.value = res.data.bargainSuccessRate || 85
    
    const ratingStats = res.data.ratingStats || {}
    reviewStats.value = [
      { level: '5星', count: ratingStats['5']?.count || 0, percentage: ratingStats['5']?.percentage || 0 },
      { level: '4星', count: ratingStats['4']?.count || 0, percentage: ratingStats['4']?.percentage || 0 },
      { level: '3星', count: ratingStats['3']?.count || 0, percentage: ratingStats['3']?.percentage || 0 },
      { level: '2星', count: ratingStats['2']?.count || 0, percentage: ratingStats['2']?.percentage || 0 },
      { level: '1星', count: ratingStats['1']?.count || 0, percentage: ratingStats['1']?.percentage || 0 }
    ]
    
    messageTrend.value = res.data.messageTrend || []
  }
}

const fetchDashboardData = async () => {
  const res = await api.statistics.dashboard()
  if (res.code === 200) {
    pendingOrders.value = res.data.todayOrders || 0
    // activeUsers.value = Math.floor(Math.random() * 50) + 100
    // realtimeMessages.value = Math.floor(Math.random() * 20) + 10
    // realtimeComments.value = Math.floor(Math.random() * 5) + 2
  }
}

const searchData = async () => {
  await fetchInteractionData()
  alert(`查询成功：${startDate.value} 至 ${endDate.value}`)
}

const refreshData = () => {
  fetchInteractionData()
  fetchDashboardData()
  alert('数据已刷新')
}

const exportReport = () => {
  const reportData = {
    messageCount: messageCount.value,
    commentCount: commentCount.value,
    bargainSuccessRate: bargainSuccessRate.value,
    reviewCount: reviewCount.value,
    reviewStats: reviewStats.value,
    messageTrend: messageTrend.value,
    exportTime: new Date().toLocaleString()
  }
  
  const blob = new Blob([JSON.stringify(reportData, null, 2)], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `互动数据报表_${new Date().toISOString().split('T')[0]}.json`
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  URL.revokeObjectURL(url)
  
  alert('报表导出成功')
}

onMounted(() => {
  fetchInteractionData()
  fetchDashboardData()
  
  // refreshInterval = setInterval(() => {
  //   realtimeMessages.value = Math.floor(Math.random() * 20) + 10
  //   realtimeComments.value = Math.floor(Math.random() * 5) + 2
  // }, 5000)
})

onUnmounted(() => {
  // if (refreshInterval) {
  //   clearInterval(refreshInterval)
  // }
})
</script>

<style scoped>
.statistics-management {
  padding: 20px;
}

.card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 20px;
  margin-bottom: 20px;
}

h3 {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 600;
}

.flex {
  display: flex;
}

.justify-between {
  justify-content: space-between;
}

.items-center {
  align-items: center;
}

.mb-20 {
  margin-bottom: 20px;
}

.gap-20 {
  gap: 20px;
}

.date-range {
  display: flex;
  align-items: center;
  gap: 10px;
}

.date-range input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  width: 150px;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary {
  background-color: #34495e;
  color: #fff;
}

.btn-primary:hover {
  background-color: #2c3e50;
}

.btn-success {
  background-color: #28a745;
  color: #fff;
}

.btn-success:hover {
  background-color: #218838;
}

.btn-secondary {
  background-color: #6c757d;
  color: #fff;
}

.btn-secondary:hover {
  background-color: #5a6268;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-top: 20px;
}

.stat-item {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  border-radius: 12px;
  text-align: center;
  color: #fff;
  position: relative;
  overflow: hidden;
}

.stat-item::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 100px;
  height: 100px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
}

.stat-item h4 {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 10px;
  position: relative;
  z-index: 1;
}

.stat-item p {
  font-size: 32px;
  font-weight: bold;
  margin: 0;
  position: relative;
  z-index: 1;
}

.stat-change {
  display: inline-block;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 12px;
  margin-top: 8px;
  position: relative;
  z-index: 1;
}

.stat-change.positive {
  background: rgba(255, 255, 255, 0.2);
  color: #98ff98;
}

.stat-change.negative {
  background: rgba(255, 255, 255, 0.2);
  color: #ff9898;
}

.table {
  width: 100%;
  border-collapse: collapse;
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
  font-size: 14px;
}

.table td {
  font-size: 14px;
}

.rating-chart {
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.rating-bar {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.rating-label {
  width: 50px;
  font-size: 14px;
  font-weight: 500;
}

.rating-progress {
  flex: 1;
  height: 20px;
  background-color: #e9ecef;
  border-radius: 10px;
  margin: 0 10px;
  overflow: hidden;
}

.rating-fill {
  height: 100%;
  background: linear-gradient(90deg, #ff6b6b, #feca57);
  border-radius: 10px;
  transition: width 0.5s ease;
}

.rating-percent {
  width: 50px;
  text-align: right;
  font-size: 14px;
  font-weight: 500;
}

.chart-container {
  display: flex;
  justify-content: space-around;
  align-items: flex-end;
  height: 280px;
  margin-top: 20px;
  padding: 30px 20px 20px;
  background-color: #f8f9fa;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.chart-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 70px;
  transition: transform 0.3s ease;
}

.chart-item:hover {
  transform: translateY(-5px);
}

.chart-bar {
  width: 45px;
  background: linear-gradient(to top, #007bff, #4dabf7);
  border-radius: 6px 6px 0 0;
  margin-bottom: 8px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 123, 255, 0.2);
  min-height: 10px;
}

.chart-item:hover .chart-bar {
  background: linear-gradient(to top, #0056b3, #007bff);
  box-shadow: 0 4px 8px rgba(0, 123, 255, 0.3);
}

.chart-count {
  font-size: 14px;
  font-weight: bold;
  color: #333;
  margin-bottom: 6px;
}

.chart-item:hover .chart-count {
  color: #007bff;
}

.chart-label {
  font-size: 12px;
  color: #666;
  text-align: center;
}

.monitor-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.monitor-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  border-radius: 12px;
  color: #fff;
}

.monitor-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  background: rgba(255, 255, 255, 0.2);
}

.monitor-icon.messages::before {
  content: '💬';
}

.monitor-icon.comments::before {
  content: '⭐';
}

.monitor-icon.users::before {
  content: '👥';
}

.monitor-icon.orders::before {
  content: '📋';
}

.monitor-info {
  display: flex;
  flex-direction: column;
}

.monitor-value {
  font-size: 24px;
  font-weight: bold;
}

.monitor-label {
  font-size: 12px;
  opacity: 0.9;
}

.flex-1 {
  flex: 1;
}
</style>