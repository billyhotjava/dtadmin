package com.yuzhi.dtadmin.domain;

import static com.yuzhi.dtadmin.domain.ExternalResourceTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.yuzhi.dtadmin.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ExternalResourceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ExternalResource.class);
        ExternalResource externalResource1 = getExternalResourceSample1();
        ExternalResource externalResource2 = new ExternalResource();
        assertThat(externalResource1).isNotEqualTo(externalResource2);

        externalResource2.setId(externalResource1.getId());
        assertThat(externalResource1).isEqualTo(externalResource2);

        externalResource2 = getExternalResourceSample2();
        assertThat(externalResource1).isNotEqualTo(externalResource2);
    }
}
