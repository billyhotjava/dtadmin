package com.yuzhi.dtadmin.security;

import com.yuzhi.dtadmin.domain.enumeration.AdminRole;
import com.yuzhi.dtadmin.service.admin.WhitelistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

public class AdminAccessFilter extends OncePerRequestFilter {

    private final WhitelistService whitelistService;

    public AdminAccessFilter(WhitelistService whitelistService) {
        this.whitelistService = whitelistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        if (!request.getRequestURI().startsWith("/admin")) {
            filterChain.doFilter(request, response);
            return;
        }
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!(authentication instanceof JwtAuthenticationToken token)) {
                throw new AccessDeniedException("Authentication required");
            }
            Jwt jwt = token.getToken();
            if (jwt.getAudience() == null || jwt.getAudience().stream().noneMatch("yts-admin-ui"::equals)) {
                throw new AccessDeniedException("Invalid audience");
            }
            String username = jwt.getClaimAsString("preferred_username");
            String email = jwt.getClaimAsString("email");
            Collection<String> roles = extractRoles(jwt);
            AdminRole role = whitelistService.enforceWhitelist(username, email, roles);
            token.setDetails(new AdminPrincipal(username, email, role));
            filterChain.doFilter(request, response);
        } catch (AccessDeniedException ex) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"FORBIDDEN\"}");
        }
    }

    @SuppressWarnings("unchecked")
    private Collection<String> extractRoles(Jwt jwt) {
        Object realmAccess = jwt.getClaim("realm_access");
        if (realmAccess instanceof Map<?, ?> map) {
            Object roles = map.get("roles");
            if (roles instanceof Collection<?> collection) {
                return collection.stream().map(Object::toString).toList();
            }
        }
        return Collections.emptyList();
    }
}
