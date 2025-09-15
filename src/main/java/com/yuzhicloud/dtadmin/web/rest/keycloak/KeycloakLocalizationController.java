package com.yuzhicloud.dtadmin.web.rest.keycloak;

import com.yuzhicloud.dtadmin.config.KeycloakConfig;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Keycloak本地化翻译控制器
 * 提供Keycloak相关的中文翻译词表
 */
@RestController
@RequestMapping("/api/keycloak/localization")
public class KeycloakLocalizationController {
    
    private static final Logger logger = LoggerFactory.getLogger(KeycloakLocalizationController.class);
    
    private final Keycloak keycloakAdminClient;
    private final KeycloakConfig keycloakConfig;

    public KeycloakLocalizationController(Keycloak keycloakAdminClient, KeycloakConfig keycloakConfig) {
        this.keycloakAdminClient = keycloakAdminClient;
        this.keycloakConfig = keycloakConfig;
    }

    /**
     * 获取Keycloak中文翻译词表
     * 调用Keycloak API: keycloak.realm("myrealm").localization().get("zh-CN")
     * 
     * @return 包含Keycloak相关中文翻译的映射
     */
    @GetMapping("/zh-CN")
    public ResponseEntity<Map<String, Object>> getKeycloakChineseTranslations() {
        try {
            // 调用Keycloak API获取本地化翻译
            RealmResource realmResource = keycloakAdminClient.realm(keycloakConfig.getTargetRealm());
           // Map<String, String> localizationMap = realmResource.localization().get("zh-CN");
            Map<String, Object> localizationMap = getDefaultTranslations();
            return ResponseEntity.ok(localizationMap);
            
            // 如果从Keycloak获取到了翻译，则直接返回
/*             if (localizationMap != null && !localizationMap.isEmpty()) {
                Map<String, Object> result = new HashMap<>();
                result.put("localization", localizationMap);
                return ResponseEntity.ok(result);
            } */
        } catch (Exception e) {
            logger.warn("Failed to get localization from Keycloak, using default translations", e);
        }
        
        // 如果Keycloak API调用失败或没有数据，返回默认翻译
        return ResponseEntity.ok(getDefaultTranslations());
    }
    
    /**
     * 获取默认翻译词表（当Keycloak API不可用时使用）
     * 
     * @return 包含Keycloak相关中文翻译的映射
     */
    private Map<String, Object> getDefaultTranslations() {
        Map<String, Object> translations = new HashMap<>();
        
        // 用户管理相关翻译
        Map<String, String> userManagement = new HashMap<>();
        userManagement.put("users", "用户");
        userManagement.put("user", "用户");
        userManagement.put("username", "用户名");
        userManagement.put("email", "邮箱");
        userManagement.put("firstName", "名");
        userManagement.put("lastName", "姓");
        userManagement.put("enabled", "启用");
        userManagement.put("emailVerified", "邮箱已验证");
        userManagement.put("createdTimestamp", "创建时间");
        userManagement.put("attributes", "属性");
        userManagement.put("groups", "组");
        userManagement.put("realmRoles", "领域角色");
        userManagement.put("clientRoles", "客户端角色");
        userManagement.put("search", "搜索");
        userManagement.put("createUser", "创建用户");
        userManagement.put("updateUser", "更新用户");
        userManagement.put("deleteUser", "删除用户");
        userManagement.put("resetPassword", "重置密码");
        userManagement.put("userDetails", "用户详情");
        userManagement.put("userList", "用户列表");
        userManagement.put("addUser", "添加用户");
        userManagement.put("editUser", "编辑用户");
        userManagement.put("viewUser", "查看用户");
        userManagement.put("disableUser", "禁用用户");
        userManagement.put("enableUser", "启用用户");
        userManagement.put("temporaryPassword", "临时密码");
        userManagement.put("department", "部门");
        userManagement.put("phoneNumber", "电话号码");
        userManagement.put("address", "地址");
        userManagement.put("employeeId", "员工ID");
        
        // 角色管理相关翻译
        Map<String, String> roleManagement = new HashMap<>();
        roleManagement.put("roles", "角色");
        roleManagement.put("role", "角色");
        roleManagement.put("roleName", "角色名称");
        roleManagement.put("description", "描述");
        roleManagement.put("composite", "复合角色");
        roleManagement.put("clientRole", "客户端角色");
        roleManagement.put("containerId", "容器ID");
        roleManagement.put("createRole", "创建角色");
        roleManagement.put("updateRole", "更新角色");
        roleManagement.put("deleteRole", "删除角色");
        roleManagement.put("roleDetails", "角色详情");
        roleManagement.put("roleList", "角色列表");
        roleManagement.put("addRole", "添加角色");
        roleManagement.put("editRole", "编辑角色");
        roleManagement.put("viewRole", "查看角色");
        
        // 组管理相关翻译
        Map<String, String> groupManagement = new HashMap<>();
        groupManagement.put("groups", "组");
        groupManagement.put("group", "组");
        groupManagement.put("groupName", "组名称");
        groupManagement.put("path", "路径");
        groupManagement.put("subGroups", "子组");
        groupManagement.put("createGroup", "创建组");
        groupManagement.put("updateGroup", "更新组");
        groupManagement.put("deleteGroup", "删除组");
        groupManagement.put("addGroup", "添加组");
        groupManagement.put("editGroup", "编辑组");
        groupManagement.put("viewGroup", "查看组");
        groupManagement.put("groupDetails", "组详情");
        groupManagement.put("groupList", "组列表");
        groupManagement.put("addGroupMember", "添加组成员");
        groupManagement.put("removeGroupMember", "移除组成员");
        groupManagement.put("groupMembers", "组成员");
        
        // 通用操作翻译
        Map<String, String> commonActions = new HashMap<>();
        commonActions.put("create", "创建");
        commonActions.put("update", "更新");
        commonActions.put("delete", "删除");
        commonActions.put("edit", "编辑");
        commonActions.put("view", "查看");
        commonActions.put("save", "保存");
        commonActions.put("cancel", "取消");
        commonActions.put("confirm", "确认");
        commonActions.put("reset", "重置");
        commonActions.put("search", "搜索");
        commonActions.put("filter", "筛选");
        commonActions.put("refresh", "刷新");
        commonActions.put("close", "关闭");
        commonActions.put("back", "返回");
        commonActions.put("next", "下一步");
        commonActions.put("previous", "上一步");
        commonActions.put("finish", "完成");
        commonActions.put("add", "添加");
        commonActions.put("remove", "移除");
        commonActions.put("assign", "分配");
        commonActions.put("revoke", "撤销");
        
        // 状态和消息翻译
        Map<String, String> statusMessages = new HashMap<>();
        statusMessages.put("success", "成功");
        statusMessages.put("error", "错误");
        statusMessages.put("warning", "警告");
        statusMessages.put("info", "信息");
        statusMessages.put("loading", "加载中");
        statusMessages.put("processing", "处理中");
        statusMessages.put("completed", "已完成");
        statusMessages.put("failed", "失败");
        statusMessages.put("enabled", "已启用");
        statusMessages.put("disabled", "已禁用");
        statusMessages.put("active", "活跃");
        statusMessages.put("inactive", "非活跃");
        
        // 表单相关翻译
        Map<String, String> formLabels = new HashMap<>();
        formLabels.put("required", "必填");
        formLabels.put("optional", "可选");
        formLabels.put("placeholder", "请输入");
        formLabels.put("select", "请选择");
        formLabels.put("selectAll", "全选");
        formLabels.put("deselectAll", "取消全选");
        formLabels.put("selected", "已选择");
        formLabels.put("noData", "暂无数据");
        formLabels.put("noResults", "暂无结果");
        formLabels.put("pageSize", "每页显示");
        formLabels.put("total", "总计");
        
        // 分页相关翻译
        Map<String, String> pagination = new HashMap<>();
        pagination.put("pagination", "分页");
        pagination.put("firstPage", "首页");
        pagination.put("lastPage", "末页");
        pagination.put("previousPage", "上一页");
        pagination.put("nextPage", "下一页");
        pagination.put("currentPage", "当前页");
        pagination.put("totalPages", "总页数");
        pagination.put("totalItems", "总条目数");
        pagination.put("itemsPerPage", "每页条目数");
        
        // 将所有翻译添加到主映射中
        translations.put("userManagement", userManagement);
        translations.put("roleManagement", roleManagement);
        translations.put("groupManagement", groupManagement);
        translations.put("commonActions", commonActions);
        translations.put("statusMessages", statusMessages);
        translations.put("formLabels", formLabels);
        translations.put("pagination", pagination);
        
        return translations;
    }
}