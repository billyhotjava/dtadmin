package com.yuzhicloud.dtadmin.service.keycloak;

import com.yuzhi.dtadmin.DtadminApp;
import com.yuzhicloud.dtadmin.config.KeycloakConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Keycloak认证集成测试
 * 验证使用目标realm进行认证的配置是否正确
 */
@SpringBootTest(classes = DtadminApp.class)
@ActiveProfiles("dev")
public class KeycloakAuthIntegrationTest {

    @Autowired
    private KeycloakConfig keycloakConfig;

    @Test
    public void testKeycloakConfigRealmSettings() {
        // 验证认证realm已设置为目标realm (s10)
        assertEquals("s10", keycloakConfig.getRealm(), "认证realm应该设置为s10");
        
        // 验证目标realm设置正确
        assertEquals("s10", keycloakConfig.getTargetRealm(), "目标realm应该设置为s10");
        
        // 验证服务器URL设置正确
        assertNotNull(keycloakConfig.getKeycloakServerUrl(), "Keycloak服务器URL不应为空");
    }
}