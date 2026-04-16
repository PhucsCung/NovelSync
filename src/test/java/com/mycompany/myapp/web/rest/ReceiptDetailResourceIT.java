package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.ReceiptDetailAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.ReceiptDetail;
import com.mycompany.myapp.repository.ReceiptDetailRepository;
import com.mycompany.myapp.service.ReceiptDetailService;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ReceiptDetailResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ReceiptDetailResourceIT {

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final BigDecimal DEFAULT_PURCHASE_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_PURCHASE_PRICE = new BigDecimal(1);

    private static final String ENTITY_API_URL = "/api/receipt-details";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReceiptDetailRepository receiptDetailRepository;

    @Mock
    private ReceiptDetailRepository receiptDetailRepositoryMock;

    @Mock
    private ReceiptDetailService receiptDetailServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReceiptDetailMockMvc;

    private ReceiptDetail receiptDetail;

    private ReceiptDetail insertedReceiptDetail;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReceiptDetail createEntity() {
        return new ReceiptDetail().quantity(DEFAULT_QUANTITY).purchasePrice(DEFAULT_PURCHASE_PRICE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReceiptDetail createUpdatedEntity() {
        return new ReceiptDetail().quantity(UPDATED_QUANTITY).purchasePrice(UPDATED_PURCHASE_PRICE);
    }

    @BeforeEach
    void initTest() {
        receiptDetail = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedReceiptDetail != null) {
            receiptDetailRepository.delete(insertedReceiptDetail);
            insertedReceiptDetail = null;
        }
    }

    @Test
    @Transactional
    void createReceiptDetail() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ReceiptDetail
        var returnedReceiptDetail = om.readValue(
            restReceiptDetailMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(receiptDetail)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ReceiptDetail.class
        );

        // Validate the ReceiptDetail in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertReceiptDetailUpdatableFieldsEquals(returnedReceiptDetail, getPersistedReceiptDetail(returnedReceiptDetail));

        insertedReceiptDetail = returnedReceiptDetail;
    }

    @Test
    @Transactional
    void createReceiptDetailWithExistingId() throws Exception {
        // Create the ReceiptDetail with an existing ID
        receiptDetail.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReceiptDetailMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(receiptDetail)))
            .andExpect(status().isBadRequest());

        // Validate the ReceiptDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        receiptDetail.setQuantity(null);

        // Create the ReceiptDetail, which fails.

        restReceiptDetailMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(receiptDetail)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPurchasePriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        receiptDetail.setPurchasePrice(null);

        // Create the ReceiptDetail, which fails.

        restReceiptDetailMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(receiptDetail)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllReceiptDetails() throws Exception {
        // Initialize the database
        insertedReceiptDetail = receiptDetailRepository.saveAndFlush(receiptDetail);

        // Get all the receiptDetailList
        restReceiptDetailMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(receiptDetail.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].purchasePrice").value(hasItem(sameNumber(DEFAULT_PURCHASE_PRICE))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReceiptDetailsWithEagerRelationshipsIsEnabled() throws Exception {
        when(receiptDetailServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restReceiptDetailMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(receiptDetailServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReceiptDetailsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(receiptDetailServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restReceiptDetailMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(receiptDetailRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getReceiptDetail() throws Exception {
        // Initialize the database
        insertedReceiptDetail = receiptDetailRepository.saveAndFlush(receiptDetail);

        // Get the receiptDetail
        restReceiptDetailMockMvc
            .perform(get(ENTITY_API_URL_ID, receiptDetail.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(receiptDetail.getId().intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.purchasePrice").value(sameNumber(DEFAULT_PURCHASE_PRICE)));
    }

    @Test
    @Transactional
    void getNonExistingReceiptDetail() throws Exception {
        // Get the receiptDetail
        restReceiptDetailMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingReceiptDetail() throws Exception {
        // Initialize the database
        insertedReceiptDetail = receiptDetailRepository.saveAndFlush(receiptDetail);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the receiptDetail
        ReceiptDetail updatedReceiptDetail = receiptDetailRepository.findById(receiptDetail.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedReceiptDetail are not directly saved in db
        em.detach(updatedReceiptDetail);
        updatedReceiptDetail.quantity(UPDATED_QUANTITY).purchasePrice(UPDATED_PURCHASE_PRICE);

        restReceiptDetailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedReceiptDetail.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedReceiptDetail))
            )
            .andExpect(status().isOk());

        // Validate the ReceiptDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedReceiptDetailToMatchAllProperties(updatedReceiptDetail);
    }

    @Test
    @Transactional
    void putNonExistingReceiptDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        receiptDetail.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReceiptDetailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, receiptDetail.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(receiptDetail))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReceiptDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReceiptDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        receiptDetail.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReceiptDetailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(receiptDetail))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReceiptDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReceiptDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        receiptDetail.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReceiptDetailMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(receiptDetail)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReceiptDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReceiptDetailWithPatch() throws Exception {
        // Initialize the database
        insertedReceiptDetail = receiptDetailRepository.saveAndFlush(receiptDetail);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the receiptDetail using partial update
        ReceiptDetail partialUpdatedReceiptDetail = new ReceiptDetail();
        partialUpdatedReceiptDetail.setId(receiptDetail.getId());

        restReceiptDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReceiptDetail.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReceiptDetail))
            )
            .andExpect(status().isOk());

        // Validate the ReceiptDetail in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReceiptDetailUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedReceiptDetail, receiptDetail),
            getPersistedReceiptDetail(receiptDetail)
        );
    }

    @Test
    @Transactional
    void fullUpdateReceiptDetailWithPatch() throws Exception {
        // Initialize the database
        insertedReceiptDetail = receiptDetailRepository.saveAndFlush(receiptDetail);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the receiptDetail using partial update
        ReceiptDetail partialUpdatedReceiptDetail = new ReceiptDetail();
        partialUpdatedReceiptDetail.setId(receiptDetail.getId());

        partialUpdatedReceiptDetail.quantity(UPDATED_QUANTITY).purchasePrice(UPDATED_PURCHASE_PRICE);

        restReceiptDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReceiptDetail.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReceiptDetail))
            )
            .andExpect(status().isOk());

        // Validate the ReceiptDetail in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReceiptDetailUpdatableFieldsEquals(partialUpdatedReceiptDetail, getPersistedReceiptDetail(partialUpdatedReceiptDetail));
    }

    @Test
    @Transactional
    void patchNonExistingReceiptDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        receiptDetail.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReceiptDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, receiptDetail.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(receiptDetail))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReceiptDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReceiptDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        receiptDetail.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReceiptDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(receiptDetail))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReceiptDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReceiptDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        receiptDetail.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReceiptDetailMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(receiptDetail)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReceiptDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReceiptDetail() throws Exception {
        // Initialize the database
        insertedReceiptDetail = receiptDetailRepository.saveAndFlush(receiptDetail);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the receiptDetail
        restReceiptDetailMockMvc
            .perform(delete(ENTITY_API_URL_ID, receiptDetail.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return receiptDetailRepository.count();
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

    protected ReceiptDetail getPersistedReceiptDetail(ReceiptDetail receiptDetail) {
        return receiptDetailRepository.findById(receiptDetail.getId()).orElseThrow();
    }

    protected void assertPersistedReceiptDetailToMatchAllProperties(ReceiptDetail expectedReceiptDetail) {
        assertReceiptDetailAllPropertiesEquals(expectedReceiptDetail, getPersistedReceiptDetail(expectedReceiptDetail));
    }

    protected void assertPersistedReceiptDetailToMatchUpdatableProperties(ReceiptDetail expectedReceiptDetail) {
        assertReceiptDetailAllUpdatablePropertiesEquals(expectedReceiptDetail, getPersistedReceiptDetail(expectedReceiptDetail));
    }
}
