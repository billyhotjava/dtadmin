package com.yuzhi.dtadmin.web.rest;

import static com.yuzhi.dtadmin.domain.ExternalResourceAsserts.*;
import static com.yuzhi.dtadmin.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuzhi.dtadmin.IntegrationTest;
import com.yuzhi.dtadmin.domain.ExternalResource;
import com.yuzhi.dtadmin.repository.ExternalResourceRepository;
import com.yuzhi.dtadmin.service.dto.ExternalResourceDTO;
import com.yuzhi.dtadmin.service.mapper.ExternalResourceMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ExternalResourceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ExternalResourceResourceIT {

    private static final String DEFAULT_URN = "AAAAAAAAAA";
    private static final String UPDATED_URN = "BBBBBBBBBB";

    private static final String DEFAULT_MAX_LEVEL = "AAAAAAAAAA";
    private static final String UPDATED_MAX_LEVEL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/external-resources";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ExternalResourceRepository externalResourceRepository;

    @Autowired
    private ExternalResourceMapper externalResourceMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restExternalResourceMockMvc;

    private ExternalResource externalResource;

    private ExternalResource insertedExternalResource;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ExternalResource createEntity() {
        return new ExternalResource().urn(DEFAULT_URN).maxLevel(DEFAULT_MAX_LEVEL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ExternalResource createUpdatedEntity() {
        return new ExternalResource().urn(UPDATED_URN).maxLevel(UPDATED_MAX_LEVEL);
    }

    @BeforeEach
    void initTest() {
        externalResource = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedExternalResource != null) {
            externalResourceRepository.delete(insertedExternalResource);
            insertedExternalResource = null;
        }
    }

    @Test
    @Transactional
    void createExternalResource() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ExternalResource
        ExternalResourceDTO externalResourceDTO = externalResourceMapper.toDto(externalResource);
        var returnedExternalResourceDTO = om.readValue(
            restExternalResourceMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(externalResourceDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ExternalResourceDTO.class
        );

        // Validate the ExternalResource in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedExternalResource = externalResourceMapper.toEntity(returnedExternalResourceDTO);
        assertExternalResourceUpdatableFieldsEquals(returnedExternalResource, getPersistedExternalResource(returnedExternalResource));

        insertedExternalResource = returnedExternalResource;
    }

    @Test
    @Transactional
    void createExternalResourceWithExistingId() throws Exception {
        // Create the ExternalResource with an existing ID
        externalResource.setId(1L);
        ExternalResourceDTO externalResourceDTO = externalResourceMapper.toDto(externalResource);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restExternalResourceMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(externalResourceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExternalResource in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUrnIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        externalResource.setUrn(null);

        // Create the ExternalResource, which fails.
        ExternalResourceDTO externalResourceDTO = externalResourceMapper.toDto(externalResource);

        restExternalResourceMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(externalResourceDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMaxLevelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        externalResource.setMaxLevel(null);

        // Create the ExternalResource, which fails.
        ExternalResourceDTO externalResourceDTO = externalResourceMapper.toDto(externalResource);

        restExternalResourceMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(externalResourceDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllExternalResources() throws Exception {
        // Initialize the database
        insertedExternalResource = externalResourceRepository.saveAndFlush(externalResource);

        // Get all the externalResourceList
        restExternalResourceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(externalResource.getId().intValue())))
            .andExpect(jsonPath("$.[*].urn").value(hasItem(DEFAULT_URN)))
            .andExpect(jsonPath("$.[*].maxLevel").value(hasItem(DEFAULT_MAX_LEVEL)));
    }

    @Test
    @Transactional
    void getExternalResource() throws Exception {
        // Initialize the database
        insertedExternalResource = externalResourceRepository.saveAndFlush(externalResource);

        // Get the externalResource
        restExternalResourceMockMvc
            .perform(get(ENTITY_API_URL_ID, externalResource.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(externalResource.getId().intValue()))
            .andExpect(jsonPath("$.urn").value(DEFAULT_URN))
            .andExpect(jsonPath("$.maxLevel").value(DEFAULT_MAX_LEVEL));
    }

    @Test
    @Transactional
    void getNonExistingExternalResource() throws Exception {
        // Get the externalResource
        restExternalResourceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingExternalResource() throws Exception {
        // Initialize the database
        insertedExternalResource = externalResourceRepository.saveAndFlush(externalResource);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the externalResource
        ExternalResource updatedExternalResource = externalResourceRepository.findById(externalResource.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedExternalResource are not directly saved in db
        em.detach(updatedExternalResource);
        updatedExternalResource.urn(UPDATED_URN).maxLevel(UPDATED_MAX_LEVEL);
        ExternalResourceDTO externalResourceDTO = externalResourceMapper.toDto(updatedExternalResource);

        restExternalResourceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, externalResourceDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(externalResourceDTO))
            )
            .andExpect(status().isOk());

        // Validate the ExternalResource in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedExternalResourceToMatchAllProperties(updatedExternalResource);
    }

    @Test
    @Transactional
    void putNonExistingExternalResource() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        externalResource.setId(longCount.incrementAndGet());

        // Create the ExternalResource
        ExternalResourceDTO externalResourceDTO = externalResourceMapper.toDto(externalResource);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExternalResourceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, externalResourceDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(externalResourceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExternalResource in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchExternalResource() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        externalResource.setId(longCount.incrementAndGet());

        // Create the ExternalResource
        ExternalResourceDTO externalResourceDTO = externalResourceMapper.toDto(externalResource);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExternalResourceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(externalResourceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExternalResource in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamExternalResource() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        externalResource.setId(longCount.incrementAndGet());

        // Create the ExternalResource
        ExternalResourceDTO externalResourceDTO = externalResourceMapper.toDto(externalResource);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExternalResourceMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(externalResourceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ExternalResource in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateExternalResourceWithPatch() throws Exception {
        // Initialize the database
        insertedExternalResource = externalResourceRepository.saveAndFlush(externalResource);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the externalResource using partial update
        ExternalResource partialUpdatedExternalResource = new ExternalResource();
        partialUpdatedExternalResource.setId(externalResource.getId());

        restExternalResourceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExternalResource.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedExternalResource))
            )
            .andExpect(status().isOk());

        // Validate the ExternalResource in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExternalResourceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedExternalResource, externalResource),
            getPersistedExternalResource(externalResource)
        );
    }

    @Test
    @Transactional
    void fullUpdateExternalResourceWithPatch() throws Exception {
        // Initialize the database
        insertedExternalResource = externalResourceRepository.saveAndFlush(externalResource);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the externalResource using partial update
        ExternalResource partialUpdatedExternalResource = new ExternalResource();
        partialUpdatedExternalResource.setId(externalResource.getId());

        partialUpdatedExternalResource.urn(UPDATED_URN).maxLevel(UPDATED_MAX_LEVEL);

        restExternalResourceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExternalResource.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedExternalResource))
            )
            .andExpect(status().isOk());

        // Validate the ExternalResource in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExternalResourceUpdatableFieldsEquals(
            partialUpdatedExternalResource,
            getPersistedExternalResource(partialUpdatedExternalResource)
        );
    }

    @Test
    @Transactional
    void patchNonExistingExternalResource() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        externalResource.setId(longCount.incrementAndGet());

        // Create the ExternalResource
        ExternalResourceDTO externalResourceDTO = externalResourceMapper.toDto(externalResource);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExternalResourceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, externalResourceDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(externalResourceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExternalResource in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchExternalResource() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        externalResource.setId(longCount.incrementAndGet());

        // Create the ExternalResource
        ExternalResourceDTO externalResourceDTO = externalResourceMapper.toDto(externalResource);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExternalResourceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(externalResourceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExternalResource in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamExternalResource() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        externalResource.setId(longCount.incrementAndGet());

        // Create the ExternalResource
        ExternalResourceDTO externalResourceDTO = externalResourceMapper.toDto(externalResource);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExternalResourceMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(externalResourceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ExternalResource in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteExternalResource() throws Exception {
        // Initialize the database
        insertedExternalResource = externalResourceRepository.saveAndFlush(externalResource);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the externalResource
        restExternalResourceMockMvc
            .perform(delete(ENTITY_API_URL_ID, externalResource.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return externalResourceRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected ExternalResource getPersistedExternalResource(ExternalResource externalResource) {
        return externalResourceRepository.findById(externalResource.getId()).orElseThrow();
    }

    protected void assertPersistedExternalResourceToMatchAllProperties(ExternalResource expectedExternalResource) {
        assertExternalResourceAllPropertiesEquals(expectedExternalResource, getPersistedExternalResource(expectedExternalResource));
    }

    protected void assertPersistedExternalResourceToMatchUpdatableProperties(ExternalResource expectedExternalResource) {
        assertExternalResourceAllUpdatablePropertiesEquals(
            expectedExternalResource,
            getPersistedExternalResource(expectedExternalResource)
        );
    }
}
