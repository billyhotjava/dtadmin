package com.yuzhi.dtadmin.repository;

import com.yuzhi.dtadmin.domain.PlatformRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformRoleRepository extends JpaRepository<PlatformRole, Long> {
    Optional<PlatformRole> findByName(String name);
}
