<template>
  <div>
    <template v-if="$route.name === 'Login'">
      <router-view />
    </template>
    <template v-else>
      <div class="app-container">
        <aside class="sidebar">
          <h1>后台管理系统</h1>
          <nav>
            <ul>
              <li>
                <router-link to="/dashboard">控制台</router-link>
              </li>
              <li>
                <router-link to="/user">用户管理</router-link>
              </li>
              <li>
                <router-link to="/order">订单管理</router-link>
              </li>
              <li>
                <router-link to="/service">服务信息</router-link>
              </li>
              <li>
                <router-link to="/statistics">互动数据统计</router-link>
              </li>
              <li>
                <router-link to="/batch">批量操作</router-link>
              </li>
              <li>
                <router-link to="/system">系统管理</router-link>
              </li>
              <li>
                <router-link to="/review">评价管理</router-link>
              </li>
            </ul>
          </nav>
        </aside>
        <main class="main-content">
          <header class="header">
            <h2>{{ $route.name }}</h2>
            <button class="logout-btn" @click="handleLogout">退出登录</button>
          </header>
          <router-view />
        </main>
      </div>
    </template>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const handleLogout = () => {
  if (confirm('确定要退出登录吗？')) {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    router.push('/login')
  }
}
</script>

<style scoped>
.app-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.sidebar {
  width: 200px;
  background-color: #333;
  color: white;
  padding: 20px;
}

.sidebar h1 {
  font-size: 18px;
  margin-bottom: 30px;
}

nav ul {
  list-style: none;
  padding: 0;
}

nav ul li {
  margin-bottom: 10px;
}

nav ul li a {
  color: white;
  text-decoration: none;
  display: block;
  padding: 10px;
  border-radius: 4px;
}

nav ul li a:hover {
  background-color: #555;
}

.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f5f5f5;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ddd;
}

.header h2 {
  font-size: 20px;
  margin: 0;
}

.logout-btn {
  padding: 8px 16px;
  background-color: #dc3545;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s ease;
}

.logout-btn:hover {
  background-color: #c82333;
}
</style>