package com.yuzhi.dtadmin.service;

import com.yuzhi.dtadmin.domain.ApprovalItem;
import com.yuzhi.dtadmin.repository.ApprovalItemRepository;
import com.yuzhi.dtadmin.service.dto.ApprovalItemDTO;
import com.yuzhi.dtadmin.service.mapper.ApprovalItemMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.yuzhi.dtadmin.domain.ApprovalItem}.
 */
@Service
@Transactional
public class ApprovalItemService {

    private static final Logger LOG = LoggerFactory.getLogger(ApprovalItemService.class);

    private final ApprovalItemRepository approvalItemRepository;

    private final ApprovalItemMapper approvalItemMapper;

    public ApprovalItemService(ApprovalItemRepository approvalItemRepository, ApprovalItemMapper approvalItemMapper) {
        this.approvalItemRepository = approvalItemRepository;
        this.approvalItemMapper = approvalItemMapper;
    }

    /**
     * Save a approvalItem.
     *
     * @param approvalItemDTO the entity to save.
     * @return the persisted entity.
     */
    public ApprovalItemDTO save(ApprovalItemDTO approvalItemDTO) {
        LOG.debug("Request to save ApprovalItem : {}", approvalItemDTO);
        ApprovalItem approvalItem = approvalItemMapper.toEntity(approvalItemDTO);
        approvalItem = approvalItemRepository.saveAndFlush(approvalItem);
        return approvalItemMapper.toDto(approvalItem);
    }

    /**
     * Update a approvalItem.
     *
     * @param approvalItemDTO the entity to save.
     * @return the persisted entity.
     */
    public ApprovalItemDTO update(ApprovalItemDTO approvalItemDTO) {
        LOG.debug("Request to update ApprovalItem : {}", approvalItemDTO);
        ApprovalItem approvalItem = approvalItemMapper.toEntity(approvalItemDTO);
        approvalItem = approvalItemRepository.save(approvalItem);
        return approvalItemMapper.toDto(approvalItem);
    }

    /**
     * Partially update a approvalItem.
     *
     * @param approvalItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ApprovalItemDTO> partialUpdate(ApprovalItemDTO approvalItemDTO) {
        LOG.debug("Request to partially update ApprovalItem : {}", approvalItemDTO);

        return approvalItemRepository
            .findById(approvalItemDTO.getId())
            .map(existingApprovalItem -> {
                approvalItemMapper.partialUpdate(existingApprovalItem, approvalItemDTO);

                return existingApprovalItem;
            })
            .map(approvalItemRepository::save)
            .map(approvalItemMapper::toDto);
    }

    /**
     * Get all the approvalItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ApprovalItemDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ApprovalItems");
        return approvalItemRepository.findAll(pageable).map(approvalItemMapper::toDto);
    }

    /**
     * Get one approvalItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ApprovalItemDTO> findOne(Long id) {
        LOG.debug("Request to get ApprovalItem : {}", id);
        return approvalItemRepository.findById(id).map(approvalItemMapper::toDto);
    }

    /**
     * Delete the approvalItem by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ApprovalItem : {}", id);
        approvalItemRepository.deleteById(id);
    }
}
