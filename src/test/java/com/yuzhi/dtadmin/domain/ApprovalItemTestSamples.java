package com.yuzhi.dtadmin.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ApprovalItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ApprovalItem getApprovalItemSample1() {
        return new ApprovalItem().id(1L).targetKind("targetKind1").targetId("targetId1").seqNumber(1);
    }

    public static ApprovalItem getApprovalItemSample2() {
        return new ApprovalItem().id(2L).targetKind("targetKind2").targetId("targetId2").seqNumber(2);
    }

    public static ApprovalItem getApprovalItemRandomSampleGenerator() {
        return new ApprovalItem()
            .id(longCount.incrementAndGet())
            .targetKind(UUID.randomUUID().toString())
            .targetId(UUID.randomUUID().toString())
            .seqNumber(intCount.incrementAndGet());
    }
}
