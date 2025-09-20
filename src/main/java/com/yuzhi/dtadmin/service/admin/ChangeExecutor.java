package com.yuzhi.dtadmin.service.admin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuzhi.dtadmin.domain.ChangeRequest;
import com.yuzhi.dtadmin.domain.OrgUnit;
import com.yuzhi.dtadmin.domain.PortalMenu;
import com.yuzhi.dtadmin.domain.SystemConfig;
import com.yuzhi.dtadmin.domain.enumeration.ChangeAction;
import com.yuzhi.dtadmin.domain.enumeration.ChangeResourceType;
import com.yuzhi.dtadmin.repository.OrgUnitRepository;
import com.yuzhi.dtadmin.repository.PortalMenuRepository;
import com.yuzhi.dtadmin.repository.SystemConfigRepository;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class ChangeExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(ChangeExecutor.class);

    private final KeycloakAdapter keycloakAdapter;
    private final OrgUnitRepository orgUnitRepository;
    private final PortalMenuRepository portalMenuRepository;
    private final SystemConfigRepository systemConfigRepository;
    private final ObjectMapper objectMapper;

    public ChangeExecutor(
        KeycloakAdapter keycloakAdapter,
        OrgUnitRepository orgUnitRepository,
        PortalMenuRepository portalMenuRepository,
        SystemConfigRepository systemConfigRepository,
        ObjectMapper objectMapper
    ) {
        this.keycloakAdapter = keycloakAdapter;
        this.orgUnitRepository = orgUnitRepository;
        this.portalMenuRepository = portalMenuRepository;
        this.systemConfigRepository = systemConfigRepository;
        this.objectMapper = objectMapper;
    }

    public void execute(ChangeRequest request) {
        ChangeResourceType resourceType = request.getResourceType();
        ChangeAction action = request.getAction();
        try {
            Map<String, Object> payload = request.getPayloadJson() != null
                ? objectMapper.readValue(request.getPayloadJson(), new TypeReference<>() {})
                : Map.of();
            switch (resourceType) {
                case USER -> handleUser(action, request.getResourceId(), payload);
                case ROLE -> handleRole(action, request.getResourceId(), payload);
                case ORG -> handleOrg(action, request.getResourceId(), payload);
                case CONFIG -> handleConfig(action, payload);
                case MENU -> handleMenu(action, request.getResourceId(), payload);
                default -> throw new IllegalArgumentException("Unsupported resource type: " + resourceType);
            }
        } catch (Exception ex) {
            LOG.error("Change execution failed", ex);
            throw new AdminOperationException("Failed to apply change: " + ex.getMessage());
        }
    }

    private void handleUser(ChangeAction action, String userId, Map<String, Object> payload) {
        switch (action) {
            case CREATE -> keycloakAdapter.createUser(payload);
            case UPDATE -> keycloakAdapter.updateUser(requiredId(userId), payload);
            case DELETE -> keycloakAdapter.deleteUser(requiredId(userId));
            case BIND_ROLE -> keycloakAdapter.addRealmRoleToUser(requiredId(userId), payload);
            case UNBIND_ROLE -> keycloakAdapter.removeRealmRoleFromUser(requiredId(userId), payload);
            default -> throw new IllegalArgumentException("Unsupported user action: " + action);
        }
    }

    private void handleRole(ChangeAction action, String roleId, Map<String, Object> payload) {
        if (action == ChangeAction.CREATE) {
            keycloakAdapter.createGroup(payload);
        } else if (action == ChangeAction.UPDATE) {
            keycloakAdapter.updateGroup(requiredId(roleId), payload);
        } else if (action == ChangeAction.DELETE) {
            keycloakAdapter.deleteGroup(requiredId(roleId));
        } else {
            throw new IllegalArgumentException("Unsupported role action: " + action);
        }
    }

    private void handleOrg(ChangeAction action, String resourceId, Map<String, Object> payload) {
        if (action == ChangeAction.CREATE) {
            OrgUnit unit = new OrgUnit();
            unit.setName((String) payload.get("name"));
            unit.setCode((String) payload.get("code"));
            if (payload.get("parentId") != null) {
                Long parentId = Long.valueOf(payload.get("parentId").toString());
                orgUnitRepository.findById(parentId).ifPresent(unit::setParent);
            }
            orgUnitRepository.save(unit);
        } else if (action == ChangeAction.UPDATE) {
            Long id = Long.valueOf(requiredId(resourceId));
            OrgUnit unit = orgUnitRepository.findById(id).orElseThrow(() -> new AdminOperationException("Org unit not found"));
            Optional.ofNullable(payload.get("name")).ifPresent(value -> unit.setName(value.toString()));
            Optional.ofNullable(payload.get("code")).ifPresent(value -> unit.setCode(value.toString()));
        } else if (action == ChangeAction.DELETE) {
            orgUnitRepository.deleteById(Long.valueOf(requiredId(resourceId)));
        } else {
            throw new IllegalArgumentException("Unsupported org action: " + action);
        }
    }

    private void handleConfig(ChangeAction action, Map<String, Object> payload) {
        if (action != ChangeAction.CONFIG_SET) {
            throw new IllegalArgumentException("Unsupported config action: " + action);
        }
        String key = (String) payload.get("key");
        String value = payload.get("value") != null ? payload.get("value").toString() : null;
        String description = payload.get("description") != null ? payload.get("description").toString() : null;
        SystemConfig config = systemConfigRepository.findByKey(key).orElseGet(SystemConfig::new);
        if (config.getId() == null) {
            config.setKey(key);
        }
        config.setValue(value);
        config.setDescription(description);
        systemConfigRepository.save(config);
    }

    private void handleMenu(ChangeAction action, String resourceId, Map<String, Object> payload) {
        if (action == ChangeAction.CREATE) {
            PortalMenu menu = new PortalMenu();
            applyMenuPayload(menu, payload);
            portalMenuRepository.save(menu);
        } else if (action == ChangeAction.UPDATE) {
            PortalMenu menu = portalMenuRepository
                .findById(Long.valueOf(requiredId(resourceId)))
                .orElseThrow(() -> new AdminOperationException("Menu not found"));
            applyMenuPayload(menu, payload);
        } else if (action == ChangeAction.DELETE) {
            portalMenuRepository.deleteById(Long.valueOf(requiredId(resourceId)));
        } else {
            throw new IllegalArgumentException("Unsupported menu action: " + action);
        }
    }

    private void applyMenuPayload(PortalMenu menu, Map<String, Object> payload) {
        Optional.ofNullable(payload.get("name")).ifPresent(value -> menu.setName(value.toString()));
        Optional.ofNullable(payload.get("path")).ifPresent(value -> menu.setPath(value.toString()));
        Optional.ofNullable(payload.get("component")).ifPresent(value -> menu.setComponent(value.toString()));
        Optional.ofNullable(payload.get("sortOrder"))
            .ifPresent(value -> menu.setSortOrder(Integer.valueOf(value.toString())));
        Optional.ofNullable(payload.get("metadata")).ifPresent(value -> menu.setMetadata(value.toString()));
        if (payload.get("parentId") != null) {
            Long parentId = Long.valueOf(payload.get("parentId").toString());
            portalMenuRepository.findById(parentId).ifPresent(menu::setParent);
        } else {
            menu.setParent(null);
        }
    }

    private String requiredId(String value) {
        if (value == null || value.isBlank()) {
            throw new AdminOperationException("Resource id is required");
        }
        return value;
    }
}
