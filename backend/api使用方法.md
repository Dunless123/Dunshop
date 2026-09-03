
# 校园二手交易平台 - 后端API使用手册

## 目录

1. [基础说明](#基础说明)
2. [认证模块](#认证模块)
3. [用户模块](#用户模块)
4. [商品模块](#商品模块)
5. [订单模块](#订单模块)
6. [评论模块](#评论模块)
7. [聊天模块](#聊天模块)
8. [通知模块](#通知模块)
9. [分类模块](#分类模块)
10. [校区模块](#校区模块)
11. [上传模块](#上传模块)
12. [统计模块](#统计模块)
13. [管理员模块](#管理员模块)
14. [系统模块](#系统模块)

---

## 基础说明

### 服务地址

- 开发环境: `http://localhost:8080`
- 生产环境: 根据部署配置

### 认证方式

所有需要认证的接口，请求头需要携带：

```
Authorization: Bearer <token>
```

### 响应格式

所有接口统一返回JSON格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

### 错误码

| 错误码 | 含义 |
| :--- | :--- |
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 认证模块

### 1. 用户登录

**接口地址**: `POST /api/auth/login`

**请求体**:

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| studentId | String | 是 | 学号 |
| password | String | 是 | 密码 |

**请求示例**:

```json
{
  "studentId": "20210001",
  "password": "123456"
}
```

**成功响应**:

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "studentId": "20210001",
      "username": "张三",
      "email": "zhangsan@example.com",
      "phone": "13800138000",
      "avatar": "/uploads/avatars/xxx.png",
      "campusId": 1,
      "role": "user",
      "status": "正常",
      "authStatus": "已认证"
    }
  }
}
```

### 2. 用户注册

**接口地址**: `POST /api/auth/register`

**请求体**:

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| studentId | String | 是 | 学号 |
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |
| email | String | 否 | 邮箱 |
| phone | String | 否 | 手机号 |
| campusId | Long | 否 | 校区ID |

**请求示例**:

```json
{
  "studentId": "20210002",
  "username": "李四",
  "password": "123456",
  "email": "lisi@example.com",
  "phone": "13900139000",
  "campusId": 1
}
```

**成功响应**:

```json
{
  "code": 200,
  "message": "注册成功",
  "data": null
}
```

### 3. 忘记密码

**接口地址**: `POST /api/auth/forgot`

**请求体**:

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| studentId | String | 是 | 学号 |
| newPassword | String | 是 | 新密码 |

**请求示例**:

```json
{
  "studentId": "20210001",
  "newPassword": "654321"
}
```

**成功响应**:

```json
{
  "code": 200,
  "message": "密码重置成功",
  "data": null
}
```

### 4. 修改密码（无需登录）

**接口地址**: `POST /api/auth/change-password`

**请求体**:

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| studentId | String | 是 | 学号 |
| oldPassword | String | 是 | 旧密码 |
| newPassword | String | 是 | 新密码 |

**请求示例**:

```json
{
  "studentId": "20210001",
  "oldPassword": "123456",
  "newPassword": "654321"
}
```

**成功响应**:

```json
{
  "code": 200,
  "message": "密码修改成功",
  "data": null
}
```

### 5. 用户退出

**接口地址**: `POST /api/auth/logout`

**认证要求**: 需要登录

**成功响应**:

```json
{
  "code": 200,
  "message": "退出登录成功",
  "data": null
}
```

---

## 用户模块

### 1. 获取当前用户信息

**接口地址**: `GET /api/user/info`

**认证要求**: 需要登录

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "studentId": "20210001",
    "username": "张三",
    "email": "zhangsan@example.com",
    "phone": "13800138000",
    "avatar": "/uploads/avatars/xxx.png",
    "campusId": 1,
    "role": "user",
    "status": "正常",
    "authStatus": "已认证",
    "createTime": "2024-01-01T10:00:00",
    "updateTime": "2024-01-01T10:00:00"
  }
}
```

### 2. 获取指定用户信息

**接口地址**: `GET /api/user/{id}`

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 用户ID |

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "studentId": "20210001",
    "username": "张三",
    "email": "zhangsan@example.com",
    "phone": "13800138000",
    "avatar": "/uploads/avatars/xxx.png",
    "campusId": 1,
    "role": "user",
    "status": "正常",
    "authStatus": "已认证"
  }
}
```

### 3. 更新用户信息

**接口地址**: `PUT /api/user/update`

**认证要求**: 需要登录

**请求体**:

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| username | String | 否 | 用户名 |
| email | String | 否 | 邮箱 |
| phone | String | 否 | 手机号 |
| avatar | String | 否 | 头像URL |
| campusId | Long | 否 | 校区ID |

**请求示例**:

```json
{
  "username": "张三修改",
  "email": "zhangsan_new@example.com",
  "phone": "13800138001"
}
```

**成功响应**:

```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

### 4. 获取用户地址列表

**接口地址**: `GET /api/user/addresses`

**认证要求**: 需要登录

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "name": "张三",
      "phone": "13800138000",
      "province": "北京市",
      "city": "北京市",
      "district": "海淀区",
      "detail": "中关村大街1号",
      "isDefault": true,
      "createTime": "2024-01-01T10:00:00",
      "updateTime": "2024-01-01T10:00:00"
    }
  ]
}
```

### 5. 添加地址

**接口地址**: `POST /api/user/addresses`

**认证要求**: 需要登录

**请求体**:

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| name | String | 是 | 收货人姓名 |
| phone | String | 是 | 手机号 |
| province | String | 是 | 省份 |
| city | String | 是 | 城市 |
| district | String | 是 | 区县 |
| detail | String | 是 | 详细地址 |
| isDefault | Boolean | 否 | 是否默认地址 |

**请求示例**:

```json
{
  "name": "张三",
  "phone": "13800138000",
  "province": "北京市",
  "city": "北京市",
  "district": "海淀区",
  "detail": "中关村大街1号",
  "isDefault": true
}
```

**成功响应**:

```json
{
  "code": 200,
  "message": "添加成功",
  "data": {
    "id": 1,
    "userId": 1,
    "name": "张三",
    "phone": "13800138000",
    "province": "北京市",
    "city": "北京市",
    "district": "海淀区",
    "detail": "中关村大街1号",
    "isDefault": true
  }
}
```

### 6. 更新地址

**接口地址**: `PUT /api/user/addresses/{id}`

**认证要求**: 需要登录

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 地址ID |

**请求体**:

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| name | String | 否 | 收货人姓名 |
| phone | String | 否 | 手机号 |
| province | String | 否 | 省份 |
| city | String | 否 | 城市 |
| district | String | 否 | 区县 |
| detail | String | 否 | 详细地址 |
| isDefault | Boolean | 否 | 是否默认地址 |

**成功响应**:

```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

### 7. 删除地址

**接口地址**: `DELETE /api/user/addresses/{id}`

**认证要求**: 需要登录

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 地址ID |

**成功响应**:

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

### 8. 获取用户统计

**接口地址**: `GET /api/user/{id}/stats`

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 用户ID |

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "totalGoods": 10,
    "totalOrders": 5,
    "totalSales": 5000.00,
    "totalComments": 3
  }
}
```

### 9. 修改密码（登录后）

**接口地址**: `POST /api/user/change-password`

**认证要求**: 需要登录

**请求体**:

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| oldPassword | String | 是 | 旧密码 |
| newPassword | String | 是 | 新密码 |

**请求示例**:

```json
{
  "oldPassword": "123456",
  "newPassword": "654321"
}
```

**成功响应**:

```json
{
  "code": 200,
  "message": "密码修改成功",
  "data": null
}
```

---

## 商品模块

### 1. 获取商品列表

**接口地址**: `GET /api/goods/list`

**请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| page | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 10 | 每页数量 |
| categoryId | Long | 否 | - | 分类ID |
| campusId | Long | 否 | - | 校区ID |
| keyword | String | 否 | - | 搜索关键词 |
| sortBy | String | 否 | - | 排序字段 |
| order | String | 否 | desc | 排序方向 |

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "list": [
      {
        "id": 1,
        "title": "二手笔记本电脑",
        "description": "9成新，配置良好",
        "price": 2500.00,
        "originalPrice": 5000.00,
        "images": "[\"/uploads/images/xxx.png\"]",
        "categoryId": 1,
        "tags": "[\"笔记本\",\"二手\"]",
        "campusId": 1,
        "sellerId": 1,
        "quality": "九成新",
        "tradeMethods": "[\"当面交易\",\"快递\"]",
        "viewCount": 100,
        "favoriteCount": 10,
        "status": "在售",
        "createTime": "2024-01-01T10:00:00",
        "updateTime": "2024-01-01T10:00:00"
      }
    ],
    "total": 100,
    "page": 1,
    "pageSize": 10,
    "totalPages": 10
  }
}
```

### 2. 获取商品详情

**接口地址**: `GET /api/goods/{id}`

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 商品ID |

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "title": "二手笔记本电脑",
    "description": "9成新，配置良好",
    "price": 2500.00,
    "originalPrice": 5000.00,
    "images": "[\"/uploads/images/xxx.png\"]",
    "categoryId": 1,
    "tags": "[\"笔记本\",\"二手\"]",
    "campusId": 1,
    "sellerId": 1,
    "quality": "九成新",
    "tradeMethods": "[\"当面交易\",\"快递\"]",
    "viewCount": 100,
    "favoriteCount": 10,
    "status": "在售"
  }
}
```

### 3. 增加浏览量

**接口地址**: `POST /api/goods/{id}/view`

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 商品ID |

**成功响应**:

```json
{
  "code": 200,
  "message": "浏览量增加成功",
  "data": null
}
```

### 4. 发布商品

**接口地址**: `POST /api/goods/publish`

**认证要求**: 需要登录

**请求体**:

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| title | String | 是 | 商品标题 |
| description | String | 是 | 商品描述 |
| price | BigDecimal | 是 | 售价 |
| originalPrice | BigDecimal | 否 | 原价 |
| categoryId | Long | 是 | 分类ID |
| campusId | Long | 是 | 校区ID |
| tags | List/String | 是 | 标签（数组或逗号分隔） |
| quality | String | 是 | 商品成色 |
| tradeMethods | List/String | 是 | 交易方式 |
| images | List/String | 是 | 图片URL数组 |

**请求示例**:

```json
{
  "title": "二手笔记本电脑",
  "description": "9成新，配置良好，使用一年",
  "price": 2500,
  "originalPrice": 5000,
  "categoryId": 1,
  "campusId": 1,
  "tags": ["笔记本", "二手", "电脑"],
  "quality": "九成新",
  "tradeMethods": ["当面交易", "快递"],
  "images": ["/uploads/images/xxx.png"]
}
```

**成功响应**:

```json
{
  "code": 200,
  "message": "发布成功",
  "data": {
    "id": 1,
    "title": "二手笔记本电脑",
    "status": "在售",
    "viewCount": 0,
    "favoriteCount": 0
  }
}
```

### 5. 更新商品

**接口地址**: `PUT /api/goods/{id}`

**认证要求**: 需要登录

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 商品ID |

**请求体**:

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| title | String | 否 | 商品标题 |
| description | String | 否 | 商品描述 |
| price | BigDecimal | 否 | 售价 |
| originalPrice | BigDecimal | 否 | 原价 |
| categoryId | Long | 否 | 分类ID |
| campusId | Long | 否 | 校区ID |
| tags | List/String | 否 | 标签 |
| quality | String | 否 | 商品成色 |
| tradeMethods | List/String | 否 | 交易方式 |
| images | List/String | 否 | 图片URL数组 |
| status | String | 否 | 状态 |

**成功响应**:

```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

### 6. 删除商品

**接口地址**: `DELETE /api/goods/{id}`

**认证要求**: 需要登录

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 商品ID |

**成功响应**:

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

### 7. 添加收藏

**接口地址**: `POST /api/goods/{id}/favorite`

**认证要求**: 需要登录

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 商品ID |

**成功响应**:

```json
{
  "code": 200,
  "message": "收藏成功",
  "data": null
}
```

### 8. 取消收藏

**接口地址**: `DELETE /api/goods/{id}/favorite`

**认证要求**: 需要登录

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 商品ID |

**成功响应**:

```json
{
  "code": 200,
  "message": "取消收藏成功",
  "data": null
}
```

### 9. 获取我的商品

**接口地址**: `GET /api/goods/my`

**认证要求**: 需要登录

**请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| page | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 10 | 每页数量 |
| status | String | 否 | - | 状态过滤 |

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "list": [...],
    "total": 5,
    "page": 1,
    "pageSize": 10,
    "totalPages": 1
  }
}
```

### 10. 检查是否收藏

**接口地址**: `GET /api/goods/favorites/check`

**认证要求**: 需要登录

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| goodsId | Long | 是 | 商品ID |

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "isFavorited": true
  }
}
```

### 11. 获取收藏列表

**接口地址**: `GET /api/goods/favorites`

**认证要求**: 需要登录

**请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| page | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 10 | 每页数量 |

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "list": [...],
    "total": 5,
    "page": 1,
    "pageSize": 10,
    "totalPages": 1
  }
}
```

---

## 订单模块

### 1. 获取订单列表

**接口地址**: `GET /api/order/list`

**认证要求**: 需要登录

**请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| page | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 10 | 每页数量 |
| status | String | 否 | - | 状态过滤 |
| keyword | String | 否 | - | 搜索关键词 |

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "list": [
      {
        "id": 1,
        "orderNo": "ORD202401010001",
        "buyerId": 2,
        "sellerId": 1,
        "goodsId": 1,
        "goodsTitle": "二手笔记本电脑",
        "goodsImage": "/uploads/images/xxx.png",
        "price": 2500.00,
        "addressId": 1,
        "status": "待支付",
        "tradeMethod": "当面交易",
        "createTime": "2024-01-01T10:00:00",
        "updateTime": "2024-01-01T10:00:00",
        "username": "李四",
        "hasComment": false
      }
    ],
    "total": 10,
    "page": 1,
    "pageSize": 10,
    "totalPages": 1
  }
}
```

### 2. 获取订单详情

**接口地址**: `GET /api/order/{id}`

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 订单ID |

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "orderNo": "ORD202401010001",
    "buyerId": 2,
    "sellerId": 1,
    "goodsId": 1,
    "goodsTitle": "二手笔记本电脑",
    "goodsImage": "/uploads/images/xxx.png",
    "price": 2500.00,
    "addressId": 1,
    "status": "待支付",
    "tradeMethod": "当面交易",
    "createTime": "2024-01-01T10:00:00",
    "updateTime": "2024-01-01T10:00:00"
  }
}
```

### 3. 创建订单

**接口地址**: `POST /api/order/create`

**认证要求**: 需要登录

**请求体**:

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| sellerId | Long | 是 | 卖家ID |
| goodsId | Long | 是 | 商品ID |
| addressId | Long | 是 | 收货地址ID |
| price | BigDecimal | 是 | 订单金额 |
| goodsTitle | String | 否 | 商品标题 |
| goodsImage | String/List | 否 | 商品图片 |
| tradeMethod | String/List | 否 | 交易方式 |

**请求示例**:

```json
{
  "sellerId": 1,
  "goodsId": 1,
  "addressId": 1,
  "price": 2500,
  "goodsTitle": "二手笔记本电脑",
  "goodsImage": "/uploads/images/xxx.png",
  "tradeMethod": "当面交易"
}
```

**成功响应**:

```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 1,
    "orderNo": "ORD202401010001",
    "status": "待支付"
  }
}
```

### 4. 支付订单

**接口地址**: `POST /api/order/{id}/pay`

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 订单ID |

**成功响应**:

```json
{
  "code": 200,
  "message": "支付成功",
  "data": null
}
```

### 5. 发货

**接口地址**: `POST /api/order/{id}/ship`

**认证要求**: 需要登录

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 订单ID |

**成功响应**:

```json
{
  "code": 200,
  "message": "发货成功",
  "data": null
}
```

### 6. 确认收货

**接口地址**: `POST /api/order/{id}/confirm`

**认证要求**: 需要登录

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 订单ID |

**成功响应**:

```json
{
  "code": 200,
  "message": "确认收货成功",
  "data": null
}
```

### 7. 取消订单

**接口地址**: `POST /api/order/{id}/cancel`

**认证要求**: 需要登录

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 订单ID |

**成功响应**:

```json
{
  "code": 200,
  "message": "取消成功",
  "data": null
}
```

### 8. 退款

**接口地址**: `POST /api/order/{id}/refund`

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 订单ID |

**成功响应**:

```json
{
  "code": 200,
  "message": "退款成功",
  "data": null
}
```

### 9. 更新订单状态

**接口地址**: `PUT /api/order/{id}/status`

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 订单ID |

**请求体**:

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| status | String | 是 | 订单状态 |

**请求示例**:

```json
{
  "status": "已完成"
}
```

**成功响应**:

```json
{
  "code": 200,
  "message": "状态更新成功",
  "data": null
}
```

### 10. 批量更新状态

**接口地址**: `PUT /api/order/batch/status`

**请求体**:

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| ids | List | 是 | 订单ID列表 |
| status | String | 是 | 订单状态 |

**请求示例**:

```json
{
  "ids": [1, 2, 3],
  "status": "已完成"
}
```

**成功响应**:

```json
{
  "code": 200,
  "message": "批量更新完成",
  "data": {
    "successCount": 3,
    "totalCount": 3
  }
}
```

### 11. 批量退款

**接口地址**: `POST /api/order/batch/refund`

**请求体**:

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| ids | List | 是 | 订单ID列表 |

**请求示例**:

```json
{
  "ids": [1, 2, 3]
}
```

**成功响应**:

```json
{
  "code": 200,
  "message": "批量退款完成",
  "data": {
    "successCount": 3,
    "totalCount": 3
  }
}
```

---

## 评论模块

### 1. 获取商品评论列表

**接口地址**: `GET /api/comment/list`

**请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| goodsId | Long | 是 | - | 商品ID |
| page | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 10 | 每页数量 |

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "list": [
      {
        "id": 1,
        "userId": 2,
        "goodsId": 1,
        "orderId": 1,
        "rating": 5,
        "content": "商品很好，卖家很靠谱",
        "images": "[\"/uploads/images/xxx.png\"]",
        "reply": null,
        "createTime": "2024-01-01T10:00:00",
        "updateTime": "2024-01-01T10:00:00",
        "username": "李四",
        "avatar": "/uploads/avatars/xxx.png",
        "goodsTitle": "二手笔记本电脑",
        "goodsImages": "[\"/uploads/images/xxx.png\"]",
        "type": "buyer"
      }
    ],
    "total": 5,
    "page": 1,
    "pageSize": 10,
    "totalPages": 1
  }
}
```

### 2. 创建评论

**接口地址**: `POST /api/comment/create`

**认证要求**: 需要登录

**请求体**:

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| goodsId | Long | 是 | 商品ID |
| orderId | Long | 是 | 订单ID |
| rating | Integer | 是 | 评分(1-5) |
| content | String | 是 | 评价内容 |
| images | List/String | 否 | 评价图片 |
| type | String | 否 | 评价类型，默认buyer |

**请求示例**:

```json
{
  "goodsId": 1,
  "orderId": 1,
  "rating": 5,
  "content": "商品很好，卖家很靠谱",
  "images": ["/uploads/images/xxx.png"]
}
```

**成功响应**:

```json
{
  "code": 200,
  "message": "评价成功",
  "data": {
    "id": 1,
    "rating": 5,
    "content": "商品很好，卖家很靠谱"
  }
}
```

### 3. 回复评论

**接口地址**: `POST /api/comment/{id}/reply`

**认证要求**: 需要登录

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 评论ID |

**请求体**:

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| reply | String | 是 | 回复内容 |

**请求示例**:

```json
{
  "reply": "感谢您的评价！"
}
```

**成功响应**:

```json
{
  "code": 200,
  "message": "回复成功",
  "data": null
}
```

### 4. 获取我的评论

**接口地址**: `GET /api/comment/my`

**认证要求**: 需要登录

**请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| page | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 10 | 每页数量 |

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "list": [...],
    "total": 5,
    "page": 1,
    "pageSize": 10,
    "totalPages": 1
  }
}
```

### 5. 获取卖家评论

**接口地址**: `GET /api/comment/seller`

**认证要求**: 需要登录

**请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| page | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 10 | 每页数量 |

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "list": [...],
    "total": 5,
    "page": 1,
    "pageSize": 10,
    "totalPages": 1
  }
}
```

### 6. 获取所有评论（管理）

**接口地址**: `GET /api/comment/all`

**认证要求**: 需要登录

**请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| page | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 10 | 每页数量 |
| keyword | String | 否 | - | 搜索关键词 |
| rating | Integer | 否 | - | 评分过滤 |

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "list": [...],
    "total": 100,
    "page": 1,
    "pageSize": 10,
    "totalPages": 10
  }
}
```

### 7. 更新评分

**接口地址**: `PUT /api/comment/{id}/rating`

**认证要求**: 需要登录

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 评论ID |

**请求体**:

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| rating | Integer | 是 | 评分(1-5) |

**请求示例**:

```json
{
  "rating": 4
}
```

**成功响应**:

```json
{
  "code": 200,
  "message": "修改成功",
  "data": {
    "id": 1,
    "rating": 4
  }
}
```

### 8. 删除评论

**接口地址**: `DELETE /api/comment/{id}`

**认证要求**: 需要登录

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 评论ID |

**成功响应**:

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

## 聊天模块

### 1. 获取聊天会话列表

**接口地址**: `GET /api/chat/sessions`

**认证要求**: 需要登录

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": [
    {
      "sessionId": 1,
      "otherUserId": 2,
      "otherUserName": "李四",
      "otherUserAvatar": "/uploads/avatars/xxx.png",
      "lastMessage": "你好，这个商品还在吗？",
      "unreadCount": 2,
      "updatedAt": "2024-01-01T10:00:00"
    }
  ]
}
```

### 2. 获取会话消息

**接口地址**: `GET /api/chat/messages`

**认证要求**: 需要登录

**请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| sessionId | Long | 是 | - | 会话ID |
| page | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 20 | 每页数量 |

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "list": [
      {
        "id": 1,
        "sessionId": 1,
        "senderId": 2,
        "receiverId": 1,
        "content": "你好，这个商品还在吗？",
        "status": 2,
        "messageType": 0,
        "createdAt": "2024-01-01T10:00:00"
      }
    ],
    "total": 10,
    "page": 1,
    "pageSize": 20,
    "totalPages": 1
  }
}
```

### 3. 发送消息

**接口地址**: `POST /api/chat/send`

**认证要求**: 需要登录

**请求体**:

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| receiverId | Long | 是 | 接收者ID |
| content | String | 是 | 消息内容 |

**请求示例**:

```json
{
  "receiverId": 2,
  "content": "你好，商品还在的"
}
```

**成功响应**:

```json
{
  "code": 200,
  "message": "发送成功",
  "data": {
    "id": 2,
    "sessionId": 1,
    "senderId": 1,
    "receiverId": 2,
    "content": "你好，商品还在的",
    "status": 1,
    "messageType": 0,
    "createdAt": "2024-01-01T10:01:00"
  }
}
```

### 4. 获取或创建会话

**接口地址**: `GET /api/chat/session`

**认证要求**: 需要登录

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| otherUserId | Long | 是 | 对方用户ID |

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "sessionId": 1,
    "otherUserId": 2,
    "otherUserName": "李四",
    "otherUserAvatar": "/uploads/avatars/xxx.png"
  }
}
```

---

## 通知模块

### 1. 获取通知列表

**接口地址**: `GET /api/notification/list`

**请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| page | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 10 | 每页数量 |
| type | String | 否 | - | 类型过滤 |
| status | String | 否 | - | 状态过滤 |

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "list": [
      {
        "id": 1,
        "userId": 1,
        "type": "order",
        "content": "您的订单已发货",
        "status": "未读",
        "createTime": "2024-01-01T10:00:00"
      }
    ],
    "total": 5,
    "page": 1,
    "pageSize": 10,
    "totalPages": 1
  }
}
```

### 2. 获取通知详情

**接口地址**: `GET /api/notification/{id}`

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 通知ID |

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "userId": 1,
    "type": "order",
    "content": "您的订单已发货",
    "status": "未读",
    "createTime": "2024-01-01T10:00:00"
  }
}
```

### 3. 发送通知

**接口地址**: `POST /api/notification/send`

**请求体**:

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| userId | Long | 是 | 用户ID |
| type | String | 是 | 通知类型 |
| content | String | 是 | 通知内容 |

**请求示例**:

```json
{
  "userId": 1,
  "type": "order",
  "content": "您的订单已发货"
}
```

**成功响应**:

```json
{
  "code": 200,
  "message": "发送成功",
  "data": null
}
```

### 4. 重发通知

**接口地址**: `POST /api/notification/{id}/resend`

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 通知ID |

**成功响应**:

```json
{
  "code": 200,
  "message": "重发成功",
  "data": null
}
```

### 5. 删除通知

**接口地址**: `DELETE /api/notification/{id}`

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 通知ID |

**成功响应**:

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

### 6. 获取通知模板列表

**接口地址**: `GET /api/notification/templates`

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": [
    {
      "id": 1,
      "name": "订单发货通知",
      "content": "您的订单#{orderNo}已发货",
      "createTime": "2024-01-01T10:00:00"
    }
  ]
}
```

### 7. 获取通知模板详情

**接口地址**: `GET /api/notification/templates/{id}`

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 模板ID |

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "name": "订单发货通知",
    "content": "您的订单#{orderNo}已发货",
    "createTime": "2024-01-01T10:00:00"
  }
}
```

### 8. 添加通知模板

**接口地址**: `POST /api/notification/templates`

**请求体**:

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| name | String | 是 | 模板名称 |
| content | String | 是 | 模板内容 |

**请求示例**:

```json
{
  "name": "订单发货通知",
  "content": "您的订单#{orderNo}已发货"
}
```

**成功响应**:

```json
{
  "code": 200,
  "message": "添加成功",
  "data": {
    "id": 1,
    "name": "订单发货通知",
    "content": "您的订单#{orderNo}已发货"
  }
}
```

### 9. 更新通知模板

**接口地址**: `PUT /api/notification/templates/{id}`

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 模板ID |

**请求体**:

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| name | String | 否 | 模板名称 |
| content | String | 否 | 模板内容 |

**成功响应**:

```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

### 10. 删除通知模板

**接口地址**: `DELETE /api/notification/templates/{id}`

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 模板ID |

**成功响应**:

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

## 分类模块

### 1. 获取分类列表

**接口地址**: `GET /api/category/list`

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": [
    {
      "id": 1,
      "name": "数码产品",
      "createTime": "2024-01-01T10:00:00"
    },
    {
      "id": 2,
      "name": "服装鞋包",
      "createTime": "2024-01-01T10:00:00"
    }
  ]
}
```

### 2. 获取分类详情

**接口地址**: `GET /api/category/{id}`

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 分类ID |

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "name": "数码产品",
    "createTime": "2024-01-01T10:00:00"
  }
}
```

---

## 校区模块

### 1. 获取校区列表

**接口地址**: `GET /api/campus/list`

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": [
    {
      "id": 1,
      "name": "东校区",
      "createTime": "2024-01-01T10:00:00"
    },
    {
      "id": 2,
      "name": "西校区",
      "createTime": "2024-01-01T10:00:00"
    }
  ]
}
```

### 2. 获取校区详情

**接口地址**: `GET /api/campus/{id}`

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 校区ID |

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "name": "东校区",
    "createTime": "2024-01-01T10:00:00"
  }
}
```

---

## 上传模块

### 1. 上传图片

**接口地址**: `POST /api/upload/image`

**认证要求**: 需要登录

**请求体**: `multipart/form-data`

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| file | File | 是 | 图片文件 |

**成功响应**:

```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "/uploads/images/xxx.png"
  }
}
```

### 2. 上传头像

**接口地址**: `POST /api/upload/avatar`

**认证要求**: 需要登录

**请求体**: `multipart/form-data`

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| file | File | 是 | 头像图片 |

**成功响应**:

```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "/uploads/avatars/avatar_1_xxx.png"
  }
}
```

### 3. 上传文件

**接口地址**: `POST /api/upload/file`

**认证要求**: 需要登录

**请求体**: `multipart/form-data`

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| file | File | 是 | 文件 |

**成功响应**:

```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "/uploads/files/xxx.pdf"
  }
}
```

---

## 统计模块

### 1. 获取仪表盘统计

**接口地址**: `GET /api/statistics/dashboard`

**认证要求**: 需要登录

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "totalGoods": 10,
    "totalOrders": 5,
    "totalSales": 5000.00,
    "totalViews": 1000,
    "todayOrders": 2,
    "todaySales": 1000.00
  }
}
```

### 2. 获取商品统计

**接口地址**: `GET /api/statistics/goods`

**认证要求**: 需要登录

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "totalGoods": 10,
    "onSaleGoods": 8,
    "soldGoods": 2,
    "totalViews": 1000,
    "totalFavorites": 50
  }
}
```

### 3. 获取订单统计

**接口地址**: `GET /api/statistics/orders`

**认证要求**: 需要登录

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "totalOrders": 100,
    "pendingOrders": 10,
    "completedOrders": 80,
    "totalSales": 50000.00,
    "averageOrderValue": 500.00
  }
}
```

### 4. 获取互动统计

**接口地址**: `GET /api/statistics/interaction`

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "messageCount": 1000,
    "commentCount": 500,
    "reviewCount": 500,
    "ratingStats": {
      "1": 10,
      "2": 20,
      "3": 50,
      "4": 100,
      "5": 320
    },
    "messageTrend": [
      {"date": "2024-01-01", "count": 100},
      {"date": "2024-01-02", "count": 120}
    ]
  }
}
```

---

## 管理员模块

### 1. 获取用户列表

**接口地址**: `GET /api/admin/users`

**请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| page | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 10 | 每页数量 |
| keyword | String | 否 | - | 搜索关键词 |
| status | String | 否 | - | 状态过滤 |
| authStatus | String | 否 | - | 认证状态过滤 |

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "list": [...],
    "total": 100,
    "page": 1,
    "pageSize": 10,
    "totalPages": 10
  }
}
```

### 2. 更新用户信息

**接口地址**: `PUT /api/admin/users/{id}`

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 用户ID |

**请求体**:

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| username | String | 否 | 用户名 |
| email | String | 否 | 邮箱 |
| phone | String | 否 | 手机号 |
| status | String | 否 | 状态 |
| authStatus | String | 否 | 认证状态 |

**成功响应**:

```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

### 3. 删除用户

**接口地址**: `DELETE /api/admin/users/{id}`

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 用户ID |

**成功响应**:

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

### 4. 获取统计数据

**接口地址**: `GET /api/admin/statistics`

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "totalUsers": 1000,
    "todayOrders": 50,
    "totalAmount": 500000.00,
    "pendingOrders": 10
  }
}
```

### 5. 获取仪表盘

**接口地址**: `GET /api/admin/dashboard`

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "totalGoods": 1000,
    "totalOrders": 5000,
    "totalSales": 500000.00,
    "totalViews": 100000,
    "todayOrders": 50,
    "todaySales": 10000.00
  }
}
```

### 6. 获取订单列表

**接口地址**: `GET /api/admin/orders`

**请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| page | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 10 | 每页数量 |
| keyword | String | 否 | - | 搜索关键词 |
| status | String | 否 | - | 状态过滤 |

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "list": [...],
    "total": 5000,
    "page": 1,
    "pageSize": 10,
    "totalPages": 500
  }
}
```

### 7. 获取商品列表

**接口地址**: `GET /api/admin/goods`

**请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| page | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 10 | 每页数量 |
| keyword | String | 否 | - | 搜索关键词 |
| status | String | 否 | - | 状态过滤 |

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "list": [...],
    "total": 1000,
    "page": 1,
    "pageSize": 10,
    "totalPages": 100
  }
}
```

### 8. 批量审核商品

**接口地址**: `PUT /api/admin/goods/batch/approve`

**请求体**:

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| ids | List | 是 | 商品ID列表 |

**请求示例**:

```json
{
  "ids": [1, 2, 3]
}
```

**成功响应**:

```json
{
  "code": 200,
  "message": "批量审核通过完成",
  "data": {
    "successCount": 3,
    "totalCount": 3
  }
}
```

### 9. 批量拒绝商品

**接口地址**: `PUT /api/admin/goods/batch/reject`

**请求体**:

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| ids | List | 是 | 商品ID列表 |

**请求示例**:

```json
{
  "ids": [1, 2, 3]
}
```

**成功响应**:

```json
{
  "code": 200,
  "message": "批量拒绝完成",
  "data": {
    "successCount": 3,
    "totalCount": 3
  }
}
```

### 10. 批量启用用户

**接口地址**: `PUT /api/admin/users/batch/enable`

**请求体**:

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| ids | List | 是 | 用户ID列表 |

**请求示例**:

```json
{
  "ids": [1, 2, 3]
}
```

**成功响应**:

```json
{
  "code": 200,
  "message": "批量启用完成",
  "data": {
    "successCount": 3,
    "totalCount": 3
  }
}
```

### 11. 批量禁用用户

**接口地址**: `PUT /api/admin/users/batch/disable`

**请求体**:

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| ids | List | 是 | 用户ID列表 |

**请求示例**:

```json
{
  "ids": [1, 2, 3]
}
```

**成功响应**:

```json
{
  "code": 200,
  "message": "批量禁用完成",
  "data": {
    "successCount": 3,
    "totalCount": 3
  }
}
```

---

## 系统模块

### 1. 获取校区列表

**接口地址**: `GET /api/system/campus`

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": [...]
}
```

### 2. 添加校区

**接口地址**: `POST /api/system/campus`

**请求体**:

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| name | String | 是 | 校区名称 |

**请求示例**:

```json
{
  "name": "南校区"
}
```

**成功响应**:

```json
{
  "code": 200,
  "message": "添加成功",
  "data": {
    "id": 3,
    "name": "南校区"
  }
}
```

### 3. 更新校区

**接口地址**: `PUT /api/system/campus/{id}`

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 校区ID |

**请求体**:

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| name | String | 是 | 校区名称 |

**成功响应**:

```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 3,
    "name": "南校区（更新）"
  }
}
```

### 4. 删除校区

**接口地址**: `DELETE /api/system/campus/{id}`

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 校区ID |

**成功响应**:

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

### 5. 获取自提点列表

**接口地址**: `GET /api/system/pickup`

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": [...]
}
```

### 6. 添加自提点

**接口地址**: `POST /api/system/pickup`

**请求体**:

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| name | String | 是 | 自提点名称 |
| address | String | 是 | 自提点地址 |
| campusId | Long | 否 | 校区ID |

**请求示例**:

```json
{
  "name": "教学楼A栋自提点",
  "address": "教学楼A栋楼下",
  "campusId": 1
}
```

**成功响应**:

```json
{
  "code": 200,
  "message": "添加成功",
  "data": {
    "id": 1,
    "name": "教学楼A栋自提点"
  }
}
```

### 7. 更新自提点

**接口地址**: `PUT /api/system/pickup/{id}`

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 自提点ID |

**请求体**:

| 字段名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| name | String | 否 | 自提点名称 |
| address | String | 否 | 自提点地址 |
| campusId | Long | 否 | 校区ID |

**成功响应**:

```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

### 8. 删除自提点

**接口地址**: `DELETE /api/system/pickup/{id}`

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 自提点ID |

**成功响应**:

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

### 9. 获取操作日志

**接口地址**: `GET /api/system/logs`

**请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| keyword | String | 否 | - | 搜索关键词 |
| date | String | 否 | - | 日期过滤 |
| page | Integer | 否 | 1 | 页码 |
| size | Integer | 否 | 20 | 每页数量 |

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "data": [...],
    "total": 100,
    "page": 1,
    "size": 20
  }
}
```

### 10. 获取文件列表

**接口地址**: `GET /api/system/files`

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": [...]
}
```

### 11. 下载文件

**接口地址**: `GET /api/system/files/{id}/download`

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 文件ID |

**成功响应**: 文件流

### 12. 删除文件

**接口地址**: `DELETE /api/system/files/{id}`

**路径参数**:

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Long | 文件ID |

**成功响应**:

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

### 13. 获取系统统计

**接口地址**: `GET /api/system/stats`

**成功响应**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "goodsCount": 1000,
    "userCount": 5000,
    "campusCount": 3
  }
}
```

---

## 附录

### 订单状态说明

| 状态值 | 说明 |
| :--- | :--- |
| 待支付 | 订单已创建，等待用户支付 |
| 已支付 | 用户已支付，等待卖家发货 |
| 已发货 | 卖家已发货，等待买家确认收货 |
| 已完成 | 买家已确认收货，订单完成 |
| 已取消 | 订单已取消 |
| 退款中 | 退款处理中 |
| 已退款 | 退款已完成 |

### 消息状态说明

| 状态值 | 说明 |
| :--- | :--- |
| 0 | 未发送 |
| 1 | 已发送 |
| 2 | 已送达 |
| 3 | 已读 |

### 消息类型说明

| 类型值 | 说明 |
| :--- | :--- |
| 0 | 文本消息 |
| 1 | 图片消息 |
| 2 | 语音消息 |

---

**文档版本**: v1.0  
**创建时间**: 2026年  
**适用项目**: 校园二手交易平台