package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.InventoryReceiptAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.InventoryReceipt;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.InventoryReceiptRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.InventoryReceiptService;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link InventoryReceiptResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class InventoryReceiptResourceIT {

    private static final Instant DEFAULT_RECEIPT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RECEIPT_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_TOTAL_AMOUNT = new BigDecimal(0);
    private static final BigDecimal UPDATED_TOTAL_AMOUNT = new BigDecimal(1);
    private static final BigDecimal SMALLER_TOTAL_AMOUNT = new BigDecimal(0 - 1);

    private static final String ENTITY_API_URL = "/api/inventory-receipts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InventoryReceiptRepository inventoryReceiptRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private InventoryReceiptRepository inventoryReceiptRepositoryMock;

    @Mock
    private InventoryReceiptService inventoryReceiptServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInventoryReceiptMockMvc;

    private InventoryReceipt inventoryReceipt;

    private InventoryReceipt insertedInventoryReceipt;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InventoryReceipt createEntity() {
        return new InventoryReceipt().receiptDate(DEFAULT_RECEIPT_DATE).totalAmount(DEFAULT_TOTAL_AMOUNT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InventoryReceipt createUpdatedEntity() {
        return new InventoryReceipt().receiptDate(UPDATED_RECEIPT_DATE).totalAmount(UPDATED_TOTAL_AMOUNT);
    }

    @BeforeEach
    void initTest() {
        inventoryReceipt = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedInventoryReceipt != null) {
            inventoryReceiptRepository.delete(insertedInventoryReceipt);
            insertedInventoryReceipt = null;
        }
    }

    @Test
    @Transactional
    void createInventoryReceipt() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the InventoryReceipt
        var returnedInventoryReceipt = om.readValue(
            restInventoryReceiptMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryReceipt)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InventoryReceipt.class
        );

        // Validate the InventoryReceipt in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertInventoryReceiptUpdatableFieldsEquals(returnedInventoryReceipt, getPersistedInventoryReceipt(returnedInventoryReceipt));

        insertedInventoryReceipt = returnedInventoryReceipt;
    }

    @Test
    @Transactional
    void createInventoryReceiptWithExistingId() throws Exception {
        // Create the InventoryReceipt with an existing ID
        inventoryReceipt.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInventoryReceiptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryReceipt)))
            .andExpect(status().isBadRequest());

        // Validate the InventoryReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReceiptDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inventoryReceipt.setReceiptDate(null);

        // Create the InventoryReceipt, which fails.

        restInventoryReceiptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryReceipt)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTotalAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inventoryReceipt.setTotalAmount(null);

        // Create the InventoryReceipt, which fails.

        restInventoryReceiptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryReceipt)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllInventoryReceipts() throws Exception {
        // Initialize the database
        insertedInventoryReceipt = inventoryReceiptRepository.saveAndFlush(inventoryReceipt);

        // Get all the inventoryReceiptList
        restInventoryReceiptMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(inventoryReceipt.getId().intValue())))
            .andExpect(jsonPath("$.[*].receiptDate").value(hasItem(DEFAULT_RECEIPT_DATE.toString())))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInventoryReceiptsWithEagerRelationshipsIsEnabled() throws Exception {
        when(inventoryReceiptServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInventoryReceiptMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(inventoryReceiptServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInventoryReceiptsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(inventoryReceiptServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInventoryReceiptMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(inventoryReceiptRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getInventoryReceipt() throws Exception {
        // Initialize the database
        insertedInventoryReceipt = inventoryReceiptRepository.saveAndFlush(inventoryReceipt);

        // Get the inventoryReceipt
        restInventoryReceiptMockMvc
            .perform(get(ENTITY_API_URL_ID, inventoryReceipt.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(inventoryReceipt.getId().intValue()))
            .andExpect(jsonPath("$.receiptDate").value(DEFAULT_RECEIPT_DATE.toString()))
            .andExpect(jsonPath("$.totalAmount").value(sameNumber(DEFAULT_TOTAL_AMOUNT)));
    }

    @Test
    @Transactional
    void getInventoryReceiptsByIdFiltering() throws Exception {
        // Initialize the database
        insertedInventoryReceipt = inventoryReceiptRepository.saveAndFlush(inventoryReceipt);

        Long id = inventoryReceipt.getId();

        defaultInventoryReceiptFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultInventoryReceiptFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultInventoryReceiptFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllInventoryReceiptsByReceiptDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInventoryReceipt = inventoryReceiptRepository.saveAndFlush(inventoryReceipt);

        // Get all the inventoryReceiptList where receiptDate equals to
        defaultInventoryReceiptFiltering("receiptDate.equals=" + DEFAULT_RECEIPT_DATE, "receiptDate.equals=" + UPDATED_RECEIPT_DATE);
    }

    @Test
    @Transactional
    void getAllInventoryReceiptsByReceiptDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInventoryReceipt = inventoryReceiptRepository.saveAndFlush(inventoryReceipt);

        // Get all the inventoryReceiptList where receiptDate in
        defaultInventoryReceiptFiltering(
            "receiptDate.in=" + DEFAULT_RECEIPT_DATE + "," + UPDATED_RECEIPT_DATE,
            "receiptDate.in=" + UPDATED_RECEIPT_DATE
        );
    }

    @Test
    @Transactional
    void getAllInventoryReceiptsByReceiptDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInventoryReceipt = inventoryReceiptRepository.saveAndFlush(inventoryReceipt);

        // Get all the inventoryReceiptList where receiptDate is not null
        defaultInventoryReceiptFiltering("receiptDate.specified=true", "receiptDate.specified=false");
    }

    @Test
    @Transactional
    void getAllInventoryReceiptsByTotalAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInventoryReceipt = inventoryReceiptRepository.saveAndFlush(inventoryReceipt);

        // Get all the inventoryReceiptList where totalAmount equals to
        defaultInventoryReceiptFiltering("totalAmount.equals=" + DEFAULT_TOTAL_AMOUNT, "totalAmount.equals=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllInventoryReceiptsByTotalAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInventoryReceipt = inventoryReceiptRepository.saveAndFlush(inventoryReceipt);

        // Get all the inventoryReceiptList where totalAmount in
        defaultInventoryReceiptFiltering(
            "totalAmount.in=" + DEFAULT_TOTAL_AMOUNT + "," + UPDATED_TOTAL_AMOUNT,
            "totalAmount.in=" + UPDATED_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllInventoryReceiptsByTotalAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInventoryReceipt = inventoryReceiptRepository.saveAndFlush(inventoryReceipt);

        // Get all the inventoryReceiptList where totalAmount is not null
        defaultInventoryReceiptFiltering("totalAmount.specified=true", "totalAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllInventoryReceiptsByTotalAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInventoryReceipt = inventoryReceiptRepository.saveAndFlush(inventoryReceipt);

        // Get all the inventoryReceiptList where totalAmount is greater than or equal to
        defaultInventoryReceiptFiltering(
            "totalAmount.greaterThanOrEqual=" + DEFAULT_TOTAL_AMOUNT,
            "totalAmount.greaterThanOrEqual=" + UPDATED_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllInventoryReceiptsByTotalAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInventoryReceipt = inventoryReceiptRepository.saveAndFlush(inventoryReceipt);

        // Get all the inventoryReceiptList where totalAmount is less than or equal to
        defaultInventoryReceiptFiltering(
            "totalAmount.lessThanOrEqual=" + DEFAULT_TOTAL_AMOUNT,
            "totalAmount.lessThanOrEqual=" + SMALLER_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllInventoryReceiptsByTotalAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedInventoryReceipt = inventoryReceiptRepository.saveAndFlush(inventoryReceipt);

        // Get all the inventoryReceiptList where totalAmount is less than
        defaultInventoryReceiptFiltering("totalAmount.lessThan=" + UPDATED_TOTAL_AMOUNT, "totalAmount.lessThan=" + DEFAULT_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllInventoryReceiptsByTotalAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedInventoryReceipt = inventoryReceiptRepository.saveAndFlush(inventoryReceipt);

        // Get all the inventoryReceiptList where totalAmount is greater than
        defaultInventoryReceiptFiltering(
            "totalAmount.greaterThan=" + SMALLER_TOTAL_AMOUNT,
            "totalAmount.greaterThan=" + DEFAULT_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllInventoryReceiptsByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            inventoryReceiptRepository.saveAndFlush(inventoryReceipt);
            user = UserResourceIT.createEntity();
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        inventoryReceipt.setUser(user);
        inventoryReceiptRepository.saveAndFlush(inventoryReceipt);
        Long userId = user.getId();
        // Get all the inventoryReceiptList where user equals to userId
        defaultInventoryReceiptShouldBeFound("userId.equals=" + userId);

        // Get all the inventoryReceiptList where user equals to (userId + 1)
        defaultInventoryReceiptShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    private void defaultInventoryReceiptFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultInventoryReceiptShouldBeFound(shouldBeFound);
        defaultInventoryReceiptShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultInventoryReceiptShouldBeFound(String filter) throws Exception {
        restInventoryReceiptMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(inventoryReceipt.getId().intValue())))
            .andExpect(jsonPath("$.[*].receiptDate").value(hasItem(DEFAULT_RECEIPT_DATE.toString())))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))));

        // Check, that the count call also returns 1
        restInventoryReceiptMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultInventoryReceiptShouldNotBeFound(String filter) throws Exception {
        restInventoryReceiptMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restInventoryReceiptMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingInventoryReceipt() throws Exception {
        // Get the inventoryReceipt
        restInventoryReceiptMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInventoryReceipt() throws Exception {
        // Initialize the database
        insertedInventoryReceipt = inventoryReceiptRepository.saveAndFlush(inventoryReceipt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inventoryReceipt
        InventoryReceipt updatedInventoryReceipt = inventoryReceiptRepository.findById(inventoryReceipt.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedInventoryReceipt are not directly saved in db
        em.detach(updatedInventoryReceipt);
        updatedInventoryReceipt.receiptDate(UPDATED_RECEIPT_DATE).totalAmount(UPDATED_TOTAL_AMOUNT);

        restInventoryReceiptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedInventoryReceipt.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedInventoryReceipt))
            )
            .andExpect(status().isOk());

        // Validate the InventoryReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInventoryReceiptToMatchAllProperties(updatedInventoryReceipt);
    }

    @Test
    @Transactional
    void putNonExistingInventoryReceipt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventoryReceipt.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInventoryReceiptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inventoryReceipt.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inventoryReceipt))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventoryReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInventoryReceipt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventoryReceipt.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryReceiptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inventoryReceipt))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventoryReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInventoryReceipt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventoryReceipt.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryReceiptMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryReceipt)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the InventoryReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInventoryReceiptWithPatch() throws Exception {
        // Initialize the database
        insertedInventoryReceipt = inventoryReceiptRepository.saveAndFlush(inventoryReceipt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inventoryReceipt using partial update
        InventoryReceipt partialUpdatedInventoryReceipt = new InventoryReceipt();
        partialUpdatedInventoryReceipt.setId(inventoryReceipt.getId());

        partialUpdatedInventoryReceipt.totalAmount(UPDATED_TOTAL_AMOUNT);

        restInventoryReceiptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInventoryReceipt.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInventoryReceipt))
            )
            .andExpect(status().isOk());

        // Validate the InventoryReceipt in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInventoryReceiptUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInventoryReceipt, inventoryReceipt),
            getPersistedInventoryReceipt(inventoryReceipt)
        );
    }

    @Test
    @Transactional
    void fullUpdateInventoryReceiptWithPatch() throws Exception {
        // Initialize the database
        insertedInventoryReceipt = inventoryReceiptRepository.saveAndFlush(inventoryReceipt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inventoryReceipt using partial update
        InventoryReceipt partialUpdatedInventoryReceipt = new InventoryReceipt();
        partialUpdatedInventoryReceipt.setId(inventoryReceipt.getId());

        partialUpdatedInventoryReceipt.receiptDate(UPDATED_RECEIPT_DATE).totalAmount(UPDATED_TOTAL_AMOUNT);

        restInventoryReceiptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInventoryReceipt.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInventoryReceipt))
            )
            .andExpect(status().isOk());

        // Validate the InventoryReceipt in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInventoryReceiptUpdatableFieldsEquals(
            partialUpdatedInventoryReceipt,
            getPersistedInventoryReceipt(partialUpdatedInventoryReceipt)
        );
    }

    @Test
    @Transactional
    void patchNonExistingInventoryReceipt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventoryReceipt.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInventoryReceiptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, inventoryReceipt.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(inventoryReceipt))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventoryReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInventoryReceipt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventoryReceipt.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryReceiptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(inventoryReceipt))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventoryReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInventoryReceipt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventoryReceipt.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryReceiptMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(inventoryReceipt)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the InventoryReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInventoryReceipt() throws Exception {
        // Initialize the database
        insertedInventoryReceipt = inventoryReceiptRepository.saveAndFlush(inventoryReceipt);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the inventoryReceipt
        restInventoryReceiptMockMvc
            .perform(delete(ENTITY_API_URL_ID, inventoryReceipt.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return inventoryReceiptRepository.count();
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

    protected InventoryReceipt getPersistedInventoryReceipt(InventoryReceipt inventoryReceipt) {
        return inventoryReceiptRepository.findById(inventoryReceipt.getId()).orElseThrow();
    }

    protected void assertPersistedInventoryReceiptToMatchAllProperties(InventoryReceipt expectedInventoryReceipt) {
        assertInventoryReceiptAllPropertiesEquals(expectedInventoryReceipt, getPersistedInventoryReceipt(expectedInventoryReceipt));
    }

    protected void assertPersistedInventoryReceiptToMatchUpdatableProperties(InventoryReceipt expectedInventoryReceipt) {
        assertInventoryReceiptAllUpdatablePropertiesEquals(
            expectedInventoryReceipt,
            getPersistedInventoryReceipt(expectedInventoryReceipt)
        );
    }
}
