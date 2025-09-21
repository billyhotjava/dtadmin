package com.yuzhicloud.dtadmin.service.keycloak.policy;

import com.yuzhicloud.dtadmin.dto.keycloak.KeycloakRoleDTO;
import com.yuzhicloud.dtadmin.dto.keycloak.KeycloakUserDTO;
import com.yuzhicloud.dtadmin.service.keycloak.KeycloakRoleService;
import com.yuzhicloud.dtadmin.service.keycloak.KeycloakUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KeycloakGovernancePolicyServiceTest {

    @Mock
    private KeycloakRoleService roleService;

    @Mock
    private KeycloakUserService userService;

    private KeycloakGovernancePolicyService policyService;

    @BeforeEach
    void setUp() {
        policyService = new KeycloakGovernancePolicyService(roleService, userService);
    }

    @Test
    void normalizeAttributesShouldPopulateDataLevels() {
        Map<String, List<String>> normalized = policyService.normalizeAttributes(Map.of(
            "person_security_level", List.of("general")
        ));

        assertEquals(List.of("GENERAL"), normalized.get("person_security_level"));
        assertEquals(List.of("GENERAL"), normalized.get("person_level"));
        assertEquals(List.of("PUBLIC", "INTERNAL"), normalized.get("data_levels"));
    }

    @Test
    void validateRoleAssignmentsShouldRejectConflicts() {
        String userId = "user-1";
        when(roleService.getUserRealmRoles(userId)).thenReturn(List.of(role("ROLE_SYS_ADMIN")));
        when(userService.getUserById(userId)).thenReturn(userWithLevel("CORE"));

        assertThrows(IllegalStateException.class, () ->
            policyService.validateRoleAssignments(userId, List.of(role(KeycloakGovernancePolicyService.ROLE_OP_ADMIN)))
        );
    }

    @Test
    void validateRoleRemovalShouldPreventDataRoleRevocation() {
        String userId = "user-2";
        when(roleService.getUserRealmRoles(userId)).thenReturn(List.of(role("DATA_PUBLIC"), role("DATA_INTERNAL")));
        when(userService.getUserById(userId)).thenReturn(userWithLevel("GENERAL"));

        assertThrows(IllegalStateException.class, () ->
            policyService.validateRoleRemoval(userId, List.of(role("DATA_PUBLIC")))
        );
    }

    @Test
    void syncDataRolesForUserAssignsMissingRoles() {
        String userId = "user-3";
        when(roleService.getUserRealmRoles(userId)).thenReturn(List.of(role("DATA_PUBLIC")));
        when(roleService.getRealmRoleByName(anyString())).thenAnswer(invocation -> role(invocation.getArgument(0)));

        policyService.syncDataRolesForUser(userId, Map.of("person_security_level", List.of("CORE")));

        ArgumentCaptor<List<?>> assignCaptor = ArgumentCaptor.forClass(List.class);
        verify(roleService).assignRealmRolesToUser(eq(userId), assignCaptor.capture());
        assertEquals(3, assignCaptor.getValue().size());

        verify(roleService, never()).removeRealmRolesFromUser(anyString(), anyList());
    }

    private KeycloakRoleDTO role(String name) {
        KeycloakRoleDTO dto = new KeycloakRoleDTO();
        dto.setName(name);
        dto.setId(name);
        return dto;
    }

    private KeycloakUserDTO userWithLevel(String level) {
        KeycloakUserDTO dto = new KeycloakUserDTO();
        dto.setAttributes(Map.of("person_security_level", List.of(level)));
        return dto;
    }
}
