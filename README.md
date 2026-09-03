
# 校园二手交易平台 (SchoolMarket)

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/technologies/downloads/#java17)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-green.svg)](https://spring.io/projects/spring-boot)

## 项目介绍

校园二手交易平台是一个专为高校学生打造的二手物品交易平台，旨在促进校园内闲置物品的流通，实现资源共享与环保理念。

**主要功能：**
- 📦 商品发布与浏览
- 💰 在线交易与支付
- 💬 即时聊天沟通
- ⭐ 商品收藏与评价
- 📊 数据统计与管理

## 技术架构

### 后端技术栈
| 技术 | 版本 | 说明 |
| :--- | :--- | :--- |
| Java | 17 | 编程语言 |
| Spring Boot | 3.2.5 | 后端框架 |
| MyBatis | 3.0.3 | ORM框架 |
| MySQL | 8.0+ | 数据库 |
| JWT | 0.9.1 | 身份认证 |
| Spring Security | 6.x | 安全框架 |

### 前端技术栈
| 技术 | 版本 | 说明 |
| :--- | :--- | :--- |
| UniApp | - | 跨平台框架 |
| Vue.js | 3.x | 前端框架 |
| SCSS | - | CSS预处理器 |

### 项目结构

```
schoolmarket/
├── backend/                    # 后端服务
│   ├── src/main/java/          # Java源码
│   ├── src/main/resources/     # 配置文件
│   └── pom.xml                 # Maven配置
├── frontend-app/               # 移动端前端
│   ├── pages/                  # 页面组件
│   ├── static/                 # 静态资源
│   └── utils/                  # 工具函数
├── frontend-h5/                # H5管理后台
│   ├── src/views/              # 页面视图
│   └── src/utils/              # 工具函数
└── README.md                   # 项目说明
```

## 功能特性

### 用户模块
- 用户注册与登录
- 个人信息管理
- 收货地址管理

### 商品模块
- 商品发布与编辑
- 商品搜索与筛选
- 商品收藏与浏览

### 订单模块
- 订单创建与支付
- 订单状态管理
- 订单评价系统

### 聊天模块
- 即时消息通信
- 聊天会话管理
- 消息状态追踪

### 管理后台
- 用户管理
- 商品审核
- 订单管理
- 数据统计

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 后端启动

1. **克隆项目**
```bash
git clone https://gitee.com/weng-suyi/schoolmarket.git
cd schoolmarket/backend
```

2. **配置数据库**
```bash
# 创建数据库
CREATE DATABASE schoolmarket CHARACTER SET utf8mb4;

# 导入数据库脚本
mysql -u root -p schoolmarket < src/main/resources/schoolmarket_complete.sql
```

3. **修改配置**
编辑 `src/main/resources/application.yaml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/schoolmarket?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

4. **启动服务**
```bash
mvn spring-boot:run
```

服务启动后访问: http://localhost:8080

### 前端启动

**移动端（UniApp）**
```bash
cd frontend-app
npm install
npm run dev:h5  # H5开发
npm run dev:mp-weixin  # 微信小程序开发
```

**管理后台**
```bash
cd frontend-h5
npm install
npm run dev
```

## API 接口文档

后端提供 RESTful API，详细接口说明请查看：
- [API 使用手册](backend/api使用方法.md)
- [系统设计文档](backend/design.md)

## 数据库设计

数据库包含以下核心表：
- `user` - 用户表
- `goods` - 商品表
- `order` - 订单表
- `comment` - 评论表
- `chat_session` - 聊天会话表
- `chat_message` - 聊天消息表

完整的数据库结构请查看：[数据库脚本](backend/src/main/resources/schoolmarket_complete.sql)

## 开发指南

### 代码规范
- 遵循 Java 代码规范（Google 代码风格）
- 使用 Lombok 简化代码
- 异常统一处理
- 日志规范输出

### 分支管理
- `main` - 主分支，稳定版本
- `develop` - 开发分支
- `feature/*` - 功能分支
- `bugfix/*` - 修复分支

### 提交规范
```
feat: 新增功能
fix: 修复bug
docs: 更新文档
style: 代码格式调整
refactor: 代码重构
test: 测试用例
chore: 构建/工具更新
```

## 参与贡献

1. Fork 本仓库
2. 新建 `feature/xxx` 分支
3. 提交代码
4. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证，详见 [LICENSE](LICENSE)

---

**更新时间**: 2026年5月  
**版本**: v1.0
