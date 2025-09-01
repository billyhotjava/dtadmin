package com.yuzhi.dtadmin.domain;

import static com.yuzhi.dtadmin.domain.ApprovalItemTestSamples.*;
import static com.yuzhi.dtadmin.domain.ApprovalRequestTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.yuzhi.dtadmin.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ApprovalRequestTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ApprovalRequest.class);
        ApprovalRequest approvalRequest1 = getApprovalRequestSample1();
        ApprovalRequest approvalRequest2 = new ApprovalRequest();
        assertThat(approvalRequest1).isNotEqualTo(approvalRequest2);

        approvalRequest2.setId(approvalRequest1.getId());
        assertThat(approvalRequest1).isEqualTo(approvalRequest2);

        approvalRequest2 = getApprovalRequestSample2();
        assertThat(approvalRequest1).isNotEqualTo(approvalRequest2);
    }

    @Test
    void itemsTest() {
        ApprovalRequest approvalRequest = getApprovalRequestRandomSampleGenerator();
        ApprovalItem approvalItemBack = getApprovalItemRandomSampleGenerator();

        approvalRequest.addItems(approvalItemBack);
        assertThat(approvalRequest.getItems()).containsOnly(approvalItemBack);
        assertThat(approvalItemBack.getRequest()).isEqualTo(approvalRequest);

        approvalRequest.removeItems(approvalItemBack);
        assertThat(approvalRequest.getItems()).doesNotContain(approvalItemBack);
        assertThat(approvalItemBack.getRequest()).isNull();

        approvalRequest.items(new HashSet<>(Set.of(approvalItemBack)));
        assertThat(approvalRequest.getItems()).containsOnly(approvalItemBack);
        assertThat(approvalItemBack.getRequest()).isEqualTo(approvalRequest);

        approvalRequest.setItems(new HashSet<>());
        assertThat(approvalRequest.getItems()).doesNotContain(approvalItemBack);
        assertThat(approvalItemBack.getRequest()).isNull();
    }
}
