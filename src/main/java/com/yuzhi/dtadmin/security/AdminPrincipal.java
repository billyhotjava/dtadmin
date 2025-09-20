package com.yuzhi.dtadmin.security;

import com.yuzhi.dtadmin.domain.enumeration.AdminRole;

public class AdminPrincipal {

    private final String username;
    private final String email;
    private final AdminRole role;

    public AdminPrincipal(String username, String email, AdminRole role) {
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public AdminRole getRole() {
        return role;
    }
}
