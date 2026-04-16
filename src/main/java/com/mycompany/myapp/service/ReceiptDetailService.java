package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.ReceiptDetail;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.ReceiptDetail}.
 */
public interface ReceiptDetailService {
    /**
     * Save a receiptDetail.
     *
     * @param receiptDetail the entity to save.
     * @return the persisted entity.
     */
    ReceiptDetail save(ReceiptDetail receiptDetail);

    /**
     * Updates a receiptDetail.
     *
     * @param receiptDetail the entity to update.
     * @return the persisted entity.
     */
    ReceiptDetail update(ReceiptDetail receiptDetail);

    /**
     * Partially updates a receiptDetail.
     *
     * @param receiptDetail the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ReceiptDetail> partialUpdate(ReceiptDetail receiptDetail);

    /**
     * Get all the receiptDetails.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ReceiptDetail> findAll(Pageable pageable);

    /**
     * Get all the receiptDetails with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ReceiptDetail> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" receiptDetail.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ReceiptDetail> findOne(Long id);

    /**
     * Delete the "id" receiptDetail.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
