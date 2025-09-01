package com.yuzhi.dtadmin.service;

import com.yuzhi.dtadmin.domain.ExternalResource;
import com.yuzhi.dtadmin.repository.ExternalResourceRepository;
import com.yuzhi.dtadmin.service.dto.ExternalResourceDTO;
import com.yuzhi.dtadmin.service.mapper.ExternalResourceMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.yuzhi.dtadmin.domain.ExternalResource}.
 */
@Service
@Transactional
public class ExternalResourceService {

    private static final Logger LOG = LoggerFactory.getLogger(ExternalResourceService.class);

    private final ExternalResourceRepository externalResourceRepository;

    private final ExternalResourceMapper externalResourceMapper;

    public ExternalResourceService(ExternalResourceRepository externalResourceRepository, ExternalResourceMapper externalResourceMapper) {
        this.externalResourceRepository = externalResourceRepository;
        this.externalResourceMapper = externalResourceMapper;
    }

    /**
     * Save a externalResource.
     *
     * @param externalResourceDTO the entity to save.
     * @return the persisted entity.
     */
    public ExternalResourceDTO save(ExternalResourceDTO externalResourceDTO) {
        LOG.debug("Request to save ExternalResource : {}", externalResourceDTO);
        ExternalResource externalResource = externalResourceMapper.toEntity(externalResourceDTO);
        externalResource = externalResourceRepository.save(externalResource);
        return externalResourceMapper.toDto(externalResource);
    }

    /**
     * Update a externalResource.
     *
     * @param externalResourceDTO the entity to save.
     * @return the persisted entity.
     */
    public ExternalResourceDTO update(ExternalResourceDTO externalResourceDTO) {
        LOG.debug("Request to update ExternalResource : {}", externalResourceDTO);
        ExternalResource externalResource = externalResourceMapper.toEntity(externalResourceDTO);
        externalResource = externalResourceRepository.save(externalResource);
        return externalResourceMapper.toDto(externalResource);
    }

    /**
     * Partially update a externalResource.
     *
     * @param externalResourceDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ExternalResourceDTO> partialUpdate(ExternalResourceDTO externalResourceDTO) {
        LOG.debug("Request to partially update ExternalResource : {}", externalResourceDTO);

        return externalResourceRepository
            .findById(externalResourceDTO.getId())
            .map(existingExternalResource -> {
                externalResourceMapper.partialUpdate(existingExternalResource, externalResourceDTO);

                return existingExternalResource;
            })
            .map(externalResourceRepository::save)
            .map(externalResourceMapper::toDto);
    }

    /**
     * Get all the externalResources.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ExternalResourceDTO> findAll() {
        LOG.debug("Request to get all ExternalResources");
        return externalResourceRepository
            .findAll()
            .stream()
            .map(externalResourceMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one externalResource by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ExternalResourceDTO> findOne(Long id) {
        LOG.debug("Request to get ExternalResource : {}", id);
        return externalResourceRepository.findById(id).map(externalResourceMapper::toDto);
    }

    /**
     * Delete the externalResource by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ExternalResource : {}", id);
        externalResourceRepository.deleteById(id);
    }
}
