package com.yuzhi.dtadmin.web.rest.admin;

import com.yuzhi.dtadmin.aop.audit.Audited;
import com.yuzhi.dtadmin.domain.enumeration.AdminRole;
import com.yuzhi.dtadmin.domain.enumeration.ChangeResourceType;
import com.yuzhi.dtadmin.domain.enumeration.ChangeStatus;
import com.yuzhi.dtadmin.security.AdminPrincipal;
import com.yuzhi.dtadmin.security.AdminSecurityUtils;
import com.yuzhi.dtadmin.service.admin.ChangeRequestService;
import com.yuzhi.dtadmin.service.dto.ChangeDecisionRequest;
import com.yuzhi.dtadmin.service.dto.ChangeRequestDTO;
import com.yuzhi.dtadmin.web.rest.util.ApiResponseUtil;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/change-requests")
public class AdminChangeRequestResource {

    private final ChangeRequestService changeRequestService;

    public AdminChangeRequestResource(ChangeRequestService changeRequestService) {
        this.changeRequestService = changeRequestService;
    }

    @PostMapping
    @Audited(action = "change.create", resource = "change-request")
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody ChangeRequestDTO request) {
        AdminPrincipal admin = requireRole(AdminRole.SYSADMIN);
        ChangeRequestDTO created = changeRequestService.createDraft(request, admin.getUsername());
        return ResponseEntity.ok(ApiResponseUtil.createSuccessResponse(created));
    }

    @PostMapping("/{id}/submit")
    @Audited(action = "change.submit", resource = "change-request")
    public ResponseEntity<Map<String, Object>> submit(@PathVariable Long id) {
        AdminPrincipal admin = requireRole(AdminRole.SYSADMIN);
        ChangeRequestDTO submitted = changeRequestService.submit(id, admin.getUsername());
        return ResponseEntity.ok(ApiResponseUtil.createSuccessResponse(submitted));
    }

    @PostMapping("/{id}/approve")
    @Audited(action = "change.approve", resource = "change-request")
    public ResponseEntity<Map<String, Object>> approve(
        @PathVariable Long id,
        @RequestBody(required = false) ChangeDecisionRequest request
    ) {
        AdminPrincipal admin = requireRole(AdminRole.AUTHADMIN);
        ChangeRequestDTO approved = changeRequestService.approve(id, admin.getUsername(), request != null ? request.getReason() : null);
        return ResponseEntity.ok(ApiResponseUtil.createSuccessResponse(approved));
    }

    @PostMapping("/{id}/reject")
    @Audited(action = "change.reject", resource = "change-request")
    public ResponseEntity<Map<String, Object>> reject(
        @PathVariable Long id,
        @RequestBody(required = false) ChangeDecisionRequest request
    ) {
        AdminPrincipal admin = requireRole(AdminRole.AUTHADMIN);
        ChangeRequestDTO rejected = changeRequestService.reject(id, admin.getUsername(), request != null ? request.getReason() : null);
        return ResponseEntity.ok(ApiResponseUtil.createSuccessResponse(rejected));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
        @RequestParam(value = "status", required = false) String status,
        @RequestParam(value = "type", required = false) String type
    ) {
        requireAnyRole(AdminRole.SYSADMIN, AdminRole.AUTHADMIN, AdminRole.AUDITADMIN);
        ChangeStatus changeStatus = parseStatus(status);
        ChangeResourceType resourceType = parseResourceType(type);
        List<ChangeRequestDTO> result = changeRequestService.findByStatus(changeStatus, resourceType);
        return ResponseEntity.ok(ApiResponseUtil.createSuccessResponse(result));
    }

    @GetMapping("/mine")
    public ResponseEntity<Map<String, Object>> mine() {
        AdminPrincipal admin = requireRole(AdminRole.SYSADMIN);
        return ResponseEntity.ok(ApiResponseUtil.createSuccessResponse(changeRequestService.findMine(admin.getUsername())));
    }

    private ChangeStatus parseStatus(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return ChangeStatus.valueOf(value.toUpperCase(Locale.ROOT));
    }

    private ChangeResourceType parseResourceType(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return ChangeResourceType.valueOf(value.toUpperCase(Locale.ROOT));
    }

    private AdminPrincipal requireRole(AdminRole expected) {
        AdminPrincipal principal = AdminSecurityUtils.getCurrentAdmin().orElseThrow(() -> new AccessDeniedException("Admin context missing"));
        if (principal.getRole() != expected) {
            throw new AccessDeniedException("Access denied for role " + principal.getRole());
        }
        return principal;
    }

    private void requireAnyRole(AdminRole... allowed) {
        AdminPrincipal principal = AdminSecurityUtils.getCurrentAdmin().orElseThrow(() -> new AccessDeniedException("Admin context missing"));
        for (AdminRole role : allowed) {
            if (principal.getRole() == role) {
                return;
            }
        }
        throw new AccessDeniedException("Access denied for role " + principal.getRole());
    }
}
