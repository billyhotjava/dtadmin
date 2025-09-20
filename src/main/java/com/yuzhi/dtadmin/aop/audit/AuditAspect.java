package com.yuzhi.dtadmin.aop.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuzhi.dtadmin.domain.enumeration.AuditOutcome;
import com.yuzhi.dtadmin.security.AdminPrincipal;
import com.yuzhi.dtadmin.security.AdminSecurityUtils;
import com.yuzhi.dtadmin.service.admin.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {

    private final AuditService auditService;
    private final HttpServletRequest request;
    private final ObjectMapper objectMapper;

    public AuditAspect(AuditService auditService, HttpServletRequest request, ObjectMapper objectMapper) {
        this.auditService = auditService;
        this.request = request;
        this.objectMapper = objectMapper;
    }

    @Around("@annotation(audited)")
    public Object around(ProceedingJoinPoint joinPoint, Audited audited) throws Throwable {
        AdminPrincipal admin = AdminSecurityUtils.getCurrentAdmin().orElse(null);
        String detail = serializeArgs(joinPoint.getArgs());
        try {
            Object result = joinPoint.proceed();
            auditService.record(
                audited.action(),
                audited.resource(),
                AuditOutcome.SUCCESS,
                detail,
                request,
                admin != null ? admin.getUsername() : null,
                admin != null ? Arrays.asList(admin.getRole().name()) : null
            );
            return result;
        } catch (Throwable ex) {
            auditService.record(
                audited.action(),
                audited.resource(),
                AuditOutcome.FAILURE,
                detail + ";error=" + ex.getMessage(),
                request,
                admin != null ? admin.getUsername() : null,
                admin != null ? Arrays.asList(admin.getRole().name()) : null
            );
            throw ex;
        }
    }

    private String serializeArgs(Object[] args) {
        try {
            return objectMapper.writeValueAsString(args);
        } catch (Exception e) {
            return "[]";
        }
    }
}
