package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.OrderDetailAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.OrderDetail;
import com.mycompany.myapp.repository.OrderDetailRepository;
import com.mycompany.myapp.service.OrderDetailService;
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
 * Integration tests for the {@link OrderDetailResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class OrderDetailResourceIT {

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final BigDecimal DEFAULT_SALE_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_SALE_PRICE = new BigDecimal(1);

    private static final String ENTITY_API_URL = "/api/order-details";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Mock
    private OrderDetailRepository orderDetailRepositoryMock;

    @Mock
    private OrderDetailService orderDetailServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderDetailMockMvc;

    private OrderDetail orderDetail;

    private OrderDetail insertedOrderDetail;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderDetail createEntity() {
        return new OrderDetail().quantity(DEFAULT_QUANTITY).salePrice(DEFAULT_SALE_PRICE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderDetail createUpdatedEntity() {
        return new OrderDetail().quantity(UPDATED_QUANTITY).salePrice(UPDATED_SALE_PRICE);
    }

    @BeforeEach
    void initTest() {
        orderDetail = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedOrderDetail != null) {
            orderDetailRepository.delete(insertedOrderDetail);
            insertedOrderDetail = null;
        }
    }

    @Test
    @Transactional
    void createOrderDetail() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the OrderDetail
        var returnedOrderDetail = om.readValue(
            restOrderDetailMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderDetail)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            OrderDetail.class
        );

        // Validate the OrderDetail in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertOrderDetailUpdatableFieldsEquals(returnedOrderDetail, getPersistedOrderDetail(returnedOrderDetail));

        insertedOrderDetail = returnedOrderDetail;
    }

    @Test
    @Transactional
    void createOrderDetailWithExistingId() throws Exception {
        // Create the OrderDetail with an existing ID
        orderDetail.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderDetailMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderDetail)))
            .andExpect(status().isBadRequest());

        // Validate the OrderDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        orderDetail.setQuantity(null);

        // Create the OrderDetail, which fails.

        restOrderDetailMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderDetail)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSalePriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        orderDetail.setSalePrice(null);

        // Create the OrderDetail, which fails.

        restOrderDetailMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderDetail)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllOrderDetails() throws Exception {
        // Initialize the database
        insertedOrderDetail = orderDetailRepository.saveAndFlush(orderDetail);

        // Get all the orderDetailList
        restOrderDetailMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderDetail.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].salePrice").value(hasItem(sameNumber(DEFAULT_SALE_PRICE))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrderDetailsWithEagerRelationshipsIsEnabled() throws Exception {
        when(orderDetailServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOrderDetailMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(orderDetailServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrderDetailsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(orderDetailServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOrderDetailMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(orderDetailRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getOrderDetail() throws Exception {
        // Initialize the database
        insertedOrderDetail = orderDetailRepository.saveAndFlush(orderDetail);

        // Get the orderDetail
        restOrderDetailMockMvc
            .perform(get(ENTITY_API_URL_ID, orderDetail.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(orderDetail.getId().intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.salePrice").value(sameNumber(DEFAULT_SALE_PRICE)));
    }

    @Test
    @Transactional
    void getNonExistingOrderDetail() throws Exception {
        // Get the orderDetail
        restOrderDetailMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOrderDetail() throws Exception {
        // Initialize the database
        insertedOrderDetail = orderDetailRepository.saveAndFlush(orderDetail);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderDetail
        OrderDetail updatedOrderDetail = orderDetailRepository.findById(orderDetail.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedOrderDetail are not directly saved in db
        em.detach(updatedOrderDetail);
        updatedOrderDetail.quantity(UPDATED_QUANTITY).salePrice(UPDATED_SALE_PRICE);

        restOrderDetailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedOrderDetail.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedOrderDetail))
            )
            .andExpect(status().isOk());

        // Validate the OrderDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOrderDetailToMatchAllProperties(updatedOrderDetail);
    }

    @Test
    @Transactional
    void putNonExistingOrderDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderDetail.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderDetailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderDetail.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(orderDetail))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrderDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderDetail.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderDetailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(orderDetail))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrderDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderDetail.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderDetailMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(orderDetail)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOrderDetailWithPatch() throws Exception {
        // Initialize the database
        insertedOrderDetail = orderDetailRepository.saveAndFlush(orderDetail);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderDetail using partial update
        OrderDetail partialUpdatedOrderDetail = new OrderDetail();
        partialUpdatedOrderDetail.setId(orderDetail.getId());

        partialUpdatedOrderDetail.salePrice(UPDATED_SALE_PRICE);

        restOrderDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderDetail.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOrderDetail))
            )
            .andExpect(status().isOk());

        // Validate the OrderDetail in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrderDetailUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedOrderDetail, orderDetail),
            getPersistedOrderDetail(orderDetail)
        );
    }

    @Test
    @Transactional
    void fullUpdateOrderDetailWithPatch() throws Exception {
        // Initialize the database
        insertedOrderDetail = orderDetailRepository.saveAndFlush(orderDetail);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderDetail using partial update
        OrderDetail partialUpdatedOrderDetail = new OrderDetail();
        partialUpdatedOrderDetail.setId(orderDetail.getId());

        partialUpdatedOrderDetail.quantity(UPDATED_QUANTITY).salePrice(UPDATED_SALE_PRICE);

        restOrderDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderDetail.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOrderDetail))
            )
            .andExpect(status().isOk());

        // Validate the OrderDetail in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrderDetailUpdatableFieldsEquals(partialUpdatedOrderDetail, getPersistedOrderDetail(partialUpdatedOrderDetail));
    }

    @Test
    @Transactional
    void patchNonExistingOrderDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderDetail.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, orderDetail.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(orderDetail))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrderDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderDetail.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(orderDetail))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrderDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderDetail.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderDetailMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(orderDetail)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOrderDetail() throws Exception {
        // Initialize the database
        insertedOrderDetail = orderDetailRepository.saveAndFlush(orderDetail);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the orderDetail
        restOrderDetailMockMvc
            .perform(delete(ENTITY_API_URL_ID, orderDetail.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return orderDetailRepository.count();
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

    protected OrderDetail getPersistedOrderDetail(OrderDetail orderDetail) {
        return orderDetailRepository.findById(orderDetail.getId()).orElseThrow();
    }

    protected void assertPersistedOrderDetailToMatchAllProperties(OrderDetail expectedOrderDetail) {
        assertOrderDetailAllPropertiesEquals(expectedOrderDetail, getPersistedOrderDetail(expectedOrderDetail));
    }

    protected void assertPersistedOrderDetailToMatchUpdatableProperties(OrderDetail expectedOrderDetail) {
        assertOrderDetailAllUpdatablePropertiesEquals(expectedOrderDetail, getPersistedOrderDetail(expectedOrderDetail));
    }
}
