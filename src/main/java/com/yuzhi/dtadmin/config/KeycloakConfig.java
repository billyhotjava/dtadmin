package com.yuzhi.dtadmin.config;

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
 * 提供RestTemplate bean用于调用Keycloak Admin API
 * 支持SSL证书忽略配置
 */
@Configuration
public class KeycloakConfig {

    @Value("${app.keycloak.server-url:https://sso.yuzhicloud.com}")
    private String keycloakServerUrl;

    @Value("${app.keycloak.realm:s10}")
    private String realm;

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
     * 创建用于Keycloak API调用的RestTemplate
     * 如果配置了trust-all-certificates，则忽略SSL证书验证
     */
    @Bean("keycloakRestTemplate")
    public RestTemplate keycloakRestTemplate() throws Exception {
        if (trustAllCertificates) {
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
        
        return new RestTemplate();
    }

    public String getKeycloakServerUrl() {
        return keycloakServerUrl;
    }

    public String getRealm() {
        return realm;
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