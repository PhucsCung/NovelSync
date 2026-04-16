package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.ReceiptDetail;
import com.mycompany.myapp.repository.ReceiptDetailRepository;
import com.mycompany.myapp.service.ReceiptDetailService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.ReceiptDetail}.
 */
@Service
@Transactional
public class ReceiptDetailServiceImpl implements ReceiptDetailService {

    private static final Logger LOG = LoggerFactory.getLogger(ReceiptDetailServiceImpl.class);

    private final ReceiptDetailRepository receiptDetailRepository;

    public ReceiptDetailServiceImpl(ReceiptDetailRepository receiptDetailRepository) {
        this.receiptDetailRepository = receiptDetailRepository;
    }

    @Override
    public ReceiptDetail save(ReceiptDetail receiptDetail) {
        LOG.debug("Request to save ReceiptDetail : {}", receiptDetail);
        return receiptDetailRepository.save(receiptDetail);
    }

    @Override
    public ReceiptDetail update(ReceiptDetail receiptDetail) {
        LOG.debug("Request to update ReceiptDetail : {}", receiptDetail);
        return receiptDetailRepository.save(receiptDetail);
    }

    @Override
    public Optional<ReceiptDetail> partialUpdate(ReceiptDetail receiptDetail) {
        LOG.debug("Request to partially update ReceiptDetail : {}", receiptDetail);

        return receiptDetailRepository
            .findById(receiptDetail.getId())
            .map(existingReceiptDetail -> {
                if (receiptDetail.getQuantity() != null) {
                    existingReceiptDetail.setQuantity(receiptDetail.getQuantity());
                }
                if (receiptDetail.getPurchasePrice() != null) {
                    existingReceiptDetail.setPurchasePrice(receiptDetail.getPurchasePrice());
                }

                return existingReceiptDetail;
            })
            .map(receiptDetailRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReceiptDetail> findAll(Pageable pageable) {
        LOG.debug("Request to get all ReceiptDetails");
        return receiptDetailRepository.findAll(pageable);
    }

    public Page<ReceiptDetail> findAllWithEagerRelationships(Pageable pageable) {
        return receiptDetailRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReceiptDetail> findOne(Long id) {
        LOG.debug("Request to get ReceiptDetail : {}", id);
        return receiptDetailRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ReceiptDetail : {}", id);
        receiptDetailRepository.deleteById(id);
    }
}
