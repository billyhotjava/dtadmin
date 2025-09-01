package com.yuzhi.dtadmin.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.yuzhi.dtadmin.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ApprovalItemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ApprovalItemDTO.class);
        ApprovalItemDTO approvalItemDTO1 = new ApprovalItemDTO();
        approvalItemDTO1.setId(1L);
        ApprovalItemDTO approvalItemDTO2 = new ApprovalItemDTO();
        assertThat(approvalItemDTO1).isNotEqualTo(approvalItemDTO2);
        approvalItemDTO2.setId(approvalItemDTO1.getId());
        assertThat(approvalItemDTO1).isEqualTo(approvalItemDTO2);
        approvalItemDTO2.setId(2L);
        assertThat(approvalItemDTO1).isNotEqualTo(approvalItemDTO2);
        approvalItemDTO1.setId(null);
        assertThat(approvalItemDTO1).isNotEqualTo(approvalItemDTO2);
    }
}
