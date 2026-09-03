<template>
  <div class="notification-management">
    <div class="card">
      <h3>发送通知</h3>
      <div class="search-box">
        <select v-model="filterType">
          <option value="">所有类型</option>
          <option value="订单状态变更">订单状态变更</option>
          <option value="系统通知">系统通知</option>
          <option value="优惠活动">优惠活动</option>
        </select>
        <select v-model="filterStatus">
          <option value="">所有状态</option>
          <option value="已送达">已送达</option>
          <option value="未送达">未送达</option>
        </select>
        <button class="btn btn-primary" @click="searchNotifications">搜索</button>
      </div>
      
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>通知类型</th>
            <th>用户</th>
            <th>内容</th>
            <th>状态</th>
            <th>发送时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="notification in notifications" :key="notification.id">
            <td>{{ notification.id }}</td>
            <td>{{ notification.type }}</td>
            <td>{{ notification.username || '-' }}</td>
            <td class="content-cell">{{ notification.content }}</td>
            <td><span :class="['status-tag', notification.status]">{{ notification.status }}</span></td>
            <td>{{ formatTime(notification.sendTime) }}</td>
            <td class="actions">
              <button class="btn btn-primary btn-sm" @click="viewNotification(notification)">查看</button>
              <button class="btn btn-secondary btn-sm" @click="resendNotification(notification)">重发</button>
            </td>
          </tr>
          <tr v-if="notifications.length === 0">
            <td colspan="7" class="empty">暂无通知数据</td>
          </tr>
        </tbody>
      </table>
      
      <div class="pagination">
        <button @click="prevPage" :disabled="currentPage === 1">上一页</button>
        <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
        <button @click="nextPage" :disabled="currentPage >= totalPages">下一页</button>
      </div>
    </div>
    
    <div class="card">
      <div class="flex justify-between items-center mb-20">
        <h3>通知模板管理</h3>
        <button class="btn btn-primary" @click="openAddTemplateModal">添加模板</button>
      </div>
      
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>模板名称</th>
            <th>模板内容</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="template in templates" :key="template.id">
            <td>{{ template.id }}</td>
            <td>{{ template.name }}</td>
            <td class="content-cell">{{ template.content }}</td>
            <td class="actions">
              <button class="btn btn-primary btn-sm" @click="openEditTemplateModal(template)">编辑</button>
              <button class="btn btn-danger btn-sm" @click="confirmDeleteTemplate(template)">删除</button>
            </td>
          </tr>
          <tr v-if="templates.length === 0">
            <td colspan="4" class="empty">暂无模板数据</td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <!-- 通知详情弹窗 -->
    <div v-if="showDetailModal" class="modal-overlay" @click="closeDetailModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>通知详情</h3>
          <button class="close-btn" @click="closeDetailModal">×</button>
        </div>
        <div class="modal-body" v-if="currentNotification">
          <div class="detail-item">
            <span class="label">ID：</span>
            <span>{{ currentNotification.id }}</span>
          </div>
          <div class="detail-item">
            <span class="label">类型：</span>
            <span>{{ currentNotification.type }}</span>
          </div>
          <div class="detail-item">
            <span class="label">用户：</span>
            <span>{{ currentNotification.username || '-' }}</span>
          </div>
          <div class="detail-item">
            <span class="label">内容：</span>
            <span>{{ currentNotification.content }}</span>
          </div>
          <div class="detail-item">
            <span class="label">状态：</span>
            <span :class="['status-tag', currentNotification.status]">{{ currentNotification.status }}</span>
          </div>
          <div class="detail-item">
            <span class="label">发送时间：</span>
            <span>{{ formatTime(currentNotification.sendTime) }}</span>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-default" @click="closeDetailModal">关闭</button>
        </div>
      </div>
    </div>
    
    <!-- 添加/编辑模板弹窗 -->
    <div v-if="showTemplateModal" class="modal-overlay" @click="closeTemplateModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ editingTemplate ? '编辑模板' : '添加模板' }}</h3>
          <button class="close-btn" @click="closeTemplateModal">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>模板名称</label>
            <input type="text" v-model="templateForm.name" placeholder="请输入模板名称" />
          </div>
          <div class="form-group">
            <label>模板内容</label>
            <textarea v-model="templateForm.content" placeholder="请输入模板内容，支持占位符如 {orderNo}" rows="4"></textarea>
          </div>
          <div class="form-group">
            <label>通知类型</label>
            <select v-model="templateForm.type">
              <option value="订单状态变更">订单状态变更</option>
              <option value="系统通知">系统通知</option>
              <option value="优惠活动">优惠活动</option>
            </select>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-default" @click="closeTemplateModal">取消</button>
          <button class="btn btn-primary" @click="saveTemplate" :disabled="!templateForm.name || !templateForm.content">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../utils/api.js'

const filterType = ref('')
const filterStatus = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const notifications = ref([])
const total = ref(0)
const templates = ref([])

const showDetailModal = ref(false)
const showTemplateModal = ref(false)
const currentNotification = ref(null)
const editingTemplate = ref(null)
const templateForm = ref({
  name: '',
  content: '',
  type: '订单状态变更'
})

const totalPages = computed(() => {
  return Math.max(1, Math.ceil(total.value / pageSize.value))
})

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

const fetchNotifications = async () => {
  try {
    const res = await api.notification.list({
      page: currentPage.value,
      pageSize: pageSize.value,
      type: filterType.value,
      status: filterStatus.value
    })
    if (res.code === 200) {
      notifications.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取通知数据失败', error)
  }
}

const fetchTemplates = async () => {
  try {
    const res = await api.notification.templates()
    if (res.code === 200) {
      templates.value = res.data || []
    }
  } catch (error) {
    console.error('获取模板数据失败', error)
  }
}

const searchNotifications = () => {
  currentPage.value = 1
  fetchNotifications()
}

const viewNotification = (notification) => {
  currentNotification.value = notification
  showDetailModal.value = true
}

const closeDetailModal = () => {
  showDetailModal.value = false
  currentNotification.value = null
}

const resendNotification = async (notification) => {
  if (!confirm(`确定要重发通知给用户 ${notification.username} 吗？`)) return
  
  try {
    const res = await api.notification.resend(notification.id)
    if (res.code === 200) {
      alert('重发成功')
      fetchNotifications()
    } else {
      alert(res.message || '重发失败')
    }
  } catch (error) {
    console.error('重发通知失败', error)
    alert('重发失败')
  }
}

const openAddTemplateModal = () => {
  editingTemplate.value = null
  templateForm.value = {
    name: '',
    content: '',
    type: '订单状态变更'
  }
  showTemplateModal.value = true
}

const openEditTemplateModal = (template) => {
  editingTemplate.value = template
  templateForm.value = {
    name: template.name,
    content: template.content,
    type: template.type || '订单状态变更'
  }
  showTemplateModal.value = true
}

const closeTemplateModal = () => {
  showTemplateModal.value = false
  editingTemplate.value = null
  templateForm.value = {
    name: '',
    content: '',
    type: '订单状态变更'
  }
}

const saveTemplate = async () => {
  if (!templateForm.value.name || !templateForm.value.content) return
  
  try {
    let res
    if (editingTemplate.value) {
      res = await api.notification.updateTemplate(editingTemplate.value.id, templateForm.value)
    } else {
      res = await api.notification.addTemplate(templateForm.value)
    }
    
    if (res.code === 200) {
      alert(editingTemplate.value ? '更新成功' : '添加成功')
      closeTemplateModal()
      fetchTemplates()
    } else {
      alert(res.message || (editingTemplate.value ? '更新失败' : '添加失败'))
    }
  } catch (error) {
    console.error('保存模板失败', error)
    alert(editingTemplate.value ? '更新失败' : '添加失败')
  }
}

const confirmDeleteTemplate = async (template) => {
  if (!confirm(`确定要删除模板 "${template.name}" 吗？`)) return
  
  try {
    const res = await api.notification.deleteTemplate(template.id)
    if (res.code === 200) {
      alert('删除成功')
      fetchTemplates()
    } else {
      alert(res.message || '删除失败')
    }
  } catch (error) {
    console.error('删除模板失败', error)
    alert('删除失败')
  }
}

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
    fetchNotifications()
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
    fetchNotifications()
  }
}

onMounted(() => {
  fetchNotifications()
  fetchTemplates()
})
</script>

<style scoped>
.notification-management {
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

.search-box select {
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

.content-cell {
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.status-tag {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status-tag.已送达 {
  background-color: #d4edda;
  color: #155724;
}

.status-tag.未送达 {
  background-color: #fff3cd;
  color: #856404;
}

.empty {
  text-align: center;
  color: #999;
  padding: 40px;
}

.actions {
  white-space: nowrap;
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

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.form-group textarea {
  resize: vertical;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 15px 20px;
  border-top: 1px solid #eee;
}
</style>