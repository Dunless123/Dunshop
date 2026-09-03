// API服务封装
const baseURL = 'http://localhost:8080';

// 封装请求方法
const request = (url, options = {}) => {
  const token = localStorage.getItem('token');
  
  // 处理GET请求的缓存问题，添加时间戳
  let finalUrl = baseURL + url;
  
  // 处理params参数
  if (options.params) {
    const params = new URLSearchParams(options.params);
    const paramsString = params.toString();
    if (paramsString) {
      finalUrl += (url.includes('?') ? '&' : '?') + paramsString;
    }
  }
  
  if ((options.method || 'GET') === 'GET') {
    const timestamp = new Date().getTime();
    finalUrl += (finalUrl.includes('?') ? '&' : '?') + `_t=${timestamp}`;
  }
  
  return new Promise((resolve, reject) => {
    const isFormData = options.data instanceof FormData;
    const headers = {
      'Authorization': token ? `Bearer ${token}` : ''
    };
    if (!isFormData) {
      headers['Content-Type'] = 'application/json';
    }
    
    fetch(finalUrl, {
      method: options.method || 'GET',
      headers: headers,
      body: isFormData ? options.data : (options.data ? JSON.stringify(options.data) : null)
    })
    .then(response => response.json())
    .then(data => {
      if (data.code === 200) {
        resolve(data);
      } else {
        reject(data);
      }
    })
    .catch(error => {
      reject(error);
    });
  });
};

// API接口
const api = {
  // 认证相关
  auth: {
    login: (data) => request('/api/auth/login', { method: 'POST', data }),
    register: (data) => request('/api/auth/register', { method: 'POST', data }),
    forgot: (data) => request('/api/auth/forgot', { method: 'POST', data }),
    changePassword: (data) => request('/api/auth/change-password', { method: 'POST', data }),
    logout: () => request('/api/auth/logout', { method: 'POST' })
  },
  
  // 用户相关
  user: {
    getInfo: () => request('/api/user/info'),
    update: (data) => request('/api/user/update', { method: 'PUT', data }),
    changePassword: (data) => request('/api/user/change-password', { method: 'POST', data }),
    getAddresses: () => request('/api/user/addresses'),
    addAddress: (data) => request('/api/user/addresses', { method: 'POST', data }),
    updateAddress: (id, data) => request(`/api/user/addresses/${id}`, { method: 'PUT', data }),
    deleteAddress: (id) => request(`/api/user/addresses/${id}`, { method: 'DELETE' })
  },
  
  // 商品相关
  goods: {
    list: (params) => request('/api/goods/list', { params }),
    detail: (id) => request(`/api/goods/${id}`),
    publish: (data) => request('/api/goods/publish', { method: 'POST', data }),
    update: (id, data) => request(`/api/goods/${id}`, { method: 'PUT', data }),
    delete: (id) => request(`/api/goods/${id}`, { method: 'DELETE' }),
    favorite: (id) => request(`/api/goods/${id}/favorite`, { method: 'POST' }),
    cancelFavorite: (id) => request(`/api/goods/${id}/favorite`, { method: 'DELETE' }),
    getMyGoods: (params) => request('/api/goods/my', { params }),
    getFavorites: (params) => request('/api/goods/favorites', { params })
  },
  
  // 聊天相关
  chat: {
    list: (params) => request('/api/chat/list', { params }),
    messages: (userId) => request(`/api/chat/${userId}/messages`),
    send: (data) => request('/api/chat/send', { method: 'POST', data })
  },
  
  // 评价相关
  comment: {
    list: (params) => request('/api/comment/list', { params }),
    create: (data) => request('/api/comment/create', { method: 'POST', data }),
    audit: (id, data) => request(`/api/comment/${id}/audit`, { method: 'PUT', data }),
    getMyComments: (params) => request('/api/comment/my', { params }),
    getAllComments: (params) => request('/api/comment/all', { params }),
    delete: (id) => request(`/api/comment/${id}`, { method: 'DELETE' }),
    updateRating: (id, rating) => request(`/api/comment/${id}/rating`, { method: 'PUT', data: { rating } })
  },
  
  // 分类相关
  category: {
    list: () => request('/api/category/list'),
    detail: (id) => request(`/api/category/${id}`)
  },
  
  // 校区相关
  campus: {
    list: () => request('/api/campus/list'),
    detail: (id) => request(`/api/campus/${id}`)
  },
  
  // 订单相关
  order: {
    list: (params) => request('/api/order/list', { params }),
    detail: (id) => request(`/api/order/${id}`),
    updateStatus: (id, data) => request(`/api/order/${id}/status`, { method: 'PUT', data }),
    refund: (id) => request(`/api/order/${id}/refund`, { method: 'POST' }),
    batchUpdateStatus: (ids, data) => request(`/api/order/batch/status`, { method: 'PUT', data: { ids, ...data } }),
    batchRefund: (ids) => request('/api/order/batch/refund', { method: 'POST', data: { ids } })
  },
  
  // 通知相关
  notification: {
    list: (params) => request('/api/notification/list', { params }),
    detail: (id) => request(`/api/notification/${id}`),
    send: (data) => request('/api/notification/send', { method: 'POST', data }),
    resend: (id) => request(`/api/notification/${id}/resend`, { method: 'POST' }),
    delete: (id) => request(`/api/notification/${id}`, { method: 'DELETE' }),
    templates: () => request('/api/notification/templates'),
    templateDetail: (id) => request(`/api/notification/templates/${id}`),
    addTemplate: (data) => request('/api/notification/templates', { method: 'POST', data }),
    updateTemplate: (id, data) => request(`/api/notification/templates/${id}`, { method: 'PUT', data }),
    deleteTemplate: (id) => request(`/api/notification/templates/${id}`, { method: 'DELETE' })
  },
  
  // 文件上传相关
  upload: {
    image: (data) => request('/api/upload/image', { method: 'POST', data }),
    avatar: (data) => request('/api/upload/avatar', { method: 'POST', data })
  },
  
  // 统计相关
  statistics: {
    dashboard: () => request('/api/statistics/dashboard'),
    goods: () => request('/api/statistics/goods'),
    orders: () => request('/api/statistics/orders'),
    interaction: () => request('/api/statistics/interaction')
  },
  
  // 管理员相关
  admin: {
    getUsers: (params) => request('/api/admin/users', { params }),
    updateUser: (id, data) => request(`/api/admin/users/${id}`, { method: 'PUT', data }),
    deleteUser: (id) => request(`/api/admin/users/${id}`, { method: 'DELETE' }),
    getStatistics: () => request('/api/admin/statistics'),
    getDashboard: () => request('/api/admin/dashboard'),
    getOrders: (params) => request('/api/admin/orders', { params }),
    getGoods: (params) => request('/api/admin/goods', { params }),
    batchApproveGoods: (ids) => request('/api/admin/goods/batch/approve', { method: 'PUT', data: { ids } }),
    batchRejectGoods: (ids) => request('/api/admin/goods/batch/reject', { method: 'PUT', data: { ids } }),
    batchEnableUsers: (ids) => request('/api/admin/users/batch/enable', { method: 'PUT', data: { ids } }),
    batchDisableUsers: (ids) => request('/api/admin/users/batch/disable', { method: 'PUT', data: { ids } })
  },
  
  // 系统管理相关
  system: {
    // 校区管理
    getCampusList: () => request('/api/system/campus'),
    addCampus: (data) => request('/api/system/campus', { method: 'POST', data }),
    updateCampus: (id, data) => request(`/api/system/campus/${id}`, { method: 'PUT', data }),
    deleteCampus: (id) => request(`/api/system/campus/${id}`, { method: 'DELETE' }),
    // 自提点管理
    getPickupList: () => request('/api/system/pickup'),
    addPickup: (data) => request('/api/system/pickup', { method: 'POST', data }),
    updatePickup: (id, data) => request(`/api/system/pickup/${id}`, { method: 'PUT', data }),
    deletePickup: (id) => request(`/api/system/pickup/${id}`, { method: 'DELETE' }),
    // 操作日志
    getOperationLogs: (params) => request('/api/system/logs', { params }),
    // 文件存储
    getFileList: () => request('/api/system/files'),
    deleteFile: (id) => request(`/api/system/files/${id}`, { method: 'DELETE' }),
    downloadFile: (id) => {
      window.location.href = `http://localhost:8080/api/system/files/${id}/download`
    },
    uploadFile: (file) => {
      const formData = new FormData()
      formData.append('file', file)
      return request('/api/upload/file', { method: 'POST', data: formData, headers: { 'Content-Type': 'multipart/form-data' } })
    },
    // 数据统计
    getStats: () => request('/api/system/stats')
  }
};

export default api;