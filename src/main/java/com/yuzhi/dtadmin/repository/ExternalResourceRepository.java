package com.yuzhi.dtadmin.repository;

import com.yuzhi.dtadmin.domain.ExternalResource;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ExternalResource entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ExternalResourceRepository extends JpaRepository<ExternalResource, Long> {}
