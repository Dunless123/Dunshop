<template>
  <div class="order-management">
    <div class="card">
      <div class="flex mb-20">
        <h3>订单管理</h3>
        <div class="search-box">
          <input type="text" v-model="searchKeyword" placeholder="搜索订单号" @keyup.enter="searchOrders" />
          <select v-model="orderStatus">
            <option value="">所有状态</option>
            <option value="待付款">待付款</option>
            <option value="待发货">待发货</option>
            <option value="待收货">待收货</option>
            <option value="已完成">已完成</option>
            <option value="已取消">已取消</option>
            <option value="已退款">已退款</option>
          </select>
          <button class="btn btn-primary" @click="searchOrders">搜索</button>
        </div>
      </div>
      
      <table class="table">
        <thead>
          <tr>
            <th><input type="checkbox" v-model="selectAll" @change="handleSelectAll" /></th>
            <th>订单号</th>
            <th>用户</th>
            <th>商品</th>
            <th>金额</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in orders" :key="order.id">
            <td><input type="checkbox" v-model="order.selected" /></td>
            <td class="order-no">{{ order.orderNo }}</td>
            <td>{{ order.username || order.buyerId || '-' }}</td>
            <td>{{ order.goodsTitle || order.goodsName || '-' }}</td>
            <td class="amount">¥{{ formatAmount(order.price) }}</td>
            <td><span :class="['status-tag', order.status]">{{ order.status }}</span></td>
            <td>{{ formatTime(order.createTime) }}</td>
            <td class="actions">
              <button class="btn btn-primary btn-sm" @click="viewOrder(order)">查看</button>
              <button class="btn btn-secondary btn-sm" @click="openUpdateStatusModal(order)">更新状态</button>
              <button class="btn btn-danger btn-sm" @click="confirmRefund(order)" v-if="order.status === '已支付'">退款</button>
            </td>
          </tr>
          <tr v-if="orders.length === 0">
            <td colspan="8" class="empty">暂无订单数据</td>
          </tr>
        </tbody>
      </table>
      
      <div class="flex justify-between items-center">
        <div class="batch-actions">
          <button class="btn btn-secondary" @click="openBatchUpdateModal" :disabled="selectedOrders.length === 0">批量更新状态</button>
          <button class="btn btn-danger" @click="confirmBatchRefund" :disabled="selectedOrders.length === 0">批量退款</button>
        </div>
        <div class="pagination">
          <button @click="prevPage" :disabled="currentPage === 1">上一页</button>
          <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
          <button @click="nextPage" :disabled="currentPage >= totalPages">下一页</button>
        </div>
      </div>
    </div>
    
    <!-- 订单详情弹窗 -->
    <div v-if="showDetailModal" class="modal-overlay" @click="closeDetailModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>订单详情</h3>
          <button class="close-btn" @click="closeDetailModal">×</button>
        </div>
        <div class="modal-body" v-if="currentOrder">
          <div class="detail-item">
            <span class="label">订单号：</span>
            <span>{{ currentOrder.orderNo }}</span>
          </div>
          <div class="detail-item">
            <span class="label">用户：</span>
            <span>{{ currentOrder.username || currentOrder.buyerId || '-' }}</span>
          </div>
          <div class="detail-item">
            <span class="label">商品：</span>
            <span>{{ currentOrder.goodsTitle || currentOrder.goodsName || '-' }}</span>
          </div>
          <div class="detail-item">
            <span class="label">金额：</span>
            <span class="amount">¥{{ formatAmount(currentOrder.price) }}</span>
          </div>
          <div class="detail-item">
            <span class="label">状态：</span>
            <span :class="['status-tag', currentOrder.status]">{{ currentOrder.status }}</span>
          </div>
          <div class="detail-item">
            <span class="label">创建时间：</span>
            <span>{{ formatTime(currentOrder.createTime) }}</span>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-default" @click="closeDetailModal">关闭</button>
        </div>
      </div>
    </div>
    
    <!-- 更新状态弹窗 -->
    <div v-if="showUpdateStatusModal" class="modal-overlay" @click="closeUpdateStatusModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ updateStatusTitle }}</h3>
          <button class="close-btn" @click="closeUpdateStatusModal">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>选择新状态</label>
            <select v-model="newStatus">
              <option value="" disabled>请选择状态</option>
              <option v-for="status in availableStatuses" :key="status" :value="status">{{ status }}</option>
            </select>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-default" @click="closeUpdateStatusModal">取消</button>
          <button class="btn btn-primary" @click="confirmUpdateStatus" :disabled="!newStatus">确认</button>
        </div>
      </div>
    </div>
    
    <!-- 批量更新状态弹窗 -->
    <div v-if="showBatchUpdateModal" class="modal-overlay" @click="closeBatchUpdateModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>批量更新状态</h3>
          <button class="close-btn" @click="closeBatchUpdateModal">×</button>
        </div>
        <div class="modal-body">
          <p>已选择 {{ selectedOrders.length }} 个订单</p>
          <div class="form-group">
            <label>选择新状态</label>
            <select v-model="batchNewStatus">
              <option value="" disabled>请选择状态</option>
              <option value="待付款">待付款</option>
              <option value="待发货">待发货</option>
              <option value="待收货">待收货</option>
              <option value="已完成">已完成</option>
              <option value="已取消">已取消</option>
              <option value="已退款">已退款</option>
            </select>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-default" @click="closeBatchUpdateModal">取消</button>
          <button class="btn btn-primary" @click="confirmBatchUpdateStatus" :disabled="!batchNewStatus">确认</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../utils/api.js'

const searchKeyword = ref('')
const orderStatus = ref('')
const selectAll = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const orders = ref([])
const total = ref(0)

const showDetailModal = ref(false)
const showUpdateStatusModal = ref(false)
const showBatchUpdateModal = ref(false)
const currentOrder = ref(null)
const newStatus = ref('')
const batchNewStatus = ref('')

const totalPages = computed(() => {
  return Math.max(1, Math.ceil(total.value / pageSize.value))
})

const selectedOrders = computed(() => {
  return orders.value.filter(order => order.selected)
})

const canRefundOrders = computed(() => {
  return orders.value.filter(order => order.selected && order.status === '已支付')
})

const updateStatusTitle = computed(() => {
  if (!currentOrder.value) return '更新状态'
  return `更新订单状态 (${currentOrder.value.orderNo})`
})

const availableStatuses = computed(() => {
  if (!currentOrder.value) return []
  const status = currentOrder.value.status
  const statusMap = {
    '待付款': ['待发货', '已取消', '已退款'],
    '待发货': ['待收货', '已退款'],
    '待收货': ['已完成', '已退款'],
    '已完成': [],
    '已取消': [],
    '已退款': []
  }
  return statusMap[status] || []
})

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
    minute: '2-digit',
    second: '2-digit'
  })
}

const fetchOrders = async () => {
  try {
    const res = await api.order.list({
      page: currentPage.value,
      pageSize: pageSize.value,
      status: orderStatus.value,
      keyword: searchKeyword.value
    })
    if (res.code === 200) {
      orders.value = (res.data.list || []).map(order => ({
        ...order,
        selected: false
      }))
      total.value = res.data.total || 0
      selectAll.value = false
    }
  } catch (error) {
    console.error('获取订单数据失败', error)
  }
}

const searchOrders = () => {
  currentPage.value = 1
  fetchOrders()
}

const handleSelectAll = () => {
  orders.value.forEach(order => {
    order.selected = selectAll.value
  })
}

const viewOrder = async (order) => {
  try {
    const res = await api.order.detail(order.id)
    if (res.code === 200) {
      currentOrder.value = res.data
      showDetailModal.value = true
    }
  } catch (error) {
    console.error('获取订单详情失败', error)
  }
}

const closeDetailModal = () => {
  showDetailModal.value = false
  currentOrder.value = null
}

const openUpdateStatusModal = (order) => {
  currentOrder.value = order
  newStatus.value = ''
  showUpdateStatusModal.value = true
}

const closeUpdateStatusModal = () => {
  showUpdateStatusModal.value = false
  currentOrder.value = null
  newStatus.value = ''
}

const confirmUpdateStatus = async () => {
  if (!currentOrder.value || !newStatus.value) return
  
  try {
    const res = await api.order.updateStatus(currentOrder.value.id, { status: newStatus.value })
    if (res.code === 200) {
      alert('状态更新成功')
      closeUpdateStatusModal()
      fetchOrders()
    } else {
      alert(res.message || '更新失败')
    }
  } catch (error) {
    console.error('更新订单状态失败', error)
    alert('更新失败')
  }
}

const confirmRefund = async (order) => {
  if (!confirm(`确定要对订单 ${order.orderNo} 进行退款吗？`)) return
  
  try {
    const res = await api.order.refund(order.id)
    if (res.code === 200) {
      alert('退款成功')
      fetchOrders()
    } else {
      alert(res.message || '退款失败')
    }
  } catch (error) {
    console.error('退款失败', error)
    alert('退款失败')
  }
}

const openBatchUpdateModal = () => {
  batchNewStatus.value = ''
  showBatchUpdateModal.value = true
}

const closeBatchUpdateModal = () => {
  showBatchUpdateModal.value = false
  batchNewStatus.value = ''
}

const confirmBatchUpdateStatus = async () => {
  if (!batchNewStatus.value) return
  
  const ids = selectedOrders.value.map(order => order.id)
  try {
    const res = await api.order.batchUpdateStatus(ids, { status: batchNewStatus.value })
    if (res.code === 200) {
      alert(`成功更新 ${res.data.successCount} 个订单状态`)
      closeBatchUpdateModal()
      fetchOrders()
    } else {
      alert(res.message || '批量更新失败')
    }
  } catch (error) {
    console.error('批量更新状态失败', error)
    alert('批量更新失败')
  }
}

const confirmBatchRefund = async () => {
  const refundableOrders = selectedOrders.value.filter(order => order.status === '已支付')
  
  if (refundableOrders.length === 0) {
    alert('没有选中可退款的订单（仅已支付状态的订单可退款）')
    return
  }
  
  const nonRefundableCount = selectedOrders.value.length - refundableOrders.length
  
  let confirmMsg = `确定要对选中的 ${refundableOrders.length} 个已支付订单进行批量退款吗？`
  if (nonRefundableCount > 0) {
    confirmMsg += `\n（其中 ${nonRefundableCount} 个订单状态不是已支付，将被跳过）`
  }
  
  if (!confirm(confirmMsg)) return
  
  const ids = refundableOrders.map(order => order.id)
  try {
    const res = await api.order.batchRefund(ids)
    if (res.code === 200) {
      alert(`成功退款 ${res.data.successCount} 个订单`)
      fetchOrders()
    } else {
      alert(res.message || '批量退款失败')
    }
  } catch (error) {
    console.error('批量退款失败', error)
    alert('批量退款失败')
  }
}

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
    fetchOrders()
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
    fetchOrders()
  }
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.order-management {
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
  width: 120px;
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

.order-no {
  font-family: monospace;
  font-size: 12px;
}

.amount {
  color: #e74c3c;
  font-weight: 600;
}

.status-tag {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status-tag.待付款 {
  background-color: #fff3cd;
  color: #856404;
}

.status-tag.待发货 {
  background-color: #d4edda;
  color: #155724;
}

.status-tag.待收货 {
  background-color: #d1ecf1;
  color: #0c5460;
}

.status-tag.已完成 {
  background-color: #e8eef5;
  color: #495057;
}

.status-tag.已取消 {
  background-color: #f0f0f0;
  color: #666666;
}

.status-tag.已退款 {
  background-color: #f8d7da;
  color: #721c24;
}

.empty {
  text-align: center;
  color: #999;
  padding: 40px;
}

.actions {
  white-space: nowrap;
}

.batch-actions {
  display: flex;
  gap: 10px;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 10px;
}

.pagination button {
  padding: 6px 12px;
  border: 1px solid #ddd;
  background: #fff;
  border-radius: 4px;
  cursor: pointer;
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

.detail-item {
  margin-bottom: 12px;
  font-size: 14px;
}

.detail-item .label {
  color: #666;
  margin-right: 10px;
}

.detail-item .amount {
  color: #e74c3c;
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

.form-group select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 15px 20px;
  border-top: 1px solid #eee;
}
</style>