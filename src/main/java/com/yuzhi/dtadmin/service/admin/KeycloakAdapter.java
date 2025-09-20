package com.yuzhi.dtadmin.service.admin;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KeycloakAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(KeycloakAdapter.class);

    private final RestTemplate restTemplate;

    @Value("${keycloak.admin.token-url:http://localhost:8080/realms/yts/protocol/openid-connect/token}")
    private String tokenUrl;

    @Value("${keycloak.admin.base-url:http://localhost:8080/admin/realms/yts}")
    private String adminBaseUrl;

    @Value("${keycloak.admin.client-id:yts-admin-api}")
    private String clientId;

    @Value("${keycloak.admin.client-secret:change-me}")
    private String clientSecret;

    public KeycloakAdapter(@Qualifier("keycloakRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void createUser(Map<String, Object> payload) {
        postAdmin("/users", payload);
    }

    public void updateUser(String userId, Map<String, Object> payload) {
        putAdmin("/users/" + userId, payload);
    }

    public void deleteUser(String userId) {
        deleteAdmin("/users/" + userId);
    }

    public void addRealmRoleToUser(String userId, Map<String, Object> roleRepresentation) {
        postAdmin("/users/" + userId + "/role-mappings/realm", new Object[] { roleRepresentation });
    }

    public void removeRealmRoleFromUser(String userId, Map<String, Object> roleRepresentation) {
        deleteAdminWithBody("/users/" + userId + "/role-mappings/realm", new Object[] { roleRepresentation });
    }

    public void createGroup(Map<String, Object> payload) {
        postAdmin("/groups", payload);
    }

    public void updateGroup(String groupId, Map<String, Object> payload) {
        putAdmin("/groups/" + groupId, payload);
    }

    public void deleteGroup(String groupId) {
        deleteAdmin("/groups/" + groupId);
    }

    public void moveGroup(String groupId, String parentId) {
        postAdmin("/groups/" + groupId + "/children", Map.of("id", parentId));
    }

    private void postAdmin(String path, Object payload) {
        String token = getServiceToken();
        HttpHeaders headers = authHeaders(token);
        ResponseEntity<String> response = restTemplate.postForEntity(adminBaseUrl + path, new HttpEntity<>(payload, headers), String.class);
        LOG.debug("Keycloak POST {} -> {}", path, response.getStatusCode());
    }

    private void putAdmin(String path, Object payload) {
        String token = getServiceToken();
        HttpHeaders headers = authHeaders(token);
        restTemplate.put(adminBaseUrl + path, new HttpEntity<>(payload, headers));
    }

    private void deleteAdmin(String path) {
        String token = getServiceToken();
        HttpHeaders headers = authHeaders(token);
        restTemplate.exchange(adminBaseUrl + path, org.springframework.http.HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
    }

    private void deleteAdminWithBody(String path, Object payload) {
        String token = getServiceToken();
        HttpHeaders headers = authHeaders(token);
        restTemplate.exchange(adminBaseUrl + path, org.springframework.http.HttpMethod.DELETE, new HttpEntity<>(payload, headers), String.class);
    }

    private HttpHeaders authHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return headers;
    }

    private String getServiceToken() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, new HttpEntity<>(form, headers), Map.class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalStateException("Unable to obtain Keycloak admin token");
        }
        return response.getBody().get("access_token").toString();
    }
}
