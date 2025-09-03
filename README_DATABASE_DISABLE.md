# 数据库功能禁用说明

## 问题描述
启动时遇到错误：`Parameter 0 of constructor in com.yuzhi.dtadmin.service.ApprovalItemService required a bean named 'entityManagerFactory' that could not be found.`

## 解决方案

### 1. 配置文件修改
已在 `application.yml` 中添加数据库禁用配置：

```yaml
app:
  database:
    enabled: false
```

### 2. 自动配置排除
在配置文件和主类中都排除了以下自动配置：
- DataSourceAutoConfiguration
- HibernateJpaAutoConfiguration
- LiquibaseAutoConfiguration
- DataSourceTransactionManagerAutoConfiguration
- JpaRepositoriesAutoConfiguration
- TransactionAutoConfiguration

### 3. 需要修改的Service类

如果您的项目中有类似 `ApprovalItemService` 这样依赖JPA的Service类，需要添加条件注解：

```java
@Service
@ConditionalOnProperty(name = "app.database.enabled", havingValue = "true")
public class ApprovalItemService {
    
    private final EntityManager entityManager;
    
    public ApprovalItemService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    // ... 其他方法
}
```

### 4. 替代方案

如果无法修改现有Service类，可以：

1. **临时移动文件**：将有问题的Service类文件移到 `src/main/java-disabled/` 目录
2. **使用Profile**：在Service类上添加 `@Profile("with-database")` 注解
3. **条件Bean**：我们已经提供了 `ConditionalBeansConfig` 作为临时解决方案

### 5. 启动验证

修改后，应用应该能够正常启动，可以通过以下方式验证：

```bash
# 测试Keycloak连接
curl -X GET "http://localhost:8080/api/keycloak/test/connection" \
  -H "Authorization: Bearer your_token"
```

## 恢复数据库功能

当需要重新启用数据库功能时：

1. 设置 `app.database.enabled: true`
2. 添加数据库连接配置
3. 取消注释JPA相关配置
4. 移除自动配置排除设置