package com.yuzhi.dtadmin.web.rest.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuzhi.dtadmin.aop.audit.Audited;
import com.yuzhi.dtadmin.domain.enumeration.AdminRole;
import com.yuzhi.dtadmin.domain.enumeration.ChangeAction;
import com.yuzhi.dtadmin.domain.enumeration.ChangeResourceType;
import com.yuzhi.dtadmin.security.AdminPrincipal;
import com.yuzhi.dtadmin.security.AdminSecurityUtils;
import com.yuzhi.dtadmin.service.admin.ChangeRequestService;
import com.yuzhi.dtadmin.service.admin.SystemConfigService;
import com.yuzhi.dtadmin.service.dto.ChangeRequestDTO;
import com.yuzhi.dtadmin.service.dto.SystemConfigDTO;
import com.yuzhi.dtadmin.web.rest.util.ApiResponseUtil;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/system/config")
public class AdminSystemConfigResource {

    private final SystemConfigService systemConfigService;
    private final ChangeRequestService changeRequestService;
    private final ObjectMapper objectMapper;

    public AdminSystemConfigResource(SystemConfigService systemConfigService, ChangeRequestService changeRequestService, ObjectMapper objectMapper) {
        this.systemConfigService = systemConfigService;
        this.changeRequestService = changeRequestService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list() {
        requireRole(AdminRole.SYSADMIN);
        List<SystemConfigDTO> configs = systemConfigService.findAll();
        return ResponseEntity.ok(ApiResponseUtil.createSuccessResponse(configs));
    }

    @PostMapping
    @Audited(action = "config.draft", resource = "system-config")
    public ResponseEntity<Map<String, Object>> draft(@RequestBody SystemConfigDTO request) throws JsonProcessingException {
        AdminPrincipal admin = requireRole(AdminRole.SYSADMIN);
        ChangeRequestDTO change = new ChangeRequestDTO();
        change.setAction(ChangeAction.CONFIG_SET);
        change.setResourceType(ChangeResourceType.CONFIG);
        change.setPayloadJson(objectMapper.writeValueAsString(request));
        ChangeRequestDTO created = changeRequestService.createDraft(change, admin.getUsername());
        return ResponseEntity.ok(ApiResponseUtil.createSuccessResponse(created));
    }

    private AdminPrincipal requireRole(AdminRole expected) {
        AdminPrincipal principal = AdminSecurityUtils.getCurrentAdmin().orElseThrow(() -> new AccessDeniedException("Admin context missing"));
        if (principal.getRole() != expected) {
            throw new AccessDeniedException("Access denied for role " + principal.getRole());
        }
        return principal;
    }
}
