package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ReceiptDetailTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ReceiptDetail getReceiptDetailSample1() {
        return new ReceiptDetail().id(1L).quantity(1);
    }

    public static ReceiptDetail getReceiptDetailSample2() {
        return new ReceiptDetail().id(2L).quantity(2);
    }

    public static ReceiptDetail getReceiptDetailRandomSampleGenerator() {
        return new ReceiptDetail().id(longCount.incrementAndGet()).quantity(intCount.incrementAndGet());
    }
}
