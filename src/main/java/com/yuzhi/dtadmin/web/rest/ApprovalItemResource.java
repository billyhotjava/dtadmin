package com.yuzhi.dtadmin.web.rest;

import com.yuzhi.dtadmin.repository.ApprovalItemRepository;
import com.yuzhi.dtadmin.service.ApprovalItemService;
import com.yuzhi.dtadmin.service.dto.ApprovalItemDTO;
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
 * REST controller for managing {@link com.yuzhi.dtadmin.domain.ApprovalItem}.
 */
@RestController
@RequestMapping("/api/approval-items")
public class ApprovalItemResource {

    private static final Logger LOG = LoggerFactory.getLogger(ApprovalItemResource.class);

    private static final String ENTITY_NAME = "approvalItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ApprovalItemService approvalItemService;

    private final ApprovalItemRepository approvalItemRepository;

    public ApprovalItemResource(ApprovalItemService approvalItemService, ApprovalItemRepository approvalItemRepository) {
        this.approvalItemService = approvalItemService;
        this.approvalItemRepository = approvalItemRepository;
    }

    /**
     * {@code POST  /approval-items} : Create a new approvalItem.
     *
     * @param approvalItemDTO the approvalItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new approvalItemDTO, or with status {@code 400 (Bad Request)} if the approvalItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ApprovalItemDTO> createApprovalItem(@Valid @RequestBody ApprovalItemDTO approvalItemDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ApprovalItem : {}", approvalItemDTO);
        if (approvalItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new approvalItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        approvalItemDTO = approvalItemService.save(approvalItemDTO);
        return ResponseEntity.created(new URI("/api/approval-items/" + approvalItemDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, approvalItemDTO.getId().toString()))
            .body(approvalItemDTO);
    }

    /**
     * {@code PUT  /approval-items/:id} : Updates an existing approvalItem.
     *
     * @param id the id of the approvalItemDTO to save.
     * @param approvalItemDTO the approvalItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated approvalItemDTO,
     * or with status {@code 400 (Bad Request)} if the approvalItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the approvalItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApprovalItemDTO> updateApprovalItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ApprovalItemDTO approvalItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ApprovalItem : {}, {}", id, approvalItemDTO);
        if (approvalItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, approvalItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!approvalItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        approvalItemDTO = approvalItemService.update(approvalItemDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, approvalItemDTO.getId().toString()))
            .body(approvalItemDTO);
    }

    /**
     * {@code PATCH  /approval-items/:id} : Partial updates given fields of an existing approvalItem, field will ignore if it is null
     *
     * @param id the id of the approvalItemDTO to save.
     * @param approvalItemDTO the approvalItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated approvalItemDTO,
     * or with status {@code 400 (Bad Request)} if the approvalItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the approvalItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the approvalItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ApprovalItemDTO> partialUpdateApprovalItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ApprovalItemDTO approvalItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ApprovalItem partially : {}, {}", id, approvalItemDTO);
        if (approvalItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, approvalItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!approvalItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ApprovalItemDTO> result = approvalItemService.partialUpdate(approvalItemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, approvalItemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /approval-items} : get all the approvalItems.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of approvalItems in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ApprovalItemDTO>> getAllApprovalItems(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of ApprovalItems");
        Page<ApprovalItemDTO> page = approvalItemService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /approval-items/:id} : get the "id" approvalItem.
     *
     * @param id the id of the approvalItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the approvalItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApprovalItemDTO> getApprovalItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ApprovalItem : {}", id);
        Optional<ApprovalItemDTO> approvalItemDTO = approvalItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(approvalItemDTO);
    }

    /**
     * {@code DELETE  /approval-items/:id} : delete the "id" approvalItem.
     *
     * @param id the id of the approvalItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApprovalItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ApprovalItem : {}", id);
        approvalItemService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
