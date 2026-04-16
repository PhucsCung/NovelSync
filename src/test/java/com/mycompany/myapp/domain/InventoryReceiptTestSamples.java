package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class InventoryReceiptTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static InventoryReceipt getInventoryReceiptSample1() {
        return new InventoryReceipt().id(1L);
    }

    public static InventoryReceipt getInventoryReceiptSample2() {
        return new InventoryReceipt().id(2L);
    }

    public static InventoryReceipt getInventoryReceiptRandomSampleGenerator() {
        return new InventoryReceipt().id(longCount.incrementAndGet());
    }
}
