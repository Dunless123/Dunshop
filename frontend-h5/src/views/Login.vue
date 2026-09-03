<template>
  <div class="login-wrapper">
    <div class="login-container">
      <div class="login-header">
        <div class="logo">
          <div class="logo-text">
            <h1>校园二手市场</h1>
            <p>后台管理系统</p>
          </div>
        </div>
      </div>
      
      <div class="login-form">
        <h2>管理员登录</h2>
        
        <div class="form-group">
          <label for="username">用户名</label>
          <div class="input-wrapper">
            <img class="input-icon" src="../static/用户.png" alt="" />
            <input type="text" id="username" v-model="form.username" placeholder="请输入用户名" />
          </div>
        </div>
        
        <div class="form-group">
          <label for="password">密码</label>
          <div class="input-wrapper">
            <img class="input-icon" src="../static/密码.png" alt="" />
            <input type="password" id="password" v-model="form.password" placeholder="请输入密码" />
          </div>
        </div>
        
        <div class="form-group remember">
          <label>
            <input type="checkbox" v-model="form.remember" />
            <span>记住我</span>
          </label>
          <a href="#" class="forgot-link" @click.prevent="showForgotModal = true">忘记密码?</a>
        </div>
        
        <button class="login-btn" @click="login" :disabled="loading">
          <span v-if="loading" class="loading-spinner"></span>
          {{ loading ? '登录中...' : '登 录' }}
        </button>
        
        <p class="error-message" v-if="errorMessage">{{ errorMessage }}</p>
      </div>
      
      <div class="login-footer">
        <p>© 2026 校园二手市场管理系统</p>
      </div>
    </div>
    
    <!-- 忘记密码弹窗 -->
    <div v-if="showForgotModal" class="modal-overlay" @click="showForgotModal = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>忘记密码</h3>
          <button class="close-btn" @click="showForgotModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label for="forgot-username">用户名</label>
            <input type="text" id="forgot-username" v-model="forgotForm.username" placeholder="请输入用户名" />
          </div>
          <div class="form-group">
            <label for="forgot-new-password">新密码</label>
            <input type="password" id="forgot-new-password" v-model="forgotForm.newPassword" placeholder="请输入新密码" />
          </div>
          <div class="form-group">
            <label for="forgot-confirm-password">确认密码</label>
            <input type="password" id="forgot-confirm-password" v-model="forgotForm.confirmPassword" placeholder="请再次输入新密码" />
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-default" @click="showForgotModal = false">取消</button>
          <button class="btn btn-primary" @click="resetPassword" :disabled="forgotLoading">
            <span v-if="forgotLoading" class="loading-spinner small"></span>
            {{ forgotLoading ? '提交中...' : '重置密码' }}
          </button>
        </div>
        <p class="error-message" v-if="forgotError">{{ forgotError }}</p>
        <p class="success-message" v-if="forgotSuccess">{{ forgotSuccess }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import api from '../utils/api.js'
import { useRouter } from 'vue-router'

const router = useRouter()
const showForgotModal = ref(false)

// 登录表单
const form = ref({
  username: '',
  password: '',
  remember: false
})
const loading = ref(false)
const errorMessage = ref('')

// 忘记密码表单
const forgotForm = ref({
  username: '',
  newPassword: '',
  confirmPassword: ''
})
const forgotLoading = ref(false)
const forgotError = ref('')
const forgotSuccess = ref('')

const login = async () => {
  if (!form.value.username) {
    errorMessage.value = '请输入用户名'
    return
  }
  if (!form.value.password) {
    errorMessage.value = '请输入密码'
    return
  }
  
  try {
    loading.value = true
    errorMessage.value = ''
    const res = await api.auth.login({
      studentId: form.value.username,
      password: form.value.password
    })
    
    if (res.code === 200) {
      if (res.data.user.role !== '管理员') {
        errorMessage.value = '您不是管理员，无法登录后台系统'
        return
      }
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('user', JSON.stringify(res.data.user))
      if (form.value.remember) {
        localStorage.setItem('rememberMe', 'true')
        localStorage.setItem('username', form.value.username)
      } else {
        localStorage.removeItem('rememberMe')
        localStorage.removeItem('username')
      }
      router.push('/dashboard')
    } else {
      errorMessage.value = res.message || '登录失败'
    }
  } catch (error) {
    errorMessage.value = error.message || '网络异常'
  } finally {
    loading.value = false
  }
}

const resetPassword = async () => {
  forgotError.value = ''
  forgotSuccess.value = ''
  
  if (!forgotForm.value.username) {
    forgotError.value = '请输入用户名'
    return
  }
  if (!forgotForm.value.newPassword) {
    forgotError.value = '请输入新密码'
    return
  }
  if (!forgotForm.value.confirmPassword) {
    forgotError.value = '请确认新密码'
    return
  }
  if (forgotForm.value.newPassword !== forgotForm.value.confirmPassword) {
    forgotError.value = '两次输入的密码不一致'
    return
  }
  if (forgotForm.value.newPassword.length < 6) {
    forgotError.value = '密码长度不能少于6位'
    return
  }
  
  try {
    forgotLoading.value = true
    const res = await api.auth.forgot({
      studentId: forgotForm.value.username,
      newPassword: forgotForm.value.newPassword
    })
    
    if (res.code === 200) {
      forgotSuccess.value = '密码修改成功'
      setTimeout(() => {
        showForgotModal.value = false
        forgotForm.value = {
          username: '',
          newPassword: '',
          confirmPassword: ''
        }
        forgotSuccess.value = ''
      }, 2000)
    } else {
      forgotError.value = res.message || '密码修改失败'
    }
  } catch (error) {
    forgotError.value = error.message || '网络异常，请稍后重试'
  } finally {
    forgotLoading.value = false
  }
}

const initForm = () => {
  const rememberMe = localStorage.getItem('rememberMe')
  if (rememberMe === 'true') {
    form.value.remember = true
    form.value.username = localStorage.getItem('username') || ''
  }
}

initForm()
</script>

<style scoped>
.login-wrapper {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
  padding: 20px;
}

.login-container {
  width: 100%;
  max-width: 420px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.login-header {
  padding: 30px 30px 20px;
  background-color: #2c3e50;
  text-align: center;
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
}

.logo-text h1 {
  margin: 0;
  color: #fff;
  font-size: 22px;
  font-weight: 600;
}

.logo-text p {
  margin: 5px 0 0;
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
}

.login-form {
  padding: 30px;
}

.login-form h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 24px;
  font-weight: 600;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-size: 14px;
  font-weight: 500;
}

.input-wrapper {
  position: relative;
}

.input-icon {
  width: 16px;
  height: 16px;
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 16px;
  color: #999;
}

.form-group input {
  width: 100%;
  padding: 14px 15px 14px 45px;
  border: 1px solid #e0e0e0;
  border-radius: 10px;
  font-size: 15px;
  transition: all 0.3s ease;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: #34495e;
  box-shadow: 0 0 0 3px rgba(52, 73, 94, 0.1);
}

.form-group input::placeholder {
  color: #ccc;
}

.form-group.remember {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.form-group.remember label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.form-group.remember input[type="checkbox"] {
  width: auto;
  padding: 0;
  margin: 0;
}

.form-group.remember span {
  font-size: 14px;
  color: #666;
}

.forgot-link {
  color: #34495e;
  text-decoration: none;
  font-size: 14px;
}

.forgot-link:hover {
  text-decoration: underline;
}

.login-btn {
  width: 100%;
  padding: 15px;
  background-color: #34495e;
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
}

.login-btn:hover {
  background-color: #2c3e50;
}

.login-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

.loading-spinner.small {
  width: 16px;
  height: 16px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.error-message {
  color: #ff4757;
  text-align: center;
  margin-top: 15px;
  font-size: 14px;
}

.success-message {
  color: #28a745;
  text-align: center;
  margin-top: 15px;
  font-size: 14px;
}

.login-footer {
  padding: 20px 30px;
  background-color: #f8f9fa;
  text-align: center;
}

.login-footer p {
  margin: 0;
  color: #999;
  font-size: 13px;
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
  max-width: 400px;
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

.modal-body .form-group input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
}

.modal-body .form-group input:focus {
  outline: none;
  border-color: #34495e;
}

.modal-footer {
  padding: 15px 25px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
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

.btn-default {
  background-color: #f0f0f0;
  color: #666;
}

.btn-default:hover:not(:disabled) {
  background-color: #e0e0e0;
}
</style>