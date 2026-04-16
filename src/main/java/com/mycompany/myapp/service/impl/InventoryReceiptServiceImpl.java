package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.InventoryReceipt;
import com.mycompany.myapp.repository.InventoryReceiptRepository;
import com.mycompany.myapp.service.InventoryReceiptService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.InventoryReceipt}.
 */
@Service
@Transactional
public class InventoryReceiptServiceImpl implements InventoryReceiptService {

    private static final Logger LOG = LoggerFactory.getLogger(InventoryReceiptServiceImpl.class);

    private final InventoryReceiptRepository inventoryReceiptRepository;

    public InventoryReceiptServiceImpl(InventoryReceiptRepository inventoryReceiptRepository) {
        this.inventoryReceiptRepository = inventoryReceiptRepository;
    }

    @Override
    public InventoryReceipt save(InventoryReceipt inventoryReceipt) {
        LOG.debug("Request to save InventoryReceipt : {}", inventoryReceipt);
        return inventoryReceiptRepository.save(inventoryReceipt);
    }

    @Override
    public InventoryReceipt update(InventoryReceipt inventoryReceipt) {
        LOG.debug("Request to update InventoryReceipt : {}", inventoryReceipt);
        return inventoryReceiptRepository.save(inventoryReceipt);
    }

    @Override
    public Optional<InventoryReceipt> partialUpdate(InventoryReceipt inventoryReceipt) {
        LOG.debug("Request to partially update InventoryReceipt : {}", inventoryReceipt);

        return inventoryReceiptRepository
            .findById(inventoryReceipt.getId())
            .map(existingInventoryReceipt -> {
                if (inventoryReceipt.getReceiptDate() != null) {
                    existingInventoryReceipt.setReceiptDate(inventoryReceipt.getReceiptDate());
                }
                if (inventoryReceipt.getTotalAmount() != null) {
                    existingInventoryReceipt.setTotalAmount(inventoryReceipt.getTotalAmount());
                }

                return existingInventoryReceipt;
            })
            .map(inventoryReceiptRepository::save);
    }

    public Page<InventoryReceipt> findAllWithEagerRelationships(Pageable pageable) {
        return inventoryReceiptRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InventoryReceipt> findOne(Long id) {
        LOG.debug("Request to get InventoryReceipt : {}", id);
        return inventoryReceiptRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete InventoryReceipt : {}", id);
        inventoryReceiptRepository.deleteById(id);
    }
}
