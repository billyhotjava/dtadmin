package com.yuzhi.dtadmin.service.mapper;

import com.yuzhi.dtadmin.domain.ApprovalRequest;
import com.yuzhi.dtadmin.service.dto.ApprovalRequestDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ApprovalRequest} and its DTO {@link ApprovalRequestDTO}.
 */
@Mapper(componentModel = "spring", uses = {ApprovalItemMapper.class})
public interface ApprovalRequestMapper extends EntityMapper<ApprovalRequestDTO, ApprovalRequest> {
    @Mapping(target = "items", qualifiedByName = "toDtoWithRequestId")
    ApprovalRequestDTO toDto(ApprovalRequest entity);
    
    @Mapping(target = "items", qualifiedByName = "toEntityWithRequest")
    ApprovalRequest toEntity(ApprovalRequestDTO dto);
    
    @Named("toDtoWithoutRequest")
    @Mapping(target = "requestId", source = "request.id")
    com.yuzhi.dtadmin.service.dto.ApprovalItemDTO toDtoWithoutRequest(com.yuzhi.dtadmin.domain.ApprovalItem item);
    
    @Named("toEntityWithoutRequest")
    @Mapping(target = "request", ignore = true)
    com.yuzhi.dtadmin.domain.ApprovalItem toEntityWithoutRequest(com.yuzhi.dtadmin.service.dto.ApprovalItemDTO item);
    
    @Named("toEntityWithRequest")
    default com.yuzhi.dtadmin.domain.ApprovalItem toEntityWithRequest(com.yuzhi.dtadmin.service.dto.ApprovalItemDTO dto, com.yuzhi.dtadmin.domain.ApprovalRequest request) {
        com.yuzhi.dtadmin.domain.ApprovalItem item = toEntityWithoutRequest(dto);
        item.setRequest(request);
        return item;
    }
}