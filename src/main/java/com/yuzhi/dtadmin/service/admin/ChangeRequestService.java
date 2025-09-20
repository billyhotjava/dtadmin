package com.yuzhi.dtadmin.service.admin;

import com.yuzhi.dtadmin.domain.ChangeRequest;
import com.yuzhi.dtadmin.domain.enumeration.ChangeResourceType;
import com.yuzhi.dtadmin.domain.enumeration.ChangeStatus;
import com.yuzhi.dtadmin.repository.ChangeRequestRepository;
import com.yuzhi.dtadmin.service.dto.ChangeRequestDTO;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ChangeRequestService {

    private final ChangeRequestRepository changeRequestRepository;
    private final ChangeExecutor changeExecutor;

    public ChangeRequestService(ChangeRequestRepository changeRequestRepository, ChangeExecutor changeExecutor) {
        this.changeRequestRepository = changeRequestRepository;
        this.changeExecutor = changeExecutor;
    }

    public ChangeRequestDTO createDraft(ChangeRequestDTO dto, String actor) {
        ChangeRequest entity = new ChangeRequest();
        entity.setResourceType(dto.getResourceType());
        entity.setResourceId(dto.getResourceId());
        entity.setAction(dto.getAction());
        entity.setPayloadJson(dto.getPayloadJson());
        entity.setDiffJson(dto.getDiffJson());
        entity.setRequestedBy(actor);
        entity.setStatus(ChangeStatus.DRAFT);
        entity.setRequestedAt(Instant.now());
        return toDto(changeRequestRepository.save(entity));
    }

    public ChangeRequestDTO submit(Long id, String actor) {
        ChangeRequest entity = changeRequestRepository.findById(id).orElseThrow(() -> new AdminOperationException("Change not found"));
        if (!entity.getRequestedBy().equals(actor)) {
            throw new AccessDeniedException("Only request creator can submit change");
        }
        if (entity.getStatus() != ChangeStatus.DRAFT) {
            throw new AdminOperationException("Only draft can be submitted");
        }
        entity.setStatus(ChangeStatus.PENDING);
        entity.setRequestedAt(Instant.now());
        changeRequestRepository.save(entity);
        return toDto(entity);
    }

    public ChangeRequestDTO approve(Long id, String actor, String reason) {
        ChangeRequest entity = changeRequestRepository.findById(id).orElseThrow(() -> new AdminOperationException("Change not found"));
        if (entity.getStatus() != ChangeStatus.PENDING) {
            throw new AdminOperationException("Only pending change can be approved");
        }
        entity.setStatus(ChangeStatus.APPROVED);
        entity.setDecidedBy(actor);
        entity.setDecidedAt(Instant.now());
        entity.setReason(reason);
        try {
            changeExecutor.execute(entity);
            entity.setStatus(ChangeStatus.APPLIED);
        } catch (AdminOperationException ex) {
            entity.setStatus(ChangeStatus.FAILED);
            throw ex;
        }
        changeRequestRepository.save(entity);
        return toDto(entity);
    }

    public ChangeRequestDTO reject(Long id, String actor, String reason) {
        ChangeRequest entity = changeRequestRepository.findById(id).orElseThrow(() -> new AdminOperationException("Change not found"));
        if (entity.getStatus() != ChangeStatus.PENDING) {
            throw new AdminOperationException("Only pending change can be rejected");
        }
        entity.setStatus(ChangeStatus.REJECTED);
        entity.setDecidedBy(actor);
        entity.setDecidedAt(Instant.now());
        entity.setReason(reason);
        changeRequestRepository.save(entity);
        return toDto(entity);
    }

    @Transactional
    public ChangeRequestDTO updateDraft(ChangeRequestDTO dto, String actor) {
        ChangeRequest entity = changeRequestRepository.findById(dto.getId()).orElseThrow(() -> new AdminOperationException("Change not found"));
        if (!entity.getRequestedBy().equals(actor)) {
            throw new AccessDeniedException("Only request creator can modify draft");
        }
        if (entity.getStatus() != ChangeStatus.DRAFT) {
            throw new AdminOperationException("Only draft can be modified");
        }
        Optional.ofNullable(dto.getResourceId()).ifPresent(entity::setResourceId);
        Optional.ofNullable(dto.getPayloadJson()).ifPresent(entity::setPayloadJson);
        Optional.ofNullable(dto.getDiffJson()).ifPresent(entity::setDiffJson);
        changeRequestRepository.save(entity);
        return toDto(entity);
    }

    @Transactional
    public List<ChangeRequestDTO> findByStatus(ChangeStatus status, ChangeResourceType type) {
        List<ChangeRequest> result;
        if (status != null && type != null) {
            result = changeRequestRepository.findByStatusAndResourceType(status, type);
        } else if (status != null) {
            result = changeRequestRepository.findByStatus(status);
        } else {
            result = changeRequestRepository.findAll();
        }
        return result.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public List<ChangeRequestDTO> findMine(String actor) {
        return changeRequestRepository
            .findByRequestedByOrderByRequestedAtDesc(actor)
            .stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    private ChangeRequestDTO toDto(ChangeRequest entity) {
        ChangeRequestDTO dto = new ChangeRequestDTO();
        dto.setId(entity.getId());
        dto.setResourceType(entity.getResourceType());
        dto.setResourceId(entity.getResourceId());
        dto.setAction(entity.getAction());
        dto.setPayloadJson(entity.getPayloadJson());
        dto.setDiffJson(entity.getDiffJson());
        dto.setStatus(entity.getStatus());
        dto.setRequestedBy(entity.getRequestedBy());
        dto.setRequestedAt(entity.getRequestedAt());
        dto.setDecidedBy(entity.getDecidedBy());
        dto.setDecidedAt(entity.getDecidedAt());
        dto.setReason(entity.getReason());
        return dto;
    }
}
