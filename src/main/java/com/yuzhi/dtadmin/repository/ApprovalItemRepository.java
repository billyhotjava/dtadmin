package com.yuzhi.dtadmin.repository;

import com.yuzhi.dtadmin.domain.ApprovalItem;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ApprovalItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ApprovalItemRepository extends JpaRepository<ApprovalItem, Long> {}
