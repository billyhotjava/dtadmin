package com.yuzhi.dtadmin.service;

import com.yuzhi.dtadmin.domain.ApprovalRequest;
import com.yuzhi.dtadmin.repository.ApprovalRequestRepository;
import com.yuzhi.dtadmin.service.dto.ApprovalRequestDTO;
import com.yuzhi.dtadmin.service.mapper.ApprovalRequestMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.yuzhi.dtadmin.domain.ApprovalRequest}.
 */
@Service
@Transactional
public class ApprovalRequestService {

    private static final Logger LOG = LoggerFactory.getLogger(ApprovalRequestService.class);

    private final ApprovalRequestRepository approvalRequestRepository;

    private final ApprovalRequestMapper approvalRequestMapper;

    public ApprovalRequestService(ApprovalRequestRepository approvalRequestRepository, ApprovalRequestMapper approvalRequestMapper) {
        this.approvalRequestRepository = approvalRequestRepository;
        this.approvalRequestMapper = approvalRequestMapper;
    }

    /**
     * Save a approvalRequest.
     *
     * @param approvalRequestDTO the entity to save.
     * @return the persisted entity.
     */
    public ApprovalRequestDTO save(ApprovalRequestDTO approvalRequestDTO) {
        LOG.debug("Request to save ApprovalRequest : {}", approvalRequestDTO);
        ApprovalRequest approvalRequest = approvalRequestMapper.toEntity(approvalRequestDTO);
        approvalRequest = approvalRequestRepository.save(approvalRequest);
        return approvalRequestMapper.toDto(approvalRequest);
    }

    /**
     * Update a approvalRequest.
     *
     * @param approvalRequestDTO the entity to save.
     * @return the persisted entity.
     */
    public ApprovalRequestDTO update(ApprovalRequestDTO approvalRequestDTO) {
        LOG.debug("Request to update ApprovalRequest : {}", approvalRequestDTO);
        ApprovalRequest approvalRequest = approvalRequestMapper.toEntity(approvalRequestDTO);
        approvalRequest = approvalRequestRepository.save(approvalRequest);
        return approvalRequestMapper.toDto(approvalRequest);
    }

    /**
     * Partially update a approvalRequest.
     *
     * @param approvalRequestDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ApprovalRequestDTO> partialUpdate(ApprovalRequestDTO approvalRequestDTO) {
        LOG.debug("Request to partially update ApprovalRequest : {}", approvalRequestDTO);

        return approvalRequestRepository
            .findById(approvalRequestDTO.getId())
            .map(existingApprovalRequest -> {
                approvalRequestMapper.partialUpdate(existingApprovalRequest, approvalRequestDTO);

                return existingApprovalRequest;
            })
            .map(approvalRequestRepository::save)
            .map(approvalRequestMapper::toDto);
    }

    /**
     * Get all the approvalRequests.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ApprovalRequestDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ApprovalRequests");
        return approvalRequestRepository.findAll(pageable).map(approvalRequestMapper::toDto);
    }

    /**
     * Get one approvalRequest by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ApprovalRequestDTO> findOne(Long id) {
        LOG.debug("Request to get ApprovalRequest : {}", id);
        return approvalRequestRepository.findById(id).map(approvalRequestMapper::toDto);
    }

    /**
     * Delete the approvalRequest by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ApprovalRequest : {}", id);
        approvalRequestRepository.deleteById(id);
    }
}
