
# 校园二手交易平台 - 后端系统设计文档

## 1. 引言

本文档描述校园二手交易平台后端系统的架构设计、技术选型、核心模块和API接口设计。

## 2. 技术选型

| 分类 | 技术 | 版本 | 说明 |
| :--- | :--- | :--- | :--- |
| 语言 | Java | 17 | 主流企业级语言，性能稳定，生态成熟 |
| 框架 | Spring Boot | 3.2.5 | 社区成熟，生态完善，便于快速开发 |
| 数据库 | MySQL | 8.0+ | 关系型数据库，稳定可靠，适合中小型应用 |
| ORM | MyBatis | 3.0.3 | SQL映射灵活，性能优秀 |
| 安全 | Spring Security | 6.x | 提供认证和授权支持 |
| 认证 | JWT | 0.9.1 | 无状态认证，便于水平扩展 |
| 构建工具 | Maven | 3.x | 依赖管理和项目构建 |

## 3. 架构设计

### 3.1 架构风格

采用经典的三层架构模式：

- **Controller层**：处理HTTP请求，参数校验，调用Service层
- **Service层**：业务逻辑处理，事务管理
- **DAO层（Mapper）**：数据访问，与数据库交互

### 3.2 模块划分

| 模块 | 职责 | 说明 |
| :--- | :--- | :--- |
| controller | 对外REST API控制层 | 处理HTTP请求，返回响应 |
| service | 业务逻辑层 | 封装核心业务逻辑 |
| mapper | 数据访问层 | MyBatis映射接口 |
| entity | 数据实体 | 数据库表对应的实体类 |
| config | 配置类 | Spring配置，如CORS、Security等 |
| utils | 工具类 | JWT、密码加密、响应封装等 |
| aspect | 切面处理 | 操作日志记录等 |

### 3.3 核心流程图

#### 用户登录流程
```
用户请求 → AuthController.login() → UserService.login() → UserMapper.selectByStudentId() → 返回用户 → 生成JWT Token → 返回响应
```

#### 商品发布流程
```
用户请求 → GoodsController.publish() → 参数校验 → GoodsService.save() → GoodsMapper.insert() → 返回响应
```

#### 订单创建流程
```
用户请求 → OrderController.create() → 参数校验 → OrderService.createOrder() → OrderMapper.insert() → 返回响应
```

## 4. 目录结构

```
backend/
├── src/main/java/com/example/schoolmarket/
│   ├── SchoolmarketApplication.java    # 启动类
│   ├── aspect/                         # 切面处理
│   │   └── OperationLogAspect.java     # 操作日志切面
│   ├── config/                         # 配置类
│   │   ├── CorsConfig.java             # CORS跨域配置
│   │   ├── SecurityConfig.java         # 安全配置
│   │   └── WebMvcConfig.java           # Web MVC配置
│   ├── controller/                     # 控制器层
│   │   ├── AdminController.java        # 管理员管理
│   │   ├── AuthController.java         # 用户认证
│   │   ├── CampusController.java       # 校区管理
│   │   ├── CategoryController.java     # 分类管理
│   │   ├── CommentController.java      # 评论管理
│   │   ├── GoodsController.java        # 商品管理
│   │   ├── NewChatController.java      # 聊天管理
│   │   ├── NotificationController.java # 通知管理
│   │   ├── OrderController.java        # 订单管理
│   │   ├── StatisticsController.java   # 统计管理
│   │   ├── SystemController.java       # 系统管理
│   │   ├── UploadController.java       # 文件上传
│   │   └── UserController.java         # 用户管理
│   ├── entity/                         # 实体类
│   │   ├── Address.java                # 地址实体
│   │   ├── Campus.java                 # 校区实体
│   │   ├── Category.java               # 分类实体
│   │   ├── ChatMessage.java            # 聊天消息实体
│   │   ├── ChatSession.java            # 聊天会话实体
│   │   ├── Comment.java                # 评论实体
│   │   ├── Favorite.java               # 收藏实体
│   │   ├── FileStorage.java            # 文件存储实体
│   │   ├── Goods.java                  # 商品实体
│   │   ├── Notification.java           # 通知实体
│   │   ├── NotificationTemplate.java   # 通知模板实体
│   │   ├── OperationLog.java           # 操作日志实体
│   │   ├── Order.java                  # 订单实体
│   │   ├── Pickup.java                 # 自提点实体
│   │   └── User.java                   # 用户实体
│   ├── mapper/                         # 数据访问层
│   │   ├── AddressMapper.java
│   │   ├── CampusMapper.java
│   │   ├── CategoryMapper.java
│   │   ├── ChatMessageMapper.java
│   │   ├── ChatSessionMapper.java
│   │   ├── CommentMapper.java
│   │   ├── FavoriteMapper.java
│   │   ├── FileStorageMapper.java
│   │   ├── GoodsMapper.java
│   │   ├── NotificationMapper.java
│   │   ├── NotificationTemplateMapper.java
│   │   ├── OperationLogMapper.java
│   │   ├── OrderMapper.java
│   │   ├── PickupPointMapper.java
│   │   └── UserMapper.java
│   ├── service/                        # 业务逻辑层
│   │   ├── impl/                       # 实现类
│   │   ├── AddressService.java
│   │   ├── CampusService.java
│   │   ├── CategoryService.java
│   │   ├── ChatMessageService.java
│   │   ├── ChatSessionService.java
│   │   ├── CommentService.java
│   │   ├── FileStorageService.java
│   │   ├── GoodsService.java
│   │   ├── NotificationService.java
│   │   ├── OperationLogService.java
│   │   ├── OrderService.java
│   │   ├── PickupPointService.java
│   │   └── UserService.java
│   └── utils/                          # 工具类
│       ├── JwtUtil.java                # JWT工具类
│       ├── PasswordUtil.java           # 密码加密工具
│       └── ResponseUtil.java           # 响应封装工具
├── src/main/resources/
│   ├── mapper/                         # MyBatis映射文件
│   ├── application.yaml                # 应用配置
│   └── schoolmarket.sql                # 数据库初始化脚本
└── pom.xml                             # Maven依赖配置
```

## 5. 核心类设计

### 5.1 控制器类

#### AuthController

| 方法名 | 功能说明 | 参数 | 返回值 |
| :--- | :--- | :--- | :--- |
| login | 用户登录 | studentId, password | token + 用户信息 |
| register | 用户注册 | User对象 | 注册结果 |
| forgot | 忘记密码 | studentId, newPassword | 重置结果 |
| changePassword | 修改密码 | studentId, oldPassword, newPassword | 修改结果 |
| logout | 用户退出 | 无 | 退出结果 |

#### GoodsController

| 方法名 | 功能说明 | 参数 | 返回值 |
| :--- | :--- | :--- | :--- |
| getList | 获取商品列表 | page, pageSize, categoryId, campusId, keyword | 商品列表 |
| getDetail | 获取商品详情 | id | 商品详情 |
| publish | 发布商品 | 商品信息 | 发布结果 |
| update | 更新商品 | id, 商品信息 | 更新结果 |
| delete | 删除商品 | id | 删除结果 |
| addFavorite | 添加收藏 | goodsId | 收藏结果 |
| removeFavorite | 取消收藏 | goodsId | 取消结果 |
| getMyGoods | 获取我的商品 | page, pageSize, status | 我的商品列表 |
| getFavorites | 获取收藏列表 | page, pageSize | 收藏列表 |

#### OrderController

| 方法名 | 功能说明 | 参数 | 返回值 |
| :--- | :--- | :--- | :--- |
| getList | 获取订单列表 | page, pageSize, status, keyword | 订单列表 |
| getDetail | 获取订单详情 | id | 订单详情 |
| create | 创建订单 | 订单信息 | 创建结果 |
| pay | 支付订单 | id | 支付结果 |
| ship | 发货 | id | 发货结果 |
| confirm | 确认收货 | id | 确认结果 |
| cancel | 取消订单 | id | 取消结果 |
| refund | 退款 | id | 退款结果 |

### 5.2 服务类

#### UserService

| 方法名 | 功能说明 | 参数 | 返回值 |
| :--- | :--- | :--- | :--- |
| login | 用户登录验证 | studentId, password | User对象 |
| register | 用户注册 | User对象 | 布尔值 |
| resetPassword | 重置密码 | studentId, newPassword | 布尔值 |
| getById | 根据ID获取用户 | id | User对象 |
| update | 更新用户信息 | User对象 | 布尔值 |
| delete | 删除用户 | id | 布尔值 |
| list | 获取用户列表 | 无 | 用户列表 |

#### GoodsService

| 方法名 | 功能说明 | 参数 | 返回值 |
| :--- | :--- | :--- | :--- |
| getList | 获取商品列表 | offset, pageSize, categoryId, campusId, keyword | 商品列表 |
| getById | 根据ID获取商品 | id | Goods对象 |
| save | 保存商品 | Goods对象 | 布尔值 |
| update | 更新商品 | Goods对象 | 布尔值 |
| delete | 删除商品 | id | 布尔值 |
| addViewCount | 增加浏览量 | id | 布尔值 |
| addFavorite | 添加收藏 | userId, goodsId | 布尔值 |
| removeFavorite | 取消收藏 | userId, goodsId | 布尔值 |
| getFavorites | 获取收藏列表 | userId, offset, pageSize | 商品列表 |

### 5.3 工具类

#### JwtUtil

| 方法名 | 功能说明 | 参数 | 返回值 |
| :--- | :--- | :--- | :--- |
| generateToken | 生成JWT Token | userId, username | Token字符串 |
| getUserIdFromToken | 从Token中提取用户ID | token | 用户ID |
| validateToken | 验证Token有效性 | token | 布尔值 |

#### ResponseUtil

| 方法名 | 功能说明 | 参数 | 返回值 |
| :--- | :--- | :--- | :--- |
| success | 成功响应 | data, message | HashMap |
| error | 错误响应 | code, message | HashMap |

#### PasswordUtil

| 方法名 | 功能说明 | 参数 | 返回值 |
| :--- | :--- | :--- | :--- |
| encode | 密码加密 | rawPassword | 加密后的密码 |
| matches | 密码验证 | rawPassword, encodedPassword | 布尔值 |

## 6. 数据库设计

### 6.1 数据库表关系图

```
user (用户表)
    ├── address (地址表)
    ├── goods (商品表)
    ├── order (订单表)
    ├── comment (评论表)
    ├── favorite (收藏表)
    ├── chat_session (聊天会话表)
    ├── chat_message (聊天消息表)
    └── notification (通知表)

category (分类表)
    └── goods (商品表)

campus (校区表)
    ├── user (用户表)
    └── goods (商品表)

order (订单表)
    └── comment (评论表)
```

### 6.2 核心表结构

#### user (用户表)

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 用户ID |
| student_id | VARCHAR(50) | UNIQUE, NOT NULL | 学号 |
| username | VARCHAR(100) | NOT NULL | 用户名 |
| password | VARCHAR(255) | NOT NULL | 密码（加密） |
| email | VARCHAR(100) | UNIQUE | 邮箱 |
| phone | VARCHAR(20) | UNIQUE | 手机号 |
| avatar | VARCHAR(255) | | 头像URL |
| campus_id | BIGINT | FOREIGN KEY | 校区ID |
| role | VARCHAR(20) | DEFAULT 'user' | 角色：admin/user |
| status | VARCHAR(20) | DEFAULT '正常' | 状态：正常/禁用 |
| auth_status | VARCHAR(20) | DEFAULT '未认证' | 认证状态 |
| create_time | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

#### goods (商品表)

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 商品ID |
| title | VARCHAR(200) | NOT NULL | 商品标题 |
| description | TEXT | | 商品描述 |
| price | DECIMAL(10,2) | NOT NULL | 售价 |
| original_price | DECIMAL(10,2) | | 原价 |
| images | TEXT | | 图片URL(JSON数组) |
| category_id | BIGINT | FOREIGN KEY | 分类ID |
| tags | TEXT | | 标签(JSON数组) |
| campus_id | BIGINT | FOREIGN KEY | 校区ID |
| seller_id | BIGINT | FOREIGN KEY | 卖家ID |
| quality | VARCHAR(20) | | 商品成色 |
| trade_methods | TEXT | | 交易方式(JSON数组) |
| view_count | INT | DEFAULT 0 | 浏览量 |
| favorite_count | INT | DEFAULT 0 | 收藏量 |
| status | VARCHAR(20) | DEFAULT '在售' | 状态：在售/已下架/已售出 |
| create_time | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

#### order (订单表)

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 订单ID |
| order_no | VARCHAR(50) | UNIQUE, NOT NULL | 订单编号 |
| buyer_id | BIGINT | FOREIGN KEY | 买家ID |
| seller_id | BIGINT | FOREIGN KEY | 卖家ID |
| goods_id | BIGINT | FOREIGN KEY | 商品ID |
| goods_title | VARCHAR(200) | | 商品标题 |
| goods_image | VARCHAR(255) | | 商品图片 |
| price | DECIMAL(10,2) | NOT NULL | 订单金额 |
| address_id | BIGINT | FOREIGN KEY | 收货地址ID |
| status | VARCHAR(20) | DEFAULT '待支付' | 订单状态 |
| trade_method | VARCHAR(50) | | 交易方式 |
| create_time | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

#### comment (评论表)

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 评论ID |
| user_id | BIGINT | FOREIGN KEY | 用户ID |
| goods_id | BIGINT | FOREIGN KEY | 商品ID |
| order_id | BIGINT | FOREIGN KEY | 订单ID |
| rating | INT | NOT NULL | 评分(1-5) |
| content | TEXT | NOT NULL | 评价内容 |
| images | TEXT | | 评价图片(JSON数组) |
| reply | TEXT | | 卖家回复 |
| type | VARCHAR(20) | DEFAULT 'buyer' | 评价类型 |
| create_time | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

#### chat_session (聊天会话表)

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 会话ID |
| user_id1 | BIGINT | FOREIGN KEY | 用户1ID |
| user_id2 | BIGINT | FOREIGN KEY | 用户2ID |
| last_message | TEXT | | 最后一条消息 |
| unread_count1 | INT | DEFAULT 0 | 用户1未读数量 |
| unread_count2 | INT | DEFAULT 0 | 用户2未读数量 |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

#### chat_message (聊天消息表)

| 字段名 | 类型 | 约束 | 说明 |
| :--- | :--- | :--- | :--- |
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 消息ID |
| session_id | BIGINT | FOREIGN KEY | 会话ID |
| sender_id | BIGINT | FOREIGN KEY | 发送者ID |
| receiver_id | BIGINT | FOREIGN KEY | 接收者ID |
| content | TEXT | NOT NULL | 消息内容 |
| status | TINYINT | DEFAULT 0 | 消息状态 |
| message_type | TINYINT | DEFAULT 0 | 消息类型 |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

## 7. API接口设计

### 7.1 认证模块

| API路径 | HTTP方法 | 功能描述 | 是否需要认证 |
| :--- | :--- | :--- | :--- |
| /api/auth/login | POST | 用户登录 | 否 |
| /api/auth/register | POST | 用户注册 | 否 |
| /api/auth/forgot | POST | 忘记密码 | 否 |
| /api/auth/change-password | POST | 修改密码 | 否 |
| /api/auth/logout | POST | 用户退出 | 是 |

### 7.2 用户模块

| API路径 | HTTP方法 | 功能描述 | 是否需要认证 |
| :--- | :--- | :--- | :--- |
| /api/user/info | GET | 获取当前用户信息 | 是 |
| /api/user/{id} | GET | 获取用户信息 | 否 |
| /api/user/update | PUT | 更新用户信息 | 是 |
| /api/user/addresses | GET | 获取用户地址列表 | 是 |
| /api/user/addresses | POST | 添加地址 | 是 |
| /api/user/addresses/{id} | PUT | 更新地址 | 是 |
| /api/user/addresses/{id} | DELETE | 删除地址 | 是 |
| /api/user/{id}/stats | GET | 获取用户统计 | 否 |
| /api/user/change-password | POST | 修改密码 | 是 |

### 7.3 商品模块

| API路径 | HTTP方法 | 功能描述 | 是否需要认证 |
| :--- | :--- | :--- | :--- |
| /api/goods/list | GET | 获取商品列表 | 否 |
| /api/goods/{id} | GET | 获取商品详情 | 否 |
| /api/goods/{id}/view | POST | 增加浏览量 | 否 |
| /api/goods/publish | POST | 发布商品 | 是 |
| /api/goods/{id} | PUT | 更新商品 | 是 |
| /api/goods/{id} | DELETE | 删除商品 | 是 |
| /api/goods/{id}/favorite | POST | 添加收藏 | 是 |
| /api/goods/{id}/favorite | DELETE | 取消收藏 | 是 |
| /api/goods/my | GET | 获取我的商品 | 是 |
| /api/goods/favorites/check | GET | 检查是否收藏 | 是 |
| /api/goods/favorites | GET | 获取收藏列表 | 是 |

### 7.4 订单模块

| API路径 | HTTP方法 | 功能描述 | 是否需要认证 |
| :--- | :--- | :--- | :--- |
| /api/order/list | GET | 获取订单列表 | 是 |
| /api/order/{id} | GET | 获取订单详情 | 是 |
| /api/order/create | POST | 创建订单 | 是 |
| /api/order/{id}/pay | POST | 支付订单 | 是 |
| /api/order/{id}/ship | POST | 发货 | 是 |
| /api/order/{id}/confirm | POST | 确认收货 | 是 |
| /api/order/{id}/cancel | POST | 取消订单 | 是 |
| /api/order/{id}/refund | POST | 退款 | 是 |
| /api/order/{id}/status | PUT | 更新订单状态 | 是 |
| /api/order/batch/status | PUT | 批量更新状态 | 是 |
| /api/order/batch/refund | POST | 批量退款 | 是 |

### 7.5 评论模块

| API路径 | HTTP方法 | 功能描述 | 是否需要认证 |
| :--- | :--- | :--- | :--- |
| /api/comment/list | GET | 获取商品评论列表 | 否 |
| /api/comment/create | POST | 创建评论 | 是 |
| /api/comment/{id}/reply | POST | 回复评论 | 是 |
| /api/comment/my | GET | 获取我的评论 | 是 |
| /api/comment/seller | GET | 获取卖家评论 | 是 |
| /api/comment/all | GET | 获取所有评论 | 是 |
| /api/comment/{id}/rating | PUT | 更新评分 | 是 |
| /api/comment/{id} | DELETE | 删除评论 | 是 |

### 7.6 聊天模块

| API路径 | HTTP方法 | 功能描述 | 是否需要认证 |
| :--- | :--- | :--- | :--- |
| /api/chat/sessions | GET | 获取聊天会话列表 | 是 |
| /api/chat/messages | GET | 获取会话消息 | 是 |
| /api/chat/send | POST | 发送消息 | 是 |
| /api/chat/session | GET | 获取或创建会话 | 是 |

### 7.7 通知模块

| API路径 | HTTP方法 | 功能描述 | 是否需要认证 |
| :--- | :--- | :--- | :--- |
| /api/notification/list | GET | 获取通知列表 | 是 |
| /api/notification/{id} | GET | 获取通知详情 | 是 |
| /api/notification/send | POST | 发送通知 | 是 |
| /api/notification/{id}/resend | POST | 重发通知 | 是 |
| /api/notification/{id} | DELETE | 删除通知 | 是 |
| /api/notification/templates | GET | 获取通知模板 | 是 |
| /api/notification/templates/{id} | GET | 获取模板详情 | 是 |
| /api/notification/templates | POST | 添加模板 | 是 |
| /api/notification/templates/{id} | PUT | 更新模板 | 是 |
| /api/notification/templates/{id} | DELETE | 删除模板 | 是 |

### 7.8 分类模块

| API路径 | HTTP方法 | 功能描述 | 是否需要认证 |
| :--- | :--- | :--- | :--- |
| /api/category/list | GET | 获取分类列表 | 否 |
| /api/category/{id} | GET | 获取分类详情 | 否 |

### 7.9 校区模块

| API路径 | HTTP方法 | 功能描述 | 是否需要认证 |
| :--- | :--- | :--- | :--- |
| /api/campus/list | GET | 获取校区列表 | 否 |
| /api/campus/{id} | GET | 获取校区详情 | 否 |

### 7.10 上传模块

| API路径 | HTTP方法 | 功能描述 | 是否需要认证 |
| :--- | :--- | :--- | :--- |
| /api/upload/image | POST | 上传图片 | 是 |
| /api/upload/avatar | POST | 上传头像 | 是 |
| /api/upload/file | POST | 上传文件 | 是 |

### 7.11 统计模块

| API路径 | HTTP方法 | 功能描述 | 是否需要认证 |
| :--- | :--- | :--- | :--- |
| /api/statistics/dashboard | GET | 获取仪表盘统计 | 是 |
| /api/statistics/goods | GET | 获取商品统计 | 是 |
| /api/statistics/orders | GET | 获取订单统计 | 是 |
| /api/statistics/interaction | GET | 获取互动统计 | 是 |

### 7.12 管理员模块

| API路径 | HTTP方法 | 功能描述 | 是否需要认证 |
| :--- | :--- | :--- | :--- |
| /api/admin/users | GET | 获取用户列表 | 是 |
| /api/admin/users/{id} | PUT | 更新用户 | 是 |
| /api/admin/users/{id} | DELETE | 删除用户 | 是 |
| /api/admin/statistics | GET | 获取统计数据 | 是 |
| /api/admin/dashboard | GET | 获取仪表盘 | 是 |
| /api/admin/orders | GET | 获取订单列表 | 是 |
| /api/admin/goods | GET | 获取商品列表 | 是 |
| /api/admin/goods/batch/approve | PUT | 批量审核商品 | 是 |
| /api/admin/goods/batch/reject | PUT | 批量拒绝商品 | 是 |
| /api/admin/users/batch/enable | PUT | 批量启用用户 | 是 |
| /api/admin/users/batch/disable | PUT | 批量禁用用户 | 是 |

### 7.13 系统模块

| API路径 | HTTP方法 | 功能描述 | 是否需要认证 |
| :--- | :--- | :--- | :--- |
| /api/system/campus | GET | 获取校区列表 | 是 |
| /api/system/campus | POST | 添加校区 | 是 |
| /api/system/campus/{id} | PUT | 更新校区 | 是 |
| /api/system/campus/{id} | DELETE | 删除校区 | 是 |
| /api/system/pickup | GET | 获取自提点列表 | 是 |
| /api/system/pickup | POST | 添加自提点 | 是 |
| /api/system/pickup/{id} | PUT | 更新自提点 | 是 |
| /api/system/pickup/{id} | DELETE | 删除自提点 | 是 |
| /api/system/logs | GET | 获取操作日志 | 是 |
| /api/system/files | GET | 获取文件列表 | 是 |
| /api/system/files/{id}/download | GET | 下载文件 | 是 |
| /api/system/files/{id} | DELETE | 删除文件 | 是 |
| /api/system/stats | GET | 获取系统统计 | 是 |

## 8. 安全设计

### 8.1 JWT认证

系统使用JWT进行无状态认证：

1. 用户登录成功后，服务端生成JWT Token
2. Token包含用户ID和用户名信息
3. 客户端在后续请求中携带Token（放在Authorization Header中）
4. 服务端验证Token有效性

### 8.2 密码安全

- 使用BCrypt算法对密码进行加密存储
- 登录时验证密码时，使用BCrypt进行匹配
- 禁止明文存储密码

### 8.3 权限控制

- 使用Spring Security进行权限控制
- 区分普通用户和管理员角色
- 敏感操作需要管理员权限

### 8.4 CORS配置

- 配置允许的来源域名
- 配置允许的HTTP方法
- 配置允许的请求头

## 9. 部署与集成

### 9.1 环境要求

| 项目 | 要求 |
| :--- | :--- |
| JDK | 17+ |
| MySQL | 8.0+ |
| Maven | 3.6+ |

### 9.2 配置说明

在 `application.yaml` 中配置：

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/schoolmarket?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis:
  configuration:
    map-underscore-to-camel-case: true
  mapper-locations: classpath:mapper/*.xml

jwt:
  secret: schoolmarket_secret_key
  expiration: 86400
```

### 9.3 启动方式

**开发环境：**
```bash
cd backend
mvn spring-boot:run
```

**生产环境：**
```bash
cd backend
mvn clean package
java -jar target/schoolmarket-0.0.1-SNAPSHOT.jar
```

## 10. 错误处理

### 10.1 统一响应格式

所有API响应统一格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {...}
}
```

### 10.2 错误码定义

| 错误码 | 含义 |
| :--- | :--- |
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

**文档版本**: v1.0  
**创建时间**: 2026年  
**适用项目**: 校园二手交易平台
