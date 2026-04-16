package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.BookTestSamples.*;
import static com.mycompany.myapp.domain.InventoryReceiptTestSamples.*;
import static com.mycompany.myapp.domain.ReceiptDetailTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReceiptDetailTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReceiptDetail.class);
        ReceiptDetail receiptDetail1 = getReceiptDetailSample1();
        ReceiptDetail receiptDetail2 = new ReceiptDetail();
        assertThat(receiptDetail1).isNotEqualTo(receiptDetail2);

        receiptDetail2.setId(receiptDetail1.getId());
        assertThat(receiptDetail1).isEqualTo(receiptDetail2);

        receiptDetail2 = getReceiptDetailSample2();
        assertThat(receiptDetail1).isNotEqualTo(receiptDetail2);
    }

    @Test
    void receiptTest() {
        ReceiptDetail receiptDetail = getReceiptDetailRandomSampleGenerator();
        InventoryReceipt inventoryReceiptBack = getInventoryReceiptRandomSampleGenerator();

        receiptDetail.setReceipt(inventoryReceiptBack);
        assertThat(receiptDetail.getReceipt()).isEqualTo(inventoryReceiptBack);

        receiptDetail.receipt(null);
        assertThat(receiptDetail.getReceipt()).isNull();
    }

    @Test
    void bookTest() {
        ReceiptDetail receiptDetail = getReceiptDetailRandomSampleGenerator();
        Book bookBack = getBookRandomSampleGenerator();

        receiptDetail.setBook(bookBack);
        assertThat(receiptDetail.getBook()).isEqualTo(bookBack);

        receiptDetail.book(null);
        assertThat(receiptDetail.getBook()).isNull();
    }
}
