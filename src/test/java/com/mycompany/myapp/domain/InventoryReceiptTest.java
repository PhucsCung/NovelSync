package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.InventoryReceiptTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InventoryReceiptTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(InventoryReceipt.class);
        InventoryReceipt inventoryReceipt1 = getInventoryReceiptSample1();
        InventoryReceipt inventoryReceipt2 = new InventoryReceipt();
        assertThat(inventoryReceipt1).isNotEqualTo(inventoryReceipt2);

        inventoryReceipt2.setId(inventoryReceipt1.getId());
        assertThat(inventoryReceipt1).isEqualTo(inventoryReceipt2);

        inventoryReceipt2 = getInventoryReceiptSample2();
        assertThat(inventoryReceipt1).isNotEqualTo(inventoryReceipt2);
    }
}
