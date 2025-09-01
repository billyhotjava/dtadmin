package com.yuzhi.dtadmin.web.rest;

import static com.yuzhi.dtadmin.domain.ApprovalItemAsserts.*;
import static com.yuzhi.dtadmin.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuzhi.dtadmin.IntegrationTest;
import com.yuzhi.dtadmin.domain.ApprovalItem;
import com.yuzhi.dtadmin.repository.ApprovalItemRepository;
import com.yuzhi.dtadmin.service.dto.ApprovalItemDTO;
import com.yuzhi.dtadmin.service.mapper.ApprovalItemMapper;
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
 * Integration tests for the {@link ApprovalItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ApprovalItemResourceIT {

    private static final String DEFAULT_TARGET_KIND = "AAAAAAAAAA";
    private static final String UPDATED_TARGET_KIND = "BBBBBBBBBB";

    private static final String DEFAULT_TARGET_ID = "AAAAAAAAAA";
    private static final String UPDATED_TARGET_ID = "BBBBBBBBBB";

    private static final Integer DEFAULT_SEQ_NUMBER = 1;
    private static final Integer UPDATED_SEQ_NUMBER = 2;

    private static final String DEFAULT_PAYLOAD = "AAAAAAAAAA";
    private static final String UPDATED_PAYLOAD = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/approval-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ApprovalItemRepository approvalItemRepository;

    @Autowired
    private ApprovalItemMapper approvalItemMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restApprovalItemMockMvc;

    private ApprovalItem approvalItem;

    private ApprovalItem insertedApprovalItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ApprovalItem createEntity() {
        return new ApprovalItem()
            .targetKind(DEFAULT_TARGET_KIND)
            .targetId(DEFAULT_TARGET_ID)
            .seqNumber(DEFAULT_SEQ_NUMBER)
            .payload(DEFAULT_PAYLOAD);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ApprovalItem createUpdatedEntity() {
        return new ApprovalItem()
            .targetKind(UPDATED_TARGET_KIND)
            .targetId(UPDATED_TARGET_ID)
            .seqNumber(UPDATED_SEQ_NUMBER)
            .payload(UPDATED_PAYLOAD);
    }

    @BeforeEach
    void initTest() {
        approvalItem = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedApprovalItem != null) {
            approvalItemRepository.delete(insertedApprovalItem);
            insertedApprovalItem = null;
        }
    }

    @Test
    @Transactional
    void createApprovalItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ApprovalItem
        ApprovalItemDTO approvalItemDTO = approvalItemMapper.toDto(approvalItem);
        var returnedApprovalItemDTO = om.readValue(
            restApprovalItemMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(approvalItemDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ApprovalItemDTO.class
        );

        // Validate the ApprovalItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedApprovalItem = approvalItemMapper.toEntity(returnedApprovalItemDTO);
        assertApprovalItemUpdatableFieldsEquals(returnedApprovalItem, getPersistedApprovalItem(returnedApprovalItem));

        insertedApprovalItem = returnedApprovalItem;
    }

    @Test
    @Transactional
    void createApprovalItemWithExistingId() throws Exception {
        // Create the ApprovalItem with an existing ID
        approvalItem.setId(1L);
        ApprovalItemDTO approvalItemDTO = approvalItemMapper.toDto(approvalItem);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restApprovalItemMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(approvalItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApprovalItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTargetKindIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        approvalItem.setTargetKind(null);

        // Create the ApprovalItem, which fails.
        ApprovalItemDTO approvalItemDTO = approvalItemMapper.toDto(approvalItem);

        restApprovalItemMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(approvalItemDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTargetIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        approvalItem.setTargetId(null);

        // Create the ApprovalItem, which fails.
        ApprovalItemDTO approvalItemDTO = approvalItemMapper.toDto(approvalItem);

        restApprovalItemMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(approvalItemDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSeqNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        approvalItem.setSeqNumber(null);

        // Create the ApprovalItem, which fails.
        ApprovalItemDTO approvalItemDTO = approvalItemMapper.toDto(approvalItem);

        restApprovalItemMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(approvalItemDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllApprovalItems() throws Exception {
        // Initialize the database
        insertedApprovalItem = approvalItemRepository.saveAndFlush(approvalItem);

        // Get all the approvalItemList
        restApprovalItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(approvalItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].targetKind").value(hasItem(DEFAULT_TARGET_KIND)))
            .andExpect(jsonPath("$.[*].targetId").value(hasItem(DEFAULT_TARGET_ID)))
            .andExpect(jsonPath("$.[*].seqNumber").value(hasItem(DEFAULT_SEQ_NUMBER)))
            .andExpect(jsonPath("$.[*].payload").value(hasItem(DEFAULT_PAYLOAD)));
    }

    @Test
    @Transactional
    void getApprovalItem() throws Exception {
        // Initialize the database
        insertedApprovalItem = approvalItemRepository.saveAndFlush(approvalItem);

        // Get the approvalItem
        restApprovalItemMockMvc
            .perform(get(ENTITY_API_URL_ID, approvalItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(approvalItem.getId().intValue()))
            .andExpect(jsonPath("$.targetKind").value(DEFAULT_TARGET_KIND))
            .andExpect(jsonPath("$.targetId").value(DEFAULT_TARGET_ID))
            .andExpect(jsonPath("$.seqNumber").value(DEFAULT_SEQ_NUMBER))
            .andExpect(jsonPath("$.payload").value(DEFAULT_PAYLOAD));
    }

    @Test
    @Transactional
    void getNonExistingApprovalItem() throws Exception {
        // Get the approvalItem
        restApprovalItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingApprovalItem() throws Exception {
        // Initialize the database
        insertedApprovalItem = approvalItemRepository.saveAndFlush(approvalItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the approvalItem
        ApprovalItem updatedApprovalItem = approvalItemRepository.findById(approvalItem.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedApprovalItem are not directly saved in db
        em.detach(updatedApprovalItem);
        updatedApprovalItem
            .targetKind(UPDATED_TARGET_KIND)
            .targetId(UPDATED_TARGET_ID)
            .seqNumber(UPDATED_SEQ_NUMBER)
            .payload(UPDATED_PAYLOAD);
        ApprovalItemDTO approvalItemDTO = approvalItemMapper.toDto(updatedApprovalItem);

        restApprovalItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, approvalItemDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(approvalItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the ApprovalItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedApprovalItemToMatchAllProperties(updatedApprovalItem);
    }

    @Test
    @Transactional
    void putNonExistingApprovalItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        approvalItem.setId(longCount.incrementAndGet());

        // Create the ApprovalItem
        ApprovalItemDTO approvalItemDTO = approvalItemMapper.toDto(approvalItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApprovalItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, approvalItemDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(approvalItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApprovalItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchApprovalItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        approvalItem.setId(longCount.incrementAndGet());

        // Create the ApprovalItem
        ApprovalItemDTO approvalItemDTO = approvalItemMapper.toDto(approvalItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApprovalItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(approvalItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApprovalItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamApprovalItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        approvalItem.setId(longCount.incrementAndGet());

        // Create the ApprovalItem
        ApprovalItemDTO approvalItemDTO = approvalItemMapper.toDto(approvalItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApprovalItemMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(approvalItemDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ApprovalItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateApprovalItemWithPatch() throws Exception {
        // Initialize the database
        insertedApprovalItem = approvalItemRepository.saveAndFlush(approvalItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the approvalItem using partial update
        ApprovalItem partialUpdatedApprovalItem = new ApprovalItem();
        partialUpdatedApprovalItem.setId(approvalItem.getId());

        partialUpdatedApprovalItem.targetKind(UPDATED_TARGET_KIND);

        restApprovalItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedApprovalItem.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedApprovalItem))
            )
            .andExpect(status().isOk());

        // Validate the ApprovalItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertApprovalItemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedApprovalItem, approvalItem),
            getPersistedApprovalItem(approvalItem)
        );
    }

    @Test
    @Transactional
    void fullUpdateApprovalItemWithPatch() throws Exception {
        // Initialize the database
        insertedApprovalItem = approvalItemRepository.saveAndFlush(approvalItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the approvalItem using partial update
        ApprovalItem partialUpdatedApprovalItem = new ApprovalItem();
        partialUpdatedApprovalItem.setId(approvalItem.getId());

        partialUpdatedApprovalItem
            .targetKind(UPDATED_TARGET_KIND)
            .targetId(UPDATED_TARGET_ID)
            .seqNumber(UPDATED_SEQ_NUMBER)
            .payload(UPDATED_PAYLOAD);

        restApprovalItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedApprovalItem.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedApprovalItem))
            )
            .andExpect(status().isOk());

        // Validate the ApprovalItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertApprovalItemUpdatableFieldsEquals(partialUpdatedApprovalItem, getPersistedApprovalItem(partialUpdatedApprovalItem));
    }

    @Test
    @Transactional
    void patchNonExistingApprovalItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        approvalItem.setId(longCount.incrementAndGet());

        // Create the ApprovalItem
        ApprovalItemDTO approvalItemDTO = approvalItemMapper.toDto(approvalItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApprovalItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, approvalItemDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(approvalItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApprovalItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchApprovalItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        approvalItem.setId(longCount.incrementAndGet());

        // Create the ApprovalItem
        ApprovalItemDTO approvalItemDTO = approvalItemMapper.toDto(approvalItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApprovalItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(approvalItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApprovalItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamApprovalItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        approvalItem.setId(longCount.incrementAndGet());

        // Create the ApprovalItem
        ApprovalItemDTO approvalItemDTO = approvalItemMapper.toDto(approvalItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApprovalItemMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(approvalItemDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ApprovalItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteApprovalItem() throws Exception {
        // Initialize the database
        insertedApprovalItem = approvalItemRepository.saveAndFlush(approvalItem);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the approvalItem
        restApprovalItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, approvalItem.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return approvalItemRepository.count();
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

    protected ApprovalItem getPersistedApprovalItem(ApprovalItem approvalItem) {
        return approvalItemRepository.findById(approvalItem.getId()).orElseThrow();
    }

    protected void assertPersistedApprovalItemToMatchAllProperties(ApprovalItem expectedApprovalItem) {
        assertApprovalItemAllPropertiesEquals(expectedApprovalItem, getPersistedApprovalItem(expectedApprovalItem));
    }

    protected void assertPersistedApprovalItemToMatchUpdatableProperties(ApprovalItem expectedApprovalItem) {
        assertApprovalItemAllUpdatablePropertiesEquals(expectedApprovalItem, getPersistedApprovalItem(expectedApprovalItem));
    }
}
