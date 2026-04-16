package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ReceiptDetail.
 */
@Entity
@Table(name = "receipt_detail")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReceiptDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Min(value = 1)
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "purchase_price", precision = 21, scale = 2, nullable = false)
    private BigDecimal purchasePrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private InventoryReceipt receipt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "category", "author" }, allowSetters = true)
    private Book book;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ReceiptDetail id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public ReceiptDetail quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPurchasePrice() {
        return this.purchasePrice;
    }

    public ReceiptDetail purchasePrice(BigDecimal purchasePrice) {
        this.setPurchasePrice(purchasePrice);
        return this;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public InventoryReceipt getReceipt() {
        return this.receipt;
    }

    public void setReceipt(InventoryReceipt inventoryReceipt) {
        this.receipt = inventoryReceipt;
    }

    public ReceiptDetail receipt(InventoryReceipt inventoryReceipt) {
        this.setReceipt(inventoryReceipt);
        return this;
    }

    public Book getBook() {
        return this.book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public ReceiptDetail book(Book book) {
        this.setBook(book);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReceiptDetail)) {
            return false;
        }
        return getId() != null && getId().equals(((ReceiptDetail) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReceiptDetail{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", purchasePrice=" + getPurchasePrice() +
            "}";
    }
}
