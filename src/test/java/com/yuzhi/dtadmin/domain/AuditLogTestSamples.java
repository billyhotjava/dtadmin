package com.yuzhi.dtadmin.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AuditLogTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static AuditLog getAuditLogSample1() {
        return new AuditLog().id(1L).actor("actor1").action("action1").target("target1").result("result1");
    }

    public static AuditLog getAuditLogSample2() {
        return new AuditLog().id(2L).actor("actor2").action("action2").target("target2").result("result2");
    }

    public static AuditLog getAuditLogRandomSampleGenerator() {
        return new AuditLog()
            .id(longCount.incrementAndGet())
            .actor(UUID.randomUUID().toString())
            .action(UUID.randomUUID().toString())
            .target(UUID.randomUUID().toString())
            .result(UUID.randomUUID().toString());
    }
}
