package com.yuzhicloud.dtadmin.service.keycloak.policy;

import com.yuzhicloud.dtadmin.dto.keycloak.KeycloakRoleDTO;
import com.yuzhicloud.dtadmin.dto.keycloak.KeycloakUserDTO;
import com.yuzhicloud.dtadmin.service.keycloak.KeycloakRoleService;
import com.yuzhicloud.dtadmin.service.keycloak.KeycloakUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Centralised governance policy enforcement for Keycloak identities.
 */
@Service
public class KeycloakGovernancePolicyService {

    private static final Logger log = LoggerFactory.getLogger(KeycloakGovernancePolicyService.class);

    public static final String ROLE_SYS_ADMIN = "ROLE_SYS_ADMIN";
    public static final String ROLE_AUTH_ADMIN = "ROLE_AUTH_ADMIN";
    public static final String ROLE_AUDITOR_ADMIN = "ROLE_AUDITOR_ADMIN";
    public static final String ROLE_OP_ADMIN = "ROLE_OP_ADMIN";

    private static final Set<String> GOVERNANCE_ROLES = Set.of(ROLE_SYS_ADMIN, ROLE_AUTH_ADMIN, ROLE_AUDITOR_ADMIN);
    private static final Set<String> DATA_CLASSIFICATION_ROLES = java.util.Arrays.stream(PersonSecurityLevel.values())
        .flatMap(level -> level.getDataRoleNames().stream())
        .collect(Collectors.toCollection(LinkedHashSet::new));

    private final KeycloakRoleService roleService;
    private final KeycloakUserService userService;

    public KeycloakGovernancePolicyService(KeycloakRoleService roleService, KeycloakUserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    /**
     * Normalises the attribute payload prior to persisting in Keycloak.
     */
    public Map<String, List<String>> normalizeAttributes(Map<String, List<String>> rawAttributes) {
        Map<String, List<String>> result = new HashMap<>();
        if (rawAttributes != null) {
            rawAttributes.forEach((key, value) -> {
                if (value != null) {
                    result.put(key, new ArrayList<>(value));
                }
            });
        }

        Optional<String> requestedLevel = extractFirst(result, "person_security_level")
            .or(() -> extractFirst(result, "person_level"));

        if (requestedLevel.isEmpty()) {
            // No security level specified, ensure derived attributes do not linger.
            result.remove("person_level");
            result.remove("data_levels");
            return result;
        }

        PersonSecurityLevel level = PersonSecurityLevel.fromValue(requestedLevel.get())
            .orElseThrow(() -> new IllegalArgumentException("Unsupported person_security_level: " + requestedLevel.get()));

        result.put("person_security_level", List.of(level.getAttributeValue()));
        result.put("person_level", List.of(level.getAttributeValue()));
        result.put("data_levels", new ArrayList<>(level.getDataLevelClaims()));
        return result;
    }

    /**
     * Ensure data classification roles are synchronised with the stored person level.
     */
    public void syncDataRolesForUser(String userId, Map<String, List<String>> attributes) {
        PersonSecurityLevel level = PersonSecurityLevel.fromAttributes(attributes).orElse(null);

        Set<String> desiredRoleNames = level == null
            ? Set.of()
            : new LinkedHashSet<>(level.getDataRoleNames());

        List<KeycloakRoleDTO> currentRoles = roleService.getUserRealmRoles(userId);
        Set<String> currentDataRoles = currentRoles.stream()
            .map(KeycloakRoleDTO::getName)
            .filter(DATA_CLASSIFICATION_ROLES::contains)
            .collect(Collectors.toCollection(LinkedHashSet::new));

        Set<String> rolesToAssign = new LinkedHashSet<>(desiredRoleNames);
        rolesToAssign.removeAll(currentDataRoles);

        Set<String> rolesToRemove = new LinkedHashSet<>(currentDataRoles);
        rolesToRemove.removeAll(desiredRoleNames);

        if (!rolesToAssign.isEmpty()) {
            List<KeycloakRoleDTO> resolved = resolveRoles(rolesToAssign);
            roleService.assignRealmRolesToUser(userId, resolved);
            log.debug("Assigned data roles {} to user {}", rolesToAssign, userId);
        }

        if (!rolesToRemove.isEmpty()) {
            List<KeycloakRoleDTO> resolved = resolveRoles(rolesToRemove);
            roleService.removeRealmRolesFromUser(userId, resolved);
            log.debug("Removed data roles {} from user {}", rolesToRemove, userId);
        }
    }

    /**
     * Validate a role assignment request against governance rules.
     */
    public void validateRoleAssignments(String userId, List<KeycloakRoleDTO> rolesToAssign) {
        if (rolesToAssign == null || rolesToAssign.isEmpty()) {
            return;
        }

        Set<String> requestedNames = rolesToAssign.stream()
            .map(KeycloakRoleDTO::getName)
            .collect(Collectors.toSet());

        Set<String> currentRoles = fetchCurrentRoleNames(userId);

        enforceSeparationOfDuties(currentRoles, requestedNames);

        validateDataRoles(userId, requestedNames);
    }

    /**
     * Validate a role removal request to ensure minimum data roles stay intact.
     */
    public void validateRoleRemoval(String userId, List<KeycloakRoleDTO> rolesToRemove) {
        if (rolesToRemove == null || rolesToRemove.isEmpty()) {
            return;
        }

        Set<String> removalNames = rolesToRemove.stream()
            .map(KeycloakRoleDTO::getName)
            .collect(Collectors.toSet());

        PersonSecurityLevel level = PersonSecurityLevel.fromAttributes(fetchAttributes(userId)).orElse(null);
        if (level == null) {
            return;
        }

        Set<String> requiredDataRoles = new HashSet<>(level.getDataRoleNames());
        boolean violating = removalNames.stream()
            .anyMatch(role -> DATA_CLASSIFICATION_ROLES.contains(role) && requiredDataRoles.contains(role));

        if (violating) {
            throw new IllegalStateException("Data classification roles are managed by person_security_level and cannot be revoked directly.");
        }
    }

    /**
     * Re-evaluate data roles for a user using the latest profile stored in Keycloak.
     */
    public void resyncDataRolesFromSource(String userId) {
        Map<String, List<String>> attributes = fetchAttributes(userId);
        syncDataRolesForUser(userId, attributes);
    }

    private void validateDataRoles(String userId, Set<String> requestedNames) {
        if (requestedNames.stream().noneMatch(DATA_CLASSIFICATION_ROLES::contains)) {
            return;
        }

        PersonSecurityLevel level = PersonSecurityLevel.fromAttributes(fetchAttributes(userId)).orElse(null);
        if (level == null) {
            throw new IllegalStateException("Assign data classification roles after configuring person_security_level.");
        }

        Set<String> allowed = new HashSet<>(level.getDataRoleNames());
        List<String> disallowed = requestedNames.stream()
            .filter(DATA_CLASSIFICATION_ROLES::contains)
            .filter(role -> !allowed.contains(role))
            .toList();

        if (!disallowed.isEmpty()) {
            throw new IllegalStateException("Requested data roles " + disallowed + " exceed the user's clearance level " + level.getAttributeValue());
        }
    }

    private void enforceSeparationOfDuties(Set<String> currentRoles, Set<String> requestedNames) {
        boolean requestingOpAdmin = requestedNames.contains(ROLE_OP_ADMIN);
        boolean requestingGovernance = requestedNames.stream().anyMatch(GOVERNANCE_ROLES::contains);
        boolean currentlyHasOpAdmin = currentRoles.contains(ROLE_OP_ADMIN);
        boolean currentlyHasGovernance = currentRoles.stream().anyMatch(GOVERNANCE_ROLES::contains);

        if (requestingOpAdmin && (currentlyHasGovernance || requestedNames.stream().anyMatch(GOVERNANCE_ROLES::contains))) {
            throw new IllegalStateException("ROLE_OP_ADMIN cannot be combined with governance roles (ROLE_SYS_ADMIN/ROLE_AUTH_ADMIN/ROLE_AUDITOR_ADMIN).");
        }

        if (requestingGovernance && (currentlyHasOpAdmin || requestedNames.contains(ROLE_OP_ADMIN))) {
            throw new IllegalStateException("Governance roles cannot be granted to application administrators. Remove ROLE_OP_ADMIN first.");
        }
    }

    private Map<String, List<String>> fetchAttributes(String userId) {
        KeycloakUserDTO user = userService.getUserById(userId);
        Map<String, List<String>> attributes = user.getAttributes();
        return attributes != null ? attributes : Map.of();
    }

    private Set<String> fetchCurrentRoleNames(String userId) {
        return roleService.getUserRealmRoles(userId).stream()
            .map(KeycloakRoleDTO::getName)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private List<KeycloakRoleDTO> resolveRoles(Set<String> names) {
        return names.stream()
            .map(roleService::getRealmRoleByName)
            .collect(Collectors.toList());
    }

    private Optional<String> extractFirst(Map<String, List<String>> attributes, String key) {
        List<String> values = attributes.get(key);
        if (values == null || values.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(values.get(0));
    }
}
