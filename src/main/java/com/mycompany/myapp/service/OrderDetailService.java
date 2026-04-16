package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.OrderDetail;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.OrderDetail}.
 */
public interface OrderDetailService {
    /**
     * Save a orderDetail.
     *
     * @param orderDetail the entity to save.
     * @return the persisted entity.
     */
    OrderDetail save(OrderDetail orderDetail);

    /**
     * Updates a orderDetail.
     *
     * @param orderDetail the entity to update.
     * @return the persisted entity.
     */
    OrderDetail update(OrderDetail orderDetail);

    /**
     * Partially updates a orderDetail.
     *
     * @param orderDetail the entity to update partially.
     * @return the persisted entity.
     */
    Optional<OrderDetail> partialUpdate(OrderDetail orderDetail);

    /**
     * Get all the orderDetails.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<OrderDetail> findAll(Pageable pageable);

    /**
     * Get all the orderDetails with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<OrderDetail> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" orderDetail.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<OrderDetail> findOne(Long id);

    /**
     * Delete the "id" orderDetail.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
