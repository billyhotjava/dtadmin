package com.yuzhi.dtadmin.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.yuzhi.dtadmin.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ApprovalRequestDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ApprovalRequestDTO.class);
        ApprovalRequestDTO approvalRequestDTO1 = new ApprovalRequestDTO();
        approvalRequestDTO1.setId(1L);
        ApprovalRequestDTO approvalRequestDTO2 = new ApprovalRequestDTO();
        assertThat(approvalRequestDTO1).isNotEqualTo(approvalRequestDTO2);
        approvalRequestDTO2.setId(approvalRequestDTO1.getId());
        assertThat(approvalRequestDTO1).isEqualTo(approvalRequestDTO2);
        approvalRequestDTO2.setId(2L);
        assertThat(approvalRequestDTO1).isNotEqualTo(approvalRequestDTO2);
        approvalRequestDTO1.setId(null);
        assertThat(approvalRequestDTO1).isNotEqualTo(approvalRequestDTO2);
    }
}
