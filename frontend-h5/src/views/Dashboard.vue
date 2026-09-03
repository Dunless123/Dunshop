<template>
  <div class="dashboard">
    <div class="card">
      <h3>系统概览</h3>
      <div class="stats-grid">
        <div class="stat-item">
          <h4>总商品数</h4>
          <p>{{ statistics.totalGoods }}</p>
        </div>
        <div class="stat-item">
          <h4>总订单数</h4>
          <p>{{ statistics.totalOrders }}</p>
        </div>
        <div class="stat-item">
          <h4>总交易金额</h4>
          <p>¥{{ formatNumber(statistics.totalSales) }}</p>
        </div>
        <div class="stat-item">
          <h4>总浏览量</h4>
          <p>{{ statistics.totalViews }}</p>
        </div>
        <div class="stat-item">
          <h4>今日订单</h4>
          <p>{{ statistics.todayOrders }}</p>
        </div>
        <div class="stat-item">
          <h4>今日销售额</h4>
          <p>¥{{ formatNumber(statistics.todaySales) }}</p>
        </div>
      </div>
    </div>
    <div class="card">
      <h3>最近订单</h3>
      <table class="table">
        <thead>
          <tr>
            <th>订单号</th>
            <th>用户</th>
            <th>金额</th>
            <th>状态</th>
            <th>时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in recentOrders" :key="order.id">
            <td>{{ order.orderNo }}</td>
            <td>{{ getUserName(order) }}</td>
            <td>¥{{ formatNumber(order.price) }}</td>
            <td>
              <span :class="['status-tag', getStatusClass(order.status)]">{{ getStatusText(order.status) }}</span>
            </td>
            <td>{{ formatTime(order.createTime) }}</td>
            <td>
              <button class="btn btn-primary" @click="viewOrder(order)">查看</button>
            </td>
          </tr>
          <tr v-if="recentOrders.length === 0">
            <td colspan="6" style="text-align: center; color: #999;">暂无订单数据</td>
          </tr>
        </tbody>
      </table>
      
      <div class="pagination" v-if="total > pageSize">
        <button 
          class="pagination-btn" 
          :disabled="currentPage === 1" 
          @click="prevPage">
          上一页
        </button>
        <span class="pagination-info">
          第 {{ currentPage }} / {{ totalPages }} 页，共 {{ total }} 条记录
        </span>
        <button 
          class="pagination-btn" 
          :disabled="currentPage >= totalPages" 
          @click="nextPage">
          下一页
        </button>
      </div>
    </div>
    
    <div v-if="showOrderModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>订单详情</h3>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        <div class="modal-body" v-if="selectedOrder">
          <div class="detail-row">
            <span class="label">订单号：</span>
            <span class="value">{{ selectedOrder.orderNo }}</span>
          </div>
          <div class="detail-row">
            <span class="label">买家ID：</span>
            <span class="value">{{ selectedOrder.buyerId }}</span>
          </div>
          <div class="detail-row">
            <span class="label">卖家ID：</span>
            <span class="value">{{ selectedOrder.sellerId }}</span>
          </div>
          <div class="detail-row">
            <span class="label">商品ID：</span>
            <span class="value">{{ selectedOrder.goodsId }}</span>
          </div>
          <div class="detail-row">
            <span class="label">商品名称：</span>
            <span class="value">{{ selectedOrder.goodsTitle }}</span>
          </div>
          <div class="detail-row">
            <span class="label">价格：</span>
            <span class="value">¥{{ formatNumber(selectedOrder.price) }}</span>
          </div>
          <div class="detail-row">
            <span class="label">交易方式：</span>
            <span class="value">{{ selectedOrder.tradeMethod }}</span>
          </div>
          <div class="detail-row">
            <span class="label">状态：</span>
            <span :class="['status-tag', getStatusClass(selectedOrder.status)]">{{ getStatusText(selectedOrder.status) }}</span>
          </div>
          <div class="detail-row">
            <span class="label">创建时间：</span>
            <span class="value">{{ formatTime(selectedOrder.createTime) }}</span>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-default" @click="closeModal">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import api from '../utils/api.js'

const recentOrders = ref([])
const statistics = ref({
  totalGoods: 0,
  totalOrders: 0,
  totalSales: 0,
  totalViews: 0,
  todayOrders: 0,
  todaySales: 0
})
const showOrderModal = ref(false)
const selectedOrder = ref(null)

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const totalPages = computed(() => {
  return Math.ceil(total.value / pageSize.value)
})

const formatNumber = (num) => {
  if (!num) return '0.00'
  return Number(num).toFixed(2)
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

const getUserName = (order) => {
  if (order.buyerName) {
    return order.buyerName
  }
  if (order.buyerId) {
    return '用户-' + order.buyerId
  }
  return '-'
}

const getStatusText = (status) => {
  const statusMap = {
    '待支付': '待支付',
    '已支付': '已支付',
    '已发货': '已发货',
    '已完成': '已完成',
    '已取消': '已取消'
  }
  return statusMap[status] || status
}

const getStatusClass = (status) => {
  const classMap = {
    '待支付': 'status-pending',
    '已支付': 'status-paid',
    '已发货': 'status-shipped',
    '已完成': 'status-completed',
    '已取消': 'status-canceled'
  }
  return classMap[status] || 'status-default'
}

const viewOrder = (order) => {
  selectedOrder.value = order
  showOrderModal.value = true
}

const closeModal = () => {
  showOrderModal.value = false
  selectedOrder.value = null
}

const fetchStatistics = async () => {
  try {
    const res = await api.admin.getDashboard()
    if (res.code === 200) {
      statistics.value = res.data
    }
  } catch (error) {
    console.error('获取统计数据失败', error)
  }
}

const fetchRecentOrders = async (page = 1) => {
  try {
    const res = await api.admin.getOrders({ page, pageSize: pageSize.value })
    if (res.code === 200) {
      recentOrders.value = res.data.list
      total.value = res.data.total
      currentPage.value = res.data.page
    }
  } catch (error) {
    console.error('获取订单数据失败', error)
  }
}

const prevPage = () => {
  if (currentPage.value > 1) {
    fetchRecentOrders(currentPage.value - 1)
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    fetchRecentOrders(currentPage.value + 1)
  }
}

onMounted(() => {
  fetchStatistics()
  fetchRecentOrders()
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.stat-item {
  background-color: #f8f9fa;
  padding: 25px;
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.stat-item h4 {
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
  font-weight: 500;
}

.stat-item p {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.card {
  background-color: #fff;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 25px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.card h3 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.table th,
.table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #f0f0f0;
}

.table th {
  background-color: #f8f9fa;
  font-weight: 600;
  color: #666;
}

.table tbody tr:hover {
  background-color: #f8f9fa;
}

.btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  font-size: 13px;
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

.btn-default {
  background-color: #f0f0f0;
  color: #666;
}

.btn-default:hover {
  background-color: #e0e0e0;
}

.status-tag {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-pending {
  background-color: #fff3cd;
  color: #856404;
}

.status-paid {
  background-color: #cce5ff;
  color: #004085;
}

.status-shipped {
  background-color: #d4edda;
  color: #155724;
}

.status-completed {
  background-color: #d1ecf1;
  color: #0c5460;
}

.status-canceled {
  background-color: #f8d7da;
  color: #721c24;
}

.status-default {
  background-color: #e2e3e5;
  color: #383d41;
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
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 25px;
  border-bottom: 1px solid #f0f0f0;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #999;
  cursor: pointer;
  line-height: 1;
}

.close-btn:hover {
  color: #333;
}

.modal-body {
  padding: 20px 25px;
}

.detail-row {
  display: flex;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
}

.detail-row:last-child {
  border-bottom: none;
}

.detail-row .label {
  width: 100px;
  color: #666;
  font-weight: 500;
}

.detail-row .value {
  flex: 1;
  color: #333;
}

.modal-footer {
  padding: 15px 25px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.pagination-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #fff;
  color: #333;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.pagination-btn:hover:not(:disabled) {
  background-color: #f8f9fa;
  border-color: #34495e;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-info {
  font-size: 14px;
  color: #666;
}
</style>