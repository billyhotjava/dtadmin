# Keycloak集成文档

本文档说明如何在dtadmin SpringBoot项目中集成和使用Keycloak进行用户、角色和权限管理。

## 功能特性

- 🔐 **用户管理**: 创建、更新、删除用户，重置密码，启用/禁用用户
- 👤 **角色管理**: 管理Realm角色，为用户分配和移除角色
- 👥 **组管理**: 创建和管理用户组，用户组成员管理
- 🔒 **SSL支持**: 支持忽略SSL证书验证（开发环境）
- 🔑 **自动认证**: 自动获取和刷新访问令牌

## 架构设计

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Controller    │───▶│     Service     │───▶│   Keycloak      │
│                 │    │                 │    │   Admin API    │
│ - UserController│    │ - UserService   │    │                 │
│ - RoleController│    │ - RoleService   │    │                 │
│ - GroupController│   │ - GroupService  │    │                 │
│ - TestController│    │ - AuthService   │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 配置说明

### application.yml配置

```yaml
app:
  keycloak:
    server-url: https://sso.yuzhicloud.com
    realm: s10
    admin:
      username: admin
      password: admin
      client-id: admin-cli
    ssl:
      trust-all: true
```

### 配置参数说明

| 参数 | 描述 | 默认值 |
|------|------|--------|
| `server-url` | Keycloak服务器地址 | `https://sso.yuzhicloud.com` |
| `realm` | Realm名称 | `s10` |
| `admin.username` | 管理员用户名 | `admin` |
| `admin.password` | 管理员密码 | `admin` |
| `admin.client-id` | 管理客户端ID | `admin-cli` |
| `ssl.trust-all` | 是否忽略SSL证书 | `true` |

## API接口

### 用户管理 API

#### 基础路径: `/api/keycloak/users`

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/` | 获取所有用户（支持分页） |
| GET | `/search?username={username}` | 按用户名搜索用户 |
| GET | `/{userId}` | 根据ID获取用户 |
| POST | `/` | 创建新用户 |
| PUT | `/{userId}` | 更新用户信息 |
| DELETE | `/{userId}` | 删除用户 |
| POST | `/{userId}/reset-password` | 重置用户密码 |
| PUT | `/{userId}/enabled` | 启用/禁用用户 |
| GET | `/{userId}/roles` | 获取用户角色 |
| POST | `/{userId}/roles` | 为用户分配角色 |
| DELETE | `/{userId}/roles` | 移除用户角色 |

#### 用户创建示例

```json
POST /api/keycloak/users
{
  "username": "testuser",
  "email": "test@example.com",
  "firstName": "Test",
  "lastName": "User",
  "enabled": true,
  "emailVerified": true
}
```

#### 密码重置示例

```json
POST /api/keycloak/users/{userId}/reset-password
{
  "password": "newpassword123",
  "temporary": false
}
```

### 角色管理 API

#### 基础路径: `/api/keycloak/roles`

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/` | 获取所有Realm角色 |
| GET | `/{roleName}` | 根据名称获取角色 |
| POST | `/` | 创建新角色 |
| PUT | `/{roleName}` | 更新角色 |
| DELETE | `/{roleName}` | 删除角色 |

#### 角色创建示例

```json
POST /api/keycloak/roles
{
  "name": "custom-role",
  "description": "自定义角色描述"
}
```

### 组管理 API

#### 基础路径: `/api/keycloak/groups`

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/` | 获取所有组 |
| GET | `/{groupId}` | 根据ID获取组 |
| POST | `/` | 创建新组 |
| PUT | `/{groupId}` | 更新组信息 |
| DELETE | `/{groupId}` | 删除组 |
| GET | `/{groupId}/members` | 获取组成员 |
| POST | `/{groupId}/members/{userId}` | 将用户加入组 |
| DELETE | `/{groupId}/members/{userId}` | 将用户从组中移除 |
| GET | `/user/{userId}` | 获取用户所属组 |

#### 组创建示例

```json
POST /api/keycloak/groups
{
  "name": "developers",
  "path": "/developers"
}
```

### 测试 API

#### 基础路径: `/api/keycloak/test`

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/connection` | 测试Keycloak连接 |
| GET | `/users` | 测试用户服务 |
| GET | `/roles` | 测试角色服务 |
| GET | `/groups` | 测试组服务 |
| GET | `/all` | 综合测试所有服务 |

## 使用示例

### 1. 测试连接

```bash
curl -X GET "http://localhost:8080/api/keycloak/test/connection" \
  -H "Authorization: Bearer your_token_here"
```

### 2. 创建用户

```bash
curl -X POST "http://localhost:8080/api/keycloak/users" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your_token_here" \
  -d '{
    "username": "newuser",
    "email": "newuser@example.com",
    "firstName": "New",
    "lastName": "User",
    "enabled": true
  }'
```

### 3. 为用户分配角色

```bash
curl -X POST "http://localhost:8080/api/keycloak/users/{userId}/roles" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your_token_here" \
  -d '[
    {
      "name": "user",
      "description": "Basic user role"
    }
  ]'
```

## 安全配置

### 权限控制

所有Keycloak管理API都需要`ADMIN`角色权限：

```java
@PreAuthorize("hasRole('ADMIN')")
public class KeycloakUserController {
    // ...
}
```

### SSL证书处理

在开发环境中，如果Keycloak使用自签名证书，可以通过配置忽略SSL验证：

```yaml
app:
  keycloak:
    ssl:
      trust-all: true
```

**注意**: 生产环境中应该使用有效的SSL证书，不建议忽略证书验证。

## 错误处理

### 常见错误及解决方案

1. **连接超时**: 检查Keycloak服务器地址和网络连接
2. **认证失败**: 验证管理员用户名和密码
3. **权限不足**: 确保管理员用户具有正确的权限
4. **SSL错误**: 在开发环境中启用`trust-all`配置

### 日志配置

建议在`logback-spring.xml`中配置Keycloak相关日志：

```xml
<logger name="com.yuzhicloud.dtadmin.service.keycloak" level="DEBUG"/>
```

## 开发建议

1. **使用事务**: 对于批量操作，考虑使用事务保证数据一致性
2. **缓存令牌**: 访问令牌会自动缓存和刷新，避免频繁请求
3. **异常处理**: 实现统一的异常处理机制
4. **接口文档**: 使用OpenAPI/Swagger生成API文档
5. **单元测试**: 为服务层编写单元测试

## 扩展功能

可以考虑添加以下扩展功能：

- 🔄 **批量操作**: 批量创建用户、分配角色
- 📊 **监控统计**: 用户活动统计、角色使用情况
- 🔍 **高级搜索**: 支持更复杂的用户搜索条件
- 📧 **邮件通知**: 用户创建、密码重置邮件通知
- 🔐 **密码策略**: 自定义密码复杂度要求
- 📝 **操作审计**: 记录所有管理操作的审计日志

## 故障排除

### 启动时问题

1. 检查Keycloak服务器是否可访问
2. 验证配置文件中的参数
3. 查看应用启动日志

### 运行时问题

1. 检查访问令牌是否过期
2. 验证用户权限
3. 查看详细错误日志

## 支持

如有问题或建议，请联系开发团队或查看项目文档。