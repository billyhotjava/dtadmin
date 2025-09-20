package com.yuzhi.dtadmin.service.admin;

import com.yuzhi.dtadmin.domain.SystemConfig;
import com.yuzhi.dtadmin.repository.SystemConfigRepository;
import com.yuzhi.dtadmin.service.dto.SystemConfigDTO;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SystemConfigService {

    private final SystemConfigRepository systemConfigRepository;

    public SystemConfigService(SystemConfigRepository systemConfigRepository) {
        this.systemConfigRepository = systemConfigRepository;
    }

    public List<SystemConfigDTO> findAll() {
        return systemConfigRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    private SystemConfigDTO toDto(SystemConfig entity) {
        SystemConfigDTO dto = new SystemConfigDTO();
        dto.setId(entity.getId());
        dto.setKey(entity.getKey());
        dto.setValue(entity.getValue());
        dto.setDescription(entity.getDescription());
        return dto;
    }
}
