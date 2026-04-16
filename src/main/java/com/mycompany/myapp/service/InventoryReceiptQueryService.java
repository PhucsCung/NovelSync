package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.InventoryReceipt;
import com.mycompany.myapp.repository.InventoryReceiptRepository;
import com.mycompany.myapp.service.criteria.InventoryReceiptCriteria;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link InventoryReceipt} entities in the database.
 * The main input is a {@link InventoryReceiptCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link InventoryReceipt} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class InventoryReceiptQueryService extends QueryService<InventoryReceipt> {

    private static final Logger LOG = LoggerFactory.getLogger(InventoryReceiptQueryService.class);

    private final InventoryReceiptRepository inventoryReceiptRepository;

    public InventoryReceiptQueryService(InventoryReceiptRepository inventoryReceiptRepository) {
        this.inventoryReceiptRepository = inventoryReceiptRepository;
    }

    /**
     * Return a {@link Page} of {@link InventoryReceipt} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<InventoryReceipt> findByCriteria(InventoryReceiptCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<InventoryReceipt> specification = createSpecification(criteria);
        return inventoryReceiptRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(InventoryReceiptCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<InventoryReceipt> specification = createSpecification(criteria);
        return inventoryReceiptRepository.count(specification);
    }

    /**
     * Function to convert {@link InventoryReceiptCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<InventoryReceipt> createSpecification(InventoryReceiptCriteria criteria) {
        Specification<InventoryReceipt> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), InventoryReceipt_.id),
                buildRangeSpecification(criteria.getReceiptDate(), InventoryReceipt_.receiptDate),
                buildRangeSpecification(criteria.getTotalAmount(), InventoryReceipt_.totalAmount),
                buildSpecification(criteria.getUserId(), root -> root.join(InventoryReceipt_.user, JoinType.LEFT).get(User_.id))
            );
        }
        return specification;
    }
}
