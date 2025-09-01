package com.yuzhi.dtadmin.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ExternalResourceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ExternalResource getExternalResourceSample1() {
        return new ExternalResource().id(1L).urn("urn1").maxLevel("maxLevel1");
    }

    public static ExternalResource getExternalResourceSample2() {
        return new ExternalResource().id(2L).urn("urn2").maxLevel("maxLevel2");
    }

    public static ExternalResource getExternalResourceRandomSampleGenerator() {
        return new ExternalResource()
            .id(longCount.incrementAndGet())
            .urn(UUID.randomUUID().toString())
            .maxLevel(UUID.randomUUID().toString());
    }
}
