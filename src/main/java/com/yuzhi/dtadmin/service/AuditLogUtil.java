package com.yuzhi.dtadmin.service;

import com.yuzhi.dtadmin.domain.AuditLog;
import com.yuzhi.dtadmin.service.dto.AuditLogDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * 审计日志工具类
 * 用于记录系统操作的审计日志
 */
@Component
public class AuditLogUtil {

    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    public AuditLogUtil(AuditLogService auditLogService, ObjectMapper objectMapper) {
        this.auditLogService = auditLogService;
        this.objectMapper = objectMapper;
    }

    /**
     * 记录审计日志
     *
     * @param actor   操作者
     * @param action  操作类型
     * @param target  操作目标ID
     * @param details 操作详情
     * @param result  操作结果
     */
    public void log(String actor, String action, String target, Object details, String result) {
        try {
            AuditLogDTO auditLogDTO = new AuditLogDTO();
            auditLogDTO.setActor(actor);
            auditLogDTO.setAction(action);
            auditLogDTO.setTarget(target);
            auditLogDTO.setAt(Instant.now());
            auditLogDTO.setResult(result);

            // 将详情对象转换为JSON字符串
            if (details != null) {
                auditLogDTO.setDetails(objectMapper.writeValueAsString(details));
            }

            auditLogService.save(auditLogDTO);
        } catch (Exception e) {
            // 记录审计日志失败不应该影响主流程
            e.printStackTrace();
        }
    }

    /**
     * 记录审批请求创建日志
     *
     * @param requester   请求发起者
     * @param requestId   审批请求ID
     * @param requestType 请求类型
     * @param reason      请求原因
     */
    public void logApprovalRequestCreated(String requester, Long requestId, String requestType, String reason) {
        log(
            requester,
            "CREATE_APPROVAL_REQUEST",
            requestId.toString(),
            reason,
            "SUCCESS"
        );
    }

    /**
     * 记录审批通过日志
     *
     * @param approver  审批者
     * @param requestId 审批请求ID
     * @param note      审批意见
     */
    public void logApprovalApproved(String approver, Long requestId, String note) {
        log(
            approver,
            "APPROVE_REQUEST",
            requestId.toString(),
            note,
            "SUCCESS"
        );
    }

    /**
     * 记录审批拒绝日志
     *
     * @param approver  审批者
     * @param requestId 审批请求ID
     * @param note      审批意见
     */
    public void logApprovalRejected(String approver, Long requestId, String note) {
        log(
            approver,
            "REJECT_REQUEST",
            requestId.toString(),
            note,
            "SUCCESS"
        );
    }
}