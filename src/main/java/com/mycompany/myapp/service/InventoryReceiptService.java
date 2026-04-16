package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.InventoryReceipt;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.InventoryReceipt}.
 */
public interface InventoryReceiptService {
    /**
     * Save a inventoryReceipt.
     *
     * @param inventoryReceipt the entity to save.
     * @return the persisted entity.
     */
    InventoryReceipt save(InventoryReceipt inventoryReceipt);

    /**
     * Updates a inventoryReceipt.
     *
     * @param inventoryReceipt the entity to update.
     * @return the persisted entity.
     */
    InventoryReceipt update(InventoryReceipt inventoryReceipt);

    /**
     * Partially updates a inventoryReceipt.
     *
     * @param inventoryReceipt the entity to update partially.
     * @return the persisted entity.
     */
    Optional<InventoryReceipt> partialUpdate(InventoryReceipt inventoryReceipt);

    /**
     * Get all the inventoryReceipts with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<InventoryReceipt> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" inventoryReceipt.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<InventoryReceipt> findOne(Long id);

    /**
     * Delete the "id" inventoryReceipt.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
