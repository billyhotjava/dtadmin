package com.yuzhi.dtadmin.service.admin;

import com.yuzhi.dtadmin.domain.PortalMenu;
import com.yuzhi.dtadmin.repository.PortalMenuRepository;
import com.yuzhi.dtadmin.service.dto.PortalMenuDTO;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PortalMenuService {

    private final PortalMenuRepository portalMenuRepository;

    public PortalMenuService(PortalMenuRepository portalMenuRepository) {
        this.portalMenuRepository = portalMenuRepository;
    }

    public List<PortalMenuDTO> findTree() {
        return portalMenuRepository
            .findAllByParentIsNullOrderBySortOrderAsc()
            .stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    private PortalMenuDTO toDto(PortalMenu entity) {
        PortalMenuDTO dto = new PortalMenuDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPath(entity.getPath());
        dto.setComponent(entity.getComponent());
        dto.setSortOrder(entity.getSortOrder());
        dto.setMetadata(entity.getMetadata());
        dto.setParentId(entity.getParent() != null ? entity.getParent().getId() : null);
        if (entity.getChildren() != null) {
            dto.setChildren(entity.getChildren().stream().map(this::toDto).collect(Collectors.toList()));
        }
        return dto;
    }
}
