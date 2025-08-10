# 绳结教程前后端项目

这是一个完整的绳结教程管理系统，包含后端Java API、微信小程序前端和后台管理系统。

## 项目结构

```
knots/
├── backend/                 # 后端Java项目
│   ├── src/main/java/
│   │   └── com/knots/
│   │       ├── controller/  # 控制器层
│   │       ├── entity/      # 实体类
│   │       ├── repository/  # 数据访问层
│   │       └── service/     # 服务层
│   ├── src/main/resources/
│   │   ├── templates/       # Thymeleaf模板
│   │   └── application.yml  # 配置文件
│   └── pom.xml             # Maven配置
├── front/                   # 微信小程序前端
│   ├── pages/              # 页面文件
│   ├── app.js              # 小程序入口
│   └── app.json            # 小程序配置
└── README.md               # 项目说明
```

## 功能特性

### 后端功能
- **用户管理**: 支持管理员登录和微信用户登录
- **绳结管理**: 创建、编辑、删除绳结教程
- **分类管理**: 绳结分类的增删改查
- **图片上传**: 支持绳结图片上传
- **API接口**: 提供RESTful API供小程序调用

### 微信小程序功能
- **微信登录**: 支持微信用户快速登录
- **绳结浏览**: 浏览所有绳结教程
- **搜索功能**: 搜索绳结教程
- **分类浏览**: 按分类查看绳结
- **详情查看**: 查看绳结详细教程
- **收藏功能**: 收藏喜欢的绳结
- **分享功能**: 分享绳结给朋友

### 后台管理功能
- **管理员登录**: 安全的管理员登录系统
- **仪表板**: 数据统计和概览
- **绳结管理**: 完整的绳结CRUD操作
- **分类管理**: 绳结分类管理
- **图片管理**: 绳结图片上传和管理

## 技术栈

### 后端
- **Spring Boot 2.7.14**: 主框架
- **Spring Data JPA**: 数据访问层
- **Spring Security**: 安全认证
- **MySQL**: 数据库
- **Thymeleaf**: 模板引擎
- **JWT**: 用户认证
- **Maven**: 项目管理

### 前端
- **微信小程序**: 原生小程序开发
- **WXML/WXSS**: 页面结构和样式
- **JavaScript**: 业务逻辑

## 快速开始

### 环境要求
- JDK 8+
- Maven 3.6+
- MySQL 8.0+
- 微信开发者工具

### 后端启动

1. **配置数据库**
   ```sql
   CREATE DATABASE knots_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. **修改配置**
   编辑 `backend/src/main/resources/application.yml`，修改数据库连接信息：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/knots_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
       username: your_username
       password: your_password
   ```

3. **启动项目**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

4. **访问后台管理**
   打开浏览器访问: http://localhost:8080/admin/login

### 小程序启动

1. **导入项目**
   使用微信开发者工具导入 `front` 目录

2. **修改配置**
   编辑 `front/app.js`，修改API地址：
   ```javascript
   baseUrl: 'http://your-server-ip:8080/api'
   ```

3. **编译运行**
   在微信开发者工具中编译运行项目

## API接口

### 用户相关
- `POST /api/wechat/login` - 微信用户登录

### 绳结相关
- `GET /api/knots` - 获取绳结列表
- `GET /api/knots/{id}` - 获取绳结详情
- `GET /api/knots/search` - 搜索绳结
- `GET /api/knots/popular` - 获取热门绳结

### 分类相关
- `GET /api/categories` - 获取分类列表
- `GET /api/categories/{id}/knots` - 获取分类下的绳结

## 数据库设计

### 用户表 (users)
- id: 主键
- username: 用户名
- password: 密码
- open_id: 微信openId
- nick_name: 微信昵称
- avatar_url: 微信头像
- role: 用户角色
- created_at: 创建时间
- updated_at: 更新时间

### 绳结分类表 (knot_categories)
- id: 主键
- name: 分类名称
- description: 分类描述
- sort_order: 排序
- created_at: 创建时间
- updated_at: 更新时间

### 绳结表 (knots)
- id: 主键
- name: 绳结名称
- description: 绳结描述
- steps: 打结步骤(JSON)
- cover_image: 封面图片
- category_id: 分类ID
- difficulty_level: 难度等级
- view_count: 浏览次数
- is_published: 是否发布
- created_by: 创建者ID
- created_at: 创建时间
- updated_at: 更新时间

### 绳结图片表 (knot_images)
- id: 主键
- image_url: 图片URL
- image_name: 图片名称
- image_type: 图片类型
- file_size: 文件大小
- sort_order: 排序
- knot_id: 绳结ID
- created_at: 创建时间
- updated_at: 更新时间

## 部署说明

### 后端部署
1. 打包项目：`mvn clean package`
2. 运行jar包：`java -jar target/knots-backend-1.0.0.jar`

### 小程序部署
1. 在微信开发者工具中上传代码
2. 提交审核并发布

## 开发说明

### 添加新的绳结
1. 在后台管理系统中登录
2. 进入绳结管理页面
3. 点击"新建绳结"
4. 填写绳结信息并上传图片
5. 保存并发布

### 自定义样式
- 后台管理样式：修改 `backend/src/main/resources/templates/admin/` 下的HTML文件
- 小程序样式：修改对应页面的 `.wxss` 文件

## 注意事项

1. **数据库配置**: 确保MySQL服务正常运行，数据库连接配置正确
2. **文件上传**: 确保上传目录有写入权限
3. **微信小程序**: 需要在微信公众平台配置服务器域名
4. **跨域问题**: 生产环境需要配置正确的CORS策略

## 许可证

MIT License

## 联系方式

如有问题，请联系开发团队。

