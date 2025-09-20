package com.yuzhi.dtadmin.repository;

import com.yuzhi.dtadmin.domain.AdminUserWhitelist;
import com.yuzhi.dtadmin.domain.enumeration.AdminRole;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminUserWhitelistRepository extends JpaRepository<AdminUserWhitelist, Long> {
    Optional<AdminUserWhitelist> findByUsernameIgnoreCase(String username);
    Optional<AdminUserWhitelist> findByEmailIgnoreCase(String email);
    List<AdminUserWhitelist> findByRole(AdminRole role);
}
