package com.yuzhi.dtadmin.domain;

import static com.yuzhi.dtadmin.domain.ApprovalItemTestSamples.*;
import static com.yuzhi.dtadmin.domain.ApprovalRequestTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.yuzhi.dtadmin.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ApprovalItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ApprovalItem.class);
        ApprovalItem approvalItem1 = getApprovalItemSample1();
        ApprovalItem approvalItem2 = new ApprovalItem();
        assertThat(approvalItem1).isNotEqualTo(approvalItem2);

        approvalItem2.setId(approvalItem1.getId());
        assertThat(approvalItem1).isEqualTo(approvalItem2);

        approvalItem2 = getApprovalItemSample2();
        assertThat(approvalItem1).isNotEqualTo(approvalItem2);
    }

    @Test
    void requestTest() {
        ApprovalItem approvalItem = getApprovalItemRandomSampleGenerator();
        ApprovalRequest approvalRequestBack = getApprovalRequestRandomSampleGenerator();

        approvalItem.setRequest(approvalRequestBack);
        assertThat(approvalItem.getRequest()).isEqualTo(approvalRequestBack);

        approvalItem.request(null);
        assertThat(approvalItem.getRequest()).isNull();
    }
}
