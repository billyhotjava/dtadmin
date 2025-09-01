package com.yuzhi.dtadmin.service.mapper;

import com.yuzhi.dtadmin.domain.ApprovalRequest;
import com.yuzhi.dtadmin.service.dto.ApprovalRequestDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ApprovalRequest} and its DTO {@link ApprovalRequestDTO}.
 */
@Mapper(componentModel = "spring")
public interface ApprovalRequestMapper extends EntityMapper<ApprovalRequestDTO, ApprovalRequest> {}
