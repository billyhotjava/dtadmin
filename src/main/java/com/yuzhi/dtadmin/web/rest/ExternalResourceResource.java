package com.yuzhi.dtadmin.web.rest;

import com.yuzhi.dtadmin.repository.ExternalResourceRepository;
import com.yuzhi.dtadmin.service.ExternalResourceService;
import com.yuzhi.dtadmin.service.dto.ExternalResourceDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.yuzhi.dtadmin.domain.ExternalResource}.
 */
@RestController
@RequestMapping("/api/external-resources")
public class ExternalResourceResource {

    private static final Logger LOG = LoggerFactory.getLogger(ExternalResourceResource.class);

    private static final String ENTITY_NAME = "externalResource";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ExternalResourceService externalResourceService;

    private final ExternalResourceRepository externalResourceRepository;

    public ExternalResourceResource(
        ExternalResourceService externalResourceService,
        ExternalResourceRepository externalResourceRepository
    ) {
        this.externalResourceService = externalResourceService;
        this.externalResourceRepository = externalResourceRepository;
    }

    /**
     * {@code POST  /external-resources} : Create a new externalResource.
     *
     * @param externalResourceDTO the externalResourceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new externalResourceDTO, or with status {@code 400 (Bad Request)} if the externalResource has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ExternalResourceDTO> createExternalResource(@Valid @RequestBody ExternalResourceDTO externalResourceDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ExternalResource : {}", externalResourceDTO);
        if (externalResourceDTO.getId() != null) {
            throw new BadRequestAlertException("A new externalResource cannot already have an ID", ENTITY_NAME, "idexists");
        }
        externalResourceDTO = externalResourceService.save(externalResourceDTO);
        return ResponseEntity.created(new URI("/api/external-resources/" + externalResourceDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, externalResourceDTO.getId().toString()))
            .body(externalResourceDTO);
    }

    /**
     * {@code PUT  /external-resources/:id} : Updates an existing externalResource.
     *
     * @param id the id of the externalResourceDTO to save.
     * @param externalResourceDTO the externalResourceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated externalResourceDTO,
     * or with status {@code 400 (Bad Request)} if the externalResourceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the externalResourceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ExternalResourceDTO> updateExternalResource(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ExternalResourceDTO externalResourceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ExternalResource : {}, {}", id, externalResourceDTO);
        if (externalResourceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, externalResourceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!externalResourceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        externalResourceDTO = externalResourceService.update(externalResourceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, externalResourceDTO.getId().toString()))
            .body(externalResourceDTO);
    }

    /**
     * {@code PATCH  /external-resources/:id} : Partial updates given fields of an existing externalResource, field will ignore if it is null
     *
     * @param id the id of the externalResourceDTO to save.
     * @param externalResourceDTO the externalResourceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated externalResourceDTO,
     * or with status {@code 400 (Bad Request)} if the externalResourceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the externalResourceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the externalResourceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ExternalResourceDTO> partialUpdateExternalResource(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ExternalResourceDTO externalResourceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ExternalResource partially : {}, {}", id, externalResourceDTO);
        if (externalResourceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, externalResourceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!externalResourceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ExternalResourceDTO> result = externalResourceService.partialUpdate(externalResourceDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, externalResourceDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /external-resources} : get all the externalResources.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of externalResources in body.
     */
    @GetMapping("")
    public List<ExternalResourceDTO> getAllExternalResources() {
        LOG.debug("REST request to get all ExternalResources");
        return externalResourceService.findAll();
    }

    /**
     * {@code GET  /external-resources/:id} : get the "id" externalResource.
     *
     * @param id the id of the externalResourceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the externalResourceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExternalResourceDTO> getExternalResource(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ExternalResource : {}", id);
        Optional<ExternalResourceDTO> externalResourceDTO = externalResourceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(externalResourceDTO);
    }

    /**
     * {@code DELETE  /external-resources/:id} : delete the "id" externalResource.
     *
     * @param id the id of the externalResourceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExternalResource(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ExternalResource : {}", id);
        externalResourceService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
