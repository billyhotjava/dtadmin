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
    @Mapping(target = "requestId", source = "request.id")
    ApprovalItemDTO toDto(ApprovalItem entity);

    @Mapping(target = "request", ignore = true)
    ApprovalItem toEntity(ApprovalItemDTO dto);
    
    @Named("approvalRequestId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ApprovalRequestDTO toDtoApprovalRequestId(ApprovalRequest approvalRequest);
    
    @Named("toDtoWithRequestId")
    @Mapping(target = "requestId", source = "request.id")
    ApprovalItemDTO toDtoWithRequestId(ApprovalItem entity);
    
    @Named("toEntityWithRequest")
    @Mapping(target = "request", ignore = true)
    ApprovalItem toEntityWithRequest(ApprovalItemDTO dto);
}