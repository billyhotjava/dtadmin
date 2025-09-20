package com.yuzhi.dtadmin.web.rest.admin;

import com.yuzhi.dtadmin.aop.audit.Audited;
import com.yuzhi.dtadmin.domain.enumeration.AdminRole;
import com.yuzhi.dtadmin.domain.enumeration.AuditOutcome;
import com.yuzhi.dtadmin.security.AdminPrincipal;
import com.yuzhi.dtadmin.security.AdminSecurityUtils;
import com.yuzhi.dtadmin.service.admin.AuditService;
import com.yuzhi.dtadmin.service.dto.AuditEventDTO;
import com.yuzhi.dtadmin.service.dto.AuditQueryCriteria;
import com.yuzhi.dtadmin.web.rest.util.ApiResponseUtil;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/audit")
public class AdminAuditResource {

    private final AuditService auditService;

    public AdminAuditResource(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> query(
        @RequestParam(value = "from", required = false) String from,
        @RequestParam(value = "to", required = false) String to,
        @RequestParam(value = "actor", required = false) String actor,
        @RequestParam(value = "action", required = false) String action,
        @RequestParam(value = "resource", required = false) String resource,
        @RequestParam(value = "outcome", required = false) String outcome
    ) {
        requireRole(AdminRole.AUDITADMIN);
        AuditQueryCriteria criteria = new AuditQueryCriteria();
        criteria.setFrom(parseInstant(from));
        criteria.setTo(parseInstant(to));
        criteria.setActor(actor);
        criteria.setAction(action);
        criteria.setResource(resource);
        criteria.setOutcome(outcome != null ? AuditOutcome.valueOf(outcome.toUpperCase(Locale.ROOT)) : null);
        List<AuditEventDTO> events = auditService.findByCriteria(criteria);
        return ResponseEntity.ok(ApiResponseUtil.createSuccessResponse(events));
    }

    @GetMapping("/export")
    @Audited(action = "audit.export", resource = "audit")
    public ResponseEntity<byte[]> export(
        @RequestParam(value = "format", defaultValue = "csv") String format,
        @RequestParam(value = "from", required = false) String from,
        @RequestParam(value = "to", required = false) String to
    ) {
        requireRole(AdminRole.AUDITADMIN);
        AuditQueryCriteria criteria = new AuditQueryCriteria();
        criteria.setFrom(parseInstant(from));
        criteria.setTo(parseInstant(to));
        List<AuditEventDTO> events = auditService.findByCriteria(criteria);
        byte[] data = auditService.export(events, format);
        String filename = "audit." + ("csv".equalsIgnoreCase(format) ? "csv" : "json");
        return ResponseEntity
            .ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType("csv".equalsIgnoreCase(format) ? MediaType.TEXT_PLAIN : MediaType.APPLICATION_JSON)
            .contentLength(data.length)
            .body(data);
    }

    private Instant parseInstant(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Instant.parse(value);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Invalid time format: " + value);
        }
    }

    private void requireRole(AdminRole expected) {
        AdminPrincipal principal = AdminSecurityUtils.getCurrentAdmin().orElseThrow(() -> new AccessDeniedException("Admin context missing"));
        if (principal.getRole() != expected) {
            throw new AccessDeniedException("Access denied for role " + principal.getRole());
        }
    }
}
