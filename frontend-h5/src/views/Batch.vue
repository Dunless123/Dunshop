<template>
  <div class="batch-management">
    <div class="card">
      <div class="flex mb-20">
        <h3>批量操作</h3>
      </div>
      <div class="tab-container">
        <div class="tabs">
          <button :class="{ active: activeTab === 'goods' }" @click="activeTab = 'goods'; loadGoods()">商品批量审核</button>
          <button :class="{ active: activeTab === 'order' }" @click="activeTab = 'order'; loadOrders()">订单批量处理</button>
          <button :class="{ active: activeTab === 'user' }" @click="activeTab = 'user'; loadUsers()">用户批量管理</button>
        </div>
        <div class="tab-content">
          <!-- 商品批量审核 -->
          <div v-if="activeTab === 'goods'">
            <div class="flex mb-20">
              <div class="search-box">
                <input type="text" v-model="goodsSearch" placeholder="搜索商品名称" @keyup.enter="searchGoods" />
                <select v-model="goodsStatus" @change="searchGoods">
                  <option value="">所有状态</option>
                  <option value="待审核">待审核</option>
                  <option value="在售">已通过</option>
                  <option value="已拒绝">已拒绝</option>
                </select>
                <button class="btn btn-primary" @click="searchGoods">搜索</button>
              </div>
              <div class="batch-actions">
                <button class="btn btn-success" @click="batchApproveGoods" :disabled="selectedGoods.length === 0">批量通过</button>
                <button class="btn btn-danger" @click="batchRejectGoods" :disabled="selectedGoods.length === 0">批量拒绝</button>
              </div>
            </div>
            <table class="table">
              <thead>
                <tr>
                  <th><input type="checkbox" v-model="selectAllGoods" @change="toggleSelectAllGoods" /></th>
                  <th>ID</th>
                  <th>商品名称</th>
                  <th>卖家</th>
                  <th>价格</th>
                  <th>状态</th>
                  <th>创建时间</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="goods in goodsList" :key="goods.id">
                  <td><input type="checkbox" v-model="goods.selected" @change="handleGoodsSelect" /></td>
                  <td>{{ goods.id }}</td>
                  <td>{{ goods.title }}</td>
                  <td>{{ goods.sellerName || '-' }}</td>
                  <td class="amount">¥{{ formatAmount(goods.price) }}</td>
                  <td><span :class="['status-tag', getGoodsStatusClass(goods.status)]">{{ goods.status }}</span></td>
                  <td>{{ formatTime(goods.createTime) }}</td>
                  <td class="actions">
                    <button class="btn btn-success btn-sm" @click="approveGoods(goods)">通过</button>
                    <button class="btn btn-danger btn-sm" @click="rejectGoods(goods)">拒绝</button>
                    <button class="btn btn-secondary btn-sm" @click="viewGoods(goods)">查看</button>
                  </td>
                </tr>
                <tr v-if="goodsList.length === 0">
                  <td colspan="8" class="empty">暂无商品数据</td>
                </tr>
              </tbody>
            </table>
            <div class="flex justify-between items-center">
              <div class="batch-info">已选择 {{ selectedGoods.length }} 件商品</div>
              <div class="pagination">
                <button @click="prevGoodsPage" :disabled="goodsCurrentPage === 1">上一页</button>
                <span class="page-info">{{ goodsCurrentPage }} / {{ goodsTotalPages }}</span>
                <button @click="nextGoodsPage" :disabled="goodsCurrentPage >= goodsTotalPages">下一页</button>
              </div>
            </div>
          </div>
          <!-- 订单批量处理 -->
          <div v-if="activeTab === 'order'">
            <div class="flex mb-20">
              <div class="search-box">
                <input type="text" v-model="orderSearch" placeholder="搜索订单号或用户" @keyup.enter="loadOrders()" />
                <select v-model="orderStatus" @change="loadOrders()">
                  <option value="">所有状态</option>
                  <option value="待支付">待支付</option>
                  <option value="已支付">已支付</option>
                  <option value="已发货">已发货</option>
                  <option value="已完成">已完成</option>
                  <option value="已取消">已取消</option>
                </select>
                <button class="btn btn-primary" @click="loadOrders()">搜索</button>
              </div>
              <div class="batch-actions">
                <button class="btn btn-danger" @click="batchCancelOrders" :disabled="selectedOrders.length === 0">批量取消</button>
              </div>
            </div>
            <table class="table">
              <thead>
                <tr>
                  <th><input type="checkbox" v-model="selectAllOrders" @change="toggleSelectAllOrders" /></th>
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
                <tr v-for="order in orderList" :key="order.id">
                  <td><input type="checkbox" v-model="order.selected" @change="handleOrderSelect" /></td>
                  <td class="order-no">{{ order.orderNo }}</td>
                  <td>{{ order.buyerName || '-' }}</td>
                  <td>{{ order.goodsTitle || '-' }}</td>
                  <td class="amount">¥{{ formatAmount(order.price) }}</td>
                  <td><span :class="['status-tag', getOrderStatusClass(order.status)]">{{ order.status }}</span></td>
                  <td>{{ formatTime(order.createTime) }}</td>
                  <td class="actions">
                    <button class="btn btn-primary btn-sm" @click="viewOrder(order)">查看</button>
                  </td>
                </tr>
                <tr v-if="orderList.length === 0">
                  <td colspan="8" class="empty">暂无订单数据</td>
                </tr>
              </tbody>
            </table>
            <div class="flex justify-between items-center">
              <div class="batch-info">已选择 {{ selectedOrders.length }} 个订单</div>
              <div class="pagination">
                <button @click="prevOrderPage" :disabled="orderCurrentPage === 1">上一页</button>
                <span class="page-info">{{ orderCurrentPage }} / {{ orderTotalPages }}</span>
                <button @click="nextOrderPage" :disabled="orderCurrentPage >= orderTotalPages">下一页</button>
              </div>
            </div>
          </div>
          <!-- 用户批量管理 -->
          <div v-if="activeTab === 'user'">
            <div class="flex mb-20">
              <div class="search-box">
                <input type="text" v-model="userSearch" placeholder="搜索用户名或邮箱" @keyup.enter="loadUsers()" />
                <select v-model="userStatus" @change="loadUsers()">
                  <option value="">所有状态</option>
                  <option value="正常">正常</option>
                  <option value="禁用">禁用</option>
                </select>
                <button class="btn btn-primary" @click="loadUsers()">搜索</button>
              </div>
              <div class="batch-actions">
                <button class="btn btn-success" @click="batchEnableUsers" :disabled="selectedUsers.length === 0">批量启用</button>
                <button class="btn btn-danger" @click="batchDisableUsers" :disabled="selectedUsers.length === 0">批量禁用</button>
              </div>
            </div>
            <table class="table">
              <thead>
                <tr>
                  <th><input type="checkbox" v-model="selectAllUsers" @change="toggleSelectAllUsers" /></th>
                  <th>ID</th>
                  <th>用户名</th>
                  <th>邮箱</th>
                  <th>手机号</th>
                  <th>状态</th>
                  <th>创建时间</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="user in userList" :key="user.id">
                  <td><input type="checkbox" v-model="user.selected" @change="handleUserSelect" /></td>
                  <td>{{ user.id }}</td>
                  <td>{{ user.username }}</td>
                  <td>{{ user.email || '-' }}</td>
                  <td>{{ user.phone || '-' }}</td>
                  <td><span :class="['status-tag', getUserStatusClass(user.status)]">{{ user.status }}</span></td>
                  <td>{{ formatTime(user.createTime) }}</td>
                  <td class="actions">
                    <button class="btn btn-primary btn-sm" @click="openEditUserModal(user)">编辑</button>
                    <button :class="['btn', 'btn-sm', user.status === '正常' ? 'btn-danger' : 'btn-success']" @click="toggleUserStatus(user)">{{ user.status === '正常' ? '禁用' : '启用' }}</button>
                  </td>
                </tr>
                <tr v-if="userList.length === 0">
                  <td colspan="8" class="empty">暂无用户数据</td>
                </tr>
              </tbody>
            </table>
            <div class="flex justify-between items-center">
              <div class="batch-info">已选择 {{ selectedUsers.length }} 个用户</div>
              <div class="pagination">
                <button @click="prevUserPage" :disabled="userCurrentPage === 1">上一页</button>
                <span class="page-info">{{ userCurrentPage }} / {{ userTotalPages }}</span>
                <button @click="nextUserPage" :disabled="userCurrentPage >= userTotalPages">下一页</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 商品详情弹窗 -->
    <div v-if="showGoodsModal" class="modal-overlay" @click="closeGoodsModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>商品详情</h3>
          <button class="close-btn" @click="closeGoodsModal">×</button>
        </div>
        <div class="modal-body" v-if="currentGoods">
          <div class="detail-item">
            <span class="label">商品ID：</span>
            <span>{{ currentGoods.id }}</span>
          </div>
          <div class="detail-item">
            <span class="label">商品名称：</span>
            <span>{{ currentGoods.title }}</span>
          </div>
          <div class="detail-item">
            <span class="label">卖家：</span>
            <span>{{ currentGoods.sellerName || '-' }}</span>
          </div>
          <div class="detail-item">
            <span class="label">价格：</span>
            <span class="amount">¥{{ formatAmount(currentGoods.price) }}</span>
          </div>
          <div class="detail-item">
            <span class="label">状态：</span>
            <span :class="['status-tag', getGoodsStatusClass(currentGoods.status)]">{{ currentGoods.status }}</span>
          </div>
          <div class="detail-item">
            <span class="label">创建时间：</span>
            <span>{{ formatTime(currentGoods.createTime) }}</span>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-default" @click="closeGoodsModal">关闭</button>
        </div>
      </div>
    </div>

    <!-- 订单详情弹窗 -->
    <div v-if="showOrderModal" class="modal-overlay" @click="closeOrderModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>订单详情</h3>
          <button class="close-btn" @click="closeOrderModal">×</button>
        </div>
        <div class="modal-body" v-if="currentOrder">
          <div class="detail-item">
            <span class="label">订单号：</span>
            <span class="order-no">{{ currentOrder.orderNo }}</span>
          </div>
          <div class="detail-item">
            <span class="label">用户：</span>
            <span>{{ currentOrder.buyerName || '-' }}</span>
          </div>
          <div class="detail-item">
            <span class="label">商品：</span>
            <span>{{ currentOrder.goodsTitle || '-' }}</span>
          </div>
          <div class="detail-item">
            <span class="label">金额：</span>
            <span class="amount">¥{{ formatAmount(currentOrder.price) }}</span>
          </div>
          <div class="detail-item">
            <span class="label">状态：</span>
            <span :class="['status-tag', getOrderStatusClass(currentOrder.status)]">{{ currentOrder.status }}</span>
          </div>
          <div class="detail-item">
            <span class="label">创建时间：</span>
            <span>{{ formatTime(currentOrder.createTime) }}</span>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-default" @click="closeOrderModal">关闭</button>
        </div>
      </div>
    </div>

    <!-- 用户编辑弹窗 -->
    <div v-if="showUserModal" class="modal-overlay" @click="closeUserModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>编辑用户</h3>
          <button class="close-btn" @click="closeUserModal">×</button>
        </div>
        <div class="modal-body" v-if="editingUser">
          <div class="form-group">
            <label>用户名</label>
            <input type="text" v-model="editingUser.username" placeholder="请输入用户名" />
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
            <label>状态</label>
            <select v-model="editingUser.status">
              <option value="正常">正常</option>
              <option value="禁用">禁用</option>
            </select>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-default" @click="closeUserModal">取消</button>
          <button class="btn btn-primary" @click="saveUser">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../utils/api'

const activeTab = ref('goods')

// 商品批量审核
const goodsSearch = ref('')
const goodsStatus = ref('')
const selectAllGoods = ref(false)
const goodsList = ref([])
const goodsCurrentPage = ref(1)
const goodsPageSize = ref(10)
const goodsTotal = ref(0)

// 订单批量处理
const orderSearch = ref('')
const orderStatus = ref('')
const selectAllOrders = ref(false)
const orderList = ref([])
const orderCurrentPage = ref(1)
const orderPageSize = ref(10)
const orderTotal = ref(0)

// 用户批量管理
const userSearch = ref('')
const userStatus = ref('')
const selectAllUsers = ref(false)
const userList = ref([])
const userCurrentPage = ref(1)
const userPageSize = ref(10)
const userTotal = ref(0)

// 弹窗状态
const showGoodsModal = ref(false)
const showOrderModal = ref(false)
const showUserModal = ref(false)
const currentGoods = ref(null)
const currentOrder = ref(null)
const editingUser = ref(null)

const goodsTotalPages = computed(() => Math.max(1, Math.ceil(goodsTotal.value / goodsPageSize.value)))
const orderTotalPages = computed(() => Math.max(1, Math.ceil(orderTotal.value / orderPageSize.value)))
const userTotalPages = computed(() => Math.max(1, Math.ceil(userTotal.value / userPageSize.value)))

const selectedGoods = computed(() => goodsList.value.filter(g => g.selected))
const selectedOrders = computed(() => orderList.value.filter(o => o.selected))
const selectedUsers = computed(() => userList.value.filter(u => u.selected))

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

const getGoodsStatusClass = (status) => {
  const classMap = {
    '待审核': 'status-pending',
    '在售': 'status-active',
    '已拒绝': 'status-disabled'
  }
  return classMap[status] || 'status-default'
}

const getOrderStatusClass = (status) => {
  const classMap = {
    '待支付': 'status-pending',
    '已支付': 'status-active',
    '已发货': 'status-shipped',
    '已完成': 'status-completed',
    '已取消': 'status-disabled',
    '已退款': 'status-refunded'
  }
  return classMap[status] || 'status-default'
}

const getUserStatusClass = (status) => {
  const classMap = {
    '正常': 'status-active',
    '禁用': 'status-disabled'
  }
  return classMap[status] || 'status-default'
}

const loadGoods = async () => {
  try {
    const params = {
      page: goodsCurrentPage.value,
      pageSize: goodsPageSize.value
    }
    if (goodsSearch.value && goodsSearch.value.trim()) {
      params.keyword = goodsSearch.value.trim()
    }
    if (goodsStatus.value && goodsStatus.value.trim()) {
      params.status = goodsStatus.value.trim()
    }
    const response = await api.admin.getGoods(params)
    if (response.code === 200) {
      goodsList.value = (response.data.list || []).map(g => ({ ...g, selected: false }))
      goodsTotal.value = response.data.total || 0
      selectAllGoods.value = false
    }
  } catch (error) {
    console.error('加载商品失败:', error)
    goodsList.value = []
    goodsTotal.value = 0
  }
}

const searchGoods = () => {
  goodsCurrentPage.value = 1
  loadGoods()
}

const loadOrders = async () => {
  try {
    const params = {
      page: orderCurrentPage.value,
      pageSize: orderPageSize.value
    }
    if (orderSearch.value && orderSearch.value.trim()) {
      params.keyword = orderSearch.value.trim()
    }
    if (orderStatus.value && orderStatus.value.trim()) {
      params.status = orderStatus.value.trim()
    }
    const response = await api.admin.getOrders(params)
    if (response.code === 200) {
      orderList.value = (response.data.list || []).map(o => ({ ...o, selected: false }))
      orderTotal.value = response.data.total || 0
      selectAllOrders.value = false
    }
  } catch (error) {
    console.error('加载订单失败:', error)
    orderList.value = []
    orderTotal.value = 0
  }
}

const loadUsers = async () => {
  try {
    const params = {
      page: userCurrentPage.value,
      pageSize: userPageSize.value
    }
    if (userSearch.value && userSearch.value.trim()) {
      params.keyword = userSearch.value.trim()
    }
    if (userStatus.value && userStatus.value.trim()) {
      params.status = userStatus.value.trim()
    }
    const response = await api.admin.getUsers(params)
    if (response.code === 200) {
      userList.value = (response.data.list || []).map(u => ({ ...u, selected: false }))
      userTotal.value = response.data.total || 0
      selectAllUsers.value = false
    }
  } catch (error) {
    console.error('加载用户失败:', error)
    userList.value = []
    userTotal.value = 0
  }
}

const toggleSelectAllGoods = () => {
  goodsList.value.forEach(goods => {
    goods.selected = selectAllGoods.value
  })
}

const toggleSelectAllOrders = () => {
  orderList.value.forEach(order => {
    order.selected = selectAllOrders.value
  })
}

const toggleSelectAllUsers = () => {
  userList.value.forEach(user => {
    user.selected = selectAllUsers.value
  })
}

const handleGoodsSelect = () => {
  selectAllGoods.value = goodsList.value.length > 0 && goodsList.value.every(g => g.selected)
}

const handleOrderSelect = () => {
  selectAllOrders.value = orderList.value.length > 0 && orderList.value.every(o => o.selected)
}

const handleUserSelect = () => {
  selectAllUsers.value = userList.value.length > 0 && userList.value.every(u => u.selected)
}

const approveGoods = async (goods) => {
  try {
    await api.admin.batchApproveGoods([goods.id])
    goods.status = '在售'
    alert('审核通过成功')
  } catch (error) {
    console.error('审核失败:', error)
    alert('审核失败: ' + (error.message || '未知错误'))
  }
}

const rejectGoods = async (goods) => {
  try {
    await api.admin.batchRejectGoods([goods.id])
    goods.status = '已拒绝'
    alert('拒绝成功')
  } catch (error) {
    console.error('拒绝失败:', error)
    alert('拒绝失败: ' + (error.message || '未知错误'))
  }
}

const viewGoods = (goods) => {
  currentGoods.value = goods
  showGoodsModal.value = true
}

const closeGoodsModal = () => {
  showGoodsModal.value = false
  currentGoods.value = null
}

const batchApproveGoods = async () => {
  if (selectedGoods.value.length === 0) {
    alert('请选择要审核的商品')
    return
  }
  try {
    const ids = selectedGoods.value.map(g => g.id)
    const response = await api.admin.batchApproveGoods(ids)
    alert(`批量审核通过成功，成功${response.data.successCount}条，共${response.data.totalCount}条`)
    loadGoods()
  } catch (error) {
    console.error('批量审核失败:', error)
    alert('批量审核失败: ' + (error.message || '未知错误'))
  }
}

const batchRejectGoods = async () => {
  if (selectedGoods.value.length === 0) {
    alert('请选择要拒绝的商品')
    return
  }
  try {
    const ids = selectedGoods.value.map(g => g.id)
    const response = await api.admin.batchRejectGoods(ids)
    alert(`批量拒绝成功，成功${response.data.successCount}条，共${response.data.totalCount}条`)
    loadGoods()
  } catch (error) {
    console.error('批量拒绝失败:', error)
    alert('批量拒绝失败: ' + (error.message || '未知错误'))
  }
}

const viewOrder = async (order) => {
  try {
    const res = await api.order.detail(order.id)
    if (res.code === 200) {
      currentOrder.value = res.data
    } else {
      currentOrder.value = order
    }
    showOrderModal.value = true
  } catch (error) {
    console.error('获取订单详情失败:', error)
    currentOrder.value = order
    showOrderModal.value = true
  }
}

const closeOrderModal = () => {
  showOrderModal.value = false
  currentOrder.value = null
}

const batchCancelOrders = async () => {
  const validOrders = selectedOrders.value.filter(o => o.status === '待支付')
  if (validOrders.length === 0) {
    alert('请选择待支付状态的订单')
    return
  }
  try {
    const ids = validOrders.map(o => o.id)
    const response = await api.order.batchUpdateStatus(ids, { status: '已取消' })
    alert(`批量取消成功，成功${response.data.successCount}条，共${response.data.totalCount}条`)
    loadOrders()
  } catch (error) {
    console.error('批量取消失败:', error)
    alert('批量取消失败: ' + (error.message || '未知错误'))
  }
}

const openEditUserModal = (user) => {
  editingUser.value = { ...user }
  showUserModal.value = true
}

const closeUserModal = () => {
  showUserModal.value = false
  editingUser.value = null
}

const saveUser = async () => {
  if (!editingUser.value) return
  
  try {
    const updateData = {
      username: editingUser.value.username,
      email: editingUser.value.email,
      phone: editingUser.value.phone,
      status: editingUser.value.status
    }
    
    const res = await api.admin.updateUser(editingUser.value.id, updateData)
    if (res.code === 200) {
      const index = userList.value.findIndex(u => u.id === editingUser.value.id)
      if (index !== -1) {
        userList.value[index] = { ...userList.value[index], ...updateData }
      }
      alert('修改成功')
      closeUserModal()
    } else {
      alert(res.message || '修改失败')
    }
  } catch (error) {
    console.error('修改用户失败:', error)
    alert('修改失败')
  }
}

const toggleUserStatus = async (user) => {
  const action = user.status === '正常' ? '禁用' : '启用'
  if (!confirm(`确定要${action}用户「${user.username}」吗？`)) return
  
  try {
    const newStatus = user.status === '正常' ? '禁用' : '正常'
    await api.admin.updateUser(user.id, { status: newStatus })
    user.status = newStatus
    alert(`${action}成功`)
  } catch (error) {
    console.error('更新用户状态失败:', error)
    alert(`${action}失败`)
  }
}

const batchEnableUsers = async () => {
  if (selectedUsers.value.length === 0) {
    alert('请选择要启用的用户')
    return
  }
  try {
    const ids = selectedUsers.value.map(u => u.id)
    const response = await api.admin.batchEnableUsers(ids)
    alert(`批量启用成功，成功${response.data.successCount}条，共${response.data.totalCount}条`)
    loadUsers()
  } catch (error) {
    console.error('批量启用失败:', error)
    alert('批量启用失败: ' + (error.message || '未知错误'))
  }
}

const batchDisableUsers = async () => {
  if (selectedUsers.value.length === 0) {
    alert('请选择要禁用的用户')
    return
  }
  try {
    const ids = selectedUsers.value.map(u => u.id)
    const response = await api.admin.batchDisableUsers(ids)
    alert(`批量禁用成功，成功${response.data.successCount}条，共${response.data.totalCount}条`)
    loadUsers()
  } catch (error) {
    console.error('批量禁用失败:', error)
    alert('批量禁用失败: ' + (error.message || '未知错误'))
  }
}

const prevGoodsPage = () => {
  if (goodsCurrentPage.value > 1) {
    goodsCurrentPage.value--
    loadGoods()
  }
}

const nextGoodsPage = () => {
  if (goodsCurrentPage.value < goodsTotalPages.value) {
    goodsCurrentPage.value++
    loadGoods()
  }
}

const prevOrderPage = () => {
  if (orderCurrentPage.value > 1) {
    orderCurrentPage.value--
    loadOrders()
  }
}

const nextOrderPage = () => {
  if (orderCurrentPage.value < orderTotalPages.value) {
    orderCurrentPage.value++
    loadOrders()
  }
}

const prevUserPage = () => {
  if (userCurrentPage.value > 1) {
    userCurrentPage.value--
    loadUsers()
  }
}

const nextUserPage = () => {
  if (userCurrentPage.value < userTotalPages.value) {
    userCurrentPage.value++
    loadUsers()
  }
}

onMounted(() => {
  loadGoods()
})
</script>

<style scoped>
.batch-management {
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
  margin-top: 10px;
}

.tabs {
  display: flex;
  margin-bottom: 20px;
  border-bottom: 1px solid #eee;
  padding-bottom: 0;
}

.tabs button {
  padding: 12px 24px;
  border: none;
  background: none;
  cursor: pointer;
  font-size: 14px;
  border-bottom: 2px solid transparent;
  color: #666;
  transition: all 0.3s ease;
}

.tabs button.active {
  border-bottom: 2px solid #34495e;
  color: #34495e;
  font-weight: 600;
}

.tabs button:hover {
  color: #34495e;
}

.flex {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.btn-secondary {
  background-color: #6c757d;
  color: #fff;
}

.btn-secondary:hover:not(:disabled) {
  background-color: #5a6268;
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

.empty {
  text-align: center;
  color: #999;
  padding: 40px;
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

.status-pending {
  background-color: #fff3cd;
  color: #856404;
}

.status-active {
  background-color: #d4edda;
  color: #155724;
}

.status-disabled {
  background-color: #f8d7da;
  color: #721c24;
}

.status-shipped {
  background-color: #d1ecf1;
  color: #0c5460;
}

.status-completed {
  background-color: #e8eef5;
  color: #495057;
}

.status-refunded {
  background-color: #f8d7da;
  color: #721c24;
}

.status-default {
  background-color: #e2e3e5;
  color: #383d41;
}

.actions {
  white-space: nowrap;
}

.batch-actions {
  display: flex;
  gap: 10px;
}

.batch-info {
  font-size: 14px;
  color: #666;
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
</style>