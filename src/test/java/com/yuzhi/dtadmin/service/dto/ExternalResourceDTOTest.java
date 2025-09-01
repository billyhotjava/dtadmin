package com.yuzhi.dtadmin.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.yuzhi.dtadmin.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ExternalResourceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ExternalResourceDTO.class);
        ExternalResourceDTO externalResourceDTO1 = new ExternalResourceDTO();
        externalResourceDTO1.setId(1L);
        ExternalResourceDTO externalResourceDTO2 = new ExternalResourceDTO();
        assertThat(externalResourceDTO1).isNotEqualTo(externalResourceDTO2);
        externalResourceDTO2.setId(externalResourceDTO1.getId());
        assertThat(externalResourceDTO1).isEqualTo(externalResourceDTO2);
        externalResourceDTO2.setId(2L);
        assertThat(externalResourceDTO1).isNotEqualTo(externalResourceDTO2);
        externalResourceDTO1.setId(null);
        assertThat(externalResourceDTO1).isNotEqualTo(externalResourceDTO2);
    }
}
