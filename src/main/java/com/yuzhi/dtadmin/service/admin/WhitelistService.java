package com.yuzhi.dtadmin.service.admin;

import com.yuzhi.dtadmin.domain.AdminUserWhitelist;
import com.yuzhi.dtadmin.domain.enumeration.AdminRole;
import com.yuzhi.dtadmin.repository.AdminUserWhitelistRepository;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class WhitelistService {

    private final AdminUserWhitelistRepository whitelistRepository;

    public WhitelistService(AdminUserWhitelistRepository whitelistRepository) {
        this.whitelistRepository = whitelistRepository;
    }

    public Optional<AdminUserWhitelist> findByUsernameOrEmail(String username, String email) {
        if (username != null) {
            Optional<AdminUserWhitelist> byUsername = whitelistRepository.findByUsernameIgnoreCase(username);
            if (byUsername.isPresent()) {
                return byUsername;
            }
        }
        if (email != null) {
            return whitelistRepository.findByEmailIgnoreCase(email);
        }
        return Optional.empty();
    }

    public AdminRole enforceWhitelist(String username, String email, Collection<String> roles) {
        AdminUserWhitelist entry = findByUsernameOrEmail(username, email)
            .orElseThrow(() -> new AccessDeniedException("User not allowed to access admin"));

        Set<String> normalizedRoles = new HashSet<>();
        if (roles != null) {
            roles.stream().filter(r -> r != null && !r.isBlank()).map(String::toUpperCase).forEach(normalizedRoles::add);
        }

        Set<String> adminRoles = normalizedRoles
            .stream()
            .filter(role -> role.equals(AdminRole.SYSADMIN.name()) || role.equals(AdminRole.AUTHADMIN.name()) || role.equals(AdminRole.AUDITADMIN.name()))
            .collect(Collectors.toSet());

        if (adminRoles.size() != 1) {
            throw new AccessDeniedException("Admin user must only have one admin role");
        }

        String assignedRole = adminRoles.iterator().next();
        String expectedRole = entry.getRole().name();
        if (!assignedRole.equals(expectedRole)) {
            throw new AccessDeniedException("User role not permitted for admin");
        }

        return entry.getRole();
    }
}
