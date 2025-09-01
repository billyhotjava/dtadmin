package com.yuzhi.dtadmin.service.mapper;

import com.yuzhi.dtadmin.domain.ApprovalItem;
import com.yuzhi.dtadmin.domain.ApprovalRequest;
import com.yuzhi.dtadmin.service.dto.ApprovalItemDTO;
import com.yuzhi.dtadmin.service.dto.ApprovalRequestDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ApprovalItem} and its DTO {@link ApprovalItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface ApprovalItemMapper extends EntityMapper<ApprovalItemDTO, ApprovalItem> {
    @Mapping(target = "request", source = "request", qualifiedByName = "approvalRequestId")
    ApprovalItemDTO toDto(ApprovalItem s);

    @Named("approvalRequestId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ApprovalRequestDTO toDtoApprovalRequestId(ApprovalRequest approvalRequest);
}
