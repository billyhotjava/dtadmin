package com.yuzhi.dtadmin.web.rest;

import com.yuzhi.dtadmin.repository.ApprovalRequestRepository;
import com.yuzhi.dtadmin.service.ApprovalRequestService;
import com.yuzhi.dtadmin.service.dto.ApprovalRequestDTO;
import com.yuzhi.dtadmin.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.yuzhi.dtadmin.domain.ApprovalRequest}.
 */
@RestController
@RequestMapping("/api/approval-requests")
public class ApprovalRequestResource {

    private static final Logger LOG = LoggerFactory.getLogger(ApprovalRequestResource.class);

    private static final String ENTITY_NAME = "approvalRequest";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ApprovalRequestService approvalRequestService;

    private final ApprovalRequestRepository approvalRequestRepository;

    public ApprovalRequestResource(ApprovalRequestService approvalRequestService, ApprovalRequestRepository approvalRequestRepository) {
        this.approvalRequestService = approvalRequestService;
        this.approvalRequestRepository = approvalRequestRepository;
    }

    /**
     * {@code POST  /approval-requests} : Create a new approvalRequest.
     *
     * @param approvalRequestDTO the approvalRequestDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new approvalRequestDTO, or with status {@code 400 (Bad Request)} if the approvalRequest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ApprovalRequestDTO> createApprovalRequest(@Valid @RequestBody ApprovalRequestDTO approvalRequestDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ApprovalRequest : {}", approvalRequestDTO);
        if (approvalRequestDTO.getId() != null) {
            throw new BadRequestAlertException("A new approvalRequest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        approvalRequestDTO = approvalRequestService.save(approvalRequestDTO);
        return ResponseEntity.created(new URI("/api/approval-requests/" + approvalRequestDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, approvalRequestDTO.getId().toString()))
            .body(approvalRequestDTO);
    }

    /**
     * {@code PUT  /approval-requests/:id} : Updates an existing approvalRequest.
     *
     * @param id the id of the approvalRequestDTO to save.
     * @param approvalRequestDTO the approvalRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated approvalRequestDTO,
     * or with status {@code 400 (Bad Request)} if the approvalRequestDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the approvalRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApprovalRequestDTO> updateApprovalRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ApprovalRequestDTO approvalRequestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ApprovalRequest : {}, {}", id, approvalRequestDTO);
        if (approvalRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, approvalRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!approvalRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        approvalRequestDTO = approvalRequestService.update(approvalRequestDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, approvalRequestDTO.getId().toString()))
            .body(approvalRequestDTO);
    }

    /**
     * {@code PATCH  /approval-requests/:id} : Partial updates given fields of an existing approvalRequest, field will ignore if it is null
     *
     * @param id the id of the approvalRequestDTO to save.
     * @param approvalRequestDTO the approvalRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated approvalRequestDTO,
     * or with status {@code 400 (Bad Request)} if the approvalRequestDTO is not valid,
     * or with status {@code 404 (Not Found)} if the approvalRequestDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the approvalRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ApprovalRequestDTO> partialUpdateApprovalRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ApprovalRequestDTO approvalRequestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ApprovalRequest partially : {}, {}", id, approvalRequestDTO);
        if (approvalRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, approvalRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!approvalRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ApprovalRequestDTO> result = approvalRequestService.partialUpdate(approvalRequestDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, approvalRequestDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /approval-requests} : get all the approvalRequests.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of approvalRequests in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ApprovalRequestDTO>> getAllApprovalRequests(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of ApprovalRequests");
        Page<ApprovalRequestDTO> page = approvalRequestService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /approval-requests/:id} : get the "id" approvalRequest.
     *
     * @param id the id of the approvalRequestDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the approvalRequestDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApprovalRequestDTO> getApprovalRequest(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ApprovalRequest : {}", id);
        Optional<ApprovalRequestDTO> approvalRequestDTO = approvalRequestService.findOne(id);
        return ResponseUtil.wrapOrNotFound(approvalRequestDTO);
    }

    /**
     * {@code DELETE  /approval-requests/:id} : delete the "id" approvalRequest.
     *
     * @param id the id of the approvalRequestDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApprovalRequest(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ApprovalRequest : {}", id);
        approvalRequestService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
