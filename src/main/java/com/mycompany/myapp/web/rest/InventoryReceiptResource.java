package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.InventoryReceipt;
import com.mycompany.myapp.repository.InventoryReceiptRepository;
import com.mycompany.myapp.service.InventoryReceiptQueryService;
import com.mycompany.myapp.service.InventoryReceiptService;
import com.mycompany.myapp.service.criteria.InventoryReceiptCriteria;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.InventoryReceipt}.
 */
@RestController
@RequestMapping("/api/inventory-receipts")
public class InventoryReceiptResource {

    private static final Logger LOG = LoggerFactory.getLogger(InventoryReceiptResource.class);

    private static final String ENTITY_NAME = "inventoryReceipt";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InventoryReceiptService inventoryReceiptService;

    private final InventoryReceiptRepository inventoryReceiptRepository;

    private final InventoryReceiptQueryService inventoryReceiptQueryService;

    public InventoryReceiptResource(
        InventoryReceiptService inventoryReceiptService,
        InventoryReceiptRepository inventoryReceiptRepository,
        InventoryReceiptQueryService inventoryReceiptQueryService
    ) {
        this.inventoryReceiptService = inventoryReceiptService;
        this.inventoryReceiptRepository = inventoryReceiptRepository;
        this.inventoryReceiptQueryService = inventoryReceiptQueryService;
    }

    /**
     * {@code POST  /inventory-receipts} : Create a new inventoryReceipt.
     *
     * @param inventoryReceipt the inventoryReceipt to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new inventoryReceipt, or with status {@code 400 (Bad Request)} if the inventoryReceipt has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<InventoryReceipt> createInventoryReceipt(@Valid @RequestBody InventoryReceipt inventoryReceipt)
        throws URISyntaxException {
        LOG.debug("REST request to save InventoryReceipt : {}", inventoryReceipt);
        if (inventoryReceipt.getId() != null) {
            throw new BadRequestAlertException("A new inventoryReceipt cannot already have an ID", ENTITY_NAME, "idexists");
        }
        inventoryReceipt = inventoryReceiptService.save(inventoryReceipt);
        return ResponseEntity.created(new URI("/api/inventory-receipts/" + inventoryReceipt.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, inventoryReceipt.getId().toString()))
            .body(inventoryReceipt);
    }

    /**
     * {@code PUT  /inventory-receipts/:id} : Updates an existing inventoryReceipt.
     *
     * @param id the id of the inventoryReceipt to save.
     * @param inventoryReceipt the inventoryReceipt to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated inventoryReceipt,
     * or with status {@code 400 (Bad Request)} if the inventoryReceipt is not valid,
     * or with status {@code 500 (Internal Server Error)} if the inventoryReceipt couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<InventoryReceipt> updateInventoryReceipt(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody InventoryReceipt inventoryReceipt
    ) throws URISyntaxException {
        LOG.debug("REST request to update InventoryReceipt : {}, {}", id, inventoryReceipt);
        if (inventoryReceipt.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, inventoryReceipt.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!inventoryReceiptRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        inventoryReceipt = inventoryReceiptService.update(inventoryReceipt);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, inventoryReceipt.getId().toString()))
            .body(inventoryReceipt);
    }

    /**
     * {@code PATCH  /inventory-receipts/:id} : Partial updates given fields of an existing inventoryReceipt, field will ignore if it is null
     *
     * @param id the id of the inventoryReceipt to save.
     * @param inventoryReceipt the inventoryReceipt to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated inventoryReceipt,
     * or with status {@code 400 (Bad Request)} if the inventoryReceipt is not valid,
     * or with status {@code 404 (Not Found)} if the inventoryReceipt is not found,
     * or with status {@code 500 (Internal Server Error)} if the inventoryReceipt couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<InventoryReceipt> partialUpdateInventoryReceipt(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody InventoryReceipt inventoryReceipt
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update InventoryReceipt partially : {}, {}", id, inventoryReceipt);
        if (inventoryReceipt.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, inventoryReceipt.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!inventoryReceiptRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<InventoryReceipt> result = inventoryReceiptService.partialUpdate(inventoryReceipt);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, inventoryReceipt.getId().toString())
        );
    }

    /**
     * {@code GET  /inventory-receipts} : get all the inventoryReceipts.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of inventoryReceipts in body.
     */
    @GetMapping("")
    public ResponseEntity<List<InventoryReceipt>> getAllInventoryReceipts(
        InventoryReceiptCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get InventoryReceipts by criteria: {}", criteria);

        Page<InventoryReceipt> page = inventoryReceiptQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /inventory-receipts/count} : count all the inventoryReceipts.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countInventoryReceipts(InventoryReceiptCriteria criteria) {
        LOG.debug("REST request to count InventoryReceipts by criteria: {}", criteria);
        return ResponseEntity.ok().body(inventoryReceiptQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /inventory-receipts/:id} : get the "id" inventoryReceipt.
     *
     * @param id the id of the inventoryReceipt to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the inventoryReceipt, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<InventoryReceipt> getInventoryReceipt(@PathVariable("id") Long id) {
        LOG.debug("REST request to get InventoryReceipt : {}", id);
        Optional<InventoryReceipt> inventoryReceipt = inventoryReceiptService.findOne(id);
        return ResponseUtil.wrapOrNotFound(inventoryReceipt);
    }

    /**
     * {@code DELETE  /inventory-receipts/:id} : delete the "id" inventoryReceipt.
     *
     * @param id the id of the inventoryReceipt to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventoryReceipt(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete InventoryReceipt : {}", id);
        inventoryReceiptService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
