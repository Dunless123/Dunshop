<template>
  <div class="user-management">
    <div class="card">
      <div class="flex mb-20">
        <h3>用户管理</h3>
        <div class="search-box">
          <input type="text" v-model="searchKeyword" placeholder="搜索用户（用户名、邮箱、手机号）" />
          <button class="btn btn-primary" @click="handleSearch">搜索</button>
        </div>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th><input type="checkbox" v-model="selectAll" @change="handleSelectAll" /></th>
            <th>ID</th>
            <th>用户名</th>
            <th>邮箱</th>
            <th>手机号</th>
            <th>角色</th>
            <th>状态</th>
            <th>认证状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td><input type="checkbox" v-model="user.selected" @change="handleUserSelect" /></td>
            <td>{{ user.id }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.email || '-' }}</td>
            <td>{{ user.phone || '-' }}</td>
            <td><span :class="['role-tag', getRoleClass(user.role)]">{{ user.role }}</span></td>
            <td><span :class="['status-tag', getStatusClass(user.status)]">{{ user.status }}</span></td>
            <td><span :class="['auth-tag', getAuthClass(user.authStatus)]">{{ getAuthText(user.authStatus) }}</span></td>
            <td>{{ formatTime(user.createTime) }}</td>
            <td>
              <button class="btn btn-primary" @click="openEditModal(user)">编辑</button>
              <button class="btn btn-danger" @click="toggleUserStatus(user)">{{ user.status === '正常' ? '禁用' : '启用' }}</button>
              <button class="btn btn-secondary" @click="openAuthModal(user)">认证审核</button>
            </td>
          </tr>
          <tr v-if="users.length === 0">
            <td colspan="10" class="empty-row">暂无用户数据</td>
          </tr>
        </tbody>
      </table>
      <div class="flex">
        <div class="batch-actions">
          <button class="btn btn-secondary" @click="confirmBatchDisable" :disabled="selectedUsers.length === 0">批量禁用</button>
          <button class="btn btn-secondary" @click="confirmBatchEnable" :disabled="selectedUsers.length === 0">批量启用</button>
        </div>
        <div class="pagination">
          <button class="pagination-btn" :disabled="currentPage === 1" @click="prevPage">上一页</button>
          <span class="pagination-info">第 {{ currentPage }} / {{ totalPages }} 页，共 {{ total }} 条记录</span>
          <button class="pagination-btn" :disabled="currentPage >= totalPages" @click="nextPage">下一页</button>
        </div>
      </div>
    </div>

    <div v-if="showEditModal" class="modal-overlay" @click="closeEditModal">
      <div class="modal-content edit-modal" @click.stop>
        <div class="modal-header">
          <h3>编辑用户</h3>
          <button class="close-btn" @click="closeEditModal">×</button>
        </div>
        <div class="modal-body" v-if="editingUser">
          <div class="form-group avatar-group">
            <label>头像</label>
            <div class="avatar-upload">
              <div class="avatar-container">
                <img v-if="localAvatarPreview" :src="localAvatarPreview" class="avatar-preview" />
                <img v-else-if="editingUser.avatar" :src="editingUser.avatar" class="avatar-preview" />
                <div v-else class="avatar-placeholder">
                  <span>{{ editingUser.username ? editingUser.username.charAt(0) : '?' }}</span>
                </div>
              </div>
              <input type="file" :id="'avatar-upload-' + editingUser.id" class="avatar-input" accept="image/*" @change="handleAvatarUpload" />
              <label class="avatar-upload-btn" :for="'avatar-upload-' + editingUser.id">上传头像</label>
              <button v-if="localAvatarPreview || editingUser.avatar" class="avatar-clear-btn" @click="clearAvatar">清除</button>
            </div>
          </div>
          <div class="form-group">
            <label>用户名</label>
            <input type="text" v-model="editingUser.username" placeholder="请输入用户名" />
          </div>
          <div class="form-group">
            <label>密码</label>
            <input type="password" v-model="editingUser.password" placeholder="不修改请留空" />
          </div>
          <div class="form-group">
            <label>邮箱</label>
            <input type="email" v-model="editingUser.email" placeholder="请输入邮箱" />
          </div>
          <div class="form-group">
            <label>手机号</label>
            <input type="tel" v-model="editingUser.phone" placeholder="请输入手机号" />
          </div>
          <div class="form-group">
            <label>角色</label>
            <select v-model="editingUser.role">
              <option value="普通用户">普通用户</option>
              <option value="管理员">管理员</option>
            </select>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-default" @click="closeEditModal">取消</button>
          <button class="btn btn-primary" @click="saveUser">保存</button>
        </div>
      </div>
    </div>

    <div v-if="showAuthModal" class="modal-overlay" @click="closeAuthModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>认证审核</h3>
          <button class="close-btn" @click="closeAuthModal">×</button>
        </div>
        <div class="modal-body" v-if="authUser">
          <div class="auth-info">
            <div class="info-row">
              <span class="label">用户名：</span>
              <span class="value">{{ authUser.username }}</span>
            </div>
            <div class="info-row">
              <span class="label">用户ID：</span>
              <span class="value">{{ authUser.id }}</span>
            </div>
            <div class="info-row">
              <span class="label">当前状态：</span>
              <span :class="['auth-tag', getAuthClass(authUser.authStatus)]">{{ getAuthText(authUser.authStatus) }}</span>
            </div>
          </div>
          <div class="auth-actions">
            <button class="btn btn-success" @click="handleAuthPass">通过认证</button>
            <button class="btn btn-danger" @click="handleAuthReject">拒绝认证</button>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-default" @click="closeAuthModal">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../utils/api.js'

const searchKeyword = ref('')
const selectAll = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const users = ref([])
const total = ref(0)

const showEditModal = ref(false)
const editingUser = ref(null)
const localAvatarPreview = ref('')
const pendingAvatarFile = ref(null)
const showAuthModal = ref(false)
const authUser = ref(null)

const totalPages = computed(() => {
  return Math.ceil(total.value / pageSize.value)
})

const selectedUsers = computed(() => {
  return users.value.filter(user => user.selected)
})

const fetchUsers = async () => {
  try {
    const res = await api.admin.getUsers({
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value
    })
    if (res.code === 200) {
      users.value = res.data.list || res.data
      total.value = res.data.total || users.value.length
      if (users.value.length > 0) {
        users.value.forEach(user => {
          if (user.selected === undefined) {
            user.selected = false
          }
        })
      }
      selectAll.value = false
    }
  } catch (error) {
    console.error('获取用户数据失败', error)
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchUsers()
}

const handleSelectAll = (event) => {
  const checked = event.target.checked
  users.value.forEach(user => {
    user.selected = checked
  })
}

const handleUserSelect = () => {
  selectAll.value = users.value.length > 0 && users.value.every(user => user.selected)
}

const openEditModal = (user) => {
  editingUser.value = { ...user }
  showEditModal.value = true
}

const closeEditModal = () => {
  showEditModal.value = false
  editingUser.value = null
}

const handleAvatarUpload = (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  pendingAvatarFile.value = file
  
  const reader = new FileReader()
  reader.onload = (e) => {
    localAvatarPreview.value = e.target.result
  }
  reader.readAsDataURL(file)
}

const uploadPendingAvatar = async () => {
  if (!pendingAvatarFile.value) return null
  
  const formData = new FormData()
  formData.append('file', pendingAvatarFile.value)
  
  try {
    const res = await api.upload.avatar(formData)
    if (res.code === 200) {
      return res.data.url
    }
  } catch (error) {
    console.error('上传头像失败', error)
    throw error
  }
  return null
}

const clearAvatar = () => {
  localAvatarPreview.value = ''
  pendingAvatarFile.value = null
  editingUser.value.avatar = ''
}

const saveUser = async () => {
  if (!editingUser.value) return
  
  const updateData = {
    username: editingUser.value.username,
    email: editingUser.value.email,
    phone: editingUser.value.phone,
    role: editingUser.value.role
  }
  
  if (editingUser.value.password && editingUser.value.password.length > 0) {
    updateData.password = editingUser.value.password
  }
  
  if (pendingAvatarFile.value) {
    try {
      const avatarUrl = await uploadPendingAvatar()
      if (avatarUrl) {
        updateData.avatar = avatarUrl
      }
    } catch (error) {
      alert('头像上传失败，请重试')
      return
    }
  } else if (editingUser.value.avatar && !localAvatarPreview.value) {
    updateData.avatar = editingUser.value.avatar
  }
  
  try {
    const res = await api.admin.updateUser(editingUser.value.id, updateData)
    if (res.code === 200) {
      const index = users.value.findIndex(u => u.id === editingUser.value.id)
      if (index !== -1) {
        users.value[index] = { ...users.value[index], ...updateData }
      }
      alert('修改成功')
      closeEditModal()
      localAvatarPreview.value = ''
      pendingAvatarFile.value = null
    }
  } catch (error) {
    console.error('修改用户失败', error)
    alert('修改失败')
  }
}

const toggleUserStatus = async (user) => {
  const action = user.status === '正常' ? '禁用' : '启用'
  if (!confirm(`确定要${action}用户「${user.username}」吗？`)) {
    return
  }
  try {
    const newStatus = user.status === '正常' ? '禁用' : '正常'
    const res = await api.admin.updateUser(user.id, {
      status: newStatus
    })
    if (res.code === 200) {
      user.status = newStatus
      alert(`${action}成功`)
    }
  } catch (error) {
    console.error('更新用户状态失败', error)
    alert(`${action}失败`)
  }
}

const openAuthModal = (user) => {
  authUser.value = user
  showAuthModal.value = true
}

const closeAuthModal = () => {
  showAuthModal.value = false
  authUser.value = null
}

const handleAuthPass = async () => {
  if (!authUser.value) return
  if (!confirm(`确定要通过用户「${authUser.value.username}」的认证吗？`)) {
    return
  }
  try {
    const res = await api.admin.updateUser(authUser.value.id, {
      authStatus: '已认证'
    })
    if (res.code === 200) {
      authUser.value.authStatus = '已认证'
      const index = users.value.findIndex(u => u.id === authUser.value.id)
      if (index !== -1) {
        users.value[index].authStatus = '已认证'
      }
      alert('认证通过成功')
      closeAuthModal()
    }
  } catch (error) {
    console.error('认证审核失败', error)
    alert('认证审核失败')
  }
}

const handleAuthReject = async () => {
  if (!authUser.value) return
  if (!confirm(`确定要拒绝用户「${authUser.value.username}」的认证吗？`)) {
    return
  }
  try {
    const res = await api.admin.updateUser(authUser.value.id, {
      authStatus: '未认证'
    })
    if (res.code === 200) {
      authUser.value.authStatus = '未认证'
      const index = users.value.findIndex(u => u.id === authUser.value.id)
      if (index !== -1) {
        users.value[index].authStatus = '未认证'
      }
      alert('已拒绝认证')
      closeAuthModal()
    }
  } catch (error) {
    console.error('认证审核失败', error)
    alert('认证审核失败')
  }
}

const confirmBatchDisable = () => {
  if (selectedUsers.value.length === 0) return
  if (!confirm(`确定要禁用选中的 ${selectedUsers.value.length} 个用户吗？`)) {
    return
  }
  batchDisable()
}

const confirmBatchEnable = () => {
  if (selectedUsers.value.length === 0) return
  if (!confirm(`确定要启用选中的 ${selectedUsers.value.length} 个用户吗？`)) {
    return
  }
  batchEnable()
}

const batchDisable = async () => {
  let successCount = 0
  for (const user of selectedUsers.value) {
    try {
      const res = await api.admin.updateUser(user.id, { status: '禁用' })
      if (res.code === 200) {
        user.status = '禁用'
        successCount++
      }
    } catch (error) {
      console.error(`禁用用户 ${user.id} 失败`, error)
    }
  }
  users.value.forEach(user => {
    user.selected = false
  })
  selectAll.value = false
  alert(`批量禁用完成，成功 ${successCount} 个，失败 ${selectedUsers.value.length - successCount} 个`)
}

const batchEnable = async () => {
  let successCount = 0
  for (const user of selectedUsers.value) {
    try {
      const res = await api.admin.updateUser(user.id, { status: '正常' })
      if (res.code === 200) {
        user.status = '正常'
        successCount++
      }
    } catch (error) {
      console.error(`启用用户 ${user.id} 失败`, error)
    }
  }
  users.value.forEach(user => {
    user.selected = false
  })
  selectAll.value = false
  alert(`批量启用完成，成功 ${successCount} 个，失败 ${selectedUsers.value.length - successCount} 个`)
}

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
    fetchUsers()
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
    fetchUsers()
  }
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

const getRoleClass = (role) => {
  const classMap = {
    '普通用户': 'role-normal',
    '管理员': 'role-admin'
  }
  return classMap[role] || 'role-default'
}

const getStatusClass = (status) => {
  const classMap = {
    '正常': 'status-active',
    '禁用': 'status-disabled'
  }
  return classMap[status] || 'status-default'
}

const getAuthClass = (authStatus) => {
  const classMap = {
    '已认证': 'auth-approved',
    '待审核': 'auth-pending',
    '未认证': 'auth-default'
  }
  return classMap[authStatus] || 'auth-default'
}

const getAuthText = (authStatus) => {
  const textMap = {
    '已认证': '已认证',
    '待审核': '待审核',
    '未认证': '未认证'
  }
  return textMap[authStatus] || authStatus || '未认证'
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.user-management {
  padding: 20px;
}

.search-box {
  display: flex;
  align-items: center;
}

.search-box input {
  margin-right: 10px;
  width: 250px;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.search-box input:focus {
  outline: none;
  border-color: #34495e;
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

.btn-success {
  background-color: #28a745;
  color: #fff;
}

.btn-success:hover:not(:disabled) {
  background-color: #218838;
}

.btn-default {
  background-color: #f0f0f0;
  color: #666;
}

.btn-default:hover:not(:disabled) {
  background-color: #e0e0e0;
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
  margin-bottom: 20px;
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

.empty-row {
  text-align: center;
  color: #999;
  padding: 40px !important;
}

.role-tag,
.status-tag,
.auth-tag {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.role-normal {
  background-color: #e7f3ff;
  color: #0066cc;
}

.role-admin {
  background-color: #fff3cd;
  color: #856404;
}

.role-default {
  background-color: #e2e3e5;
  color: #383d41;
}

.status-active {
  background-color: #d4edda;
  color: #155724;
}

.status-disabled {
  background-color: #f8d7da;
  color: #721c24;
}

.status-default {
  background-color: #e2e3e5;
  color: #383d41;
}

.auth-approved {
  background-color: #d4edda;
  color: #155724;
}

.auth-pending {
  background-color: #fff3cd;
  color: #856404;
}

.auth-default {
  background-color: #e2e3e5;
  color: #383d41;
}

.flex {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.mb-20 {
  margin-bottom: 20px;
}

.batch-actions {
  display: flex;
  gap: 10px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
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

.modal-footer {
  padding: 15px 25px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
  color: #333;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #34495e;
}

.auth-info {
  margin-bottom: 20px;
}

.info-row {
  display: flex;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
}

.info-row:last-child {
  border-bottom: none;
}

.info-row .label {
  width: 100px;
  color: #666;
  font-weight: 500;
}

.info-row .value {
  flex: 1;
  color: #333;
}

.auth-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.avatar-group {
  margin-bottom: 20px;
}

.avatar-upload {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar-preview {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #ddd;
}

.avatar-placeholder {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background-color: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #999;
  border: 2px solid #ddd;
}

.avatar-input {
  display: none;
}

.avatar-upload-btn {
  padding: 8px 16px;
  background-color: #34495e;
  color: #fff;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s ease;
}

.avatar-upload-btn:hover {
  background-color: #2c3e50;
}

.avatar-clear-btn {
  padding: 8px 16px;
  background-color: #dc3545;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s ease;
}

.avatar-clear-btn:hover {
  background-color: #c82333;
}

.avatar-container {
  position: relative;
}

.edit-modal {
  max-width: 550px;
}
</style>