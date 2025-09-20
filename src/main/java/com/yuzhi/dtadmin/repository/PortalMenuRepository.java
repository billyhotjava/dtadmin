package com.yuzhi.dtadmin.repository;

import com.yuzhi.dtadmin.domain.PortalMenu;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortalMenuRepository extends JpaRepository<PortalMenu, Long> {
    @EntityGraph(attributePaths = { "children" })
    List<PortalMenu> findAllByParentIsNullOrderBySortOrderAsc();
}
