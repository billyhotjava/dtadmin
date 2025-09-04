package com.yuzhicloud.dtadmin.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.HttpsURLConnection;
import java.security.cert.X509Certificate;
import java.security.SecureRandom;

/**
 * Keycloak配置类
 * 提供Keycloak Admin Client bean用于调用Keycloak Admin API
 * 支持SSL证书忽略配置
 */
@Configuration
public class KeycloakConfig {

    @Value("${app.keycloak.server-url:https://sso.yuzhicloud.com}")
    private String keycloakServerUrl;

    @Value("${app.keycloak.auth-realm:master}")
    private String authRealm;

    @Value("${app.keycloak.target-realm:s10}")
    private String targetRealm;

    @Value("${app.keycloak.admin.username:admin}")
    private String adminUsername;

    @Value("${app.keycloak.admin.password:admin}")
    private String adminPassword;

    @Value("${app.keycloak.admin.client-id:admin-cli}")
    private String adminClientId;

    @Value("${app.keycloak.client-id:web-app}")
    private String clientId;

    @Value("${app.keycloak.client-secret:}")
    private String clientSecret;

    @Value("${app.keycloak.ssl.trust-all:true}")
    private boolean trustAllCertificates;

    /**
     * 创建Keycloak Admin Client
     * 用于调用Keycloak管理API
     */
    @Bean
    public Keycloak keycloakAdminClient() throws Exception {
        if (trustAllCertificates) {
            configureTrustAllCertificates();
        }
        
        // 配置Jackson忽略未知属性以解决版本兼容性问题
        System.setProperty("keycloak.jackson.configure-disable-fail-on-unknown-properties", "true");
        
        return KeycloakBuilder.builder()
                .serverUrl(keycloakServerUrl)
                .realm(authRealm)  // 使用master realm进行管理认证
                .clientId(adminClientId)
                .username(adminUsername)
                .password(adminPassword)
                .build();
    }

    /**
     * 创建用于Keycloak REST API调用的RestTemplate
     * 如果配置了trust-all-certificates，则忽略SSL证书验证
     */
    @Bean("keycloakRestTemplate")
    public RestTemplate keycloakRestTemplate() throws Exception {
        if (trustAllCertificates) {
            configureTrustAllCertificates();
        }
        
        return new RestTemplate();
    }

    /**
     * 配置信任所有SSL证书
     */
    private void configureTrustAllCertificates() throws Exception {
        // 创建信任所有证书的TrustManager
        TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
        };

        // 安装信任所有证书的TrustManager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        
        // 禁用主机名验证
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
    }

    public String getKeycloakServerUrl() {
        return keycloakServerUrl;
    }

    public String getRealm() {
        return authRealm;
    }

    public String getTargetRealm() {
        return targetRealm;
    }

    /**
     * 获取管理API的基础URL
     */
    public String getAdminApiBaseUrl() {
        return keycloakServerUrl + "/admin";
    }

    /**
     * 获取认证用的token URL
     */
    public String getAuthTokenUrl() {
        return String.format("%s/realms/%s/protocol/openid-connect/token", 
                keycloakServerUrl, authRealm);
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public String getAdminClientId() {
        return adminClientId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public boolean isTrustAllCertificates() {
        return trustAllCertificates;
    }
}