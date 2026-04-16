package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.OrderDetail;
import com.mycompany.myapp.repository.OrderDetailRepository;
import com.mycompany.myapp.service.OrderDetailService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.OrderDetail}.
 */
@Service
@Transactional
public class OrderDetailServiceImpl implements OrderDetailService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderDetailServiceImpl.class);

    private final OrderDetailRepository orderDetailRepository;

    public OrderDetailServiceImpl(OrderDetailRepository orderDetailRepository) {
        this.orderDetailRepository = orderDetailRepository;
    }

    @Override
    public OrderDetail save(OrderDetail orderDetail) {
        LOG.debug("Request to save OrderDetail : {}", orderDetail);
        return orderDetailRepository.save(orderDetail);
    }

    @Override
    public OrderDetail update(OrderDetail orderDetail) {
        LOG.debug("Request to update OrderDetail : {}", orderDetail);
        return orderDetailRepository.save(orderDetail);
    }

    @Override
    public Optional<OrderDetail> partialUpdate(OrderDetail orderDetail) {
        LOG.debug("Request to partially update OrderDetail : {}", orderDetail);

        return orderDetailRepository
            .findById(orderDetail.getId())
            .map(existingOrderDetail -> {
                if (orderDetail.getQuantity() != null) {
                    existingOrderDetail.setQuantity(orderDetail.getQuantity());
                }
                if (orderDetail.getSalePrice() != null) {
                    existingOrderDetail.setSalePrice(orderDetail.getSalePrice());
                }

                return existingOrderDetail;
            })
            .map(orderDetailRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDetail> findAll(Pageable pageable) {
        LOG.debug("Request to get all OrderDetails");
        return orderDetailRepository.findAll(pageable);
    }

    public Page<OrderDetail> findAllWithEagerRelationships(Pageable pageable) {
        return orderDetailRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDetail> findOne(Long id) {
        LOG.debug("Request to get OrderDetail : {}", id);
        return orderDetailRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete OrderDetail : {}", id);
        orderDetailRepository.deleteById(id);
    }
}
