package com.yuzhicloud.dtadmin.service.keycloak.policy;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Supported person security levels and the data classifications they unlock.
 */
public enum PersonSecurityLevel {

    NON_SECRET("NON_SECRET", List.of("DATA_PUBLIC")),
    GENERAL("GENERAL", List.of("DATA_PUBLIC", "DATA_INTERNAL")),
    IMPORTANT("IMPORTANT", List.of("DATA_PUBLIC", "DATA_INTERNAL", "DATA_SECRET")),
    CORE("CORE", List.of("DATA_PUBLIC", "DATA_INTERNAL", "DATA_SECRET", "DATA_TOP_SECRET"));

    private final String attributeValue;
    private final List<String> dataRoleNames;

    PersonSecurityLevel(String attributeValue, List<String> dataRoleNames) {
        this.attributeValue = attributeValue;
        this.dataRoleNames = dataRoleNames;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public List<String> getDataRoleNames() {
        return dataRoleNames;
    }

    public List<String> getDataLevelClaims() {
        return dataRoleNames.stream()
            .map(role -> role.startsWith("DATA_") ? role.substring("DATA_".length()) : role)
            .toList();
    }

    public static Optional<PersonSecurityLevel> fromAttributes(java.util.Map<String, java.util.List<String>> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return Optional.empty();
        }
        String candidate = extractFirst(attributes, "person_security_level")
            .orElseGet(() -> extractFirst(attributes, "person_level").orElse(null));
        if (candidate == null) {
            return Optional.empty();
        }
        return fromValue(candidate);
    }

    public static Optional<PersonSecurityLevel> fromValue(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return Optional.empty();
        }
        String normalized = rawValue.trim().toUpperCase(Locale.ROOT);
        return Arrays.stream(values())
            .filter(level -> level.attributeValue.equals(normalized))
            .findFirst();
    }

    private static Optional<String> extractFirst(java.util.Map<String, java.util.List<String>> attributes, String key) {
        java.util.List<String> values = attributes.get(key);
        if (values == null || values.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(values.get(0));
    }
}
