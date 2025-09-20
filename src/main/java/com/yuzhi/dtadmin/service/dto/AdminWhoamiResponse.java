package com.yuzhi.dtadmin.service.dto;

import com.yuzhi.dtadmin.domain.enumeration.AdminRole;

public class AdminWhoamiResponse {

    private boolean allowed;
    private AdminRole role;
    private String username;
    private String email;

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    public AdminRole getRole() {
        return role;
    }

    public void setRole(AdminRole role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
