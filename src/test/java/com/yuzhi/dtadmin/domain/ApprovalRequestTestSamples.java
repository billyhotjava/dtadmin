package com.yuzhi.dtadmin.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ApprovalRequestTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ApprovalRequest getApprovalRequestSample1() {
        return new ApprovalRequest()
            .id(1L)
            .requester("requester1")
            .reason("reason1")
            .approver("approver1")
            .decisionNote("decisionNote1")
            .errorMessage("errorMessage1");
    }

    public static ApprovalRequest getApprovalRequestSample2() {
        return new ApprovalRequest()
            .id(2L)
            .requester("requester2")
            .reason("reason2")
            .approver("approver2")
            .decisionNote("decisionNote2")
            .errorMessage("errorMessage2");
    }

    public static ApprovalRequest getApprovalRequestRandomSampleGenerator() {
        return new ApprovalRequest()
            .id(longCount.incrementAndGet())
            .requester(UUID.randomUUID().toString())
            .reason(UUID.randomUUID().toString())
            .approver(UUID.randomUUID().toString())
            .decisionNote(UUID.randomUUID().toString())
            .errorMessage(UUID.randomUUID().toString());
    }
}
