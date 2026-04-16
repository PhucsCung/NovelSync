package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.InventoryReceipt} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.InventoryReceiptResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /inventory-receipts?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InventoryReceiptCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter receiptDate;

    private BigDecimalFilter totalAmount;

    private LongFilter userId;

    private Boolean distinct;

    public InventoryReceiptCriteria() {}

    public InventoryReceiptCriteria(InventoryReceiptCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.receiptDate = other.optionalReceiptDate().map(InstantFilter::copy).orElse(null);
        this.totalAmount = other.optionalTotalAmount().map(BigDecimalFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public InventoryReceiptCriteria copy() {
        return new InventoryReceiptCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public InstantFilter getReceiptDate() {
        return receiptDate;
    }

    public Optional<InstantFilter> optionalReceiptDate() {
        return Optional.ofNullable(receiptDate);
    }

    public InstantFilter receiptDate() {
        if (receiptDate == null) {
            setReceiptDate(new InstantFilter());
        }
        return receiptDate;
    }

    public void setReceiptDate(InstantFilter receiptDate) {
        this.receiptDate = receiptDate;
    }

    public BigDecimalFilter getTotalAmount() {
        return totalAmount;
    }

    public Optional<BigDecimalFilter> optionalTotalAmount() {
        return Optional.ofNullable(totalAmount);
    }

    public BigDecimalFilter totalAmount() {
        if (totalAmount == null) {
            setTotalAmount(new BigDecimalFilter());
        }
        return totalAmount;
    }

    public void setTotalAmount(BigDecimalFilter totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public Optional<LongFilter> optionalUserId() {
        return Optional.ofNullable(userId);
    }

    public LongFilter userId() {
        if (userId == null) {
            setUserId(new LongFilter());
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final InventoryReceiptCriteria that = (InventoryReceiptCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(receiptDate, that.receiptDate) &&
            Objects.equals(totalAmount, that.totalAmount) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, receiptDate, totalAmount, userId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InventoryReceiptCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReceiptDate().map(f -> "receiptDate=" + f + ", ").orElse("") +
            optionalTotalAmount().map(f -> "totalAmount=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
