package com.yuzhi.dtadmin.web.rest.admin;

import com.yuzhi.dtadmin.security.AdminPrincipal;
import com.yuzhi.dtadmin.security.AdminSecurityUtils;
import com.yuzhi.dtadmin.service.dto.AdminWhoamiResponse;
import com.yuzhi.dtadmin.web.rest.util.ApiResponseUtil;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminSessionResource {

    @GetMapping("/whoami")
    public ResponseEntity<Map<String, Object>> whoami() {
        AdminWhoamiResponse response = new AdminWhoamiResponse();
        AdminPrincipal principal = AdminSecurityUtils.getCurrentAdmin().orElse(null);
        if (principal == null) {
            response.setAllowed(false);
            return ResponseEntity.ok(ApiResponseUtil.createSuccessResponse(response));
        }
        response.setAllowed(true);
        response.setRole(principal.getRole());
        response.setUsername(principal.getUsername());
        response.setEmail(principal.getEmail());
        return ResponseEntity.ok(ApiResponseUtil.createSuccessResponse(response));
    }
}
