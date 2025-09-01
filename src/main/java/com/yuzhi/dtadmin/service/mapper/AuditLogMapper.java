package com.yuzhi.dtadmin.service.mapper;

import com.yuzhi.dtadmin.domain.AuditLog;
import com.yuzhi.dtadmin.service.dto.AuditLogDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AuditLog} and its DTO {@link AuditLogDTO}.
 */
@Mapper(componentModel = "spring")
public interface AuditLogMapper extends EntityMapper<AuditLogDTO, AuditLog> {}
