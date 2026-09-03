// API服务封装
const baseURL = 'http://localhost:8080';

// 请求重试次数
const MAX_RETRIES = 2;

// 封装请求方法
const request = (url, options = {}, retryCount = 0) => {
  const token = uni.getStorageSync('token');
  
  let finalUrl = baseURL + url;
  const method = options.method || 'GET';
  let reqData = options.data || {};
  
  if (method === 'GET') {
    const timestamp = new Date().getTime();
    const params = { ...options.params, _t: timestamp };
    const queryString = Object.keys(params).map(key => 
      `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`
    ).join('&');
    if (queryString) {
      finalUrl += (url.includes('?') ? '&' : '?') + queryString;
    }
  } else {
    reqData = options.data || {};
  }
  
  return new Promise((resolve, reject) => {
    uni.request({
      url: finalUrl,
      method: method,
      data: reqData,
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : ''
      },
      timeout: 15000,
      success: (res) => {
        if (res.statusCode === 200) {
          const data = res.data;
          if (data.code === 401) {
            uni.removeStorageSync('token');
            uni.removeStorageSync('user');
            uni.showToast({ title: '登录已失效，请重新登录', icon: 'none' });
            setTimeout(() => {
              uni.reLaunch({ url: '/pages/auth/login' });
            }, 1500);
            reject(data);
          } else {
            resolve(data);
          }
        } else if (res.statusCode === 401) {
          uni.removeStorageSync('token');
          uni.removeStorageSync('user');
          uni.showToast({ title: '登录已失效，请重新登录', icon: 'none' });
          setTimeout(() => {
            uni.reLaunch({ url: '/pages/auth/login' });
          }, 1500);
          reject(res.data);
        } else {
          reject(res.data);
        }
      },
      fail: (err) => {
        console.error('Request failed:', err);
        
        if (retryCount < MAX_RETRIES) {
          console.log(`Retry ${retryCount + 1}/${MAX_RETRIES} for ${url}`);
          setTimeout(() => {
            request(url, options, retryCount + 1).then(resolve).catch(reject);
          }, 1000 * (retryCount + 1));
        } else {
          if (err.errMsg && err.errMsg.includes('timeout')) {
            uni.showToast({ title: '网络请求超时', icon: 'none' });
          } else if (err.errMsg && err.errMsg.includes('connect')) {
            uni.showToast({ title: '无法连接到服务器', icon: 'none' });
          } else {
            uni.showToast({ title: '网络请求失败', icon: 'none' });
          }
          reject(err);
        }
      }
    });
  });
};

const api = {
  auth: {
    login: (data) => request('/api/auth/login', { method: 'POST', data }),
    register: (data) => request('/api/auth/register', { method: 'POST', data }),
    forgot: (data) => request('/api/auth/forgot', { method: 'POST', data }),
    wechatLogin: (code) => request('/api/auth/wechat/login', { method: 'POST', data: { code } }),
    wechatRegister: (openId, nickname, avatar) => request('/api/auth/wechat/register', { method: 'POST', data: { openId, nickname, avatar } }),
    wechatBind: (userId, code) => request('/api/auth/wechat/bind', { method: 'POST', data: { userId, code } })
  },
  
  user: {
    getInfo: () => request('/api/user/info'),
    getById: (id) => request(`/api/user/${id}`),
    update: (data) => request('/api/user/update', { method: 'PUT', data }),
    changePassword: (data) => request('/api/user/change-password', { method: 'POST', data }),
    getAddresses: () => request('/api/user/addresses'),
    addAddress: (data) => request('/api/user/addresses', { method: 'POST', data }),
    updateAddress: (id, data) => request(`/api/user/addresses/${id}`, { method: 'PUT', data }),
    deleteAddress: (id) => request(`/api/user/addresses/${id}`, { method: 'DELETE' }),
    getStats: (userId) => request(`/api/user/${userId}/stats`),
    getAuthStatus: () => request('/api/user/auth/status'),
    submitAuth: (studentId, name) => request('/api/user/auth/submit', { method: 'POST', data: { studentId, name } }),
    getFavoritePickupPoints: () => request('/api/user/pickup-points'),
    addFavoritePickupPoint: (pickupId) => request(`/api/user/pickup-points/${pickupId}`, { method: 'POST' }),
    removeFavoritePickupPoint: (pickupId) => request(`/api/user/pickup-points/${pickupId}`, { method: 'DELETE' })
  },
  
  goods: {
    list: (params) => request('/api/goods/list', { params }),
    detail: (id) => request(`/api/goods/${id}`),
    addView: (id) => request(`/api/goods/${id}/view`, { method: 'POST' }),
    publish: (data) => request('/api/goods/publish', { method: 'POST', data }),
    update: (id, data) => request(`/api/goods/${id}`, { method: 'PUT', data }),
    delete: (id) => request(`/api/goods/${id}`, { method: 'DELETE' }),
    favorite: (id) => request(`/api/goods/${id}/favorite`, { method: 'POST' }),
    cancelFavorite: (id) => request(`/api/goods/${id}/favorite`, { method: 'DELETE' }),
    getMyGoods: (params) => request('/api/goods/my', { params }),
    getFavorites: (params) => request('/api/goods/favorites', { data: params }),
    checkFavorite: (goodsId) => request(`/api/goods/favorites/check?goodsId=${goodsId}`),
    getGoodsOrders: (goodsId, status) => request(`/api/goods/${goodsId}/orders`, { params: { status } }),
  },
  
  order: {
    list: (params) => request('/api/order/list', { params }),
    detail: (id) => request(`/api/order/${id}`),
    create: (data) => request('/api/order/create', { method: 'POST', data }),
    pay: (id) => request(`/api/order/${id}/pay`, { method: 'POST' }),
    cancel: (id) => request(`/api/order/${id}/cancel`, { method: 'POST' }),
    confirm: (id) => request(`/api/order/${id}/confirm`, { method: 'POST' }),
    ship: (id) => request(`/api/order/${id}/ship`, { method: 'POST' }),
    update: (id, data) => request(`/api/order/${id}/update`, { method: 'PUT', data }),
    applyRefund: (id, data) => request(`/api/order/${id}/refund`, { method: 'POST', data }),
    sellerList: (params) => request('/api/order/seller/list', { params })
  },
  
  chat: {
    getSessions: () => request('/api/chat/sessions'),
    getMessages: (sessionId, page, pageSize) => request('/api/chat/messages', { params: { sessionId, page, pageSize } }),
    sendMessage: (receiverId, content) => request('/api/chat/send', { method: 'POST', data: { receiverId, content } }),
    getSession: (otherUserId) => request('/api/chat/session', { params: { otherUserId } })
  },
  
  comment: {
    list: (params) => request('/api/comment/list', { data: params }),
    create: (data) => request('/api/comment/create', { method: 'POST', data }),
    reply: (id, data) => request(`/api/comment/${id}/reply`, { method: 'POST', data }),
    getMyComments: (params) => request('/api/comment/my', { params }),
    getSellerComments: (params) => request('/api/comment/seller', { params })
  },
  
  category: {
    list: () => request('/api/category/list'),
    detail: (id) => request(`/api/category/${id}`)
  },
  
  campus: {
    list: () => request('/api/campus/list'),
    detail: (id) => request(`/api/campus/${id}`),
    getAll: () => request('/api/campus/all')
  },
  
  pickup: {
    getAll: () => request('/api/pickup-points/all')
  },
  
  upload: {
    image: (data) => request('/api/upload/image', { method: 'POST', data }),
    avatar: (data) => request('/api/upload/avatar', { method: 'POST', data })
  },
  
  statistics: {
    dashboard: () => request('/api/statistics/dashboard'),
    goods: () => request('/api/statistics/goods'),
    orders: () => request('/api/statistics/orders')
  },
  
  cache: {
    getHotGoods: () => request('/api/cache/hot-goods'),
    refreshHotGoods: () => request('/api/cache/hot-goods', { method: 'POST' }),
    getCategories: () => request('/api/cache/categories'),
    refreshCategories: () => request('/api/cache/categories', { method: 'POST' }),
    getUserAuth: (userId) => request(`/api/cache/user-auth/${userId}`),
    setUserAuth: (userId, token) => request(`/api/cache/user-auth/${userId}`, { method: 'POST', data: { token } }),
    removeUserAuth: (userId) => request(`/api/cache/user-auth/${userId}`, { method: 'DELETE' }),
    getOrderStatus: (orderId) => request(`/api/cache/order-status/${orderId}`),
    setOrderStatus: (orderId, status) => request(`/api/cache/order-status/${orderId}`, { method: 'POST', data: { status } }),
    removeOrderStatus: (orderId) => request(`/api/cache/order-status/${orderId}`, { method: 'DELETE' }),
    clearCache: (key) => request(`/api/cache/clear/${key}`, { method: 'DELETE' }),
    exists: (key) => request(`/api/cache/exists/${key}`)
  },
  
  recommend: {
    userBased: (userId, limit = 10) => request('/api/recommend/user-based', { params: { userId, limit } }),
    itemBased: (userId, limit = 10) => request('/api/recommend/item-based', { params: { userId, limit } }),
    hybrid: (userId, limit = 10) => request('/api/recommend/hybrid', { params: { userId, limit } }),
    cached: (userId) => request('/api/recommend/cached', { params: { userId } }),
    refresh: (userId) => request(`/api/recommend/refresh/${userId}`, { method: 'POST' })
  },
  
  payPassword: {
    set: (userId, password) => request('/api/pay-password/set', { method: 'POST', data: { userId, password } }),
    verify: (userId, password) => request('/api/pay-password/verify', { method: 'POST', data: { userId, password } }),
    change: (userId, oldPassword, newPassword) => request('/api/pay-password/change', { method: 'POST', data: { userId, oldPassword, newPassword } }),
    reset: (userId, newPassword) => request('/api/pay-password/reset', { method: 'POST', data: { userId, newPassword } }),
    has: (userId) => request('/api/pay-password/has', { params: { userId } })
  },
  
  mockPay: {
    create: (orderId) => request('/api/mock/pay/create', { method: 'POST', data: { orderId } }),
    confirm: (orderId, userId, payPassword) => request('/api/mock/pay/confirm', { method: 'POST', data: { orderId, userId, payPassword } }),
    cancel: (orderId) => request('/api/mock/pay/cancel', { method: 'POST', data: { orderId } }),
    refund: (orderId, reason) => request('/api/mock/pay/refund', { method: 'POST', data: { orderId, reason } }),
    status: (orderId) => request('/api/mock/pay/status', { params: { orderId } }),
    info: () => request('/api/mock/pay/info')
  },
  
  notification: {
    list: (params) => request('/api/notification/list', { params }),
    detail: (id) => request(`/api/notification/${id}`),
    send: (data) => request('/api/notification/send', { method: 'POST', data }),
    resend: (id) => request(`/api/notification/${id}/resend`, { method: 'POST' }),
    delete: (id) => request(`/api/notification/${id}`, { method: 'DELETE' })
  }
};

export default api;