package com.yuzhi.dtadmin.service.mapper;

import com.yuzhi.dtadmin.domain.ExternalResource;
import com.yuzhi.dtadmin.service.dto.ExternalResourceDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ExternalResource} and its DTO {@link ExternalResourceDTO}.
 */
@Mapper(componentModel = "spring")
public interface ExternalResourceMapper extends EntityMapper<ExternalResourceDTO, ExternalResource> {}
