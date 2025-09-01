package com.yuzhi.dtadmin.repository;

import com.yuzhi.dtadmin.domain.ApprovalRequest;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ApprovalRequest entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, Long> {}
