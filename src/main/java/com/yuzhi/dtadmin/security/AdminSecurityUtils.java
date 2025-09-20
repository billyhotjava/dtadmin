package com.yuzhi.dtadmin.security;

import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class AdminSecurityUtils {

    private AdminSecurityUtils() {}

    public static Optional<AdminPrincipal> getCurrentAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof AdminPrincipal admin) {
            return Optional.of(admin);
        }
        return Optional.empty();
    }
}
