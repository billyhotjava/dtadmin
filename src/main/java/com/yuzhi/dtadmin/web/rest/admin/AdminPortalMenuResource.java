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
import com.yuzhi.dtadmin.service.admin.PortalMenuService;
import com.yuzhi.dtadmin.service.dto.ChangeRequestDTO;
import com.yuzhi.dtadmin.service.dto.PortalMenuDTO;
import com.yuzhi.dtadmin.web.rest.util.ApiResponseUtil;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/portal/menus")
public class AdminPortalMenuResource {

    private final PortalMenuService portalMenuService;
    private final ChangeRequestService changeRequestService;
    private final ObjectMapper objectMapper;

    public AdminPortalMenuResource(PortalMenuService portalMenuService, ChangeRequestService changeRequestService, ObjectMapper objectMapper) {
        this.portalMenuService = portalMenuService;
        this.changeRequestService = changeRequestService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list() {
        requireRole(AdminRole.SYSADMIN);
        List<PortalMenuDTO> tree = portalMenuService.findTree();
        return ResponseEntity.ok(ApiResponseUtil.createSuccessResponse(tree));
    }

    @PostMapping
    @Audited(action = "menu.draft.create", resource = "portal-menu")
    public ResponseEntity<Map<String, Object>> create(@RequestBody PortalMenuDTO request) throws JsonProcessingException {
        AdminPrincipal admin = requireRole(AdminRole.SYSADMIN);
        ChangeRequestDTO change = new ChangeRequestDTO();
        change.setAction(ChangeAction.CREATE);
        change.setResourceType(ChangeResourceType.MENU);
        change.setPayloadJson(objectMapper.writeValueAsString(request));
        ChangeRequestDTO created = changeRequestService.createDraft(change, admin.getUsername());
        return ResponseEntity.ok(ApiResponseUtil.createSuccessResponse(created));
    }

    @PutMapping("/{id}")
    @Audited(action = "menu.draft.update", resource = "portal-menu")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody PortalMenuDTO request) throws JsonProcessingException {
        AdminPrincipal admin = requireRole(AdminRole.SYSADMIN);
        ChangeRequestDTO change = new ChangeRequestDTO();
        change.setId(id);
        change.setAction(ChangeAction.UPDATE);
        change.setResourceType(ChangeResourceType.MENU);
        change.setResourceId(String.valueOf(id));
        change.setPayloadJson(objectMapper.writeValueAsString(request));
        ChangeRequestDTO created = changeRequestService.createDraft(change, admin.getUsername());
        return ResponseEntity.ok(ApiResponseUtil.createSuccessResponse(created));
    }

    @DeleteMapping("/{id}")
    @Audited(action = "menu.draft.delete", resource = "portal-menu")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) throws JsonProcessingException {
        AdminPrincipal admin = requireRole(AdminRole.SYSADMIN);
        ChangeRequestDTO change = new ChangeRequestDTO();
        change.setAction(ChangeAction.DELETE);
        change.setResourceType(ChangeResourceType.MENU);
        change.setResourceId(String.valueOf(id));
        change.setPayloadJson(objectMapper.writeValueAsString(new PortalMenuDTO()));
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
