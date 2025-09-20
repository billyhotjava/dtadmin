package com.yuzhi.dtadmin.repository;

import com.yuzhi.dtadmin.domain.OrgUnit;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgUnitRepository extends JpaRepository<OrgUnit, Long> {
    Optional<OrgUnit> findByCode(String code);

    @EntityGraph(attributePaths = { "children" })
    List<OrgUnit> findAllByParentIsNull();
}
