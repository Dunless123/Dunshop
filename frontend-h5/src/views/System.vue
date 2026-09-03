<template>
  <div class="system-management">
    <div class="card">
      <h3>系统管理</h3>
      <div class="tab-container">
        <div class="tabs">
          <button :class="{ active: activeTab === 'campus' }" @click="activeTab = 'campus'; loadCampuses()">校区信息管理</button>
          <button :class="{ active: activeTab === 'pickup' }" @click="activeTab = 'pickup'; loadPickups()">自提点设置</button>
          <button :class="{ active: activeTab === 'log' }" @click="activeTab = 'log'; loadOperationLogs()">操作日志</button>
          <button :class="{ active: activeTab === 'file' }" @click="activeTab = 'file'; loadFiles()">文件存储管理</button>
        </div>
        <div class="tab-content">
          <!-- 校区信息管理 -->
          <div v-if="activeTab === 'campus'">
            <div class="flex mb-20">
              <button class="btn btn-success" @click="showCampusModal = true; resetCampusForm()">添加校区</button>
            </div>
            <table class="table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>校区名称</th>
                  <th>创建时间</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="campus in campuses" :key="campus.id">
                  <td>{{ campus.id }}</td>
                  <td>{{ campus.name }}</td>
                  <td>{{ formatTime(campus.createTime) }}</td>
                  <td>
                    <button class="btn btn-primary btn-sm" @click="editCampus(campus)">编辑</button>
                    <button class="btn btn-danger btn-sm" @click="deleteCampus(campus)">删除</button>
                  </td>
                </tr>
                <tr v-if="campuses.length === 0">
                  <td colspan="4" class="empty">暂无校区数据</td>
                </tr>
              </tbody>
            </table>
          </div>
          <!-- 自提点设置 -->
          <div v-if="activeTab === 'pickup'">
            <div class="flex mb-20">
              <button class="btn btn-success" @click="showPickupModal = true; resetPickupForm()">添加自提点</button>
            </div>
            <table class="table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>自提点名称</th>
                  <th>所属校区</th>
                  <th>地址</th>
                  <th>联系电话</th>
                  <th>状态</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="pickup in pickups" :key="pickup.id">
                  <td>{{ pickup.id }}</td>
                  <td>{{ pickup.name }}</td>
                  <td>{{ pickup.campusName || '-' }}</td>
                  <td>{{ pickup.address }}</td>
                  <td>{{ pickup.phone }}</td>
                  <td><span :class="['status-tag', pickup.status === 1 ? 'status-active' : 'status-inactive']">{{ pickup.status === 1 ? '启用' : '禁用' }}</span></td>
                  <td>
                    <button class="btn btn-primary btn-sm" @click="editPickup(pickup)">编辑</button>
                    <button class="btn btn-danger btn-sm" @click="deletePickup(pickup)">删除</button>
                  </td>
                </tr>
                <tr v-if="pickups.length === 0">
                  <td colspan="7" class="empty">暂无自提点数据</td>
                </tr>
              </tbody>
            </table>
          </div>
          <!-- 操作日志 -->
          <div v-if="activeTab === 'log'">
            <div class="flex mb-20">
              <div class="search-box">
                <input type="text" v-model="logSearch" placeholder="搜索日志" />
                <input type="date" v-model="logDate" />
                <button class="btn btn-primary" @click="searchLogs">搜索</button>
              </div>
            </div>
            <table class="table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>操作人</th>
                  <th>操作类型</th>
                  <th>请求路径</th>
                  <th>IP地址</th>
                  <th>操作时间</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="log in operationLogs" :key="log.id">
                  <td>{{ log.id }}</td>
                  <td>{{ log.username }}</td>
                  <td><span :class="['type-tag', getTypeClass(log.type)]">{{ log.type }}</span></td>
                  <td class="path-cell">{{ getPathFromContent(log.content) }}</td>
                  <td>{{ log.ip }}</td>
                  <td>{{ formatTime(log.createTime) }}</td>
                  <td><button class="btn btn-primary btn-sm" @click="showLogDetail(log)">详情</button></td>
                </tr>
                <tr v-if="operationLogs.length === 0">
                  <td colspan="7" class="empty">暂无操作日志</td>
                </tr>
              </tbody>
            </table>
            <div v-if="logTotal > 0" class="pagination">
              <button class="btn btn-default" :disabled="logPage <= 1" @click="prevLogPage">上一页</button>
              <span>第 {{ logPage }} / {{ Math.ceil(logTotal / logSize) }} 页</span>
              <button class="btn btn-default" :disabled="logPage >= Math.ceil(logTotal / logSize)" @click="nextLogPage">下一页</button>
            </div>
          </div>
          <!-- 文件存储管理 -->
          <div v-if="activeTab === 'file'">
            <div class="flex mb-20">
              <button class="btn btn-primary" @click="triggerFileUpload">上传文件</button>
              <input type="file" ref="fileInput" class="hidden" @change="handleFileUpload" />
            </div>
            <table class="table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>文件名</th>
                  <th>原始名称</th>
                  <th>文件大小</th>
                  <th>文件类型</th>
                  <th>上传时间</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="file in files" :key="file.id">
                  <td>{{ file.id }}</td>
                  <td>{{ file.fileName }}</td>
                  <td>{{ file.originalName }}</td>
                  <td>{{ formatFileSize(file.fileSize) }}</td>
                  <td>{{ file.fileType }}</td>
                  <td>{{ formatTime(file.createTime) }}</td>
                  <td>
                    <button class="btn btn-primary btn-sm" @click="downloadFile(file)">下载</button>
                    <button class="btn btn-danger btn-sm" @click="deleteFile(file)">删除</button>
                  </td>
                </tr>
                <tr v-if="files.length === 0">
                  <td colspan="7" class="empty">暂无文件数据</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

    <!-- 校区弹窗 -->
    <div v-if="showCampusModal" class="modal-overlay" @click="closeCampusModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ editingCampus ? '编辑校区' : '添加校区' }}</h3>
          <button class="close-btn" @click="closeCampusModal">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>校区名称</label>
            <input type="text" v-model="campusForm.name" placeholder="请输入校区名称" />
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-default" @click="closeCampusModal">取消</button>
          <button class="btn btn-primary" @click="saveCampus">{{ editingCampus ? '保存修改' : '添加' }}</button>
        </div>
      </div>
    </div>

    <!-- 自提点弹窗 -->
    <div v-if="showPickupModal" class="modal-overlay" @click="closePickupModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ editingPickup ? '编辑自提点' : '添加自提点' }}</h3>
          <button class="close-btn" @click="closePickupModal">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>自提点名称</label>
            <input type="text" v-model="pickupForm.name" placeholder="请输入自提点名称" />
          </div>
          <div class="form-group">
            <label>所属校区</label>
            <select v-model="pickupForm.campusId" @change="updateCampusName">
              <option value="">请选择校区</option>
              <option v-for="campus in campuses" :key="campus.id" :value="campus.id">{{ campus.name }}</option>
            </select>
          </div>
          <div class="form-group">
            <label>地址</label>
            <input type="text" v-model="pickupForm.address" placeholder="请输入地址" />
          </div>
          <div class="form-group">
            <label>联系电话</label>
            <input type="text" v-model="pickupForm.phone" placeholder="请输入联系电话" />
          </div>
          <div class="form-group">
            <label>状态</label>
            <select v-model="pickupForm.status">
              <option :value="1">启用</option>
              <option :value="0">禁用</option>
            </select>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-default" @click="closePickupModal">取消</button>
          <button class="btn btn-primary" @click="savePickup">{{ editingPickup ? '保存修改' : '添加' }}</button>
        </div>
      </div>
    </div>

    <!-- 操作日志详情弹窗 -->
    <div v-if="showLogDetailModal" class="modal-overlay" @click="closeLogDetailModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>操作日志详情</h3>
          <button class="close-btn" @click="closeLogDetailModal">×</button>
        </div>
        <div class="modal-body" v-if="currentLog">
          <div class="detail-item">
            <span class="detail-label">ID:</span>
            <span class="detail-value">{{ currentLog.id }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">操作人:</span>
            <span class="detail-value">{{ currentLog.username }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">操作类型:</span>
            <span :class="['detail-value', 'type-tag', getTypeClass(currentLog.type)]">{{ currentLog.type }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">IP地址:</span>
            <span class="detail-value">{{ currentLog.ip }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">操作时间:</span>
            <span class="detail-value">{{ formatTime(currentLog.createTime) }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">详细内容:</span>
            <pre class="detail-content">{{ currentLog.content }}</pre>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-primary" @click="closeLogDetailModal">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../utils/api.js'

const activeTab = ref('campus')

// 校区信息
const campuses = ref([])
const showCampusModal = ref(false)
const editingCampus = ref(null)
const campusForm = ref({
  name: ''
})

// 自提点
const pickups = ref([])
const showPickupModal = ref(false)
const editingPickup = ref(null)
const pickupForm = ref({
  name: '',
  campusId: '',
  campusName: '',
  address: '',
  phone: '',
  status: 1
})

// 操作日志
const logSearch = ref('')
const logDate = ref('')
const operationLogs = ref([])
const logPage = ref(1)
const logSize = ref(20)
const logTotal = ref(0)
const showLogDetailModal = ref(false)
const currentLog = ref(null)

// 文件存储
const files = ref([])
const fileInput = ref(null)

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

const formatFileSize = (size) => {
  if (!size) return '0 B'
  if (size < 1024) return size + ' B'
  if (size < 1024 * 1024) return (size / 1024).toFixed(1) + ' KB'
  return (size / (1024 * 1024)).toFixed(1) + ' MB'
}

// 校区操作
const loadCampuses = async () => {
  try {
    const res = await api.system.getCampusList()
    if (res.code === 200) {
      campuses.value = res.data || []
    }
  } catch (error) {
    console.error('获取校区列表失败', error)
    campuses.value = []
  }
}

const resetCampusForm = () => {
  editingCampus.value = null
  campusForm.value = { name: '' }
}

const closeCampusModal = () => {
  showCampusModal.value = false
  resetCampusForm()
}

const editCampus = (campus) => {
  editingCampus.value = campus
  campusForm.value = { name: campus.name }
  showCampusModal.value = true
}

const saveCampus = async () => {
  if (!campusForm.value.name.trim()) {
    alert('请输入校区名称')
    return
  }
  
  try {
    let res
    if (editingCampus.value) {
      res = await api.system.updateCampus(editingCampus.value.id, campusForm.value)
    } else {
      res = await api.system.addCampus(campusForm.value)
    }
    
    if (res.code === 200) {
      alert(editingCampus.value ? '更新成功' : '添加成功')
      loadCampuses()
      closeCampusModal()
    } else {
      alert(res.message || '操作失败')
    }
  } catch (error) {
    console.error('校区操作失败', error)
    alert('操作失败: ' + (error.message || '未知错误'))
  }
}

const deleteCampus = async (campus) => {
  if (!confirm(`确定要删除校区 "${campus.name}" 吗？`)) {
    return
  }
  
  try {
    const res = await api.system.deleteCampus(campus.id)
    if (res.code === 200) {
      alert('删除成功')
      loadCampuses()
    } else {
      alert(res.message || '删除失败')
    }
  } catch (error) {
    console.error('删除校区失败', error)
    alert('删除失败: ' + (error.message || '未知错误'))
  }
}

// 自提点操作
const loadPickups = async () => {
  try {
    const res = await api.system.getPickupList()
    if (res.code === 200) {
      pickups.value = res.data || []
    }
  } catch (error) {
    console.error('获取自提点列表失败', error)
    pickups.value = []
  }
}

const updateCampusName = () => {
  if (pickupForm.value.campusId) {
    const campus = campuses.value.find(c => c.id == pickupForm.value.campusId)
    if (campus) {
      pickupForm.value.campusName = campus.name
    }
  } else {
    pickupForm.value.campusName = ''
  }
}

const resetPickupForm = () => {
  editingPickup.value = null
  pickupForm.value = {
    name: '',
    campusId: '',
    campusName: '',
    address: '',
    phone: '',
    status: 1
  }
}

const closePickupModal = () => {
  showPickupModal.value = false
  resetPickupForm()
}

const editPickup = (pickup) => {
  editingPickup.value = pickup
  pickupForm.value = {
    name: pickup.name,
    campusId: pickup.campusId,
    campusName: pickup.campusName,
    address: pickup.address,
    phone: pickup.phone,
    status: pickup.status
  }
  showPickupModal.value = true
}

const savePickup = async () => {
  if (!pickupForm.value.name.trim()) {
    alert('请输入自提点名称')
    return
  }
  
  try {
    let res
    if (editingPickup.value) {
      res = await api.system.updatePickup(editingPickup.value.id, pickupForm.value)
    } else {
      res = await api.system.addPickup(pickupForm.value)
    }
    
    if (res.code === 200) {
      alert(editingPickup.value ? '更新成功' : '添加成功')
      loadPickups()
      closePickupModal()
    } else {
      alert(res.message || '操作失败')
    }
  } catch (error) {
    console.error('自提点操作失败', error)
    alert('操作失败: ' + (error.message || '未知错误'))
  }
}

const deletePickup = async (pickup) => {
  if (!confirm(`确定要删除自提点 "${pickup.name}" 吗？`)) {
    return
  }
  
  try {
    const res = await api.system.deletePickup(pickup.id)
    if (res.code === 200) {
      alert('删除成功')
      loadPickups()
    } else {
      alert(res.message || '删除失败')
    }
  } catch (error) {
    console.error('删除自提点失败', error)
    alert('删除失败: ' + (error.message || '未知错误'))
  }
}

// 操作日志
const loadOperationLogs = async (keyword, date, page = 1, size = 20) => {
  try {
    const params = { page, size }
    if (keyword) params.keyword = keyword
    if (date) params.date = date
    const res = await api.system.getOperationLogs(params)
    if (res.code === 200) {
      operationLogs.value = res.data.data || []
      logTotal.value = res.data.total || 0
      logPage.value = res.data.page || 1
    }
  } catch (error) {
    console.error('获取操作日志失败', error)
    operationLogs.value = []
    logTotal.value = 0
  }
}

const searchLogs = () => {
  logPage.value = 1
  loadOperationLogs(logSearch.value, logDate.value, 1)
}

const prevLogPage = () => {
  if (logPage.value > 1) {
    logPage.value--
    loadOperationLogs(logSearch.value, logDate.value, logPage.value)
  }
}

const nextLogPage = () => {
  if (logPage.value < Math.ceil(logTotal.value / logSize.value)) {
    logPage.value++
    loadOperationLogs(logSearch.value, logDate.value, logPage.value)
  }
}

const getPathFromContent = (content) => {
  if (!content) return '-'
  const match = content.match(/路径: ([^\|]+)/)
  return match ? match[1].trim() : content.substring(0, 50) + '...'
}

const getTypeClass = (type) => {
  return {
    '新增': 'type-add',
    '修改': 'type-update',
    '删除': 'type-delete',
    '查询': 'type-query'
  }[type] || 'type-other'
}

const showLogDetail = (log) => {
  currentLog.value = log
  showLogDetailModal.value = true
}

const closeLogDetailModal = () => {
  showLogDetailModal.value = false
  currentLog.value = null
}

// 文件操作
const loadFiles = async () => {
  try {
    const res = await api.system.getFileList()
    if (res.code === 200) {
      files.value = res.data || []
    }
  } catch (error) {
    console.error('获取文件列表失败', error)
    files.value = []
  }
}

const triggerFileUpload = () => {
  fileInput.value?.click()
}

const handleFileUpload = (event) => {
  const file = event.target.files?.[0]
  if (file) {
    uploadFile(file)
  }
  event.target.value = ''
}

const uploadFile = async (file) => {
  try {
    const res = await api.system.uploadFile(file)
    if (res.code === 200) {
      alert(`文件 "${file.name}" 上传成功`)
      loadFiles()
    } else {
      alert(res.message || '上传失败')
    }
  } catch (error) {
    console.error('上传文件失败', error)
    alert('上传失败: ' + (error.message || '未知错误'))
  }
}

const downloadFile = (file) => {
  api.system.downloadFile(file.id)
}

const deleteFile = async (file) => {
  if (!confirm(`确定要删除文件 "${file.fileName}" 吗？`)) {
    return
  }
  
  try {
    const res = await api.system.deleteFile(file.id)
    if (res.code === 200) {
      alert('删除成功')
      loadFiles()
    } else {
      alert(res.message || '删除失败')
    }
  } catch (error) {
    console.error('删除文件失败', error)
    alert('删除失败: ' + (error.message || '未知错误'))
  }
}

onMounted(() => {
  loadCampuses()
})
</script>

<style scoped>
.system-management {
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

.tab-container {
  margin-top: 20px;
}

.tabs {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 20px;
  border-bottom: 1px solid #ddd;
  gap: 5px;
}

.tabs button {
  padding: 10px 15px;
  border: none;
  background: none;
  cursor: pointer;
  font-size: 14px;
  border-bottom: 2px solid transparent;
  margin-right: 5px;
  transition: all 0.3s ease;
}

.tabs button.active {
  border-bottom: 2px solid #34495e;
  color: #34495e;
  font-weight: bold;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-box input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.flex {
  display: flex;
  justify-content: flex-start;
  gap: 10px;
}

.mb-20 {
  margin-bottom: 20px;
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

.btn-success {
  background-color: #28a745;
  color: #fff;
}

.btn-success:hover:not(:disabled) {
  background-color: #218838;
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

.empty {
  text-align: center;
  color: #999;
  padding: 40px;
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

.status-inactive {
  background-color: #f8d7da;
  color: #721c24;
}

.hidden {
  display: none;
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
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #34495e;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 20px;
}

.pagination span {
  font-size: 14px;
  color: #666;
}

.type-tag {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.type-add {
  background-color: #d4edda;
  color: #155724;
}

.type-update {
  background-color: #d1ecf1;
  color: #0c5460;
}

.type-delete {
  background-color: #f8d7da;
  color: #721c24;
}

.type-query {
  background-color: #fff3cd;
  color: #856404;
}

.type-other {
  background-color: #e2e3e5;
  color: #383d41;
}

.path-cell {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.detail-item {
  margin-bottom: 15px;
}

.detail-label {
  display: inline-block;
  width: 100px;
  font-weight: 500;
  color: #666;
}

.detail-value {
  display: inline-block;
}

.detail-content {
  margin-top: 10px;
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 4px;
  font-family: monospace;
  font-size: 13px;
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 300px;
  overflow-y: auto;
}
</style>