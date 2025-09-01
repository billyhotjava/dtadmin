package com.yuzhi.dtadmin.web.rest;

import static com.yuzhi.dtadmin.domain.ApprovalRequestAsserts.*;
import static com.yuzhi.dtadmin.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuzhi.dtadmin.IntegrationTest;
import com.yuzhi.dtadmin.domain.ApprovalRequest;
import com.yuzhi.dtadmin.domain.enumeration.ApprovalStatus;
import com.yuzhi.dtadmin.domain.enumeration.ApprovalType;
import com.yuzhi.dtadmin.repository.ApprovalRequestRepository;
import com.yuzhi.dtadmin.service.dto.ApprovalRequestDTO;
import com.yuzhi.dtadmin.service.mapper.ApprovalRequestMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link ApprovalRequestResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ApprovalRequestResourceIT {

    private static final String DEFAULT_REQUESTER = "AAAAAAAAAA";
    private static final String UPDATED_REQUESTER = "BBBBBBBBBB";

    private static final ApprovalType DEFAULT_TYPE = ApprovalType.CREATE_USER;
    private static final ApprovalType UPDATED_TYPE = ApprovalType.UPDATE_USER;

    private static final String DEFAULT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REASON = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DECIDED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DECIDED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final ApprovalStatus DEFAULT_STATUS = ApprovalStatus.PENDING;
    private static final ApprovalStatus UPDATED_STATUS = ApprovalStatus.APPROVED;

    private static final String DEFAULT_APPROVER = "AAAAAAAAAA";
    private static final String UPDATED_APPROVER = "BBBBBBBBBB";

    private static final String DEFAULT_DECISION_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_DECISION_NOTE = "BBBBBBBBBB";

    private static final String DEFAULT_ERROR_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_ERROR_MESSAGE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/approval-requests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ApprovalRequestRepository approvalRequestRepository;

    @Autowired
    private ApprovalRequestMapper approvalRequestMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restApprovalRequestMockMvc;

    private ApprovalRequest approvalRequest;

    private ApprovalRequest insertedApprovalRequest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ApprovalRequest createEntity() {
        return new ApprovalRequest()
            .requester(DEFAULT_REQUESTER)
            .type(DEFAULT_TYPE)
            .reason(DEFAULT_REASON)
            .createdAt(DEFAULT_CREATED_AT)
            .decidedAt(DEFAULT_DECIDED_AT)
            .status(DEFAULT_STATUS)
            .approver(DEFAULT_APPROVER)
            .decisionNote(DEFAULT_DECISION_NOTE)
            .errorMessage(DEFAULT_ERROR_MESSAGE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ApprovalRequest createUpdatedEntity() {
        return new ApprovalRequest()
            .requester(UPDATED_REQUESTER)
            .type(UPDATED_TYPE)
            .reason(UPDATED_REASON)
            .createdAt(UPDATED_CREATED_AT)
            .decidedAt(UPDATED_DECIDED_AT)
            .status(UPDATED_STATUS)
            .approver(UPDATED_APPROVER)
            .decisionNote(UPDATED_DECISION_NOTE)
            .errorMessage(UPDATED_ERROR_MESSAGE);
    }

    @BeforeEach
    void initTest() {
        approvalRequest = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedApprovalRequest != null) {
            approvalRequestRepository.delete(insertedApprovalRequest);
            insertedApprovalRequest = null;
        }
    }

    @Test
    @Transactional
    void createApprovalRequest() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ApprovalRequest
        ApprovalRequestDTO approvalRequestDTO = approvalRequestMapper.toDto(approvalRequest);
        var returnedApprovalRequestDTO = om.readValue(
            restApprovalRequestMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(approvalRequestDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ApprovalRequestDTO.class
        );

        // Validate the ApprovalRequest in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedApprovalRequest = approvalRequestMapper.toEntity(returnedApprovalRequestDTO);
        assertApprovalRequestUpdatableFieldsEquals(returnedApprovalRequest, getPersistedApprovalRequest(returnedApprovalRequest));

        insertedApprovalRequest = returnedApprovalRequest;
    }

    @Test
    @Transactional
    void createApprovalRequestWithExistingId() throws Exception {
        // Create the ApprovalRequest with an existing ID
        approvalRequest.setId(1L);
        ApprovalRequestDTO approvalRequestDTO = approvalRequestMapper.toDto(approvalRequest);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restApprovalRequestMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(approvalRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApprovalRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRequesterIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        approvalRequest.setRequester(null);

        // Create the ApprovalRequest, which fails.
        ApprovalRequestDTO approvalRequestDTO = approvalRequestMapper.toDto(approvalRequest);

        restApprovalRequestMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(approvalRequestDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        approvalRequest.setType(null);

        // Create the ApprovalRequest, which fails.
        ApprovalRequestDTO approvalRequestDTO = approvalRequestMapper.toDto(approvalRequest);

        restApprovalRequestMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(approvalRequestDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        approvalRequest.setCreatedAt(null);

        // Create the ApprovalRequest, which fails.
        ApprovalRequestDTO approvalRequestDTO = approvalRequestMapper.toDto(approvalRequest);

        restApprovalRequestMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(approvalRequestDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        approvalRequest.setStatus(null);

        // Create the ApprovalRequest, which fails.
        ApprovalRequestDTO approvalRequestDTO = approvalRequestMapper.toDto(approvalRequest);

        restApprovalRequestMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(approvalRequestDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllApprovalRequests() throws Exception {
        // Initialize the database
        insertedApprovalRequest = approvalRequestRepository.saveAndFlush(approvalRequest);

        // Get all the approvalRequestList
        restApprovalRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(approvalRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].requester").value(hasItem(DEFAULT_REQUESTER)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].decidedAt").value(hasItem(DEFAULT_DECIDED_AT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].approver").value(hasItem(DEFAULT_APPROVER)))
            .andExpect(jsonPath("$.[*].decisionNote").value(hasItem(DEFAULT_DECISION_NOTE)))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE)));
    }

    @Test
    @Transactional
    void getApprovalRequest() throws Exception {
        // Initialize the database
        insertedApprovalRequest = approvalRequestRepository.saveAndFlush(approvalRequest);

        // Get the approvalRequest
        restApprovalRequestMockMvc
            .perform(get(ENTITY_API_URL_ID, approvalRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(approvalRequest.getId().intValue()))
            .andExpect(jsonPath("$.requester").value(DEFAULT_REQUESTER))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.reason").value(DEFAULT_REASON))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.decidedAt").value(DEFAULT_DECIDED_AT.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.approver").value(DEFAULT_APPROVER))
            .andExpect(jsonPath("$.decisionNote").value(DEFAULT_DECISION_NOTE))
            .andExpect(jsonPath("$.errorMessage").value(DEFAULT_ERROR_MESSAGE));
    }

    @Test
    @Transactional
    void getNonExistingApprovalRequest() throws Exception {
        // Get the approvalRequest
        restApprovalRequestMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingApprovalRequest() throws Exception {
        // Initialize the database
        insertedApprovalRequest = approvalRequestRepository.saveAndFlush(approvalRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the approvalRequest
        ApprovalRequest updatedApprovalRequest = approvalRequestRepository.findById(approvalRequest.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedApprovalRequest are not directly saved in db
        em.detach(updatedApprovalRequest);
        updatedApprovalRequest
            .requester(UPDATED_REQUESTER)
            .type(UPDATED_TYPE)
            .reason(UPDATED_REASON)
            .createdAt(UPDATED_CREATED_AT)
            .decidedAt(UPDATED_DECIDED_AT)
            .status(UPDATED_STATUS)
            .approver(UPDATED_APPROVER)
            .decisionNote(UPDATED_DECISION_NOTE)
            .errorMessage(UPDATED_ERROR_MESSAGE);
        ApprovalRequestDTO approvalRequestDTO = approvalRequestMapper.toDto(updatedApprovalRequest);

        restApprovalRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, approvalRequestDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(approvalRequestDTO))
            )
            .andExpect(status().isOk());

        // Validate the ApprovalRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedApprovalRequestToMatchAllProperties(updatedApprovalRequest);
    }

    @Test
    @Transactional
    void putNonExistingApprovalRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        approvalRequest.setId(longCount.incrementAndGet());

        // Create the ApprovalRequest
        ApprovalRequestDTO approvalRequestDTO = approvalRequestMapper.toDto(approvalRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApprovalRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, approvalRequestDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(approvalRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApprovalRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchApprovalRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        approvalRequest.setId(longCount.incrementAndGet());

        // Create the ApprovalRequest
        ApprovalRequestDTO approvalRequestDTO = approvalRequestMapper.toDto(approvalRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApprovalRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(approvalRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApprovalRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamApprovalRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        approvalRequest.setId(longCount.incrementAndGet());

        // Create the ApprovalRequest
        ApprovalRequestDTO approvalRequestDTO = approvalRequestMapper.toDto(approvalRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApprovalRequestMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(approvalRequestDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ApprovalRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateApprovalRequestWithPatch() throws Exception {
        // Initialize the database
        insertedApprovalRequest = approvalRequestRepository.saveAndFlush(approvalRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the approvalRequest using partial update
        ApprovalRequest partialUpdatedApprovalRequest = new ApprovalRequest();
        partialUpdatedApprovalRequest.setId(approvalRequest.getId());

        partialUpdatedApprovalRequest
            .requester(UPDATED_REQUESTER)
            .type(UPDATED_TYPE)
            .reason(UPDATED_REASON)
            .approver(UPDATED_APPROVER)
            .decisionNote(UPDATED_DECISION_NOTE);

        restApprovalRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedApprovalRequest.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedApprovalRequest))
            )
            .andExpect(status().isOk());

        // Validate the ApprovalRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertApprovalRequestUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedApprovalRequest, approvalRequest),
            getPersistedApprovalRequest(approvalRequest)
        );
    }

    @Test
    @Transactional
    void fullUpdateApprovalRequestWithPatch() throws Exception {
        // Initialize the database
        insertedApprovalRequest = approvalRequestRepository.saveAndFlush(approvalRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the approvalRequest using partial update
        ApprovalRequest partialUpdatedApprovalRequest = new ApprovalRequest();
        partialUpdatedApprovalRequest.setId(approvalRequest.getId());

        partialUpdatedApprovalRequest
            .requester(UPDATED_REQUESTER)
            .type(UPDATED_TYPE)
            .reason(UPDATED_REASON)
            .createdAt(UPDATED_CREATED_AT)
            .decidedAt(UPDATED_DECIDED_AT)
            .status(UPDATED_STATUS)
            .approver(UPDATED_APPROVER)
            .decisionNote(UPDATED_DECISION_NOTE)
            .errorMessage(UPDATED_ERROR_MESSAGE);

        restApprovalRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedApprovalRequest.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedApprovalRequest))
            )
            .andExpect(status().isOk());

        // Validate the ApprovalRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertApprovalRequestUpdatableFieldsEquals(
            partialUpdatedApprovalRequest,
            getPersistedApprovalRequest(partialUpdatedApprovalRequest)
        );
    }

    @Test
    @Transactional
    void patchNonExistingApprovalRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        approvalRequest.setId(longCount.incrementAndGet());

        // Create the ApprovalRequest
        ApprovalRequestDTO approvalRequestDTO = approvalRequestMapper.toDto(approvalRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApprovalRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, approvalRequestDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(approvalRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApprovalRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchApprovalRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        approvalRequest.setId(longCount.incrementAndGet());

        // Create the ApprovalRequest
        ApprovalRequestDTO approvalRequestDTO = approvalRequestMapper.toDto(approvalRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApprovalRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(approvalRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApprovalRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamApprovalRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        approvalRequest.setId(longCount.incrementAndGet());

        // Create the ApprovalRequest
        ApprovalRequestDTO approvalRequestDTO = approvalRequestMapper.toDto(approvalRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApprovalRequestMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(approvalRequestDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ApprovalRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteApprovalRequest() throws Exception {
        // Initialize the database
        insertedApprovalRequest = approvalRequestRepository.saveAndFlush(approvalRequest);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the approvalRequest
        restApprovalRequestMockMvc
            .perform(delete(ENTITY_API_URL_ID, approvalRequest.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return approvalRequestRepository.count();
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

    protected ApprovalRequest getPersistedApprovalRequest(ApprovalRequest approvalRequest) {
        return approvalRequestRepository.findById(approvalRequest.getId()).orElseThrow();
    }

    protected void assertPersistedApprovalRequestToMatchAllProperties(ApprovalRequest expectedApprovalRequest) {
        assertApprovalRequestAllPropertiesEquals(expectedApprovalRequest, getPersistedApprovalRequest(expectedApprovalRequest));
    }

    protected void assertPersistedApprovalRequestToMatchUpdatableProperties(ApprovalRequest expectedApprovalRequest) {
        assertApprovalRequestAllUpdatablePropertiesEquals(expectedApprovalRequest, getPersistedApprovalRequest(expectedApprovalRequest));
    }
}
